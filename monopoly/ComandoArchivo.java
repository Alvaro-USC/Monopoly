package monopoly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ComandoArchivo {
    private final Menu menu;
    private final String archivo;

    public ComandoArchivo(Menu menu, String archivo) {
        this.menu = menu;
        this.archivo = archivo;
    }

    public void procesarComandos() {
        if (archivo == null) {
            System.out.println("No se proporcionó un archivo de comandos. Iniciando modo interactivo.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) { // Ignorar líneas en blanco
                    if (linea.charAt(0) == '#')
                        continue; // Las líneas que empiezan con # se ignoran, son comentarios para los comandos
                    System.out.println(linea);
                    menu.analizarComando(linea);
                }
            }
            System.out.println("Procesamiento del archivo de comandos completado.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo " + archivo + ": \n" + e.getMessage());
            System.err.println("Iniciando modo interactivo como respaldo...");
        }
    }
}