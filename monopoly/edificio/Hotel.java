package monopoly.edificio;

import monopoly.casilla.propiedad.Solar;

public final class Hotel extends Edificio {
    public Hotel(Solar solar, String grupo, float coste) {
        super("hotel", solar, grupo, coste);
    }

    protected String generarId(String tipo) {
        return "hotel-" + (this.getSolar().getCantidadEdificioTipo("hotel") + 1);
    }
}