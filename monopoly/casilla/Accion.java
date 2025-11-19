package monopoly.casilla;

import partida.Jugador;

public abstract class Accion extends Casilla {

    public Accion(String nombre, int posicion, Jugador duenho) {
        super(nombre, "Especial", posicion, duenho);
    }

}
