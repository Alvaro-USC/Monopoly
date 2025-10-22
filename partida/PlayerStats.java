package partida;

/**
 * Estadísticas por jugador.
 */
public class PlayerStats {
    private float dineroInvertido = 0f; // compras propiedades, edificaciones
    private float pagoTasasEImpuestos = 0f;
    private float pagoDeAlquileres = 0f;
    private float cobroDeAlquileres = 0f;
    private float pasarPorSalida = 0f;
    private float premiosBote = 0f;
    private int vecesEnLaCarcel = 0;

    public void addDineroInvertido(float v) { dineroInvertido += v; }
    public void addPagoTasas(float v) { pagoTasasEImpuestos += v; }
    public void addPagoAlquiler(float v) { pagoDeAlquileres += v; }
    public void addCobroAlquiler(float v) { cobroDeAlquileres += v; }
    public void addPasarSalida(float v) { pasarPorSalida += v; }
    public void addPremioBote(float v) { premiosBote += v; }
    public void addVezEnCarcel() { vecesEnLaCarcel++; }

    // Getters
    public float getDineroInvertido() { return dineroInvertido; }
    public float getPagoTasasEImpuestos() { return pagoTasasEImpuestos; }
    public float getPagoDeAlquileres() { return pagoDeAlquileres; }
    public float getCobroDeAlquileres() { return cobroDeAlquileres; }
    public float getPasarPorSalida() { return pasarPorSalida; }
    public float getPremiosBote() { return premiosBote; }
    public int getVecesEnLaCarcel() { return vecesEnLaCarcel; }
}