package partida;

import java.util.ArrayList;

import monopoly.*;

public class Jugador {

    //Atributos:
    private String nombre; //Nombre del jugador
    private Avatar avatar; //Avatar que tiene en la partida.
    private float fortuna; //Dinero que posee.
    private float gastos; //Gastos realizados a lo largo del juego.
    private boolean enCarcel; //Será true si el jugador está en la carcel
    private int tiradasCarcel; //Cuando está en la carcel, contará las tiradas sin éxito que ha hecho allí para intentar salir (se usa para limitar el numero de intentos).
    private int vueltas; //Cuenta las vueltas dadas al tablero.
    private ArrayList<Casilla> propiedades; //Propiedades que posee el jugador.
    private PlayerStats estadisticas; // nuevas estadísticas por jugador

    //Constructor vacío. Se usará para crear la banca.
    public Jugador() {
        this.propiedades = new ArrayList<>();
        this.fortuna = Valor.FORTUNA_BANCA;
        this.nombre = "Banca";
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.gastos = 0;
        this.estadisticas = new PlayerStats();
    }

    /*Constructor principal. Requiere parámetros:
     * Nombre del jugador, tipo del avatar que tendrá, casilla en la que empezará y ArrayList de
     * avatares creados (usado para dos propósitos: evitar que dos jugadores tengan el mismo nombre y
     * que dos avatares tengan mismo ID). Desde este constructor también se crea el avatar.
     */
    public Jugador(String nombre, String tipoAvatar, ArrayList<Avatar> avCreados) {
        this.nombre = nombre;
        this.fortuna = 15_000_000f; // Valor inicial según Parte 1
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.propiedades = new ArrayList<>();
        // Crear avatar con validación
        try {
            this.avatar = new Avatar(tipoAvatar, this, avCreados);
        } catch (IllegalArgumentException e) {
            this.avatar = null; // No se crea el jugador si el avatar no es válido
        }
        this.estadisticas = new PlayerStats();
    }

    // Método para generar un ID único (ejemplo simple)
    private String generarIdUnico() {
        char ch = (char) ((int) (Math.random() * 26) + 'A');
        return String.valueOf(ch);
    }

    //Otros métodos:
    //Método para añadir una propiedad al jugador. Como parámetro, la casilla a añadir.
    public void anhadirPropiedad(Casilla casilla) {
        propiedades.add(casilla);
    }

    //Método para eliminar una propiedad del arraylist de propiedades de jugador.
    public void eliminarPropiedad(Casilla casilla) {
        propiedades.remove(casilla);
    }

    //Método para añadir fortuna a un jugador
    //Como parámetro se pide el valor a añadir. Si hay que restar fortuna, se pasaría un valor negativo.
    public void sumarFortuna(float valor) {
        this.fortuna += valor;
    }

    //Método para sumar gastos a un jugador.
    //Parámetro: valor a añadir a los gastos del jugador (será el precio de un solar, impuestos pagados...).
    public void sumarGastos(float valor) {
        this.gastos += valor;
    }

    /*Método para establecer al jugador en la cárcel.
     * Se requiere disponer de las casillas del tablero para ello (por eso se pasan como parámetro).*/
    public void encarcelar(ArrayList<ArrayList<Casilla>> pos) {
        this.enCarcel = true;
        this.tiradasCarcel = 0;
        Casilla carcel = null;
        for (ArrayList<Casilla> lado : pos) {
            for (Casilla c : lado) {
                if (c.getNombre().equals("Carcel")) {
                    carcel = c;
                    break;
                }
            }
            if (carcel != null) break;
        }

        this.avatar.setLugar(carcel);
        this.avatar.getLugar().eliminarAvatar(this.avatar);
        carcel.anhadirAvatar(this.avatar);
    }

    // Getters y setters adicionales
    public boolean isEnCarcel() { return enCarcel; }
    public void setEnCarcel(boolean enCarcel) { this.enCarcel = enCarcel; }
    public int getTiradasCarcel() { return tiradasCarcel; }
    public void setTiradasCarcel(int tiradasCarcel) { this.tiradasCarcel = tiradasCarcel; }
    public int getVueltas() { return vueltas; }
    public void setVueltas(int vueltas) { this.vueltas = vueltas; }
    public String getNombre() { return nombre; }
    public Avatar getAvatar() { return avatar; }
    public float getFortuna() { return fortuna; }
    public float getGastos() { return gastos; }
    public ArrayList<Casilla> getPropiedades() { return propiedades; }
    public PlayerStats getEstadisticas() { return estadisticas; }
}