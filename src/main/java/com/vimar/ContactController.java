package com.vimar;

import com.vimar.dto.ContactForm;
import com.vimar.service.SupabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    private final SupabaseService supabaseService;

    public ContactController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    @PostMapping("/contact")
    public String sendMessage(@ModelAttribute ContactForm form, RedirectAttributes redirect) {
        if (form.getName() == null || form.getName().isBlank()
                || form.getEmail() == null || form.getEmail().isBlank()
                || form.getMessage() == null || form.getMessage().isBlank()) {
            redirect.addFlashAttribute("contactMessage", "Preencha nome, email e mensagem.");
            redirect.addFlashAttribute("contactType", "error");
        } else if (supabaseService.sendContactMessage(form)) {
            redirect.addFlashAttribute("contactMessage", "Mensagem enviada com sucesso!");
            redirect.addFlashAttribute("contactType", "success");
        } else {
            redirect.addFlashAttribute("contactMessage", "Não foi possível enviar a mensagem (verifique o Supabase).");
            redirect.addFlashAttribute("contactType", "error");
        }
        return "redirect:/portfolio#contact";
    }

}