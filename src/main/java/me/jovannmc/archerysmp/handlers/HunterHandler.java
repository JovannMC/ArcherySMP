package me.jovannmc.archerysmp.handlers;

import me.jovannmc.archerysmp.utils.Utils;
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

public class HunterHandler implements Listener {
    Utils utils = new Utils();

    ArrayList<UUID> hunters = new ArrayList<UUID>();

    public void addHunter(Player player) {
        utils.sendMessage(player, "&aYou are now a hunter!");

        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        crossbow.getItemMeta().setUnbreakable(true);
        crossbow.getItemMeta().setDisplayName("Hunter's Crossbow");
        crossbow.getItemMeta().addEnchant(Enchantment.QUICK_CHARGE, 5, true);
        player.getInventory().addItem(crossbow);
    }

    @EventHandler
    public void bowLeftClick(PlayerInteractEvent e) {
        if (hunters.contains(e.getPlayer().getUniqueId()) && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.CROSSBOW) {
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
