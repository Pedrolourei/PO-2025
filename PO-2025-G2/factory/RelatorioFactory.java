package gestorfinanceiro.factory;

import gestorfinanceiro.interfaces.IRelatorio;
import gestorfinanceiro.service.RelatorioGastosCategoria; // Implementação concreta

/**
 * Padrão Factory: Cria instâncias de Relatórios (Módulo 6).
 */
public class RelatorioFactory {
    
    public IRelatorio criarRelatorio(TipoRelatorio tipo) {
        switch (tipo) {
            case GASTOS_POR_CATEGORIA:
                return (IRelatorio) new RelatorioGastosCategoria();
            case EVOLUCAO_SALDO:
                // return new RelatorioEvolucaoSaldo(); // Implementação futura
            case RANKING_DESPESAS:
                // return new RelatorioRankingDespesas(); // Implementação futura
            case RESUMO_GRUPO:
                // return new RelatorioResumoGrupo(); // Implementação futura
            default:
                throw new IllegalArgumentException("Tipo de relatório desconhecido.");
        }
    }
}