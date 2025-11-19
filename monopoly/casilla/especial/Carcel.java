package monopoly.casilla.especial;

import monopoly.casilla.Casilla;
import partida.Avatar;
import partida.Jugador;

public class Carcel extends Casilla {
    public Carcel(int posicion, Jugador duenho) {
        super("Carcel", "Especial", posicion, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        return true;
    }

    @Override
    public void comprarCasilla(Jugador solicitante, Jugador banca) {System.out.println("Esta casilla no se puede comprar.");}

    @Override
    public String infoCasilla() {
        String info = "{ \n tipo: " + getTipo();
        info += ", \n salir: 500.000"; // Cantidad a pagar para salir de cárcel
        StringBuilder jugs = new StringBuilder();
        for (Avatar a : getAvatares()) {
            jugs.append("[").append(a.getJugador().getNombre()).append(",").append(a.getJugador().getTiradasCarcel()).append("] ");
        }
        info += ", \n jugadores: " + jugs;
        info += "\n}";
        return info;
    }

    @Override
    public String casEnVenta() {
        return "";
    }

}