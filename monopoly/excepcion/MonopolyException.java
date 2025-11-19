package monopoly.excepcion;

/**
 * Clase base para todas las excepciones de Monopoly.
 */
public abstract class MonopolyException extends Exception {
    public MonopolyException(String mensaje) {
        super(mensaje);
    }
}
