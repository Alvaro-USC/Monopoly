package monopoly;

import partida.Jugador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Solar extends PropiedadComprable {

    private final ArrayList<Edificio> edificios = new ArrayList<>();
    int idSolar;
    private boolean hipotecada = false;

    public Solar(String nombre, int posicion, float valor, Jugador duenho, float hipoteca, float impuesto) {
        super(nombre, "Solar", posicion, valor, duenho);
        String numStr = nombre.replaceAll("\\D+", "");
        setHipoteca(hipoteca);
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
        return this.procesarPago(actual, toPay);
    }

    @Override
    public String infoCasilla() {
        StringBuilder info = new StringBuilder("{ \n tipo: " + getTipo());
        info.append(", \n grupo: ").append(getGrupo() != null ? getGrupo().getColorGrupo() : "").append(", \n propietario: ").append(getDuenho().getNombre()).append(", \n valor: ").append(Valor.formatear(getValor())).append(", \n alquiler: ").append(Valor.formatear(getImpuesto()));

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
            info.append(", \n valor casa: ").append(Valor.formatear(valorCasa)).append(", \n valor hotel: ").append(Valor.formatear(valorHotel)).append(", \n valor piscina: ").append(Valor.formatear(valorPiscina)).append(", \n valor pista de deporte: ").append(Valor.formatear(valorPista)).append(", \n alquiler casa: ").append(Valor.formatear(alquilerCasa)).append(", \n alquiler hotel: ").append(Valor.formatear(alquilerHotel)).append(", \n alquiler piscina: ").append(Valor.formatear(alquilerPiscina)).append(", \n alquiler pista de deporte: ").append(Valor.formatear(alquilerPista));
        }

        info.append("\n}");
        return info.toString();
    }

    @Override
    public String casEnVenta() {
        String g = (getGrupo() != null ? " grupo: " + getGrupo().getColorGrupo() + "," : "");
        return "{\n nombre: " + this.getNombre() + "\n tipo: " + getTipo() + "," + g + " \n valor: " + getValor() + "\n}";
    }

    public String construirEdificio(Jugador jugador, String tipo) {
        // Validar Propiedad
        if (!this.getDuenho().equals(jugador)) {
            return "No eres el propietario de " + this.getNombre();
        }

        // Validar Hipoteca (ni esta ni ninguna del grupo)
        if (this.isHipotecada()) {
            return "No se puede edificar en una propiedad hipotecada.";
        }
        if (this.getGrupo() != null) {
            for (Casilla c : this.getGrupo().getMiembros()) {
                if (c instanceof Solar s && s.isHipotecada()) {
                    return "No se puede edificar en este grupo mientras haya propiedades hipotecadas.";
                }
            }
        } else {
            return "Error: Este solar no pertenece a ningún grupo.";
        }

        // Contar edificios existentes
        int casas = this.getCantidadEdificioTipo("casa");
        int hoteles = this.getCantidadEdificioTipo("hotel");
        int piscinas = this.getCantidadEdificioTipo("piscina");
        int pistas = this.getCantidadEdificioTipo("pista_deporte");

        // Determinar Coste
        float coste;
        switch (tipo.toLowerCase()) {
            case "casa":
                coste = Valor.PRECIO_EDIFICIOS[this.idSolar][0];
                break;
            case "hotel":
                coste = Valor.PRECIO_EDIFICIOS[this.idSolar][1];
                break;
            case "piscina":
                coste = Valor.PRECIO_EDIFICIOS[this.idSolar][2];
                break;
            case "pista_deporte":
                coste = Valor.PRECIO_EDIFICIOS[this.idSolar][3];
                break;
            default:
                return "Tipo de edificio no válido. Use casa, hotel, piscina o pista_deporte.";
        }

        // Validar Fortuna
        if (jugador.getFortuna() < coste) {
            return "La fortuna de " + jugador.getNombre() + " no es suficiente para edificar un " + tipo + ".";
        }

        // Validar Reglas de Construcción
        switch (tipo.toLowerCase()) {
            case "casa":
                if (casas >= 4) return "No se puede edificar más de 4 casas en " + this.getNombre();
                if (hoteles >= 1) return "No se puede edificar casas en " + this.getNombre() + " puesto a que ya hay un hotel";
                break;
            case "hotel":
                if (casas < 4) return "No se puede edificar un hotel en " + this.getNombre() + " sin 4 casas previas";
                if (hoteles >= 1) return "Ya hay un hotel construido en " + this.getNombre();
                edificios.clear();
                break;
            case "piscina":
                if (hoteles < 1) return "No se puede edificar una piscina en " + this.getNombre() + " sin un hotel";
                if (piscinas >= 1) return "Ya hay una pisicina construida en " + this.getNombre();
                break;
            case "pista_deporte":
                if (hoteles < 1)
                    return "No se puede edificar una pista de deporte en " + this.getNombre() + " sin un hotel";
                if (piscinas < 1)
                    return "No se puede edificar una pista de deporte en " + this.getNombre() + " sin una piscina";
                if (pistas >= 1) return "Ya hay una pista de deporte construida en " + this.getNombre();
                break;
        }

        // Ejecutar Construcción
        Edificio nuevo = new Edificio(tipo, jugador, this, this.getGrupo().getColorGrupo(), coste);
        this.addEdificio(nuevo);
        jugador.sumarGastos(coste);

        // Actualizar Stats (Singleton)
        StatsTracker.getInstance().asegurarJugador(jugador);
        StatsTracker.getInstance().byPlayer.get(jugador.getNombre()).addDineroInvertido(coste);

        return "Se ha edificado un " + tipo + " en " + this.getNombre() + ". La fortuna de " + jugador.getNombre() + " se reduce en " + Valor.formatear(coste) + "€.";
    }

    public String hipotecarPropiedad(Jugador jugador) {
        if (!this.getDuenho().equals(jugador)) {
            return "No eres el propietario de " + this.getNombre();
        }

        if (this.isHipotecada()) {
            return "La propiedad ya está hipotecada.";
        }

        if (!this.getEdificios().isEmpty()) {
            return "Debes vender todos los edificios antes de hipotecar " + this.getNombre();
        }

        float cantidad = this.calcularValorHipoteca(); // Ya tienes este método
        this.setHipotecada(true);
        jugador.sumarFortuna(cantidad);

        return "Se hipoteca " + this.getNombre() + " por " + Valor.formatear(cantidad) + "€.";
    }

    public String deshipotecarPropiedad(Jugador jugador) {
        // El propietario (jugador) se infiere de this.getDuenho()
        if (!this.getDuenho().equals(jugador)) {
            return "No eres el propietario de " + this.getNombre();
        }

        if (!this.isHipotecada()) {
            return "La propiedad no está hipotecada.";
        }

        // El coste de deshipotecar es el valor de la hipoteca
        float cantidad = this.calcularValorHipoteca();

        if (jugador.getFortuna() < cantidad) {
            return "No tienes suficiente dinero (" + Valor.formatear(cantidad) + "€) para deshipotecar esta propiedad.";
        }

        jugador.sumarGastos(cantidad);
        this.setHipotecada(false);

        return "Se deshipoteca " + this.getNombre() + " pagando " + Valor.formatear(cantidad) + "€.";
    }

    public String venderEdificios(Jugador jugador, String tipo, int cantidad) {
        if (!this.getDuenho().equals(jugador)) {
            return "No eres el propietario de " + this.getNombre();
        }

        int disponibles = this.getCantidadEdificioTipo(tipo.toLowerCase());

        if (disponibles == 0) {
            return "No hay edificios de tipo " + tipo + " en " + this.getNombre();
        }

        int vender = Math.min(disponibles, cantidad);

        // Calcular precio de venta
        float precioVentaUnitario;
        int col = switch (tipo.toLowerCase()) {
            case "casa" -> 0;
            case "hotel" -> 1;
            case "piscina" -> 2;
            case "pista_deporte" -> 3;
            default -> -1;
        };

        if (col == -1) {
            return "Tipo de edificio no válido: " + tipo;
        }

        precioVentaUnitario = Valor.PRECIO_EDIFICIOS[this.idSolar][col];

        float totalGanado = vender * precioVentaUnitario;

        this.eliminarEdificios(tipo, vender);
        jugador.sumarFortuna(totalGanado);

        // Actualizar Stats
        StatsTracker.getInstance().asegurarJugador(jugador);
        StatsTracker.getInstance().byPlayer.get(jugador.getNombre()).addDineroInvertido(-totalGanado);

        return "Vendidas " + vender + " " + tipo + "(s) en " + this.getNombre() + " por " + Valor.formatear(totalGanado) + "€.";
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

    public String getResumenEdificios() { // Renombramos el método para reflejar que devuelve un String
        StringBuilder descripcion = new StringBuilder();
        for (Edificio e : edificios) {
            descripcion.append(e.describirEdificio() + ",\n");
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

        // Contar edificios construidos (Lógica de conteo copiada del método original)
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
            faltan.setLength(faltan.length() - 2); // Eliminar el ", " final
            return "Aún se pueden construir " + faltan;
        } else {
            return "";
        }
    }

    public boolean isHipotecada() {return hipotecada;}

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

    public float calcularValorHipoteca() {return getValor() / 2;}

    public int contarEdificiosTipo(String tipo) {
        int c = 0;
        for (Edificio e : edificios)
            if (e.getTipo().equalsIgnoreCase(tipo)) c++;
        return c;
    }

    public int getIdSolar() {return idSolar;}
}