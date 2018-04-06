package mx.edu.ittepic.a3_3_webirisa;

/**
 * Created by OEM on 04/04/2018.
 */

public class Alumno {
    private int id;
    private String nombre, direccion;

    public Alumno(int id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() { return direccion; }
}
