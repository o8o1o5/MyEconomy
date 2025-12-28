package dev.o8o1o5.myEconomy;

import dev.o8o1o5.myEconomy.command.EconomyCommand;
import dev.o8o1o5.myEconomy.data.CoinManager;
import dev.o8o1o5.myEconomy.data.DataManager;
import dev.o8o1o5.myEconomy.item.EconomyItemManager;
import dev.o8o1o5.myeconomy.vault.Economy_MyEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyEconomy extends JavaPlugin {
    private static Economy vaultEconomy = null;
    private DataManager dataManager;
    private CoinManager coinManager;
    private EconomyItemManager itemManager;

    @Override
    public void onEnable() {
        this.dataManager = new DataManager(this);
        this.coinManager = new CoinManager();
        this.itemManager = new EconomyItemManager();

        // Vault 연동 체크 및 등록
        setupVault();
        itemManager.registerItems();

        // 명령어 등록 (getCommand가 null인지 체크하여 안전하게 등록)
        registerCommands();

        getLogger().info("MyEconomy 플러그인이 성공적으로 활성화되었습니다.");
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultEconomy = new Economy_MyEconomy(this);
            getServer().getServicesManager().register(
                    Economy.class,
                    vaultEconomy,
                    this,
                    ServicePriority.Highest
            );
            getLogger().info("Vault와 연동되어 외부 플러그인과 경제를 공유합니다.");
        } else {
            getLogger().warning("Vault 플러그인을 찾을 수 없습니다. 외부 플러그인 연동이 제한됩니다.");
        }
    }

    private void registerCommands() {
        if (getCommand("me") != null) {
            getCommand("me").setExecutor(new EconomyCommand(this));
        } else {
            getLogger().severe("plugin.yml에서 'me' 명령어를 찾을 수 없습니다!");
        }
    }

    public static Economy getVault() {
        return vaultEconomy;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public CoinManager getCoinManager() {
        return coinManager;
    }

    public EconomyItemManager getItemManager() {
        return itemManager;
    }
}