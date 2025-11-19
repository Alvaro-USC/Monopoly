package monopoly.carta;

import monopoly.Tablero;
import partida.Jugador;

import java.util.ArrayList;

/**
 * Representa una carta con ID, descripción y acción ejecutable.
 */
public abstract class Carta {
    public enum TipoAccion {
        MOVER_A,        // mover a casilla (posible cobrar salida)
        IR_A_CARCEL, COBRAR,         // cobrar cantidad
        PAGAR,          // pagar cantidad
        PAGAR_A_CADA,   // pagar a cada jugador
        COBRAR_DE_CADA, // cobrar de cada jugador
        NINGUNA
    }

    protected final int id;
    protected final String descripcion;
    protected final TipoAccion accion;
    protected final float cantidad; // para acciones de pagar/cobrar
    protected final String destino; // para acciones de mover (nombre de casilla)
    public Carta(int id, String descripcion, TipoAccion accion, float cantidad, String destino) {
        this.id = id;
        this.descripcion = descripcion;
        this.accion = accion;
        this.cantidad = cantidad;
        this.destino = destino;
    }

    public int getId() {return id;}

    public String getDescripcion() {return descripcion;}

    /**
     * Ejecuta la acción de la carta sobre el jugador actual.
     * No fuerza hipotecar ni bancarrota automática: si el jugador no puede pagar, se imprime un aviso
     * y se deja que el jugador use los comandos disponibles (hipotecar, vender, etc.).
     */
    public abstract void accion(Jugador actual, Tablero tablero, ArrayList<Jugador> todosJugadores);
}