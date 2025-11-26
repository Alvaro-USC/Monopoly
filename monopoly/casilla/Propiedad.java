package monopoly.casilla;

import monopoly.Juego;
import monopoly.StatsTracker;
import monopoly.Valor;
import monopoly.excepcion.AccionInvalidaException;
import monopoly.excepcion.FondosInsuficientesException;
import monopoly.excepcion.PropiedadNoPerteneceException;
import partida.Jugador;

/**
 * Clase intermedia abstracta que agrupa toda la lógica común
 * de las casillas que se pueden comprar (Solares, Transportes, Servicios).
 */
public abstract class Propiedad extends Casilla {

    public Propiedad(String nombre, String tipo, int posicion, float valor, Jugador duenho) {
        super(nombre, tipo, posicion, valor, duenho);
    }

    /**
     * Este método implementa el 'comprar' abstracto de Casilla.
     * Ahora, Solar, Transporte y Servicio heredarán ESTA implementación.
     */
    public void comprar(Jugador solicitante) throws PropiedadNoPerteneceException, AccionInvalidaException {
        Jugador banca = Juego.getInstance().getBanca();
        if (getDuenho().equals(banca)) {
            if (solicitante.getFortuna() >= getValor()) {
                solicitante.sumarGastos(getValor());
                banca.sumarFortuna(getValor());
                setDuenho(solicitante);
                solicitante.anhadirPropiedad(this);
                System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + getNombre() + " por " + Valor.formatear(getValor()) + "€. Su fortuna actual es " + Valor.formatear(solicitante.getFortuna()) + "€.");

                StatsTracker.getInstance().asegurarJugador(solicitante);
                StatsTracker.getInstance().byPlayer.get(solicitante.getNombre()).addDineroInvertido(getValor());

            } else {
                // Fondos insuficientes
                throw new FondosInsuficientesException(getNombre());
            }
        } else {
            // La propiedad ya pertenece a otro jugador
            throw new PropiedadNoPerteneceException(getNombre());
        }
    }

    public boolean perteneceAJugador(Jugador jugador) {
        return this.getDuenho().getNombre().equals(jugador.getNombre());
    }

    public boolean estaHipotecada() {return isHipotecada();}

    public void hipotecar() {this.hipotecada = true;}

    public abstract boolean alquiler();

    public abstract float valor();
}