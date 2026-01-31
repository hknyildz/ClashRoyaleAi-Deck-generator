package com.deftgray.clashproxy.service;

import com.deftgray.clashproxy.model.Card;
import com.deftgray.clashproxy.dto.DeckResponse;
import com.deftgray.clashproxy.dto.SimplifiedCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeckService {

    private final ClashService clashService;
    private final LlmService llmService;
    private static final int MAX_RETRIES = 3;

    public DeckResponse generateFreeDeck(String playerTag) {
        log.info("Generating free deck for player: {}", playerTag);
        // 1. Get Player Cards
        List<Card> allCards = clashService.getPlayerCards(playerTag);
        if (allCards.isEmpty()) {
            log.warn("No cards found for player: {}", playerTag);
            return DeckResponse.builder().valid(false).strategy("N/A")
                    .tacticMessage("Player not found or no cards available.").build();
        }
        log.info("Found {} cards for player", allCards.size());

        // 2. Simplify for LLM
        List<SimplifiedCard> simplifiedCards = allCards.stream()
                .map(this::toSimplified)
                .toList();

        // Map for quick lookup
        Map<String, Card> cardMap = allCards.stream()
                .collect(Collectors.toMap(Card::getName, Function.identity(), (a, b) -> a)); // Handle duplicates if any

        // 3. Retry Loop
        for (int i = 0; i < MAX_RETRIES; i++) {
            log.info("Attempt {}/{} to generate deck via LLM", i + 1, MAX_RETRIES);
            com.deftgray.clashproxy.dto.LlmDeckSuggestion suggestion = llmService
                    .generateDeckRecommendation(simplifiedCards);

            if (suggestion == null || suggestion.getCards() == null || suggestion.getCards().isEmpty()) {
                log.error("LLM returned empty suggestion");
                continue;
            }
            log.info("LLM suggested cards: {}", suggestion.getCards());

            // Map suggestions back to Cards
            List<Card> deck = new ArrayList<>();
            for (com.deftgray.clashproxy.dto.LlmDeckSuggestion.LlmCardSuggestion cardSuggestion : suggestion
                    .getCards()) {
                String name = cardSuggestion.getName();

                if (cardMap.containsKey(name)) {
                    Card originalCard = cardMap.get(name);

                    // Create a copy or modify the card for the response (to reflect selected
                    // evolution)
                    // Since Card is mutable, we should ideally clone it, but for now we'll set it.
                    // However, we must be careful not to modify the 'allCards' list if it's
                    // cached/persisted.
                    // In this scope, 'allCards' is just fetched from API, so it's safe to modify
                    // strictly for response.

                    // But wait, if user has Evolution but LLM says isEvolved=false, we should set
                    // it false.
                    // If user has NO Evolution but LLM says isEvolved=true, this is invalid (or we
                    // ignore LLM).

                    boolean userHasEvolution = Boolean.TRUE.equals(originalCard.getEvolved());
                    boolean llmWantsEvolution = cardSuggestion.isEvolved();

                    // Only invoke evolution if user HAS it AND LLM wants it.
                    originalCard.setEvolved(userHasEvolution && llmWantsEvolution);

                    // Same for Hero? Usually Hero is intrinsic to the card type (Champion).
                    // LLM shouldn't "turn off" Hero status for a Champion.
                    // But we can trust the original card's Hero status.
                    // originalCard.setIsHero(originalCard.getIsHero());

                    deck.add(originalCard);
                } else {
                    log.warn("Suggested card '{}' not found in player's collection", name);
                }
            }

            // 4. Validate
            if (isValidDeck(deck)) {
                log.info("Valid deck generated: {}", suggestion.getStrategy());
                return DeckResponse.builder()
                        .deck(deck)
                        .valid(true)
                        .strategy(suggestion.getStrategy())
                        .tacticMessage(suggestion.getTactic())
                        .build();
            }
            log.warn("Generated deck validation failed.");
            // If invalid, loop again
        }

        log.error("Failed to generate valid deck after retries");
        return DeckResponse.builder()
                .valid(false)
                .strategy("N/A")
                .tacticMessage("Failed to generate a valid deck after " + MAX_RETRIES + " attempts.")
                .build();
    }

    private boolean isValidDeck(List<Card> deck) {
        if (deck.size() != 8)
            return false;

        long heroCount = deck.stream().filter(c -> Boolean.TRUE.equals(c.getIsHero())).count();
        if (heroCount > 1)
            return false;

        long evolvedCount = deck.stream().filter(c -> Boolean.TRUE.equals(c.getEvolved())).count();
        if (evolvedCount > 2)
            return false;

        return true;
    }

    private SimplifiedCard toSimplified(Card card) {
        return SimplifiedCard.builder()
                .name(card.getName())
                .level(card.getLevel())
                .isEvolved(Boolean.TRUE.equals(card.getEvolved()))
                .isHero(Boolean.TRUE.equals(card.getIsHero()))
                .elixirCost(card.getElixirCost())
                .build();
    }
}
