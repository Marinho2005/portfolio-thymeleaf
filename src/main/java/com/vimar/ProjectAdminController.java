package com.vimar;

import com.vimar.dto.ProjectForm;
import com.vimar.service.SupabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProjectAdminController {

    private final SupabaseService supabaseService;

    public ProjectAdminController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    @GetMapping("/admin/projetos")
    public String listarProjetos(Model model) {
        model.addAttribute("projects", supabaseService.getProjects());
        model.addAttribute("projectForm", new ProjectForm());
        return "admin/projetos";
    }

    @PostMapping("/admin/projetos")
    public String criarProjeto(@ModelAttribute ProjectForm form, RedirectAttributes redirect) {
        if (form.getTitle() == null || form.getTitle().isBlank()) {
            redirect.addFlashAttribute("message", "Título é obrigatório.");
            redirect.addFlashAttribute("messageType", "error");
        } else if (supabaseService.createProject(form)) {
            redirect.addFlashAttribute("message", "Projeto criado com sucesso.");
            redirect.addFlashAttribute("messageType", "success");
        } else {
            redirect.addFlashAttribute("message", "Não foi possível criar o projeto (verifique o Supabase).");
            redirect.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/projetos";
    }

    @PostMapping("/admin/projetos/update")
    public String atualizarProjeto(@RequestParam String id, @ModelAttribute ProjectForm form, RedirectAttributes redirect) {
        if (form.getTitle() == null || form.getTitle().isBlank()) {
            redirect.addFlashAttribute("message", "Título é obrigatório.");
            redirect.addFlashAttribute("messageType", "error");
        } else if (supabaseService.updateProject(id, form)) {
            redirect.addFlashAttribute("message", "Projeto atualizado com sucesso.");
            redirect.addFlashAttribute("messageType", "success");
        } else {
            redirect.addFlashAttribute("message", "Não foi possível atualizar o projeto.");
            redirect.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/projetos";
    }

    @PostMapping("/admin/projetos/delete")
    public String excluirProjeto(@RequestParam String id, RedirectAttributes redirect) {
        if (supabaseService.deleteProject(id)) {
            redirect.addFlashAttribute("message", "Projeto excluído.");
            redirect.addFlashAttribute("messageType", "success");
        } else {
            redirect.addFlashAttribute("message", "Não foi possível excluir o projeto.");
            redirect.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/projetos";
    }

}