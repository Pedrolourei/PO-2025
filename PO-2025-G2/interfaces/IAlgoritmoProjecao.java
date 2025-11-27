package gestorfinanceiro.interfaces;

import gestorfinanceiro.model.usuario.Usuario;

/**
 * Padrão Strategy: Define a interface para algoritmos de projeção (Módulo 5).
 */
public interface IAlgoritmoProjecao {
    double projetarSaldoFuturo(Usuario usuario, int meses);
}