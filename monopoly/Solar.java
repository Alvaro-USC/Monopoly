package monopoly;

import partida.*;
import java.util.ArrayList;

public class Solar extends Casilla {

    private ArrayList<Edificio> edificios = new ArrayList<>();
    private boolean hipotecada = false;
    int idSolar;

    public Solar(String nombre, int posicion, float valor, Jugador duenho) {
        super(nombre, "Solar", posicion, valor, duenho);
        String numStr = nombre.replaceAll("\\D+", "");
        this.idSolar = Integer.parseInt(numStr) - 1;
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        boolean solv = true;
        if (!getDuenho().equals(banca) && !getDuenho().equals(actual)) {
            float toPay = getImpuesto();
            if (getGrupo() != null && getGrupo().esDuenhoGrupo(this.getDuenho())) {
                toPay *= 2;
            }
            if (actual.getFortuna() < toPay) {
                solv = false;
                System.out.println("No tienes suficiente dinero. Debes hipotecar una propiedad o declararte en bancarrota.");
            } else {
                actual.sumarFortuna(-toPay);
                getDuenho().sumarFortuna(toPay);
                System.out.println("Se han pagado " + Valor.formatear(toPay) + " € de alquiler.");
                StatsTracker.getInstance().registrarPagoAlquiler(actual, toPay);
                StatsTracker.getInstance().registrarCobroAlquiler(getDuenho(), toPay);
                StatsTracker.getInstance().registrarAlquiler(this, toPay);
            }
        }
        return solv;
    }

    @Override
    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        if (getDuenho().equals(banca)) {
            if (solicitante.getFortuna() >= getValor()) {
                solicitante.sumarFortuna(-getValor());
                banca.sumarFortuna(getValor());
                setDuenho(solicitante);
                solicitante.anhadirPropiedad(this);
                System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + getNombre() + " por " + Valor.formatear(getValor()) + "€. Su fortuna actual es " + Valor.formatear(solicitante.getFortuna()) + "€.");
                StatsTracker.getInstance().asegurarJugador(solicitante);
                StatsTracker.getInstance().byPlayer.get(solicitante.getNombre()).addDineroInvertido(getValor());
            } else {
                System.out.println("No tienes suficiente dinero para comprar esta casilla.");
            }
        } else {
            System.out.println("Esta casilla no se puede comprar.");
        }
    }

    @Override
    public String infoCasilla() {
        String info = "{ \n tipo: " + getTipo();
        info += ", \n grupo: " + (getGrupo() != null ? getGrupo().getColorGrupo() : "") +
                ", \n propietario: " + getDuenho().getNombre() +
                ", \n valor: " + Valor.formatear(getValor()) +
                ", \n alquiler: " + Valor.formatear(getImpuesto());

        if (hipotecada) {
            info += ", \n hipotecada: sí";
        } else {
            info += ", \n hipotecada: no";
        }

        if (!edificios.isEmpty()) {
            info += ", \n edificios: [";
            for (Edificio e : edificios) {
                info += e.getId() + ", ";
            }
            if (info.endsWith(", ")) info = info.substring(0, info.length() - 2);
            info += "]";

            // Map para acumular valores y alquileres por tipo
            long valorCasa = 0, valorHotel = 0, valorPiscina = 0, valorPista = 0;
            long alquilerCasa = 0, alquilerHotel = 0, alquilerPiscina = 0, alquilerPista = 0;

            for (Edificio e : edificios) {
                switch (e.getTipo().toLowerCase()) {
                    case "casa":
                        valorCasa += Valor.PRECIO_EDIFICIOS[idSolar][0];
                        alquilerCasa += Valor.ALQUILER_EDIFICIOS[idSolar][0];
                        break;
                    case "hotel":
                        valorHotel += Valor.PRECIO_EDIFICIOS[idSolar][1];
                        alquilerHotel += Valor.ALQUILER_EDIFICIOS[idSolar][1];
                        break;
                    case "piscina":
                        valorPiscina += Valor.PRECIO_EDIFICIOS[idSolar][2];
                        alquilerPiscina += Valor.ALQUILER_EDIFICIOS[idSolar][2];
                        break;
                    case "pista_deporte":
                        valorPista += Valor.PRECIO_EDIFICIOS[idSolar][3];
                        alquilerPista += Valor.ALQUILER_EDIFICIOS[idSolar][3];
                        break;
                }
            }
            StringBuilder info2 = new StringBuilder("");
            info2.append(", \n valor casa: ").append(Valor.formatear(valorCasa))
                    .append(", \n valor hotel: ").append(Valor.formatear(valorHotel))
                    .append(", \n valor piscina: ").append(Valor.formatear(valorPiscina))
                    .append(", \n valor pista de deporte: ").append(Valor.formatear(valorPista))
                    .append(", \n alquiler casa: ").append(Valor.formatear(alquilerCasa))
                    .append(", \n alquiler hotel: ").append(Valor.formatear(alquilerHotel))
                    .append(", \n alquiler piscina: ").append(Valor.formatear(alquilerPiscina))
                    .append(", \n alquiler pista de deporte: ").append(Valor.formatear(alquilerPista));
            info += info2.toString();
        }

        info += "\n}";
        return info;
    }


    @Override
    public String casEnVenta() {
        String g = (getGrupo() != null ? " grupo: " + getGrupo().getColorGrupo() + "," : "");
        return "{\n nombre: " + this.getNombre() +  "\n tipo: " + getTipo() + "," + g + " \n valor: " + getValor() + "\n}";
    }

    public boolean isHipotecada() { return hipotecada; }
    public void setHipotecada(boolean h) { this.hipotecada = h; }

    public ArrayList<Edificio> getEdificios() { return edificios; }

    public void addEdificio(Edificio e) {
        edificios.add(e);
    }

    public void eliminarEdificios(String tipo, int cantidad) {
        int count = 0;
        for (int i = edificios.size() - 1; i >= 0 && count < cantidad; i--) {
            if (edificios.get(i).getTipo().equalsIgnoreCase(tipo)) {
                edificios.remove(i);
                count++;
            }
        }
    }

    public float calcularValorHipoteca() {
        return getValor() / 2;
    }

    public boolean puedeEdificar() {
        return !hipotecada && getDuenho() != null;
    }

    public int contarEdificiosTipo(String tipo) {
        int c = 0;
        for (Edificio e : edificios)
            if (e.getTipo().equalsIgnoreCase(tipo)) c++;
        return c;
    }

    public int getIdSolar()
    {
        return idSolar;
    }
}