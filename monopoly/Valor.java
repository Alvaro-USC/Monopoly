package monopoly;

public class Valor {
    //Se incluyen una serie de constantes útiles para no repetir valores.
    public static final float FORTUNA_BANCA = Float.MAX_VALUE; // Cantidad que tiene inicialmente la Banca (ilimitada)
    public static final float FORTUNA_INICIAL = 15_000_000f; // Cantidad que recibe cada jugador al comenzar la partida
    public static final float SUMA_VUELTA = 2_000_000f; // Cantidad que recibe un jugador al pasar por la Salida
    public static final float SALIR_CARCEL = 500_000f; // Cantidad para salir de la cárcel
    public static final float IMPUESTO = 2_000_000f; // Valor de los impuestos
    public static final float VALOR_TRANSP_SERV = 500_000f; // Valor de compra de transportes y servicios
    public static final float ALQUILER_TRANSP = 250_000f; // Alquiler base de transportes
    public static final float FACTOR_SERVICIO = 50_000f; // Factor para servicios

    //Colores del texto:
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
}

