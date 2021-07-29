package com.example.demo.service;

import com.example.demo.controller.EndpointContainer;
import com.example.demo.model.ConfirmationToken;
import com.example.demo.model.Log;
import com.example.demo.model.Poster;
import com.example.demo.model.User;
import com.example.demo.repository.ConfirmationTokenRepository;
import com.example.demo.repository.PosterRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository tokenRepository;
    private final UserService userService;
    private final PosterRepository posterRepository;
    private final EmailService emailService;
    private final StatisticsService statsService;

    // Done...
    public User LoginUser(String email, String password) {
        User user = userRepository.findByEmailAndPasswordAndIsConfirmed(email, password, true);
        if( user == null )
            return new User(null, null, null, null, null);

        new Log(user, "User logged in", EndpointContainer.LOGIN);
        userRepository.save(user);
        user.setLogs(null);
        return user;
    }

    // Done...
    public Response resendPassword(String email) {
        User user = userRepository.findByEmailAndIsConfirmed(email, true);
        if( user == null )
            return new Response("Email not found!", false);

        new Thread(new Runnable() {

            @Override
            public void run() {
                emailService.send(
                        email,
                        "IlyaShop Password",
                        "Hi " + user.getName() + "!\n\nYour password : " + user.getPassword()
                );
            }
        }).start();

        new Log(user, "User requested to resend its password", EndpointContainer.RESEND_PASSWORD);
        return new Response("Email is sent", true);
    }

    // Done...
    // TODO : Making confirmation link with dynamic ip
    public Response signUp(User new_user) {

        if( !EmailValidator.getInstance().isValid(new_user.getEmail().trim()) )
            return new Response("Email is not valid", false);

        if( !userService.addUser(new_user) )
            return new Response("Email is already in use", false);

        new Log(new_user, "Confirmation token sent to user", EndpointContainer.SIGNUP);

        // Making and Saving token
        ConfirmationToken token = new ConfirmationToken(new_user);
        tokenRepository.save(token);

        // Sending token to user's email address
        String link;

        try {
            link = "http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/"
                    + EndpointContainer.CONFIRM_EMAIL + "?token=" + token.getToken();
        }
        catch (Exception e) {
            link = "http://127.0.0.1:8080/" + EndpointContainer.CONFIRM_EMAIL + "?token=" + token.getToken();
        }

        final String final_link = link;

        new Thread(new Runnable() {
            @Override
            public void run() {
                emailService.send(
                        new_user.getEmail(),
                        "IlyaShop Email Confirmation",
                        "Hello " + new_user.getName() + "!\nConfirmation code: " + final_link);
            }
        }).start();


        return new Response("Confirmation link is sent!", true);
    }

    public ServerReport getReport(String email, String password) {
        User user = userRepository.findByEmailAndPasswordAndIsConfirmed(email, password, true);
        if( user == null || !user.isAdmin() )
            return new ServerReport(0, 0, null);

        return new ServerReport(statsService.getUsersCount(), statsService.getPostersCount(), statsService.getTopSellers());
    }

    // Done...
    public String confirmEmail(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token);

        if( confirmationToken == null )
            return "token not found";

        if (confirmationToken.isConfirmed())
            return "email is already confirmed";

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now()))
            return "token is expired";

        confirmationToken.setConfirmed();
        confirmationToken.getOwner().Activate();
        new Log(confirmationToken.getOwner(), "User confirmed email " + confirmationToken.getOwner().getEmail(), EndpointContainer.CONFIRM_EMAIL);
        tokenRepository.updateConfirmedAt(confirmationToken.getToken(), LocalDateTime.now());
        return "confirmed successfully";
    }

    // Done...
    public Response changePassword(String email,  String old_password,  String new_password) {
        User target = userRepository.findByEmailAndPassword(email, old_password);
        if( target == null )
            return new Response("Password is wrong", false);

        target.setPassword(new_password);

        new Log(target, "User changed its password", EndpointContainer.CHANGE_PASSWORD);
        userRepository.save(target);
        return new Response("Password is successfully changed", true);
    }

    public Response removeAccount(Long user_id, String email, String password) {
        User target = userRepository.findById(user_id);
        User request_owner = userRepository.findByEmailAndPassword(email, password);
        if( target == null )
            return new Response("User not found", false);
        if( request_owner == null )
            return new Response("Password is wrong", false);
        if( !request_owner.isAdmin() && !request_owner.getId().equals(target.getId()) )
            return new Response("You don't have permission", false);

        for(Poster poster : target.getPosters())
            posterRepository.delete(poster);

        userRepository.delete(target);
        return new Response("Account deleted successfully", true);
    }
}
