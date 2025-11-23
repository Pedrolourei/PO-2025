package gestorfinanceiro.service;

import gestorfinanceiro.interfaces.Notificavel;
import gestorfinanceiro.model.usuario.Usuario;

/**
 * Implementação polimórfica de Notificação (via Console).
 */
public class ConsoleNotificador implements Notificavel {

    public void enviar(Usuario usuario, String mensagem) {
        System.out.println("\n====================== [ALERTA] ======================");
        System.out.println("Para: " + usuario.getNome());
        System.out.println("Mensagem: " + mensagem);
        System.out.println("======================================================\n");
    }
}