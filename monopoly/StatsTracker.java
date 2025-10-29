package monopoly;

import partida.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * Singleton responsable de recoger estadísticas globales y por casilla/grupo.
 */
public class StatsTracker {

    private static StatsTracker instance = null;

    // visitas por casilla (clave: nombre casilla)
    private final Map<String, Integer> visits = new HashMap<>();
    // alquiler recolectado por casilla
    private final Map<String, Float> rentCollected = new HashMap<>();
    // alquiler recolectado por grupo (color)
    private final Map<String, Float> rentByGroup = new HashMap<>();
    // vueltas por jugador
    private final Map<String, Integer> laps = new HashMap<>();
    private ArrayList<Jugador> jugadores = new ArrayList<>();

    // estadísticas por jugador (referencia por nombre)
    public final Map<String, PlayerStats> byPlayer = new HashMap<>();

    private StatsTracker() {
    }

    public static StatsTracker getInstance() {
        if (instance == null) instance = new StatsTracker();
        return instance;
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
        rentCollected.put(c.getNombre(), rentCollected.getOrDefault(c.getNombre(), 0f) + amount);
        if (c.getGrupo() != null) {
            String color = c.getGrupo().getColorGrupo();
            rentByGroup.put(color, rentByGroup.getOrDefault(color, 0f) + amount);
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

    public void registrarEncarcelamiento(Jugador jugador) {
        asegurarJugador(jugador);
        byPlayer.get(jugador.getNombre()).addVezEnCarcel();
    }

    // Reportes

    public String reporteJugador(String nombre, ArrayList<Jugador> jugadores) {
        for (Jugador j : jugadores) {
            asegurarJugador(j);
        }
        if (!byPlayer.containsKey(nombre)) return "No hay estadísticas para " + nombre;
        PlayerStats s = byPlayer.get(nombre);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  dineroInvertido: ").append(Valor.formatear(s.getDineroInvertido())).append(",\n");
        sb.append("  pagoTasasEImpuestos: ").append(Valor.formatear(s.getPagoTasasEImpuestos())).append(",\n");
        sb.append("  pagoDeAlquileres: ").append(Valor.formatear(s.getPagoDeAlquileres())).append(",\n");
        sb.append("  cobroDeAlquileres: ").append(Valor.formatear(s.getCobroDeAlquileres())).append(",\n");
        sb.append("  pasarPorSalida: ").append(Valor.formatear(s.getPasarPorSalida())).append(",\n");
        sb.append("  premiosBote: ").append(Valor.formatear(s.getPremiosBote())).append(",\n");
        sb.append("  vecesEnLaCarcel: ").append(Valor.formatear(s.getVecesEnLaCarcel())).append("\n");
        sb.append("}");
        return sb.toString();
    }

    public String reporteGlobal(ArrayList<Jugador> jugadores, Tablero tablero) {
        // casilla mas visitada
        String casillaMasVisitada = null;
        int maxVisitas = -1;
        for (Map.Entry<String,Integer> e : visits.entrySet()) {
            if (e.getValue() > maxVisitas) {
                maxVisitas = e.getValue();
                casillaMasVisitada = e.getKey();
            }
        }

        // casilla mas rentable
        String casillaMasRentable = null;
        float maxRent = -1f;
        for (Map.Entry<String,Float> e : rentCollected.entrySet()) {
            if (e.getValue() > maxRent) {
                maxRent = e.getValue();
                casillaMasRentable = e.getKey();
            }
        }

        // grupo mas rentable
        String grupoMasRentable = null;
        float maxGrupo = -1f;
        for (Map.Entry<String,Float> e : rentByGroup.entrySet()) {
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
        String jugadorEnCabeza = null;
        float maxFortuna = Float.NEGATIVE_INFINITY;
        for (Jugador j : jugadores) {
            float total = j.getFortuna();
            // sumar valor de propiedades y edificios (usamos getValor() de Casilla y coste de Edificio aproximado)
            for (Casilla c : j.getPropiedades()) {
                total += c.getValor();
                if (c instanceof Solar) {
                    Solar s = (Solar) c;
                    for (Edificio ed : s.getEdificios()) total += ed.getCoste();
                }
            }
            if (total > maxFortuna) {
                maxFortuna = total;
                jugadorEnCabeza = j.getNombre();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  casillaMasRentable: ").append(casillaMasRentable).append(",\n");
        sb.append("  grupoMasRentable: ").append(grupoMasRentable).append(",\n");
        sb.append("  casillaMasFrecuentada: ").append(casillaMasVisitada).append(",\n");
        sb.append("  jugadorMasVueltas: ").append(jugadorMasVueltas).append(",\n");
        sb.append("  jugadorEnCabeza: ").append(jugadorEnCabeza).append("\n");
        sb.append("}");
        return sb.toString();
    }
}