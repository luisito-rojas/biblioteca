package mini_biblioteca.services.impl;


import mini_biblioteca.entities.Editorial;
import mini_biblioteca.repositories.EditorialRepository;
import mini_biblioteca.services.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EditorialServiceImpl implements EditorialService {

    @Autowired
    EditorialRepository editorialRepository;

    @Override
    public Editorial guardarEditorial(Editorial editorial) {
        return editorialRepository.save(editorial);
    }

    @Override
    public Optional<Editorial> buscarPorId(Long id) {
        return editorialRepository.findById(id);
    }

    @Override
    public Optional<Editorial> buscarPorId(String nombre) {
        return editorialRepository.findByNombre(nombre);
    }

    @Override
    public List<Editorial> listarTodasLasEditoriales() {
        return editorialRepository.findAll();
    }

    @Override
    public Editorial actualizarEditorial(Editorial editorial) {
        return editorialRepository.save(editorial);
    }

    @Override
    public void eliminarEditorial(Long id) {
        editorialRepository.deleteById(id);
    }
}
