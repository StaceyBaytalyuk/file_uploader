package uploadingfiles.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("main_menu")
    public String mainMenu() {
        return "main_menu";
    }
}
