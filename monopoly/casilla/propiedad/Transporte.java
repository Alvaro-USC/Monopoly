package monopoly.casilla.propiedad;

import monopoly.Valor;
import monopoly.casilla.Propiedad;
import monopoly.excepcion.FondosInsuficientesException;
import partida.Jugador;

import static monopoly.Juego.consola;

public final class Transporte extends Propiedad {
    public Transporte(String nombre, int posicion, float valor, Jugador duenho) {
        super(nombre, "Transporte", posicion, valor, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {

        // La casilla es de la banca (se puede comprar)
        if (getDuenho().equals(banca)) {
            consola.imprimir("El dueño de esta casilla de transporte es " + getDuenho().getNombre() + ", se puede comprar.");
            return true;
        }

        if (getDuenho().equals(actual)) {
            return true;
        }

        float toPay = getImpuesto();
        try {
            return this.procesarPago(actual, toPay);

        } catch (FondosInsuficientesException e) {
            return false;
        }
    }

    @Override
    public String infoCasilla() {return "{ \n tipo: " + getTipo() + ", \n propietario: " + getDuenho().getNombre() + ", \n valor: " + Valor.formatear(getValor()) + ", \n alquiler: " + getImpuesto() + "\n}";}

    @Override
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