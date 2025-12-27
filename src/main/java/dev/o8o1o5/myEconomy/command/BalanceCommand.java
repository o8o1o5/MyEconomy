package dev.o8o1o5.myEconomy.command;

import dev.o8o1o5.myEconomy.MyEconomy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    private final MyEconomy plugin;

    public BalanceCommand(MyEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length == 1 && args[0].equalsIgnoreCase("balance")) {
            double bal = plugin.getDataManager().getBalance(player.getUniqueId());
            player.sendMessage("현재 잔고는 " + String.format("%,.0f", bal) + "골드 입니다.");
            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give") && player.isOp()) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) return false;

            double amount = Double.parseDouble(args[2]);

            MyEconomy.getVault().depositPlayer(target, amount);

            player.sendMessage(target.getName() + "님에게 " + amount + "골드 를 지급했습니다.");
            return true;
        }

        return false;
    }
}
