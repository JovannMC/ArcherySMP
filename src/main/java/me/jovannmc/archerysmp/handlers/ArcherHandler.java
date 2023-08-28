package me.jovannmc.archerysmp.handlers;

import me.jovannmc.archerysmp.ArcherySMP;
import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class ArcherHandler implements Listener {
    private final ArcherySMP plugin = JavaPlugin.getPlugin(ArcherySMP.class);
    private final Utils utils = new Utils();

    private HashMap<UUID, Long> archers = new HashMap<>();

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
        if (archers.containsKey(e.getPlayer().getUniqueId()) && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BOW) {
            Action action = e.getAction();
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                // Shoot a "super" arrow that deals 2 hearts, cooldown of 1 minute
                if (archers.get(e.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                    utils.sendMessage(e.getPlayer(), "&cYou can't use 'Super arrow' yet!");
                    return;
                }

                boolean hasStrength = e.getPlayer().hasPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                AbstractArrow arrow = e.getPlayer().launchProjectile(Arrow.class, e.getPlayer().getLocation().getDirection());
                double currentDamage = arrow.getDamage();
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                arrow.setDamage(currentDamage + (hasStrength ? 4.0 : 0.0));
                archers.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 60000);

                CooldownHandler cooldownHandler = new CooldownHandler(e.getPlayer(), 60000, "Super arrow");
                cooldownHandler.runTaskTimer(plugin, 0L, 20L);
            } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                // Give player "strength 2" (more arrow damage) for 10 seconds, cooldown of 40 seconds.
                // Still allow bow to be used normally
                if (archers.get(e.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                    utils.sendMessage(e.getPlayer(), "&cYou can't use 'Strength' yet!");
                    return;
                }

                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2));
                archers.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 40000);

                CooldownHandler cooldownHandler = new CooldownHandler(e.getPlayer(), 40000, "Strength");
                cooldownHandler.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }

    @EventHandler
    public void onArrowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (archers.containsKey(player.getUniqueId())) {
                boolean hasStrength = player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                AbstractArrow arrow = (AbstractArrow) e.getProjectile();
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

                double currentDamage = arrow.getDamage();
                double newDamage = currentDamage + (hasStrength ? 4.0 : 0.0);

                arrow.setDamage(newDamage);
            }
        }
    }

    private void setActionBarCooldown(Player player, long cooldown) {

    }
}
