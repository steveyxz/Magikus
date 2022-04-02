package me.magikus.core.commands;

import de.tr7zw.nbtapi.NBTItem;
import me.magikus.core.items.MagikusItem;
import me.magikus.core.items.ModifierType;
import me.magikus.core.items.additions.Addition;
import me.magikus.core.items.additions.AdditionInfo;
import me.magikus.core.items.additions.AdditionManager;
import me.magikus.core.util.DataUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MagikusAddAddition implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player p) || strings.length < 1 || !s.equals("sbadd")) {
            return true;
        }
        String additionId = strings[0];
        AdditionInfo info = AdditionManager.getAddition(additionId);
        if (info == null) {
            p.sendMessage(ChatColor.RED + "Invalid addition type: " + additionId);
            return true;
        }
        ItemStack stack = p.getInventory().getItemInMainHand();
        if (new NBTItem(stack).getBoolean("vanilla")) {
            MagikusItem sbi = MagikusItem.getItemFrom(stack, p);
            if (sbi == null) {
                p.sendMessage(ChatColor.RED + "Item on your hand is not valid: " + additionId);
                return true;
            }
            boolean a = false;
            Addition instance = info.getNewInstance(sbi);
            if (info.type() == ModifierType.ABILITY) {
                sbi.abilityAdditions().addAddition(instance);
                a = true;
            } else if (info.type() == ModifierType.STAT) {
                sbi.statAdditions().addAddition(instance);
                a = true;
            } else if (info.type() == ModifierType.RARITY) {
                sbi.rarityAdditions().addAddition(instance);
                a = true;
            }
            p.getInventory().setItemInMainHand(sbi.getSkyblockItem());
            if (a) {
                p.sendMessage(ChatColor.GREEN + "Successfully applied the addition " + info.id() + " to your held item");
            } else {
                p.sendMessage(ChatColor.RED + "Ummm if your seeing thins it means something went wrong probably. ADDITION_ID: " + info.id());
            }
            p.getInventory().setItemInMainHand(sbi.getSkyblockItem());
        } else {
            MagikusItem sbi = DataUtils.getSkyblockItem(stack, p);
            if (sbi == null) {
                p.sendMessage(ChatColor.RED + "Item on your hand is not valid: " + additionId);
                return true;
            }
            boolean a = false;
            Addition instance = info.getNewInstance(sbi);
            if (info.type() == ModifierType.ABILITY) {
                sbi.abilityAdditions().addAddition(instance);
                a = true;
            } else if (info.type() == ModifierType.STAT) {
                sbi.statAdditions().addAddition(instance);
                a = true;
            } else if (info.type() == ModifierType.RARITY) {
                sbi.rarityAdditions().addAddition(instance);
                a = true;
            }
            p.getInventory().setItemInMainHand(sbi.getSkyblockItem());
            if (a) {
                p.sendMessage(ChatColor.GREEN + "Successfully applied the addition " + info.id() + " to your held item");
            } else {
                p.sendMessage(ChatColor.RED + "Ummm if your seeing thins it means something went wrong probably. ADDITION_ID: " + info.id());
            }
        }
        return true;
    }
}
