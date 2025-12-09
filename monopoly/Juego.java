package monopoly;

import monopoly.carta.Carta;
import monopoly.casilla.Casilla;
import monopoly.casilla.Propiedad;
import monopoly.casilla.accion.CajaComunidad;
import monopoly.casilla.accion.Suerte;
import monopoly.casilla.propiedad.Solar;
import monopoly.excepcion.*;
import monopoly.trato.Trato;
import partida.Avatar;
import partida.Dado;
import partida.Jugador;

import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// Nota: Ya no importamos java.util.Scanner aquí

public class Juego implements Comando {

    // Atributo estático para la consola (accesible desde otras clases)
    public static Consola consola = new ConsolaNormal();
    private static Juego instancia;

    //Atributos
    private final ArrayList<Jugador> jugadores;
    private final ArrayList<Avatar> avatares;
    private final Tablero tablero;
    private final Dado dado1;
    private final Dado dado2;
    private final Jugador banca;
    private final MazoCartas mazoSuerte;
    private final MazoCartas mazoCaja;
    private final StatsTracker stats;
    private final ArrayList<String> cmdsHistory = new ArrayList<>();
    private final ArrayList<Trato> tratos;

    // Variables de estado
    private int turno = 0;
    private int lanzamientos;
    private boolean tirado;
    private boolean solvente;
    private boolean partidaIniciada;

    public Juego(String archivoComandos) {
        instancia = this;
        jugadores = new ArrayList<>();
        avatares = new ArrayList<>();
        lanzamientos = 0;
        dado1 = new Dado();
        dado2 = new Dado();
        banca = new Jugador();
        tablero = new Tablero(banca);
        tirado = false;
        solvente = true;
        partidaIniciada = false;
        this.mazoSuerte = new MazoCartas(true);
        this.mazoCaja = new MazoCartas(false);
        this.stats = StatsTracker.getInstance();
        this.tratos = new ArrayList<>();

        consola.imprimir("Bienvenido a MonopolyETSE.");

        if (archivoComandos != null) {
            ComandoArchivo comandoArchivo = new ComandoArchivo(this, archivoComandos);
            comandoArchivo.procesarComandos();
        }

        iniciarBucleComandos();
    }

    public Juego() {
        instancia = this;
        jugadores = new ArrayList<>();
        avatares = new ArrayList<>();
        lanzamientos = 0;
        dado1 = new Dado();
        dado2 = new Dado();
        banca = new Jugador();
        tablero = new Tablero(banca);
        tirado = false;
        solvente = true;
        partidaIniciada = false;
        this.mazoSuerte = new MazoCartas(true);
        this.mazoCaja = new MazoCartas(false);
        this.stats = StatsTracker.getInstance();
        this.tratos = new ArrayList<>();

        consola.imprimir("Bienvenido a MonopolyETSE. Introduce un comando (ejemplo: 'crear jugador' o 'salir' para terminar).");

        iniciarBucleComandos();
    }

    public static Juego getInstance() throws AccionInvalidaException {
        if (instancia == null) {
            throw new AccionInvalidaException("El juego no ha sido inicializado todavía.");
        }
        return instancia;
    }

    public Jugador getBanca() {
        return banca;
    }

    // Método que contiene el bucle de comandos usando la Consola
    private void iniciarBucleComandos() {
        while (true) {
            String prompt = (!jugadores.isEmpty() && partidaIniciada) ? jugadores.get(turno).getNombre() + ">" : ">";

            // Usamos consola para leer para mostrar el prompt y obtener el comando
            String comando = consola.leer(prompt);

            if (comando.equalsIgnoreCase("salir")) break;
            analizarComando(comando);
        }
    }

    private void iniciarPartida() {
        if (jugadores.size() < 2) {
            consola.imprimir("Se necesitan como mínimo 2 jugadores para iniciar la partida.");
        } else if (jugadores.size() > 4) {
            consola.imprimir("Máximo 4 jugadores permitidos.");
        } else {
            partidaIniciada = true;
            consola.imprimir("Partida iniciada.");
        }
    }

