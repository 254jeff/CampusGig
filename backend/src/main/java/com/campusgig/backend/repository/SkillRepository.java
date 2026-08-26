package com.campusgig.backend.repository;

import com.campusgig.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);
    boolean existsByName(String name);
    List<Skill> findByNameContainingIgnoreCase(String name);
}

