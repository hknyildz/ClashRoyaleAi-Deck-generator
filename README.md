https://www.clashdeckster.com/
# 🏰 ClashProxy: AI-Powered Clash Royale Deck Generator

**Welcome to the future of deck building!**

Forget scrolling through endless YouTube videos or copying outdated decks from 2021. **ClashProxy** connects to your Clash Royale account, looks at your specific card collection (yes, even that under-leveled Wizard), and uses advanced Artificial Intelligence to craft the perfect deck for you.

*Because let's be honest, it's not a skill issue, it was just a bad matchup.* 😉

## 🚀 Features

*   **🕵️‍♂️ Smart Proxy:** Fetches real-time player data from the official Clash Royale API.
*   **🧠 AI-Driven Deck Building:** Uses LLMs (Claude 3.5 Sonnet via OpenRouter) to analyze your card levels and build a meta-compliant deck.
*   **⚖️ Rules Enforcer:** Automatically checks valid deck compositions (Max 1 Champion, Max 2 Evolutions**).
*   **🛡️ Evolution Aware:** Only suggests Evolved cards if you actually own the evolution!

## 🛠️ Getting Started

### Prerequisites

*   Java 17 or higher
*   Maven
*   A Clash Royale API Token (from [developer.clashroyale.com](https://developer.clashroyale.com))
*   An OpenRouter API Key

### Configuration

You can configure the application via `src/main/resources/application.properties` or environment variables.

**Security Note:** Create a `src/main/resources/application-secrets.properties` file for your keys (it's `.gitignored` to keep you safe!).

```properties
# src/main/resources/application-secrets.properties
clash.api.token=YOUR_CLASH_ROYALE_TOKEN
openrouter.api.key=YOUR_OPENROUTER_KEY
```

### Running the App

```bash
./mvnw spring-boot:run
```

Once started, the magic happens at `http://localhost:8080`.

## 📡 API Endpoints

### 1. 🃏 The "I Just Need a Deck" Endpoint

**GET** `/freeDeck/{playerTag}`

This is where the magic happens. Give us your player tag, and we'll give you a winning strategy.

*   **Usage:** `curl http://localhost:8080/freeDeck/%23J08CVRJ00` (Don't forget to URL-encode the `#` as `%23`!)
*   **Response:**
    ```json
    {
        "cards": [
            { "name": "Hog Rider", "isEvolved": false, "isHero": false, "level": 14 },
            { "name": "Firecracker", "isEvolved": true, "isHero": false, "level": 13 },
            ...
        ],
        "strategy": "Hog Cycle",
        "tactic": "Cycle efficiently to your Hog Rider. Use Evolved Firecracker for massive chip damage. If you lose, blame lag."
    }
    ```

### 2. 📊 Raw Player Stats

**GET** `/cards/{playerTag}`

Want to see the raw data we see? Fetch your entire card collection.

*   **Usage:** `curl http://localhost:8080/cards/%23J08CVRJ00`

---

*Built with ❤️ and too much caffeine.*
*Disclaimer: We guarantee a great deck, but we can't play the cards for you. Missed rockets are on you.* 🚀
