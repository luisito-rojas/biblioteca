package mini_biblioteca.controllers;


import mini_biblioteca.entities.Categoria;
import mini_biblioteca.entities.Libro;
import mini_biblioteca.services.CategoriaService;
import mini_biblioteca.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {


    @Autowired
    CategoriaService categoriaService;

    @Autowired
    LibroService libroService;

    /*

Flujo completo
- El usuario entra a /categorias/listar o /categorias/.
- El controlador obtiene todas las categorías desde la BD.
- Las manda al modelo con el nombre "categorias".
- Thymeleaf renderiza el HTML listar_categorias.html mostrando la tabla con los datos.

 Explicación paso a paso
1. @GetMapping({"/listar", "/"})
	-Este método responde a solicitudes GET en dos rutas diferentes:
		localhost:8080/categorias/listar
		localhost:8080/categorias/
	-Es decir, si entras a cualquiera de esas dos rutas, se ejecutará este método.

2. public String listarCategorias(Model model)
	-Método que devuelve un String, que representa el nombre de la vista HTML que se va a renderizar.
	-Model model: se usa para enviar datos desde el controlador a la vista (por ejemplo, pasarle la lista de categorías).

3. List<Categoria> categorias = categoriaService.listarTodasLasCategorias();
	-Llama al servicio (categoriaService) para obtener todas las categorías de la base de datos.
	-El método listarTodasLasCategorias() internamente probablemente usa el repositorio: List<Categoria> findAll();

4. model.addAttribute("categorias", categorias);
	-Se agrega la lista de categorías al model bajo la clave "categorias".
	-Esto significa que en el archivo Thymeleaf (listar_categorias.html) podrás usar esa lista, por ejemplo:
		<tr th:each="categoria : ${categorias}">
    		<td th:text="${categoria.id}"></td>
    		<td th:text="${categoria.nombre}"></td>
		</tr>

5. return "categoria/listar_categorias";
	- Indica que la vista que se va a mostrar está en: templates/categoria/listar_categorias.html
	- "categoria" es el nombre de la carpeta.
	- "listar_categorias" es el nombre del archivo HTML dentro de esa carpeta.
    * */

    @GetMapping({"/listar", "/"})
    public String listarCategorias(Model model){
        List<Categoria> categorias = categoriaService.listarTodasLasCategorias();
        model.addAttribute("categorias", categorias);
        //categoria es el nombre de la carpeta y listar_categorias es el nombre del archivo html
        return "categoria/listar_categorias";
    }

    //metodo para mostrar el formulario de nueva categoria
    /*

Flujo completo
- El usuario hace clic en “Nueva categoría”.
- El navegador va a /categorias/nuevo.
- El controlador crea un objeto vacío Categoria.
- Lo envía al modelo como "categoria".
- Thymeleaf genera un formulario HTML en blanco pero listo para mapearse a Categoria.
- Cuando el usuario complete el formulario y lo envíe, otro método (@PostMapping("/guardar")) recibirá ese objeto lleno.

1. @GetMapping("/nuevo")
	- Este método se ejecuta cuando alguien entra a la URL: /categorias/nuevo
	- Normalmente, el usuario llega aquí después de hacer clic en un botón como "Nueva categoría" en la lista de categorías.

2. public String mostrarFormularioNuevaCategoria(Model model)
	- Retorna un String, que será el nombre de la vista HTML que se va a mostrar (en este caso el formulario).
	- Recibe un Model model que permite pasar datos desde el controlador hacia la vista.

3. Categoria categoria = new Categoria();
	- Crea un objeto vacío de Categoria.
	- Esto sirve para que el formulario esté listo para vincular datos con Thymeleaf (th:field).

4. model.addAttribute("categoria", categoria);
	- Se agrega ese objeto vacío al modelo con el nombre "categoria".
	- Gracias a esto, en la vista HTML se podrán enlazar los campos del formulario a este objeto.

	Ejemplo en Thymeleaf:
		-<input type="text" th:field="*{nombre}" placeholder="Nombre de la categoría"/>
	Aquí, nombre corresponde al atributo de la clase Categoria.

5. return "categoria/formulario_categoria";
	-Se devuelve la vista: templates/categoria/formulario_categoria.html
	- Ese archivo contiene el formulario donde el usuario llenará los datos de la nueva categoría.
    * */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaCategoria(Model model){
        Categoria categoria = new Categoria();
        model.addAttribute("categoria", categoria);
        return "categoria/formulario_categoria";
    }

    /*
    Explicación línea por línea
-@PostMapping("/guardar")
	-Indica que este método del controlador se ejecuta cuando se hace una petición POST a la ruta /categorias/guardar.
	-Normalmente viene de un formulario HTML enviado con method="post".

-public String guardarCategoria(@ModelAttribute Categoria categoria)
	-@ModelAttribute hace que Spring construya un objeto Categoria automáticamente con los datos enviados en el formulario (los input del HTML).
	-Por ejemplo, si el formulario tenía campos como nombre, descripcion, esos valores se inyectan en el objeto categoria.

-Categoria categoriaGuardada = categoriaService.guardarCategoria(categoria);
	-Se manda a guardar la categoría (usando el servicio, que a su vez usará un repositorio JPA).
	-Esto regresa la categoría persistida en la base de datos, normalmente con el ID ya generado.

-List<Libro> libros = libroService.buscarPorCategoria(categoriaGuardada);
	-Se buscan todos los libros asociados a esa categoría.
	-Aquí se asume que existe una relación Categoria → Libro.

-categoriaGuardada.setLibros(libros);
	-Se asigna la lista de libros encontrados a la categoría que ya se guardó en BD.

-categoriaService.guardarCategoria(categoria);
	- Aquí hay algo raro: se está guardando de nuevo la categoría, pero no categoriaGuardada, sino el objeto original categoria.
	-Esto podría ser un descuido. Lo lógico sería guardar nuevamente categoriaGuardada, porque es el que ya contiene la lista de libros asignada.

-return "redirect:/categorias/listar";
	-Después de guardar, se redirige al usuario a la vista donde se listan todas las categorías.
	-"redirect:/..." le dice a Spring que no cargue una vista directamente, sino que mande al navegador a otra URL.
    * */

    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria){
        Categoria categoriaGuardada = categoriaService.guardarCategoria(categoria);
        List<Libro> libros = libroService.buscarPorCategoria(categoriaGuardada);
        categoriaGuardada.setLibros(libros);
        //categoriaService.guardarCategoria(categoria);
        categoriaService.guardarCategoria(categoriaGuardada); // <- aquí debería ser categoriaGuardada
        return "redirect:/categorias/listar";
    }


    /*

Flujo completo
	-El usuario hace clic en editar en la lista de categorías.
	-Ejemplo: /categorias/2/editar
	-El controlador busca la categoría con ID = 2 en la BD.
	-Si la encuentra, la manda al modelo bajo la clave "categoria".
	-Thymeleaf (u otro motor de plantillas) la usará para precargar el formulario con los datos existentes.
	-El usuario podrá modificar y luego enviará el formulario a otro método (@PostMapping("/guardar")) para guardar los cambios.

Explicación línea por línea
@GetMapping("/{id}/editar")
	-Indica que este método responde a las solicitudes GET en una URL como: /categorias/5/editar
	-El {id} es una variable de ruta (path variable). Ejemplo: si escribes /categorias/10/editar, el valor 10 se pasará al método.

-public String mostrarFormularioEditarCategoria(@PathVariable Long id, Model model)
	-@PathVariable Long id: Spring inyecta en id el número que venía en la URL.
	-Ejemplo: si era /categorias/3/editar, entonces id = 3.
	-Model model: es un objeto que permite enviar datos a la vista (Thymeleaf, JSP, etc.).

-Optional<Categoria> categoria = categoriaService.buscarPorId(id);
	-Llama al servicio para buscar la categoría con el id recibido.
	-El servicio regresa un Optional<Categoria> (para evitar null).

-categoria.ifPresent(value -> model.addAttribute("categoria", value));
	-Se usa el Optional.
	-Si la categoría existe (isPresent), entonces la agrega al modelo con la clave "categoria".
	-Esa clave "categoria" será usada en el formulario HTML para mostrar los datos actuales de la categoría (ejemplo: nombre, descripción, etc.).

-return "categoria/formulario_categoria";
	-Devuelve la vista formulario_categoria.html (dentro de la carpeta categoria/).
	-Esa vista normalmente tiene un formulario (<form>) que ya estará rellenado con los valores de la categoría a editar.
    * */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditarCategoria(@PathVariable Long id, Model model){
        Optional<Categoria> categoria = categoriaService.buscarPorId(id);
        categoria.ifPresent(value -> model.addAttribute("categoria", value));
        return "categoria/formulario_categoria";
    }


    /*

 Flujo completo
-El usuario entra al formulario de edición con /categorias/5/editar.
-Modifica algunos campos (ejemplo: cambia el nombre).
-Envía el formulario (POST /categorias/5/actualizar).
-El controlador busca en BD la categoría 5.
-Copia la lista de libros existentes (para no borrarlos por accidente).
-Guarda los cambios con categoriaService.actualizarCategoria.
-Redirige a /categorias/listar.


Explicación paso por paso
1-@PostMapping("/{id}/actualizar")
	-Este método responde a solicitudes POST en una URL como: /categorias/5/actualizar
	-Generalmente, esta URL es la que apunta el formulario de edición (<form method="post">) cuando el usuario guarda los cambios.

2. Parámetros del método
	@PathVariable Long id:Recibe el ID de la categoría desde la URL.
		Ejemplo: si la ruta era /categorias/3/actualizar, entonces id = 3.

	@ModelAttribute Categoria categoria: Spring construye un objeto Categoria con los datos enviados en el formulario (nombre, descripción, etc.).
	- Este objeto es nuevo en memoria, todavía no es el mismo que está guardado en la BD.

3. Categoria categoriaActual = categoriaService.buscarPorId(id).orElse(null);
	-Se busca en la BD la categoría actual con ese id.
	-Se obtiene el objeto existente (categoriaActual).
	-Si no existe, se asigna null.

4. if(categoriaActual != null){ ... }
	-Verifica que la categoría exista en BD antes de intentar actualizarla.

5. categoria.setLibros(categoriaActual.getLibros());
	-Este paso es muy importante:
		El objeto categoria recibido del formulario no tiene los libros cargados (solo trae lo que llenó el usuario).
		Para no perder la relación Categoria → Libros, se copian los libros de la categoría actual (categoriaActual) al nuevo objeto (categoria).

6. categoriaService.actualizarCategoria(categoria);
	-Ahora que el objeto categoria ya tiene:
		Los datos nuevos del formulario.
		La lista de libros que ya tenía antes.
		Se manda al servicio para guardar los cambios en la BD.

7. return "redirect:/categorias/listar";
	-Finalmente, redirige al listado de categorías, mostrando la tabla actualizada.

    * */
    @PostMapping("/{id}/actualizar")
    public String actualizarCategoria(@PathVariable Long id, @ModelAttribute Categoria categoria){
        Categoria categoriaActual = categoriaService.buscarPorId(id).orElse(null);
        if(categoriaActual != null){
            categoria.setLibros(categoriaActual.getLibros());
            categoriaService.actualizarCategoria(categoria);
        }
        return "redirect:/categorias/listar";
    }

