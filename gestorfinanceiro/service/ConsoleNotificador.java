package br.com.gestorfinanceiro.service;

import br.com.gestorfinanceiro.interfaces.Notificavel;
import br.com.gestorfinanceiro.model.usuario.Usuario;

/**
 * Implementação polimórfica de Notificação (via Console).
 */
public class ConsoleNotificador implements Notificavel {
    @Override
    public void enviar(Usuario usuario, String mensagem) {
        System.out.println("\n====================== [ALERTA] ======================");
        System.out.println("Para: " + usuario.getNome());
        System.out.println("Mensagem: " + mensagem);
        System.out.println("======================================================\n");
    }
}