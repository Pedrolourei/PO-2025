package gestorfinanceiro.model.lancamento;

public class Categoria {
    public String nomePrincipal;
    public String subcategoria;

    public Categoria(String principal, String sub) {
        this.nomePrincipal = principal;
        this.subcategoria = sub;
    }

    public String toString() {
        return nomePrincipal + " > " + subcategoria;
    }
}
