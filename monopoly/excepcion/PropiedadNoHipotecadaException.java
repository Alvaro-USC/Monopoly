package monopoly.excepcion;

// Propiedad ya hipotecada
public final class PropiedadYaHipotecadaException extends PropiedadException {
    public PropiedadYaHipotecadaException() {
        super("No se puede hipotecar una propiedad ya hipotecada.\nDebes vender todos los edificios antes de poder hipotecar.");
    }
}