package dev.o8o1o5.myEconomy.item;

import dev.o8o1o5.myTextures.api.MyTexturesAPI;
import dev.o8o1o5.myTextures.api.TexturesItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EconomyItemManager {

    public static final String PLATINUM = "platinum_coin";
    public static final String GOLD = "gold_coin";
    public static final String SILVER = "silver_coin";
    public static final String BRONZE = "bronze_coin";

    public void registerItems() {
        // 백금화
        MyTexturesAPI.registerItem(new TexturesItemBuilder(PLATINUM)
                .material(Material.PAPER)
                .name("§b백금 셀 주화")
                .addLore("§7가장 가치 있는 화폐"));

        // 금화
        MyTexturesAPI.registerItem(new TexturesItemBuilder(GOLD)
                .material(Material.PAPER)
                .name("§e황금 셀 주화")
                .addLore("§7황금빛으로 빛나는 화폐"));

        // 은화
        MyTexturesAPI.registerItem(new TexturesItemBuilder(SILVER)
                .material(Material.PAPER)
                .name("§f은 셀 주화")
                .addLore("§7단단한 은색 화폐"));

        // 동화
        MyTexturesAPI.registerItem(new TexturesItemBuilder(BRONZE)
                .material(Material.PAPER)
                .name("§6동 셀 주화")
                .addLore("§7기초가 되는 구리 화폐"));
    }

    public ItemStack createCoin(String id, int amount) {
        ItemStack item = MyTexturesAPI.createItem(id);
        if (item == null) return null;

        item.setAmount(amount);
        // 여기서 필요하다면 '가치' 정보를 NBT에 추가로 박거나
        // 전용 로어를 업데이트하는 로직을 가질 수 있습니다.

        return item;
    }

    public List<String> getAllCoinIds() {
        return Arrays.asList(PLATINUM, GOLD, SILVER, BRONZE);
    }
}