    public void analizarComando(String comando) {
        if (comando.isEmpty()) return;

        comando = Normalizer.normalize(comando, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]+", "");

        if (comando.equalsIgnoreCase("guardar cmds")) {
            guardarComandos();
            return;
        } else {
            cmdsHistory.add(comando);
        }

        String[] partes = comando.split("\\s+");
        if (partes.length == 0) return;

        String cmdLower = comando.toLowerCase();

        if (cmdLower.startsWith("crear jugador")) {
            if (partes.length == 2) {
                String nombre = consola.leer("Introduce el nombre del jugador:");
                String tipoAvatar = consola.leer("Introduce el tipo de avatar (Coche \uD83D\uDE97, Esfinge \uD83D\uDED5, Sombrero \uD83C\uDFA9 o Pelota ⚽):");
                crearJugador(nombre, tipoAvatar);
            } else if (partes.length == 4) {
                crearJugador(partes[2], partes[3]);
            }
        } else if (comando.equalsIgnoreCase("jugador")) {
            mostrarInfoJugadorTurno();
        } else if (comando.equalsIgnoreCase("iniciar partida")) {
            iniciarPartida();
        } else if (comando.equalsIgnoreCase("listar jugadores")) {
            listarJugadores();
        } else if (comando.equalsIgnoreCase("listar avatares")) {
            listarAvatares();
        } else if (cmdLower.startsWith("describir jugador") && partes.length == 3) {
            descJugador(partes[2]);
        } else if (cmdLower.startsWith("describir avatar") && partes.length == 3) {
            descAvatar(partes[2]);
        } else if (cmdLower.startsWith("describir ") && partes.length == 2) {
            descCasilla(partes[1]);
        } else if (comando.equalsIgnoreCase("lanzar dados") || cmdLower.startsWith("lanzar dados ")) {
            if (!partidaIniciada) {
                iniciarPartida();
            }
            boolean forced = partes.length == 3;
            int fv1 = 0, fv2 = 0;
            if (forced) {
                String[] vals = partes[2].split("\\+");
                fv1 = Integer.parseInt(vals[0]);
                fv2 = Integer.parseInt(vals[1]);
            }
            try {
                lanzarDados(forced, fv1, fv2);
            } catch (AccionInvalidaException e) {
                consola.imprimir(e.getMessage());
            }

        } else if (cmdLower.startsWith("comprar ") && partes.length == 2) {
            if (partidaIniciada) comprar(partes[1]);
            else consola.imprimir("No se ha iniciado la partida.");
        } else if (comando.equalsIgnoreCase("salir carcel")) {
            if (partidaIniciada) salirCarcel();
            else consola.imprimir("No se ha iniciado la partida.");
        } else if (comando.equalsIgnoreCase("listar enventa")) {
            listarVenta();
        } else if (cmdLower.startsWith("listar enventa") && partes.length == 3) {
            listarVenta(partes[2]);
        } else if (comando.equalsIgnoreCase("acabar turno")) {
            if (partidaIniciada) acabarTurno();
            else consola.imprimir("No se ha iniciado la partida.");
        } else if (comando.equalsIgnoreCase("ver tablero")) {
            verTablero();
        } else if (cmdLower.startsWith("comandos") && partes.length == 2) {
            ComandoArchivo comandoArchivo = new ComandoArchivo(this, partes[1]);
            comandoArchivo.procesarComandos();
        } else if (cmdLower.startsWith("edificar") && partes.length == 2) {
            edificar(partes[1]);
        } else if (cmdLower.startsWith("hipotecar") && partes.length == 2) {
            hipotecar(partes[1]);
        } else if (cmdLower.startsWith("deshipotecar") && partes.length == 2) {
            deshipotecar(partes[1]);
        } else if (cmdLower.startsWith("vender") && partes.length == 4) {
            venderEdificios(partes[1], partes[2], Integer.parseInt(partes[3]));
        } else if (comando.equalsIgnoreCase("listar edificios")) {
            listarEdificios();
        } else if (cmdLower.startsWith("listar edificios") && partes.length == 3) {
            listarEdificios(partes[2]);
        } else if (cmdLower.startsWith("estadisticas")) {
            if (partes.length == 1) {
                mostrarEstadisticas(null);
            } else {
                mostrarEstadisticas(partes[1]);
            }
        } else if (cmdLower.startsWith("trato ")) {
            try {
                proponerTrato(comando);
            } catch (AccionInvalidaException e) {
                consola.imprimir(e.getMessage());
            }
        } else if (cmdLower.startsWith("aceptar trato")) {
            String idTrato = cmdLower.replace("aceptar ", "").trim();
            try {
                aceptarTrato(idTrato);
            } catch (PropiedadNoPerteneceException | AccionInvalidaException e) {
                consola.imprimir(e.getMessage());
            }
        } else if (cmdLower.startsWith("eliminar trato")) {
            String idTrato = cmdLower.replace("eliminar ", "").trim();
            try {
                eliminarTrato(idTrato);
            } catch (AccionInvalidaException e) {
                consola.imprimir(e.getMessage());
            }
        } else if (cmdLower.equals("tratos")) {
            listarTratos();
        } else {
            consola.imprimir("Comando no reconocido.");
            consola.imprimir("Comandos disponibles: crear jugador, lanzar dados, comprar, etc.");
        }
    }

