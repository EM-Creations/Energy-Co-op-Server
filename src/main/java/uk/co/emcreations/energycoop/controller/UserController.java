package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.emcreations.energycoop.model.User;
import uk.co.emcreations.energycoop.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "User endpoints for the backend application")
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping(name = "Current User", value = "/currentUser")
    @Operation(summary = "Current User", description = "Returns the current user details")
    public ResponseEntity<User> currentUser(@AuthenticationPrincipal OidcUser principal) {
        return ResponseEntity.ok(userService.getUserFromPrincipal(principal));
    }
}