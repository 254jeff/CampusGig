package com.campusgig.backend.service;

import com.campusgig.backend.dto.ProfileRequest;
import com.campusgig.backend.dto.ProfileResponse;
import com.campusgig.backend.entity.Skill;
import com.campusgig.backend.entity.StudentProfile;
import com.campusgig.backend.entity.User;
import com.campusgig.backend.repository.StudentProfileRepository;
import com.campusgig.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final SkillService skillService;

    public ProfileResponse createProfile(String email, ProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("Profile already exists");
        }

        List<Skill> skills = request.getSkillIds() != null
                ? skillService.getSkillsByIds(request.getSkillIds())
                : List.of();

        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .studentId(request.getStudentId())
                .bio(request.getBio())
                .university(request.getUniversity())
                .course(request.getCourse())
                .yearOfStudy(request.getYearOfStudy())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .rating(0.0)
                .completedTasks(0)
                .skills(skills.stream().collect(Collectors.toSet()))
                .build();

        profileRepository.save(profile);

        return toResponse(profile);
    }

    public ProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return toResponse(profile);
    }

    public ProfileResponse updateProfile(String email, ProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (request.getStudentId() != null) profile.setStudentId(request.getStudentId());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getUniversity() != null) profile.setUniversity(request.getUniversity());
        if (request.getCourse() != null) profile.setCourse(request.getCourse());
        if (request.getYearOfStudy() != null) profile.setYearOfStudy(request.getYearOfStudy());
        if (request.getAvailable() != null) profile.setAvailable(request.getAvailable());

        if (request.getSkillIds() != null) {
            List<Skill> skills = skillService.getSkillsByIds(request.getSkillIds());
            profile.setSkills(skills.stream().collect(Collectors.toSet()));
        }

        profileRepository.save(profile);

        return toResponse(profile);
    }

    private ProfileResponse toResponse(StudentProfile profile) {
        User user = profile.getUser();

        List<ProfileResponse.SkillDto> skillDtos = profile.getSkills() != null
                ? profile.getSkills().stream()
                .map(skill -> ProfileResponse.SkillDto.builder()
                        .id(skill.getId())
                        .name(skill.getName())
                        .build())
                .collect(Collectors.toList())
                : List.of();

        return ProfileResponse.builder()
                .id(profile.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .studentId(profile.getStudentId())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .university(profile.getUniversity())
                .course(profile.getCourse())
                .yearOfStudy(profile.getYearOfStudy())
                .available(profile.isAvailable())
                .rating(profile.getRating())
                .completedTasks(profile.getCompletedTasks())
                .verified(user.isVerified())
                .skills(skillDtos)
                .build();
    }
}
