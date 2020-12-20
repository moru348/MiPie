package me.moru3.mipie;

import me.moru3.marstools.ContentsMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiManage implements Listener {
    public static ContentsMap<ItemStack, GuiItem> actions = new ContentsMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(player==null) { return; }
        GuiItem guiItem = actions.get(event.getCurrentItem());
        if(guiItem==null) { return; }
        guiItem.runAction(player, event.getCurrentItem());
    }

    public static void addActionItem(GuiItem item) { actions.put(item.getItemStack(), item); }
}
