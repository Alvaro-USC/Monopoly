package monopoly;

import partida.Jugador;

public class Edificio {
    private String id;
    private String tipo; // casa, hotel, piscina, pistaDeDeporte
    private Jugador propietario;
    private Solar casilla;
    private String grupo;
    private float coste;

    public Edificio(String tipo, Jugador propietario, Solar casilla, String grupo, float coste) {
        this.tipo = tipo.toLowerCase();
        this.propietario = propietario;
        this.casilla = casilla;
        this.grupo = grupo;
        this.coste = coste;
        this.id = generarId(tipo);
    }

    private String generarId(String tipo) {
        switch (tipo.toLowerCase()) {
            case "casa": return "casa-" + casilla.getCantidadEdificioTipo("casa") + 1;
            case "hotel": return "hotel-" + casilla.getCantidadEdificioTipo("hotel") + 1;
            case "piscina": return "piscina-" + casilla.getCantidadEdificioTipo("casa") + 1;
            case "pista_deporte": return "pista_deporte-" + casilla.getCantidadEdificioTipo("casa") + 1;
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