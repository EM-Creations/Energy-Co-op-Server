package uk.co.emcreations.energycoop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Management", description = "Management endpoints for the backend application")
public class ManagementController {

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
}