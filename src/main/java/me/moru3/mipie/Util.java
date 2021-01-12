package me.moru3.mipie;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Util {
    public static ItemStack generateItem(Material material, String displayName, List<String> lore) {
        ItemStack result = new ItemStack(material);
        ItemMeta itemMeta = result.getItemMeta();
        itemMeta.setLore(lore);
        itemMeta.setDisplayName(displayName);
        result.setItemMeta(itemMeta);
        return result;
    }

    public static boolean inRange(Location pos1, Location pos2, Location i) {
        return !(Math.max(pos1.getBlockX(), pos2.getBlockX())<i.getBlockX()||Math.min(pos1.getBlockX(), pos2.getBlockX())>i.getBlockX()||Math.max(pos1.getBlockY(), pos2.getBlockY())<i.getBlockY()||Math.min(pos1.getBlockY(), pos2.getBlockY())>i.getBlockY()||Math.max(pos1.getBlockZ(), pos2.getBlockZ())<i.getBlockZ()||Math.min(pos1.getBlockZ(), pos2.getBlockZ())>i.getBlockZ());
    }
}
