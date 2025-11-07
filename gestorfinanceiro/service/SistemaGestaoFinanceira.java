package gestorfinanceiro.service;

import gestorfinanceiro.exception.SaldoInsuficienteException;
import gestorfinanceiro.model.conta.*; // Importa todas as contas
import gestorfinanceiro.model.lancamento.Categoria;
import gestorfinanceiro.model.lancamento.Lancamento;
import gestorfinanceiro.model.lancamento.TipoLancamento;
import gestorfinanceiro.model.usuario.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe principal que centraliza toda a lógica de negócio.
 * Gerencia usuários, contas e a execução de todos os lançamentos.
 */
public class SistemaGestaoFinanceira {
    private Map<String, Usuario> usuarios;
    private Map<String, ContaFinanceira> contas;
    private List<Lancamento> historicoLancamentos;
    private List<Lancamento> lancamentosAgendados;

    public SistemaGestaoFinanceira() {
        this.usuarios = new HashMap<>();
        this.contas = new HashMap<>();
        this.historicoLancamentos = new ArrayList<>();
        this.lancamentosAgendados = new ArrayList<>();
    }

    public void registrarUsuario(Usuario u) {
        this.usuarios.put(u.getId(), u);
        System.out.println("Usuário registrado: " + u.getNome());
    }

    public void registrarConta(ContaFinanceira c) {
        this.contas.put(c.getIdConta(), c);
        c.getDono().adicionarConta(c);
        System.out.println("Conta registrada: " + c.getNome() + " para " + c.getDono().getNome());
    }

    public Usuario getUsuario(String id) {
        return usuarios.get(id);
    }

    public ContaFinanceira getConta(String id) {
        return contas.get(id);
    }

