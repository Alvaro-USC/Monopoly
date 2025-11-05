package monopoly;

public class MonopolyETSE {

    static void main(String[] args) {
        if (args.length > 0) {
            new Menu(args[0]);
        } else new Menu();
    }

}