package monopoly;

import monopoly.casilla.Casilla;
import partida.Jugador;

import java.util.ArrayList;

/**
 * Representa una carta con id, descripción y acción ejecutable.
 */
public class Carta {
    public enum TipoAccion {
        MOVER_A,        // mover a casilla (posible cobrar salida)
        IR_A_CARCEL, COBRAR,         // cobrar cantidad
        PAGAR,          // pagar cantidad
        PAGAR_A_CADA,   // pagar a cada jugador
        COBRAR_DE_CADA, // cobrar de cada jugador
        NINGUNA
    }

    private final int id;
    private final String descripcion;
    private final TipoAccion accion;
    private final float cantidad; // para acciones de pagar/cobrar
    private final String destino; // para acciones de mover (nombre de casilla)

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
    public void ejecutar(Jugador actual, Tablero tablero, ArrayList<Jugador> todosJugadores) {
        System.out.println("Carta: " + descripcion);
        switch (accion) {
            case MOVER_A:
                ArrayList<ArrayList<Casilla>> lados = tablero.getPosiciones();
                ArrayList<Casilla> todas = new ArrayList<>();
                for (ArrayList<Casilla> lado : lados) todas.addAll(lado);

                Casilla actualCasilla = actual.getAvatar().getLugar();
                int posActual = todas.indexOf(actualCasilla);
                if (cantidad < 0) {
                    // movimiento relativo (retroceder)
                    int retroceso = (int) Math.abs(cantidad);

                    int posDestino = (posActual - retroceso + todas.size()) % todas.size();
                    Casilla destinoRetro = todas.get(posDestino);

                    actual.getAvatar().setLugar(destinoRetro);
                    destinoRetro.anhadirAvatar(actual.getAvatar());
                    System.out.println("Retrocedes " + retroceso + " casillas hasta " + destinoRetro.getNombre() + ".");
                    StatsTracker.getInstance().registrarVisita(destinoRetro);
                    destinoRetro.evaluarCasilla(actual, tablero.getBanca(), 0);
                } else if (cantidad > 40) { // TRUCO: Se está pasando en cantidad el valor de cobro de transporte, no una casilla de desplazamiento
                    int movimiento = 1;
                    int posDestino = (posActual + movimiento + todas.size()) % todas.size();
                    Casilla destinoMov = todas.get(posDestino);
                    actual.getAvatar().setLugar(destinoMov);

                    if (!destinoMov.getNombre().contains(destino)) {
                        for (int i = 0; (i < todas.size()) && (!destinoMov.getNombre().contains("Trans")); i++) {
                            actualCasilla = actual.getAvatar().getLugar();
                            posActual = todas.indexOf(actualCasilla);
                            posDestino = (posActual + movimiento + todas.size()) % todas.size();
                            destinoMov = todas.get(posDestino);
                            actual.getAvatar().setLugar(destinoMov);
                        }
                    }

                    destinoMov.anhadirAvatar(actual.getAvatar());
                    System.out.println("Te mueves " + destinoMov + " casillas hasta " + destinoMov.getNombre() + ".");
                    StatsTracker.getInstance().registrarVisita(destinoMov);
                    destinoMov.procesarPago(actual, Valor.ALQUILER_TRANSP * 2);
                } else {
                    boolean pasoPorSalida = false;
                    // movimiento absoluto
                    Casilla destinoCasilla = tablero.encontrar_casilla(destino);
                    if (destinoCasilla == null) {
                        System.out.println("La casilla destino '" + destino + "' no existe.");
                        return;
                    }
                    Casilla lugarAnterior = actual.getAvatar().getLugar();
                    if (lugarAnterior != null) {
                        if (lugarAnterior.getPosicion() > destinoCasilla.getPosicion()) {
                            pasoPorSalida = true;
                        }
                        lugarAnterior.eliminarAvatar(actual.getAvatar());
                    }
                    actual.getAvatar().setLugar(destinoCasilla);
                    destinoCasilla.anhadirAvatar(actual.getAvatar());
                    System.out.println("Avanzas hasta " + destinoCasilla.getNombre() + ".");
                    destinoCasilla.evaluarCasilla(actual, tablero.getBanca(), 0);
                    if (pasoPorSalida && !destino.equalsIgnoreCase("Solar1")) {
                        actual.sumarFortuna(Valor.SUMA_VUELTA);
                        actual.setVueltas(actual.getVueltas() + 1);
                        StatsTracker.getInstance().registrarPasoSalida(actual, Valor.SUMA_VUELTA);

                    }
                    StatsTracker.getInstance().registrarVisita(destinoCasilla);
                }
                break;
            case IR_A_CARCEL:
                actual.encarcelar(tablero.getPosiciones());
                StatsTracker.getInstance().registrarEncarcelamiento(actual);
                System.out.println("Vas directo a la cárcel.");
                break;

            case COBRAR:
                actual.sumarFortuna(cantidad);
                StatsTracker.getInstance().registrarPremioBote(actual, cantidad);
                System.out.println("Recibes " + Valor.formatear(cantidad) + "€.");
                break;

            case PAGAR:
                if (actual.getFortuna() >= cantidad) {
                    actual.sumarGastos(cantidad);
                    // Lo paga a la banca
                    tablero.getBanca().sumarFortuna(cantidad);
                    StatsTracker.getInstance().registrarPagoImpuesto(actual, cantidad);
                    System.out.println("Pagas " + Valor.formatear(cantidad) + "€.");
                } else {
                    System.out.println("No tienes suficiente dinero para pagar " + Valor.formatear(cantidad) + "€. Debes hipotecar propiedades.");
                }
                break;

            case PAGAR_A_CADA:
                float porCada = cantidad;
                float totalPagar = porCada * (todosJugadores.size() - 1);
                if (actual.getFortuna() < totalPagar) {
                    System.out.println("No tienes suficiente dinero para pagar a cada jugador " + Valor.formatear(porCada) + "€. Debes hipotecar propiedades.");
                    // no se realiza pago
                    return;
                }
                // Paga a cada jugador
                for (Jugador otro : todosJugadores) {
                    if (otro.equals(actual)) continue;
                    actual.sumarGastos(porCada);
                    otro.sumarFortuna(porCada);
                    StatsTracker.getInstance().registrarPagoEntreJugadores(actual, otro, porCada);
                }
                System.out.println("Has pagado " + Valor.formatear(porCada) + "€ a cada jugador.");
                break;

            case COBRAR_DE_CADA:
                float porCadaCobrar = cantidad;
                for (Jugador otro : todosJugadores) {
                    if (otro.equals(actual)) continue;
                    if (otro.getFortuna() < porCadaCobrar) {
                        System.out.println(otro.getNombre() + " no tiene suficiente para pagar " + Valor.formatear(porCadaCobrar) + "€ a " + actual.getNombre() + ". Debe hipotecar.");
                        // no forzamos; dejamos que los jugadores gestionen su liquidez
                        continue;
                    }
                    otro.sumarGastos(porCadaCobrar);
                    actual.sumarFortuna(porCadaCobrar);
                    StatsTracker.getInstance().registrarPagoEntreJugadores(otro, actual, porCadaCobrar);
                }
                System.out.println("Has cobrado " + Valor.formatear(porCadaCobrar) + "€ a cada jugador (si han podido).");
                break;

            default:
                System.out.println("Carta sin acción definida.");
        }
    }
}