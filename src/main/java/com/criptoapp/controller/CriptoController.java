package com.criptoapp.controller;

import com.criptoapp.model.Cripto;
import com.criptoapp.service.CriptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CriptoController {

    private final CriptoService criptoService;

    @Autowired
    public CriptoController(CriptoService criptoService) {
        this.criptoService = criptoService;
    }

    @GetMapping("/")
    public String getCriptos(Model model) {
        model.addAttribute("criptos", criptoService.getCriptos());
        return "index";  // Nombre de la plantilla Thymeleaf.
    }
}
