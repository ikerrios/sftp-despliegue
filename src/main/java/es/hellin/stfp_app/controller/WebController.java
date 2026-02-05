package es.hellin.stfp_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.hellin.stfp_app.service.SftpService;

@Controller
public class WebController {

    private final SftpService sftpService;

    public WebController(SftpService sftpService) {
        this.sftpService = sftpService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, 
                           RedirectAttributes redirectAttributes) {
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor selecciona un archivo");
            return "redirect:/";
        }

        try {
            sftpService.uploadFile(file);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Archivo '" + file.getOriginalFilename() + "' subido exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al subir archivo: " + e.getMessage());
        }

        return "redirect:/";
    }
}
