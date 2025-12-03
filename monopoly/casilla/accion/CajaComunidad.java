package monopoly.casilla.accion;

import monopoly.casilla.Accion;
import partida.Jugador;

public class CajaComunidad extends Accion {
    public CajaComunidad(int posicion, Jugador duenho) {
        super("Caja", posicion, duenho);
    }

    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        return true;
    }

    public String infoCasilla() {return "{ \n tipo: " + getTipo() + "\n}";}
}