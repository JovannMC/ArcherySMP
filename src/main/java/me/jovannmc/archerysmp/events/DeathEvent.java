package me.jovannmc.archerysmp.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DeathEvent implements Listener {
    @EventHandler
    public void onSelectedDeath(PlayerDeathEvent e) {

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        ItemStack bow = player.getInventory().getItemInMainHand();

        if (bow.getType() == Material.BOW) {
            ItemMeta itemMeta = bow.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

            if (dataContainer.has(roleKey, PersistentDataType.STRING)) {
                String role = dataContainer.get(roleKey, PersistentDataType.STRING);

                if (role.equals("archer/hunter")) {
                    // Remove the role information from the bow
                    dataContainer.remove(roleKey);
                    bow.setItemMeta(itemMeta);
                }
            }
        }
    }
}
