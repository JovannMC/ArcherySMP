package me.jovannmc.archerysmp.handlers;

import me.jovannmc.archerysmp.ArcherySMP;
import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HunterHandler implements Listener {
    private final ArcherySMP plugin = JavaPlugin.getPlugin(ArcherySMP.class);
    Utils utils = new Utils();

    public void addHunter(Player player) {
        utils.sendMessage(player, "&aYou are now a hunter!");

        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        crossbow.getItemMeta().setUnbreakable(true);
        crossbow.getItemMeta().setDisplayName("Hunter's Crossbow");
        crossbow.getItemMeta().addEnchant(Enchantment.QUICK_CHARGE, 5, true);
        crossbow.getItemMeta().setLore(utils.colorizeList(List.of("&7Left click to &cPoison&7!")));
        player.getInventory().addItem(crossbow);
    }

    @EventHandler
    public void bowLeftClick(PlayerInteractEvent e) {
        if (plugin.hunters.containsKey(e.getPlayer().getUniqueId()) && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.CROSSBOW) {
            Action action = e.getAction();

            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                // Poison all entities within a 10 block radius, cooldown of 1 minute
                if (plugin.hunters.containsKey(e.getPlayer().getUniqueId())) {
                    utils.sendMessage(e.getPlayer(), "&cYou can't use 'Poison' yet!");
                    return;
                }

                e.getPlayer().getWorld().getNearbyEntities(e.getPlayer().getLocation(), 10, 10, 10).forEach(entity -> {
                    if (entity instanceof Player) {
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
                    }
                });
                plugin.hunters.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 60000);

                CooldownHandler cooldownHandler = new CooldownHandler(e.getPlayer(), 60000, "Poison");
                cooldownHandler.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }
}