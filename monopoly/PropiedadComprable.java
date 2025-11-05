// CREAR NUEVO ARCHIVO: PropiedadComprable.java

package monopoly; // O tu paquete 'partida'

import partida.Jugador;

/**
 * Clase intermedia abstracta que agrupa toda la lógica común
 * de las casillas que se pueden comprar (Solares, Transportes, Servicios).
 */
public abstract class PropiedadComprable extends Casilla {

    public PropiedadComprable(String nombre, String tipo, int posicion, float valor, Jugador duenho) {
        super(nombre, tipo, posicion, valor, duenho);
    }

    /**
     * Este método implementa el 'comprarCasilla' abstracto de Casilla.
     * Ahora, Solar, Transporte y Servicio heredarán ESTA implementación.
     */
    @Override
    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        if (getDuenho().equals(banca)) {
            if (solicitante.getFortuna() >= getValor()) {
                solicitante.sumarFortuna(-getValor());
                banca.sumarFortuna(getValor());
                setDuenho(solicitante);
                solicitante.anhadirPropiedad(this);
                System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + getNombre() + " por " + Valor.formatear(getValor()) + "€. Su fortuna actual es " + Valor.formatear(solicitante.getFortuna()) + "€.");

                StatsTracker.getInstance().asegurarJugador(solicitante);
                StatsTracker.getInstance().byPlayer.get(solicitante.getNombre()).addDineroInvertido(getValor());

            } else {
                System.out.println("No tienes suficiente dinero para comprar esta casilla.");
            }
        } else {
            // Un mensaje genérico para todas las propiedades compradas
            System.out.println("Esta casilla no se puede comprar, es de " + getDuenho().getNombre());
        }
    }
}