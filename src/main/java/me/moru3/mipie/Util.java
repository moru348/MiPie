package me.moru3.mipie;

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
}
