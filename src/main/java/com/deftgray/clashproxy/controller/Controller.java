package com.deftgray.clashproxy.controller;

import com.deftgray.clashproxy.model.Card;
import com.deftgray.clashproxy.service.ClashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Controller {

    private final ClashService clashService;

    private final com.deftgray.clashproxy.service.DeckService deckService;

    @GetMapping("/player/{tag}")
    public List<Card> getPlayerCards(@PathVariable String tag) {
        log.info("Received request for player cards with tag: {}", tag);
        return clashService.getPlayerCards(tag);
    }

    @GetMapping("/freeDeck/{tag}")
    public com.deftgray.clashproxy.dto.DeckResponse getFreeDeck(@PathVariable String tag) {
        log.info("Received freeDeck request for tag: {}", tag);
        return deckService.generateFreeDeck(tag);
    }
}
