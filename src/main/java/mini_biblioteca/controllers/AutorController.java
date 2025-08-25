package mini_biblioteca.controllers;


import mini_biblioteca.entities.Autor;
import mini_biblioteca.repositories.AutorRepository;
import mini_biblioteca.services.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/autores")
public class AutorController {

    @Autowired
    private AutorService autorService;
    //==================================================================================================================
    /* - Este método  sirve para mostrar la lista de autores en pantalla.
         - En resumen:
    -Este método recibe la petición GET a /listar o /, obtiene todos los autores desde la base de datos, los mete en el
    modelo y los manda a la vista lista_autores.html para mostrarlos en una tabla o listado.
    */

    /*
    -Indica que este método se ejecutará cuando alguien entre por GET a:
        *  localhost:8080/autores/listar
                o
        * localhost:8080/autores/listar/(la raíz del módulo de autores).
    - Esto significa que tienes dos formas de acceder al listado de autores en la aplicación.
    * */
    @GetMapping({"listar", "/"})
    /*
    2. public String listarAutores(Model model)
    - El método recibe un parámetro Model, que se usa para pasar datos del backend a la vista (HTML).
    * */
    public String listarAutores(Model model){
        /*
    3. List<Autor> autores = autorService.listarTodosLosAutores();
    - Aquí se llama al servicio (autorService) para obtener la lista de todos los autores guardados en la base de datos.
    - El servicio probablemente está usando un AutorRepository (Spring Data JPA) que hace algo como findAll().
    */
        List<Autor> autores = autorService.listarTodosLosAutores();
        /*
    4. model.addAttribute("autores", autores);
    - Se agrega la lista de autores al modelo, con la clave "autores".
    - De esta forma, la vista podrá acceder a esa lista y recorrerla para mostrar los datos.
    - Ejemplo con Thymeleaf en lista_autores.html:
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Apellido</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="autor : ${autores}">
                    <td th:text="${autor.id}"></td>
                    <td th:text="${autor.nombre}"></td>
                    <td th:text="${autor.apellido}"></td>
                </tr>
            </tbody>
        </table>
    */
        model.addAttribute("autores", autores);
    /*
    5. return "autor/lista_autores";
    - Indica que se debe renderizar la vista ubicada en: templates/autor/lista_autores.html
    * */
        return "autor/lista_autores";
    }

//=====================================================================================================================

    /*
    - Este metodo nos sirve para mostrar el formulario donde se registran autores nuevos.
    1. @GetMapping("/nuevo")
    - Indica que este método se ejecutará cuando llegue una petición HTTP GET a la URL /nuevo.
    - Es decir, cuando en el navegador entres a: http://localhost:8080/nuevo
    - Normalmente esta URL está asociada a un botón como "Agregar Autor" en la vista del listado.
    * */
    @GetMapping("/nuevo")
    /*
    2. public String mostrarFormularioNuevoAutor(Model model)
    - El método recibe como parámetro un objeto de tipo Model.
    - Model se usa para enviar datos desde el controlador hacia la vista (plantilla HTML con Thymeleaf, por ejemplo).

    * */
    public String mostrarFormularioNuevoAutor(Model model){
    /*
    3. model.addAttribute("autor", new Autor());
    - Aquí se crea un objeto vacío de la entidad Autor.
    - Luego, se agrega al modelo con la clave "autor".
    - De esta manera, cuando se renderice la vista, Thymeleaf podrá acceder a ese objeto y enlazarlo con los campos del formulario.
    -Ejemplo de vista con Thymeleaf:
        <form action="/guardar" method="post" th:object="${autor}">
                <input type="text" th:field="*{nombre}" placeholder="Nombre del autor">
                <input type="text" th:field="*{apellido}" placeholder="Apellido del autor">
             <button type="submit">Guardar</button>
        </form>
    * */
        model.addAttribute("autor", new Autor());
    /*
    return "autor/formulario_autor";
    - Indica que se debe renderizar la vista ubicada en: templates/autor/formulario_autor.html
    - Es decir, este método no procesa datos todavía, solo muestra la página con el formulario vacío.
    * */
        return "autor/formulario_autor";
    }

