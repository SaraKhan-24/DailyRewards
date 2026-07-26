package org.sara.minecraftPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RewardsListener implements Listener {

    private static final long COOLDOWN_MILLIS = TimeUnit.HOURS.toMillis(24);

    private final DatabaseManager databaseManager;

    public RewardsListener(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(
                Component.text("Welcome! ", NamedTextColor.GREEN)
                        .append(Component.text("Type ", NamedTextColor.GRAY))
                        .append(Component.text("/rewards", NamedTextColor.GOLD))
                        .append(Component.text(" to claim your daily reward.", NamedTextColor.GRAY))
        );
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Only care about clicks while OUR gui is the top inventory open.
        if (!(event.getInventory().getHolder() instanceof RewardsGUI)) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();
        // If the click landed in the player's own inventory (below the GUI), let it behave normally.
        if (clickedInventory == null || !(clickedInventory.getHolder() instanceof RewardsGUI)) {
            return;
        }

        // Any click inside the GUI itself should never move the item around.
        event.setCancelled(true);

        if (event.getSlot() != RewardsGUI.REWARD_SLOT) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        long lastClaim = databaseManager.getLastClaim(uuid);
        long elapsed = System.currentTimeMillis() - lastClaim;

        if (elapsed < COOLDOWN_MILLIS) {
            long remainingMillis = COOLDOWN_MILLIS - elapsed;
            long hours = TimeUnit.MILLISECONDS.toHours(remainingMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60;

            player.sendMessage(Component.text(
                    "You already claimed your reward. Try again in " + hours + "h " + minutes + "m.",
                    NamedTextColor.RED
            ));
            return;
        }

        databaseManager.claimReward(uuid, RewardsGUI.REWARD_AMOUNT);
        int newBalance = databaseManager.getBalance(uuid);

        player.sendMessage(Component.text(
                "You claimed $" + RewardsGUI.REWARD_AMOUNT + "! Your new balance is $" + newBalance + ".",
                NamedTextColor.GREEN
        ));
        player.closeInventory();
    }
}
