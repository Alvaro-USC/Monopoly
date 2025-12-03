package monopoly.casilla.propiedad;

import monopoly.Valor;
import monopoly.casilla.Propiedad;
import monopoly.excepcion.FondosInsuficientesException;
import partida.Jugador;

import static monopoly.Juego.consola;

public final class Servicio extends Propiedad {
    public Servicio(String nombre, int posicion, float valor, Jugador duenho) {
        super(nombre, "Servicios", posicion, valor, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        if (getDuenho().equals(banca)) {
            consola.imprimir("Esta casilla (" + getNombre() + ") pertenece a la banca y se puede comprar.");
            return true;
        }

        if (getDuenho().equals(actual)) {
            return true;
        }

        float toPay = 4 * tirada * Valor.FACTOR_SERVICIO;

        try {
            return this.procesarPago(actual, toPay);
        } catch (FondosInsuficientesException e) {
            return false;
        }
    }

    public String infoCasilla() {return "{ \n tipo: " + getTipo() + ", \n propietario: " + getDuenho().getNombre() + ", \n valor: " + Valor.formatear(getValor()) + "\n}";}

    public String casEnVenta() {return "{\n nombre: " + this.getNombre() + "\n tipo: " + getTipo() + ", \n valor: " + Valor.formatear(getValor()) + "\n}";}

    @Override
    public boolean alquiler() {
        return getImpuesto() > 0;
    }

    @Override
    public float valor() {
        return getValor();
    }
}