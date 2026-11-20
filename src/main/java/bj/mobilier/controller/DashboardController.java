package bj.mobilier.controller;

import bj.mobilier.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String afficherDashboard(Model model) {
        model.addAttribute("pageActive", "dashboard");
        model.addAttribute("indicateurs", dashboardService.calculerIndicateurs());
        return "dashboard";
    }
}
