package monopoly.casilla.propiedad;

import monopoly.edificio.*;
import monopoly.StatsTracker;
import monopoly.Valor;
import monopoly.casilla.Casilla;
import monopoly.casilla.Propiedad;
import monopoly.excepcion.*;
import partida.Jugador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static monopoly.Juego.consola;

public final class Solar extends Propiedad {

    private final ArrayList<Edificio> edificios = new ArrayList<>();
    private boolean hipotecada;
    private final float hipoteca = getValor() / 2;
    int idSolar;

    public Solar(String nombre, int posicion, float valor, Jugador duenho, float impuesto) {
        super(nombre, "Solar", posicion, valor, duenho);
        String numStr = nombre.replaceAll("\\D+", "");
        this.hipotecada = false;
        setImpuesto(impuesto);
        this.idSolar = Integer.parseInt(numStr) - 1;
    }

    public float calcularAlquiler() {
        float toPay = calcularSumaTotalAlquileres(); // Tu método existente
        if (getGrupo() != null && getGrupo().esDuenhoGrupo(this.getDuenho())) {
            toPay *= 2;
        }
        return toPay;
    }

    @Override
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {

        if (getDuenho().equals(banca) || getDuenho().equals(actual)) {
            return true; // No se paga, es solvente
        }

        float toPay = this.calcularAlquiler();
        try {
            return this.procesarPago(actual, toPay);
        } catch (FondosInsuficientesException e) {
            return false;
        }
    }

    public String infoCasilla() {
        StringBuilder info = new StringBuilder("{ \n tipo: " + getTipo());
        info.append(", \n grupo: ").append(getGrupo() != null ? getGrupo().getColorGrupo() : "")
                .append(", \n propietario: ").append(getDuenho().getNombre()).append(", \n valor: ")
                .append(Valor.formatear(getValor())).append(", \n alquiler: ").append(Valor.formatear(getImpuesto()));

        if (hipotecada) {
            info.append(", \n hipotecada: sí");
        } else {
            info.append(", \n hipotecada: no");
        }

        if (!edificios.isEmpty()) {
            info.append(", \n edificios: [");
            for (Edificio e : edificios) {
                info.append(e.getId()).append(", ");
            }
            if (info.toString().endsWith(", ")) info = new StringBuilder(info.substring(0, info.length() - 2));
            info.append("]");

            // Map para acumular valores y alquileres por tipo
            float valorCasa = 0, valorHotel = 0, valorPiscina = 0, valorPista = 0;
            float alquilerCasa = 0, alquilerHotel = 0, alquilerPiscina = 0, alquilerPista = 0;

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
            info.append(", \n valor casa: ").append(Valor.formatear(valorCasa)).append(", \n valor hotel: ")
                    .append(Valor.formatear(valorHotel)).append(", \n valor piscina: ").append(Valor.formatear(valorPiscina))
                    .append(", \n valor pista de deporte: ").append(Valor.formatear(valorPista)).append(", \n alquiler casa: ")
                    .append(Valor.formatear(alquilerCasa)).append(", \n alquiler hotel: ").append(Valor.formatear(alquilerHotel))
                    .append(", \n alquiler piscina: ").append(Valor.formatear(alquilerPiscina))
                    .append(", \n alquiler pista de deporte: ").append(Valor.formatear(alquilerPista));
        }

        info.append("\n}");
        return info.toString();
    }

    public String casEnVenta() {
        String g = (getGrupo() != null ? " grupo: " + getGrupo().getColorGrupo() + "," : "");
        return "{\n nombre: " + this.getNombre() + "\n tipo: " + getTipo() + "," + g + " \n valor: " + getValor() + "\n}";
    }

    public String casEnVenta(String grupoBuscado) {
        String g = "";
        String colorGr = (this.getGrupo() != null ? this.getGrupo().getColorGrupo() : "");

        if (colorGr.equals(grupoBuscado)) {
            g += (this.getGrupo() != null ? " \nthis.getGrupo(): " + colorGr : "");
            return "{ \n nombre: " + this.getNombre() + "\n this.getTipo(): " + this.getTipo() + "," + g + " \n valor: " + Valor.formatear(this.getValor()) + "\n}";
        }
        else {
            return "";
        }
    }

