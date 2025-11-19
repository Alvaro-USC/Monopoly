package monopoly;

import java.util.Scanner;

public class ConsolaNormal implements Consola {
    private final Scanner scanner;

    public ConsolaNormal() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public String leer(String descripcion) {
        if (descripcion != null && !descripcion.isEmpty()) {
            System.out.print(descripcion + " "); // Añadimos un espacio para estética
        }
        return scanner.nextLine().trim();
    }
}