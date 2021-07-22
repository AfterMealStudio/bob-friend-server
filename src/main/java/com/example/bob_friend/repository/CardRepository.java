package com.example.bob_friend.repository;

import com.example.bob_friend.model.card.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

}
