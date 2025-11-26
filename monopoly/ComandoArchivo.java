package monopoly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static monopoly.Juego.consola;

public class ComandoArchivo {
    private final Juego juego;
    private final String archivo;

    public ComandoArchivo(Juego juego, String archivo) {
        this.juego = juego;
        this.archivo = archivo;
    }

    public void procesarComandos() {
        if (archivo == null) {
            consola.imprimir("No se proporcionó un archivo de comandos. Iniciando modo interactivo.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) { // Ignorar líneas en blanco
                    if (linea.charAt(0) == '#')
                        continue; // Las líneas que empiezan con # se ignoran, son comentarios para los comandos
                    consola.imprimir(linea);
                    juego.analizarComando(linea);
                }
            }
            consola.imprimir("Procesamiento del archivo de comandos completado.");
        } catch (IOException e) {
            consola.imprimir("Error al leer el archivo " + archivo + ": \n" + e.getMessage());
            consola.imprimir("Iniciando modo interactivo como respaldo...");
        }
    }
}