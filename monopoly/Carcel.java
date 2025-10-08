package monopoly;

import partida.*;
import java.util.ArrayList;

public class Carcel extends Casilla {
    public Carcel(int posicion, Jugador duenho) {
        super("Carcel", "Especial", posicion, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        return true;
    }

    @Override
    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        System.out.println("Esta casilla no se puede comprar.");
    }

    @Override
    public String infoCasilla() {
        String info = "{ \n tipo: " + getTipo();
        info += ", \n salir: 500000";
        String jugs = "";
        for (Avatar a : getAvatares()) {
            jugs += "[" + a.getJugador().getNombre() + "," + a.getJugador().getTiradasCarcel() + "] ";
        }
        info += ", \n jugadores: " + jugs;
        info += "\n}";
        return info;
    }

    @Override
    public String casEnVenta() {
        return "";
    }

    @Override
    public String representacionColoreada() {
        String rep = getNombre();
        if (!getAvatares().isEmpty()) {
            String avatars = "&";
            for (Avatar a : getAvatares()) {
                avatars += a.getId();
            }
            rep += avatars;
        }
        return rep;
    }
}