package monopoly;

import partida.Jugador;

public class Edificio {
    private static int contadorCasas = 0;
    private static int contadorHoteles = 0;
    private static int contadorPiscinas = 0;
    private static int contadorPistas = 0;
    private final String id;
    private final String tipo; // casa, hotel, piscina, pistaDeDeporte
    private final Jugador propietario;
    private final Casilla casilla;
    private final String grupo;
    private final float coste;

    public Edificio(String tipo, Jugador propietario, Casilla casilla, String grupo, float coste) {
        this.tipo = tipo.toLowerCase();
        this.propietario = propietario;
        this.casilla = casilla;
        this.grupo = grupo;
        this.coste = coste;
        this.id = generarId(tipo);
    }

    private String generarId(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "casa" -> "casa-" + (++contadorCasas);
            case "hotel" -> "hotel-" + (++contadorHoteles);
            case "piscina" -> "piscina-" + (++contadorPiscinas);
            case "pista_deporte" -> "pista_deporte-" + (++contadorPistas);
            default -> "";
        };
    }

    public String toString() {return "{ \n id: " + id + ", \n propietario: " + propietario.getNombre() + ", \n casilla: " + casilla.getNombre() + ", \n grupo: " + grupo + ", \n coste: " + Valor.formatear(coste) + " \n}";}

    public String getId() {return id;}

    public String getTipo() {return tipo;}

    public float getCoste() {return coste;}

    public Casilla getCasilla() {return casilla;}
}