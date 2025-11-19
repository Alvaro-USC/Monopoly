package monopoly.edificio;

import monopoly.casilla.propiedad.Solar;
import partida.Jugador;

public final class PistaDeporte extends Edificio {
    public PistaDeporte(Solar solar, String grupo, float coste) {
        super("pista_deporte", solar, grupo, coste);
    }
}