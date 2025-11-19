package monopoly.excepcion;

// Trato inválido entre jugadores
public final class TratoInvalidoException extends AccionInvalidaException {
    public TratoInvalidoException(String mensaje) {
        super(mensaje);
    }
}