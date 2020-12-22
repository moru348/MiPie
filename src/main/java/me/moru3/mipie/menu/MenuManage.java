package me.moru3.mipie.menu;

import me.moru3.marstools.ContentsMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class MenuManage {
    private static final ContentsMap<Player, ContentsMap<ItemStack, GuiItem>> actions = new ContentsMap<>();
    private static ItemStack next;
    private static ItemStack back;
    private static ItemStack noNext;
    private static ItemStack noBack;

    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(player==null) { return; }
        GuiItem guiItem = actions.get(player).get(event.getCurrentItem());
        if(guiItem==null) { return; }
        event.setCancelled(true);
        guiItem.runAction(player, event.getCurrentItem());
    }

    public MenuManage() {}

    public static void setNextItem(ItemStack itemStack) { next = itemStack; }

    public static void setBackItem(ItemStack itemStack) { back = itemStack; }

    public static void setNoNextItem(ItemStack itemStack) { noNext = itemStack; }

    public static void setNoBackItem(ItemStack itemStack) { noBack = itemStack; }

    public static ItemStack getNextItem() {
        if(next==null) {
            next = new ItemStack(Material.ARROW);
            ItemMeta itemMeta = next.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + "Next");
            itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Click to move to the next page"));
            next.setItemMeta(itemMeta);
        }
        return next.clone();
    }

    public static ItemStack getBackItem() {
        if(back==null) {
            back = new ItemStack(Material.ARROW);
            ItemMeta itemMeta = back.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + "Back");
            itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Click to return to the previous page."));
            back.setItemMeta(itemMeta);
        }
        return back.clone();
    }

    public static ItemStack getNoNextItem() {
        if(noNext==null) {
            noNext = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta itemMeta = noNext.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Last page");
            itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + "This page is the final."));
            noNext.setItemMeta(itemMeta);
        }
        return noNext.clone();
    }

    public static ItemStack getNoBackItem() {
        if(noBack==null) {
            noBack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta itemMeta = noBack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "First page");
            itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + "This page is the first."));
            noBack.setItemMeta(itemMeta);
        }
        return noBack.clone();
    }

    public static void addActionItem(Player player, ContentsMap<ItemStack, GuiItem> action) { actions.put(player, action);}
    public static ContentsMap<Player, ContentsMap<ItemStack, GuiItem>> getActions() { return actions; }
}