    //=====================================================================================================================
    /*
    -En resumen:
    Este método recibe los datos de un formulario, los convierte en un objeto Autor, los guarda en la BD mediante el
    servicio, y luego redirige al listado de autores.
    1. @PostMapping("/guardar")
    Indica que este método se ejecutará cuando llegue una petición HTTP POST a la URL /guardar.
    Normalmente viene de un formulario en HTML que tiene algo como:
    * */
    @PostMapping("/guardar")
    /*
    2. public String guardarAutor(@ModelAttribute Autor autor)
    El parámetro @ModelAttribute Autor autor significa que Spring va a crear un objeto Autor automáticamente y le asignará
    los valores enviados desde el formulario (usando name de los inputs).
	    <input type="text" name="nombre">
	    <input type="text" name="apellido">
    Se mapeará a:
	    autor.setNombre(valorDelInputNombre);
	    autor.setApellido(valorDelInputApellido);
    */
    public String guardarAutor(@ModelAttribute Autor autor){
    /*
    3. autorService.guardarAutor(autor);
    Aquí se llama al servicio (autorService) que se encarga de la lógica de negocio, por ejemplo guardar el autor en la
    base de datos mediante un repositorio JPA.
    Esto ayuda a separar la lógica de negocio del controlador.
    * */
        autorService.guardarAutor(autor);
    /*
    4. return "redirect:/autores/listar";
    En lugar de devolver una vista (.html o .jsp), retorna "redirect:/autores/listar".
    Esto le dice a Spring: haz una redirección hacia la ruta /autores/listar.
    Es una buena práctica porque evita que al refrescar el navegador se vuelva a enviar el formulario
    (problema del doble submit en POST).
    * */
        return "redirect:/autores/listar";
    }

//==========================================================================================================================
    /*

- Este método sirve para mostrar el formulario de edición de un autor existente
- En resumen:
   Este método permite cargar en un formulario los datos de un autor ya existente para que el usuario los modifique.
   Recibe el id por la URL.
   Busca el autor en la BD.
   Si existe, lo envía a la vista.
- Muestra el formulario prellenado con los datos actuales.
1. @GetMapping("/editar/{id}")
    - Este método responde a una petición GET en la URL:
	/editar/1
	/editar/2
	/editar/5
    - El número (1, 2, 5, etc.) es el ID del autor que se quiere editar.
    - Se captura gracias a la anotación @PathVariable.
2. public String mostrarFormularioEditarAutor(@PathVariable Long id, Model model)
    - @PathVariable Long id: indica que el valor que viene en la URL ({id}) se asigna a la variable id.
       Ejemplo: si accedes a /editar/3, entonces id = 3.
    - Model model: se usa para enviar datos a la vista (el HTML con Thymeleaf).
3. Optional<Autor> autor = autorService.buscarPorId(id);
    - Se llama al servicio autorService para buscar el autor en la base de datos por su id.
    - El servicio devuelve un Optional<Autor> porque puede que el autor exista o no.
4. autor.ifPresent(value -> model.addAttribute("autor", value));
    - Si el Optional contiene un autor, se lo agrega al modelo bajo la clave "autor".
    - Esto hará que en la vista formulario_autor.html, los campos del formulario se llenen automáticamente con los       datos actuales del autor.
        Ejemplo de formulario en Thymeleaf:
	<form action="/guardar" method="post" th:object="${autor}">
    	<input type="hidden" th:field="*{id}"/>
    	<input type="text" th:field="*{nombre}" placeholder="Nombre">
    	<input type="text" th:field="*{apellido}" placeholder="Apellido">
    	<button type="submit">Actualizar</button>
	</form>
5. return "autor/formulario_autor";
   - Devuelve la vista formulario_autor.html ubicada en la carpeta templates/autor/.
    - Es el mismo formulario que se usa para crear un autor nuevo, pero esta vez llega con los datos cargados en      	los inputs para editarlos.
    * */

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarAutor(@PathVariable Long id,Model model){
        Optional<Autor> autor = autorService.buscarPorId(id);
        autor.ifPresent(value -> model.addAttribute("autor", value));
        return "autor/formulario_autor";
    }

    /*
    - Este método recibe el id de un autor, lo elimina usando el servicio y después redirige al listado de autores.
    - En tu código original solo había dos detalles a corregir:
    1. @GetMapping("/eliminar/{id}")
    Este método se ejecuta cuando haces una petición GET a una URL como: /eliminar/5
    El número que aparece en {id} corresponde al ID del autor que quieres eliminar.

    2. public String eliminarAutor(@PathVariable Long id)
    - El parámetro @PathVariable Long id captura el valor que viene en la URL.
      Ejemplo:
         - Si accedes a /eliminar/10, entonces id = 10.
    3. autorService.eliminarAutor(id);
     - Aquí se delega al servicio (autorService) la tarea de eliminar el autor en la base de datos.
    4. return "redirect:/autores/listar";
     - Cuando se elimina el autor, el método redirige al listado de autores.
     - "redirect:/..." en Spring Boot significa:
     - No muestres una vista directamente.
     - En su lugar, haz una nueva petición GET a la URL indicada.
    */

    @GetMapping("/eliminar/{id}")
    public String eliminarAutor(@PathVariable Long id) throws ClassNotFoundException {
        autorService.eliminarAutor(id);
        return  "redirect:/autores/listar";
    }


    @PostMapping("/actualizar")
    public String actualizarAutor(@ModelAttribute Autor autor){
        autorService.actualizarAutor(autor);
        return  "redirect:/autores/listar";
    }

}
