package com.vimar;

import com.vimar.dto.SettingsForm;
import com.vimar.service.SupabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class ConfigController {

    private final SupabaseService supabaseService;

    public ConfigController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    @GetMapping("/configuracoes")
    public String configuracoes(Model model) {
        Optional<SettingsForm> saved = supabaseService.getSettings();
        if (saved.isPresent()) {
            SettingsForm form = saved.get();
            model.addAttribute("displayName", form.getDisplayName());
            model.addAttribute("email", form.getEmail());
            model.addAttribute("theme", form.getTheme());
        } else {
            model.addAttribute("displayName", "Seu Nome");
            model.addAttribute("email", "seu@exemplo.com");
            model.addAttribute("theme", "light");
        }
        return "configuracoes";
    }

    @PostMapping("/configuracoes")
    public String saveConfiguracoes(@ModelAttribute SettingsForm form, Model model) {
        boolean ok = supabaseService.saveSettings(form);
        if (ok) {
            model.addAttribute("message", "Configurações salvas com sucesso.");
        } else {
            model.addAttribute("message", "Não foi possível salvar (verifique configuração do Supabase).");
        }
        model.addAttribute("displayName", form.getDisplayName());
        model.addAttribute("email", form.getEmail());
        model.addAttribute("theme", form.getTheme());
        return "configuracoes";
    }

}