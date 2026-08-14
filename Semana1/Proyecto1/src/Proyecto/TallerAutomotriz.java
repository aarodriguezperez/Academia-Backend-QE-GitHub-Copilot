package Proyecto;

public class TallerAutomotriz {
    public static void main(String[] args) {
        Automovil auto = new Automovil(1,"ssz", "Toyota",
                "Corolla", 2002,10000, "Manual");

        try {
            auto.actualizarKilometraje(9000);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println(auto);
    }
}