    public void executarLancamento(Lancamento l) {
        try {
            switch (l.getTipo()) {
                case RECEITA:
                    l.getConta().creditar(l.getValor());
                    l.getConta().adicionarLancamentoAoExtrato(l);
                    break;

                case DESPESA:
                    l.getConta().debitar(l.getValor());
                    l.getConta().adicionarLancamentoAoExtrato(l);

                    if (l.isCompartilhado()) {
                        processarRateioAutomatico(l);
                    }
                    break;

                case TRANSFERENCIA:
                    l.getContaOrigem().debitar(l.getValor());
                    l.getContaDestino().creditar(l.getValor());

                    l.getContaOrigem().adicionarLancamentoAoExtrato(l);
                    l.getContaDestino().adicionarLancamentoAoExtrato(l);
                    break;
            }

            this.historicoLancamentos.add(l);
            System.out.println("-> Lançamento " + l.getId() + " (" + l.getDescricao() + ") executado com sucesso.");

        } catch (SaldoInsuficienteException e) {
            System.err.println("!!! FALHA no Lançamento " + l.getId() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("!!! ERRO INESPERADO no Lançamento " + l.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processarRateioAutomatico(Lancamento despesaPaga) {
        System.out.println("... Processando rateio automático para: " + despesaPaga.getDescricao());

        Usuario pagadorPrincipal = despesaPaga.getConta().getDono();

        ContaFinanceira contaReembolso = pagadorPrincipal.getContas().stream()
                .filter(c -> c instanceof ContaCorrente)
                .findFirst()
                .orElse(null);

        if (contaReembolso == null) {
            System.err.println("!!! FALHA Rateio: Usuário " + pagadorPrincipal.getNome()
                    + " não possui conta corrente para receber reembolso.");
            return;
        }

        for (Map.Entry<Usuario, Double> entry : despesaPaga.getDefinicaoRateio().entrySet()) {
            Usuario devedor = entry.getKey();
            Double valorDevido = entry.getValue();

            if (devedor.equals(pagadorPrincipal)) {
                System.out.println("  > Quota do pagador (" + pagadorPrincipal.getNome() + "): R$" + valorDevido);
                continue;
            }

            ContaFinanceira contaDevedor = devedor.getContas().stream()
                    .filter(c -> c instanceof ContaCorrente)
                    .findFirst()
                    .orElse(null);

            if (contaDevedor == null) {
                System.err.println(
                        "!!! FALHA Rateio: Devedor " + devedor.getNome() + " não tem conta corrente para debitar.");
                continue;
            }

            System.out.println("  > Transferindo R$" + valorDevido + " de " + devedor.getNome() + " para "
                    + pagadorPrincipal.getNome() + "...");

            Lancamento transferenciaRateio = new Lancamento(
                    valorDevido,
                    LocalDate.now(),
                    "Reembolso rateio: " + despesaPaga.getDescricao(),
                    contaDevedor,
                    contaReembolso);

            this.executarLancamento(transferenciaRateio);
        }
    }

    public void estornarLancamento(String idLancamento) {
        Lancamento original = historicoLancamentos.stream()
                .filter(l -> l.getId().equals(idLancamento))
                .findFirst()
                .orElse(null);

        if (original == null) {
            System.err.println("!!! FALHA Estorno: Lançamento " + idLancamento + " não encontrado.");
            return;
        }

        System.out
                .println("... Iniciando estorno do lançamento " + idLancamento + " (" + original.getDescricao() + ")");
        Lancamento estorno = original.criarEstorno();
        this.executarLancamento(estorno);
    }

    public void criarDespesaParcelada(double valorTotal, int numParcelas, LocalDate dataInicio,
            String descBase, Categoria cat, ContaFinanceira conta) {

        double valorParcela = Math.round((valorTotal / numParcelas) * 100.0) / 100.0;

        System.out.println("... Criando despesa parcelada: " + descBase + " (R$" + valorTotal + " em " + numParcelas
                + "x de R$" + valorParcela + ")");

        for (int i = 0; i < numParcelas; i++) {
            String descParcela = String.format("%s (%d/%d)", descBase, i + 1, numParcelas);
            LocalDate dataParcela = dataInicio.plusMonths(i);

            Lancamento parcela = new Lancamento(valorParcela, dataParcela, descParcela,
                    TipoLancamento.DESPESA, cat, conta);

            if (i == 0 && (dataParcela.isEqual(LocalDate.now()) || dataParcela.isBefore(LocalDate.now()))) {
                System.out.println("  > Executando 1ª parcela hoje.");
                executarLancamento(parcela);
            } else {
                this.lancamentosAgendados.add(parcela);
                System.out.println("  > Agendando parcela " + (i + 1) + " para " + dataParcela);
            }
        }
    }

    public void gerarRelatorioConsolidadoUsuario(Usuario u) {
        System.out.println("\n=======================================================");
        System.out.println("RELATÓRIO CONSOLIDADO: " + u.getNome().toUpperCase());
        System.out.println("=======================================================");
        for (ContaFinanceira conta : u.getContas()) {
            System.out.printf("  - Conta: %-20s | Saldo/Fatura: R$ %10.2f\n",
                    conta.getNome(), conta.getSaldo());

            if (conta instanceof CartaoCredito) {
                CartaoCredito cc = (CartaoCredito) conta;
                System.out.printf("    (Limite Disponível: R$ %.2f)\n", cc.getLimiteDisponivel());
            }
        }
        System.out.println("-------------------------------------------------------");
        System.out.printf("  SALDO TOTAL CONSOLIDADO (Líquido): R$ %10.2f\n", u.getSaldoTotalConsolidado());
        System.out.println("=======================================================");
    }

    public void gerarExtratoConta(ContaFinanceira conta) {
        System.out.println("\n--- EXTRATO: " + conta.getNome() + " (" + conta.getDono().getNome() + ") ---");
        for (Lancamento l : conta.getExtrato()) {
            String tipo = "";
            String valorStr = "";

            if (l.getTipo() == TipoLancamento.RECEITA) {
                tipo = "[CRÉDITO]";
                valorStr = "+R$ " + String.format("%.2f", l.getValor());
            } else if (l.getTipo() == TipoLancamento.DESPESA) {
                tipo = "[DÉBITO]  ";
                valorStr = "-R$ " + String.format("%.2f", l.getValor());
            } else if (l.getTipo() == TipoLancamento.TRANSFERENCIA) {
                if (l.getContaOrigem().equals(conta)) {
                    tipo = "[TRANSF. SAÍDA]";
                    valorStr = "-R$ " + String.format("%.2f", l.getValor());
                } else {
                    tipo = "[TRANSF. ENTRADA]";
                    valorStr = "+R$ " + String.format("%.2f", l.getValor());
                }
            }

            System.out.printf("[%s] %-18s | %-30s | %s\n",
                    l.getData(), tipo, l.getDescricao(), valorStr);
        }
        System.out.printf("--- SALDO ATUAL: R$ %.2f ---\n", conta.getSaldo());
    }
}