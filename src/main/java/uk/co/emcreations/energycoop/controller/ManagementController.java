package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.emcreations.energycoop.service.AuthService;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Management", description = "Management endpoints for the backend application")
public class ManagementController {
    @Autowired
    private final AuthService authService;

    @GetMapping(name = "Hello", value = "/hello")
    @Operation(summary = "Hello", description = "Returns hello to verify the backend is running")
    public String hello() {
        return "Hello, backend is running!";
    }

    @GetMapping(name = "Thread Check", value = "/threadCheck")
    @Operation(summary = "Thread Check", description = "Check details of the current thread handling the request")
    public String threadCheck() {
        return Thread.currentThread().toString();
    }

    @GetMapping(name = "Token check", value = "/tokenCheck")
    @Operation(summary = "Token check", description = "Check details of the principal's token")
    public String tokenCheck(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            model.addAttribute("profile", principal.getClaims());
            Map<String, Object> claims = principal.getClaims();
            String userId = claims.get("sub").toString();

            String permissions = authService.getPermissionsForUser(userId);

            log.info("User ID: {}", userId);
        }
        return "index";
    }
}