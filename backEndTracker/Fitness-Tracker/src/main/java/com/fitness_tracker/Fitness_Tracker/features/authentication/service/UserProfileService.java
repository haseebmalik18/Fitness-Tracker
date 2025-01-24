package com.fitness_tracker.Fitness_Tracker.features.authentication.service;

import com.fitness_tracker.Fitness_Tracker.features.authentication.model.UserProfile;
import com.fitness_tracker.Fitness_Tracker.features.authentication.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile updateUserProfile(Long userId, String firstName, String lastName, String fitnessGoal, String activityLevel, String timeCommitment) {
        UserProfile profile = userProfileRepository.findByAuthenticationUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found."));

        if (firstName != null) {
            profile.setFirstName(firstName);
        }
        if (lastName != null) {
            profile.setLastName(lastName);
        }
        if (fitnessGoal != null) {
            profile.setFitnessGoal(fitnessGoal);
        }
        if (activityLevel != null) {
            profile.setActivityLevel(activityLevel);
        }
        if (timeCommitment != null) {
            profile.setTimeCommitment(timeCommitment);
        }

        return userProfileRepository.save(profile);
    }
}

