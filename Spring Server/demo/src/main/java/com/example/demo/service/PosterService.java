package com.example.demo.service;

import com.example.demo.controller.EndpointContainer;
import com.example.demo.model.Log;
import com.example.demo.model.Poster;
import com.example.demo.model.User;
import com.example.demo.repository.PosterRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PosterService {

    private final PosterRepository posterRepository;
    private final UserRepository userRepository;



    public Response addPoster(String email, String password, Poster poster_instance) {
        User user = userRepository.findByEmailAndPasswordAndIsConfirmed(email, password, true);
        if( user == null )
            return new Response("User not found", false);

        poster_instance.setOwner(user);
        user.setPosterscnt(user.getPosterscnt() + 1);

        new Log(user, "User added poster " + poster_instance.getName(), EndpointContainer.ADD_POSTER);
        posterRepository.save(poster_instance);
        userRepository.save(user);
        return new Response("Poster added successfully", true);
    }

    public List<Poster> getPosters(String search, String sort_option, int pageIndex, int amount) {

        String[] sort_details = sort_option.split(" ");

        Sort sort = Sort.by(sort_details[0].toLowerCase());
        if( sort_details[1].trim().equalsIgnoreCase("ascending") )
            sort = sort.ascending();
        else
            sort = sort.descending();

        sort.and(Sort.by("priority").descending()).and(Sort.by("submission_date").ascending());

        Pageable sortedPosters = PageRequest.of(pageIndex, amount, sort);

        Page<Poster> result = posterRepository.findPosterByNameContaining(search, sortedPosters);

        return result.getContent();
    }

    public Response updatePoster(String email, String password, Poster poster_instance) {
        User owner = userRepository.findByEmailAndPasswordAndIsConfirmed(email, password, true);
        if( owner == null )
            return new Response("Owner not found", false);

        if( !owner.hasPoster(poster_instance) && !owner.isAdmin() )
            return new Response("You don't have permission", false);

        Poster target = posterRepository.findPosterById(poster_instance.getId());
        if( target == null )
            return new Response("Poster not found", false);


        target.updatePoster(poster_instance);

        posterRepository.save(target);
        new Log(owner, "User updated poster " + target.getName(), EndpointContainer.UPDATE_POSTER);

        return new Response("Poster updated successfully", true);
    }

    public Response removePoster(Long poster_id, String email, String password) {
        User user = userRepository.findByEmailAndPasswordAndIsConfirmed(email, password, true);
        Poster poster = posterRepository.findPosterById(poster_id);

        if( user == null )
            return new Response("User not found", false);

        if( poster == null )
            return new Response("Poster not found", false);

        if( user.isAdmin() || user.hasPoster(poster) ) {
            User owner = poster.getOwner();
            owner.setPosterscnt(owner.getPosterscnt() - 1);
            new Log(owner, "User removed poster " + poster.getName(), EndpointContainer.DELETE_POSTER);
            userRepository.save(owner);
            posterRepository.delete(poster);
            return new Response("Poster Removed Successfully", true);
        }
        else
            return new Response("Premission Denied", false);
    }

    public User getPosterOwner(Long poster_id) {
        Poster poster = posterRepository.findPosterById(poster_id);
        if( poster == null )
            return new User(null, null, null, null, null);

        User owner = poster.getOwner();
        owner.setBookmarks(null);
        owner.setPassword(null);
        return owner;
    }

    public Response toggleBookmark(String email, String password, Long poster_id) {
        User user = userRepository.findByEmailAndPasswordAndIsConfirmed(email, password, true);
        Poster poster = posterRepository.findPosterById(poster_id);
        if( user == null )
            return new Response("User not found", false);

        if( poster == null )
            return new Response("Poster not found", false);

        user.toggleBookmark(poster);
        userRepository.save(user);
        return new Response("Operation completed", true);
    }
}
