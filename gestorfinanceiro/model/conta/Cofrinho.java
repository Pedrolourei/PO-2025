package gestorfinanceiro.model.conta;

import gestorfinanceiro.exception.SaldoInsuficienteException;
import gestorfinanceiro.model.usuario.Usuario;

public class Cofrinho extends ContaBase {
    public Cofrinho(String id, String nome, Usuario dono, double saldo) {
        super(id, nome, dono, saldo);
    }

    @Override
    public void debitar(double valor) throws SaldoInsuficienteException {
        if (valor > this.saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente no Cofrinho " + getNome() + ".");
        }
        this.saldo -= valor;
    }
}