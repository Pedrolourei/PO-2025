package gestorfinanceiro.model.lancamento;

import gestorfinanceiro.model.conta.ContaFinanceira;
import gestorfinanceiro.model.usuario.Usuario;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serializable;
import java.util.Map;

// Adicionar "implements Serializable"
public class Lancamento implements Serializable {
    private static final long serialVersionUID = 1L;

    String id;
    double valor;
    LocalDate data;
    String descricao;
    TipoLancamento tipo;
    Categoria categoria;

    // Para Despesa/Receita
    ContaFinanceira conta;

    // Para Transferencia
    ContaFinanceira contaOrigem;
    ContaFinanceira contaDestino;

    // Despesas compartilhadas
    boolean compartilhado = false;
    Map<Usuario, Double> definicaoRateio;
    private Map<Usuario, Integer> definicaoPesos; // Peso (ex: 1, 2, 3)

    // Construtor para Despesa/Receita
    public Lancamento(double valor, LocalDate data, String desc, TipoLancamento tipo,
            Categoria cat, ContaFinanceira conta) {
        this.id = "L-" + idCounter.getAndIncrement();
        this.valor = valor;
        this.data = data;
        this.descricao = desc;
        this.tipo = tipo;
        this.categoria = cat;
        this.conta = conta;
        if (tipo == TipoLancamento.TRANSFERENCIA) {
            throw new IllegalArgumentException("Use o construtor de transferência.");
        }
    }

    // Construtor para Transferencia
    public Lancamento(double valor, LocalDate data, String desc,
            ContaFinanceira origem, ContaFinanceira destino) {
        this.id = "L-" + idCounter.getAndIncrement();
        this.valor = valor;
        this.data = data;
        this.descricao = desc;
        this.tipo = TipoLancamento.TRANSFERENCIA;
        this.categoria = new Categoria("Transferência", "Transferência");
        this.contaOrigem = origem;
        this.contaDestino = destino;
    }

    // Construtor privado para Estorno
    private Lancamento(String id, double valor, LocalDate data, String desc, TipoLancamento tipo,
            Categoria cat, ContaFinanceira conta, ContaFinanceira origem, ContaFinanceira destino) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.descricao = desc;
        this.tipo = tipo;
        this.categoria = cat;
        this.conta = conta;
        this.contaOrigem = origem;
        this.contaDestino = destino;
    }

    public void setRateioPorPeso(Map<Usuario, Integer> rateio) {
        this.compartilhado = true;
        this.definicaoPesos = rateio;
        this.definicaoRateio = null; // Garante que apenas um tipo de rateio seja usado
    }

    // Getters
    public String getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public ContaFinanceira getConta() {
        return conta;
    }

    public ContaFinanceira getContaOrigem() {
        return contaOrigem;
    }

    public ContaFinanceira getContaDestino() {
        return contaDestino;
    }

    public boolean isCompartilhado() {
        return compartilhado;
    }

    public Map<Usuario, Double> getDefinicaoRateio() {
        return definicaoRateio;
    }

    public Map<Usuario, Integer> getDefinicaoPesos() {
        return definicaoPesos;
    }

    public void setRateio(Map<Usuario, Double> rateio) {
        this.compartilhado = true;

        double somaRateio = rateio.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(somaRateio - this.valor) > 0.01) {
            System.err.println("ALERTA de Rateio: A soma das partes (R$" + somaRateio +
                    ") não bate com o valor total da despesa (R$" + this.valor + ").");
        }
        this.definicaoRateio = rateio;
    }

    public Lancamento criarEstorno() {
        String descEstorno = "ESTORNO: " + this.descricao;
        String idEstorno = "E-" + this.id;

        switch (this.tipo) {
            case RECEITA:
                return new Lancamento(this.valor, LocalDate.now(), descEstorno,
                        TipoLancamento.DESPESA, this.categoria, this.conta);
            case DESPESA:
                return new Lancamento(this.valor, LocalDate.now(), descEstorno,
                        TipoLancamento.RECEITA, this.categoria, this.conta);
            case TRANSFERENCIA:
                return new Lancamento(this.valor, LocalDate.now(), descEstorno,
                        this.contaDestino, this.contaOrigem);
            default:
                throw new IllegalStateException("Tipo de lançamento desconhecido para estorno.");
        }
    }
}
