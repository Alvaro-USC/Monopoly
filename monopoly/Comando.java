package monopoly;

import monopoly.excepcion.AccionInvalidaException;
import monopoly.excepcion.PropiedadNoPerteneceException;

public interface Comando {
    void crearJugador(String nombre, String tipoAvatar);

    void listarJugadores();

    void listarAvatares();

    void descJugador(String nombre);

    void descAvatar(String id);

    void descCasilla(String nombre);

    void lanzarDados(boolean forced, int fv1, int fv2) throws AccionInvalidaException;

    void comprar(String nombre);

    void salirCarcel();

    void listarVenta();

    void listarVenta(String grupo);

    void edificar(String tipo);

    void hipotecar(String nombreCasilla);

    void deshipotecar(String nombreCasilla);

    void venderEdificios(String tipo, String nombreCasilla, int cantidad);

    void listarEdificios();

    void listarEdificios(String grupo);

    void acabarTurno();

    void verTablero();

    void mostrarEstadisticas(String objetivo);

    void mostrarInfoJugadorTurno();

    void proponerTrato(String comandoCompleto) throws AccionInvalidaException;

    void aceptarTrato(String idTrato) throws PropiedadNoPerteneceException, AccionInvalidaException;

    void listarTratos();

    void eliminarTrato(String idTrato) throws AccionInvalidaException;
}