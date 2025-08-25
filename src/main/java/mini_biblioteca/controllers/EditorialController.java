package mini_biblioteca.controllers;


import mini_biblioteca.entities.Editorial;
import mini_biblioteca.services.EditorialService;
import mini_biblioteca.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/editoriales")
public class EditorialController {

    @Autowired
    private EditorialService editorialService;

    @Autowired
    private LibroService libroService;

    /*
    Resumen:
Este método muestra el formulario de registro de una nueva editorial. Crea un objeto vacío de tipo Editorial, lo envía al modelo y retorna la vista formulario_editorial.html.
Gracias a eso, en la vista se pueden mapear los campos del formulario con los atributos de la clase Editorial.

1.- @GetMapping("/nuevo")
	- Define que este método se ejecutará cuando se haga una petición HTTP GET a la URL: /editoriales/nuevo
	  (asumiendo que el controlador esté mapeado con @RequestMapping("/editoriales")).
	-Generalmente esta ruta se usa para mostrar el formulario de registro de una nueva entidad (en este caso, una editorial).

2.- public String mostrarFormularioNuevaEditorial(Model model)
	- Método que devuelve un String, el cual será interpretado como el nombre de la vista (archivo HTML de Thymeleaf o JSP).
	- Recibe como parámetro un Model model, que permite pasar datos desde el controlador hacia la vista.

3.- Editorial editorial = new Editorial();
	- Se crea un objeto vacío de la clase Editorial.
	- Esto sirve para que en el formulario (formulario_editorial.html) se tenga un objeto con el que Thymeleaf pueda hacer el data binding
	(enlazar campos del formulario a las propiedades de Editorial).

4.- model.addAttribute("editorial", editorial);
	- Agrega al modelo el objeto Editorial vacío con la clave "editorial".
	- En la vista Thymeleaf podrás referirte a este objeto como ${editorial}.
		Ejemplo en la vista:
		<input type="text" th:field="*{nombre}" placeholder="Nombre de la editorial" />

5.- return "editorial/formulario_editorial";
	- Retorna el nombre de la vista que se renderizará.
	- Aquí buscará el archivo:
		src/main/resources/templates/editorial/formulario_editorial.html
	- Ese archivo será el formulario donde el usuario escribirá los datos de la nueva editorial.
    * */


    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaEditorial(Model model){
        Editorial editorial = new Editorial();
        model.addAttribute("editorial", editorial);
        return "editorial/formulario_editorial";
    }

    /*
    Resumen:
- Este método recibe los datos del formulario de una nueva editorial (gracias a @ModelAttribute),
  guarda la editorial en la base de datos con ayuda del servicio y luego redirige al listado de editoriales.

- Este método es el encargado de procesar el formulario de la editorial y guardarla en la base de datos.

Explicación paso a paso:

@PostMapping("/guardar")
	- Indica que este método se ejecuta cuando se hace una petición HTTP POST a la URL: /editoriales/guardar
	- Normalmente esta petición viene desde un formulario HTML como este:
		<form th:action="@{/editoriales/guardar}" th:object="${editorial}" method="post">
    			<input type="text" th:field="*{nombre}" placeholder="Nombre" />
    			<button type="submit">Guardar</button>
		</form>

2.- public String guardarEditorial(@ModelAttribute Editorial editorial)
	@ModelAttribute Editorial editorial: Spring automáticamente recibe los valores enviados en el formulario y los convierte en un objeto Editorial.
	Ejemplo:
	Si el formulario envía:
		nombre = "Penguin Random House"
		pais   = "México"
	entonces tendrás un objeto:
		editorial.getNombre() = "Penguin Random House";
		editorial.getPais()   = "México";

3.- Editorial editorialGuardada = editorialService.guardarEditorial(editorial);
	- Se manda el objeto Editorial al servicio para guardarlo en la base de datos (generalmente con repository.save(editorial) dentro del servicio).
	- El objeto devuelto (editorialGuardada) ya tendrá asignado su ID generado (si usas @GeneratedValue).

4.- return "redirect:/editoriales/listar";
	- Después de guardar, redirige al listado de editoriales.
	- Esto implementa el patrón PRG (Post-Redirect-Get) → evita que el usuario vuelva a enviar el formulario si recarga la página

    * */

