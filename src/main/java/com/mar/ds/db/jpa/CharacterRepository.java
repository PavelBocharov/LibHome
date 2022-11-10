package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character, Long> {
}
