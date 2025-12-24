package com.myorg.tracemed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/error"})
    public String redirectToSwagger() {
        // Redirect root and default error page to Swagger UI for convenience
        return "redirect:/swagger-ui/index.html";
    }
}
