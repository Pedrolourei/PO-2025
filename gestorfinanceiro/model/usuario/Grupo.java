package gestorfinanceiro.model.usuario;

import java.util.HashMap;
import java.util.Map;

public class Grupo extends Usuario {
    private Map<Usuario, Permissao> membros;

    public Grupo(String id, String nome) {
        super(id, nome);
        this.membros = new HashMap<>();
    }

    public void adicionarMembro(Usuario membro, Permissao permissao) {
        this.membros.put(membro, permissao);
        System.out.println(
                "Usuário " + membro.getNome() + " adicionado ao grupo " + getNome() + " com permissão " + permissao);
    }

    public Map<Usuario, Permissao> getMembros() {
        return membros;
    }


    public double getSaldoTotalConsolidado() {
        System.out.println("Calculando saldo das contas do grupo " + getNome() + ":");
        return super.getSaldoTotalConsolidado();
    }
}
