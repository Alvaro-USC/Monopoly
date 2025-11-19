package monopoly.edificio;

import monopoly.casilla.propiedad.Solar;
import partida.Jugador;

public final class Piscina extends Edificio {
    public Piscina(Solar solar, String grupo, float coste) {
        super("piscina", solar, grupo, coste);
    }
}