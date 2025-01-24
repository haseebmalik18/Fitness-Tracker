package com.fitness_tracker.Fitness_Tracker.features.authentication.controller;

import com.fitness_tracker.Fitness_Tracker.features.authentication.model.AuthenticationUser;
import com.fitness_tracker.Fitness_Tracker.features.authentication.model.UserProfile;
import com.fitness_tracker.Fitness_Tracker.features.authentication.service.AuthenticationService;
import com.fitness_tracker.Fitness_Tracker.features.authentication.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/authentication")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PutMapping("profile/{id}")
    public UserProfile updateUserProfile(
            @RequestAttribute("authenticatedUser") AuthenticationUser authenticatedUser,
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String fitnessGoal,
            @RequestParam(required = false) String activityLevel,
            @RequestParam(required = false) String timeCommitment
    ) {
        if (!authenticatedUser.getId().equals(id)) {
            throw new IllegalArgumentException("User not authorized to update this profile.");
        }

        return userProfileService.updateUserProfile(authenticatedUser.getId(), firstName, lastName, fitnessGoal, activityLevel, timeCommitment);
    }
}

