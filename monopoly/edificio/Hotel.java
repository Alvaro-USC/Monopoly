package monopoly.edificio;

import monopoly.casilla.propiedad.Solar;
import partida.Jugador;

public final class Hotel extends Edificio {
    public Hotel(Jugador propietario, Solar solar, String grupo, float coste) {
        super("hotel", propietario, solar, grupo, coste);
    }
}