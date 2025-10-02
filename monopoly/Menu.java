package monopoly;

import java.util.ArrayList;
import java.util.Scanner;
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

        // Mensaje de bienvenida
        System.out.println("Bienvenido a MonopolyETSE.");

        // Procesar archivo de comandos si se proporciona
        if (archivoComandos != null)
        {
            ComandoArchivo comandoArchivo = new ComandoArchivo(this, archivoComandos);
            comandoArchivo.procesarComandos();
        }
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

        // Mensaje de bienvenida
        System.out.println("Bienvenido a MonopolyETSE. Introduce un comando (ejemplo: 'crear jugador <nombre> <tipoAvatar>' o 'salir' para terminar).");

        // Iniciar el bucle de comandos
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
        if (jugadores.size() > 1){
            partidaIniciada  = true;
            System.out.println("Partida iniciada.");
        } else {
            System.out.println("Se necesitan como mínimo 2 jugadores para iniciar la partida.");
        }

    }

    /*Método que interpreta el comando introducido y toma la acción correspondiente.
     * Parámetro: cadena de caracteres (el comando).
     */
    public void analizarComando(String comando) {
        String[] partes = comando.split("\\s+");
        if (partes.length == 0) return;

        if (comando.toLowerCase().startsWith("crear jugador") && partes.length == 4) {
            String nombre = partes[2];
            String tipoAvatar = partes[3];

            boolean used = false;
            for (Jugador j : jugadores) {
                if (j.getNombre().equals(nombre)) {
                    used = true;
                    break;
                }
            }

            if (nombre.equalsIgnoreCase(banca.getNombre().toLowerCase())) used = true;

            if (!used) {
                Casilla inicio = tablero.encontrar_casilla("Salida");
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
                    System.out.println("No se creó el jugador. Avatar " + tipoAvatar + " no es válido. Use Coche, Esfinge, Sombrero o Pelota.");
                }

            } else {
                System.out.println("Nombre ya usado.");
            }
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

        } else if (comando.toLowerCase().startsWith("comprar ") && partes.length == 2) {
            if (partidaIniciada) comprar(partes[1]);
            else System.out.println("No se ha iniciado la partida. Use el comando: iniciar partida o lanzar dados");
        } else if (comando.equalsIgnoreCase("salir carcel")) {
            if (partidaIniciada) salirCarcel();
            else System.out.println("No se ha iniciado la partida. Use el comando: iniciar partida o lanzar dados");
        } else if (comando.equalsIgnoreCase("listar enventa")) {
            listarVenta();
            System.out.println(tablero.toString());
        } else if (comando.equalsIgnoreCase("acabar turno")) {
            if (partidaIniciada) acabarTurno();
            else System.out.println("No se ha iniciado la partida. Use el comando: iniciar partida o lanzar dados");
        } else if (comando.equalsIgnoreCase("ver tablero")) {
            System.out.println(tablero.toString());
        } else if (comando.toLowerCase().startsWith("comandos")) {
            ComandoArchivo comandoArchivo = new ComandoArchivo(this, partes[1]);
            comandoArchivo.procesarComandos();
        } else {
            System.out.println("Comando no reconocido.");
            System.out.println("Comandos disponibles:\n\tcrear jugador <nombre> <avatar>");
            System.out.println("\tjugador\n\tlistar jugadores\n\tlistar avatares\n\tdescribir jugador <nombre>\n\tdescribir avatar <nombre>");
            System.out.println("\tdescribir <nombre>\n\tlanzar dados\n\tcomprar <casilla>\n\tsalir carcel\n\tlistar enventa\n\tacabar turno\n\tver tablero");
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
        if (j != null) {
            String props = "";
            for (Casilla p : j.getPropiedades()) {
                props += p.getNombre() + ", ";
            }
            if (props.endsWith(", ")) props = props.substring(0, props.length() - 2);
            System.out.println("{ \n nombre: " + j.getNombre() + ", \n avatar: " + j.getAvatar().getId() + ", \n fortuna: " + j.getFortuna() + ", \n propiedades: [" + props + "] \n hipotecas: - \n edificios: - \n}");
        }
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
        if (tirado && !forced) {
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
        String msg = "El avatar " + current.getAvatar().getId() + " avanza " + tirada + " posiciones, desde " + current.getAvatar().getLugar().getNombre();

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
            acabarTurno();
        } else {
            current.getAvatar().moverAvatar(tablero.getPosiciones(), tirada);
            String destino = current.getAvatar().getLugar().getNombre();
            msg += " hasta " + current.getAvatar().getLugar().getNombre() + ".";
            System.out.println(msg);
            solvente = current.getAvatar().getLugar().evaluarCasilla(current, banca, tirada);
            if (!solvente) {
                System.out.println("El jugador no es solvente. Debes hipotecar o declarar bancarrota.");
                acabarTurno();
            } else if (destino.equalsIgnoreCase("Carcel")) { // Verificar si aterrizó en Cárcel
                current.encarcelar(tablero.getPosiciones());
                System.out.println("Has aterrizado en Carcel. Estás encarcelado.");
                acabarTurno();
            } else if (destino.equalsIgnoreCase("IrCarcel")) {
                current.encarcelar(tablero.getPosiciones());
                System.out.println("Has caído en IrCarcel. Vas directo a Carcel.");
                acabarTurno();
            }
            tirado = true;

            if (v1 == v2) { // Incrementar lanzamientos solo si son dobles
                lanzamientos++;
                tirado = false; // Permitir otra tirada con dobles
                if (lanzamientos == 3) {
                    current.encarcelar(tablero.getPosiciones());
                    System.out.println("Tres dobles, vas a la cárcel.");
                    lanzamientos = 0;
                    acabarTurno();
                }
            } else if (solvente) {
                acabarTurno(); // Cambiar turno si no hay dobles y es solvente
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
            System.out.println("No estás en esa casilla.");
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
            System.out.println("No estás en la cárcel. Estás en " + current.getAvatar().getLugar().getNombre());
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

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    private void listarJugadores() {
        String s = "";
        for (Jugador j : jugadores) {
            String props = "";
            for (Casilla p : j.getPropiedades()) {
                props += p.getNombre() + ", ";
            }
            if (props.endsWith(", ")) props = props.substring(0, props.length() - 2);
            s += "{ \n nombre: " + j.getNombre() + ", \n avatar: " + j.getAvatar().getId() + ", \n fortuna: " + j.getFortuna() + ", \n propiedades: [" + props + "] \n hipotecas: - \n edificios: - \n},\n";
        }
        System.out.println(s);
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

}