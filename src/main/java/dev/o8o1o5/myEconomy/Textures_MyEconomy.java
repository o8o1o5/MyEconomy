package dev.o8o1o5.myEconomy;

import dev.o8o1o5.myTextures.api.MyTexturesAPI;
import dev.o8o1o5.myTextures.api.TexturesItemBuilder;
import dev.o8o1o5.myTextures.api.TexturesModule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Textures_MyEconomy extends TexturesModule {

    public static final String PLATINUM_COIN = "platinum_coin";
    public static final String GOLD_COIN = "gold_coin";
    public static final String SILVER_COIN = "silver_coin";
    public static final String BRONZE_COIN = "bronze_coin";

    public Textures_MyEconomy() {
        super("MyEconomy");
    }

    @Override
    public void onRegister() {
        // 백금화
        register(new TexturesItemBuilder(PLATINUM_COIN)
                .material(Material.PAPER)
                .name("§b백금 셀 주화")
                .addLore("§7가장 가치 있는 화폐"));

        // 금화
        register(new TexturesItemBuilder(GOLD_COIN)
                .material(Material.PAPER)
                .name("§e황금 셀 주화")
                .addLore("§7황금빛으로 빛나는 화폐"));

        // 은화
        register(new TexturesItemBuilder(SILVER_COIN)
                .material(Material.PAPER)
                .name("§f은 셀 주화")
                .addLore("§7단단한 은색 화폐"));

        // 동화
        register(new TexturesItemBuilder(BRONZE_COIN)
                .material(Material.PAPER)
                .name("§6동 셀 주화")
                .addLore("§7기초가 되는 구리 화폐"));
    }

    public ItemStack createItem(String id, int amount) {
        ItemStack item = MyTexturesAPI.createItem(id);
        if (item == null) return null;

        item.setAmount(amount);

        return item;
    }

    public List<String> getAllItemIds() {
        return Arrays.asList(PLATINUM_COIN, GOLD_COIN, SILVER_COIN, BRONZE_COIN);
    }
}