    @PostMapping("/guardar")
    public String guardarEditorial(@ModelAttribute Editorial editorial){
        Editorial editorialGuardada = editorialService.guardarEditorial(editorial);
        return "redirect:/editoriales/listar";
    }

    /*
    Resumen:

- Este método carga la lista de todas las editoriales desde la base de datos, la pasa al modelo y muestra la vista listar_editoriales.html, donde se despliegan en pantalla.

-Este código es el encargado de mostrar la lista de todas las editoriales registradas en tu aplicación.

- Explicación paso por paso:

1.- @GetMapping({"/listar","/"})
	- Este método responderá a peticiones HTTP GET en dos rutas: /editoriales/listar o /editoriales/
	- Esto significa que puedes acceder al listado tanto con /listar como con la raíz del controlador.

2.- Ejemplo: si tu controlador está anotado con @RequestMapping("/editoriales"), entonces:
		http://localhost:8080/editoriales/listar
		http://localhost:8080/editoriales/
	-mostrarán lo mismo.

3.- public String listarEditoriales(Model model)
	- El método devuelve un String que representa el nombre de la vista a renderizar (archivo Thymeleaf o JSP).
	- Recibe un parámetro Model model, que sirve para pasar datos desde el controlador hacia la vista.

4.- List<Editorial> editoriales = editorialService.listarTodasLasEditoriales();
	- Llama al servicio editorialService para obtener todas las editoriales almacenadas en la base de datos.
	- Este servicio seguramente internamente usa editorialRepository.findAll() (Spring Data JPA).
	- El resultado es una lista de objetos Editorial.

5.- model.addAttribute("editoriales", editoriales);
	- Se agrega al modelo la lista de editoriales con la clave "editoriales".
	- En la vista Thymeleaf podrás acceder a esta lista con ${editoriales}.
	- Ejemplo en HTML:
		<tr th:each="editorial : ${editoriales}">
    		<td th:text="${editorial.id}"></td>
    		<td th:text="${editorial.nombre}"></td>
		</tr>

6.- return "editorial/listar_editoriales";
	- Indica que se renderizará la vista:
		src/main/resources/templates/editorial/listar_editoriales.html
	-En este archivo Thymeleaf se mostrará la tabla o lista de editoriales.
    * */

    @GetMapping({"/listar","/"})
    public String listarEditoriales(Model model){
        List<Editorial> editoriales = editorialService.listarTodasLasEditoriales();
        model.addAttribute("editoriales", editoriales);
        return "editorial/listar_editoriales";
    }

    /*

Resumen práctico:

Este método recibe el id de una editorial desde la URL, la busca en la base de datos, si existe la manda a la vista junto con sus libros, y renderiza la página mostrar_editorial.

Ejemplo de flujo:

URL: http://localhost:8080/editorial/3

Se busca la editorial con id = 3.

Si existe, se agregan al modelo:

editorial → datos de la editorial

libros → lista de libros de esa editorial

Se muestra la vista mostrar_editorial.html.

Explicación línea por línea:

1.- @GetMapping("/{id}")
	- Esta anotación indica que el método responderá a solicitudes HTTP GET en la ruta /editorial/{id}.
	- {id} es un parámetro dinámico de la URL.
		Ejemplo: si entras a http://localhost:8080/editorial/5, el valor 5 se pasará como parámetro al método.

2.- public String mostrarEditorial(@PathVariable Long id, Model model)
	- @PathVariable Long id: extrae el valor de {id} de la URL y lo asigna a la variable id.
	- Model model: se utiliza para enviar datos desde el backend hacia la vista (Thymeleaf, JSP, etc.).

3.- Optional<Editorial> editorialOptional = editorialService.buscarPorId(id);
	- Llama al servicio editorialService para buscar una editorial en la base de datos por su id.
	- Como podría no existir, se usa un Optional<Editorial> para evitar NullPointerException.

4.- if(editorialOptional.isPresent())
	- Verifica si realmente existe la editorial con el id dado.
	- Si no existe, el if no se ejecuta y se sigue al return.

5.- Editorial editorial = editorialOptional.get();
	- Obtiene la instancia real de la editorial contenida dentro del Optional.

6.- model.addAttribute("editorial", editorial);
	- Agrega el objeto editorial al modelo con el nombre "editorial".
	- Esto permite acceder a la editorial en la vista (por ejemplo en Thymeleaf con ${editorial.nombre}).

7.- model.addAttribute("libros", editorial.getLibros());
	- Obtiene la lista de libros asociados a esa editorial y la agrega al modelo con el nombre "libros".
	- En la vista se puede iterar esa lista para mostrar los libros de la editorial.

8.- return "editorial/mostrar_editorial";
	- Indica que debe renderizarse la vista ubicada en la plantilla editorial/mostrar_editorial.html (o .jsp dependiendo del motor de plantillas).

	- Gracias al model, en esa vista tendrás disponibles las variables editorial y libros.
    * */

