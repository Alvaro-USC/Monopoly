package monopoly.casilla.accion;

import monopoly.casilla.Accion;
import partida.Jugador;

public class Suerte extends Accion {
    public Suerte(int posicion, Jugador duenho) {
        super("Suerte", posicion, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        return true;
    }

    @Override
    public String infoCasilla() {return "{ \n tipo: " + getTipo() + "\n}";}

    @Override
    public String casEnVenta() {
        return "";
    }

}