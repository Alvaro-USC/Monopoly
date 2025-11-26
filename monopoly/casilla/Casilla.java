package monopoly.casilla;

import monopoly.Grupo;
import monopoly.StatsTracker;
import monopoly.Tablero;
import monopoly.Valor;
import monopoly.excepcion.FondosInsuficientesException;
import partida.Avatar;
import partida.Jugador;

import java.util.ArrayList;

public abstract class Casilla {

    private final ArrayList<Avatar> avatares; //Avatares que están situados en la casilla.
    protected Jugador duenho; //Dueño de la casilla (por defecto sería la banca).
    //Atributos:
    private String tipo;
    private String nombre; //Nombre de la casilla
    private float valor; //Valor de esa casilla (en la mayoría será valor de compra, en la casilla parking se usará como el bote).
    private int posicion; //Posición que ocupa la casilla en el tablero (entero entre 1 y 40).
    private Grupo grupo; //Grupo al que pertenece la casilla (si es solar).
    private float impuesto; //Cantidad a pagar por caer en la casilla: el alquiler en solares/servicios/transportes o impuestos.
    private Tablero tablero; // Referencia al tablero

    //Constructores:
    public Casilla() {
        this.avatares = new ArrayList<>();
    }//Parámetros vacíos

    /*Constructor para casillas tipo Solar, Servicios o Transporte:
     * Parámetros: nombre casilla, tipo (debe ser solar, serv. o transporte), posición en el tablero, valor y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador duenho) {
        this();
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.valor = valor;
        this.duenho = duenho;
        this.impuesto = 0;
    }

    /*Constructor utilizado para inicializar las casillas de tipo IMPUESTOS.
     * Parámetros: nombre, posición en el tablero, impuesto establecido y dueño.
     */
    public Casilla(String nombre, int posicion, float impuesto, Jugador duenho) {
        this();
        this.nombre = nombre;
        this.tipo = "Impuesto";
        this.posicion = posicion;
        this.impuesto = impuesto;
        this.duenho = duenho;
        this.valor = 0;
    }

