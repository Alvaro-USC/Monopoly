package monopoly;

import monopoly.casilla.Casilla;
import monopoly.casilla.Impuesto;
import monopoly.casilla.accion.CajaComunidad;
import monopoly.casilla.accion.Suerte;
import monopoly.casilla.especial.Carcel;
import monopoly.casilla.especial.IrCarcel;
import monopoly.casilla.especial.Parking;
import monopoly.casilla.especial.Salida;
import monopoly.casilla.propiedad.Servicio;
import monopoly.casilla.propiedad.Solar;
import monopoly.casilla.propiedad.Transporte;
import partida.Jugador;

import java.util.ArrayList;
import java.util.HashMap;

public class Tablero {
    // Atributos.
    private final ArrayList<ArrayList<Casilla>> posiciones; // Posiciones del tablero: se define como un arraylist de arraylists de casillas (uno por cada lado del tablero).
    private final HashMap<String, Grupo> grupos; // Grupos del tablero, almacenados como un HashMap con clave String (será el nombre del grupo como "marron").
    private final Jugador banca; // Un jugador que será la banca.

    // Constructor: únicamente le pasamos el jugador banca (que se creará desde el menú).
    public Tablero(Jugador banca) {
        this.banca = banca;
        this.posiciones = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.posiciones.add(new ArrayList<>());
        }
        this.grupos = new HashMap<>();
        this.generarCasillas();
    }

    private void generarCasillas() {
        // Obtener las listas de los lados (para legibilidad) ---
        ArrayList<Casilla> ladoSur = this.posiciones.get(0);
        ArrayList<Casilla> ladoOeste = this.posiciones.get(1);
        ArrayList<Casilla> ladoNorte = this.posiciones.get(2);
        ArrayList<Casilla> ladoEste = this.posiciones.get(3);

        // Crear las 4 esquinas primero (rompe dependencias)
        Casilla salida = new Salida(1, this.banca);
        Casilla carcel = new Carcel(11, this.banca);
        Casilla parking = new Parking(21, this.banca);
        Casilla irCarcel = new IrCarcel(31, this.banca);

        // Poblar LADO SUR (Pos 11 a 1, Solares 0-4)
        // Se añaden en orden inverso para que 'Salida' sea la última (índice 10)
        ladoSur.add(carcel); // 11
        ladoSur.add(new Solar("Solar5", 10, Valor.PRECIO_SOLAR[4], this.banca, Valor.HIPOTECA_SOLAR[4], Valor.ALQUILER_BASE[4]));
        ladoSur.add(new Solar("Solar4", 9, Valor.PRECIO_SOLAR[3], this.banca, Valor.HIPOTECA_SOLAR[3], Valor.ALQUILER_BASE[3]));
        ladoSur.add(new Suerte(8, this.banca));
        ladoSur.add(new Solar("Solar3", 7, Valor.PRECIO_SOLAR[2], this.banca, Valor.HIPOTECA_SOLAR[2], Valor.ALQUILER_BASE[2]));
        ladoSur.add(new Transporte("Trans1", 6, Valor.VALOR_TRANSP_SERV, this.banca));
        ladoSur.add(new Impuesto("Imp1", 5, Valor.IMPUESTO, this.banca));
        ladoSur.add(new Solar("Solar2", 4, Valor.PRECIO_SOLAR[1], this.banca, Valor.HIPOTECA_SOLAR[1], Valor.ALQUILER_BASE[1]));
        ladoSur.add(new CajaComunidad(3, this.banca));
        ladoSur.add(new Solar("Solar1", 2, Valor.PRECIO_SOLAR[0], this.banca, Valor.HIPOTECA_SOLAR[0], Valor.ALQUILER_BASE[0]));
        ladoSur.add(salida); // 1

        // Poblar LADO OESTE (Pos 11 a 21, Solares 5-10)
        ladoOeste.add(carcel); // 11 (compartida)
        ladoOeste.add(new Solar("Solar6", 12, Valor.PRECIO_SOLAR[5], this.banca, Valor.HIPOTECA_SOLAR[5], Valor.ALQUILER_BASE[5]));
        ladoOeste.add(new Servicio("Serv1", 13, Valor.VALOR_TRANSP_SERV, this.banca));
        ladoOeste.add(new Solar("Solar7", 14, Valor.PRECIO_SOLAR[6], this.banca, Valor.HIPOTECA_SOLAR[6], Valor.ALQUILER_BASE[6]));
        ladoOeste.add(new Solar("Solar8", 15, Valor.PRECIO_SOLAR[7], this.banca, Valor.HIPOTECA_SOLAR[7], Valor.ALQUILER_BASE[7]));
        ladoOeste.add(new Transporte("Trans2", 16, Valor.VALOR_TRANSP_SERV, this.banca));
        ladoOeste.add(new Solar("Solar9", 17, Valor.PRECIO_SOLAR[8], this.banca, Valor.HIPOTECA_SOLAR[8], Valor.ALQUILER_BASE[8]));
        ladoOeste.add(new CajaComunidad(18, this.banca));
        ladoOeste.add(new Solar("Solar10", 19, Valor.PRECIO_SOLAR[9], this.banca, Valor.HIPOTECA_SOLAR[9], Valor.ALQUILER_BASE[9]));
        ladoOeste.add(new Solar("Solar11", 20, Valor.PRECIO_SOLAR[10], this.banca, Valor.HIPOTECA_SOLAR[10], Valor.ALQUILER_BASE[10]));
        ladoOeste.add(parking); // 21

        // Poblar LADO NORTE (Pos 21 a 31, Solares 11-16)
        ladoNorte.add(parking); // 21 (compartida)
        ladoNorte.add(new Solar("Solar12", 22, Valor.PRECIO_SOLAR[11], this.banca, Valor.HIPOTECA_SOLAR[11], Valor.ALQUILER_BASE[11]));
        ladoNorte.add(new Suerte(23, this.banca));
        ladoNorte.add(new Solar("Solar13", 24, Valor.PRECIO_SOLAR[12], this.banca, Valor.HIPOTECA_SOLAR[12], Valor.ALQUILER_BASE[12]));
        ladoNorte.add(new Solar("Solar14", 25, Valor.PRECIO_SOLAR[13], this.banca, Valor.HIPOTECA_SOLAR[13], Valor.ALQUILER_BASE[13]));
        ladoNorte.add(new Transporte("Trans3", 26, Valor.VALOR_TRANSP_SERV, this.banca));
        ladoNorte.add(new Solar("Solar15", 27, Valor.PRECIO_SOLAR[14], this.banca, Valor.HIPOTECA_SOLAR[14], Valor.ALQUILER_BASE[14]));
        ladoNorte.add(new Solar("Solar16", 28, Valor.PRECIO_SOLAR[15], this.banca, Valor.HIPOTECA_SOLAR[15], Valor.ALQUILER_BASE[15]));
        ladoNorte.add(new Servicio("Serv2", 29, Valor.VALOR_TRANSP_SERV, this.banca));
        ladoNorte.add(new Solar("Solar17", 30, Valor.PRECIO_SOLAR[16], this.banca, Valor.HIPOTECA_SOLAR[16], Valor.ALQUILER_BASE[16]));
        ladoNorte.add(irCarcel); // 31

        // Poblar LADO ESTE (Pos 31 a 1, Solares 17-21)
        ladoEste.add(irCarcel); // 31 (compartida)
        ladoEste.add(new Solar("Solar18", 32, Valor.PRECIO_SOLAR[17], this.banca, Valor.HIPOTECA_SOLAR[17], Valor.ALQUILER_BASE[17]));
        ladoEste.add(new Solar("Solar19", 33, Valor.PRECIO_SOLAR[18], this.banca, Valor.HIPOTECA_SOLAR[18], Valor.ALQUILER_BASE[18]));
        ladoEste.add(new CajaComunidad(34, this.banca));
        ladoEste.add(new Solar("Solar20", 35, Valor.PRECIO_SOLAR[19], this.banca, Valor.HIPOTECA_SOLAR[19], Valor.ALQUILER_BASE[19]));
        ladoEste.add(new Transporte("Trans4", 36, Valor.VALOR_TRANSP_SERV, this.banca));
        ladoEste.add(new Suerte(37, this.banca));
        ladoEste.add(new Solar("Solar21", 38, Valor.PRECIO_SOLAR[20], this.banca, Valor.HIPOTECA_SOLAR[20], Valor.ALQUILER_BASE[20]));
        ladoEste.add(new Impuesto("Imp2", 39, Valor.IMPUESTO, this.banca));
        ladoEste.add(new Solar("Solar22", 40, Valor.PRECIO_SOLAR[21], this.banca, Valor.HIPOTECA_SOLAR[21], Valor.ALQUILER_BASE[21]));
        ladoEste.add(salida); // 1 (compartida)

        // Asignar tableros a casillas y configurar Transportes/Servicios
        // Este bucle es crucial.
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                c.setTablero(this);

                // Configuración adicional que no se podía hacer en el constructor
                if (c instanceof Transporte) {
                    c.setImpuesto(Valor.ALQUILER_TRANSP);
                    c.setHipoteca(Valor.VALOR_TRANSP_SERV / 2); // Hipoteca es la mitad
                }
                if (c instanceof Servicio) {
                    c.setHipoteca(Valor.VALOR_TRANSP_SERV / 2); // Hipoteca es la mitad
                    c.setImpuesto(0f); // Se calcula dinámicamente
                }
            }
        }

        // Crear grupos
        // Esta lógica funciona perfectamente ahora que todas las casillas están creadas.
        Grupo gMarron = new Grupo(encontrar_casilla("Solar1"), encontrar_casilla("Solar2"), "marron");
        grupos.put("marron", gMarron);

        Grupo gCeleste = new Grupo(encontrar_casilla("Solar3"), encontrar_casilla("Solar4"), encontrar_casilla("Solar5"), "celeste");
        grupos.put("celeste", gCeleste);

        Grupo gRosa = new Grupo(encontrar_casilla("Solar6"), encontrar_casilla("Solar7"), encontrar_casilla("Solar8"), "rosa");
        grupos.put("rosa", gRosa);

        Grupo gNaranja = new Grupo(encontrar_casilla("Solar9"), encontrar_casilla("Solar10"), encontrar_casilla("Solar11"), "naranja");
        grupos.put("naranja", gNaranja);

        Grupo gRojo = new Grupo(encontrar_casilla("Solar12"), encontrar_casilla("Solar13"), encontrar_casilla("Solar14"), "rojo");
        grupos.put("rojo", gRojo);

        Grupo gAmarillo = new Grupo(encontrar_casilla("Solar15"), encontrar_casilla("Solar16"), encontrar_casilla("Solar17"), "amarillo");
        grupos.put("amarillo", gAmarillo);

        Grupo gVerde = new Grupo(encontrar_casilla("Solar18"), encontrar_casilla("Solar19"), encontrar_casilla("Solar20"), "verde");
        grupos.put("verde", gVerde);

        Grupo gAzul = new Grupo(encontrar_casilla("Solar21"), encontrar_casilla("Solar22"), "azul");
        grupos.put("azul", gAzul);
    }

    // Para imprimir el tablero, modificamos el método toString().
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Línea superior (norte)
        sb.append("┎─────────┰───────┰───────┰───────┰───────┰───────┰───────┰───────┰───────┰───────┰────────┒");
        sb.append("\n");
        int contador = 0;
        int nChars;
        for (Casilla c : posiciones.get(2)) {
            StringBuilder rep = new StringBuilder(c.representacionColoreada());
            String resultado = rep.toString().replaceAll("\u001B\\[\\d+m", "");
            nChars = resultado.length();
            if (contador == 0) {
                for (int i = 0; i < 9 - nChars; i++) {
                    rep.append(" ");
                }
            } else if (contador > 0) {
                for (int i = 0; i < 7 - nChars; i++) {
                    rep.append(" ");
                }
            }

            sb.append("┃").append(rep);
            contador++;
        }
        sb.append("┃\n");
        sb.append("┠─────────╂───────┸───────┸───────┸───────┸───────┸───────┸───────┸───────┸───────╂────────┨");
        sb.append("\n");
        // Líneas medias
        for (int i = 9; i >= 1; i--) {
            Casilla left = posiciones.get(1).get(i);
            StringBuilder leftRep = new StringBuilder(left.representacionColoreada());

            Casilla right = posiciones.get(3).get(10 - i);
            StringBuilder rightRep = new StringBuilder(right.representacionColoreada());


            String resultado = leftRep.toString().replaceAll("\u001B\\[\\d+m", "");
            nChars = resultado.length();

            if (nChars < 9) {
                for (int j = 0; j < 9 - nChars; j++) {
                    leftRep.append(" ");
                }
            } else if (i < 7) {
                if (!leftRep.toString().contains("&")) leftRep.append("   ");

            } else {
                if (!leftRep.toString().contains("&")) leftRep.append("  ");

            }


            resultado = rightRep.toString().replaceAll("\u001B\\[\\d+m", "");
            nChars = resultado.length();
            if (nChars < 8) {
                for (int j = 0; j < 8 - nChars; j++) {
                    rightRep.append(" ");
                }
            } else {
                if (!rightRep.toString().contains("&")) rightRep.append(" ");

            }

            sb.append("┃").append(leftRep).append("┃").append("                                                                       ┃").append(rightRep).append("┃\n");
            if (i >= 2) {
                sb.append("┠─────────┨                                                                       ┠────────┨\n");
            }

        }

        // Línea inferior (sur)
        sb.append("┠─────────╂───────┰───────┰───────┰───────┰───────┰───────┰───────┰───────┰───────╂────────┨");
        sb.append("\n");
        contador = 0;
        for (Casilla c : posiciones.getFirst()) {
            StringBuilder rep = new StringBuilder(c.representacionColoreada());
            String resultado = rep.toString().replaceAll("\u001B\\[\\d+m", "");
            nChars = resultado.length();

            if (contador == 0) {
                for (int i = 0; i < 9 - nChars; i++) {
                    rep.append(" ");
                }
            } else if (contador > 0) {
                if (nChars < 7) {
                    for (int i = 0; i < 7 - nChars; i++) {
                        rep.append(" ");
                    }
                } else {
                    if (!rep.toString().contains("&")) rep.append(" ");
                }
            }

            if (contador > 9) rep.append(" ");

            sb.append("┃").append(rep);
            contador++;
        }
        sb.append("┃\n");
        sb.append("┖─────────┸───────┸───────┸───────┸───────┸───────┸───────┸───────┸───────┸───────┸────────┚");
        sb.append("\n");

        return sb.toString();
    }

    // Método usado para buscar la casilla con el nombre pasado como argumento:
    public Casilla encontrar_casilla(String nombre) {
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                if (c.getNombre().equals(nombre)) {
                    return c;
                }
            }
        }
        return null;
    }

    public ArrayList<ArrayList<Casilla>> getPosiciones() {
        return posiciones;
    }

    public HashMap<String, Grupo> getGrupos() {
        return grupos;
    }

    public Jugador getBanca() {
        return banca;
    }
}