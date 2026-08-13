package com.vimar;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class PortfolioController {

    @GetMapping("/portfolio")
    public String portfolio(Model model) {
        model.addAttribute("name", "Seu Nome");
        model.addAttribute("role", "Desenvolvedor Full Stack");
        model.addAttribute("bio", "Breve descrição sobre você. Substitua por seu texto.");

        List<Map<String, String>> projects = List.of(
                Map.of("title", "Projeto A", "description", "Descrição curta do projeto A.", "link", "#"),
                Map.of("title", "Projeto B", "description", "Descrição curta do projeto B.", "link", "#"),
                Map.of("title", "Projeto C", "description", "Descrição curta do projeto C.", "link", "#")
        );

        model.addAttribute("projects", projects);
        return "portfolio";
    }

}
