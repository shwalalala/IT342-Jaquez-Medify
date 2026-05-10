package com.edu.cit.jaquez.medify.dashboard;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edu.cit.jaquez.medify.common.ApiResponse;
import com.edu.cit.jaquez.medify.dashboard.dto.DashboardResponse;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ApiResponse<DashboardResponse> dashboard(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(dashboardService.getDashboard(userDetails.getUsername()));
    }
}
