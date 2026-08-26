package com.campusgig.backend.service;

import com.campusgig.backend.dto.SkillResponse;
import com.campusgig.backend.entity.Skill;
import com.campusgig.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SkillResponse> searchSkills(String name) {
        return skillRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SkillResponse createSkill(String name, String description) {
        if (skillRepository.existsByName(name)) {
            throw new RuntimeException("Skill already exists: " + name);
        }
        Skill skill = Skill.builder()
                .name(name)
                .description(description)
                .build();
        return toResponse(skillRepository.save(skill));
    }

    public List<Skill> getSkillsByIds(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }

    private SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .build();
    }
}