//@GetMapping : por que una vez eliminada la categoria nos va a redirigir a un archivo html
    /*

Explicación paso por paso
1. @GetMapping("/{id}/eliminar")
	-Este método responde a solicitudes GET en una URL como: /categorias/5/eliminar
	-Es decir, cuando un usuario hace clic en un botón o enlace para eliminar una categoría, el navegador hace la petición GET a esa ruta

2. @PathVariable Long id
	-Extrae el ID de la categoría directamente desde la URL.
	-Ejemplo: si entras a /categorias/7/eliminar, entonces id = 7.

3. categoriaService.eliminarCategoria(id);
	-Llama al servicio para eliminar la categoría con ese ID.
	-El Service a su vez usará el Repository de JPA, probablemente con algo como: void deleteById(Long id);
	-Aquí es donde realmente se borra el registro de la BD.

4. return "redirect:/categorias/listar";
	-Después de eliminar, se redirige al listado de categorías para mostrar la tabla ya sin la categoría borrada.
	-"redirect:/..." evita que se quede en la misma página y recarga la lista actualizada.
    * */
    @GetMapping("/{id}/eliminar")
    public String eliminarCategoria(@PathVariable Long id){
        categoriaService.eliminarCategoria(id);
        return "redirect:/categorias/listar";
    }



}
