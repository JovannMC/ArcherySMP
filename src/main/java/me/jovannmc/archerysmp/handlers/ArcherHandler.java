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

        if (giveBow) {
            player.getInventory().addItem(bow);
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        }

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
        if (superArrowCooldown.containsKey(player.getUniqueId())) {
            if (superArrowCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                Utils.sendMessage(player, "&cYou can't use 'Super arrow' yet!");
                return;
            }
        }
        int cooldown = plugin.getConfig().getInt("archer.superArrow.cooldown") * 1000; // 1000 milliseconds = 1 second
        int extraArrowDamage = plugin.getConfig().getInt("archer.strength.extraArrowDamage");

        boolean hasStrength = player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE);
        AbstractArrow arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection());
        double currentDamage = arrow.getDamage();
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setDamage(currentDamage + (hasStrength ? extraArrowDamage : 0.0));
        superArrowCooldown.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);

        CooldownHandler cooldownHandler = new CooldownHandler(player, cooldown, "Super arrow");
        cooldownHandler.runTaskTimer(plugin, 0L, 20L);
    }

    private void strength(Player player) {
        if (strengthCooldown.containsKey(player.getUniqueId())) {
            if (strengthCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                Utils.sendMessage(player, "&cYou can't use 'Strength' yet!");
                return;
            }
        }
        int cooldown = plugin.getConfig().getInt("archer.strength.cooldown") * 1000; // 1000 milliseconds = 1 second
        int duration = plugin.getConfig().getInt("archer.strength.duration") * 20; // 20 ticks = 1 second
        int amplifier = plugin.getConfig().getInt("archer.strength.amplifier");

        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, amplifier));
        strengthCooldown.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);

        CooldownHandler cooldownHandler = new CooldownHandler(player, cooldown, "Strength");
        cooldownHandler.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler
    public void onArrowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (plugin.archers.contains(player.getUniqueId())) {
                int extraArrowDamage = plugin.getConfig().getInt("archer.strength.extraArrowDamage");
                boolean hasStrength = player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                AbstractArrow arrow = (AbstractArrow) e.getProjectile();
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

                double currentDamage = arrow.getDamage();
                double newDamage = currentDamage + (hasStrength ? extraArrowDamage : 0.0);

                arrow.setDamage(newDamage);
            }
        }
    }
}
