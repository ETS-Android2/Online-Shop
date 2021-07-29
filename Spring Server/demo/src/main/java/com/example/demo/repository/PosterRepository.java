package com.example.demo.repository;

import com.example.demo.model.Poster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PosterRepository extends JpaRepository<Poster, Long> {

    public Poster findPosterById(Long id);

    public Page<Poster> findPosterByNameContaining(String phrase, Pageable pageable);
}
