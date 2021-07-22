package com.example.bob_friend.controller;

import com.example.bob_friend.model.card.domain.Card;
import com.example.bob_friend.model.card.exception.CardNotFoundException;
import com.example.bob_friend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardRepository cardRepository;

    @GetMapping("/cards/{cardId}")
    public ResponseEntity getCard(@PathVariable Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new CardNotFoundException()
        );
        return ResponseEntity.ok(card);
    }

    @ExceptionHandler(value = CardNotFoundException.class)
    public ResponseEntity handleCardNotFound(CardNotFoundException exception) {
        // excetion handling
        return null;
    }
}
