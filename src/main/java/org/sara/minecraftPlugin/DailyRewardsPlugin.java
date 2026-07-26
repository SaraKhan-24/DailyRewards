package org.sara.minecraftPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class DailyRewardsPlugin extends JavaPlugin {

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.connect();
        this.databaseManager.createTable();

        getServer().getPluginManager().registerEvents(new RewardsListener(databaseManager), this);

        RewardsCommand rewardsCommand = new RewardsCommand(databaseManager);
        getCommand("rewards").setExecutor(rewardsCommand);

        getLogger().info("DailyRewards has been enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("DailyRewards has been disabled!");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
