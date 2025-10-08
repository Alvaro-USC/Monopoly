package monopoly;

import partida.*;
import java.util.ArrayList;

public class Solar extends Casilla {
    public Solar(String nombre, int posicion, float valor, Jugador duenho) {
        super(nombre, "Solar", posicion, valor, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        boolean solv = true;
        if (!getDuenho().equals(banca) && !getDuenho().equals(actual)) {
            float toPay = getImpuesto();
            if (getGrupo() != null && getGrupo().esDuenhoGrupo(this.getDuenho())) {
                toPay *= 2;
            }
            if (actual.getFortuna() < toPay) {
                solv = false;
                System.out.println("No tienes suficiente dinero. Debes hipotecar una propiedad o declararte en bancarrota.");
            } else {
                actual.sumarFortuna(-toPay);
                getDuenho().sumarFortuna(toPay);
                System.out.println("Se han pagado " + toPay + " € de alquiler.");
            }
        }
        return solv;
    }

    @Override
    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        if (getDuenho().equals(banca)) {
            if (solicitante.getFortuna() >= getValor()) {
                solicitante.sumarFortuna(-getValor());
                banca.sumarFortuna(getValor());
                setDuenho(solicitante);
                solicitante.anhadirPropiedad(this);
                System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + getNombre() + " por " + getValor() + "€. Su fortuna actual es " + solicitante.getFortuna() + "€.");
            } else {
                System.out.println("No tienes suficiente dinero para comprar esta casilla.");
            }
        } else {
            System.out.println("Esta casilla no se puede comprar.");
        }
    }

    @Override
    public String infoCasilla() {
        String info = "{ \n tipo: " + getTipo();
        info += ", \n grupo: " + (getGrupo() != null ? getGrupo().getColorGrupo() : "") + ", \n propietario: " + getDuenho().getNombre() + ", \n valor: " + getValor() + ", \n alquiler: " + getImpuesto();
        info += "\n}";
        return info;
    }

    @Override
    public String casEnVenta() {
        String g = (getGrupo() != null ? " grupo: " + getGrupo().getColorGrupo() + "," : "");
        return "{ \n tipo: " + getTipo() + "," + g + " \n valor: " + getValor() + "\n}";
    }

    @Override
    public String representacionColoreada() {
        String rep = getNombre();
        String color = "";
        if (getGrupo() != null) {
            color = getGrupo().getAnsiColor();
            rep = color + rep;
        }
        if (!getAvatares().isEmpty()) {
            String avatars = "&";
            for (Avatar a : getAvatares()) {
                avatars += a.getId();
            }
            rep += color + avatars;
        }
        if (!color.isEmpty()) {
            rep += Valor.RESET;
        }
        return rep;
    }
}