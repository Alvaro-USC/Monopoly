package monopoly.carta;

import monopoly.StatsTracker;
import monopoly.Tablero;
import monopoly.Valor;
import monopoly.casilla.Casilla;
import partida.Jugador;

import java.util.ArrayList;

public class CartaCajaComunidad extends Carta {

    public CartaCajaComunidad(int id, String descripcion, TipoAccion accion, float cantidad, String destino) {
        super(id, descripcion, accion, cantidad, destino);
    }

    @Override
    public void accion(Jugador actual, Tablero tablero, ArrayList<Jugador> todosJugadores) {
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
