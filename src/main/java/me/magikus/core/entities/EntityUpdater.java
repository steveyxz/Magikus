package me.magikus.core.entities;

import me.magikus.Magikus;
import me.magikus.core.entities.name.EntityNameLines;
import me.magikus.core.entities.name.EntityNameManager;
import me.magikus.core.entities.stats.EntityStatType;
import me.magikus.core.tools.reflection.JavaAccessor;
import me.magikus.core.tools.util.EntityUtils;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class EntityUpdater implements Listener {

    public EntityUpdater(Server s) {
        new ConstantUpdater(s).runTaskTimer(JavaPlugin.getPlugin(Magikus.class), 0, 1);
        new PlayerAttackStrengthUpdater(s).runTaskTimer(JavaPlugin.getPlugin(Magikus.class), 0, 4);
    }

    public static void updateStats(Entity e) {
        EntityUtils.repairEntity(e);
        if (e instanceof LivingEntity) {
            ((LivingEntity) e).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(EntityStatType.getStat(e, EntityStatType.SPEED).floatValue() / 500f);
        }
    }

    public static void updateName(Entity e) {
        boolean ignore = EntityUtils.getIgnore(e);
        if (e.getPassengers().size() < 1 && !ignore) {
            EntityNameLines nl = EntityNameManager.getLines(e);
            String displayName = EntityUtils.getDisplayName(e);
            if (!nl.getLine(0).equals(displayName)) {
                nl.setLine(0, displayName);
                String elementalInfo = EntityUtils.getElementalInfo(e);
                if (elementalInfo.equals("")) {
                    nl.removeLine(1);
                } else {
                    nl.setLine(1, elementalInfo);
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getType().isAlive() || entity.getType() == EntityType.ARMOR_STAND || entity.getType() == EntityType.PLAYER) {
            return;
        }
        updateStats(entity);
        updateName(entity);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity e : event.getChunk().getEntities()) {
            if (!e.getType().isAlive() || e.getType() == EntityType.ARMOR_STAND || e.getType() == EntityType.PLAYER) {
                return;
            }
            updateStats(e);
            updateName(e);
        }
        EntityNameManager.wipeOld(event.getChunk());
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        for (Entity e : event.getWorld().getEntities()) {
            if (!e.getType().isAlive() || e.getType() == EntityType.ARMOR_STAND || e.getType() == EntityType.PLAYER) {
                return;
            }
            updateStats(e);
            updateName(e);
        }
    }
}

class PlayerAttackStrengthUpdater extends BukkitRunnable {

    private final Server s;

    public PlayerAttackStrengthUpdater(Server s) {
        this.s = s;
    }

    @Override
    public void run() {
        for (Player p : s.getOnlinePlayers()) {
            ServerPlayer cp = ((CraftPlayer) p).getHandle();
            Field aQ = JavaAccessor.getField(net.minecraft.world.entity.LivingEntity.class, "aQ");
            if ((int) JavaAccessor.getValue(cp, aQ) > cp.getCurrentItemAttackStrengthDelay() * 0.9 - 0.5) {
                JavaAccessor.setValue(cp, aQ, cp.getCurrentItemAttackStrengthDelay() * 0.9 - 0.5);
            }
        }
    }
}

class ConstantUpdater extends BukkitRunnable {

    private final Server s;
    private int count = 0;

    public ConstantUpdater(Server s) {
        this.s = s;
    }

    @Override
    public void run() {
        for (World w : s.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (!(!e.getType().isAlive() || e.getType() == EntityType.ARMOR_STAND || e.getType() == EntityType.PLAYER)) {
                    EntityUpdater.updateName(e);
                }
            }
        }
        if (count % 5 == 0) {
            EntityNameManager.tick();
        }
        if (count == 20) {
            for (World w : s.getWorlds()) {
                for (Entity e : w.getEntities()) {
                    if (!(!e.getType().isAlive() || e.getType() == EntityType.ARMOR_STAND || e.getType() == EntityType.PLAYER)) {
                        EntityUpdater.updateStats(e);
                    }
                }
            }
            count = 0;
        }
        count++;
    }
}
