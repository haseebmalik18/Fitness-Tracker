package com.fitness_tracker.Fitness_Tracker.features.authentication.repository;

import com.fitness_tracker.Fitness_Tracker.features.authentication.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByAuthenticationUser_Id(Long userId);
}


