package gestorfinanceiro.model.conta;

import gestorfinanceiro.exception.SaldoInsuficienteException;
import gestorfinanceiro.model.usuario.Usuario;

public class ContaCorrente extends ContaBase {
    public ContaCorrente(String id, String nome, Usuario dono, double saldo) {
        super(id, nome, dono, saldo);
    }

    @Override
    public void debitar(double valor) throws SaldoInsuficienteException {
        // Regra de Validação de Saldo
        if (valor > this.saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente na Conta Corrente " + getNome() + ".");
        }
        this.saldo -= valor;
    }
}