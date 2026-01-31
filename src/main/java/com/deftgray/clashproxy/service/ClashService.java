package com.deftgray.clashproxy.service;

import com.deftgray.clashproxy.dto.CardDto;
import com.deftgray.clashproxy.dto.ClashApıResponse;
import com.deftgray.clashproxy.model.Card;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClashService {

    private final RestTemplate restTemplate = new RestTemplate();

    @org.springframework.beans.factory.annotation.Value("${clash.api.token}")
    private String apiToken;

    public ClashService() {
    }

    public List<Card> getPlayerCards(String playerTag) {
        String url = "https://api.clashroyale.com/v1/players/{tag}";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.apiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ClashApıResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    ClashApıResponse.class, playerTag);
            System.out.println(response);
            ClashApıResponse player = response.getBody();

            if (player == null || player.getCards() == null) {
                return new ArrayList<>();
            }

            return player.getCards().stream()
                    .map(this::mapToCard)
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private Card mapToCard(CardDto dto) {
        Card card = new Card();
        card.setName(dto.getName());
        card.setLevel(dto.getLevel());

        card.setElixirCost(dto.getElixirCost());
        card.setRarity(dto.getRarity());

        // Map Icon URLs
        if (dto.getIconUrls() != null) {
            card.setImageUri(dto.getIconUrls().getMedium());
            card.setImageUriEvolved(dto.getIconUrls().getEvolutionMedium());
            card.setImageUriHero(dto.getIconUrls().getHeroMedium());
        }

        // Logic for isHero and evolved
        boolean isHero = false;
        boolean evolved = false;

        Integer maxEvo = dto.getMaxEvolutionLevel();
        Integer currentEvo = dto.getEvolutionLevel();

        if (maxEvo != null && maxEvo == 3) {
            if (currentEvo != null) {
                if (currentEvo == 3) {
                    isHero = true;
                    evolved = true;
                } else if (currentEvo == 2) {
                    isHero = true;
                    evolved = false;
                } else if (currentEvo == 1) {
                    isHero = false;
                    evolved = true;
                }
            }
        } else {
            // Default logic if not specifically maxEvo 3 (e.g. Archers maxEvo 1)
            // If it has evolution level coverage, assume evolved if level > 0
            if (currentEvo != null && currentEvo > 0) {
                evolved = true;
            }
        }

        card.setIsHero(isHero);
        card.setEvolved(evolved);

        return card;
    }
}
