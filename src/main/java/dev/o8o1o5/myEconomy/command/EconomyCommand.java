package dev.o8o1o5.myEconomy.command;

import dev.o8o1o5.myEconomy.MyEconomy;
import dev.o8o1o5.myEconomy.data.CoinManager;
import dev.o8o1o5.myEconomy.Textures_MyEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EconomyCommand implements CommandExecutor, TabCompleter {

    private final MyEconomy plugin;

    public EconomyCommand(MyEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "balance" -> handleBalance(player);
            case "pay" -> handlePay(player, args);
            case "withdraw" -> handleWithdraw(player, args); // 메서드 연결
            case "give" -> handleAdminAction(player, args, "give");
            case "take" -> handleAdminAction(player, args, "take");
            case "set" -> handleAdminAction(player, args, "set");
            default -> sendHelp(player);
        }

        return true;
    }

    // --- 기존 잔고 확인 및 송금 로직 유지 ---

    private void handleBalance(Player player) {
        double balance = plugin.getDataManager().getBalance(player.getUniqueId());
        player.sendMessage("현재 잔고는 " + ChatColor.GOLD + String.format("%,.0f", balance) + " 셀 " + ChatColor.WHITE + "입니다.");
    }

    private void handlePay(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.YELLOW + "사용법: /me pay <유저> <금액>");
            return;
        }
        // ... (생략: 기존 코드와 동일)
    }

    /**
     * 돈을 주화 아이템으로 인출하는 핵심 로직
     */
    private void handleWithdraw(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "사용법: /me withdraw <금액>");
            return;
        }

        try {
            long amount = Long.parseLong(args[1]);
            CoinManager cm = plugin.getCoinManager();
            double bronzePrice = cm.getPrice(Textures_MyEconomy.BRONZE_COIN);

            // 1. 인출 단위 체크
            if (amount < bronzePrice || amount % (long)bronzePrice != 0) {
                player.sendMessage(ChatColor.RED + "인출은 최소 " + (int) bronzePrice + " 셀 단위로 가능합니다.");
                return;
            }

            // 2. 잔액 체크
            double currentBalance = plugin.getDataManager().getBalance(player.getUniqueId());
            if (currentBalance < amount) {
                player.sendMessage(ChatColor.RED + "잔액이 부족합니다.");
                return;
            }

            // 3. 주화별 개수 계산
            double platinumPrice = cm.getPrice(Textures_MyEconomy.PLATINUM_COIN);
            double goldPrice = cm.getPrice(Textures_MyEconomy.GOLD_COIN);
            double silverPrice = cm.getPrice(Textures_MyEconomy.SILVER_COIN);

            long remaining = amount;

            int platinumCount = (int) (remaining / platinumPrice);
            remaining %= (long) platinumPrice;

            int goldCount = (int) (remaining / goldPrice);
            remaining %= (long) goldPrice;

            int silverCount = (int) (remaining / silverPrice);
            remaining %= (long) silverPrice;

            int bronzeCount = (int) (remaining / bronzePrice);

            // 4. 잔고 차감 및 아이템 지급
            plugin.getDataManager().setBalance(player.getUniqueId(), currentBalance - amount);

            Textures_MyEconomy im = plugin.getItemManager();

            // 주화 생성 및 지급 호출
            giveCoin(player, im.createItem(Textures_MyEconomy.PLATINUM_COIN, platinumCount));
            giveCoin(player, im.createItem(Textures_MyEconomy.GOLD_COIN, goldCount));
            giveCoin(player, im.createItem(Textures_MyEconomy.SILVER_COIN, silverCount));
            giveCoin(player, im.createItem(Textures_MyEconomy.BRONZE_COIN, bronzeCount));

            player.sendMessage(ChatColor.GREEN + String.format("%,d", amount) + " 셀을 인출하였습니다.");

        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "올바른 금액을 입력해주세요.");
        }
    }

    /**
     * 아이템을 지급하고 인벤토리가 꽉 찼으면 바닥에 드랍
     */
    private void giveCoin(Player player, ItemStack item) {
        if (item == null || item.getAmount() <= 0) return;

        // 인벤토리에 넣고 남은 아이템은 맵으로 반환됨
        player.getInventory().addItem(item).values().forEach(remaining ->
                player.getWorld().dropItemNaturally(player.getLocation(), remaining)
        );
    }

    // --- 관리자 로직 및 기타 메서드 ---

    private void handleAdminAction(Player player, String[] args, String type) {

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "권한이 없습니다.");
            return;
        }

        if (args.length < 3) {
            player.sendMessage(ChatColor.YELLOW + "사용법: /me " + type + " <유저> <금액>");
            return;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        try {
            double amount = Double.parseDouble(args[2]);
            double current = plugin.getDataManager().getBalance(target.getUniqueId());

            switch (type) {
                case "give" -> plugin.getDataManager().setBalance(target.getUniqueId(), current + amount);
                case "take" -> plugin.getDataManager().setBalance(target.getUniqueId(), Math.max(0, current - amount));
                case "set" -> plugin.getDataManager().setBalance(target.getUniqueId(), amount);
            }

            player.sendMessage(ChatColor.GREEN + target.getName() + "님의 잔고를 업테이트 했습니다. (Type: " + type + ")");
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "올바른 금액(숫자)를 입력해주세요.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "===== [ MyEconomy Help ] =====");
        player.sendMessage(ChatColor.WHITE + "/me balance " + ChatColor.GRAY + "- 내 잔고 확인");
        player.sendMessage(ChatColor.WHITE + "/me pay <유저> <금액> " + ChatColor.GRAY + "- 유저에게 돈 송금");
        player.sendMessage(ChatColor.WHITE + "/me withdraw <금액> " + ChatColor.GRAY + "- 돈을 아이템으로 인출");
        if (player.isOp()) {
            player.sendMessage(ChatColor.RED + "/me give <유저> <금액> " + ChatColor.GRAY + "- 돈 지급");
            player.sendMessage(ChatColor.RED + "/me take <유저> <금액> " + ChatColor.GRAY + "- 돈 차압");
            player.sendMessage(ChatColor.RED + "/me set <유저> <금액> " + ChatColor.GRAY + "- 잔고 설정");
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("balance", "pay", "withdraw"));
            if (sender.isOp()) {
                subCommands.addAll(Arrays.asList("give", "take", "set"));
            }
            return subCommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}