package exercicios.exerc1;

public class Produto {
    private String nome;
    private double preco;
    private int quantidade;

    public Produto(String nome, double preco, int quantidade) {
        this.nome = nome;
        setPreco(preco);
        setQuantidade(quantidade);
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo.");
        }
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        }
        this.quantidade = quantidade;
    }

    public double valorTotal() {
        return preco * quantidade;
    }

    public void adicionar(int qtd) {
        if (qtd > 0) {
            this.quantidade += qtd;
        }
    }

    public void remover(int qtd) {
        if (qtd > quantidade) {
            throw new IllegalArgumentException("Quantidade insuficiente em estoque.");
        }
        this.quantidade -= qtd;
    }

    @Override
    public String toString() {
        return nome + " - Preço: " + preco + " - Quantidade: " + quantidade + " - Total: " + valorTotal();
    }
}
