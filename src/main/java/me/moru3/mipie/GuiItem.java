package me.moru3.mipie;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiItem {
    ContentsMap<ActionType, Object> actions = new ContentsMap<>();
    ContentsList<BiConsumer<Player, GuiItem>> biConsumers = new ContentsList<>();
    ContentsList<Supplier<Void>> suppliers = new ContentsList<>();
    ContentsList<Consumer<Player>> consumers = new ContentsList<>();

    private final ItemStack itemStack;

    public GuiItem(MarsItem marsItem) {
        this.itemStack = marsItem;
    }

    public GuiItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public GuiItem addRunCommand(String cmd) {
        actions.put(ActionType.RUN_COMMAND, cmd.startsWith("/") ? cmd : "/" + cmd);
        return this;
    }

    public GuiItem addRuCommandConsole(String cmd) {
        actions.put(ActionType.RUN_COMMAND_CONSOLE, cmd.startsWith("/") ? cmd : "/" + cmd);
        return this;
    }

    public GuiItem addBiConsumer(BiConsumer<Player, GuiItem> consumer) {
        biConsumers.add(consumer);
        return this;
    }

    public GuiItem addSupplier(Supplier<Void> supplier) {
        suppliers.add(supplier);
        return this;
    }

    public GuiItem addConsumer(Consumer<Player> consumer) {
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
        biConsumers.forEach(consumer -> consumer.accept(player, MenuManage.getActions().get(player).get(item)));
        suppliers.forEach(Supplier::get);
        consumers.forEach(consumer -> consumer.accept(player));
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
