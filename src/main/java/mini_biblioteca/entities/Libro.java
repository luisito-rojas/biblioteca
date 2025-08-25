package mini_biblioteca.entities;

import java.util.*;
import jakarta.persistence.*;

@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY )
    private Long Id;

    private String titulo;

    @ManyToOne
    @JoinColumn(name="categoria_id")
    private Categoria categoria;


    @ManyToOne
    @JoinColumn(name="editorial_id")
    private Editorial editorial;

    @ManyToMany(fetch=FetchType.EAGER)
    //Como es una realacion @ManyToMany tenemos que crear una tabla intermedia que se llamara libro_autor
    @JoinTable(
            name="libro_autor",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name="autor_id")
    )
    private List<Autor> autores = new ArrayList<>();


    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public Libro() {
    }


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Editorial getEditorial() {
        return editorial;
    }

    public void setEditorial(Editorial editorial) {
        this.editorial = editorial;
    }
}
