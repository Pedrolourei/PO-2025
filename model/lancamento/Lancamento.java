package gestorfinanceiro.model.lancamento;

import gestorfinanceiro.model.conta.ContaCorrente;
import gestorfinanceiro.model.conta.ContaFinanceira;
import gestorfinanceiro.model.usuario.Usuario;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serializable;


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
        AtomicInteger idCounter = null;
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
            ContaFinanceira origem, ContaFinanceira destino, AtomicInteger idCounter) {
        this.id = "L-" + idCounter.getAndIncrement();
        this.valor = valor;
        this.data = data;
        this.descricao = desc;
        this.tipo = TipoLancamento.TRANSFERENCIA;
        this.categoria = new Categoria("Transferência", "Transferência");
        this.contaOrigem = origem;
        this.contaDestino = destino;
    }

    public Lancamento(double valor2, LocalDate now, String desc, TipoLancamento despesa, Categoria cat,
            ContaCorrente c) {
        //TODO Auto-generated constructor stub
    }

    public Lancamento(double valor2, LocalDate now, String descEstorno, ContaFinanceira contaDestino2,
            ContaFinanceira contaOrigem2) {
        //TODO Auto-generated constructor stub
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
