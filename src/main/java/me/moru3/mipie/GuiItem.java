package me.moru3.mipie;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GuiItem {
    ContentsMap<ActionType, Object> actions = new ContentsMap<>();
    ContentsList<BiConsumer<Player, GuiItem>> consumers = new ContentsList<>();
    GuiCreator movePage = null;

    private final ItemStack itemStack;

    public GuiItem(MarsItem marsItem) {
        this.itemStack = marsItem;
    }

    public GuiItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public GuiItem addMovePage(GuiCreator page) {
        movePage = page;
        return this;
    }

    public GuiItem addRunCommand(String cmd) {
        actions.put(ActionType.RUN_COMMAND, cmd.startsWith("/") ? cmd : "/" + cmd);
        return this;
    }

    public GuiItem addRuCommandConsole(String cmd) {
        actions.put(ActionType.RUN_COMMAND_CONSOLE, cmd.startsWith("/") ? cmd : "/" + cmd);
        return this;
    }

    public GuiItem addConsumer(BiConsumer<Player, GuiItem> consumer) {
        consumers.add(consumer);
        return this;
    }

    public void runAction(Player player, ItemStack item) {
        actions.forEach((type, v) -> {
            if (type==ActionType.RUN_COMMAND) {
                String cmd = (String) v;
                Bukkit.getServer().dispatchCommand(player, cmd.replace("%player%", player.getName()));
            } else if (type==ActionType.RUN_COMMAND_CONSOLE) {
                String cmd = (String) v;
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.replace("%player%", player.getName()));
            }
        });
        if(movePage!=null) { movePage.open(player); }
        consumers.forEach(consumer -> consumer.accept(player, GuiManage.actions.get(item)));
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
