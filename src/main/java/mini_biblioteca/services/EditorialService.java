package mini_biblioteca.services;

import mini_biblioteca.entities.Editorial;

import java.util.List;
import java.util.Optional;

public interface EditorialService {

    Editorial guardarEditorial(Editorial editorial);

    Optional<Editorial>buscarPorId(Long id);

    Optional<Editorial>buscarPorId(String nombre);

    List<Editorial> listarTodasLasEditoriales();

    Editorial actualizarEditorial(Editorial editorial);

    void eliminarEditorial(Long id);

}
