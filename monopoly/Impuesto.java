package monopoly;

import partida.*;
import java.util.ArrayList;

public class Impuesto extends Casilla {
    public Impuesto(String nombre, int posicion, float impuesto, Jugador duenho) {
        super(nombre, posicion, impuesto, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        boolean solv = true;
        float toPay = getImpuesto();
        if (actual.getFortuna() < toPay) {
            solv = false;
        } else {
            actual.sumarFortuna(-toPay);
            Casilla parking = getTablero().encontrar_casilla("Parking");
            parking.sumarValor(toPay);
            System.out.println("El jugador paga " + Valor.formatear(toPay) + "€ que se depositan en el Parking.");
        }
        return solv;
    }

    @Override
    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        System.out.println("Esta casilla no se puede comprar.");
    }

    @Override
    public String infoCasilla() {
        String info = "{ \n tipo: " + getTipo() + ", \n apagar: " + Valor.formatear(getImpuesto()) + "\n}";
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