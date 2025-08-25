package mini_biblioteca.services;


import mini_biblioteca.entities.Autor;
import mini_biblioteca.entities.Categoria;

import java.util.List;
import java.util.Optional;

public interface  AutorService {

    Autor guardarAutor(Autor autor);

    Optional<Autor> buscarPorId(Long id);

    Optional<Autor> buscarPorNombre(String nombre);

    List<Autor> listarTodosLosAutores();

    Autor actualizarAutor(Autor autor);

    void eliminarAutor(Long id) throws ClassNotFoundException;

    List<Autor> buscarPorIds(List<Long>ids);
}
