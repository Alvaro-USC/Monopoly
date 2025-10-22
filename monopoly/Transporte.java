package monopoly;

import partida.*;
import java.util.ArrayList;

public class Transporte extends Casilla {
    public Transporte(String nombre, int posicion, float valor, Jugador duenho) {
        super(nombre, "Transporte", posicion, valor, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        boolean solv = true;
        if (!getDuenho().equals(banca) && !getDuenho().equals(actual)) {
            float toPay = getImpuesto();
            if (actual.getFortuna() < toPay) {
                solv = false;
                System.out.println("No tienes suficiente dinero. Debes hipotecar una propiedad o declararte en bancarrota.");
            } else {
                actual.sumarFortuna(-toPay);
                getDuenho().sumarFortuna(toPay);
                System.out.println("Se han pagado " + Valor.formatear(toPay) + " € de alquiler.");
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
                System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + getNombre() + " por " + Valor.formatear(getValor()) + "€. Su fortuna actual es " + Valor.formatear(solicitante.getFortuna()) + "€.");
            } else {
                System.out.println("No tienes suficiente dinero para comprar esta casilla.");
            }
        } else {
            System.out.println("Esta casilla no se puede comprar.");
        }
    }

    @Override
    public String infoCasilla() {
        String info = "{ \n tipo: " + getTipo() + ", \n propietario: " + getDuenho().getNombre() + ", \n valor: " + Valor.formatear(getValor()) + ", \n alquiler: " + getImpuesto() + "\n}";
        return info;
    }

    @Override
    public String casEnVenta() {
        return "{\n nombre: " + this.getNombre() +  "\n tipo: " + getTipo() + ", \n valor: " + Valor.formatear(getValor()) + "\n}";
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