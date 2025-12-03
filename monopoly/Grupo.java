package monopoly;

import monopoly.casilla.Casilla;
import monopoly.casilla.Propiedad;
import monopoly.casilla.propiedad.Solar;
import monopoly.edificio.Edificio;
import partida.Jugador;

import java.util.ArrayList;

public class Grupo {

    //Atributos
    private final ArrayList<Propiedad> miembros; //Casillas miembros del grupo.
    private String colorGrupo; //Nombre del color del grupo
    private int numCasillas; //Número de casillas del grupo.

    //Constructor vacío.
    public Grupo() {
        this.miembros = new ArrayList<>();
    }

    /*Constructor para cuando el grupo está formado por DOS CASILLAS:
     * Requiere como parámetros las dos casillas miembro y el color del grupo.
     */
    public Grupo(Propiedad cas1, Propiedad cas2, String colorGrupo) {
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
    public Grupo(Propiedad cas1, Propiedad cas2, Propiedad cas3, String colorGrupo) {
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

    // Grupo no es una casilla evaluable, es un agrupador de casillas
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        return false;
    }

    /* Método que añade una casilla al array de casillas miembro de un grupo.
     * Parámetro: casilla que se quiere añadir.
     */
    public void anhadirCasilla(Propiedad miembro) {
        miembros.add(miembro);
    }

    /*Método que comprueba si el jugador pasado tiene en su haber todas las casillas del grupo:
     * Parámetro: jugador que se quiere evaluar.
     * Valor devuelto: true si es dueño de todas las casillas del grupo, false en otro caso.
     */
    public boolean esDuenhoGrupo(Jugador jugador) {
        int count = 0;
        for (Propiedad c : miembros) {
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
        return switch (colorGrupo) {
            case "marron", "naranja", "amarillo" -> Valor.YELLOW;
            case "celeste" -> Valor.CYAN;
            case "rosa" -> Valor.PURPLE;
            case "rojo" -> Valor.RED;
            case "verde" -> Valor.GREEN;
            case "azul" -> Valor.BLUE;
            default -> Valor.RESET;
        };
    }

    public ArrayList<Propiedad> getMiembros() {
        return miembros;
    }

    public String getDescripcionGrupo() {
        StringBuilder r = new StringBuilder();
        for (Propiedad casilla : miembros) {
            if (casilla instanceof Solar s) {
                r.append("{\n");
                r.append(" propiedad: ").append(s.getNombre());
                r.append("\n hoteles: ");
                if (s.getCantidadEdificioTipo("hotel") > 0) {
                    for (Edificio edificio : s.getEdificios()) {
                        if (edificio.getTipo().equals("hotel")) {
                            r.append("[ ").append(edificio.getId()).append(" ]");
                        }
                    }
                } else {
                    r.append("-");
                }
                r.append("\n casas: ");
                if (s.getCantidadEdificioTipo("casa") > 0) {
                    r.append("[");
                    for (Edificio edificio : s.getEdificios()) {
                        if (edificio.getTipo().equals("casa")) {
                            r.append(edificio.getId()).append(" ");
                        }
                    }
                    r.append("]");
                } else {
                    r.append("-");
                }
                r.append("\n piscinas: ");
                if (s.getCantidadEdificioTipo("piscinas") > 0) {
                    for (Edificio edificio : s.getEdificios()) {
                        if (edificio.getTipo().equals("piscinas")) {
                            r.append("[ ").append(edificio.getId()).append(" ]");
                        }
                    }
                } else {
                    r.append("-");
                }
                r.append("\n pistasDeDeporte: ");
                if (s.getCantidadEdificioTipo("pista_deporte") > 0) {
                    for (Edificio edificio : s.getEdificios()) {
                        if (edificio.getTipo().equals("pista_deporte")) {
                            r.append("[ ").append(edificio.getId()).append(" ]");
                        }
                    }
                } else {
                    r.append("-");
                }
                r.append("\n alquiler: ").append(Valor.formatear(s.calcularSumaTotalAlquileres())).append("\n}");
                r.append("\n");
                r.append(s.getEdificiosFaltantesDescripcion());
                r.append("\n");
            }
        }
        return r.toString();
    }
}