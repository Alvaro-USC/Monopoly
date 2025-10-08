package monopoly;

import partida.*;
import java.util.ArrayList;

public class IrCarcel extends Casilla {
    public IrCarcel(int posicion, Jugador duenho) {
        super("IrCarcel", "Especial", posicion, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        Casilla carcel = getTablero().encontrar_casilla("Carcel");
        if (carcel == null) {
            System.out.println("Error: No se encontró la casilla Carcel.");
        } else {
            Casilla lugarAnterior = actual.getAvatar().getLugar();
            if (lugarAnterior != null) {
                lugarAnterior.eliminarAvatar(actual.getAvatar());
            }
            actual.getAvatar().setLugar(carcel);
            carcel.anhadirAvatar(actual.getAvatar());
            actual.setEnCarcel(true);
            actual.setTiradasCarcel(0);
            System.out.println("El avatar se coloca en la casilla de Carcel.");
        }
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