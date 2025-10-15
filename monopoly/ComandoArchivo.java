package monopoly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ComandoArchivo {
    private Menu menu;
    private String archivo;

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
                    System.out.println(linea);
                    menu.analizarComando(linea);
                }
            }
            System.out.println("Procesamiento del archivo de comandos completado.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo " + archivo + ": " + e.getMessage());
            System.err.println("Iniciando modo interactivo como respaldo...");
        }
    }
}