package monopoly;

import monopoly.casilla.Casilla;
import monopoly.casilla.propiedad.Solar;
import monopoly.edificio.Edificio;
import partida.Jugador;
import partida.PlayerStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton responsable de recoger estadísticas globales y por casilla/grupo.
 */
public class StatsTracker {

    private static StatsTracker instance = null;
    // estadísticas por jugador (referencia por nombre)
    public final Map<String, PlayerStats> byPlayer = new HashMap<>();
    // visitas por casilla (clave: nombre casilla)
    private final Map<String, Integer> visits = new HashMap<>();
    // alquiler recolectado por casilla
    private final Map<String, Float> alquilerRecibido = new HashMap<>();
    // alquiler recolectado por grupo (color)
    private final Map<String, Float> alquilerPorGrupo = new HashMap<>();
    // vueltas por jugador
    private final Map<String, Integer> laps = new HashMap<>();
    private final ArrayList<Jugador> jugadores = new ArrayList<>();

    private StatsTracker() {
    }

    public static StatsTracker getInstance() {
        if (instance == null) instance = new StatsTracker();
        return instance;
    }

    private static String getJugadorEnCabeza(ArrayList<Jugador> jugadores) {
        String jugadorEnCabeza = null;
        float maxFortuna = Float.NEGATIVE_INFINITY;
        for (Jugador j : jugadores) {
            float total = j.getFortuna();
            // sumar valor de propiedades y edificios (usamos getValor() de Casilla y coste de Edificio aproximado)
            for (Casilla c : j.getPropiedades()) {
                total += c.getValor();
                if (c instanceof Solar s) {
                    for (Edificio ed : s.getEdificios()) total += ed.getCoste();
                }
            }
            if (total > maxFortuna) {
                maxFortuna = total;
                jugadorEnCabeza = j.getNombre();
            }
        }
        return jugadorEnCabeza;
    }

    public void asegurarJugador(Jugador j) {
        if (!byPlayer.containsKey(j.getNombre())) {
            byPlayer.put(j.getNombre(), new PlayerStats());
            laps.put(j.getNombre(), 0);
        }
    }

    public void registrarVisita(Casilla c) {
        if (c == null) return;
        visits.put(c.getNombre(), visits.getOrDefault(c.getNombre(), 0) + 1);
    }

    public void registrarAlquiler(Casilla c, float amount) {
        if (c == null) return;
        alquilerRecibido.put(c.getNombre(), alquilerRecibido.getOrDefault(c.getNombre(), 0f) + amount);
        if (c.getGrupo() != null) {
            String color = c.getGrupo().getColorGrupo();
            alquilerPorGrupo.put(color, alquilerPorGrupo.getOrDefault(color, 0f) + amount);
        }
    }

    public void registrarPagoImpuesto(Jugador jugador, float amount) {
        asegurarJugador(jugador);
        byPlayer.get(jugador.getNombre()).addPagoTasas(amount);
    }

    public void registrarPremioBote(Jugador jugador, float amount) {
        asegurarJugador(jugador);
        byPlayer.get(jugador.getNombre()).addPremioBote(amount);
    }

    public void registrarPagoAlquiler(Jugador jugador, float amount) {
        asegurarJugador(jugador);
        byPlayer.get(jugador.getNombre()).addPagoAlquiler(amount);
    }

    public void registrarCobroAlquiler(Jugador jugador, float amount) {
        asegurarJugador(jugador);
        byPlayer.get(jugador.getNombre()).addCobroAlquiler(amount);
    }

    public void registrarPagoEntreJugadores(Jugador pagador, Jugador receptor, float amount) {
        asegurarJugador(pagador);
        asegurarJugador(receptor);
        byPlayer.get(pagador.getNombre()).addPagoAlquiler(amount);
        byPlayer.get(receptor.getNombre()).addCobroAlquiler(amount);
    }

