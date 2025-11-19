package monopoly.excepcion;

// Propiedad ya hipotecada
public final class PropiedadNoHipotecadaException extends PropiedadException {
    public PropiedadNoHipotecadaException() {
        super("No se puede deshipotecar una propiedad que no está hipotecada.");
    }
}