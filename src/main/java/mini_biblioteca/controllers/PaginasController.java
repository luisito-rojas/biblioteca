package mini_biblioteca.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/acerca_de")
public class PaginasController {

    @GetMapping("/acerca_de")
    public String acerca() {
        return "acerca_de"; // Spring buscará acerca_de.html en src/main/resources/templates
    }

}
