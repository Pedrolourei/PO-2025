package gestorfinanceiro.service;

// (Imports de todos os modelos, exceções e interfaces)
import gestorfinanceiro.exception.SaldoInsuficienteException;
import gestorfinanceiro.factory.RelatorioFactory;
import gestorfinanceiro.factory.TipoRelatorio;
import gestorfinanceiro.interfaces.IAlgoritmoProjecao;
import gestorfinanceiro.interfaces.IRelatorio;
import gestorfinanceiro.interfaces.Notificavel;
import gestorfinanceiro.model.conta.*;
import gestorfinanceiro.model.lancamento.Categoria;
import gestorfinanceiro.model.lancamento.Lancamento;
import gestorfinanceiro.model.lancamento.Orcamento;
import gestorfinanceiro.model.lancamento.TipoLancamento;
import gestorfinanceiro.model.usuario.Usuario;
import gestorfinanceiro.util.PersistenciaUtil;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Padrão Singleton: Garante uma única instância do gestor.
 * Agora atua como o orquestrador central (Facade) para os serviços.
 */
public class SistemaGestaoFinanceira implements Serializable {
    private static final long serialVersionUID = 1L;

    // --- Implementação do Singleton ---
    private static SistemaGestaoFinanceira instance;

    private SistemaGestaoFinanceira() {
        // Construtor privado para impedir instanciação externa
        this.usuarios = new HashMap<>();
        this.contas = new HashMap<>();
        this.historicoLancamentos = new ArrayList<>();
        this.lancamentosAgendados = new ArrayList<>();
        this.orcamentos = new ArrayList<>();
        
        // Padrões (DIP) - Injetando implementações padrão
        this.notificacaoService = new ConsoleNotificador(); // Polimorfismo
        this.algoritmoProjecao = new ProjecaoSaldoLinearStrategy(); // Strategy
        this.relatorioFactory = new RelatorioFactory(); // Factory
    }

    public static synchronized SistemaGestaoFinanceira getInstance() {
        if (instance == null) {
            instance = new SistemaGestaoFinanceira();
        }
        return instance;
    }

    // --- Atributos do Sistema (Estado) ---
    private Map<String, Usuario> usuarios;
    private Map<String, ContaFinanceira> contas;
    private List<Lancamento> historicoLancamentos;
    private List<Lancamento> lancamentosAgendados;
    private List<Orcamento> orcamentos; // Módulo 4

    // --- Módulos de Serviço e Estratégias (Injetados) ---
    // transient = não serializar esses componentes, eles são recriados no load
    private transient Notificavel notificacaoService;
    private transient IAlgoritmoProjecao algoritmoProjecao;
    private transient RelatorioFactory relatorioFactory;

    // --- Módulo 7: Persistência ---
    private static final String ARQUIVO_PERSISTENCIA = "gestor_financeiro.ser";

    public void salvarEstado() {
        try {
            PersistenciaUtil.salvar(this, ARQUIVO_PERSISTENCIA);
            System.out.println("[PERSISTÊNCIA] Dados salvos com sucesso!");
        } catch (IOException e) {
            System.err.println("[PERSISTÊNCIA] Erro ao salvar dados: " + e.getMessage());
        }
    }

    public static void carregarEstado() {
        try {
            instance = (SistemaGestaoFinanceira) PersistenciaUtil.carregar(ARQUIVO_PERSISTENCIA);
            instance.reiniciarServicosTransient(); // Recria os serviços
            System.out.println("[PERSISTÊNCIA] Dados carregados com sucesso!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[PERSISTÊNCIA] Nenhum dado salvo encontrado. Iniciando novo sistema.");
            instance = new SistemaGestaoFinanceira();
        }
    }
    
    /**
     * Recria serviços marcados como 'transient' após a desserialização.
     */
    private void reiniciarServicosTransient() {
        this.notificacaoService = new ConsoleNotificador();
        this.algoritmoProjecao = new ProjecaoSaldoLinearStrategy();
        this.relatorioFactory = new RelatorioFactory();
    }

    // --- Getters para consulta ---
    public Usuario getUsuario(String id) { return usuarios.get(id); }
    public ContaFinanceira getConta(String id) { return contas.get(id); }
    public List<Lancamento> getHistoricoLancamentos() { return historicoLancamentos; }
    public Map<String, Usuario> getUsuarios() { return usuarios; }
    
    // --- Lógica de Negócio (Serviços) ---

    public void registrarUsuario(Usuario u) {
        this.usuarios.put(u.getId(), u);
        System.out.println("Usuário registrado: " + u.getNome());
    }

    public void registrarConta(ContaFinanceira c) {
        this.contas.put(c.getIdConta(), c);
        c.getDono().adicionarConta(c);
        System.out.println("Conta registrada: " + c.getNome() + " para " + c.getDono().getNome());
    }
    