    /*Constructor utilizado para crear las otras casillas (Suerte, Caja de comunidad y Especiales):
     * Parámetros: nombre, tipo de la casilla (será uno de los que queda), posición en el tablero y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, Jugador duenho) {
        this();
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.duenho = duenho;
        this.valor = 0;
        this.impuesto = 0;
    }

    //Método utilizado para añadir un avatar al array de avatares en casilla.
    public void anhadirAvatar(Avatar av) {
        if (!avatares.contains(av)) {
            avatares.add(av);
        }
    }

    //Método utilizado para eliminar un avatar del array de avatares en casilla.
    public void eliminarAvatar(Avatar av) {avatares.remove(av);}

    public boolean procesarPago(Jugador actual, float toPay) throws FondosInsuficientesException {
        boolean solv = true; // Asumimos solvencia inicial

        if (actual.getFortuna() < toPay) {
            solv = false;
            throw new FondosInsuficientesException(" / procesar este pago. Debes hipotecar una propiedad o declararte en bancarrota.\nVas a estar en negativo.");
        }

        actual.sumarGastos(toPay);
        getDuenho().sumarFortuna(toPay);

        System.out.println("Se han pagado " + Valor.formatear(toPay) + " € de alquiler.");

        // 'this' se refiere a la instancia de la subclase (Solar, Transporte, etc.)
        StatsTracker.getInstance().registrarPagoAlquiler(actual, toPay);
        StatsTracker.getInstance().registrarCobroAlquiler(getDuenho(), toPay);
        StatsTracker.getInstance().registrarAlquiler(this, toPay);

        return solv;
    }

    /*Método para evaluar qué hacer en una casilla concreta. Parámetros:
     * - Jugador cuyo avatar está en esa casilla.
     * - La banca (para ciertas comprobaciones).
     * - El valor de la tirada: para determinar impuesto a pagar en casillas de servicios.
     * Valor devuelto: true en caso de ser solvente (es decir, de cumplir las deudas), y false
     * en caso de no cumplirlas.*/
    public abstract boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada);

    /*Método para añadir valor a una casilla. Utilidad:
     * - Sumar valor a la casilla de parking.
     * - Sumar valor a las casillas de solar al no comprarlas tras cuatro vueltas de todos los jugadores.
     * Este método toma como argumento la cantidad a añadir del valor de la casilla.*/
    public void sumarValor(float suma) {
        this.valor += suma;
    }

    /*Método para mostrar información sobre una casilla.
     * Devuelve una cadena con información específica de cada tipo de casilla.*/
    public String infoCasilla() {
        String info = "{ \n tipo: " + tipo;
        switch (tipo) {
            case "Solar" ->
                    info += ", \n grupo: " + (grupo != null ? grupo.getColorGrupo() : "") + ", \n propietario: " + duenho.getNombre() + ", \n valor: " + Valor.formatear(valor) + ", \n alquiler: " + Valor.formatear(impuesto);

            // Añadir valores de edificios en futuras partes
            case "Impuesto" -> info += ", \n apagar: " + Valor.formatear(impuesto);
            case "Especial" -> {
                if (nombre.equals("Parking")) {
                    info += ", \n bote: " + Valor.formatear(valor);
                    StringBuilder jugs = new StringBuilder();
                    for (Avatar a : avatares) {
                        jugs.append(a.getJugador().getNombre()).append(", ");
                    }
                    if (jugs.toString().endsWith(", ")) jugs = new StringBuilder(jugs.substring(0, jugs.length() - 2));
                    info += ", \n jugadores: [" + jugs + "]";
                } else if (nombre.equals("Carcel")) {
                    info += ", \n salir: 500.000";
                    StringBuilder jugs = new StringBuilder();
                    for (Avatar a : avatares) {
                        jugs.append("[").append(a.getJugador().getNombre()).append(",").append(a.getJugador().getTiradasCarcel()).append("] ");
                    }
                    info += ", \n jugadores: " + jugs;
                }
            }
        }
        info += "\n}";
        return info;
    }

    /* Método para mostrar información de una casilla en venta.
     * Valor devuelto: texto con esa información.
     */
    public String casEnVenta() {
        if (tipo.equals("Solar") || tipo.equals("Transporte") || tipo.equals("Servicios")) {
            String g = (grupo != null ? " \ngrupo: " + grupo.getColorGrupo() + "," : "");
            return "{ \n nombre: " + this.getNombre() + "\ntipo: " + tipo + "," + g + " \n valor: " + valor + "\n}";
        }
        return "";
    }

    public String casEnVenta(String grupoBuscado) {
        if (tipo.equals("Solar") || tipo.equals("Transporte") || tipo.equals("Servicios")) {
            String g = "";
            String colorGr = (grupo != null ? grupo.getColorGrupo() : "");

            if (colorGr.equals(grupoBuscado)) {
                g += (grupo != null ? " \ngrupo: " + colorGr : "");
                return "{ \n nombre: " + this.getNombre() + "\n tipo: " + tipo + "," + g + " \n valor: " + Valor.formatear(valor) + "\n}";
            }
        }
        return "";
    }

    public String representacionColoreada() {
        String rep = nombre;
        String color = ""; // Color por defecto (sin color)
        // Aplicar color si es un solar y tiene grupo
        if (tipo.equals("Solar") && grupo != null) {
            color = grupo.getAnsiColor();
            rep = color + rep; // + Valor.RESET;
        }
        if (!avatares.isEmpty()) {
            StringBuilder avatars = new StringBuilder("&");
            for (Avatar a : avatares) {
                avatars.append(a.getId());
            }
            rep += color + avatars;
        }

        // Añadir RESET solo si se aplicó color
        if (!color.isEmpty()) {
            rep += Valor.RESET;
        }
        return rep;
    }

    // Getters y setters adicionales
    public String getNombre() {return nombre;}

    public String getTipo() {return this.getClass().getSimpleName();}

    public float getValor() {return valor;}

    // Nuevo método para establecer el valor directamente
    public void setValor(float valor) {
        this.valor = valor;
    }

    public int getPosicion() {return posicion;}

    public Jugador getDuenho() {return duenho;}

    public void setDuenho(Jugador duenho) {this.duenho = duenho;}

    public Grupo getGrupo() {return grupo;}

    public void setGrupo(Grupo grupo) {this.grupo = grupo;}

    public float getImpuesto() {return impuesto;}

    public void setImpuesto(float impuesto) {this.impuesto = impuesto;}

    public ArrayList<Avatar> getAvatares() {return avatares;}

    public Tablero getTablero() {return tablero;}

    public void setTablero(Tablero tablero) {this.tablero = tablero;}

    // TODO: a lo mejor hay que hacer solo un isEmpty(), que la función no tome parámetros
    public boolean estaAvatar(Avatar avatar) {
        return this.getAvatares().contains(avatar);
    }

    public int frecuenciaVisita() {
        return StatsTracker.getInstance().frecuenciaVisitada(this.getNombre());
    }

    @Override
    public String toString() {
        return this.infoCasilla();
    }
}
