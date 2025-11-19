package monopoly.trato;

import monopoly.Valor;
import monopoly.casilla.propiedad.Solar;
import partida.Jugador;

public class Trato {
    private static int contador = 0;
    private final String id;
    private final Jugador proponente;
    private final Jugador receptor;

    // Lo que ofrece el proponente
    private final Solar propiedadOferta;
    private final float dineroOferta;

    // Lo que pide el proponente
    private final Solar propiedadDemanda;
    private final float dineroDemanda;

    public Trato(Jugador proponente, Jugador receptor, Solar propOferta, float dineroOferta, Solar propDemanda, float dineroDemanda) {
        this.id = "trato" + (++contador);
        this.proponente = proponente;
        this.receptor = receptor;
        this.propiedadOferta = propOferta;
        this.dineroOferta = dineroOferta;
        this.propiedadDemanda = propDemanda;
        this.dineroDemanda = dineroDemanda;
    }

    public String getId() {return id;}

    public Jugador getProponente() {return proponente;}

    public Jugador getReceptor() {return receptor;}

    public Solar getPropiedadOferta() {return propiedadOferta;}

    public float getDineroOferta() {return dineroOferta;}

    public Solar getPropiedadDemanda() {return propiedadDemanda;}

    public float getDineroDemanda() {return dineroDemanda;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n").append("  jugadorPropone: ").append(proponente.getNombre()).append(",\n").append("  trato: cambiar (");

        // Lado izquierdo (Oferta)
        if (propiedadOferta != null) sb.append(propiedadOferta.getNombre());
        if (propiedadOferta != null && dineroOferta > 0) sb.append(" y ");
        if (dineroOferta > 0)
            sb.append(Valor.formatear(dineroOferta)); // Asumiendo clase Valor auxiliar o poner número directo

        sb.append(", ");

        // Lado derecho (Demanda)
        if (propiedadDemanda != null) sb.append(propiedadDemanda.getNombre());
        if (propiedadDemanda != null && dineroDemanda > 0) sb.append(" y ");
        if (dineroDemanda > 0) sb.append(Valor.formatear(dineroDemanda));

        sb.append(")\n}");
        return sb.toString();
    }

    public String getDescripcion() {
        String oferta = (propiedadOferta != null ? propiedadOferta.getNombre() : "") + (propiedadOferta != null && dineroOferta > 0 ? " y " : "") + (dineroOferta > 0 ? String.format("%.0f€", dineroOferta) : "");

        String demanda = (propiedadDemanda != null ? propiedadDemanda.getNombre() : "") + (propiedadDemanda != null && dineroDemanda > 0 ? " y " : "") + (dineroDemanda > 0 ? String.format("%.0f€", dineroDemanda) : "");

        return "cambiar (" + oferta + ", " + demanda + ")";
    }
}