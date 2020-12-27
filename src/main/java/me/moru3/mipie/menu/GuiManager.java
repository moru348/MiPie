package me.moru3.mipie.Menu;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GuiManager {
    private static final ContentsMap<Player, ContentsMap<ItemStack, GuiItem>> actions = new ContentsMap<>();
    private static final ContentsMap<UUID, ContentsList<Gui>> guiList = new ContentsMap<>();

    public void onClick(InventoryClickEvent event) {
        if(actions.size()==0) { return; }
        Player player = (Player) event.getWhoClicked();
        if(player==null) { return; }
        GuiItem guiItem = actions.get(player).get(event.getCurrentItem());
        if(guiItem==null) { return; }
        if(guiItem.isAllowGet()) { event.setCancelled(true); }
        guiItem.runAction(event);
    }

    public void addGui(Gui gui) {
        ContentsList<Gui> temp = new ContentsList<>();
        temp.addAll(guiList.get(gui.getID()));
        temp.add(gui);
        guiList.put(gui.getID(), temp);
    }
}