    private void guardarComandos() {
        try {
            FileWriter writer = new FileWriter("comandos_.txt", false);
            for (String cmd : cmdsHistory) {
                writer.write(cmd + System.lineSeparator());
            }
            writer.close();
            consola.imprimir("Comandos guardados correctamente en comandos_.txt");
        } catch (IOException e) {
            consola.imprimir("Error al guardar los comandos: " + e.getMessage());
        }
    }

    // IMPLEMENTACIÓN DE LA INTERFAZ COMANDO

    @Override
    public void crearJugador(String nombre, String tipoAvatar) {
        if (jugadores.size() >= 4) {
            consola.imprimir("Máximo 4 jugadores permitidos.");
            return;
        }

        boolean used = false;
        for (Jugador j : jugadores) {
            if (j.getNombre().equals(nombre)) {
                used = true;
                break;
            }
        }

        if (nombre.equalsIgnoreCase(banca.getNombre())) used = true;

        if (!used) {
            Jugador nuevoJugador = new Jugador(nombre, tipoAvatar, avatares);

            if (nuevoJugador.getAvatar() != null) {
                Casilla salida = tablero.encontrar_casilla("Salida");
                if (salida != null) {
                    nuevoJugador.getAvatar().setLugar(salida);
                    salida.anhadirAvatar(nuevoJugador.getAvatar());
                    jugadores.add(nuevoJugador);
                    avatares.add(nuevoJugador.getAvatar());
                    consola.imprimir("Jugador " + nombre + " creado con avatar " + tipoAvatar + " (ID: " + nuevoJugador.getAvatar().getId() + ").");
                } else {
                    consola.imprimir("Error: No se encontró la casilla Salida.");
                }
            } else {
                consola.imprimir("No se creó el jugador. Avatar " + tipoAvatar + " no es válido. Use Coche \uD83D\uDE97, Esfinge \uD83D\uDED5, Sombrero \uD83C\uDFA9 o Pelota ⚽.");
            }
        } else {
            consola.imprimir("Nombre ya usado.");
        }
    }

    @Override
    public void listarJugadores() {
        if (jugadores.isEmpty()) {
            consola.imprimir("No hay jugadores.");
            return;
        }
        for (Jugador j : jugadores) {
            consola.imprimir(j.getDescripcionDetallada());
        }
    }

    @Override
    public void listarAvatares() {
        if (jugadores.isEmpty()) {
            consola.imprimir("No hay jugadores.");
            return;
        }
        for (Avatar a : avatares) {
            consola.imprimir("{ \n id: " + a.getId() + ", \n tipo: " + a.getTipo() + ", \n jugador: " + a.getJugador().getNombre() + ", \n casilla: " + a.getLugar().getNombre() + "\n}");
        }
    }

    @Override
    public void descJugador(String name) {
        Jugador j = null;
        for (Jugador ju : jugadores) {
            if (ju.getNombre().equals(name)) {
                j = ju;
                break;
            }
        }

        if (j == null) {
            consola.imprimir("No existe el jugador " + name);
            return;
        }

        consola.imprimir(j.getDescripcionDetallada());
    }

    @Override
    public void descAvatar(String ID) {
        Avatar a = null;
        for (Avatar av : avatares) {
            if (av.getId().equals(ID)) {
                a = av;
                break;
            }
        }

        if (a == null) {
            consola.imprimir("No se ha encontrado ningún avatar con el ID: " + ID);
            return;
        }

        consola.imprimir(a.getDescripcionDetallada());
    }

    @Override
    public void descCasilla(String nombre) {
        Casilla c = tablero.encontrar_casilla(nombre);
        if (c != null) {
            consola.imprimir(c.infoCasilla());
        }
    }