    @GetMapping("/{id}")
    public String mostrarEditorial(@PathVariable Long id, Model model){
        Optional<Editorial> editorialOptional = editorialService.buscarPorId(id);
        if(editorialOptional.isPresent()){
            Editorial editorial = editorialOptional.get();
            model.addAttribute("editorial", editorial);
            model.addAttribute("libros", editorial.getLibros());
        }
        return "editorial/mostrar_editorial";
    }

    /*

Resumen práctico:
	- Este método sirve para abrir el formulario de edición de una editorial.
	- El usuario entra a la URL /editorial/{id}/editar.
	- Se busca la editorial en la base de datos.
	- Si existe, se pasa al model para que los datos se muestren en el formulario.
	- Se carga la vista formuario_editorial.html, que tendrá campos precargados con los datos de esa editorial.

Ejemplo de flujo:
	- URL: http://localhost:8080/editorial/7/editar
	- Se busca la editorial con id = 7.
	- Si se encuentra, se agrega al modelo como "editorial".
	- Se abre la vista formuario_editorial.html mostrando un formulario con los datos de la editorial lista para modificar.

Explicación línea por línea:

1.- @GetMapping("/{id}/editar")
	- Define una ruta HTTP GET.
	- Responderá a URLs como: http://localhost:8080/editorial/5/editar
	- El valor {id} es dinámico y se pasará como parámetro al método.

2.- public String mostrarFormularioEditarEditorial(@PathVariable Long id, Model model)
	- @PathVariable Long id: toma el valor {id} de la URL y lo asigna a la variable id.
	Ejemplo: si la URL es /editorial/5/editar, entonces id = 5.
	- Model model: se usa para enviar datos del backend a la vista (plantilla HTML)

3.- Optional<Editorial> editorial = editorialService.buscarPorId(id);
	- Se llama al servicio para buscar la editorial en la base de datos según el id.
	- Como puede que exista o no exista, el resultado es un Optional<Editorial>.

4.- editorial.ifPresent(value -> model.addAttribute("editorial", value));
	- Aquí se usa una expresión lambda para simplificar.
	- ifPresent(...) se ejecuta solo si la editorial existe dentro del Optional.
	- En ese caso, agrega la editorial al model con el nombre "editorial".
	- Esto permite que la vista tenga precargados los datos de la editorial a editar.
	- Equivalente a:
			if(editorial.isPresent()){
    			model.addAttribute("editorial", editorial.get());
			}

5.- return "editorial/formuario_editorial";
	- Indica que se debe renderizar la vista (plantilla HTML/Thymeleaf) ubicada en editorial/formuario_editorial.html.
	- Esa vista normalmente contiene un formulario con los datos de la editorial cargados para editarlos.
    * */

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditarEditorial(@PathVariable Long id,  Model model){
        Optional<Editorial> editorial = editorialService.buscarPorId(id);
        editorial.ifPresent(value -> model.addAttribute("editorial", value));
        return "editorial/formulario_editorial";
    }

/*
Resumen práctico:
	- Este método sirve para actualizar una editorial existente.
	- Se recibe el id por la URL y los datos editados desde el formulario.
	- Se busca la editorial en la BD.
	- Si existe, se actualiza su nombre (u otros campos).
	- Se guarda en la BD.
	- Se redirige al listado de editoriales.

Ejemplo de flujo:
	- El usuario entra a /editorial/5/editar y edita el nombre.
	- Al enviar el formulario, se hace un POST a /editorial/5/actualizar.
	- El método recibe el id=5 y los datos editados (nuevo nombre).
	- Se busca la editorial con id=5.
	- Si existe, se actualiza el nombre y se guarda.
	- Se redirige a /editoriales/listar para mostrar la lista actualizada.

Explicación paso a paso:
1.-@PostMapping("/{id}/actualizar")
	- Este método responde a solicitudes HTTP POST en la ruta /editoriales/{id}/actualizar.
	- Normalmente se invoca desde un formulario de edición cuando el usuario hace clic en Guardar.

2.- public String actualizarEditorial(@PathVariable Long id, @ModelAttribute Editorial editorial)
	- @PathVariable Long id: toma el id de la editorial desde la URL (/5/actualizar → id = 5).
	- @ModelAttribute Editorial editorial: recibe los datos enviados desde el formulario de edición (por ejemplo, el nombre actualizado de la editorial) y los carga en un objeto Editorial.

3.- Optional<Editorial> editorialOptional = editorialService.buscarPorId(id);
	- Se busca en la base de datos la editorial con el id proporcionado.
	- Se usa Optional para manejar el caso de que la editorial no exista.

4.- if(editorialOptional.isPresent()){ ... }
	- Se verifica si la editorial con ese id fue encontrada.
	- Si existe, se procede a actualizarla.

5.- Editorial editorialActual = editorialOptional.get();
	- Obtiene la instancia real de la editorial que ya existe en la base de datos.

6.- editorialActual.setNombre(editorial.getNombre());
	- Se actualiza el campo nombre de la editorial con el valor que vino desde el formulario (editorial.getNombre()).
	- Aquí solo se está actualizando el nombre, pero en un caso real podrías actualizar varios campos (direccion, telefono, etc.).

7.- editorialService.actualizarEditorial(editorialActual);
	- Se llama al servicio para guardar los cambios en la base de datos.
	- Normalmente esto internamente hace un repository.save(editorialActual).

8.- return "redirect:/editoriales/listar";
	- Después de actualizar, se redirige al listado de editoriales (/editoriales/listar).
	- Con redirect: se le dice a Spring que no cargue una vista directamente, sino que envíe una redirección al navegador.
* */

