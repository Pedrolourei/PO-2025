package gestorfinanceiro.interfaces;

import java.io.IOException;

public interface Exportavel {
    void exportar(String caminhoArquivo) throws IOException;
}