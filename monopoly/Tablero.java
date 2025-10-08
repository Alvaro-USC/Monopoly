package monopoly;

import partida.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Tablero {
    // Atributos.
    private ArrayList<ArrayList<Casilla>> posiciones; // Posiciones del tablero: se define como un arraylist de arraylists de casillas (uno por cada lado del tablero).
    private HashMap<String, Grupo> grupos; // Grupos del tablero, almacenados como un HashMap con clave String (será el nombre del grupo como "marron").
    private Jugador banca; // Un jugador que será la banca.

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

    // Método para crear todas las casillas del tablero. Formado a su vez por cuatro métodos (1/lado).
    private void generarCasillas() {
        this.insertarLadoSur();
        this.insertarLadoOeste();
        this.insertarLadoNorte();
        this.insertarLadoEste();

        // Asignar tableros a casillas
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                c.setTablero(this);
            }
        }

        // Crear grupos
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

    // Método para insertar las casillas del lado norte.
    private void insertarLadoNorte() {
        ArrayList<Casilla> lado = this.posiciones.get(2);

        Casilla parking = encontrar_casilla("Parking"); // Compartido con oeste
        if (parking == null) {
            parking = new Parking(21, this.banca);
        }
        lado.add(parking);

        Solar solar12 = new Solar("Solar12", 22, 2200000f, this.banca);
        solar12.setHipoteca(1100000f);
        solar12.setImpuesto(180000f);
        lado.add(solar12);

        Suerte suerte2 = new Suerte(23, this.banca);
        lado.add(suerte2);

        Solar solar13 = new Solar("Solar13", 24, 2200000f, this.banca);
        solar13.setHipoteca(1100000f);
        solar13.setImpuesto(180000f);
        lado.add(solar13);

        Solar solar14 = new Solar("Solar14", 25, 2400000f, this.banca);
        solar14.setHipoteca(1200000f);
        solar14.setImpuesto(200000f);
        lado.add(solar14);

        Transporte trans3 = new Transporte("Trans3", 26, Valor.VALOR_TRANSP_SERV, this.banca);
        trans3.setImpuesto(Valor.ALQUILER_TRANSP);
        trans3.setHipoteca(0f);
        lado.add(trans3);

        Solar solar15 = new Solar("Solar15", 27, 2600000f, this.banca);
        solar15.setHipoteca(1300000f);
        solar15.setImpuesto(220000f);
        lado.add(solar15);

        Solar solar16 = new Solar("Solar16", 28, 2600000f, this.banca);
        solar16.setHipoteca(1300000f);
        solar16.setImpuesto(220000f);
        lado.add(solar16);

        Servicio serv2 = new Servicio("Serv2", 29, Valor.VALOR_TRANSP_SERV, this.banca);
        serv2.setHipoteca(0f);
        serv2.setImpuesto(0f);
        lado.add(serv2);

        Solar solar17 = new Solar("Solar17", 30, 2800000f, this.banca);
        solar17.setHipoteca(1400000f);
        solar17.setImpuesto(240000f);
        lado.add(solar17);

        IrCarcel irCarcel = new IrCarcel(31, this.banca);
        lado.add(irCarcel);
    }

    // Método para insertar las casillas del lado sur.
    private void insertarLadoSur() {
        ArrayList<Casilla> lado = this.posiciones.get(0);

        Carcel carcel = new Carcel(11, this.banca);
        lado.add(carcel);

        Solar solar5 = new Solar("Solar5", 10, 1200000f, this.banca);
        solar5.setHipoteca(600000f);
        solar5.setImpuesto(80000f);
        lado.add(solar5);

        Solar solar4 = new Solar("Solar4", 9, 1000000f, this.banca);
        solar4.setHipoteca(500000f);
        solar4.setImpuesto(60000f);
        lado.add(solar4);

        Suerte suerte1 = new Suerte(8, this.banca);
        lado.add(suerte1);

        Solar solar3 = new Solar("Solar3", 7, 1000000f, this.banca);
        solar3.setHipoteca(500000f);
        solar3.setImpuesto(60000f);
        lado.add(solar3);

        Transporte trans1 = new Transporte("Trans1", 6, Valor.VALOR_TRANSP_SERV, this.banca);
        trans1.setImpuesto(Valor.ALQUILER_TRANSP);
        trans1.setHipoteca(0f);
        lado.add(trans1);

        Impuesto imp1 = new Impuesto("Imp1", 5, Valor.IMPUESTO, this.banca);
        lado.add(imp1);

        Solar solar2 = new Solar("Solar2", 4, 600000f, this.banca);
        solar2.setHipoteca(300000f);
        solar2.setImpuesto(40000f);
        lado.add(solar2);

        CajaComunidad caja1 = new CajaComunidad(3, this.banca);
        lado.add(caja1);

        Solar solar1 = new Solar("Solar1", 2, 600000f, this.banca);
        solar1.setHipoteca(300000f);
        solar1.setImpuesto(20000f);
        lado.add(solar1);

        Salida salida = new Salida(1, this.banca);
        lado.add(salida);
    }

    // Método que inserta casillas del lado oeste.
    private void insertarLadoOeste() {
        ArrayList<Casilla> lado = this.posiciones.get(1);

        Casilla carcel = encontrar_casilla("Carcel"); // Compartido
        lado.add(carcel);

        Solar solar6 = new Solar("Solar6", 12, 1400000f, this.banca);
        solar6.setHipoteca(700000f);
        solar6.setImpuesto(100000f);
        lado.add(solar6);

        Servicio serv1 = new Servicio("Serv1", 13, Valor.VALOR_TRANSP_SERV, this.banca);
        serv1.setHipoteca(0f);
        serv1.setImpuesto(0f);
        lado.add(serv1);

        Solar solar7 = new Solar("Solar7", 14, 1400000f, this.banca);
        solar7.setHipoteca(700000f);
        solar7.setImpuesto(100000f);
        lado.add(solar7);

        Solar solar8 = new Solar("Solar8", 15, 1600000f, this.banca);
        solar8.setHipoteca(800000f);
        solar8.setImpuesto(120000f);
        lado.add(solar8);

        Transporte trans2 = new Transporte("Trans2", 16, Valor.VALOR_TRANSP_SERV, this.banca);
        trans2.setImpuesto(Valor.ALQUILER_TRANSP);
        trans2.setHipoteca(0f);
        lado.add(trans2);

        Solar solar9 = new Solar("Solar9", 17, 1800000f, this.banca);
        solar9.setHipoteca(900000f);
        solar9.setImpuesto(140000f);
        lado.add(solar9);

        CajaComunidad caja2 = new CajaComunidad(18, this.banca);
        lado.add(caja2);

        Solar solar10 = new Solar("Solar10", 19, 1800000f, this.banca);
        solar10.setHipoteca(900000f);
        solar10.setImpuesto(140000f);
        lado.add(solar10);

        Solar solar11 = new Solar("Solar11", 20, 2200000f, this.banca);
        solar11.setHipoteca(1000000f);
        solar11.setImpuesto(160000f);
        lado.add(solar11);

        Parking parking = new Parking(21, this.banca);
        lado.add(parking);
    }

    // Método que inserta las casillas del lado este.
    private void insertarLadoEste() {
        ArrayList<Casilla> lado = this.posiciones.get(3);

        Casilla irCarcel = encontrar_casilla("IrCarcel"); // Compartido con norte
        lado.add(irCarcel);

        Solar solar18 = new Solar("Solar18", 32, 3000000f, this.banca);
        solar18.setHipoteca(1500000f);
        solar18.setImpuesto(260000f);
        lado.add(solar18);

        Solar solar19 = new Solar("Solar19", 33, 3000000f, this.banca);
        solar19.setHipoteca(1500000f);
        solar19.setImpuesto(260000f);
        lado.add(solar19);

        CajaComunidad caja3 = new CajaComunidad(34, this.banca);
        lado.add(caja3);

        Solar solar20 = new Solar("Solar20", 35, 3200000f, this.banca);
        solar20.setHipoteca(1600000f);
        solar20.setImpuesto(280000f);
        lado.add(solar20);

        Transporte trans4 = new Transporte("Trans4", 36, Valor.VALOR_TRANSP_SERV, this.banca);
        trans4.setImpuesto(Valor.ALQUILER_TRANSP);
        trans4.setHipoteca(0f);
        lado.add(trans4);

        Suerte suerte3 = new Suerte(37, this.banca);
        lado.add(suerte3);

        Solar solar21 = new Solar("Solar21", 38, 3500000f, this.banca);
        solar21.setHipoteca(1750000f);
        solar21.setImpuesto(350000f);
        lado.add(solar21);

        Impuesto imp2 = new Impuesto("Imp2", 39, Valor.IMPUESTO, this.banca);
        lado.add(imp2);

        Solar solar22 = new Solar("Solar22", 40, 4000000f, this.banca);
        solar22.setHipoteca(2000000f);
        solar22.setImpuesto(500000f);
        lado.add(solar22);

        Casilla salida = encontrar_casilla("Salida"); // Compartido con sur
        lado.add(salida);
    }

    // Para imprimir el tablero, modificamos el método toString().
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Línea superior (norte)
        sb.append("┎─────────┰───────┰───────┰───────┰───────┰───────┰───────┰───────┰───────┰───────┰────────┒");
        sb.append("\n");
        int contador = 0;
        int nChars = 0;
        for (Casilla c : posiciones.get(2)) {
            String rep = c.representacionColoreada();
            nChars = 0;

            for (char z : rep.toCharArray()) {
                nChars++;
            }

            if (contador == 0) {
                for (int i = 0; i < 9 - nChars; i++) {
                    rep += " ";
                }
            } else if (contador > 0) {
                for (int i = 0; i < 7 - nChars; i++) {
                    rep += " ";
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
            String leftRep = left.representacionColoreada();

            Casilla right = posiciones.get(3).get(10 - i);
            String rightRep = right.representacionColoreada();



            nChars = 0;

            for (char z : leftRep.toCharArray()) {
                nChars++;
            }

            if (nChars < 9) {
                for (int j = 0; j < 9 - nChars; j++) {
                    leftRep += " ";
                }
            } else if (i < 7)
            {
                if (!leftRep.contains("&")) leftRep += "   ";
            } else {
                if (!leftRep.contains("&")) leftRep += "  ";
            }


            nChars = 0;
            for (char z : rightRep.toCharArray()) {
                nChars++;
            }
            if (nChars < 8)
            {
                for (int j = 0; j < 8 - nChars; j++)
                {
                    rightRep += " ";
                }
            } else {
                if (!rightRep.contains("&")) rightRep += " ";
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
        for (Casilla c : posiciones.get(0)) {
            String rep = c.representacionColoreada();
            nChars = 0;
            for (char z : rep.toCharArray()) {
                nChars++;
            }

            if (contador == 0) {
                for (int i = 0; i < 9 - nChars; i++) {
                    rep += " ";
                }
            } else if (contador > 0) {
                if (nChars < 7)
                {
                    for (int i = 0; i < 7 - nChars; i++)
                    {
                        rep += " ";
                    }
                } else {
                    if (!rep.contains("&")) rep += " ";
                }
            }

            if (contador > 9) rep += " ";

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
