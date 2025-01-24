package com.fitness_tracker.Fitness_Tracker.features.authentication.repository;

import com.fitness_tracker.Fitness_Tracker.features.authentication.model.AuthenticationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationUserRepository extends JpaRepository<AuthenticationUser, Long> {
    Optional<AuthenticationUser> findByEmail(String email);

}
