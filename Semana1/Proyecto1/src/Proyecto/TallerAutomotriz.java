package Proyecto;

public class TallerAutomotriz {
    public static void main(String[] args) {
        Automovil auto = new Automovil(1,"ssz", "Toyota",
                "Corolla", 2002,10000, "Manual");

        auto.actualizarKilometraje(9000);
        System.out.println(auto.toString());
    }
}
