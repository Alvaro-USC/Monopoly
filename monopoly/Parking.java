package monopoly;

import partida.*;
import java.util.ArrayList;

public class Parking extends Casilla {
    public Parking(int posicion, Jugador duenho) {
        super("Parking", "Especial", posicion, duenho);
        setValor(0f);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        float bote = getValor();
        actual.sumarFortuna(bote);
        setValor(0);
        System.out.println("El jugador recibe " + bote + "€.");
        return true;
    }

    @Override
    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        System.out.println("Esta casilla no se puede comprar.");
    }

    @Override
    public String infoCasilla() {
        String info = "{ \n tipo: " + getTipo();
        info += ", \n bote: " + getValor();
        String jugs = "";
        for (Avatar a : getAvatares()) {
            jugs += a.getJugador().getNombre() + ", ";
        }
        if (jugs.endsWith(", ")) jugs = jugs.substring(0, jugs.length() - 2);
        info += ", \n jugadores: [" + jugs + "]";
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