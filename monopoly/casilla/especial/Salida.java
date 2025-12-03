package monopoly.casilla.especial;

import monopoly.casilla.Casilla;
import partida.Jugador;

public class Salida extends Casilla {
    public Salida(int posicion, Jugador duenho) {
        super("Salida", "Especial", posicion, duenho);
    }

    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        return true;
    }

    public String infoCasilla() {return "{ \n tipo: " + getTipo() + "\n}";}
}