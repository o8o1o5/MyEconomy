package dev.o8o1o5.myEconomy.command;

import dev.o8o1o5.myEconomy.MyEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
            case "give" -> handleAdminAction(player, args, "give");
            case "take" -> handleAdminAction(player, args, "take");
            case "set" -> handleAdminAction(player, args, "set");
            default -> sendHelp(player);
        }

        return true;
    }

    private void handleBalance(Player player) {
        double balance = plugin.getDataManager().getBalance(player.getUniqueId());
        player.sendMessage("현재 잔고는 " + ChatColor.GOLD + String.format("%,.0f", balance) + " 골드 " + ChatColor.WHITE + "입니다.");
    }

    private void handlePay(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.YELLOW + "사용법: /me pay <유저> <금액>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "대상을 찾을 수 없습니다.");
            return;
        }

        if (target.equals(player)) {
            player.sendMessage(ChatColor.RED + "자기 자신에게 송금할 수 없습니다.");
            return;
        }

        try {
            double amount = Double.parseDouble(args[2]);
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "0보다 큰 금액을 입력해주세요.");
                return;
            }

            double senderBalance = plugin.getDataManager().getBalance(player.getUniqueId());
            if (senderBalance < amount) {
                player.sendMessage(ChatColor.RED + "잔액이 부족합니다.");
                return;
            }

            plugin.getDataManager().setBalance(player.getUniqueId(), senderBalance - amount);
            plugin.getDataManager().setBalance(target.getUniqueId(), plugin.getDataManager().getBalance(target.getUniqueId()) + amount);

            player.sendMessage(ChatColor.GREEN + target.getName() + "님에게 " + ChatColor.GOLD + String.format("%,.0f", amount) + " 골드" + ChatColor.GREEN + "를 보냈습니다.");
            target.sendMessage(ChatColor.GREEN + player.getName() + "님으로부터 " + ChatColor.GOLD + String.format("%,.0f", amount) + " 골드" + ChatColor.GREEN + "를 받았습니다.");

        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "올바른 금액(숫자)를 입력해주세요.");
        }
    }

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
        if (player.isOp()) {
            player.sendMessage(ChatColor.RED + "/me give <유저> <금액> " + ChatColor.GRAY + "- 돈 지급");
            player.sendMessage(ChatColor.RED + "/me take <유저> <금액> " + ChatColor.GRAY + "- 돈 차압");
            player.sendMessage( ChatColor.RED + "/me set <유저> <금액> " + ChatColor.GRAY + "- 잔고 설정");
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("balance", "pay"));
            if (sender.isOp()) {
                subCommands.addAll(Arrays.asList("give", "take", "set"));
            }
            return subCommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "pay", "give", "take", "set" -> { return null; } // 플레이어 이름 추천
            }
        }

        return Collections.emptyList();
    }
}
