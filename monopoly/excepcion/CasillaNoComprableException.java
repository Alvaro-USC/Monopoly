package monopoly.excepcion;

// Casilla no comprable (especiales como Suerte)
public final class CasillaNoComprableException extends PropiedadException {
    public CasillaNoComprableException(String mensaje) {
        super(mensaje);
    }
}