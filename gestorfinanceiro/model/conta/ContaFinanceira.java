package gestorfinanceiro.model.conta;

import g2.gestorfinanceiro.exception.SaldoInsuficienteException;
import g2.gestorfinanceiro.model.lancamento.Lancamento;
import g2.gestorfinanceiro.model.usuario.Usuario;
import java.util.List;

public interface ContaFinanceira {
    String getIdConta();

    String getNome();

    double getSaldo();

    Usuario getDono();

    List<Lancamento> getExtrato();

    void adicionarLancamentoAoExtrato(Lancamento l);

    /**
     * Adiciona fundos à conta.
     */
    void creditar(double valor);

    /**
     * Remove fundos da conta.
     */
    void debitar(double valor) throws SaldoInsuficienteException;
}