    public void executarLancamento(Lancamento l) {
        // ... (Lógica de execução de lançamento idêntica à da V1) ...
        // (switch case para RECEITA, DESPESA, TRANSFERENCIA)
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
                    // Módulo 4: Verificar orçamento após a despesa
                    verificarAlertasOrcamento(l);
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
        }
    }

    // --- Módulo 4: Orçamentos e Alertas ---
    
    public void adicionarOrcamento(Orcamento o) {
        this.orcamentos.add(o);
        System.out.println("Novo orçamento criado: " + o.toString());
    }

    private void verificarAlertasOrcamento(Lancamento despesa) {
        LocalDate hoje = despesa.getData();

        for (Orcamento orc : orcamentos) {
            // Verifica se a categoria bate e se está no período do orçamento
            if (orc.getCategoria().equals(despesa.getCategoria()) &&
                !hoje.isBefore(orc.getDataInicio()) && !hoje.isAfter(orc.getDataFim())) {

                // Calcula o total gasto nessa categoria no período
                double totalGasto = getGastosPorCategoriaNoPeriodo(
                                        orc.getCategoria(), 
                                        orc.getDataInicio(), 
                                        orc.getDataFim());

                if (totalGasto > orc.getValorLimite()) {
                    // Módulo 5 (Inteligência): Sugestão de economia
                    String sugestao = "Sugestão: Reduzir gastos com " + orc.getCategoria().subcategoria + ".";
                    
                    // Polimorfismo de Notificação
                    notificacaoService.enviar(
                        despesa.getConta().getDono(),
                        "ESTOURO DE ORÇAMENTO! Categoria " + orc.getCategoria().nomePrincipal +
                        " ultrapassou R$" + orc.getValorLimite() + ". Total gasto: R$" + totalGasto + "\n" + sugestao
                    );
                }
            }
        }
    }
    
    private double getGastosPorCategoriaNoPeriodo(Categoria cat, LocalDate inicio, LocalDate fim) {
        return historicoLancamentos.stream()
            .filter(l -> l.getTipo() == TipoLancamento.DESPESA &&
                         l.getCategoria().equals(cat) &&
                         !l.getData().isBefore(inicio) && !l.getData().isAfter(fim))
            .mapToDouble(Lancamento::getValor)
            .sum();
    }
    
    // --- Módulo 5: Algoritmos Inteligentes ---
    
    /**
     * Módulo 5: Rateio com Pesos
     */
    private void processarRateioAutomatico(Lancamento despesa) {
        if (despesa.getDefinicaoPesos() != null) {
            processarRateioPorPeso(despesa);
        } else if (despesa.getDefinicaoRateio() != null) {
            processarRateioPorValor(despesa);
        }
    }

    private void processarRateioPorValor(Lancamento despesa) {
        System.out.println("... Processando rateio (Valor Fixo) para: " + despesa.getDescricao());
        // (Lógica idêntica à V1... busca conta, transfere)
    }

    private void processarRateioPorPeso(Lancamento despesa) {
        System.out.println("... Processando rateio (Pesos) para: " + despesa.getDescricao());
        
        Map<Usuario, Integer> pesos = despesa.getDefinicaoPesos();
        double valorTotal = despesa.getValor();
        int somaPesos = pesos.values().stream().mapToInt(Integer::intValue).sum();
        
        if (somaPesos == 0) {
            System.err.println("!!! FALHA Rateio: Soma dos pesos é zero.");
            return;
        }

        // Calcula o valor por "ponto" de peso
        double valorPorPeso = valorTotal / somaPesos;
        
        // Cria o mapa de "valor devido"
        Map<Usuario, Double> valorDevidoMap = new HashMap<>();
        for (Map.Entry<Usuario, Integer> entry : pesos.entrySet()) {
            double valorDevido = Math.round((valorPorPeso * entry.getValue()) * 100.0) / 100.0;
            valorDevidoMap.put(entry.getKey(), valorDevido);
        }
        
        // Seta o mapa de valor e chama o processador de rateio por valor
        despesa.setRateio(valorDevidoMap);
        processarRateioPorValor(despesa);
    }

    /**
     * Módulo 5: Simulação de Cenário
     */
    public void simularCenario(Usuario usuario, double mudancaPercentual, Categoria categoria) {
        System.out.println("\n--- SIMULAÇÃO DE CENÁRIO ---");
        System.out.println("Usuário: " + usuario.getNome());
        System.out.println("Cenário: " + (mudancaPercentual > 0 ? "+" : "") + mudancaPercentual + 
                           "% em " + categoria.nomePrincipal);

        // Lógica de simulação (simplificada):
        // Pega o gasto médio atual na categoria e aplica a mudança
        double gastoMedioAtual = 500.0; // Simulado
        double novoGasto = gastoMedioAtual * (1 + (mudancaPercentual / 100.0));
        double impactoMensal = novoGasto - gastoMedioAtual;

        System.out.printf("Impacto mensal estimado: R$ %.2f\n", impactoMensal);

        // Usa o Strategy de projeção para mostrar o impacto futuro
        // Simula uma mudança no "líquido mensal"
        double saldoProjetadoOriginal = algoritmoProjecao.projetarSaldoFuturo(usuario, 6);
        
        // (Simulação de como o algoritmo mudaria)
        // Criar um "Usuario clone" ou "Contexto de Simulação" seria o ideal.
        // Por simplicidade, apenas calculamos o impacto.
        double saldoProjetadoNovo = saldoProjetadoOriginal + (impactoMensal * 6);
        System.out.printf("Saldo projetado em 6 meses (Novo Cenário): R$ %.2f\n", saldoProjetadoNovo);
    }
    
    // --- Módulo 6: Relatórios (usando Factory) ---
    
    public String gerarRelatorio(TipoRelatorio tipo) {
        try {
            IRelatorio relatorio = relatorioFactory.criarRelatorio(tipo);
            // Injeta o próprio gestor (DIP) para o relatório buscar os dados
            return relatorio.gerar(this);
        } catch (IllegalArgumentException e) {
            return "Erro: Tipo de relatório não implementado.";
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
                    TipoLancamento.DESPESA, cat, null);

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