package monopoly;

import java.text.NumberFormat;
import java.util.Locale;

public final class Valor {
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

    private static final NumberFormat FORMATO_MILES;

    static {
        // Inicialización estática
        FORMATO_MILES = NumberFormat.getInstance(Locale.forLanguageTag("es-ES"));
        FORMATO_MILES.setMaximumFractionDigits(0); // Cero decimales
        FORMATO_MILES.setMinimumFractionDigits(0); // Asegura que no haya ".0"
    }

    // la clase Valor es una clase de utilidad estática, un contenedor de datos, no se debe instanciar
    // ES UN SINGLETON
    private Valor() {
        throw new IllegalStateException("Singleton. Clase de utilidad. No instanciar.");
    }

    public static String formatear(float cantidad) {
        return FORMATO_MILES.format(cantidad);
    }

    // El índice 0 corresponde a Solar1, el índice 21 a Solar22.
    // --- Precios de compra e Hipoteca de Solares (Apéndice I) ---
    public static final float[] PRECIO_SOLAR = {600_000f, 600_000f, 1_000_000f, 1_000_000f, 1_200_000f, 1_400_000f, 1_400_000f, 1_600_000f, 1_800_000f, 1_800_000f, 2_200_000f, 2_200_000f, 2_200_000f, 2_400_000f, 2_600_000f, 2_600_000f, 2_800_000f, 3_000_000f, 3_000_000f, 3_200_000f, 3_500_000f, 4_000_000f};
    // --- Precios de Construcción de Edificios (Apéndice I) ---
    // Columna: [0=casa, 1=hotel, 2=piscina, 3=pistaDeporte]
    public static final float[][] PRECIO_EDIFICIOS = {{500_000f, 500_000f, 100_000f, 200_000f}, // Solar1 a Solar5 (Grupos Marrón, Celeste)
            {500_000f, 500_000f, 100_000f, 200_000f}, // Solar2
            {500_000f, 500_000f, 100_000f, 200_000f}, // Solar3
            {500_000f, 500_000f, 100_000f, 200_000f}, // Solar4
            {500_000f, 500_000f, 100_000f, 200_000f}, // Solar5
            {1_000_000f, 1_000_000f, 200_000f, 400_000f}, // Solar6 a Solar11 (Grupos Rosa, Naranja)
            {1_000_000f, 1_000_000f, 200_000f, 400_000f}, // Solar7
            {1_000_000f, 1_000_000f, 200_000f, 400_000f}, // Solar8
            {1_000_000f, 1_000_000f, 200_000f, 400_000f}, // Solar9
            {1_000_000f, 1_000_000f, 200_000f, 400_000f}, // Solar10
            {1_000_000f, 1_000_000f, 200_000f, 400_000f}, // Solar11
            {1_500_000f, 1_500_000f, 300_000f, 600_000f}, // Solar12 a Solar17 (Grupos Rojo, Amarillo)
            {1_500_000f, 1_500_000f, 300_000f, 600_000f}, // Solar13
            {1_500_000f, 1_500_000f, 300_000f, 600_000f}, // Solar14
            {1_500_000f, 1_500_000f, 300_000f, 600_000f}, // Solar15
            {1_500_000f, 1_500_000f, 300_000f, 600_000f}, // Solar16
            {1_500_000f, 1_500_000f, 300_000f, 600_000f}, // Solar17
            {2_000_000f, 2_000_000f, 400_000f, 800_000f}, // Solar18 a Solar22 (Grupos Verde, Azul)
            {2_000_000f, 2_000_000f, 400_000f, 800_000f}, // Solar19
            {2_000_000f, 2_000_000f, 400_000f, 800_000f}, // Solar20
            {2_000_000f, 2_000_000f, 400_000f, 800_000f}, // Solar21
            {2_000_000f, 2_000_000f, 400_000f, 800_000f}  // Solar22
    };
    // --- Alquileres de Solar y Edificios (Apéndice I/II) ---
    // Se usa como la renta base de la casilla sin edificios
    public static final float[] ALQUILER_BASE = {20_000f, 40_000f, 60_000f, 60_000f, 80_000f, 100_000f, 100_000f, 120_000f, 140_000f, 140_000f, 160_000f, 180_000f, 180_000f, 200_000f, 220_000f, 220_000f, 240_000f, 260_000f, 260_000f, 280_000f, 350_000f, 500_000f};
    // Columna: [0=casa, 1=hotel, 2=piscina, 3=pistaDeporte]
    // La suma del alquiler del solar es ALQUILER_BASE + (suma de estos valores por edificio)
    public static final float[][] ALQUILER_EDIFICIOS = {{400_000f, 2_500_000f, 500_000f, 500_000f}, // Solar1
            {800_000f, 4_500_000f, 900_000f, 900_000f}, // Solar2
            {1_000_000f, 5_500_000f, 1_100_000f, 1_100_000f}, // Solar3
            {1_000_000f, 5_500_000f, 1_100_000f, 1_100_000f}, // Solar4
            {1_250_000f, 6_000_000f, 1_200_000f, 1_200_000f}, // Solar5
            {1_500_000f, 7_500_000f, 1_500_000f, 1_500_000f}, // Solar6
            {1_500_000f, 7_500_000f, 1_500_000f, 1_500_000f}, // Solar7
            {1_750_000f, 9_000_000f, 1_800_000f, 1_800_000f}, // Solar8
            {1_850_000f, 9_500_000f, 1_900_000f, 1_900_000f}, // Solar9
            {1_850_000f, 9_500_000f, 1_900_000f, 1_900_000f}, // Solar10
            {2_000_000f, 10_000_000f, 2_000_000f, 2_000_000f}, // Solar11
            {2_200_000f, 10_500_000f, 2_100_000f, 2_100_000f}, // Solar12
            {2_200_000f, 10_500_000f, 2_100_000f, 2_100_000f}, // Solar13
            {2_325_000f, 11_000_000f, 2_200_000f, 2_200_000f}, // Solar14
            {2_450_000f, 11_500_000f, 2_300_000f, 2_300_000f}, // Solar15
            {2_450_000f, 11_500_000f, 2_300_000f, 2_300_000f}, // Solar16
            {2_600_000f, 12_000_000f, 2_400_000f, 2_400_000f}, // Solar17
            {2_750_000f, 12_750_000f, 2_550_000f, 2_550_000f}, // Solar18
            {2_750_000f, 12_750_000f, 2_550_000f, 2_550_000f}, // Solar19
            {3_000_000f, 14_000_000f, 2_800_000f, 2_800_000f}, // Solar20
            {3_250_000f, 17_000_000f, 3_400_000f, 3_400_000f}, // Solar21
            {4_250_000f, 20_000_000f, 4_000_000f, 4_000_000f}  // Solar22
    };
}

