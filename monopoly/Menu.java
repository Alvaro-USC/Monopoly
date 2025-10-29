package monopoly;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.Normalizer;
import java.util.stream.Collectors;
import partida.*;

public class Menu {

    //Atributos
    private ArrayList<Jugador> jugadores; //Jugadores de la partida.
    private ArrayList<Avatar> avatares; //Avatares en la partida.
    private int turno = 0; //Índice correspondiente a la posición en el arrayList del jugador (y el avatar) que tienen el turno
    private int lanzamientos; //Variable para contar el número de lanzamientos de un jugador en un turno.
    private Tablero tablero; //Tablero en el que se juega.
    private Dado dado1; //Dos dados para lanzar y avanzar casillas.
    private Dado dado2;
    private Jugador banca; //El jugador banca.
    private boolean tirado; //Booleano para comprobar si el jugador que tiene el turno ha tirado o no.
    private boolean solvente; //Booleano para comprobar si el jugador que tiene el turno es solvente, es decir, si ha pagado sus deudas.
    private boolean partidaIniciada;
    private MazoCartas mazoSuerte;
    private MazoCartas mazoCaja;
    private StatsTracker stats;

    private ArrayList<String> CmdsHistory = new ArrayList<>();

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
        if (archivoComandos != null)
        {
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

    // Añadir un Scanner
    private Scanner scanner = new Scanner(System.in);

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
        CmdsHistory.add(comando);
        String[] partes = comando.split("\\s+");

        if (partes.length == 0) return;

        if (comando.equalsIgnoreCase("guardar cmd")) {
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
        }


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
            }
            else System.out.println("Partida no iniciada. Use el comando: iniciar partida o lanzar dados");
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
        } else if (comando.toLowerCase().startsWith("comandos") &&  partes.length == 2) {
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
        } else if (comando.toLowerCase().startsWith("listar edificios") && partes.length == 3)
        {
            listarEdificios(partes[2]);
        } else if (comando.toLowerCase().startsWith("estadisticas"))
        {
            String[] parts = comando.split("\\s+");
            if (parts.length == 1)
            {
                System.out.println(stats.reporteGlobal(jugadores, tablero));
            } else
            {
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

        // --- Propiedades ---
        String props = "";
        String hipos = "";
        String edificiosTexto = "";

        for (Casilla c : j.getPropiedades()) {
            props += c.getNombre() + ", ";
            if (c instanceof Solar) {
                Solar s = (Solar) c;
                if (s.isHipotecada()) {
                    hipos += s.getNombre() + ", ";
                }
                for (Edificio e : s.getEdificios()) {
                    edificiosTexto += e.getId() + " (" + e.getTipo() + " en " + s.getNombre() + "), ";
                }
            }
        }

        if (props.endsWith(", ")) props = props.substring(0, props.length() - 2);
        if (hipos.endsWith(", ")) hipos = hipos.substring(0, hipos.length() - 2);
        if (edificiosTexto.endsWith(", ")) edificiosTexto = edificiosTexto.substring(0, edificiosTexto.length() - 2);

        if (hipos.isEmpty()) hipos = "-";
        if (edificiosTexto.isEmpty()) edificiosTexto = "-";

        System.out.println("{");
        System.out.println("  nombre: " + j.getNombre() + ",");
        System.out.println("  avatar: " + j.getAvatar().getId() + ",");
        System.out.println("  fortuna: " + Valor.formatear(j.getFortuna()) + ",");
        System.out.println("  propiedades: [" + props + "],");
        System.out.println("  hipotecas: [" + hipos + "],");
        System.out.println("  edificios: [" + edificiosTexto + "]");
        System.out.println("}");
    }



    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
     * Parámetro: id del avatar a describir.
     */
    private void descAvatar(String ID) {
        Avatar a = null;
        String casillaActual;
        for (Avatar av : avatares) {
            if (av.getId().equals(ID)) {
                a = av;
                break;
            }
        }
        if (a.getLugar() == null ) {
            casillaActual = "Salida";
        } else {
            casillaActual = a.getLugar().getNombre();
        }
        if (a != null) {
            System.out.println("{ \n id: " + ID + ", \n tipo: " + a.getTipo() + ", \n jugador: " + a.getJugador().getNombre() + ", \n casilla: " + casillaActual + "\n}");
        }
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
                        if (current.getFortuna() >= Valor.SALIR_CARCEL) {
                            current.sumarFortuna(-Valor.SALIR_CARCEL);
                            banca.sumarFortuna(Valor.SALIR_CARCEL);
                            current.setEnCarcel(false);
                            current.getAvatar().moverAvatar(tablero.getPosiciones(), tirada);
                            msg += " hasta " + current.getAvatar().getLugar().getNombre() + ". Paga para salir después de 3 intentos.";
                            System.out.println(msg);
                            solvente = current.getAvatar().getLugar().evaluarCasilla(current, banca, tirada);
                        } else {
                            System.out.println("No puedes pagar para salir, declara bancarrota.");
                        }
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
            } else if (solvente) {

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
                current.sumarFortuna(-Valor.SALIR_CARCEL);
                banca.sumarFortuna(Valor.SALIR_CARCEL);
                current.setEnCarcel(false);
                System.out.println(current.getNombre() + " paga 500.000€ y sale de la cárcel. Puede lanzar los dados.");
            } else {
                System.out.println("No tienes suficiente dinero.");
            }
        } else{
            System.out.println("No estás en la cárcel. Estás en " + current.getAvatar().getLugar().getNombre() + ", no encarcelado.");
        }
    }

    // Método que realiza las acciones asociadas al comando 'listar enventa'.
    private void listarVenta() {
        String s = "";
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if ((c.getTipo().equals("Solar") || c.getTipo().equals("Transporte") || c.getTipo().equals("Servicios")) && c.getDuenho().equals(banca)) {
                    s += c.casEnVenta() + ",\n";
                }
            }
        }
        System.out.println(s);
    }

    // Método que realiza las acciones asociadas al comando 'listar enventa [grupo]'.
    private void listarVenta(String grupoBuscado) {
        String s = "";
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if ((c.getTipo().equals("Solar") || c.getTipo().equals("Transporte") || c.getTipo().equals("Servicios")) && c.getDuenho().equals(banca)) {
                    String dato = c.casEnVenta(grupoBuscado);
                    if (!dato.equals("")) {
                        System.out.println(dato);
                        s += dato + ",\n";
                    }
                }
            }
        }
        System.out.println(s);
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    private void listarJugadores() {
        for (Jugador j : jugadores) {

            String props = "";
            String hipos = "";
            String edificiosTexto = "";

            for (Casilla c : j.getPropiedades()) {
                props += c.getNombre() + ", ";
                if (c instanceof Solar) {
                    Solar s = (Solar) c;
                    if (s.isHipotecada()) {
                        hipos += s.getNombre() + ", ";
                    }
                    for (Edificio e : s.getEdificios()) {
                        edificiosTexto += e.getId() + " (" + e.getTipo() + " en " + s.getNombre() + "), ";
                    }
                }
            }

            if (props.endsWith(", ")) props = props.substring(0, props.length() - 2);
            if (hipos.endsWith(", ")) hipos = hipos.substring(0, hipos.length() - 2);
            if (edificiosTexto.endsWith(", ")) edificiosTexto = edificiosTexto.substring(0, edificiosTexto.length() - 2);

            if (hipos.isEmpty()) hipos = "-";
            if (edificiosTexto.isEmpty()) edificiosTexto = "-";

            System.out.println("{");
            System.out.println("  nombre: " + j.getNombre() + ",");
            System.out.println("  avatar: " + j.getAvatar().getId() + ",");
            System.out.println("  fortuna: " + Valor.formatear(j.getFortuna()) + ",");
            System.out.println("  propiedades: [" + props + "],");
            System.out.println("  hipotecas: [" + hipos + "],");
            System.out.println("  edificios: [" + edificiosTexto + "]");
            System.out.println("},");
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
        turno = (turno + 1) % jugadores.size();
        tirado = false;
        solvente = true;
        lanzamientos = 0;
        System.out.println("El jugador actual es " + jugadores.get(turno).getNombre() + ".");
    }

    // -------------------------------------------------
    // Métodos de la Parte 2
    // -------------------------------------------------

    private void edificar(String tipo) {
        Jugador current = jugadores.get(turno);
        Casilla actual = current.getAvatar().getLugar();

        if (!(actual instanceof Solar)) {
            System.out.println("No se puede edificar en esta casilla. Estás en " + actual.getNombre());
            return;
        }

        Solar solar = (Solar) actual;

        if (!solar.getDuenho().equals(current)) {
            System.out.println("No eres el propietario de " + solar.getNombre());
            return;
        }

        if (solar.isHipotecada()) {
            System.out.println("No se puede edificar en una propiedad hipotecada.");
            return;
        }

        // No se puede edificar si alguna propiedad del grupo está hipotecada
        for (Casilla c : solar.getGrupo().getMiembros()) {
            if (c instanceof Solar s && s.isHipotecada()) {
                System.out.println("No se puede edificar en este grupo mientras haya propiedades hipotecadas.");
                return;
            }
        }

        // Contar edificios existentes
        int casas = 0, hoteles = 0, piscinas = 0, pistas = 0;
        for (Edificio e : solar.getEdificios()) {
            switch (e.getTipo().toLowerCase()) {
                case "casa": casas++; break;
                case "hotel": hoteles++; break;
                case "piscina": piscinas++; break;
                case "pista_deporte": pistas++; break;
            }
        }

        float coste;
        switch (tipo.toLowerCase()) {
            case "casa": coste = 600000; break;
            case "hotel": coste = 2200000; break;
            case "piscina": coste = 1000000; break;
            case "pista_deporte": coste = 2000000; break;
            default:
                System.out.println("Tipo de edificio no válido. Use casa, hotel, piscina o pista_deporte.");
                return;
        }

        if (current.getFortuna() < coste) {
            System.out.println("La fortuna de " + current.getNombre() + " no es suficiente para edificar un " + tipo + ".");
            return;
        }

        // Validaciones por tipo de edificio
        switch (tipo.toLowerCase()) {
            case "casa":
                if (casas >= 4) {
                    System.out.println("No se puede edificar más de 4 casas en " + solar.getNombre());
                    return;
                }
                break;
            case "hotel":
                if (casas < 4) {
                    System.out.println("No se puede edificar un hotel en " + solar.getNombre() + " sin 4 casas previas");
                    return;
                }
                if (hoteles >= 1) {
                    System.out.println("Ya hay un hotel construido en " + solar.getNombre());
                    return;
                }
                break;
            case "piscina":
                if (hoteles < 1) {
                    System.out.println("No se puede edificar una piscina en " + solar.getNombre() + " sin un hotel");
                    return;
                }
                if (piscinas >= 1) {
                    System.out.println("Ya hay una pisicina construida en " + solar.getNombre());
                    return;
                }
                break;
            case "pista_deporte":
                if (hoteles < 1) {
                    System.out.println("No se puede edificar una pista de deporte en " + solar.getNombre() + " sin un hotel");
                    return;
                }
                if (piscinas < 1) {
                    System.out.println("No se puede edificar una pista de deporte en " + solar.getNombre() + " sin una piscina");
                    return;
                }
                if (pistas >= 1) {
                    System.out.println("Ya hay una pista de deporte construida en " + solar.getNombre());
                    return;
                }
                break;
            default:
                System.out.println("Tipo de edificio no válido. Use casa, hotel, piscina o pista_deporte.");
                return;
        }

        // Crear y añadir el edificio
        Edificio nuevo = new Edificio(tipo, current, solar, solar.getGrupo().getColorGrupo(), coste);
        solar.addEdificio(nuevo);
        current.sumarFortuna(-coste);
        current.getEstadisticas().addDineroInvertido(coste);
        StatsTracker.getInstance().asegurarJugador(current);
        StatsTracker.getInstance().byPlayer.get(current.getNombre()).addDineroInvertido(coste);


        System.out.println("Se ha edificado un " + tipo + " en " + solar.getNombre() +
                ". La fortuna de " + current.getNombre() + " se reduce en " + Valor.formatear(coste) + "€.");
    }

    private void hipotecar(String nombreCasilla) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombreCasilla);

        if (!(c instanceof Solar)) {
            System.out.println("Solo se pueden hipotecar Solares.");
            return;
        }

        Solar s = (Solar) c;

        if (!s.getDuenho().equals(current)) {
            System.out.println("No eres el propietario de " + nombreCasilla);
            return;
        }

        if (s.isHipotecada()) {
            System.out.println("La propiedad ya está hipotecada.");
            return;
        }

        if (!s.getEdificios().isEmpty()) {
            System.out.println("Debes vender todos los edificios antes de hipotecar " + nombreCasilla);
            return;
        }

        float cantidad = s.getValor() / 2;
        s.setHipotecada(true);
        current.sumarFortuna(cantidad);


        System.out.println("Se hipoteca " + nombreCasilla + " por " + Valor.formatear(cantidad) + "€.");
    }

    private void deshipotecar(String nombreCasilla) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombreCasilla);

        if (!(c instanceof Solar)) {
            System.out.println("Solo se pueden deshipotecar solares.");
            return;
        }

        Solar s = (Solar) c;

        if (!s.isHipotecada()) {
            System.out.println("La propiedad no está hipotecada.");
            return;
        }

        float cantidad = s.getValor() / 2;

        if (current.getFortuna() < cantidad) {
            System.out.println("No tienes suficiente dinero para deshipotecar esta propiedad.");
            return;
        }

        current.sumarFortuna(-cantidad);
        s.setHipotecada(false);
        System.out.println("Se deshipoteca " + nombreCasilla + " pagando " + Valor.formatear(cantidad) + "€.");
    }

    private void venderEdificios(String tipo, String nombreCasilla, int cantidad) {
        Jugador current = jugadores.get(turno);
        Casilla c = tablero.encontrar_casilla(nombreCasilla);

        if (!(c instanceof Solar)) {
            System.out.println("Solo se pueden vender edificios en solares.");
            return;
        }

        Solar s = (Solar) c;
        if (!s.getDuenho().equals(current)) {
            System.out.println("No eres el propietario de " + nombreCasilla);
            return;
        }

        int disponibles = (int) s.getEdificios().stream()
                .filter(e -> e.getTipo().equalsIgnoreCase(tipo)).count();

        if (disponibles == 0) {
            System.out.println("No hay edificios de tipo " + tipo + " en " + nombreCasilla);
            return;
        }

        int vender = Math.min(disponibles, cantidad);
        float precioVenta = 0;
        // Índices de columnas de Valor.PRECIO_EDIFICIOS
        // 0 = casa, 1 = hotel, 2 = piscina, 3 = pista_deporte
        int col = switch (tipo.toLowerCase()) {
            case "casa" -> 0;
            case "hotel" -> 1;
            case "piscina" -> 2;
            case "pista_deporte" -> 3;
            default -> -1;
        };
        if (col == -1) {
            System.out.println("Tipo de edificio no válido: " + tipo);
            return;
        }
        precioVenta = Valor.PRECIO_EDIFICIOS[s.getIdSolar()][col];

        float total = vender * precioVenta;
        s.eliminarEdificios(tipo, vender);
        current.sumarFortuna(total);
        StatsTracker.getInstance().asegurarJugador(current);
        StatsTracker.getInstance().byPlayer.get(current.getNombre()).addDineroInvertido(-total);

        System.out.println("Vendidas " + vender + " " + tipo + "(s) en " + nombreCasilla + " por " + Valor.formatear(total) + "€.");
    }

    // Método que realiza las acciones asociadas al comando 'listar edificios' y 'listar edificios <grupo>'
    private void listarEdificios() {
        listarEdificios(null);
    }

    private void listarEdificios(String grupoFiltro) {
        boolean filtrado = (grupoFiltro != null);
        boolean encontrados = false;

        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if (!(c instanceof Solar)) continue;
                Solar s = (Solar) c;
                if (s.getEdificios().isEmpty() && !filtrado) continue;

                String grupo = (s.getGrupo() != null) ? s.getGrupo().getColorGrupo() : "sin-grupo";
                if (filtrado && !grupo.equalsIgnoreCase(grupoFiltro)) continue;

                encontrados = true;

                // Inicializamos el mapa con los tipos de edificio y contador 0
                Map<String, Integer> contadorEdificios = new HashMap<>();
                contadorEdificios.put("casa", 0);
                contadorEdificios.put("hotel", 0);
                contadorEdificios.put("piscina", 0);
                contadorEdificios.put("pista_deporte", 0);

                // Contamos los edificios existentes
                for (Edificio e : s.getEdificios()) {
                    String tipo = e.getTipo().toLowerCase();
                    if (contadorEdificios.containsKey(tipo)) {
                        contadorEdificios.put(tipo, contadorEdificios.get(tipo) + 1);
                    }
                }

                // Generamos los textos (si el contador es 0 mostramos "-")
                String casasTxt = contadorEdificios.get("casa") == 0 ? "-" : String.valueOf(contadorEdificios.get("casa"));
                String hotelesTxt = contadorEdificios.get("hotel") == 0 ? "-" : String.valueOf(contadorEdificios.get("hotel"));
                String piscinasTxt = contadorEdificios.get("piscina") == 0 ? "-" : String.valueOf(contadorEdificios.get("piscina"));
                String pistasTxt = contadorEdificios.get("pista_deporte") == 0 ? "-" : String.valueOf(contadorEdificios.get("pista_deporte"));

                // Mostrar la información del solar
                System.out.println("{");
                System.out.println("  propiedad: " + s.getNombre() + ",");
                System.out.println("  hoteles: " + hotelesTxt + ",");
                System.out.println("  casas: " + casasTxt + ",");
                System.out.println("  piscinas: " + piscinasTxt + ",");
                System.out.println("  pista_deporte: " + pistasTxt + ",");
                System.out.println("  alquiler: " + Valor.formatear(s.getImpuesto()));
                System.out.println("}");

                // Calcular y mostrar cuántos edificios faltan por construir
                int casasFaltan = 4 - contadorEdificios.get("casa");
                int hotelesFaltan = 1 - contadorEdificios.get("hotel");
                int piscinasFaltan = 1 - contadorEdificios.get("piscina");
                int pistasFaltan = 1 - contadorEdificios.get("pista_deporte");

                StringBuilder faltan = new StringBuilder();

                if (casasFaltan > 0)
                    faltan.append(casasFaltan).append(" casa(s), ");
                if (hotelesFaltan > 0)
                    faltan.append(hotelesFaltan).append(" hotel(es), ");
                if (piscinasFaltan > 0)
                    faltan.append(piscinasFaltan).append(" piscina(s), ");
                if (pistasFaltan > 0)
                    faltan.append(pistasFaltan).append(" pista(s) de deporte, ");

                // Si se añadió algo, eliminamos la última coma y espacio
                if (faltan.length() > 0) {
                    // Quita la coma y espacio final
                    faltan.setLength(faltan.length() - 2);
                    System.out.println("En el solar '" + s.getNombre() + "' faltan por construir: " + faltan);
                } else {
                    System.out.println("En el solar '" + s.getNombre() + "' no falta ningún edificio por construir.");
                }

            }
        }

        if (!encontrados) {
            if (filtrado)
                System.out.println("No hay edificios construidos en el grupo " + grupoFiltro + ".");
            else
                System.out.println("No hay edificios construidos.");
        }
    }

}