    @Override
    public void lanzarDados(boolean forced, int fv1, int fv2) throws AccionInvalidaException {
        if (jugadores.isEmpty()) return;
        Jugador current = jugadores.get(turno);
        if (tirado) {
            consola.imprimir("Ya has tirado este turno. Debes acabar el turno con 'acabar turno'");
            return;
        }
        int v1, v2;
        if (forced) {
            v1 = fv1;
            v2 = fv2;
        } else {
            v1 = dado1.hacerTirada();
            v2 = dado2.hacerTirada();
        }
        int tirada = v1 + v2;
        String msg = "El avatar " + current.getAvatar().getId() + " avanza " + tirada + " (" + v1 + ", " + v2 + ") posiciones, desde " + current.getAvatar().getLugar().getNombre();
        tirado = true;

        if (current.isEnCarcel()) {
            if (current.getTiradasCarcel() < 3) {
                if (v1 == v2) {
                    current.setEnCarcel(false);
                    current.setTiradasCarcel(0);
                    current.getAvatar().moverAvatar(tablero.getPosiciones(), tirada);
                    msg += " hasta " + current.getAvatar().getLugar().getNombre() + ". Sale de la cárcel por dobles.";
                    consola.imprimir(msg);
                    solvente = current.getAvatar().getLugar().evaluarCasilla(current, banca, tirada);
                } else {
                    current.setTiradasCarcel(current.getTiradasCarcel() + 1);
                    consola.imprimir("No sacaste dobles.");
                    if (current.getTiradasCarcel() == 3) {
                        if (current.getFortuna() < Valor.SALIR_CARCEL) {
                            consola.imprimir("No puedes pagar para salir, declara bancarrota o vende propiedades e hipotecar.\nVas a estar en negativo.");
                        }

                        current.sumarGastos(Valor.SALIR_CARCEL);
                        banca.sumarFortuna(Valor.SALIR_CARCEL);
                        current.setEnCarcel(false);
                        current.getAvatar().moverAvatar(tablero.getPosiciones(), tirada);
                        msg += " hasta " + current.getAvatar().getLugar().getNombre() + ". Paga para salir después de 3 intentos.";
                        consola.imprimir(msg);
                        solvente = current.getAvatar().getLugar().evaluarCasilla(current, banca, tirada);

                    }
                }
            }
        } else {
            current.getAvatar().moverAvatar(tablero.getPosiciones(), tirada);
            String destino = current.getAvatar().getLugar().getNombre();
            msg += " hasta " + current.getAvatar().getLugar().getNombre() + ".";
            consola.imprimir(msg);
            solvente = current.getAvatar().getLugar().evaluarCasilla(current, banca, tirada);
            // Registrar visita a la casilla
            Casilla destinoC = current.getAvatar().getLugar();
            StatsTracker.getInstance().registrarVisita(destinoC);

            // Si es Suerte o Caja, usar mazo central
            if (destinoC instanceof Suerte) {
                consola.imprimir("Estas en suerte");
                procesarCarta(destinoC, current, tablero, jugadores);
            } else if (destinoC instanceof CajaComunidad) {
                consola.imprimir("Estas en caja comunidad");
                procesarCarta(destinoC, current, tablero, jugadores);
            }

            if (!solvente) {
                consola.imprimir("El jugador no es solvente. Debes hipotecar o declarar bancarrota.");
            } else if (destino.equalsIgnoreCase("Carcel")) {
                consola.imprimir("Has aterrizado en Carcel. No estás encarcelado, estás de visita.");
            } else if (destino.equalsIgnoreCase("IrCarcel")) {
                current.encarcelar(tablero.getPosiciones());
                consola.imprimir("Has caído en IrCarcel. Vas directo a Carcel.");
                StatsTracker.getInstance().registrarEncarcelamiento(current);
            }

            if (v1 == v2) {
                lanzamientos++;
                tirado = false;
                if (lanzamientos == 3) {
                    current.encarcelar(tablero.getPosiciones());
                    consola.imprimir("Tres dobles, vas a la cárcel.");
                    lanzamientos = 0;
                }
            }
        }
    }

