package partida;

import monopoly.casilla.Casilla;
import monopoly.edificio.Edificio;
import monopoly.casilla.propiedad.Solar;
import monopoly.Valor;
import monopoly.excepcion.AccionInvalidaException;

import java.util.ArrayList;

public class Jugador {

    //Atributos:
    private final String nombre; //Nombre del jugador
    private final ArrayList<Casilla> propiedades; //Propiedades que posee el jugador.
    private final PlayerStats estadisticas; // nuevas estadísticas por jugador
    private Avatar avatar; //Avatar que tiene en la partida.
    private float fortuna; //Dinero que posee.
    private float gastos; //Gastos realizados a lo largo del juego.
    private boolean enCarcel; //Será true si el jugador está en la carcel
    private int tiradasCarcel; //Cuando está en la carcel, contará las tiradas sin éxito que ha hecho allí para intentar salir (se usa para limitar el numero de intentos).
    private int vueltas; //Cuenta las vueltas dadas al tablero.

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
        this.fortuna = Valor.FORTUNA_INICIAL; // Valor inicial según Parte 1
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.propiedades = new ArrayList<>();
        // Crear avatar con validación
        try {
            this.avatar = new Avatar(tipoAvatar, this, avCreados);
        } catch (AccionInvalidaException e) {
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
    public void anhadirPropiedad(Casilla casilla) {propiedades.add(casilla);}

    //Método para eliminar una propiedad del arraylist de propiedades de jugador.
    public void eliminarPropiedad(Casilla casilla) {
        propiedades.remove(casilla);
    }

    //Método para añadir fortuna a un jugador
    //Como parámetro se pide el valor a añadir. Si hay que restar fortuna, se pasaría un valor negativo.
    public void sumarFortuna(float valor) {fortuna += valor;}

    //Método para sumar gastos a un jugador.
    //Parámetro: valor a añadir a los gastos del jugador (será el precio de un solar, impuestos pagados...).
    public void sumarGastos(float valor) {
        gastos += valor;
        fortuna -= valor;
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

    public String getDescripcionDetallada() {
        StringBuilder props = new StringBuilder();
        StringBuilder hipos = new StringBuilder();
        StringBuilder edificiosTexto = new StringBuilder();

        // Lógica para iterar y recopilar los datos
        for (Casilla c : this.getPropiedades()) {
            props.append(c.getNombre()).append(", ");
            if (c instanceof Solar s) { // Java 14+ pattern matching
                if (s.isHipotecada()) {
                    hipos.append(s.getNombre()).append(", ");
                }
                for (Edificio e : s.getEdificios()) {
                    edificiosTexto.append(e.getId()).append(" (").append(e.getTipo()).append(" en ").append(s.getNombre()).append("), ");
                }
            }
        }

        // Lógica para limpiar las comas finales
        String propsStr = props.length() > 2 ? props.substring(0, props.length() - 2) : "-";
        String hiposStr = hipos.length() > 2 ? hipos.substring(0, hipos.length() - 2) : "-";
        String edificiosStr = edificiosTexto.length() > 2 ? edificiosTexto.substring(0, edificiosTexto.length() - 2) : "-";

        // Formato final (la parte de la presentación)
        return "{\n" + "  nombre: " + this.getNombre() + ",\n" + "  avatar: " + this.getAvatar().getId() + ",\n" + "  fortuna: " + Valor.formatear(this.getFortuna()) + ",\n" + "  propiedades: [" + propsStr + "],\n" + "  hipotecas: [" + hiposStr + "],\n" + "  edificios: [" + edificiosStr + "]\n" + "}";
    }

    // Método para retornar todas las propiedades a la banca
    public void declararBancarrota(Jugador banca) {
        for (Casilla c : this.getPropiedades()) {
            c.setDuenho(banca);
        }
    }

    // Getters y setters adicionales
    public boolean isEnCarcel() {return enCarcel;}

    public void setEnCarcel(boolean enCarcel) {this.enCarcel = enCarcel;}

    public int getTiradasCarcel() {return tiradasCarcel;}

    public void setTiradasCarcel(int tiradasCarcel) {this.tiradasCarcel = tiradasCarcel;}

    public int getVueltas() {return vueltas;}

    public void setVueltas(int vueltas) {this.vueltas = vueltas;}

    public String getNombre() {return nombre;}

    public Avatar getAvatar() {return avatar;}

    public float getFortuna() {return fortuna;}

    public float getGastos() {return gastos;}

    public ArrayList<Casilla> getPropiedades() {return propiedades;}

    public PlayerStats getEstadisticas() {return estadisticas;}
}