package com.example.demo.repository;

import com.example.demo.model.Poster;
import com.example.demo.model.User;
import com.example.demo.model.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    public User findById(Long id);
    public User findUserByEmail(String email);
    public User findUserByEmailAndIsConfirmed(String email, Boolean isConfirmed);
    public User findByEmailAndPassword(String email, String password);
    public User findByEmailAndPasswordAndIsConfirmed(String email, String password, Boolean isConfirmed);
    public User findByIdAndEmailAndPassword(Long id, String email, String password);
    public User findByEmailAndIsConfirmed(String email, Boolean status);

    public Page<User> findUsersByNameContainingOrFamilyContaining(String name_phrase,
                                                                  String family_phrase,
                                                                  Pageable pageable);

    public List<User> findUsersByType(UserType type);
    public Page<User> findUsersByType(UserType type, Pageable pageable);
}
