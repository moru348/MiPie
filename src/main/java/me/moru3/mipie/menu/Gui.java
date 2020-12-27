package me.moru3.mipie.Menu;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import me.moru3.marstools.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Gui {
    UUID id;
    Inventory inventory;

    int startX;
    int startY;
    int endX;
    int endY;

    ContentsList<ItemStack> contents = new ContentsList<>();
    ContentsList<Pair<GuiButton, Pair<Integer, Integer>>> buttons = new ContentsList<>();
    ContentsList<Integer> contentSlot = new ContentsList<>();
    ContentsMap<ItemStack, GuiItem> actions = new ContentsMap<>();

    int size;
    int now;
    int rows;
    String title;

    AsyncType asyncType;

    Sound sound;

    /**
     * This is when creating a page menu.
     * @param startX startX 0 - 8
     * @param startY startY 0 - 5
     * @param endX endX 0 - 8
     * @param endY endY 0 - 5
     * @param title Replaced by using %page%.
     * @param rows line 0 - 5
     */
    public Gui(int startX, int startY, int endX, int endY, String title, int rows, AsyncType asyncType) {
        id = UUID.randomUUID();
        if(rows<endY) { throw new IllegalArgumentException("There are not enough rows."); }
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.size = (endY-startY)*(endX+1-startX)-1;
        this.rows = rows;
        this.inventory = Bukkit.createInventory(null, rows*9, title);
        this.title = title;
        int nowSlot = (startY*9) + startX;
        int skip = startX+(8-endX);
        this.asyncType = asyncType;
        for(int i = 0;i< size+1;i++) {
            if((((int) Math.ceil(nowSlot/9.0))-1)*9+endX+1==nowSlot) { nowSlot+=skip; }
            contentSlot.add(nowSlot);
            nowSlot++;
        }
    }

    public UUID getID() { return id; }

    public Gui addContents(GuiItem guiItem) {
        contents.add(guiItem.getItemStack());
        return this;
    }

    private ContentsMap<Integer, ItemStack> getContents(int page) {
        return contentSlot.coalesce(contents.slice((page-1)*size+page-1, (page-1)*size+size+page-1));
    }

    public Gui setButton(GuiButton menuButton, int x, int y) {
        Pair<GuiButton, Pair<Integer, Integer>> temp = new Pair<>(menuButton, new Pair<>(x, y));
        buttons.add(temp);
        return this;
    }

    public void clear() {
        contentSlot.forEach(i -> inventory.setItem(i, null));
    }

    public Inventory getInventory() { return inventory; }

    public Gui placeContents(int page) {
        int max = (int) Math.ceil((double) (contents.size()-1)/size);
        clear();
        Inventory result = Bukkit.createInventory(null, rows*9, title.replace("%page%", String.valueOf(page)));
        result.setContents(inventory.getContents());
        getContents(page).forEach(result::setItem);
        inventory = result;
        return this;
    }
}
