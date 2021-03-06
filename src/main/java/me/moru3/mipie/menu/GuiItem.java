package me.moru3.mipie.menu;

import me.moru3.marstools.ContentsList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class GuiItem {
    private boolean allowGet = false;
    ContentsList<Consumer<InventoryClickEvent>> consumers = new ContentsList<>();
    ItemStack itemStack;
    Gui gui;
    Gui listGui;
    int page;

    public GuiItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public GuiItem addConsumer(Consumer<InventoryClickEvent> consumer) {
        consumers.add(consumer);
        return this;
    }

    public GuiItem open(Gui gui) {
        this.gui = gui;
        return this;
    }

    public GuiItem open(Gui gui, int page) {
        this.page = page;
        this.listGui = gui;
        return this;
    }

    public GuiItem setAllowGet(boolean b) {
        allowGet = b;
        return this;
    }

    public ItemStack getItemStack() { return itemStack; }

    public boolean isAllowGet() { return allowGet; }

    public void runAction(InventoryClickEvent event) {
        consumers.forEach(consumer -> consumer.accept(event));
    }
}
