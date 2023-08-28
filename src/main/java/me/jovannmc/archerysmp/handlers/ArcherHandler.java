package me.jovannmc.archerysmp.handlers;

import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class ArcherHandler implements Listener {
    Utils utils = new Utils();

    ArrayList<UUID> archers = new ArrayList<UUID>();

    public void addArcher(Player player) {
        utils.sendMessage(player, "&aYou are now an archer!");

        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.getItemMeta().setUnbreakable(true);
        bow.getItemMeta().addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        bow.getItemMeta().addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
        bow.getItemMeta().addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
        bow.getItemMeta().setDisplayName("Archer's Bow");
        player.getInventory().addItem(bow);
    }

    @EventHandler
    public void bowLeftClick(PlayerInteractEvent e) {
        if (archers.contains(e.getPlayer().getUniqueId()) && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BOW) {
            Action action = e.getAction();
            ItemStack itemInHand = e.getPlayer().getInventory().getItemInMainHand();

            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                // Shoot arrow that deals 2 hearts, cooldown of 1 minute

            } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                // Give player "strength 2" (more arrow damage) for 10 seconds, cooldown of 40 seconds.
                // Still allow bow to be used normally
            }

        }
    }
}
