package monopoly.edificio;

import monopoly.casilla.propiedad.Solar;
import partida.Jugador;

public final class Casa extends Edificio {

    public Casa(Jugador propietario, Solar solar, String grupo, float coste) {
        super("casa", propietario, solar, grupo, coste);
    }
}
