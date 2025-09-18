package exercicios.exerc1;

public class LojaApp {
    public static void main(String[] args) {
        Produto p1 = new Produto("Arroz", 5.0, 10);
        Produto p2 = new Produto("Feijão", 7.0, 5);
        Produto p3 = new Produto("Macarrão", 4.0, 8);

        Estoque estoque = new Estoque();
        estoque.adicionarProduto(p1);
        estoque.adicionarProduto(p2);
        estoque.adicionarProduto(p3);

        System.out.println("Valor total inicial: " + estoque.valorTotalEmEstoque());

        estoque.movimentar("Arroz", 5, true);   // entrada
        estoque.movimentar("Feijão", 2, false); // saída

        System.out.println("Valor total após movimentações: " + estoque.valorTotalEmEstoque());

        Produto maior = estoque.maiorValor();
        System.out.println("Produto de maior valor em estoque: " + maior);
    }
}
