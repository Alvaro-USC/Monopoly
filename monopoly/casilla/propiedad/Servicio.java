package monopoly.casilla.propiedad;

import monopoly.Valor;
import monopoly.casilla.Propiedad;
import partida.Jugador;

public class Servicio extends Propiedad {
    public Servicio(String nombre, int posicion, float valor, Jugador duenho) {
        super(nombre, "Servicios", posicion, valor, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        if (getDuenho().equals(banca)) {
            System.out.println("Esta casilla (" + getNombre() + ") pertenece a la banca y se puede comprar.");
            return true;
        }

        if (getDuenho().equals(actual)) {
            return true;
        }

        float toPay = 4 * tirada * Valor.FACTOR_SERVICIO;

        return this.procesarPago(actual, toPay);
    }

    @Override
    public String infoCasilla() {return "{ \n tipo: " + getTipo() + ", \n propietario: " + getDuenho().getNombre() + ", \n valor: " + Valor.formatear(getValor()) + "\n}";}

    @Override
    public String casEnVenta() {return "{\n nombre: " + this.getNombre() + "\n tipo: " + getTipo() + ", \n valor: " + Valor.formatear(getValor()) + "\n}";}
}