package org.sara.minecraftPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * A custom InventoryHolder marks this inventory as "ours" so RewardsListener
 * can tell it apart from any other 9-slot chest inventory a player might open.
 */
public class RewardsGUI implements InventoryHolder {

    public static final Component TITLE = Component.text("Daily Rewards", NamedTextColor.GOLD);
    public static final int REWARD_SLOT = 4;
    public static final int REWARD_AMOUNT = 100;

    private final Inventory inventory;

    public RewardsGUI() {
        this.inventory = Bukkit.createInventory(this, 9, TITLE);
        this.inventory.setItem(REWARD_SLOT, buildRewardItem());
    }

    private ItemStack buildRewardItem() {
        ItemStack item = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
                Component.text("Daily Reward - $" + REWARD_AMOUNT, NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
        );

        meta.lore(List.of(
                Component.text("Click to claim $" + REWARD_AMOUNT, NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Resets every 24 hours", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
