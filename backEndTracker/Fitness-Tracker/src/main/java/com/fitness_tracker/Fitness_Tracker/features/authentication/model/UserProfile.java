package com.fitness_tracker.Fitness_Tracker.features.authentication.model;

import jakarta.persistence.*;

@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "authentication_user_id", nullable = false, unique = true)
    private AuthenticationUser authenticationUser;

    private String firstName;
    private String lastName;
    private String fitnessGoal;
    private String activityLevel;
    private String timeCommitment;

    private boolean profileComplete = false;
    public void setId(Long id) {
        this.id = id;
    }

    private void updateProfileCompletionStatus() {
        if (this.firstName != null && this.lastName != null && this.fitnessGoal != null && this.activityLevel != null && this.timeCommitment != null) {
            this.profileComplete = true;
        }
    }

    public void setAuthenticationUser(AuthenticationUser authenticationUser) {
        this.authenticationUser = authenticationUser;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateProfileCompletionStatus();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateProfileCompletionStatus();
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
        updateProfileCompletionStatus();
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
        updateProfileCompletionStatus();
    }

    public void setTimeCommitment(String timeCommitment) {
        this.timeCommitment = timeCommitment;
        updateProfileCompletionStatus();
    }

    public Long getId() {
        return id;
    }

    public AuthenticationUser getAuthenticationUser() {
        return authenticationUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public String getTimeCommitment() {
        return timeCommitment;
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }
}
