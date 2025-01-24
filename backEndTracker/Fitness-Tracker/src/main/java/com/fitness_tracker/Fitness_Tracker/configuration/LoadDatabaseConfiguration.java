package com.fitness_tracker.Fitness_Tracker.configuration;

import com.fitness_tracker.Fitness_Tracker.features.authentication.model.AuthenticationUser;
import com.fitness_tracker.Fitness_Tracker.features.authentication.model.UserProfile;
import com.fitness_tracker.Fitness_Tracker.features.authentication.repository.AuthenticationUserRepository;
import com.fitness_tracker.Fitness_Tracker.features.authentication.repository.UserProfileRepository;
import com.fitness_tracker.Fitness_Tracker.features.authentication.utils.Encoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabaseConfiguration {
    private final Encoder encoder;

    public LoadDatabaseConfiguration(Encoder encoder) {
        this.encoder = encoder;
    }

    @Bean
    public CommandLineRunner initDatabase(AuthenticationUserRepository authenticationUserRepository, UserProfileRepository userProfileRepository) {
        return args -> {
            // Create AuthenticationUser
            AuthenticationUser authenticationUser = new AuthenticationUser("bob@example.com", encoder.encode("bobpassword"));
            authenticationUser = authenticationUserRepository.save(authenticationUser);

            // Create UserProfile for the AuthenticationUser
            UserProfile userProfile = new UserProfile();
            userProfile.setAuthenticationUser(authenticationUser);

            // Save the profile
            userProfileRepository.save(userProfile);
        };
    }
}
