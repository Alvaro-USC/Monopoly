package monopoly;

import partida.*;
import java.util.ArrayList;

class Grupo {

    //Atributos
    private ArrayList<Casilla> miembros; //Casillas miembros del grupo.
    private String colorGrupo; //Nombre del color del grupo (e.g., "rosa")
    private int numCasillas; //Número de casillas del grupo.

    //Constructor vacío.
    public Grupo() {
        this.miembros = new ArrayList<>();
    }

    /*Constructor para cuando el grupo está formado por DOS CASILLAS:
     * Requiere como parámetros las dos casillas miembro y el color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, String colorGrupo) {
        this();
        this.anhadirCasilla(cas1);
        this.anhadirCasilla(cas2);
        this.colorGrupo = colorGrupo;
        this.numCasillas = 2;
        cas1.setGrupo(this);
        cas2.setGrupo(this);
    }

    /*Constructor para cuando el grupo está formado por TRES CASILLAS:
     * Requiere como parámetros las tres casillas miembro y el color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, Casilla cas3, String colorGrupo) {
        this();
        this.anhadirCasilla(cas1);
        this.anhadirCasilla(cas2);
        this.anhadirCasilla(cas3);
        this.colorGrupo = colorGrupo;
        this.numCasillas = 3;
        cas1.setGrupo(this);
        cas2.setGrupo(this);
        cas3.setGrupo(this);
    }

    /* Método que añade una casilla al array de casillas miembro de un grupo.
     * Parámetro: casilla que se quiere añadir.
     */
    public void anhadirCasilla(Casilla miembro) {
        miembros.add(miembro);
    }

    /*Método que comprueba si el jugador pasado tiene en su haber todas las casillas del grupo:
     * Parámetro: jugador que se quiere evaluar.
     * Valor devuelto: true si es dueño de todas las casillas del grupo, false en otro caso.
     */
    public boolean esDuenhoGrupo(Jugador jugador) {
        int count = 0;
        for (Casilla c : miembros) {
            if (c.getDuenho().equals(jugador)) {
                count++;
            }
        }
        return count == numCasillas;
    }

    public String getColorGrupo() {
        return colorGrupo;
    }

    public String getAnsiColor() {
        switch (colorGrupo) {
            case "marron":
                return Valor.YELLOW;
            case "celeste":
                return Valor.CYAN;
            case "rosa":
                return Valor.PURPLE;
            case "naranja":
                return Valor.YELLOW;
            case "rojo":
                return Valor.RED;
            case "amarillo":
                return Valor.YELLOW;
            case "verde":
                return Valor.GREEN;
            case "azul":
                return Valor.BLUE;
            default:
                return Valor.RESET;
        }
    }

    public ArrayList<Casilla> getMiembros()
    {
        return miembros;
    }

    public String getDescripcionGrupo() {
        String r = new String();

        for (Casilla casilla : miembros) {
            if (casilla instanceof Solar s) {
                if (!s.getEdificios().isEmpty()) {
                    r += "{\n";
                    r += " propiedad: " + casilla.getNombre();
                    r += "\n hoteles: ";
                    if (s.getCantidadEdificioTipo("hotel") > 0) {
                        for (Edificio edificio : s.getEdificios()) {
                            if (edificio.getTipo().equals("hotel")) {
                                r += "[ " + edificio.getId() + " ]";
                            }
                        }
                    }
                    else {
                        r += "-";
                    }

                    r += "\n casas: ";
                    if (s.getCantidadEdificioTipo("casa") > 0) {
                        r += "[";
                        for (Edificio edificio : s.getEdificios()) {
                            if (edificio.getTipo().equals("casa")) {
                                r += edificio.getId() + " ";
                            }
                        }
                        r += "]";
                    }
                    else {
                        r += "-";
                    }

                    r += "\n piscinas: ";
                    if (s.getCantidadEdificioTipo("piscinas") > 0) {
                        for (Edificio edificio : s.getEdificios()) {
                            if (edificio.getTipo().equals("piscinas")) {
                                r += "[ " + edificio.getId() + " ]";
                            }
                        }
                    }
                    else {
                        r += "-";
                    }

                    r += "\n pistasDeDeporte: ";
                    if (s.getCantidadEdificioTipo("pista_deporte") > 0) {
                        for (Edificio edificio : s.getEdificios()) {
                            if (edificio.getTipo().equals("pista_deporte")) {
                                r += "[ " + edificio.getId() + " ]";
                            }
                        }
                    }
                    else {
                        r += "-";
                    }
                    
                    r += "\n alquiler: " + casilla.getValor() + casilla.getAlquileres() + "\n}";
                }
                r += "\n";
            }
        }

        return r;
    }
}