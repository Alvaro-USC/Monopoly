package monopoly;

import partida.Jugador;

public class Edificio {
    private String id;
    private String tipo; // casa, hotel, piscina, pistaDeDeporte
    private Jugador propietario;
    private Casilla casilla;
    private String grupo;
    private float coste;

    private static int contadorCasas = 0;
    private static int contadorHoteles = 0;
    private static int contadorPiscinas = 0;
    private static int contadorPistas = 0;

    public Edificio(String tipo, Jugador propietario, Casilla casilla, String grupo, float coste) {
        this.tipo = tipo.toLowerCase();
        this.propietario = propietario;
        this.casilla = casilla;
        this.grupo = grupo;
        this.coste = coste;
        this.id = generarId(tipo);
    }

    private String generarId(String tipo) {
        switch (tipo.toLowerCase()) {
            case "casa": return "casa-" + (++contadorCasas);
            case "hotel": return "hotel-" + (++contadorHoteles);
            case "piscina": return "piscina-" + (++contadorPiscinas);
            case "pista_deporte": return "pista_deporte-" + (++contadorPistas);
        }
        return "";
    }

    public String toString() {
        return "{ \n id: " + id + ", \n propietario: " + propietario.getNombre() + ", \n casilla: " + casilla.getNombre() +
                ", \n grupo: " + grupo + ", \n coste: " + Valor.formatear(coste) + " \n}";
    }

    public String getId() { return id; }
    public String getTipo() { return tipo; }
    public float getCoste() { return coste; }
    public Casilla getCasilla() { return casilla; }
}