package mini_biblioteca.services;

import mini_biblioteca.entities.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {

    Categoria guardarCategoria(Categoria categoria);

    Optional<Categoria> buscarPorId(Long id);

    Optional<Categoria> buscarPorNombre(String nombre);

    List<Categoria> listarTodasLasCategorias();

    Categoria actualizarCategoria(Categoria categoria);

    void eliminarCategoria(Long id);


}
