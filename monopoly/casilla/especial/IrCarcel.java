package monopoly;

import monopoly.casilla.Casilla;
import partida.Jugador;

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
    public void comprarCasilla(Jugador solicitante, Jugador banca) {System.out.println("Esta casilla no se puede comprar.");}

    @Override
    public String infoCasilla() {return "{ \n tipo: " + getTipo() + "\n}";}

    @Override
    public String casEnVenta() {
        return "";
    }

}