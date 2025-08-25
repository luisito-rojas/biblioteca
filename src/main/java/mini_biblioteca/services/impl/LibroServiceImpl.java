package mini_biblioteca.services.impl;

import mini_biblioteca.entities.Categoria;
import mini_biblioteca.entities.Libro;
import mini_biblioteca.repositories.LibroRepository;
import mini_biblioteca.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public Libro saveLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public Optional<Libro> buscarPorId(Long id) {
        return libroRepository.findById(id);
    }


    @Override
    public List<Libro> listarTodosLosLibros() {
        return libroRepository.findAll();
    }

    @Override
    public Libro actualizarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }

    //Este metodo que se retorna esta declarado en la  interface LibroRepository
    @Override
    public Optional<Libro> buscarPorTitulo(String titulo) {
        return libroRepository.findByTitulo(titulo);
    }

    //Este metodo que se retorna esta declarado en la  interface LibroRepository
    @Override
    public List<Libro> buscarPorCategoria(Categoria categoria) {
        return libroRepository.findByCategoria(categoria);
    }
}

// interface LibroRepository findByCategoria(), findByTitulo