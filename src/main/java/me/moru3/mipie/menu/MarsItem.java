package me.moru3.mipie.Menu;

import me.moru3.marstools.ContentsList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MarsItem extends ItemStack {
    public MarsItem setDisplayName(String name) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(name);
        this.setItemMeta(itemMeta);
        return this;
    }

    public String getDisplayName() {
        return this.getItemMeta().getDisplayName();
    }

    public List<String> getLore() {
        return (ContentsList<String>) this.getItemMeta().getLore();
    }

    public MarsItem setLore(List<String> lore) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setLore(lore);
        this.setItemMeta(itemMeta);
        return this;
    }
}
