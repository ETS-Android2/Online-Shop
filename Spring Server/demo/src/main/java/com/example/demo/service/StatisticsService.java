package com.example.demo.service;

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

import java.util.List;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final PosterRepository posterRepository;

    public int getUsersCount() {
        return userRepository.findUsersByType(UserType.USER).size();
    }

    public int getPostersCount() {
        return posterRepository.findAll().size();
    }

    public List<User> getTopSellers() {

        Pageable sortOptions = PageRequest.of(0, 10, Sort.by("posterscnt").descending());

        Page<User> result = userRepository.findUsersByType(UserType.USER, sortOptions);

        return result.getContent();
    }
}
