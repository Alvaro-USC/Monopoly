package monopoly.excepcion;

import monopoly.excepcion.MonopolyException;

// Errores relacionados con propiedades y edificios
public abstract class PropiedadException extends MonopolyException {
    public PropiedadException(String mensaje) {
        super(mensaje);
    }
}