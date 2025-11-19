package monopoly;

import monopoly.casilla.PropiedadComprable;
import partida.Jugador;

public class Transporte extends PropiedadComprable {
    public Transporte(String nombre, int posicion, float valor, Jugador duenho) {
        super(nombre, "Transporte", posicion, valor, duenho);
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {

        // La casilla es de la banca (se puede comprar)
        if (getDuenho().equals(banca)) {
            System.out.println("El dueño de esta casilla de transporte es " + getDuenho().getNombre() + ", se puede comprar.");
            return true;
        }

        if (getDuenho().equals(actual)) {
            return true;
        }

        float toPay = getImpuesto();
        return this.procesarPago(actual, toPay);
    }

    @Override
    public String infoCasilla() {return "{ \n tipo: " + getTipo() + ", \n propietario: " + getDuenho().getNombre() + ", \n valor: " + Valor.formatear(getValor()) + ", \n alquiler: " + getImpuesto() + "\n}";}

    @Override
    public String casEnVenta() {return "{\n nombre: " + this.getNombre() + "\n tipo: " + getTipo() + ", \n valor: " + Valor.formatear(getValor()) + "\n}";}

}