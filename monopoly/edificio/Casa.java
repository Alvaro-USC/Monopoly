package monopoly.edificio;

import monopoly.casilla.propiedad.Solar;
import partida.Jugador;

public final class Casa extends Edificio {

    public Casa(Solar solar, String grupo, float coste) {
        super("casa", solar, grupo, coste);
    }

    protected String generarId(String tipo) {
        return "casa-" + (this.getSolar().getCantidadEdificioTipo("casa") + 1);
    }
}