    public void edificar(String tipo) throws PropiedadNoPerteneceException, PropiedadYaHipotecadaException, EdificacionIlegalException, AccionInvalidaException {
        // Validar Propiedad
        Jugador jugador = this.getDuenho();

        // Validar Hipoteca (ni esta ni ninguna del grupo)
        if (this.estaHipotecada()) {
            throw new PropiedadYaHipotecadaException();
        }
        if (this.getGrupo() != null) {
            for (Casilla c : this.getGrupo().getMiembros()) {
                if (c instanceof Solar s && s.estaHipotecada()) {
                    throw new EdificacionIlegalException();
                }
            }
        } else {
            throw new EdificacionIlegalException("Error: Este solar no pertenece a ningún grupo.");
        }

        // Contar edificios existentes
        int casas = this.getCantidadEdificioTipo("casa");
        int hoteles = this.getCantidadEdificioTipo("hotel");
        int piscinas = this.getCantidadEdificioTipo("piscina");
        int pistas = this.getCantidadEdificioTipo("pista_deporte");

        // Determinar Coste
        float coste = getCoste(jugador, tipo);

        // Validar Reglas de Construcción
        switch (tipo.toLowerCase()) {
            case "casa":
                if (casas >= 4)
                    throw new AccionInvalidaException("No se puede edificar más de 4 casas en " + this.getNombre());
                if (hoteles >= 1)
                    throw new AccionInvalidaException("No se puede edificar casas en " + this.getNombre() + " puesto a que ya hay un hotel");
                break;
            case "hotel":
                if (casas < 4)
                    throw new AccionInvalidaException("No se puede edificar un hotel en " + this.getNombre() + " sin 4 casas previas");
                if (hoteles >= 1)
                    throw new AccionInvalidaException("Ya hay un hotel construido en " + this.getNombre());
                edificios.clear();
                break;
            case "piscina":
                if (hoteles < 1)
                    throw new AccionInvalidaException("No se puede edificar una piscina en " + this.getNombre() + " sin un hotel");
                if (piscinas >= 1)
                    throw new AccionInvalidaException("Ya hay una pisicina construida en " + this.getNombre());
                break;
            case "pista_deporte":
                if (hoteles < 1)
                    throw new AccionInvalidaException("No se puede edificar una pista de deporte en " + this.getNombre() + " sin un hotel");
                if (piscinas < 1)
                    throw new AccionInvalidaException("No se puede edificar una pista de deporte en " + this.getNombre() + " sin una piscina");
                if (pistas >= 1)
                    throw new AccionInvalidaException("Ya hay una pista de deporte construida en " + this.getNombre());
                break;
        }

        // Ejecutar Construcción
        Edificio nuevo = crearEdificio(tipo, coste);
        this.addEdificio(nuevo);
        jugador.sumarGastos(coste);

        // Actualizar Stats (Singleton)
        StatsTracker.getInstance().asegurarJugador(jugador);
        StatsTracker.getInstance().byPlayer.get(jugador.getNombre()).addDineroInvertido(coste);

        consola.imprimir("Se ha edificado un " + tipo + " en " + this.getNombre() + ". La fortuna de " + jugador.getNombre() + " se reduce en " + Valor.formatear(coste) + "€.");
    }

    private float getCoste(Jugador jugador, String tipo) throws EdificacionIlegalException, FondosInsuficientesException {
        float coste = switch (tipo.toLowerCase()) {
            case "casa" -> Valor.PRECIO_EDIFICIOS[this.idSolar][0];
            case "hotel" -> Valor.PRECIO_EDIFICIOS[this.idSolar][1];
            case "piscina" -> Valor.PRECIO_EDIFICIOS[this.idSolar][2];
            case "pista_deporte" -> Valor.PRECIO_EDIFICIOS[this.idSolar][3];
            default ->
                    throw new EdificacionIlegalException("Tipo de edificio no válido. Use casa, hotel, piscina o pista_deporte.");
        };

        // Validar Fortuna
        if (jugador.getFortuna() < coste) {
            throw new FondosInsuficientesException(tipo);
        }
        return coste;
    }

