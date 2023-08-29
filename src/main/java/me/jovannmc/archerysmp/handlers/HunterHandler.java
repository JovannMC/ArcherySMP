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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HunterHandler implements Listener {
    private HashMap<UUID, Long> poisonCooldown = new HashMap<>();
    private final ArcherySMP plugin;

    public HunterHandler(ArcherySMP plugin) {
        this.plugin = plugin;
    }

    public void addHunter(Player player) {
        Utils.sendMessage(player, "&aYou are now a hunter!");
        plugin.hunters.add(player.getUniqueId());

        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        ItemMeta crossbowMeta = crossbow.getItemMeta();

        crossbowMeta.setUnbreakable(true);
        crossbowMeta.setDisplayName("Hunter's Crossbow");
        crossbowMeta.addEnchant(Enchantment.QUICK_CHARGE, 5, true);
        crossbowMeta.setLore(Utils.colorizeList(List.of("&7Left click to &cPoison&7!")));

        PersistentDataContainer dataContainer = crossbowMeta.getPersistentDataContainer();
        dataContainer.set(plugin.getRoleKey(), PersistentDataType.STRING, "hunter");

        crossbow.setItemMeta(crossbowMeta);

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.CROSSBOW) {
                ItemMeta itemMeta = item.getItemMeta();
                PersistentDataContainer itemDataContainer = itemMeta.getPersistentDataContainer();
                if (itemDataContainer.has(plugin.getRoleKey(), PersistentDataType.STRING)) {
                    if (itemDataContainer.get(plugin.getRoleKey(), PersistentDataType.STRING).equals("hunter")) {
                        return;
                    }
                }
            }
        }
        player.getInventory().addItem(crossbow);

        Utils.announceMessage("&a" + player.getName() + " &7is the hunter!");
        Utils.sendMessage(player, "&aYou are now a hunter!");

        plugin.configUtils.getData().set(player.getUniqueId() + ".role", "hunter");
        plugin.configUtils.saveFile("data");
    }

    @EventHandler
    public void bowLeftClick(PlayerInteractEvent e) {
        if (plugin.hunters.contains(e.getPlayer().getUniqueId()) && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.CROSSBOW) {
            Action action = e.getAction();

            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                // Poison all entities within a 10 block radius, cooldown of 1 minute
                if (poisonCooldown.containsKey(e.getPlayer().getUniqueId())) {
                    if (poisonCooldown.get(e.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                        Utils.sendMessage(e.getPlayer(), "&cYou can't use 'Poison' yet!");
                        return;
                    }
                }

                e.getPlayer().getWorld().getNearbyEntities(e.getPlayer().getLocation(), 10, 10, 10).forEach(entity -> {
                    if (entity instanceof Player) {
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
                    }
                });
                poisonCooldown.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 60000);

                CooldownHandler cooldownHandler = new CooldownHandler(e.getPlayer(), 60000, "Poison");
                cooldownHandler.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }
}
