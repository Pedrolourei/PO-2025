package gestorfinanceiro.interfaces;

import gestorfinanceiro.model.usuario.Usuario;

public interface Notificavel {
    void enviar(Usuario usuario, String mensagem);
}