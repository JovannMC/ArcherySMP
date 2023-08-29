package me.jovannmc.archerysmp.handlers;

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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ArcherHandler implements Listener {
    public void addArcher(Player player) {
        Utils.sendMessage(player, "&aYou are now an archer!");

        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bow.getItemMeta();

        bowMeta.setUnbreakable(true);
        bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
        bowMeta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
        bowMeta.setDisplayName("Archer's Bow");
        bowMeta.setLore(Utils.colorizeList(List.of("&7Left click to shoot a &cSuper arrow&7!", "&7Right click to get &cStrength&7!")));

        PersistentDataContainer dataContainer = bowMeta.getPersistentDataContainer();
        dataContainer.set(Utils.plugin.getRoleKey(), PersistentDataType.STRING, "archer");

        bow.setItemMeta(bowMeta);
        player.getInventory().addItem(bow);
    }


    @EventHandler
    public void bowLeftClick(PlayerInteractEvent e) {
        if (Utils.plugin.archers.containsKey(e.getPlayer().getUniqueId()) && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BOW) {
            Action action = e.getAction();
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                // Shoot a "super" arrow that deals 2 hearts, cooldown of 1 minute
                if (Utils.plugin.archers.get(e.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                    Utils.sendMessage(e.getPlayer(), "&cYou can't use 'Super arrow' yet!");
                    return;
                }

                boolean hasStrength = e.getPlayer().hasPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                AbstractArrow arrow = e.getPlayer().launchProjectile(Arrow.class, e.getPlayer().getLocation().getDirection());
                double currentDamage = arrow.getDamage();
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                arrow.setDamage(currentDamage + (hasStrength ? 4.0 : 0.0));
                Utils.plugin.archers.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 60000);

                CooldownHandler cooldownHandler = new CooldownHandler(e.getPlayer(), 60000, "Super arrow");
                cooldownHandler.runTaskTimer(Utils.plugin, 0L, 20L);
            } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                // Give player "strength 2" (more arrow damage) for 10 seconds, cooldown of 40 seconds.
                // Still allow bow to be used normally
                if (Utils.plugin.archers.get(e.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                    Utils.sendMessage(e.getPlayer(), "&cYou can't use 'Strength' yet!");
                    return;
                }

                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2));
                Utils.plugin.archers.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 40000);

                CooldownHandler cooldownHandler = new CooldownHandler(e.getPlayer(), 40000, "Strength");
                cooldownHandler.runTaskTimer(Utils.plugin, 0L, 20L);
            }
        }
    }

    @EventHandler
    public void onArrowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (Utils.plugin.archers.containsKey(player.getUniqueId())) {
                boolean hasStrength = player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                AbstractArrow arrow = (AbstractArrow) e.getProjectile();
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

                double currentDamage = arrow.getDamage();
                double newDamage = currentDamage + (hasStrength ? 4.0 : 0.0);

                arrow.setDamage(newDamage);
            }
        }
    }
}
