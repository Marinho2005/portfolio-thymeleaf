package com.vimar;

import com.vimar.service.SupabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class PortfolioController {

    private final SupabaseService supabaseService;

    public PortfolioController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model) {
        java.util.Optional<com.vimar.dto.SettingsForm> settings = supabaseService.getSettings();
        String name = settings.map(com.vimar.dto.SettingsForm::getDisplayName).orElse("Seu Nome");

        model.addAttribute("name", name);
        model.addAttribute("role", "Desenvolvedor Full Stack");
        model.addAttribute("bio", "Construindo soluções web com Java, Spring Boot e Supabase.");

        java.util.List<Map<String, Object>> projects = supabaseService.getProjects();
        model.addAttribute("projects", projects);
        return "portfolio";
    }

}