package gestorfinanceiro.model.conta;

import gestorfinanceiro.model.lancamento.Lancamento;
import gestorfinanceiro.model.usuario.Usuario;
import java.util.ArrayList;
import java.util.List;

public abstract class ContaBase implements ContaFinanceira {
    protected String idConta;
    protected String nome;
    protected double saldo;
    protected Usuario dono;
    protected List<Lancamento> extrato;

    public ContaBase(String idConta, String nome, Usuario dono, double saldoInicial) {
        this.idConta = idConta;
        this.nome = nome;
        this.dono = dono;
        this.saldo = saldoInicial;
        this.extrato = new ArrayList<>();
    }

    @Override
    public String getIdConta() {
        return idConta;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public Usuario getDono() {
        return dono;
    }

    @Override
    public List<Lancamento> getExtrato() {
        return extrato;
    }

    @Override
    public void adicionarLancamentoAoExtrato(Lancamento l) {
        this.extrato.add(l);
    }

    @Override
    public double getSaldo() {
        return this.saldo;
    }

    @Override
    public void creditar(double valor) {
        if (valor > 0) {
            this.saldo += valor;
        }
    }
}