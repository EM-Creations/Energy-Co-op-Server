package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Management", description = "Management endpoints for the backend application")
@RestController
public class ManagementController {

    @GetMapping(name = "Hello", value = "/hello")
    @Operation(summary = "Hello", description = "Returns hello to verify the backend is running")
    public String hello() {
        return "Hello, backend is running!";
    }
}