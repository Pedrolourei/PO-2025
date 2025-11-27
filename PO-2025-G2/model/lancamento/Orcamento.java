package gestorfinanceiro.model.lancamento;

import java.io.Serializable;
import java.time.LocalDate;

public class Orcamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Categoria categoria;
    private double valorLimite;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public Orcamento(String id, Categoria categoria, double valorLimite, LocalDate dataInicio, LocalDate dataFim) {
        this.id = id;
        this.categoria = categoria;
        this.valorLimite = valorLimite;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    // Getters
    public Categoria getCategoria() {
        return categoria;
    }

    public double getValorLimite() {
        return valorLimite;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    @Override
    public String toString() {
        return "Orçamento [" + id + "] Categoria=" + categoria.nomePrincipal +
                ", Limite=R$" + valorLimite + ", Período=" + dataInicio + " a " + dataFim;
    }
}