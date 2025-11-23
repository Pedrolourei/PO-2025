package gestorfinanceiro.service;

import gestorfinanceiro.interfaces.IAlgoritmoProjecao;
import gestorfinanceiro.model.usuario.Usuario;

/**
 * Implementação Strategy (Módulo 5): Projeção de Saldo.
 */
public class ProjecaoSaldoLinearStrategy implements IAlgoritmoProjecao {

    public double projetarSaldoFuturo(Usuario usuario, int meses) {
        // Lógica de simulação (simplificada):
        // Pega o saldo atual e assume uma média de gastos/receitas.
        // Em um sistema real, buscaria o histórico de lançamentos.
        
        // Simulação: assume R$ 2000 de receita e R$ 1500 de despesa por mês
        double mediaMensalLiquida = 500.0; 
        
        double saldoAtual = usuario.getSaldoTotalConsolidado();
        double saldoProjetado = saldoAtual + (mediaMensalLiquida * meses);
        
        System.out.println("[Projeção Linear] Saldo atual: R$" + saldoAtual + 
                           ". Saldo projetado em " + meses + " meses: R$" + saldoProjetado);
        return saldoProjetado;
    }
}