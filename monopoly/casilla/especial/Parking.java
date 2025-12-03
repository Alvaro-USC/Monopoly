package monopoly.casilla.especial;

import monopoly.Valor;
import monopoly.casilla.Especial;
import partida.Avatar;
import partida.Jugador;

import static monopoly.Juego.consola;

public class Parking extends Especial {
    public Parking(int posicion, Jugador duenho) {
        super("Parking", posicion, duenho);
        setValor(0f);
    }

    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        float bote = getValor();
        actual.sumarFortuna(bote);
        setValor(0);
        consola.imprimir("El jugador recibe " + Valor.formatear(bote) + "€.");
        monopoly.StatsTracker.getInstance().registrarPremioBote(actual, bote);
        return true;
    }

    public String infoCasilla() {
        String info = "{ \n tipo: " + getTipo();
        info += ", \n bote: " + getValor();
        StringBuilder jugs = new StringBuilder();
        for (Avatar a : getAvatares()) {
            jugs.append(a.getJugador().getNombre()).append(", ");
        }
        if (jugs.toString().endsWith(", ")) jugs = new StringBuilder(jugs.substring(0, jugs.length() - 2));
        info += ", \n jugadores: [" + jugs + "]";
        info += "\n}";
        return info;
    }
}