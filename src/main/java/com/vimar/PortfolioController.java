package com.vimar;

import com.vimar.dto.SettingsForm;
import com.vimar.service.SupabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.Optional;

@Controller
public class PortfolioController {

    private static final String DEFAULT_PHOTO = "https://i.pravatar.cc/300";

    private final SupabaseService supabaseService;

    public PortfolioController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model) {
        Optional<SettingsForm> saved = supabaseService.getSettings();

        String name = saved.map(SettingsForm::getDisplayName).filter(s -> !s.isBlank()).orElse("Seu Nome");
        String email = saved.map(SettingsForm::getEmail).orElse(null);
        String bio = saved.map(SettingsForm::getBio).filter(s -> !s.isBlank())
                .orElse("Desenvolvedor apaixonado por tecnologia, construindo soluções web com Java, Spring Boot e Supabase.");
        String photo = saved.map(SettingsForm::getPhotoUrl).filter(s -> !s.isBlank()).orElse(DEFAULT_PHOTO);

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("photo", photo);
        model.addAttribute("role", "Desenvolvedor Full Stack");
        model.addAttribute("bio", bio);
        model.addAttribute("githubUrl", saved.map(SettingsForm::getGithubUrl).orElse(null));
        model.addAttribute("linkedinUrl", saved.map(SettingsForm::getLinkedinUrl).orElse(null));
        model.addAttribute("instagramUrl", saved.map(SettingsForm::getInstagramUrl).orElse(null));
        model.addAttribute("twitterUrl", saved.map(SettingsForm::getTwitterUrl).orElse(null));

        model.addAttribute("projects", supabaseService.getProjects());
        return "portfolio";
    }

}