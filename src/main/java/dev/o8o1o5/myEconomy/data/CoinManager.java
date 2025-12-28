package dev.o8o1o5.myEconomy.data;

import java.util.HashMap;
import java.util.Map;

public class CoinManager {
    private final Map<String, Double> coinPrices = new HashMap<>();

    public CoinManager() {
        coinPrices.put("bronze_coin", 10.0);
        coinPrices.put("silver_coin", coinPrices.get("bronze_coin") * 64);
        coinPrices.put("gold_coin", coinPrices.get("silver_coin") * 64);
        coinPrices.put("platinum_coin", coinPrices.get("gold_coin") * 64);
    }

    public double getPrice(String id) {
        return coinPrices.getOrDefault(id, 0.0);
    }

    public void setPrice(String id, double newPrice) {
        coinPrices.put(id, newPrice);
    }
}
