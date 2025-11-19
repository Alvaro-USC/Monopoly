package monopoly.excepcion;

// Propiedad no pertenece al jugador
public final class PropiedadNoPerteneceException extends PropiedadException {
    public PropiedadNoPerteneceException(String mensaje) {
        super("No eres el propietario de " + mensaje + ". Pertenece a otro jugador.");
    }
}