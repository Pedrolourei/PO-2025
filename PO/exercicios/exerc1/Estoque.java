package exercicios.exerc1;
import java.util.ArrayList;

public class Estoque {
    private ArrayList<Produto> produtos = new ArrayList<>();

    public void adicionarProduto(Produto p) {
        if (p == null) return;
        for (Produto prod : produtos) {
            if (prod.getNome().equalsIgnoreCase(p.getNome())) {
                System.out.println("Produto já existe: " + p.getNome());
                return;
            }
        }
        produtos.add(p);
    }

    public Produto buscarPorNome(String nome) {
        for (Produto p : produtos) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                return p;
            }
        }
        return null;
    }

    public double valorTotalEmEstoque() {
        double soma = 0;
        for (Produto p : produtos) {
            soma += p.valorTotal();
        }
        return soma;
    }

    public boolean movimentar(String nome, int qtd, boolean entrada) {
        Produto p = buscarPorNome(nome);
        if (p == null) return false;

        if (entrada) {
            p.adicionar(qtd);
        } else {
            try {
                p.remover(qtd);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    public Produto maiorValor() {
        if (produtos.isEmpty()) return null;
        Produto maior = produtos.get(0);
        for (Produto p : produtos) {
            if (p.valorTotal() > maior.valorTotal()) {
                maior = p;
            }
        }
        return maior;
    }
}
