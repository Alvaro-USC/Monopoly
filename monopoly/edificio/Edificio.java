package monopoly.edificio;

import monopoly.Valor;
import monopoly.casilla.propiedad.Solar;
import partida.Jugador;

public abstract class Edificio {
    private final String id;
    private final String tipo; // casa, hotel, piscina, pistaDeDeporte
    private Jugador propietario;
    private final Solar solar;
    private final String grupo;
    private final float coste;

    public Edificio(String tipo, Solar solar, String grupo, float coste) {
        this.tipo = tipo.toLowerCase();
        this.propietario = solar.getDuenho();
        this.solar = solar;
        this.grupo = grupo;
        this.coste = coste;
        this.id = generarId(tipo);
    }

    protected abstract String generarId(String tipo);

    public String describirEdificio() {
        return "{\n" + " id: " + id + "\n proprietario: " + propietario.getNombre() + "\n casilla: " + solar.getNombre() + "\n grupo: " + grupo + "\n coste: " + coste + "\n}";
    }

    public String toString() {return "{ \n id: " + id + ", \n propietario: " + propietario.getNombre() + ", \n casilla: " + solar.getNombre() + ", \n grupo: " + grupo + ", \n coste: " + Valor.formatear(coste) + " \n}";}

    public String getId() {return id;}

    public String getTipo() {return tipo;}

    public float getCoste() {return coste;}

    public Solar getSolar() {return solar;}

    public void setPropietario(Jugador propietario) {this.propietario = propietario;}
}