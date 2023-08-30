package me.jovannmc.archerysmp.events;

import me.jovannmc.archerysmp.ArcherySMP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ItemEvent implements Listener {
    private final ArcherySMP plugin = ArcherySMP.getPlugin(ArcherySMP.class);
    
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) { return; }
        Player player = (Player) e.getEntity();

        ItemMeta itemMeta = e.getItem().getItemStack().getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        if (dataContainer.has(plugin.getRoleKey(), PersistentDataType.STRING)) {
            String role = dataContainer.get(plugin.getRoleKey(), PersistentDataType.STRING);

            if (role.equals("archer") && !plugin.hunters.contains(player.getUniqueId())) {
                plugin.archerHandler.addArcher(player, false);
                dataContainer.set(plugin.getOwnerKey(), PersistentDataType.STRING, player.getUniqueId().toString());
            } else if (role.equals("hunter") && !plugin.archers.contains(player.getUniqueId())) {
                plugin.hunterHandler.addHunter(player, false);
                dataContainer.set(plugin.getOwnerKey(), PersistentDataType.STRING, player.getUniqueId().toString());
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Item item = e.getItemDrop();
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        if (dataContainer.has(plugin.getRoleKey(), PersistentDataType.STRING)) {
            String role = dataContainer.get(plugin.getRoleKey(), PersistentDataType.STRING);
            Player owner = Bukkit.getPlayer(UUID.fromString(dataContainer.get(plugin.getOwnerKey(), PersistentDataType.STRING)));
            if (role.equals("archer")) {
                plugin.archerHandler.removeArcher(owner);
            } else if (role.equals("hunter")) {
                plugin.hunterHandler.removeHunter(owner);
            }
        }
    }
}