    private Edificio crearEdificio(String tipo, float coste) {
        return switch (tipo) {
            case "casa" -> new Casa(this, this.getGrupo().getColorGrupo(), coste);
            case "hotel" -> new Hotel(this, this.getGrupo().getColorGrupo(), coste);
            case "piscina" -> new Piscina(this, this.getGrupo().getColorGrupo(), coste);
            default -> new PistaDeporte(this, this.getGrupo().getColorGrupo(), coste);
        };
    }

    public boolean estaHipotecada() {
        return hipotecada;
    }

    public void hipotecar(Jugador jugador) throws PropiedadYaHipotecadaException, PropiedadNoPerteneceException {
        if (!this.getDuenho().equals(jugador)) {
            throw new PropiedadNoPerteneceException(this.getNombre());
        }

        if (this.estaHipotecada()) {
            throw new PropiedadYaHipotecadaException();
        }

        if (!this.getEdificios().isEmpty()) {
            throw new PropiedadYaHipotecadaException();
        }

        float cantidad = this.hipoteca;
        this.setHipotecada(true);
        jugador.sumarFortuna(cantidad);

        consola.imprimir("Se hipoteca " + this.getNombre() + " por " + Valor.formatear(cantidad) + "€.");
    }

    public void deshipotecar(Jugador jugador) throws FondosInsuficientesException, PropiedadNoPerteneceException, PropiedadNoHipotecadaException {
        // El propietario (jugador) se infiere de this.getDuenho()
        if (!this.getDuenho().equals(jugador)) {
            throw new PropiedadNoPerteneceException(this.getNombre());
        }

        if (!this.estaHipotecada()) {
            throw new PropiedadNoHipotecadaException();
        }

        // El coste de deshipotecar es el valor de la hipoteca
        float cantidad = this.hipoteca;

        if (jugador.getFortuna() < cantidad) {
            throw new FondosInsuficientesException(cantidad);
        }

        jugador.sumarGastos(cantidad);
        this.setHipotecada(false);
        consola.imprimir("Se deshipoteca " + this.getNombre() + " pagando " + Valor.formatear(cantidad) + "€.");
    }

    public String venderEdificios(Jugador jugador, String tipo, int cantidad) throws PropiedadNoPerteneceException, AccionInvalidaException {
        if (!this.getDuenho().equals(jugador)) {
            throw new PropiedadNoPerteneceException(this.getNombre());
        }

        int disponibles = this.getCantidadEdificioTipo(tipo.toLowerCase());

        if (disponibles == 0) {
            throw new AccionInvalidaException("No hay edificios de tipo " + tipo + " en " + this.getNombre());
        }

        int vender = Math.min(disponibles, cantidad);

        // Calcular precio de venta
        float totalGanado = getTotalGanado(tipo, vender);

        this.eliminarEdificios(tipo, vender);
        jugador.sumarFortuna(totalGanado);

        // Actualizar Stats
        StatsTracker.getInstance().asegurarJugador(jugador);
        StatsTracker.getInstance().byPlayer.get(jugador.getNombre()).addDineroInvertido(-totalGanado);

        return "Vendidas " + vender + " " + tipo + "(s) en " + this.getNombre() + " por " + Valor.formatear(totalGanado) + "€.";
    }

    private float getTotalGanado(String tipo, int vender) throws AccionInvalidaException {
        float precioVentaUnitario;
        int col = switch (tipo.toLowerCase()) {
            case "casa" -> 0;
            case "hotel" -> 1;
            case "piscina" -> 2;
            case "pista_deporte" -> 3;
            default -> -1;
        };

        if (col == -1) {
            throw new AccionInvalidaException("Tipo de edificio no válido: " + tipo);
        }

        precioVentaUnitario = Valor.PRECIO_EDIFICIOS[this.idSolar][col];

        return vender * precioVentaUnitario;
    }

