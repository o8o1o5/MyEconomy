package dev.o8o1o5.myeconomy.vault;

import dev.o8o1o5.myEconomy.MyEconomy;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class Economy_MyEconomy extends AbstractEconomy {

    private final MyEconomy plugin;

    public Economy_MyEconomy(MyEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() { return plugin.isEnabled(); }

    @Override
    public String getName() { return "MyEconomy"; }

    /* ------------------------------------------------------------------
       [1] 계좌 확인 (hasAccount) - 모든 형태 구현
       ------------------------------------------------------------------ */
    @Override
    public boolean hasAccount(String playerName) { return true; }

    @Override
    public boolean hasAccount(OfflinePlayer player) { return true; }

    @Override
    public boolean hasAccount(String playerName, String worldName) { return true; }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) { return true; }

    /* ------------------------------------------------------------------
       [2] 잔액 조회 (getBalance)
       ------------------------------------------------------------------ */
    @Override
    @SuppressWarnings("deprecation")
    public double getBalance(String playerName) {
        return getBalance(Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return plugin.getDataManager().getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName, String world) { return getBalance(playerName); }

    @Override
    public double getBalance(OfflinePlayer player, String world) { return getBalance(player); }

    /* ------------------------------------------------------------------
       [3] 금액 보유 확인 (has)
       ------------------------------------------------------------------ */
    @Override
    public boolean has(String playerName, double amount) { return getBalance(playerName) >= amount; }

    @Override
    public boolean has(OfflinePlayer player, double amount) { return getBalance(player) >= amount; }

    @Override
    public boolean has(String playerName, String worldName, double amount) { return has(playerName, amount); }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) { return has(player, amount); }

    /* ------------------------------------------------------------------
       [4] 출금 (withdrawPlayer)
       ------------------------------------------------------------------ */
    @Override
    @SuppressWarnings("deprecation")
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "음수 불가");
        double current = getBalance(player);
        if (current < amount) return new EconomyResponse(0, current, EconomyResponse.ResponseType.FAILURE, "잔액 부족");

        plugin.getDataManager().setBalance(player.getUniqueId(), current - amount);
        return new EconomyResponse(amount, current - amount, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) { return withdrawPlayer(playerName, amount); }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) { return withdrawPlayer(player, amount); }

    /* ------------------------------------------------------------------
       [5] 입금 (depositPlayer)
       ------------------------------------------------------------------ */
    @Override
    @SuppressWarnings("deprecation")
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "음수 불가");
        double current = getBalance(player);
        plugin.getDataManager().setBalance(player.getUniqueId(), current + amount);
        return new EconomyResponse(amount, current + amount, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) { return depositPlayer(playerName, amount); }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) { return depositPlayer(player, amount); }

    /* ------------------------------------------------------------------
       [6] 기타 필수 설정 및 은행 기능 (기본값)
       ------------------------------------------------------------------ */
    @Override public String format(double amount) { return String.format("%,.0f 셀", amount); }
    @Override public String currencyNamePlural() { return "셀"; }
    @Override public String currencyNameSingular() { return "셀"; }
    @Override public int fractionalDigits() { return 0; }
    @Override public boolean createPlayerAccount(String playerName) { return true; }
    @Override public boolean createPlayerAccount(OfflinePlayer player) { return true; }
    @Override public boolean createPlayerAccount(String playerName, String worldName) { return true; }
    @Override public boolean createPlayerAccount(OfflinePlayer player, String worldName) { return true; }

    @Override public boolean hasBankSupport() { return false; }
    @Override public EconomyResponse createBank(String name, String player) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse createBank(String name, OfflinePlayer player) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse deleteBank(String name) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse bankBalance(String name) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse bankHas(String name, double amount) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse isBankOwner(String name, String playerName) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse isBankOwner(String name, OfflinePlayer player) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse isBankMember(String name, String playerName) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public EconomyResponse isBankMember(String name, OfflinePlayer player) { return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Bank Support"); }
    @Override public List<String> getBanks() { return new ArrayList<>(); }
}