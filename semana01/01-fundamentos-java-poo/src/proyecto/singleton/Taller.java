package proyecto.singleton;

public class Taller {

    private static Taller instancia;

    private Taller() {
    }

    public static Taller getInstancia() {
        if (instancia == null) {
            instancia = new Taller();
        }

        return instancia;
    }
}