    public Map<String, Integer> getSumaAlquileresPorTipo() {

        Map<String, Integer> alquileresPorTipo = new HashMap<>();

        alquileresPorTipo.put("casa", 0);
        alquileresPorTipo.put("hotel", 0);
        alquileresPorTipo.put("piscina", 0);
        alquileresPorTipo.put("pista_deporte", 0);

        for (Edificio e : edificios) {
            String tipo = e.getTipo().toLowerCase();
            long alquilerUnitario;
            int indiceTipo; // Usado para acceder al array ALQUILER_EDIFICIOS

            switch (tipo) {
                case "casa":
                    indiceTipo = 0;
                    break;
                case "hotel":
                    indiceTipo = 1;
                    break;
                case "piscina":
                    indiceTipo = 2;
                    break;
                case "pista_deporte":
                    indiceTipo = 3;
                    break;
                default:
                    continue;
            }
            alquilerUnitario = (long) Valor.ALQUILER_EDIFICIOS[idSolar][indiceTipo];

            alquileresPorTipo.put(tipo, (int) (alquileresPorTipo.get(tipo) + alquilerUnitario));
        }
        return alquileresPorTipo;
    }

    public int calcularSumaTotalAlquileres() {
        Map<String, Integer> alquileresPorTipo = getSumaAlquileresPorTipo();

        return (int) (alquileresPorTipo.values().stream().mapToInt(Integer::intValue).sum() + this.getImpuesto());
    }

    public String getResumenEdificios() {
        StringBuilder descripcion = new StringBuilder();
        for (Edificio e : edificios) {
            descripcion.append(e.describirEdificio()).append(",\n");
        }

        return descripcion.toString();
    }

    public int getCantidadEdificioTipo(String tipo) {
        int contador = 0;
        for (Edificio edificio : edificios) {
            if (edificio.getTipo().equals(tipo)) {
                contador++;
            }
        }
        return contador;
    }

    public String getEdificiosFaltantesDescripcion() {

        // Contar edificios construidos
        Map<String, Integer> contadorEdificios = new HashMap<>();
        contadorEdificios.put("casa", this.getCantidadEdificioTipo("casa"));
        contadorEdificios.put("hotel", this.getCantidadEdificioTipo("hotel"));
        contadorEdificios.put("piscina", this.getCantidadEdificioTipo("piscina"));
        contadorEdificios.put("pista_deporte", this.getCantidadEdificioTipo("pista_deporte"));

        // Calcular cuántos faltan ( 4 casas max, 1 hotel/piscina/pista max)
        int casasFaltan = 4 - contadorEdificios.get("casa");
        int hotelesFaltan = 1 - contadorEdificios.get("hotel");
        int piscinasFaltan = 1 - contadorEdificios.get("piscina");
        int pistasFaltan = 1 - contadorEdificios.get("pista_deporte");

        StringBuilder faltan = new StringBuilder();

        if (casasFaltan > 0) faltan.append(casasFaltan).append(" casa(s), ");
        if (hotelesFaltan > 0) faltan.append(hotelesFaltan).append(" hotel(es), ");
        if (piscinasFaltan > 0) faltan.append(piscinasFaltan).append(" piscina(s), ");
        if (pistasFaltan > 0) faltan.append(pistasFaltan).append(" pista(s) de deporte, ");

        if (!faltan.isEmpty()) {
            faltan.setLength(faltan.length() - 2);
            return "Aún se pueden construir " + faltan;
        } else {
            return "";
        }
    }

    public void setHipotecada(boolean h) {this.hipotecada = h;}

    public ArrayList<Edificio> getEdificios() {return edificios;}

    public void addEdificio(Edificio e) {edificios.add(e);}

    public void eliminarEdificios(String tipo, int cantidad) {
        int count = 0;
        for (int i = edificios.size() - 1; i >= 0 && count < cantidad; i--) {
            if (edificios.get(i).getTipo().equalsIgnoreCase(tipo)) {
                edificios.remove(i);
                count++;
            }
        }
    }

    public int contarEdificiosTipo(String tipo) {
        int c = 0;
        for (Edificio e : edificios)
            if (e.getTipo().equalsIgnoreCase(tipo)) c++;
        return c;
    }

    public int getIdSolar() {return idSolar;}

    @Override
    public boolean alquiler() {
        return getImpuesto() > 0;
    }

    @Override
    public float valor() {
        return getValor();
    }

    public void setDuenho(Jugador duenho) {
        this.duenho = duenho;
        for (Edificio e : edificios) {
            e.setPropietario(duenho);
        }
    }


}