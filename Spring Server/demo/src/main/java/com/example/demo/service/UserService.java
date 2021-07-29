package com.example.demo.service;

import com.example.demo.controller.EndpointContainer;
import com.example.demo.model.Log;
import com.example.demo.model.User;
import com.example.demo.model.UserType;
import com.example.demo.repository.PosterRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
// Also we can use @Component
public class UserService {

    private final UserRepository userRepository;
    private final PosterRepository posterRepository;

    public List<User> getUsersList(){
        return userRepository.findAll();
    }

    // returns true if user added to database
    public Boolean addUser(User user) {
        User target = userRepository.findUserByEmailAndIsConfirmed(user.getEmail().trim(), true);
        if( target != null )
            return false;

        target = userRepository.findUserByEmail(user.getEmail().trim());

        if( target != null )
            userRepository.save(target.updateUser(user));
        else
            userRepository.save(user);
        return true;
    }

    public Response updateUser(User user) {
        User targetUser = userRepository.findByIdAndEmailAndPassword(user.getId(), user.getEmail(), user.getPassword());

        if( targetUser == null )
            return new Response("User not found", false);

        new Log(targetUser, "Profile updated", EndpointContainer.UPDATE_USER);
        userRepository.save(targetUser.updateUser(user));

        return new Response("User update successfully", true);
    }

    public List<User> getUsers(String search, String sort_option, int pageIndex, int amount) {
        String[] sort_details = sort_option.split(" ");

        Sort sort = Sort.by(sort_details[0].toLowerCase());
        if( sort_details[1].trim().equalsIgnoreCase("ascending") )
            sort = sort.ascending();
        else
            sort = sort.descending();

        //sort.and(Sort.by("signup_date").ascending());

        Pageable sortedPosters = PageRequest.of(pageIndex, amount, sort);

        Page<User> result =
                userRepository.findUsersByNameContainingOrFamilyContaining(search, search, sortedPosters);

        List<User> users = new ArrayList<>();
        for(User user : result.getContent())
            if( !user.isAdmin() )
                users.add(user);

        return users;
    }

    public List<User> getTopSellers() {
        return null;
    }

    // TODO : defining args...
    public List<Log> getUserLogs(/* Not sure about args */) { return null; }
}