    @Override
    public void comprar(String nombre) {
        Jugador current = jugadores.get(turno);
        Casilla casilla = current.getAvatar().getLugar();

        try {
            if (!casilla.getNombre().equals(nombre)) {
                throw new AccionInvalidaException("No estás en la casilla " + nombre + " estás en " + casilla.getNombre());
            }

            if (!(casilla instanceof Propiedad propiedad)) {
                throw new CasillaNoComprableException("La casilla " + nombre + " no se puede comprar.");
            }

            propiedad.comprar(current);
        } catch (AccionInvalidaException | CasillaNoComprableException | PropiedadNoPerteneceException e) {
            consola.imprimir("Error: " + e.getMessage());
        }
    }

    @Override
    public void salirCarcel() {
        if (jugadores.isEmpty()) return;
        Jugador current = jugadores.get(turno);
        if (current.isEnCarcel()) {
            if (current.getFortuna() >= Valor.SALIR_CARCEL) {
                consola.imprimir("No tienes suficiente dinero. Vende propiedades e hipoteca.\nVas a estar en negativo.");
            }
            current.sumarGastos(Valor.SALIR_CARCEL);
            banca.sumarFortuna(Valor.SALIR_CARCEL);
            current.setEnCarcel(false);
            consola.imprimir(current.getNombre() + " paga 500.000€ y sale de la cárcel. Puede lanzar los dados.");

        } else {
            consola.imprimir("No estás en la cárcel. Estás en " + current.getAvatar().getLugar().getNombre() + ", no encarcelado.");
        }
    }

