package com.viettran.reading_story_web.repository.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    Optional<Inventory> findFirstByUserIdAndExpirationDateAfter(String userId, Instant instant);

    Boolean existsByUserIdAndExpirationDateAfter(String userId, Instant instant);

    List<Inventory> findByExpirationDateBefore(Instant instant);
}
