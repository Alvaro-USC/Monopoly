package monopoly.casilla;

import monopoly.Valor;
import partida.Jugador;

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
            System.out.println("No eres solvente, vas a estar en negativo.");
        }
        actual.sumarGastos(toPay);
        Casilla parking = getTablero().encontrar_casilla("Parking");
        parking.sumarValor(toPay);
        System.out.println("El jugador paga " + Valor.formatear(toPay) + "€ en impuestos, que se depositan en el Parking.");
        monopoly.StatsTracker.getInstance().registrarPagoImpuesto(actual, toPay);
        return solv;
    }

    @Override
    public void comprarCasilla(Jugador solicitante, Jugador banca) {System.out.println("Esta casilla no se puede comprar.");}

    @Override
    public String infoCasilla() {return "{ \n tipo: " + getTipo() + ", \n apagar: " + Valor.formatear(getImpuesto()) + "\n}";}

    @Override
    public String casEnVenta() {
        return "";
    }

}