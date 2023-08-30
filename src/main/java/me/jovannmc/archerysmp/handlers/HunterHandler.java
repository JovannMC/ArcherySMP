package me.jovannmc.archerysmp.handlers;

import me.jovannmc.archerysmp.ArcherySMP;
import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    public void addHunter(Player player, boolean giveCrossbow) {
        plugin.hunters.add(player.getUniqueId());

        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        ItemMeta crossbowMeta = crossbow.getItemMeta();

        crossbowMeta.setUnbreakable(true);
        crossbowMeta.setDisplayName("Hunter's Crossbow");
        crossbowMeta.addEnchant(Enchantment.QUICK_CHARGE, 5, true);
        crossbowMeta.setLore(Utils.colorizeList(List.of("&7Left click to &cPoison&7!")));

        PersistentDataContainer dataContainer = crossbowMeta.getPersistentDataContainer();
        dataContainer.set(plugin.getRoleKey(), PersistentDataType.STRING, "hunter");
        dataContainer.set(plugin.getOwnerKey(), PersistentDataType.STRING, player.getUniqueId().toString());

        crossbow.setItemMeta(crossbowMeta);
        if (giveCrossbow) {
            player.getInventory().addItem(crossbow);
            player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        }

        Utils.announceMessage("&a" + player.getName() + " &7is a hunter!");
        Utils.sendMessage(player, "&aYou are now a hunter!");

        plugin.configUtils.getData().set(player.getUniqueId() + ".role", "hunter");
        plugin.configUtils.saveFile("data");
    }

    public void removeHunter(Player player) {
        plugin.hunters.remove(player.getUniqueId());
        Utils.sendMessage(player, "&cYou are no longer a hunter!");
        Utils.announceMessage("&a" + player.getName() + " &7is no longer a hunter!");

        plugin.configUtils.getData().set(player.getUniqueId() + ".role", "player");
        plugin.configUtils.saveFile("data");
    }

    @EventHandler
    public void bowLeftClick(PlayerInteractEvent e) {
        if (plugin.hunters.contains(e.getPlayer().getUniqueId()) && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.CROSSBOW) {
            Action action = e.getAction();

            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                poison(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void bowLeftClickEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && plugin.hunters.contains(e.getDamager().getUniqueId()) && ((Player) e.getDamager()).getInventory().getItemInMainHand().getType() == Material.CROSSBOW) {
            poison((Player) e.getDamager());
        }
    }

    private void poison(Player player) {
        // Poison all entities within a 10 block radius, cooldown of 1 minute
        if (poisonCooldown.containsKey(player.getUniqueId())) {
            if (poisonCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                Utils.sendMessage(player, "&cYou can't use 'Poison' yet!");
                return;
            }
        }
        int cooldown = plugin.getConfig().getInt("hunter.poison.cooldown") * 1000; // 1000 milliseconds = 1 second
        int duration = plugin.getConfig().getInt("hunter.poison.duration") * 20; // 20 ticks = 1 second
        int amplifier = plugin.getConfig().getInt("hunter.poison.amplifier");

        player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10).forEach(entity -> {
            if (entity instanceof Player && entity != player) {
                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier));
            }
        });
        poisonCooldown.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);

        CooldownHandler cooldownHandler = new CooldownHandler(player, cooldown, "Poison");
        cooldownHandler.runTaskTimer(plugin, 0L, 20L);
    }
}
