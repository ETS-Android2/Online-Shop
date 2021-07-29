package com.example.demo.controller;

import com.example.demo.service.Response;
import com.example.demo.model.Poster;
import com.example.demo.model.PosterGroup;
import com.example.demo.model.User;
import com.example.demo.repository.PosterRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class PosterController {

    @Autowired
    private PosterService posterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PosterRepository posterRepository;

    @PostMapping(path = EndpointContainer.ADD_POSTER)
    public Response addPoster(@RequestParam String email, @RequestParam String password, @RequestBody Poster poster) {
        return posterService.addPoster(email, password, poster);
    }

    @GetMapping(path = EndpointContainer.GET_POSTERS)
    public List<Poster> getPosters(@RequestParam String search,
                                   @RequestParam String sort_option,
                                   @RequestParam int pageIndex,
                                   @RequestParam int amount) {
        return posterService.getPosters(search, sort_option, pageIndex, amount);
    }

    @PostMapping(path = EndpointContainer.UPDATE_POSTER)
    public Response updatePoster(@RequestParam String email, @RequestParam String password, @RequestBody Poster poster) {
        return posterService.updatePoster(email, password, poster);
    }

    @PostMapping(path = EndpointContainer.DELETE_POSTER)
    public Response removePoster(@RequestParam Long poster_id, @RequestParam String email, @RequestParam String password) {
        return posterService.removePoster(poster_id, email, password);
    }

    @PostMapping(path = EndpointContainer.TOGGLE_BOOKMARK)
    public Response toggleBookmark(@RequestParam String email, @RequestParam String password, @RequestParam Long poster_id) {
        return posterService.toggleBookmark(email, password, poster_id);
    }

    @GetMapping(path = EndpointContainer.GET_POSTER_OWNER)
    public User getPosterOwner(@RequestParam Long poster_id) {
        return posterService.getPosterOwner(poster_id);
    }
}
