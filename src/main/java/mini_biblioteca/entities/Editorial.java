package mini_biblioteca.entities;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Editorial {

    @jakarta.persistence.Id
    @GeneratedValue(strategy= GenerationType.IDENTITY )
    private Long Id;

    private String nombre;

    //editorial: La tenemos declarada como atributo en la clase Libro
    @OneToMany(mappedBy = "editorial", cascade=CascadeType.ALL)
    private List<Libro> libros = new ArrayList<>();

    public Editorial() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }
}
