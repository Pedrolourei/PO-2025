package gestorfinanceiro.model.conta;

import gestorfinanceiro.exception.SaldoInsuficienteException;
import gestorfinanceiro.model.usuario.Usuario;

public class CartaoCredito extends ContaBase {
    private double limite;
    // "saldo" na classe base será usado para representar a FATURA (um valor
    // negativo)

    public CartaoCredito(String id, String nome, Usuario dono, double limite) {
        super(id, nome, dono, 0.0); // Fatura inicial é 0
        this.limite = limite;
    }

    public double getLimiteDisponivel() {
        return this.limite + this.saldo; // Saldo é negativo, ex: 1000 + (-300) = 700
    }

    public double getFaturaAtual() {
        return -this.saldo; // Retorna o valor positivo da fatura
    }

    @Override
    public double getSaldo() {
        return this.saldo; // Retorna o valor negativo da fatura
    }

    @Override
    public void creditar(double valor) {
        this.saldo += valor;
        if (this.saldo > 0) {
            this.saldo = 0;
        }
    }

    @Override
    public void debitar(double valor) throws SaldoInsuficienteException {
        if (valor > getLimiteDisponivel()) {
            throw new SaldoInsuficienteException("Limite insuficiente no Cartão de Crédito " + getNome() + ".");
        }
        this.saldo -= valor;

        if (getLimiteDisponivel() < (this.limite * 0.15)) { // 15% de limite
            System.out.println("  [ALERTA DE LIMITE] Cartão " + getNome() + " está com apenas R$" +
                    String.format("%.2f", getLimiteDisponivel()) + " de limite disponível.");
        }
    }
}