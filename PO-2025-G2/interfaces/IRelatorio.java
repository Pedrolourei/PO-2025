package gestorfinanceiro.interfaces;

import gestorfinanceiro.service.SistemaGestaoFinanceira;

public interface IRelatorio {

    String gerar(SistemaGestaoFinanceira gestor);
}