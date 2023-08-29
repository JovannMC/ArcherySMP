package me.jovannmc.archerysmp.handlers;


import me.jovannmc.archerysmp.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CooldownHandler extends BukkitRunnable {
    private final Player player;
    private final long cooldownEndTime;
    private final String abilityName;

    public CooldownHandler(Player player, long cooldownDuration, String abilityName) {
        this.player = player;
        this.cooldownEndTime = System.currentTimeMillis() + cooldownDuration;
        this.abilityName = abilityName;
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        long remainingTime = cooldownEndTime - currentTime;

        if (remainingTime <= 0) {
            cancel();
            sendActionBar(player, "");
        } else {
            String formattedTime = formatTime(remainingTime);
            sendActionBar(player, new Utils().color("&e" + abilityName + " cooldown: " + formattedTime));
        }
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds %= 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    private void sendActionBar(Player player, String message) {
        TextComponent component = new TextComponent(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }
}
