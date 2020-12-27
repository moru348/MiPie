package me.moru3.mipie.Menu;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GuiManager {
    private static final ContentsMap<Player, ContentsMap<ItemStack, GuiItem>> actions = new ContentsMap<>();
    public static final ContentsMap<UUID, ContentsList<Inventory>> guiList = new ContentsMap<>();
    public static final ContentsMap<Player, UUID> playerGuiList = new ContentsMap<>();

    public void onClick(InventoryClickEvent event) {
        if(actions.size()==0) { return; }
        Player player = (Player) event.getWhoClicked();
        if(player==null) { return; }
        GuiItem guiItem = actions.get(player).get(event.getCurrentItem());
        if(guiItem==null) { return; }
        if(guiItem.isAllowGet()) { event.setCancelled(true); }
        guiItem.runAction(event);
    }

    public static void addGui(Gui gui, Inventory inventory, Player player) {
        ContentsList<Inventory> temp = new ContentsList<>();
        temp.addAll(guiList.get(gui.getID()));
        temp.add(inventory);
        guiList.put(gui.getID(), temp);
        playerGuiList.put(player, gui.getID());
        ContentsMap<ItemStack, GuiItem> temp2 = new ContentsMap<>();
        gui.actions.forEach(temp2::put);
        actions.put(player, temp2);
    }
}
