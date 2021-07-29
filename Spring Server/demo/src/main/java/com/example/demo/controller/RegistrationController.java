package com.example.demo.controller;

import com.example.demo.service.Response;
import com.example.demo.model.User;
import com.example.demo.service.RegistrationService;
import com.example.demo.service.ServerReport;
import com.example.demo.service.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService regService;

    @PostMapping(path = EndpointContainer.LOGIN)
    public User loginUser(@RequestParam String email, @RequestParam String password) {
        return regService.LoginUser(email, password);
    }

    @Async
    @GetMapping(path = EndpointContainer.RESEND_PASSWORD)
    public Response resendPassword(@RequestParam String email) {
        return regService.resendPassword(email);
    }

    @PostMapping(path = EndpointContainer.CHANGE_PASSWORD)
    public Response changePassword(@RequestParam String email,
                                   @RequestParam String old_password,
                                   @RequestParam String new_password) {
        return regService.changePassword(email, old_password, new_password);
    }

    @PostMapping(path = EndpointContainer.REMOVE_ACCOUNT)
    public Response removeAccount(@RequestParam Long user_id, @RequestParam String email, @RequestParam String password) {
        return regService.removeAccount(user_id, email, password);
    }

    @Async
    @PostMapping(path = EndpointContainer.SIGNUP)
    public Response signUp(@RequestParam String name,
                           @RequestParam String family,
                           @RequestParam String phone_number,
                           @RequestParam String email,
                           @RequestParam String password) {
        return regService.signUp( new User(name, family, phone_number, email, password) );
    }

    @PostMapping(path = EndpointContainer.GET_REPORT)
    public ServerReport getReport(@RequestParam String email, @RequestParam String password) {
        return regService.getReport(email, password);
    }

    @GetMapping(path = EndpointContainer.CONFIRM_EMAIL)
    public String confirmEmail(@RequestParam String token) {
        return regService.confirmEmail(token);
    }

}
