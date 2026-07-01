package com.specuprpg.domain.pet.repository;

import com.specuprpg.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    // 유저 ID로 펫 조회 (1:1 관계)
    Optional<Pet> findByUserId(Long userId);

    // 배고픈 펫 목록 조회 (Scheduler용 — 알람 발송 대상)
    // hunger가 30 이하인 펫 조회
    @Query("SELECT p FROM Pet p WHERE p.hunger <= 30")
    List<Pet> findHungryPets();

    // 진화 가능한 펫 조회 (EVOLVED 제외)
    @Query("SELECT p FROM Pet p WHERE p.status != 'EVOLVED'")
    List<Pet> findEvolvablePets();
}