    @Override
    public void listarVenta() {
        StringBuilder s = new StringBuilder();
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if ((c instanceof Propiedad) && c.getDuenho().equals(banca)) {
                    s.append(((Propiedad) c).casEnVenta()).append(",\n");
                }
            }
        }
        consola.imprimir(s.toString());
    }

    @Override
    public void listarVenta(String grupoBuscado) {
        StringBuilder s = new StringBuilder();
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if ((c instanceof Solar) && c.getDuenho().equals(banca)) {
                    String dato = ((Solar) c).casEnVenta(grupoBuscado);
                    if (!dato.isEmpty()) {
                        consola.imprimir(dato);
                        s.append(dato).append(",\n");
                    }
                }
            }
        }
        consola.imprimir(s.toString());
    }

    @Override
    public void edificar(String tipo) {
        Jugador current = jugadores.get(turno);
        Casilla actual = current.getAvatar().getLugar();

        if (!(actual instanceof Solar solar)) {
            consola.imprimir("No se puede edificar en esta casilla. Estás en " + actual.getNombre());
            return;
        }


        try {
            if (!solar.getDuenho().equals(current)) {
                throw new PropiedadNoPerteneceException(current.getNombre());
            }
            solar.edificar(tipo);
        } catch (PropiedadYaHipotecadaException | PropiedadNoPerteneceException | EdificacionIlegalException |
                 AccionInvalidaException e) {
            String resultado = e.getMessage();
            consola.imprimir(resultado);
        }
    }

    @Override
    public void hipotecar(String nombreCasilla) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombreCasilla);

        if (!(c instanceof Solar solar)) {
            consola.imprimir("Solo se pueden hipotecar Solares.");
            return;
        }

        try {
            solar.hipotecar(current);
        } catch (PropiedadYaHipotecadaException | PropiedadNoPerteneceException e) {
            String resultado = e.getMessage();
            consola.imprimir(resultado);
        }
    }

    @Override
    public void deshipotecar(String nombreCasilla) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombreCasilla);

        if (!(c instanceof Solar solar)) {
            consola.imprimir("Solo se pueden deshipotecar solares.");
            return;
        }

        try {
            solar.deshipotecar(current);
        } catch (FondosInsuficientesException | PropiedadNoHipotecadaException | PropiedadNoPerteneceException e) {
            String resultado = e.getMessage();
            consola.imprimir(resultado);
        }
    }

    @Override
    public void venderEdificios(String tipo, String nombreCasilla, int cantidad) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombreCasilla);

        if (!(c instanceof Solar solar)) {
            consola.imprimir("Solo se pueden vender edificios en solares.");
            return;
        }

        String resultado;
        try {
            resultado = solar.venderEdificios(current, tipo, cantidad);
        } catch (PropiedadNoPerteneceException | AccionInvalidaException e) {
            resultado = e.getMessage();
        }
        consola.imprimir(resultado);
    }

    @Override
    public void listarEdificios() {
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if (!(c instanceof Solar s)) continue;

                if (s.getEdificios().isEmpty()) continue;
                consola.imprimir(s.getResumenEdificios());
            }
        }
    }

    @Override
    public void listarEdificios(String grupoFiltro) {
        for (ArrayList<Casilla> casilla : tablero.getPosiciones()) {
            for (Casilla c : casilla) {
                if (c.getGrupo() == null) continue;
                if (c.getGrupo().getColorGrupo().equalsIgnoreCase(grupoFiltro)) {
                    consola.imprimir(c.getGrupo().getDescripcionGrupo());
                    break;
                }
            }
        }
    }

    @Override
    public void acabarTurno() {
        Jugador current = jugadores.get(turno);
        this.solvente = current.getFortuna() >= 0.0f;
        float deuda = Math.abs(current.getFortuna());
        ArrayList<Casilla> propiedadesJugador = current.getPropiedades();
        Casilla actual = current.getAvatar().getLugar();
        boolean casillasSinHipotecar = false;
        int contarSolares = 0;

        if (!solvente) {
            if (propiedadesJugador.isEmpty()) {
                consola.imprimir("No tienes propiedades, entonces no puedes obtener dinero. Estás en bancarrota");
                actual.eliminarAvatar(current.getAvatar());
                avatares.remove(current.getAvatar());
                jugadores.remove(current);
            } else {
                for (Casilla c : propiedadesJugador) {
                    if ((c instanceof Solar s)) {
                        contarSolares++;
                        if (!s.estaHipotecada()) casillasSinHipotecar = true;
                    }
                }
                if (casillasSinHipotecar && (propiedadesJugador.size() == contarSolares)) {
                    consola.imprimir("Todos los Solares están hipotecados, y no tienes más propiedades, entonces no puedes obtener dinero. Estás en bancarrota");
                    current.declararBancarrota(banca);
                    actual.eliminarAvatar(current.getAvatar());
                    avatares.remove(current.getAvatar());
                    jugadores.remove(current);
                }
            }
            consola.imprimir("Turno: " + jugadores.get(turno).getNombre() + ", no eres solvente, no puedes acabar turno. " + "Tienes que vender o hipotecar hasta dejar de estar en negativo.\n" + "Tienes que conseguir " + Valor.formatear(deuda) + " de dinero.");
            return;
        }
        turno = (turno + 1) % jugadores.size();
        tirado = false;
        lanzamientos = 0;
        Jugador siguiente = jugadores.get(turno);
        consola.imprimir("El jugador actual es " + siguiente.getNombre() + ".");
        listarTratosPendientes(siguiente);
    }

    @Override
    public void verTablero() {
        consola.imprimir(tablero.toString());
    }

    @Override
    public void mostrarEstadisticas(String objetivo) {
        if (objetivo == null) {
            consola.imprimir(stats.reporteGlobal(jugadores, tablero));
        } else {
            consola.imprimir(stats.reporteJugador(objetivo, jugadores));
        }
    }

    @Override
    public void mostrarInfoJugadorTurno() {
        if (partidaIniciada) {
            Jugador curr = jugadores.get(turno);
            consola.imprimir("{ \n nombre: " + curr.getNombre() + ", \n avatar: " + curr.getAvatar().getId() + "\n}");
        } else {
            consola.imprimir("Partida no iniciada.");
        }
    }

    @Override
    public void proponerTrato(String comandoCompleto) throws AccionInvalidaException {
        if (!partidaIniciada) return;
        Jugador emisor = jugadores.get(turno);

        // Regex para parsear: trato <jugador>: cambiar (<parte1>, <parte2>)
        // Ejemplo: trato Maria: cambiar (Solar1, Solar14 y 300000)
        Pattern pattern = Pattern.compile("trato\\s+([a-zA-Z0-9]+):\\s+cambiar\\s*\\((.+?),\\s*(.+?)\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(comandoCompleto);

        if (!matcher.find()) {
            throw new AccionInvalidaException("Sintaxis incorrecta. Usa: trato <jugador>: cambiar (<lo_que_das>, <lo_que_pides>)");
        }

        String nombreReceptor = matcher.group(1);
        String ladoOfertaStr = matcher.group(2).trim();
        String ladoDemandaStr = matcher.group(3).trim();

        // Buscar receptor
        Jugador receptor = null;
        for (Jugador j : jugadores) {
            if (j.getNombre().equalsIgnoreCase(nombreReceptor)) {
                receptor = j;
                break;
            }
        }
        if (receptor == null) {
            throw new TratoInvalidoException("El jugador " + nombreReceptor + " no existe.");
        }
        if (receptor.equals(emisor)) {
            throw new TratoInvalidoException("No puedes proponerte un trato a ti mismo.");
        }

        try {
            // Analizar qué ofrece el emisor
            Object[] oferta = analizarLadoTrato(ladoOfertaStr);
            Solar solarOferta = (Solar) oferta[0];
            float dineroOferta = (float) oferta[1];

            // Analizar qué pide al receptor
            Object[] demanda = analizarLadoTrato(ladoDemandaStr);
            Solar solarDemanda = (Solar) demanda[0];
            float dineroDemanda = (float) demanda[1];

            // Validaciones de PROPIEDAD (Solo se chequea pertenencia al proponer, dinero se chequea al aceptar según enunciado, aunque enunciado dice "el que propone no tiene dinero suficiente" lanza excepción)

            if (solarOferta != null && !solarOferta.getDuenho().equals(emisor)) {
                throw new TratoInvalidoException("No se puede proponer el trato: " + solarOferta.getNombre() + " no pertenece a " + emisor.getNombre() + ".");
            }
            if (solarDemanda != null && !solarDemanda.getDuenho().equals(receptor)) {
                throw new TratoInvalidoException("No se puede proponer el trato: " + solarDemanda.getNombre() + " no pertenece a " + receptor.getNombre() + ".");
            }

            // Validación de dinero del proponente (si ofrece dinero)
            if (dineroOferta > 0 && emisor.getFortuna() < dineroOferta) {
                throw new TratoInvalidoException("No se puede proponer el trato: No tienes " + dineroOferta + "€.");
            }

            // Crear y guardar el trato
            Trato nuevoTrato = new Trato(emisor, receptor, solarOferta, dineroOferta, solarDemanda, dineroDemanda);
            tratos.add(nuevoTrato);

            consola.imprimir(emisor.getNombre() + ", ¿te doy " + ladoOfertaStr + " y tú me das " + ladoDemandaStr + "?");
            consola.imprimir("Trato registrado con ID: " + nuevoTrato.getId());

        } catch (Exception e) {
            consola.imprimir("Error al procesar el trato: " + e.getMessage());
        }
    }

    @Override
    public void aceptarTrato(String idTrato) throws PropiedadNoPerteneceException, AccionInvalidaException {
        Jugador turnoActual = jugadores.get(turno);
        Trato trato = null;

        for (Trato t : tratos) {
            if (t.getId().equalsIgnoreCase(idTrato)) {
                trato = t;
                break;
            }
        }

        if (trato == null) {
            throw new AccionInvalidaException("El trato " + idTrato + " no existe.");
        }

        if (!trato.getReceptor().equals(turnoActual)) {
            throw new AccionInvalidaException("Este trato no fue propuesto a ti. Fue a " + trato.getReceptor().getNombre());
        }

        Jugador proponente = trato.getProponente();
        Jugador receptor = trato.getReceptor();

        // Validar si el trato sigue siendo válido (propiedades no vendidas, dinero suficiente)

        // Validar Propiedades
        if (trato.getPropiedadOferta() != null && !trato.getPropiedadOferta().getDuenho().equals(proponente)) {
            consola.imprimir("El trato no puede ser aceptado: " + trato.getPropiedadOferta().getNombre() + " ya no pertenece a " + proponente.getNombre());
            tratos.remove(trato); // Eliminamos trato inválido
            throw new PropiedadNoPerteneceException(trato.getPropiedadOferta().getNombre());
        }
        if (trato.getPropiedadDemanda() != null && !trato.getPropiedadDemanda().getDuenho().equals(receptor)) {
            consola.imprimir("El trato no puede ser aceptado: " + trato.getPropiedadDemanda().getNombre() + " ya no pertenece a ti.");
            tratos.remove(trato);
            throw new PropiedadNoPerteneceException(trato.getPropiedadOferta().getNombre());
        }

        // Validar Dinero
        if (trato.getDineroOferta() > 0 && proponente.getFortuna() < trato.getDineroOferta()) {
            consola.imprimir("El trato no puede ser aceptado: " + proponente.getNombre() + " no dispone de " + trato.getDineroOferta());
            throw new FondosInsuficientesException(" / aceptar el trato.");
        }
        if (trato.getDineroDemanda() > 0 && receptor.getFortuna() < trato.getDineroDemanda()) {
            consola.imprimir("El trato no puede ser aceptado: " + receptor.getNombre() + " (tú) no dispones de " + trato.getDineroDemanda());
            throw new FondosInsuficientesException(" / aceptar el trato.");
        }

        // Ejecutar Intercambio

        // Transferir propiedades
        if (trato.getPropiedadOferta() != null) {
            trato.getPropiedadOferta().setDuenho(receptor);
            proponente.getPropiedades().remove(trato.getPropiedadOferta());
            receptor.getPropiedades().add(trato.getPropiedadOferta());
        }

        if (trato.getPropiedadDemanda() != null) {
            trato.getPropiedadDemanda().setDuenho(proponente);
            receptor.getPropiedades().remove(trato.getPropiedadDemanda());
            proponente.getPropiedades().add(trato.getPropiedadDemanda());
        }

        // Transferir dinero
        if (trato.getDineroOferta() > 0) {
            proponente.sumarGastos(trato.getDineroOferta()); // Quita dinero
            receptor.sumarFortuna(trato.getDineroOferta());  // Añade dinero
        }
        if (trato.getDineroDemanda() > 0) {
            receptor.sumarGastos(trato.getDineroDemanda());
            proponente.sumarFortuna(trato.getDineroDemanda());
        }

        consola.imprimir("Se ha aceptado el siguiente trato con " + proponente.getNombre() + ": " + trato.getDescripcion());
        tratos.remove(trato);
    }

    @Override
    public void listarTratos() {
        Jugador actual = jugadores.get(turno);
        boolean hayTratos = false;
        for (Trato t : tratos) {
            if (t.getReceptor().equals(actual)) {
                consola.imprimir(t + ", ID: " + t.getId());
                hayTratos = true;
            }
        }
        if (!hayTratos) consola.imprimir("No tienes tratos pendientes.");
    }

    @Override
    public void eliminarTrato(String idTrato) throws AccionInvalidaException {
        Jugador actual = jugadores.get(turno);
        Iterator<Trato> iter = tratos.iterator();
        boolean encontrado = false;

        while (iter.hasNext()) {
            Trato t = iter.next();
            if (t.getId().equalsIgnoreCase(idTrato)) {
                encontrado = true;
                if (t.getProponente().equals(actual)) {
                    iter.remove();
                    consola.imprimir("Se ha eliminado el " + idTrato + ".");
                } else {
                    consola.imprimir("No puedes eliminar un trato que no has propuesto tú.");
                }
                break;
            }
        }
        if (!encontrado) throw new TratoInvalidoException("No existe el trato " + idTrato + ".");
    }

    private void procesarCarta(Casilla destinoC, Jugador current, Tablero tablero, ArrayList<Jugador> jugadores) {
        MazoCartas mazo = null;
        if (destinoC instanceof Suerte) mazo = mazoSuerte;
        else if (destinoC instanceof CajaComunidad) mazo = mazoCaja;

        if (mazo != null) {
            Carta c = mazo.sacarCarta();
            if (c != null) c.accion(current, tablero, jugadores);
        }
    }

    // Método auxiliar para parsear "Solar1" o "Solar1 y 500" o "500"
    private Object[] analizarLadoTrato(String entrada) throws AccionInvalidaException {
        Solar solar = null;
        float dinero = 0;

        String[] partes = entrada.split(" y ");

        for (String p : partes) {
            p = p.trim();
            // Intentar ver si es número
            try {
                dinero = Float.parseFloat(p);
            } catch (NumberFormatException e) {
                // Es una propiedad
                Casilla c = tablero.encontrar_casilla(p);
                if (c == null) throw new AccionInvalidaException("La casilla " + p + " no existe.");
                if (!(c instanceof Solar))
                    throw new AccionInvalidaException(p + " no es un Solar (propiedad válida para trato).");
                solar = (Solar) c;
            }
        }
        return new Object[]{solar, dinero};
    }

    // Método interno para avisar automáticamente al inicio de turno
    private void listarTratosPendientes(Jugador jugador) {
        int count = 0;
        for (Trato t : tratos) {
            if (t.getReceptor().equals(jugador)) {
                count++;
            }
        }
        if (count > 0) {
            consola.imprimir("TIENES " + count + " TRATOS PENDIENTES.");
            listarTratos();
        }
    }
}