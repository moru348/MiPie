package me.moru3.mipie.Menu;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import me.moru3.marstools.Pair;
import me.moru3.mipie.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class GuiManager implements Listener {
    public static final ContentsMap<Player, ContentsMap<ItemStack, GuiItem>> actions = new ContentsMap<>();
    public static final ContentsMap<UUID, Gui> guiList = new ContentsMap<>();
    public static final ContentsMap<Player, Pair<UUID, Inventory>> playerGuiList = new ContentsMap<>();
    public static final ContentsList<Player> notClose = new ContentsList<>();

    public static ItemStack next;
    public static ItemStack back;
    public static ItemStack last;
    public static ItemStack first;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getView().getTopInventory()==null) { return; }
        Player player = (Player) event.getWhoClicked();
        if(playerGuiList.get(player)==null) { return; }
        // Gui gui = guiList.get(playerGuiList.get(player).first());
        if(actions.size()!=0&&actions.get(player).containsKey(event.getCurrentItem())) {
            GuiItem guiItem = actions.get(player).get(event.getCurrentItem());
            if(guiItem==null) { return; }
            if(!guiItem.isAllowGet()) { event.setCancelled(true); }
            if(guiItem.listGui!=null) {
                guiItem.listGui.open(player, guiItem.page);
            } else if(guiItem.gui!=null) {
                guiItem.gui.open(player);
            }
            guiItem.runAction(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if(notClose.contains(player)) {
            notClose.remove(player);
            return;
        }
        AtomicBoolean remove = new AtomicBoolean(true);
        if(playerGuiList.get(player)==null) { return; }
        UUID uuid = playerGuiList.get(player).first();
        playerGuiList.remove(player);
        playerGuiList.forEach((plyr, pair) -> { if(pair.first()==uuid) { remove.set(false); } });
        if(remove.get()) { guiList.remove(uuid); }
        actions.remove(player);
    }

    public GuiManager() {
        next = Util.generateItem(Material.ARROW, ChatColor.GREEN + "Next", Collections.singletonList(ChatColor.GRAY + "Go to the next page"));
        back = Util.generateItem(Material.ARROW, ChatColor.GREEN + "Back", Collections.singletonList(ChatColor.GRAY + "Return to the previous page"));
        last = Util.generateItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Last page", Collections.singletonList(ChatColor.GRAY + "This page is the last."));
        first = Util.generateItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "First page", Collections.singletonList(ChatColor.GRAY + "This page is the first."));
    }

    public static void addGui(Gui gui, Inventory inventory, Player player) {
        guiList.put(gui.getID(), gui);
        playerGuiList.put(player, new Pair<>(gui.getID(), inventory));
        ContentsMap<ItemStack, GuiItem> temp2 = new ContentsMap<>();
        gui.actions.forEach(temp2::put);
        actions.put(player, temp2);
    }
}
