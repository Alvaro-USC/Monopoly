package monopoly.edificio;

import monopoly.casilla.propiedad.Solar;
import partida.Jugador;

public final class Hotel extends Edificio {
    public Hotel(Solar solar, String grupo, float coste) {
        super("hotel", solar, grupo, coste);
    }
}