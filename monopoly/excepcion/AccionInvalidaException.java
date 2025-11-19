package monopoly.excepcion;

// Errores relacionados con la acción del jugador
public class AccionInvalidaException extends MonopolyException {
    public AccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}