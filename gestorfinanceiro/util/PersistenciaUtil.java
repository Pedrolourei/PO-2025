package gestorfinanceiro.util;

import java.io.*;

/**
 * Módulo 7: Persistência usando Serialização Java.
 */
public class PersistenciaUtil {

    /**
     * Salva um objeto serializável em arquivo.
     */
    public static void salvar(Serializable objeto, String caminhoArquivo) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(caminhoArquivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(objeto);
        }
    }

    /**
     * Carrega um objeto serializável do arquivo.
     */
    public static Object carregar(String caminhoArquivo) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(caminhoArquivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        }
    }
}