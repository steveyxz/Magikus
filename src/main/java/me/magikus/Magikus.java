package me.magikus;

import me.magikus.additions.AdditionRegister;
import me.magikus.core.ConsoleLogger;
import me.magikus.core.commands.*;
import me.magikus.core.entities.damage.DamageManager;
import me.magikus.core.entities.EntityUpdater;
import me.magikus.core.items.ItemUpdater;
import me.magikus.core.player.PlayerStatManager;
import me.magikus.core.player.PlayerUpdater;
import me.magikus.core.util.ConfigManager;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import static me.magikus.abilities.AbilityRegister.registerAbilities;
import static me.magikus.core.player.BaseStatManager.initializeBaseStats;
import static me.magikus.core.player.BaseStatManager.repairDefaultStats;
import static me.magikus.data.recipes.RecipeRegister.registerRecipes;
import static me.magikus.entities.EntityRegister.registerEntityInfos;
import static me.magikus.items.ItemRegister.registerItems;

public final class Magikus extends JavaPlugin {

    public static ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(JavaPlugin.getPlugin(Magikus.class));
        repairDefaultStats();
        initializeBaseStats(this);
        registerCommands();
        AdditionRegister.registerEnchants();
        registerListeners();
        registerItems();
        registerAbilities();
        AdditionRegister.registerAdditions();
        AdditionRegister.registerReforges();
        registerEntityInfos();
        registerRecipes();
        updateEverything();
        ConsoleLogger.console("Loaded Magikus plugin on version " + getDescription().getVersion() + "...");
    }

    private void updateEverything() {
        for (Player p : getServer().getOnlinePlayers()) {
            Inventory inventory = p.getInventory();
            ItemUpdater.updateVanilla(inventory, p);
            ItemUpdater.idify(inventory, p);
            ItemUpdater.updateInventory(inventory, p);
        }

        for (World w : getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (!e.getType().isAlive() || e.getType() == EntityType.ARMOR_STAND || e.getType() == EntityType.PLAYER) {
                    continue;
                }
                EntityUpdater.updateStats(e);
                EntityUpdater.updateName(e);
            }
        }
    }

    @SuppressWarnings("all")
    private void registerCommands() {
        getCommand("mgive").setExecutor(new MagikusGive());
        getCommand("msummon").setExecutor(new MagikusSummon());
        getCommand("madd").setExecutor(new MagikusAddAddition());
        getCommand("mreforge").setExecutor(new MagikusReforge());
        getCommand("mstar").setExecutor(new MagikusStar());
        getCommand("mfrag").setExecutor(new MagikusFrag());
        getCommand("menchant").setExecutor(new MagikusEnchant());
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new ItemUpdater(), this);
        this.getServer().getPluginManager().registerEvents(new EntityUpdater(getServer()), this);
        this.getServer().getPluginManager().registerEvents(new DamageManager(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerStatManager(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerUpdater(getServer()), this);
    }

    @Override
    public void onDisable() {
        ConsoleLogger.console("Shutting down Skyblock plugin...");
    }

}
