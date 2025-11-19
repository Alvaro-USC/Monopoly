package monopoly.excepcion;

import monopoly.Valor;

// Fondos insuficientes
public final class FondosInsuficientesException extends AccionInvalidaException {
    public FondosInsuficientesException(float cantidad) {
        super("No tienes suficiente dinero (" + Valor.formatear(cantidad) + "€) para deshipotecar esta propiedad.");
    }

    public FondosInsuficientesException(String nombreCasilla) {
        super("No tienes suficiente dinero para comprar / edificar " + nombreCasilla);
    }
}