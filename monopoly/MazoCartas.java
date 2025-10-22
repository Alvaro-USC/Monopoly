package monopoly;

import partida.*;
import java.util.ArrayList;

/**
 * Mazo circular de cartas Suerte o Caja de Comunidad (Apéndice A).
 */
public class MazoCartas {
    private final ArrayList<Carta> cartas;
    private int siguiente = 0;
    private final boolean suerte;

    public MazoCartas(boolean suerte) {
        this.suerte = suerte;
        cartas = new ArrayList<>();
        if (suerte) crearMazoSuerte();
        else crearMazoCaja();
    }

    private void crearMazoSuerte() {
        cartas.add(new Carta(1, "Decides hacer un viaje de placer. Avanza hasta Solar19. Si pasas por la casilla de Salida, cobra 2.000.000€.",
                Carta.TipoAccion.MOVER_A, 0f, "Solar19"));
        cartas.add(new Carta(2, "Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€.",
                Carta.TipoAccion.IR_A_CARCEL, 0f, null));
        cartas.add(new Carta(3, "¡Has ganado el bote de la lotería! Recibe 1.000.000€.",
                Carta.TipoAccion.COBRAR, 1_000_000f, null));
        cartas.add(new Carta(4, "Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€.",
                Carta.TipoAccion.PAGAR_A_CADA, 250_000f, null));
        cartas.add(new Carta(5, "¡Hora punta de tráfico! Retrocede tres casillas.",
                Carta.TipoAccion.MOVER_A, -3f, null)); // retroceder: movimiento negativo
        cartas.add(new Carta(6, "Te multan por usar el móvil mientras conduces. Paga 150.000€.",
                Carta.TipoAccion.PAGAR, 150_000f, null));
    }

    private void crearMazoCaja() {
        cartas.add(new Carta(1, "Paga 500.000€ por un fin de semana en un balneario de 5 estrellas.",
                Carta.TipoAccion.PAGAR, 500_000f, null));
        cartas.add(new Carta(2, "Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€.",
                Carta.TipoAccion.IR_A_CARCEL, 0f, null));
        cartas.add(new Carta(3, "Colócate en la casilla de Salida. Cobra 2.000.000€.",
                Carta.TipoAccion.MOVER_A, 0f, "Salida"));
        cartas.add(new Carta(4, "Devolución de Hacienda. Cobra 500.000€.",
                Carta.TipoAccion.COBRAR, 500_000f, null));
        cartas.add(new Carta(5, "Retrocede hasta Solar1 para comprar antigüedades exóticas.",
                Carta.TipoAccion.MOVER_A, 0f, "Solar1"));
        cartas.add(new Carta(6, "Ve a Solar20 para disfrutar del San Fermín. Si pasas por la casilla de Salida, cobra 2.000.000€.",
                Carta.TipoAccion.MOVER_A, 0f, "Solar20"));
    }

    /**
     * Saca la siguiente carta circularmente.
     */
    public Carta sacarCarta() {
        if (cartas.isEmpty()) return null;
        Carta c = cartas.get(siguiente);
        siguiente = (siguiente + 1) % cartas.size();
        return c;
    }

    public boolean esSuerte() { return suerte; }
}