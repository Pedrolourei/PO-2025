package gestorfinanceiro.model.conta;

import gestorfinanceiro.exception.SaldoInsuficienteException;
import gestorfinanceiro.model.lancamento.Lancamento;
import gestorfinanceiro.model.usuario.Usuario;
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