    public void registrarPasoSalida(Jugador jugador, float amount) {
        asegurarJugador(jugador);
        byPlayer.get(jugador.getNombre()).addPasarSalida(amount);
        laps.put(jugador.getNombre(), laps.getOrDefault(jugador.getNombre(), 0) + 1);
    }

    // Reportes

    public void registrarEncarcelamiento(Jugador jugador) {
        asegurarJugador(jugador);
        byPlayer.get(jugador.getNombre()).addVezEnCarcel();
    }

    public String reporteJugador(String nombre, ArrayList<Jugador> jugadores) {
        for (Jugador j : jugadores) {
            asegurarJugador(j);
        }
        if (!byPlayer.containsKey(nombre)) return "No hay estadísticas para " + nombre;
        PlayerStats s = byPlayer.get(nombre);
        return "{\n" + "  dineroInvertido: " + Valor.formatear(s.getDineroInvertido()) + ",\n" + "  pagoTasasEImpuestos: " + Valor.formatear(s.getPagoTasasEImpuestos()) + ",\n" + "  pagoDeAlquileres: " + Valor.formatear(s.getPagoDeAlquileres()) + ",\n" + "  cobroDeAlquileres: " + Valor.formatear(s.getCobroDeAlquileres()) + ",\n" + "  pasarPorSalida: " + Valor.formatear(s.getPasarPorSalida()) + " (" + Valor.formatear(s.getPasarPorSalida() / Valor.SUMA_VUELTA) + " vuelta(s))" + ",\n" + "  premiosBote: " + Valor.formatear(s.getPremiosBote()) + ",\n" + "  vecesEnLaCarcel: " + Valor.formatear(s.getVecesEnLaCarcel()) + "\n" + "}";
    }

    public int frecuenciaVisitada(String nombreCasilla) {
        int maxVisitas = -1;
        for (Map.Entry<String, Integer> e : visits.entrySet()) {
            if (e.getKey().equals(nombreCasilla)) {
                maxVisitas = e.getValue();
            }
        }
        return maxVisitas;
    }

    public String reporteGlobal(ArrayList<Jugador> jugadores, Tablero tablero) {
        // casilla más visitada
        String casillaMasVisitada = null;
        int maxVisitas = -1;
        for (Map.Entry<String, Integer> e : visits.entrySet()) {
            if (e.getValue() > maxVisitas) {
                maxVisitas = e.getValue();
                casillaMasVisitada = e.getKey();
            }
        }

        // casilla más rentable
        String casillaMasRentable = null;
        float maxRent = -1f;
        for (Map.Entry<String, Float> e : alquilerRecibido.entrySet()) {
            if (e.getValue() > maxRent) {
                maxRent = e.getValue();
                casillaMasRentable = e.getKey();
            }
        }

        // grupo más rentable
        String grupoMasRentable = null;
        float maxGrupo = -1f;
        for (Map.Entry<String, Float> e : alquilerPorGrupo.entrySet()) {
            if (e.getValue() > maxGrupo) {
                maxGrupo = e.getValue();
                grupoMasRentable = e.getKey();
            }
        }

        // jugador con mas vueltas
        String jugadorMasVueltas = null;
        int maxVueltas = -1;
        for (Jugador j : jugadores) {
            int v = laps.getOrDefault(j.getNombre(), 0);
            if (v > maxVueltas) {
                maxVueltas = v;
                jugadorMasVueltas = j.getNombre();
            }
        }

        // jugador en cabeza (fortuna total = fortuna líquido + propiedades + edificios)
        String jugadorEnCabeza = getJugadorEnCabeza(jugadores);

        return "{\n" + "  casillaMasRentable: " + casillaMasRentable + ",\n" + "  grupoMasRentable: " + grupoMasRentable + ",\n" + "  casillaMasFrecuentada: " + casillaMasVisitada + ",\n" + "  jugadorMasVueltas: " + jugadorMasVueltas + ",\n" + "  jugadorEnCabeza: " + jugadorEnCabeza + "\n" + "}";
    }
}