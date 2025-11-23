package gestorfinanceiro.exception;

public class UsuarioSemPermissaoException extends Exception {
    public UsuarioSemPermissaoException(String message) {
        super(message);
    }
}