package monopoly;

import monopoly.casilla.Casilla;
import partida.Jugador;

public class Salida extends Casilla {
    public Salida(int posicion, Jugador duenho) {
        super("Salida", "Especial", posicion, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        return true;
    }

    @Override
    public void comprarCasilla(Jugador solicitante, Jugador banca) {System.out.println("Esta casilla no se puede comprar.");}

    @Override
    public String infoCasilla() {return "{ \n tipo: " + getTipo() + "\n}";}

    @Override
    public String casEnVenta() {
        return "";
    }

}