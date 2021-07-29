package com.example.demo.controller;

import com.example.demo.service.Response;
import com.example.demo.model.Log;
import com.example.demo.service.UserService;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // ** Should be removed **
    @GetMapping(path = EndpointContainer.LIST_USERS)
    public List<User> getUsersList(){
        return userService.getUsersList();
    }

    // ** Should be removed **
    @PostMapping(path = EndpointContainer.ADD_USER)
    public String addUser(@RequestParam String name, @RequestParam String family, @RequestParam String phone_number, @RequestParam String email, @RequestParam String password) {
        User user = new User(name, family, phone_number, email, password);
        user.Activate();
        System.out.println(user);
        if( userService.addUser(user) )
            return "User successfully added";
        return "Email already used";
    }

    @PostMapping(path = EndpointContainer.UPDATE_USER)
    public Response updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping(path = EndpointContainer.GET_USERS)
    public List<User> getUsers(@RequestParam String search,
                                     @RequestParam String sort_option,
                                     @RequestParam int pageIndex,
                                     @RequestParam int amount) {
        return userService.getUsers(search, sort_option, pageIndex, amount);
    }


    // Just for admin...
    @GetMapping(path = EndpointContainer.FILTER_USERS)
    public List<User> filterUsers(@RequestParam String name, @RequestParam String family, @RequestParam String phone_number, @RequestParam String email) {
        return null;
    }

    /*
    @PostMapping(path = EndpointContainer.MARK_POSTER)
    public Response markPoster(@RequestParam String user_id, @RequestParam String poster_id) {
        return userService.markPoster(user_id, poster_id);
    }*/

    // TODO : defining args...
    @PostMapping(path = EndpointContainer.GET_USER_LOGS)
    public List<Log> getUserLogs(/* Not sure about args */) { return null; }

}
