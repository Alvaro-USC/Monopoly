package monopoly;

import partida.*;
import java.util.ArrayList;

public class Casilla {

    //Atributos:
    private String tipo; //Tipo de casilla (Solar, Especial, Transporte, Servicios, Comunidad, Suerte y Impuesto).
    private String nombre; //Nombre de la casilla
    private float valor; //Valor de esa casilla (en la mayoría será valor de compra, en la casilla parking se usará como el bote).
    private int posicion; //Posición que ocupa la casilla en el tablero (entero entre 1 y 40).
    private Jugador duenho; //Dueño de la casilla (por defecto sería la banca).
    private Grupo grupo; //Grupo al que pertenece la casilla (si es solar).
    private float impuesto; //Cantidad a pagar por caer en la casilla: el alquiler en solares/servicios/transportes o impuestos.
    private float hipoteca; //Valor otorgado por hipotecar una casilla
    private ArrayList<Avatar> avatares; //Avatares que están situados en la casilla.
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
        this.hipoteca = 0;
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
        this.hipoteca = 0;
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
        this.hipoteca = 0;
    }

    //Método utilizado para añadir un avatar al array de avatares en casilla.
    public void anhadirAvatar(Avatar av) {
        if (!avatares.contains(av)) {
            avatares.add(av);
        }
    }

    //Método utilizado para eliminar un avatar del array de avatares en casilla.
    public void eliminarAvatar(Avatar av) {
        avatares.remove(av);
    }

    /*Método para evaluar qué hacer en una casilla concreta. Parámetros:
     * - Jugador cuyo avatar está en esa casilla.
     * - La banca (para ciertas comprobaciones).
     * - El valor de la tirada: para determinar impuesto a pagar en casillas de servicios.
     * Valor devuelto: true en caso de ser solvente (es decir, de cumplir las deudas), y false
     * en caso de no cumplirlas.*/
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        boolean solv = true;
        if (tipo.equals("Solar") || tipo.equals("Transporte") || tipo.equals("Servicios")) {
            if (!duenho.equals(banca) && !duenho.equals(actual)) {
                float toPay = 0;
                if (tipo.equals("Solar")) {
                    toPay = impuesto;
                    if (grupo != null && grupo.esDuenhoGrupo(this.duenho)) {
                        toPay *= 2;
                    }
                } else if (tipo.equals("Transporte")) {
                    toPay = impuesto; // En parte 1, alquiler fijo
                } else if (tipo.equals("Servicios")) {
                    toPay = 4 * tirada * Valor.FACTOR_SERVICIO; // En parte 1, siempre x4
                }
                if (actual.getFortuna() < toPay) {
                    solv = false;
                } else {
                    actual.sumarFortuna(-toPay);
                    duenho.sumarFortuna(toPay);
                    System.out.println("Se han pagado " + toPay + " € de alquiler.");
                }
            }
        } else if (tipo.equals("Impuesto")) {
            float toPay = impuesto;
            if (actual.getFortuna() < toPay) {
                solv = false;
            } else {
                actual.sumarFortuna(-toPay);
                Casilla parking = tablero.encontrar_casilla("Parking");
                parking.sumarValor(toPay);
                System.out.println("El jugador paga " + toPay + "€ que se depositan en el Parking.");
            }
        } else if (tipo.equals("Especial")) {
            if (nombre.equals("Parking")) {
                float bote = valor;
                actual.sumarFortuna(bote);
                valor = 0;
                System.out.println("El jugador recibe " + bote + "€.");
            } else if (nombre.equals("IrCarcel")) {
                Casilla carcel = tablero.encontrar_casilla("Carcel");
                if (carcel == null) {
                    System.out.println("Error: No se encontró la casilla Carcel.");
                } else {
                    Casilla lugarAnterior = actual.getAvatar().getLugar(); // Guardar la casilla anterior
                    if (lugarAnterior != null) {
                        lugarAnterior.eliminarAvatar(actual.getAvatar()); // Eliminar de la casilla anterior
                    }
                    actual.getAvatar().setLugar(carcel);
                    carcel.anhadirAvatar(actual.getAvatar());
                    actual.setEnCarcel(true);
                    actual.setTiradasCarcel(0);
                    System.out.println("El avatar se coloca en la casilla de Carcel.");
                }
            }
        } // Suerte y Comunidad sin acción en parte 1
        return solv;
    }

    /*Método usado para comprar una casilla determinada. Parámetros:
     * - Jugador que solicita la compra de la casilla.
     * - Banca del monopoly (es el dueño de las casillas no compradas aún).*/
    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        if (duenho.equals(banca) && (tipo.equals("Solar") || tipo.equals("Transporte") || tipo.equals("Servicios"))) {
            if (solicitante.getFortuna() >= valor) {
                solicitante.sumarFortuna(-valor);
                banca.sumarFortuna(valor);
                duenho = solicitante;
                solicitante.anhadirPropiedad(this);
                System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + nombre + " por " + valor + "€. Su fortuna actual es " + solicitante.getFortuna() + "€.");
            } else {
                System.out.println("No tienes suficiente dinero para comprar esta casilla.");
            }
        } else {
            System.out.println("Esta casilla no se puede comprar.");
        }
    }

    /*Método para añadir valor a una casilla. Utilidad:
     * - Sumar valor a la casilla de parking.
     * - Sumar valor a las casillas de solar al no comprarlas tras cuatro vueltas de todos los jugadores.
     * Este método toma como argumento la cantidad a añadir del valor de la casilla.*/
    public void sumarValor(float suma) {
        this.valor += suma;
    }

    // Nuevo método para establecer el valor directamente
    public void setValor(float valor) {
        this.valor = valor;
    }

    /*Método para mostrar información sobre una casilla.
     * Devuelve una cadena con información específica de cada tipo de casilla.*/
    public String infoCasilla() {
        String info = "{ \n tipo: " + tipo;
        if (tipo.equals("Solar")) {
            info += ", \n grupo: " + (grupo != null ? grupo.getColorGrupo() : "") + ", \n propietario: " + duenho.getNombre() + ", \n valor: " + valor + ", \n alquiler: " + impuesto;
            // Añadir valores de edificios en futuras partes
        } else if (tipo.equals("Impuesto")) {
            info += ", \n apagar: " + impuesto;
        } else if (tipo.equals("Especial")) {
            if (nombre.equals("Parking")) {
                info += ", \n bote: " + valor;
                String jugs = "";
                for (Avatar a : avatares) {
                    jugs += a.getJugador().getNombre() + ", ";
                }
                if (jugs.endsWith(", ")) jugs = jugs.substring(0, jugs.length() - 2);
                info += ", \n jugadores: [" + jugs + "]";
            } else if (nombre.equals("Carcel")) {
                info += ", \n salir: 500000";
                String jugs = "";
                for (Avatar a : avatares) {
                    jugs += "[" + a.getJugador().getNombre() + "," + a.getJugador().getTiradasCarcel() + "] ";
                }
                info += ", \n jugadores: " + jugs;
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
            String g = (grupo != null ? " grupo: " + grupo.getColorGrupo() + "," : "");
            return "{ \n tipo: " + tipo + "," + g + " \n valor: " + valor + "\n}";
        }
        return "";
    }

    public String representacionColoreada() {
        int WIDTH = 9;
        String rep = nombre;
        String color = ""; // Color por defecto (sin color)
        // Aplicar color si es un solar y tiene grupo
        if (tipo.equals("Solar") && grupo != null) {
            color = grupo.getAnsiColor();
            rep = color + rep; // + Valor.RESET;
        }
        if (!avatares.isEmpty()) {
            String avatars = "&";
            for (Avatar a : avatares) {
                avatars += a.getId();
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
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public float getValor() { return valor; }
    public int getPosicion() { return posicion; }
    public Jugador getDuenho() { return duenho; }
    public void setDuenho(Jugador duenho) { this.duenho = duenho; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public float getImpuesto() { return impuesto; }
    public void setImpuesto(float impuesto) { this.impuesto = impuesto; }
    public float getHipoteca() { return hipoteca; }
    public void setHipoteca(float hipoteca) { this.hipoteca = hipoteca; }
    public ArrayList<Avatar> getAvatares() { return avatares; }
    public Tablero getTablero() { return tablero; }
    public void setTablero(Tablero tablero) { this.tablero = tablero; }
}
