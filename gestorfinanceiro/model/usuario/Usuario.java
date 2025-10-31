package gestorfinanceiro.model.usuario;

import g2.gestorfinanceiro.model.conta.ContaFinanceira;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Usuario {
    protected String id;
    protected String nome;
    protected List<ContaFinanceira> contas;

    public Usuario(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.contas = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<ContaFinanceira> getContas() {
        return contas;
    }

    public void adicionarConta(ContaFinanceira conta) {
        if (conta.getDono().equals(this)) {
            this.contas.add(conta);
        } else {
            System.err.println("Erro: Tentativa de adicionar conta de outro dono.");
        }
    }

    /**
     * Retorna o saldo consolidado de todas as contas do usuário.
     */
    public double getSaldoTotalConsolidado() {
        // O polimorfismo de getSaldo() (Cartão subtrai, Conta soma) é aplicado aqui.
        return contas.stream().mapToDouble(ContaFinanceira::getSaldo).sum();
    }

    // hashCode e equals são essenciais para usar Usuario como chave em Maps (ex:
    // rateio).
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" + "id='" + id + '\'' + ", nome='" + nome + '\'' + '}';
    }

}