    @PostMapping("/{id}/actualizar")
    public String actualizarEditorial(@PathVariable Long id, @ModelAttribute Editorial editorial){
        Optional<Editorial> editorialOptional = editorialService.buscarPorId(id);
        if(editorialOptional.isPresent()){
            Editorial editorialActual = editorialOptional.get();
            editorialActual.setNombre(editorial.getNombre());
            editorialService.actualizarEditorial(editorialActual);
        }
        return "redirect:/editoriales/listar";
    }

    /*

Resumen práctico
	- Este método:
	- Recibe un id desde la URL.
	- Llama al servicio para eliminar la editorial correspondiente en la base de datos.
	- Redirige al listado de editoriales para que el usuario vea la lista actualizada.

Ejemplo de flujo:
	- El usuario hace clic en un botón "Eliminar" en la tabla de editoriales.
	- El botón lo manda a /editorial/5/eliminar.
	- El método elimina la editorial con id=5.
	- El usuario es redirigido a /editoriales/listar y ya no ve la editorial borrada.

Explicación línea por línea

1.- @GetMapping("/{id}/eliminar")
	- Este método se ejecuta cuando el navegador hace una petición GET a una URL con el formato:

		/editorial/{id}/eliminar
	- Ejemplo: /editorial/7/eliminar

	- {id} es un parámetro dinámico de la URL (el identificador de la editorial que quieres borrar).

2.- public String eliminarEditorial(@PathVariable Long id)
	- Define el método que recibe el id desde la URL.
	- @PathVariable Long id: Spring toma el valor de {id} y lo asigna a la variable id.
		Ejemplo: si la URL es /editorial/7/eliminar, entonces id = 7.

3.- editorialService.eliminarEditorial(id);
	- Llama al servicio para eliminar la editorial con ese id en la base de datos.
	- Internamente, el servicio seguramente hará un repository.deleteById(id).

4.- return "redirect:/editoriales/listar";
	- Después de eliminar la editorial, el método redirige al listado de todas las editoriales.
	- redirect: indica a Spring que debe hacer una redirección HTTP en lugar de cargar una plantilla directamente.
	- El navegador del usuario será enviado automáticamente a la URL: /editoriales/listar
    * */

