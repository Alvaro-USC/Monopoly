package monopoly.casilla;

import partida.Jugador;

public abstract class Especial extends Casilla {

    public Especial(String nombre, int posicion, Jugador duenho) {
        super(nombre, "Especial", posicion, duenho);
    }
}
