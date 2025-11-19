package monopoly.edificio;

import monopoly.Valor;
import monopoly.casilla.propiedad.Solar;
import partida.Jugador;

public abstract class Edificio {
    private final String id;
    private final String tipo; // casa, hotel, piscina, pistaDeDeporte
    private final Jugador propietario;
    private final Solar solar;
    private final String grupo;
    private final float coste;

    public Edificio(String tipo, Jugador propietario, Solar solar, String grupo, float coste) {
        this.tipo = tipo.toLowerCase();
        this.propietario = propietario;
        this.solar = solar;
        this.grupo = grupo;
        this.coste = coste;
        this.id = generarId(tipo);
    }

    private String generarId(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "casa" -> "casa-" + (solar.getCantidadEdificioTipo("casa") + 1);
            case "hotel" -> "hotel-" + (solar.getCantidadEdificioTipo("hotel") + 1);
            case "piscina" -> "piscina-" + (solar.getCantidadEdificioTipo("piscina") + 1);
            case "pista_deporte" -> "pista_deporte-" + (solar.getCantidadEdificioTipo("pista_deporte") + 1);
            default -> "";
        };
    }

    public String describirEdificio() {

        return "{\n" + " id: " + id + "\n proprietario: " + propietario.getNombre() + "\n casilla: " + solar.getNombre() + "\n grupo: " + grupo + "\n coste: " + coste + "\n}";
    }

    public String toString() {return "{ \n id: " + id + ", \n propietario: " + propietario.getNombre() + ", \n casilla: " + solar.getNombre() + ", \n grupo: " + grupo + ", \n coste: " + Valor.formatear(coste) + " \n}";}

    public String getId() {return id;}

    public String getTipo() {return tipo;}

    public float getCoste() {return coste;}

    public Solar getSolar() {return solar;}
}