    @GetMapping("/{id}/eliminar")
    public String eliminarEditorial(@PathVariable Long id){
        editorialService.eliminarEditorial(id);
        return "redirect:/editoriales/listar";
    }

    /*

Resumen práctico
	- Este método muestra todos los libros de una editorial específica:
	- El usuario entra a la URL /editorial/{id}/libros.
	- Se busca la editorial con ese id.
	- Si existe, se agregan al modelo:
		 * "editorial" → datos de la editorial
		 * "libros" → lista de libros de esa editorial

	- Se carga la vista mostrar_libros_editorial.html para desplegar la información.

Ejemplo de flujo:
	- El usuario visita /editorial/3/libros.
	- Se busca la editorial con id=3.
	- Se obtienen sus libros (editorial.getLibros()).
	- En la página mostrar_libros_editorial.html se muestra el nombre de la editorial y una lista de sus libros.


Explicación línea por línea

1.- @GetMapping("/{id}/libros")
	- Define una ruta HTTP GET.
	- Responderá a URLs como: /editorial/5/libros
	- {id} es dinámico, representa el ID de la editorial.

2.- public String mostrarLibrosDeEditorial(@PathVariable Long id, Model model)
	- @PathVariable Long id: Spring toma el valor de {id} en la URL y lo asigna a la variable id.
	  Ejemplo: si la URL es /editorial/7/libros, entonces id = 7.
	- Model model: se usa para pasar datos desde el backend hacia la vista (HTML con Thymeleaf, JSP, etc.).

3.- Optional<Editorial> editorialOptional = editorialService.buscarPorId(id);
	- Llama al servicio para buscar la editorial en la base de datos.
	- Retorna un Optional<Editorial> porque puede que exista o no exista la editorial.

4.- if(editorialOptional.isPresent()){ ... }
	- Verifica si la editorial con ese id existe.
	- Si no existe, no se agregan datos al modelo (aunque en un caso real lo ideal sería mostrar un mensaje de error o redirigir a otra vista).

5.- Editorial editorial = editorialOptional.get();
	- Obtiene la editorial dentro del Optional.

6.- model.addAttribute("editorial", editorial);
	- Pasa la editorial al modelo con el nombre "editorial".
	- Esto permite acceder a los datos de la editorial en la vista, por ejemplo con ${editorial.nombre}.

7.- model.addAttribute("libros", editorial.getLibros());
	- Obtiene la lista de libros asociados a esa editorial (editorial.getLibros()).
	- Se agrega al modelo con el nombre "libros".
	- En la vista se puede recorrer la lista para mostrar los libros.

8.- return "editorial/mostrar_libros_editorial";
	- Devuelve el nombre de la plantilla que se va a renderizar: editorial/mostrar_libros_editorial.html
	- Esa vista recibirá dos objetos en el modelo:
		editorial → la editorial encontrada
		libros → la lista de libros asociados
    * */

    @GetMapping("/{id}/libros")
    public String mostrarLibrosDeEditorial(@PathVariable Long id, Model model){
        Optional<Editorial> editorialOptional = editorialService.buscarPorId(id);
        if(editorialOptional.isPresent()){
            Editorial editorial = editorialOptional.get();
            model.addAttribute("editorial", editorial);
            model.addAttribute("libros", editorial.getLibros());
        }
        return "editorial/mostrar_libros_editorial";
    }
}

