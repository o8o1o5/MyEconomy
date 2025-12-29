package dev.o8o1o5.myEconomy.listener;

import dev.o8o1o5.myEconomy.MyEconomy;
import dev.o8o1o5.myTextures.MyTextures;
import dev.o8o1o5.myTextures.api.MyTexturesAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EconomyItemListener implements Listener {
    private final MyEconomy plugin;

    public EconomyItemListener(MyEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCoinRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();

        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return;
        }

        String coinId = MyTexturesAPI.getItemId(item);
        if (coinId == null) {
            return;
        }

        if (!plugin.getCoinManager().getPrices().containsKey(coinId)) {
            return;
        }

        Player player = event.getPlayer();
        double pricePerOne = plugin.getCoinManager().getPrice(coinId);
        int currentAmount = item.getAmount();

        int exchangeAmount;

        if (player.isSneaking()) {
            exchangeAmount = currentAmount;
        } else {
            exchangeAmount = 1;
        }

        double totalValue = pricePerOne * exchangeAmount;

        event.setCancelled(true);

        if (exchangeAmount >= currentAmount) {
            item.setAmount(0);
        } else {
            item.setAmount(currentAmount - exchangeAmount);
        }

        double currentBalance = plugin.getDataManager().getBalance(player.getUniqueId());
        plugin.getDataManager().setBalance(player.getUniqueId(), currentBalance + totalValue);

        sendExchangeMessage(player, exchangeAmount, totalValue, player.isSneaking());
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1.0f, 1.8f);
    }

    private void sendExchangeMessage(Player player, int amount, double value, boolean isBulk) {
        String prefix = isBulk ? "[일괄 환전]" : "[환전]";
        String msg = prefix + "주화 " + amount + "개" +
                " -> " +
                ChatColor.GOLD + String.format("%,.0f", value) + "셀" +
                ChatColor.WHITE + " 입금!";
        player.sendMessage(msg);
    }
}
