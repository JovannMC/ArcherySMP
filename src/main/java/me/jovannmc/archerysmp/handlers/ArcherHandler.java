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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
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

public class ArcherHandler implements Listener {
    private final ArcherySMP plugin;
    private HashMap<UUID, Long> superArrowCooldown = new HashMap<>();
    private HashMap<UUID, Long> strengthCooldown = new HashMap<>();

    public ArcherHandler(ArcherySMP plugin) {
        this.plugin = plugin;
    }

    public void addArcher(Player player, boolean giveBow) {
        plugin.archers.add(player.getUniqueId());

        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bow.getItemMeta();

        bowMeta.setUnbreakable(true);
        bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
        bowMeta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
        bowMeta.setDisplayName("Archer's Bow");
        bowMeta.setLore(Utils.colorizeList(List.of("&7Left click to shoot a &cSuper arrow&7!", "&7Right click to get &cStrength&7!")));

        PersistentDataContainer dataContainer = bowMeta.getPersistentDataContainer();
        dataContainer.set(plugin.getRoleKey(), PersistentDataType.STRING, "archer");
        dataContainer.set(plugin.getOwnerKey(), PersistentDataType.STRING, player.getUniqueId().toString());

        bow.setItemMeta(bowMeta);

        if (giveBow) { player.getInventory().addItem(bow); }

        Utils.announceMessage("&a" + player.getName() + " &7is an archer!");
        Utils.sendMessage(player, "&aYou are now an archer!");

        plugin.configUtils.getData().set(player.getUniqueId() + ".role", "archer");
        plugin.configUtils.saveFile("data");
    }

    public void removeArcher(Player player) {
        plugin.archers.remove(player.getUniqueId());
        Utils.sendMessage(player, "&cYou are no longer an archer!");
        Utils.announceMessage("&a" + player.getName() + " &7is no longer an archer!");

        plugin.configUtils.getData().set(player.getUniqueId() + ".role", "player");
        plugin.configUtils.saveFile("data");
    }

    @EventHandler
    public void bowInteract(PlayerInteractEvent e) {
        if (plugin.archers.contains(e.getPlayer().getUniqueId()) && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BOW) {
            Action action = e.getAction();
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                shootSuperArrow(e.getPlayer());
            } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                strength(e.getPlayer());
            }
        }
    }

    @EventHandler
    private void bowLeftClickEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && plugin.archers.contains(e.getDamager().getUniqueId()) && ((Player) e.getDamager()).getInventory().getItemInMainHand().getType() == Material.BOW) {
            shootSuperArrow((Player) e.getDamager());
        }
    }

    private void shootSuperArrow(Player player) {
        // Shoot a "super" arrow that deals 2 hearts, cooldown of 1 minute
        if (superArrowCooldown.containsKey(player.getUniqueId())) {
            if (superArrowCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                Utils.sendMessage(player, "&cYou can't use 'Super arrow' yet!");
                return;
            }
        }

        boolean hasStrength = player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE);
        AbstractArrow arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection());
        double currentDamage = arrow.getDamage();
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setDamage(currentDamage + (hasStrength ? 4.0 : 0.0));
        superArrowCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 60000);

        CooldownHandler cooldownHandler = new CooldownHandler(player, 60000, "Super arrow");
        cooldownHandler.runTaskTimer(plugin, 0L, 20L);
    }

    private void strength(Player player) {
        // Give player "strength 2" (more arrow damage) for 10 seconds, cooldown of 40 seconds.
        // Still allow bow to be used normally
        if (strengthCooldown.containsKey(player.getUniqueId())) {
            if (strengthCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                Utils.sendMessage(player, "&cYou can't use 'Strength' yet!");
                return;
            }
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2));
        strengthCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 40000);

        CooldownHandler cooldownHandler = new CooldownHandler(player, 40000, "Strength");
        cooldownHandler.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler
    public void onArrowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (plugin.archers.contains(player.getUniqueId())) {
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
