package partida;

import monopoly.*;

import java.util.ArrayList;

public class Avatar {
    // Enumeración de avatares válidos
    public enum TipoAvatar {
        COCHE("Coche"),
        ESFINGE("Esfinge"),
        SOMBRERO("Sombrero"),
        PELOTA("Pelota");

        private final String nombre;

        TipoAvatar(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }

        // Método para obtener TipoAvatar a partir del nombre
        public static TipoAvatar fromString(String nombre) {
            for (TipoAvatar tipo : TipoAvatar.values()) {
                if (tipo.getNombre().equalsIgnoreCase(nombre)) {
                    return tipo;
                }
            }
            return null;
        }
    }
    //Atributos
    private String id; //Identificador: una letra generada aleatoriamente.
    private TipoAvatar tipo; //Sombrero, Esfinge, Pelota, Coche
    private Jugador jugador; //Un jugador al que pertenece ese avatar.
    private Casilla lugar; //Los avatares se sitúan en casillas del tablero.

    //Constructor vacío
    public Avatar() {
    }

    /*Constructor principal. Requiere éstos parámetros:
     * Tipo del avatar, jugador al que pertenece, lugar en el que estará ubicado, y un arraylist con los
     * avatares creados (usado para crear un ID distinto del de los demás avatares).
     */
    public Avatar(String tipoAvatar, Jugador jugador, ArrayList<Avatar> avCreados) {
        this.jugador = jugador;
        generarId(avCreados);
        // Validación del tipo de avatar
        TipoAvatar tipoValidado = TipoAvatar.fromString(tipoAvatar);
        if (tipoValidado == null) {
            throw new IllegalArgumentException();
        }
        this.tipo = tipoValidado;
        this.lugar = null; // Inicialmente sin posición
    }

    //A continuación, tenemos otros métodos útiles para el desarrollo del juego.
    /*Método que permite mover a un avatar a una casilla concreta. Parámetros:
     * - Un array con las casillas del tablero. Se trata de un arrayList de arrayList de casillas (uno por lado).
     * - Un entero que indica el numero de casillas a moverse (será el valor sacado en la tirada de los dados).
     * EN ESTA VERSIÓN SUPONEMOS QUE valorTirada siempre es positivo.
     */
    public void moverAvatar(ArrayList<ArrayList<Casilla>> casillas, int valorTirada) {
        int currentPos = (this.lugar != null) ? this.lugar.getPosicion() : 1; // Si null, empieza en Salida (posición 1)
        int newPos = (currentPos + valorTirada) % 40;
        if (newPos == 0) newPos = 40;
        if (currentPos + valorTirada > 40) {
            this.jugador.sumarFortuna(Valor.SUMA_VUELTA);
            this.jugador.setVueltas(this.jugador.getVueltas() + 1);
            // registrar en estadísticas globales
            monopoly.StatsTracker.getInstance().registrarPasoSalida(this.jugador, Valor.SUMA_VUELTA);
        }

        Casilla newLugar = null;
        for (ArrayList<Casilla> lado : casillas) {
            for (Casilla c : lado) {
                if (c.getPosicion() == newPos) {
                    newLugar = c;
                    break;
                }
            }
            if (newLugar != null) break;
        }
        if (newLugar != null) {
            if (this.lugar != null) this.lugar.eliminarAvatar(this);
            newLugar.anhadirAvatar(this);
            this.lugar = newLugar;
        } else {
            throw new IllegalStateException("No se encontró una casilla válida para la posición " + newPos);
        }
    }

    /*Método que permite generar un ID para un avatar. Sólo lo usamos en esta clase (por ello es privado).
     * El ID generado será una letra mayúscula. Parámetros:
     * - Un arraylist de los avatares ya creados, con el objetivo de evitar que se generen dos ID iguales.
     */
    private void generarId(ArrayList<Avatar> avCreados) {
        char ch = (char) ((int) (Math.random() * 26) + 'A');
        String candidate = String.valueOf(ch);
        boolean used = true;
        while (used) {
            used = false;
            for (Avatar a : avCreados) {
                if (a.getId().equals(candidate)) {
                    used = true;
                    break;
                }
            }
            if (used) {
                ch = (char) ((int) (Math.random() * 26) + 'A');
                candidate = String.valueOf(ch);
            }
        }
        this.id = candidate;
    }

    // Getters
    public String getId() { return id; }
    public TipoAvatar getTipo() { return tipo; }
    public Jugador getJugador() { return jugador; }
    public Casilla getLugar() { return lugar; }
    public void setLugar(Casilla newLugar) {
        Casilla casillaAntigua = this.lugar;
        if (casillaAntigua != null) {
            casillaAntigua.eliminarAvatar(this);
        }
        if (newLugar != null) {
            newLugar.anhadirAvatar(this);
        }
        this.lugar = newLugar;
    }
}