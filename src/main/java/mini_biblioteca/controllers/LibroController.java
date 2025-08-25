package mini_biblioteca.controllers;


import mini_biblioteca.entities.Autor;
import mini_biblioteca.entities.Categoria;
import mini_biblioteca.entities.Editorial;
import mini_biblioteca.entities.Libro;
import mini_biblioteca.services.AutorService;
import mini_biblioteca.services.CategoriaService;
import mini_biblioteca.services.EditorialService;
import mini_biblioteca.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private EditorialService editorialService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private AutorService autorService;

    /*
    Flujo completo
- El usuario entra a /libros/listar o /libros/.
- El controlador consulta todos los libros en la BD.
- Los agrega al modelo con el nombre "libros".
- Thymeleaf genera la vista listar_libros.html mostrando la tabla con los registros.

Explicación paso por paso
1. @GetMapping({"/listar", "/"})
	-Este método responde a solicitudes GET en dos rutas diferentes:
		- /libros/listar
		- /libros/

	-O sea, si entras a cualquiera de esas dos rutas, se ejecutará este método.

2. public String listarLibros(Model model)
	-Devuelve un String que representa el nombre de la vista HTML que se va a renderizar.
	-Recibe un parámetro Model model, que se usa para pasar datos desde el controlador hacia la vista.

3. List<Libro> libros = libroService.listarTodosLosLibros();
	- Llama al servicio (libroService) para obtener todos los libros de la base de datos.
	- El método listarTodosLosLibros() seguramente dentro usa el repositorio JPA con: List<Libro> findAll();

4. model.addAttribute("libros", libros);
	- Agrega la lista de libros al modelo bajo la clave "libros".
	- Esto hace que en el archivo HTML puedas usar esa lista, por ejemplo en Thymeleaf:
		 <tr th:each="libro : ${libros}">
   		 <td th:text="${libro.id}"></td>
    		 <td th:text="${libro.titulo}"></td>
   		 <td th:text="${libro.autor}"></td>
		 </tr>
5. return "libro/listar_libros";
	- Indica que la vista a mostrar está en: templates/libro/listar_libros.html
	- "libro" es la carpeta.
	- "listar_libros" es el archivo HTML donde se construye la tabla con los datos.

    * */

    @GetMapping({"/listar", "/"})
    public String listarLibros(Model model){
        List<Libro> libros = libroService.listarTodosLosLibros();
        model.addAttribute("libros", libros);
        return "libro/listar_libros";
    }

    /*
    Flujo completo
- El usuario hace clic en "Nuevo libro".
- El navegador va a /libros/nuevo.
- El controlador:
	- Crea un objeto vacío de Libro.
	- Carga las listas de editoriales, categorías y autores desde la BD.
	- Las manda todas al modelo.
- Thymeleaf genera un formulario donde el usuario puede:
	- Escribir título, ISBN, etc.
	- Seleccionar una editorial.
	- Seleccionar una categoría.
	- Seleccionar un autor.
- Al enviar el formulario, otro método con @PostMapping("/guardar") recibirá el objeto Libro lleno.

- Este método es el que prepara el formulario para registrar un nuevo libro.
1. @GetMapping("/nuevo")
	- Este método responde a una solicitud GET en la URL: /libros/nuevo
	- Generalmente, el usuario llega aquí después de hacer clic en un botón como "Nuevo libro" en la lista de libros.

2. Libro libro = new Libro();
	- Se crea un objeto vacío de la clase Libro.
	- Sirve como “contenedor” para que Thymeleaf pueda mapear los campos del formulario con los atributos de Libro.

3. model.addAttribute("libro", libro);
	- Se agrega ese objeto vacío al modelo bajo la clave "libro".
	- En la vista formulario_libro.html, con Thymeleaf, podrás hacer algo como:
		<input type="text" th:field="*{titulo}" placeholder="Título del libro">
	- Aquí titulo corresponde a un atributo de la clase Libro.

4. model.addAttribute("editoriales", editorialService.listarTodasLasEditoriales());
	- Obtiene de la base de datos todas las editoriales registradas y las manda al formulario.
	- Así, el formulario podrá tener un <select> para que el usuario elija la editorial:
		<select th:field="*{editorial}">
    		<option th:each="e : ${editoriales}"
            		th:value="${e.id}"
            		th:text="${e.nombre}">
    		</option>
		</select>

5. model.addAttribute("categorias", categoriaService.listarTodasLasCategorias());
	- Igual que con las editoriales, se cargan todas las categorías para mostrarlas en otro <select>.
	Ejemplo:
		<select th:field="*{categoria}">
    		<option th:each="c : ${categorias}"
            		th:value="${c.id}"
            		th:text="${c.nombre}">
    		</option>
		</select>

6. model.addAttribute("autor", autorService.listarTodosLosAutores());
	- Aquí ocurre algo curioso: el nombre del atributo es "autor" (en singular), pero se está guardando una lista de autores.
	- Lo más coherente sería llamarlo "autores", porque el formulario seguramente mostrará un combo o lista de selección múltiple.
	Ejemplo:
	<select th:field="*{autor}">
    	<option th:each="a : ${autor}"
           	 th:value="${a.id}"
            	th:text="${a.nombre}">
   	 </option>
	</select>

7. return "libro/formulario_libro";
	- Indica que la vista a mostrar está en: templates/libro/formulario_libro.html
	- Ese archivo es el formulario donde el usuario escribirá los datos del nuevo libro.


    * */


    @GetMapping("/nuevo")
     public String mostrarFormularioNuevoLibro(Model model){
        Libro libro = new Libro();
        model.addAttribute("libro", libro);
        model.addAttribute("editoriales", editorialService.listarTodasLasEditoriales());
        model.addAttribute("categorias", categoriaService.listarTodasLasCategorias());
        model.addAttribute("autores", autorService.listarTodosLosAutores());
        return "libro/formulario_libro";
    }


    /*
    Flujo completo
- El usuario llena el formulario de nuevo libro (título, ISBN, selecciona editorial, categoría y autores).
- Envía el formulario (POST /libros/guardar).
- El controlador recibe los datos y construye el objeto Libro.
- Se buscan las entidades relacionadas (editorial, categoría y autores) en la BD.
- Se asignan esas entidades al libro.
- Se guarda el libro en la BD con todas sus relaciones.
- El usuario es redirigido al listado de libros.
- Este método es el que se encarga de guardar un libro nuevo en la base de datos, junto con sus relaciones (editorial, categoría y autores).

Explicación paso a paso
1. @PostMapping("/guardar")
	- Este método responde a solicitudes POST en la URL /libros/guardar.
	- Generalmente viene de un formulario de nuevo libro o edición de libro.

2. Parámetros del método
	- @ModelAttribute Libro libro
		Spring construye un objeto Libro automáticamente con los datos enviados en el formulario (título, ISBN, año, etc.).
	- @RequestParam("editorialId") Long editorialId
		Obtiene el ID de la editorial seleccionado en el formulario (desde un <select>).
	- @RequestParam("categoriaId") Long categoriaId
		Obtiene el ID de la categoría seleccionada.
	- @RequestParam("autoresIds") List<Long> autoresIds
		Obtiene la lista de IDs de los autores seleccionados (si el formulario permite elegir varios autores).

3. Asignación de Editorial
		Optional<Editorial> editorial = editorialService.buscarPorId(editorialId);
		editorial.ifPresent(libro::setEditorial);
	- Busca la editorial en la BD por su ID.
	- Si existe, se asigna al libro con libro.setEditorial(...).

4. Asignación de Categoría
		Optional<Categoria> categoria = categoriaService.buscarPorId(categoriaId);
		categoria.ifPresent(libro::setCategoria);
	-Igual que con la editorial: se busca en la BD y se asigna al libro.

5. Asignación de Autores
		List<Autor> autores = autorService.buscarPorIds(autoresIds);
		libro.setAutores(new ArrayList<>(autores));
	- Busca en la BD todos los autores seleccionados.
	- Se asignan al libro como lista (setAutores).
	- Aquí se crea un new ArrayList<>(autores) para asegurarse de que sea una colección modificable (y no una lista inmutable).

6. Guardar el libro
		libroService.saveLibro(libro);
	-Una vez que el libro ya tiene editorial, categoría y autores asignados, se guarda en la base de datos.

7. Redirección
		return "redirect:/libros/listar";
	Después de guardar, redirige al usuario a la página donde se listan todos los libros.

    * */

    @PostMapping("/guardar")
    public String guardarLibro(@ModelAttribute Libro libro,
                               @RequestParam("editorialId") Long editorialId,
                               @RequestParam("categoriaId") Long categoriaId,
                               @RequestParam("autoresIds") List<Long> autoresIds){
        //Obtener y asignar la editorial y la categoría al libro
        Optional<Editorial> editorial = editorialService.buscarPorId(editorialId);
        editorial.ifPresent(libro::setEditorial);

        Optional<Categoria> categoria = categoriaService.buscarPorId(categoriaId);
        categoria.ifPresent(libro::setCategoria);

        List<Autor> autores = autorService.buscarPorIds(autoresIds);
        libro.setAutores(new ArrayList<>(autores));

        libroService.saveLibro(libro);
        return "redirect:/libros/listar";
    }


    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditarLibro(@PathVariable Long id, Model model){
        Optional<Libro> libro = libroService.buscarPorId(id);
        if(libro.isPresent()) {
            model.addAttribute("libro", libro.get());
            model.addAttribute("editoriales", editorialService.listarTodasLasEditoriales());
            model.addAttribute("categorias", categoriaService.listarTodasLasCategorias());
            model.addAttribute("autores", autorService.listarTodosLosAutores());
        }
        return "libro/formulario_libro";
    }

    /*
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarLibro(@PathVariable Long id, Model model){
        Optional<Libro> libro = libroService.buscarPorId(id);

        if(libro.isPresent()) {
            model.addAttribute("libro", libro.get()); // Se envía el objeto real, no el Optional
            model.addAttribute("editoriales", editorialService.listarTodasLasEditoriales());
            model.addAttribute("categorias", categoriaService.listarTodasLasCategorias());
            model.addAttribute("autores", autorService.listarTodosLosAutores()); // plural
            return "libro/formulario_libro";
        } else {
            // si no se encuentra el libro, redirigimos al listado
            return "redirect:/libros/listar";
        }
    }
*/

    /*
    Resumen:
Este método recibe los datos del formulario de edición de un libro, busca y asigna correctamente la editorial, la categoría y los autores, actualiza el ID del libro que se está editando y lo guarda en la base de datos. Luego redirige al listado de libros.

Explicación línea por línea:

1.- @PostMapping("/{id}/guardar")
	- Este método responde a solicitudes POST en la URL /libros/{id}/guardar.
	- Se suele invocar desde un formulario de edición, por ejemplo:
		<form th:action="@{/libros/{id}/guardar(id=${libro.id})}" method="post">

2.- Parámetros del método

	- @PathVariable Long id
		Captura el ID del libro desde la URL. Ejemplo: /libros/5/guardar → id = 5.

	- @ModelAttribute Libro libro
		Recibe automáticamente los campos del formulario y los convierte en un objeto Libro.
		(Spring hace el data binding con los name="" del formulario).

 	 - @RequestParam("editorialId") Long editorialId
		Captura el valor del campo seleccionado en el formulario (<select> con editoriales).

 	- @RequestParam("categoriaId") Long categoriaId
		Igual que el anterior, pero para la categoría.

	- @RequestParam("autoresIds") List<Long> autoresIds
		Recibe una lista de IDs de los autores seleccionados (por ejemplo, si el formulario tiene un 		<select multiple> o checkboxes).

3.- Asignar Editorial al libro
		Optional<Editorial> editorial = editorialService.buscarPorId(editorialId);
		editorial.ifPresent(libro::setEditorial);
	- Busca la editorial en la base de datos.
	- Si existe, se asigna al libro (libro.setEditorial(editorialEncontrada)).
4.- Asignar Categoría al libro
		Optional<Categoria> categoria = categoriaService.buscarPorId(categoriaId);
		categoria.ifPresent(libro::setCategoria);
	- Lo mismo, pero con la categoría.

5.- Asignar Autores al libro
		List<Autor> autores = autorService.buscarPorIds(autoresIds);
		libro.setAutores(new ArrayList<>(autores));
	- Busca los autores a partir de la lista de IDs recibidos.
	- Se asigna la lista de autores al libro.

6.- Forzar el ID del libro que se está editando
		libro.setId(id);
	- Como recibimos un Libro desde el formulario, este objeto podría no traer el id o venir vacío.
	- Aquí se asegura que el Libro tenga el ID correcto antes de guardarlo (para que JPA sepa que es una 	actualización y no un insert nuevo).

7.- Guardar el libro
		libroService.saveLibro(libro);
	- Llama al servicio para guardar los cambios.
	- Internamente, saveLibro() usará save() de Spring Data JPA → si el id existe, actualiza; si no, crea uno 	nuevo.

8.- Redirección al listado
		return "redirect:/libros/listar";
	- Después de guardar, redirige al listado de libros.
	- Esto evita que el usuario vuelva a enviar el formulario al refrescar la página (problema del POST-	Redirect-GET).
    * */

    @PostMapping("/{id}/actualizar")
    public String actualizarLibro(@PathVariable Long id,
                                @ModelAttribute Libro libro,
                               @RequestParam("editorialId")Long editorialId,
                               @RequestParam("categoriaId")Long categoriaId,
                               @RequestParam("autoresIds") List<Long> autoresIds){
        //obtener y asignar la editorial y la categoria al libro
        Optional<Editorial> editorial = editorialService.buscarPorId(editorialId);
        editorial.ifPresent(libro::setEditorial);


        Optional<Categoria> categoria = categoriaService.buscarPorId(categoriaId);
        categoria.ifPresent(libro::setCategoria);

        List<Autor> autores = autorService.buscarPorIds(autoresIds);
        libro.setAutores(new ArrayList<>(autores));

        libro.setId(id);
        libroService.saveLibro(libro);
        return "redirect:/libros/listar";

    }
    /*
    Explicación línea por línea:

1.- @GetMapping("/{id}/autores")
	-Este método se ejecuta cuando un cliente hace una petición GET a la URL: /libros/{id}/autores
		Ejemplo: /libros/5/autores
	-Aquí el id = 5 es el identificador del libro.

2.- public String mostrarAutoresDelLibro(@PathVariable Long id, Model model)
	- @PathVariable Long id: captura el id del libro directamente desde la URL.
	- Model model: sirve para enviar datos desde el controlador hacia la vista (Thymeleaf o JSP).

3.- Optional<Libro> libroOptional = libroService.buscarPorId(id);
	- Busca en la base de datos el libro con el ID proporcionado.
	- Se envuelve en un Optional<Libro> para evitar errores de NullPointerException si no existe.

4.- if(libroOptional.isPresent()) { ... }
	- Verifica si el libro fue encontrado.
	- Si no existe, no agrega nada al modelo y solo retorna la vista (probablemente vacía).

5.- Libro libro = libroOptional.get();
	- Obtiene el objeto Libro real del Optional.

6.- model.addAttribute("libro", libro);
	- Envía el libro a la vista para poder mostrar información básica, por ejemplo el título o la portada del libro.

7.- model.addAttribute("autores", libro.getAutores());
	- Obtiene la lista de autores relacionados con ese libro (libro.getAutores()).
	- La agrega al modelo para poder mostrarla en la vista.

8.- return "libro/mostrar_autores_libro";
	- Devuelve el nombre de la vista (plantilla HTML Thymeleaf, por ejemplo src/main/resources/templates/libro/mostrar_autores_libro.html).
	- En esa página se mostrarán los autores del libro seleccionado.

    * */

    @GetMapping("/{id}/autores")
    public String mostrarAutoresDelLibro(@PathVariable Long id, Model model){
        Optional<Libro> libroOptional = libroService.buscarPorId(id);
        if(libroOptional.isPresent()){
            Libro libro = libroOptional.get();
            model.addAttribute("libro", libro);
            model.addAttribute("autores", libro.getAutores());
        }
        return "libro/mostrar_autores_libro";
    }


    @GetMapping("/{id}/eliminar")
    public String eliminarLibro(@PathVariable Long id){
        libroService.eliminarLibro(id);
        return "redirect:/libros/listar";
    }

}
