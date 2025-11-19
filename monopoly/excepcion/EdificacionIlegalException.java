package monopoly.excepcion;

// Edificación ilegal (por ejemplo, construir hotel sin tener casas)
public final class EdificacionIlegalException extends PropiedadException {
    public EdificacionIlegalException() {
        super("No se puede edificar en este grupo mientras haya propiedades hipotecadas.");
    }

    public EdificacionIlegalException(String mensaje) {
        super(mensaje);
    }
}