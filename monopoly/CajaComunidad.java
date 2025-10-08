package monopoly;

import partida.*;
import java.util.ArrayList;

public class CajaComunidad extends Casilla {
    public CajaComunidad(int posicion, Jugador duenho) {
        super("Caja", "CajaComunidad", posicion, duenho);
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
        String info = "{ \n tipo: " + getTipo() + "\n}";
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