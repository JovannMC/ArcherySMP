package me.jovannmc.archerysmp.handlers;

import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CooldownHandler extends BukkitRunnable {
    private final Player player;
    private final long cooldownEndTime;
    private final String abilityName;
    private final long cooldownDuration;
    private final BossBar bossBar;

    public CooldownHandler(Player player, long cooldownDuration, String abilityName) {
        this.player = player;
        this.cooldownEndTime = System.currentTimeMillis() + cooldownDuration;
        this.abilityName = abilityName;
        this.bossBar = Utils.createBossBar(abilityName, BarColor.BLUE, BarStyle.SOLID);
        this.cooldownDuration = cooldownDuration;
        bossBar.addPlayer(player);
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        long remainingTime = cooldownEndTime - currentTime;

        if (remainingTime <= 0) {
            cancel();
            bossBar.removeAll();
        } else {
            double progress = (double) remainingTime / cooldownDuration;
            progress = Math.max(0.0, Math.min(1.0, progress));
            bossBar.setProgress(progress);
        }
    }

    public void cancel() {
        bossBar.removeAll();
        super.cancel();
    }
}
