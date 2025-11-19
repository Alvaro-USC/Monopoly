package monopoly;

import monopoly.carta.Carta;
import monopoly.casilla.Casilla;
import monopoly.casilla.accion.CajaComunidad;
import monopoly.casilla.accion.Suerte;
import monopoly.casilla.propiedad.Solar;
import partida.Avatar;
import partida.Dado;
import partida.Jugador;

import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {

    //Atributos
    private final ArrayList<Jugador> jugadores; //Jugadores de la partida.
    private final ArrayList<Avatar> avatares; //Avatares en la partida.
    private final Tablero tablero; //Tablero en el que se juega.
    private final Dado dado1; //Dos dados para lanzar y avanzar casillas.
    private final Dado dado2;
    private final Jugador banca; //El jugador banca.
    private final MazoCartas mazoSuerte;
    private final MazoCartas mazoCaja;
    private final StatsTracker stats;
    private final ArrayList<String> CmdsHistory = new ArrayList<>();
    // Añadir un Scanner
    private final Scanner scanner = new Scanner(System.in);
    private int turno = 0; //Índice correspondiente a la posición en el arrayList del jugador (y el avatar) que tienen el turno
    private int lanzamientos; //Variable para contar el número de lanzamientos de un jugador en un turno.
    private boolean tirado; //Booleano para comprobar si el jugador que tiene el turno ha tirado o no.
    private boolean solvente; //Booleano para comprobar si el jugador que tiene el turno es solvente, es decir, si ha pagado sus deudas.
    private boolean partidaIniciada;

    public Menu(String archivoComandos) {
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


        // Mensaje de bienvenida
        System.out.println("Bienvenido a MonopolyETSE.");

        // Procesar archivo de comandos si se proporciona
        if (archivoComandos != null) {
            ComandoArchivo comandoArchivo = new ComandoArchivo(this, archivoComandos);
            comandoArchivo.procesarComandos();
        }

        iniciarBucleComandos();
    }

    public Menu() {
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


        // Mensaje de bienvenida
        System.out.println("Bienvenido a MonopolyETSE. Introduce un comando (ejemplo: 'crear jugador <nombre> <tipoAvatar>' o 'salir' para terminar).");

        iniciarBucleComandos();
    }

    // Método que contiene el bucle de comandos
    private void iniciarBucleComandos() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!jugadores.isEmpty() && partidaIniciada) {
                Jugador current = jugadores.get(turno);
                System.out.print(current.getNombre() + "> ");
            } else {
                System.out.print("> ");
            }
            String comando = scanner.nextLine().trim();
            if (comando.equals("salir")) break;
            analizarComando(comando);
        }
        scanner.close(); // Cerrar el Scanner al salir
    }

    // Método para iniciar una partida: crea los jugadores y avatares.
    private void iniciarPartida() {
        if (jugadores.size() < 2) {
            System.out.println("Se necesitan como mínimo 2 jugadores para iniciar la partida.");
        } else if (jugadores.size() > 4) {
            System.out.println("Máximo 4 jugadores permitidos.");
        } else {
            partidaIniciada = true;
            System.out.println("Partida iniciada.");
        }
    }

    // Nuevo método para crear al jugador
    private void crearJugador(String nombre, String tipoAvatar) {
        if (jugadores.size() >= 4) {
            System.out.println("Máximo 4 jugadores permitidos.");
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
                    System.out.println("Jugador " + nombre + " creado con avatar " + tipoAvatar + " (ID: " + nuevoJugador.getAvatar().getId() + ").");
                } else {
                    System.out.println("Error: No se encontró la casilla Salida.");
                }
            } else {
                System.out.println("No se creó el jugador. Avatar " + tipoAvatar + " no es válido. Use Coche \uD83D\uDE97, Esfinge \uD83D\uDED5, Sombrero \uD83C\uDFA9 o Pelota ⚽.");
            }
        } else {
            System.out.println("Nombre ya usado.");
        }
    }

    /*Método que interpreta el comando introducido y toma la acción correspondiente.
     * Parámetro: cadena de caracteres (el comando).
     */
    public void analizarComando(String comando) {
        comando = Normalizer.normalize(comando, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]+", "");

        if (comando.equalsIgnoreCase("guardar cmds")) {
            try {
                // Abrir el archivo "comandos_.txt" en modo de sobreescritura (borra todo antes de escribir)
                FileWriter writer = new FileWriter("comandos_.txt", false);

                // Iterar sobre el ArrayList CmdsHistory y escribir cada comando en una línea
                for (String cmd : CmdsHistory) {
                    writer.write(cmd + System.lineSeparator());
                }

                // Cerrar el writer
                writer.close();

                System.out.println("Comandos guardados correctamente en comandos_.txt");
            } catch (IOException e) {
                System.out.println("Error al guardar los comandos: " + e.getMessage());
            }
        } else CmdsHistory.add(comando);
        String[] partes = comando.split("\\s+");

        if (partes.length == 0) return;

        if (comando.toLowerCase().startsWith("crear jugador") && partes.length == 2) {
            System.out.println("Introduce el nombre del jugador:");
            String nombre = scanner.nextLine().trim();
            System.out.println("Introduce el tipo de avatar (Coche \uD83D\uDE97, Esfinge \uD83D\uDED5, Sombrero \uD83C\uDFA9 o Pelota ⚽):");
            String tipoAvatar = scanner.nextLine().trim();
            crearJugador(nombre, tipoAvatar);
        } else if (comando.toLowerCase().startsWith("crear jugador") && partes.length == 4) {
            String nombre = partes[2];
            String tipoAvatar = partes[3];
            crearJugador(nombre, tipoAvatar);
        } else if (comando.equalsIgnoreCase("jugador")) {
            if (partidaIniciada) {
                Jugador curr = jugadores.get(turno);
                System.out.println("{ \n nombre: " + curr.getNombre() + ", \n avatar: " + curr.getAvatar().getId() + "\n}");
            } else System.out.println("Partida no iniciada. Use el comando: iniciar partida o lanzar dados");
        } else if (comando.equalsIgnoreCase("iniciar partida")) {
            iniciarPartida();
        } else if (comando.equalsIgnoreCase("listar jugadores")) {
            if (!jugadores.isEmpty()) listarJugadores();
            else System.out.println("No hay jugadores. Use el comando: crear jugador <nombre> <tipoAvatar>");
        } else if (comando.equalsIgnoreCase("listar avatares")) {
            if (!jugadores.isEmpty()) listarAvatares();
            else System.out.println("No hay jugadores. Use el comando: crear jugador <nombre> <tipoAvatar>");
        } else if (comando.toLowerCase().startsWith("describir jugador") && partes.length == 3) {
            descJugador(partes[2]);
        } else if (comando.toLowerCase().startsWith("describir avatar") && partes.length == 3) {
            descAvatar(partes[2]);
        } else if (comando.toLowerCase().startsWith("describir ") && partes.length == 2) {
            descCasilla(partes[1]);
        } else if (comando.equalsIgnoreCase("lanzar dados") || comando.toLowerCase().startsWith("lanzar dados ")) {
            if (!partidaIniciada) {
                iniciarPartida();
            }
            boolean forced = partes.length == 3;
            int forcedV1 = 0, forcedV2 = 0;
            if (forced) {
                String[] vals = partes[2].split("\\+");
                forcedV1 = Integer.parseInt(vals[0]);
                forcedV2 = Integer.parseInt(vals[1]);
            }
            lanzarDados(forced, forcedV1, forcedV2);
            //System.out.println(tablero.toString()); // Mostrar el tablero tras la ejecución del comando

        } else if (comando.toLowerCase().startsWith("comprar ") && partes.length == 2) {
            if (partidaIniciada) comprar(partes[1]);
            else System.out.println("No se ha iniciado la partida. Use el comando: iniciar partida o lanzar dados");
        } else if (comando.equalsIgnoreCase("salir carcel")) {
            if (partidaIniciada) salirCarcel();
            else System.out.println("No se ha iniciado la partida. Use el comando: iniciar partida o lanzar dados");
        } else if (comando.equalsIgnoreCase("listar enventa")) {
            listarVenta();
            // System.out.println(tablero.toString()); // Mostrar el tablero tras la ejecución del comando
        } else if (comando.toLowerCase().startsWith("listar enventa") && partes.length == 3) {
            listarVenta(partes[2]);
            System.out.println(tablero.toString());
        } else if (comando.equalsIgnoreCase("acabar turno")) {
            if (partidaIniciada) acabarTurno();
            else System.out.println("No se ha iniciado la partida. Use el comando: iniciar partida o lanzar dados");
        } else if (comando.equalsIgnoreCase("ver tablero")) {
            System.out.println(tablero.toString());
        } else if (comando.toLowerCase().startsWith("comandos") && partes.length == 2) {
            ComandoArchivo comandoArchivo = new ComandoArchivo(this, partes[1]);
            comandoArchivo.procesarComandos();
        } else if (comando.toLowerCase().startsWith("edificar") && partes.length == 2) {
            edificar(partes[1]);
        } else if (comando.toLowerCase().startsWith("hipotecar") && partes.length == 2) {
            hipotecar(partes[1]);
        } else if (comando.toLowerCase().startsWith("deshipotecar") && partes.length == 2) {
            deshipotecar(partes[1]);
        } else if (comando.toLowerCase().startsWith("vender") && partes.length == 4) {
            venderEdificios(partes[1], partes[2], Integer.parseInt(partes[3]));
        } else if (comando.equalsIgnoreCase("listar edificios")) {
            listarEdificios();
        } else if (comando.toLowerCase().startsWith("listar edificios") && partes.length == 3) {
            listarEdificios(partes[2]);
        } else if (comando.toLowerCase().startsWith("estadisticas")) {
            String[] parts = comando.split("\\s+");
            if (parts.length == 1) {
                System.out.println(stats.reporteGlobal(jugadores, tablero));
            } else {
                String nombre = parts[1];
                System.out.println(stats.reporteJugador(nombre, jugadores));
            }
        } else {
            System.out.println("Comando no reconocido.");
            System.out.println("Comandos disponibles:\n\tcrear jugador <nombre> <avatar>");
            System.out.println("\tjugador\n\tlistar jugadores\n\tlistar avatares\n\tdescribir jugador <nombre>\n\tdescribir avatar <nombre>");
            System.out.println("\tdescribir <nombre>\n\tlanzar dados\n\tcomprar <casilla>\n\tsalir carcel\n\tlistar enventa\n\tacabar turno\n\tver tablero\n\tcomandos <comandos.txt>");
        }
    }

    /*Método que realiza las acciones asociadas al comando 'describir jugador'.
     * Parámetro: comando introducido
     */
    private void descJugador(String name) {
        Jugador j = null;
        for (Jugador ju : jugadores) {
            if (ju.getNombre().equals(name)) {
                j = ju;
                break;
            }
        }

        if (j == null) {
            System.out.println("No existe el jugador " + name);
            return;
        }

        System.out.println(j.getDescripcionDetallada());
    }


    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
     * Parámetro: id del avatar a describir.
     */
    private void descAvatar(String ID) {
        Avatar a = null;
        for (Avatar av : avatares) {
            if (av.getId().equals(ID)) {
                a = av;
                break;
            }
        }

        if (a == null) {
            System.out.println("No se ha encontrado ningún avatar con el ID: " + ID);
            return;
        }

        System.out.println(a.getDescripcionDetallada());
    }

    /* Método que realiza las acciones asociadas al comando 'describir nombre_casilla'.
     * Parámetros: nombre de la casilla a describir.
     */
    private void descCasilla(String nombre) {
        Casilla c = tablero.encontrar_casilla(nombre);
        if (c != null) {
            System.out.println(c.infoCasilla());
        }
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'lanzar dados'.
    private void lanzarDados(boolean forced, int fv1, int fv2) {
        if (jugadores.isEmpty()) return;
        Jugador current = jugadores.get(turno);
        if (tirado) {
            System.out.println("Ya has tirado este turno. Debes acabar el turno con 'acabar turno'");
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
                    System.out.println(msg);
                    solvente = current.getAvatar().getLugar().evaluarCasilla(current, banca, tirada);
                } else {
                    current.setTiradasCarcel(current.getTiradasCarcel() + 1);
                    System.out.println("No sacaste dobles.");
                    if (current.getTiradasCarcel() == 3) {
                        if (current.getFortuna() < Valor.SALIR_CARCEL) {
                            System.out.println("No puedes pagar para salir, declara bancarrota o vende propiedades e hipotecar.\nVas a estar en negativo.");
                        }

                        current.sumarGastos(Valor.SALIR_CARCEL);
                        banca.sumarFortuna(Valor.SALIR_CARCEL);
                        current.setEnCarcel(false);
                        current.getAvatar().moverAvatar(tablero.getPosiciones(), tirada);
                        msg += " hasta " + current.getAvatar().getLugar().getNombre() + ". Paga para salir después de 3 intentos.";
                        System.out.println(msg);
                        solvente = current.getAvatar().getLugar().evaluarCasilla(current, banca, tirada);

                    }
                }
            }
        } else {
            current.getAvatar().moverAvatar(tablero.getPosiciones(), tirada);
            String destino = current.getAvatar().getLugar().getNombre();
            msg += " hasta " + current.getAvatar().getLugar().getNombre() + ".";
            System.out.println(msg);
            solvente = current.getAvatar().getLugar().evaluarCasilla(current, banca, tirada);
            // Registrar visita a la casilla
            Casilla destinoC = current.getAvatar().getLugar();
            StatsTracker.getInstance().registrarVisita(destinoC);

            // Si es Suerte o Caja, usar mazo central
            if (destinoC instanceof Suerte) {
                System.out.println("Estas en suerte");
                Carta c = mazoSuerte.sacarCarta();
                if (c != null) c.ejecutar(current, tablero, jugadores);
            } else if (destinoC instanceof CajaComunidad) {
                System.out.println("Estas en caja comunidad");
                Carta c = mazoCaja.sacarCarta();
                if (c != null) c.ejecutar(current, tablero, jugadores);
            }

            if (!solvente) {
                System.out.println("El jugador no es solvente. Debes hipotecar o declarar bancarrota.");
            } else if (destino.equalsIgnoreCase("Carcel")) { // Verificar si aterrizó en Cárcel
                System.out.println("Has aterrizado en Carcel. No estás encarcelado, estás de visita.");
            } else if (destino.equalsIgnoreCase("IrCarcel")) {
                current.encarcelar(tablero.getPosiciones());
                System.out.println("Has caído en IrCarcel. Vas directo a Carcel.");
                StatsTracker.getInstance().registrarEncarcelamiento(current);
            }

            if (v1 == v2) { // Incrementar lanzamientos solo si son dobles
                lanzamientos++;
                tirado = false; // Permitir otra tirada con dobles
                if (lanzamientos == 3) {
                    current.encarcelar(tablero.getPosiciones());
                    System.out.println("Tres dobles, vas a la cárcel.");
                    lanzamientos = 0;
                }
            }
        }
    }

    /*Método que ejecuta todas las acciones asociadas al comando 'comprar nombre_casilla'.
     * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    private void comprar(String nombre) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombre);
        if (c != null && c.equals(current.getAvatar().getLugar())) {
            c.comprarCasilla(current, banca);
        } else {
            System.out.println("No estás en la casilla " + nombre + " estás en " + current.getAvatar().getLugar().getNombre());
        }
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'.
    private void salirCarcel() {
        if (jugadores.isEmpty()) return;
        Jugador current = jugadores.get(turno);
        if (current.isEnCarcel()) {
            if (current.getFortuna() >= Valor.SALIR_CARCEL) {
                System.out.println("No tienes suficiente dinero. Vende propiedades e hipoteca.\nVas a estar en negativo.");
            }
            current.sumarGastos(Valor.SALIR_CARCEL);
            banca.sumarFortuna(Valor.SALIR_CARCEL);
            current.setEnCarcel(false);
            System.out.println(current.getNombre() + " paga 500.000€ y sale de la cárcel. Puede lanzar los dados.");

        } else {
            System.out.println("No estás en la cárcel. Estás en " + current.getAvatar().getLugar().getNombre() + ", no encarcelado.");
        }
    }

    // Método que realiza las acciones asociadas al comando 'listar enventa'.
    private void listarVenta() {
        StringBuilder s = new StringBuilder();
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if ((c.getTipo().equals("Solar") || c.getTipo().equals("Transporte") || c.getTipo().equals("Servicios")) && c.getDuenho().equals(banca)) {
                    s.append(c.casEnVenta()).append(",\n");
                }
            }
        }
        System.out.println(s);
    }

    // Método que realiza las acciones asociadas al comando 'listar enventa [grupo]'.
    private void listarVenta(String grupoBuscado) {
        StringBuilder s = new StringBuilder();
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if ((c.getTipo().equals("Solar") || c.getTipo().equals("Transporte") || c.getTipo().equals("Servicios")) && c.getDuenho().equals(banca)) {
                    String dato = c.casEnVenta(grupoBuscado);
                    if (!dato.isEmpty()) {
                        System.out.println(dato);
                        s.append(dato).append(",\n");
                    }
                }
            }
        }
        System.out.println(s);
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    private void listarJugadores() {
        for (Jugador j : jugadores) {
            System.out.println(j.getDescripcionDetallada());
        }
    }


    // Método que realiza las acciones asociadas al comando 'listar avatares'.
    private void listarAvatares() {
        for (Avatar a : avatares) {
            System.out.println("{ \n id: " + a.getId() + ", \n tipo: " + a.getTipo() + ", \n jugador: " + a.getJugador().getNombre() + ", \n casilla: " + a.getLugar().getNombre() + "\n}");
        }
    }

    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    private void acabarTurno() {

        Jugador current = jugadores.get(turno);
        this.solvente = current.getFortuna() >= 0.0f;
        float deuda = Math.abs(current.getFortuna());
        ArrayList<Casilla> propiedadesJugador = current.getPropiedades();
        Casilla actual = current.getAvatar().getLugar();
        boolean casillasSinHipotecar = false;
        int contarSolares = 0;

        if (!solvente) { // Si el jugador no tiene dinero
            if (propiedadesJugador.isEmpty()) { // Si no tiene propiedades tampoco, entonces está en bancarrota y muere
                System.out.println("No tienes propiedades, entonces no puedes obtener dinero. Estás en bancarrota");
                actual.eliminarAvatar(current.getAvatar());
                avatares.remove(current.getAvatar());
                jugadores.remove(current);
            } else { // Si sí tiene propiedades
                for (Casilla c : propiedadesJugador) {
                    if ((c instanceof Solar s)) {
                        contarSolares++;
                        if (!s.isHipotecada())
                            casillasSinHipotecar = true; // Comprobamos que alguna de las propiedades que tiene no esté hipotecada
                    }
                }
                // Si ninguna casilla Solar está hipotecada y todas sus propiedades son solares, entonces muere
                if (casillasSinHipotecar && (propiedadesJugador.size() == contarSolares)) {
                    System.out.println("Todos los Solares están hipotecados, y no tienes más propiedes, entonces no puedes obtener dinero. Estás en bancarrota");
                    current.declararBancarrota(banca);
                    actual.eliminarAvatar(current.getAvatar());
                    avatares.remove(current.getAvatar());
                    jugadores.remove(current);
                }
            }
            // Si sí tiene propiedades y no están todas hipotecadas: tiene alguna para poder vender y salir de la insolvencia
            System.out.println("Turno: " + jugadores.get(turno).getNombre() + ", no eres solvente, no puedes acabar turno. " + "Tienes que vender o hipotecar hasta dejar de estar en negativo.\n" + "Tienes que conseguir " + Valor.formatear(deuda) + " de dinero.");
            return;
        }
        turno = (turno + 1) % jugadores.size();
        tirado = false;
        lanzamientos = 0;
        System.out.println("El jugador actual es " + jugadores.get(turno).getNombre() + ".");
    }

    // Métodos de la Parte 2

    private void edificar(String tipo) {
        Jugador current = jugadores.get(turno);
        Casilla actual = current.getAvatar().getLugar();

        if (!(actual instanceof Solar solar)) {
            System.out.println("No se puede edificar en esta casilla. Estás en " + actual.getNombre());
            return;
        }
        String resultado = solar.construirEdificio(current, tipo);
        System.out.println(resultado);
    }

    private void hipotecar(String nombreCasilla) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombreCasilla);

        if (!(c instanceof Solar solar)) {
            System.out.println("Solo se pueden hipotecar Solares.");
            return;
        }

        String resultado = solar.hipotecarPropiedad(current);
        System.out.println(resultado);
    }

    private void deshipotecar(String nombreCasilla) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombreCasilla);

        if (!(c instanceof Solar solar)) {
            System.out.println("Solo se pueden deshipotecar solares.");
            return;
        }

        String resultado = solar.deshipotecarPropiedad(current);
        System.out.println(resultado);
    }

    private void venderEdificios(String tipo, String nombreCasilla, int cantidad) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombreCasilla);

        if (!(c instanceof Solar solar)) {
            System.out.println("Solo se pueden vender edificios en solares.");
            return;
        }

        String resultado = solar.venderEdificios(current, tipo, cantidad);
        System.out.println(resultado);
    }

    // Métodos que realizan las acciones asociadas al comando 'listar edificios' y 'listar edificios <grupo>'
    private void listarEdificios() {
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if (!(c instanceof Solar s)) continue;

                if (s.getEdificios().isEmpty()) continue;
                System.out.println(s.getResumenEdificios());
            }
        }

    }

    private void listarEdificios(String grupoFiltro) {
        for (ArrayList<Casilla> casilla : tablero.getPosiciones()) {
            for (Casilla c : casilla) {
                if (c.getGrupo() == null) continue;
                if (c.getGrupo().getColorGrupo().equalsIgnoreCase(grupoFiltro)) {
                    System.out.println(c.getGrupo().getDescripcionGrupo());
                    break;
                }
            }
        }

    }

}