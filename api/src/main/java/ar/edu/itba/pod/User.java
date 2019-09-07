package ar.edu.itba.pod;

import java.io.Serializable;
import java.rmi.Remote;

public class User implements Serializable, Remote {
    private static final long serialVersionUID = 8383170378958425496L;
    private final String id;
    private final String nombre;

    public User(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}

