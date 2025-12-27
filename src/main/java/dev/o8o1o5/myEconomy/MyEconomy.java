package dev.o8o1o5.myEconomy;

import dev.o8o1o5.myEconomy.command.BalanceCommand;
import dev.o8o1o5.myEconomy.data.DataManager;
import dev.o8o1o5.myeconomy.vault.Economy_MyEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyEconomy extends JavaPlugin {
    private static Economy econ = null;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        this.dataManager = new DataManager(this);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            getServer().getServicesManager().register(
                    Economy.class,
                    new Economy_MyEconomy(this),
                    this,
                    ServicePriority.Highest
            );

            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                econ = rsp.getProvider();
                getLogger().info("Vault Economy가 성공적으로 연결되었습니다.");
            }
        }

        getCommand("me").setExecutor(new BalanceCommand(this));

        getLogger().info("MyEconomy 플러그인이 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MyEconomy 플러그인이 비활성화되었습니다.");
    }

    public static Economy getVault() {
        return econ;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
