package br.com.gestorfinanceiro.service;

import br.com.gestorfinanceiro.interfaces.IRelatorio;
import br.com.gestorfinanceiro.model.lancamento.Lancamento;
import br.com.gestorfinanceiro.model.lancamento.TipoLancamento;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementação de Relatório (Módulo 6): Gastos por Categoria.
 */
public class RelatorioGastosCategoria implements IRelatorio {

    public String gerar(SistemaGestaoFinanceira gestor) {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================\n");
        sb.append("RELATÓRIO: GASTOS POR CATEGORIA (GLOBAL)\n");
        sb.append("=======================================================\n");

        Map<String, Double> gastosPorCategoria = new HashMap<>();

        for (Lancamento l : gestor.getHistoricoLancamentos()) {
            if (l.getTipo() == TipoLancamento.DESPESA) {
                String categoria = l.getCategoria().nomePrincipal;
                double valor = gastosPorCategoria.getOrDefault(categoria, 0.0);
                gastosPorCategoria.put(categoria, valor + l.getValor());
            }
        }

        if (gastosPorCategoria.isEmpty()) {
            sb.append("Nenhuma despesa registrada.\n");
        } else {
            for (Map.Entry<String, Double> entry : gastosPorCategoria.entrySet()) {
                sb.append(String.format("  - %-20s: R$ %.2f\n", entry.getKey(), entry.getValue()));
            }
        }
        sb.append("=======================================================\n");
        return sb.toString();
    }
}