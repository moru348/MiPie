package me.moru3.mipie;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiCreator {

    Inventory inventory;

    int startX;
    int startY;
    int endX;
    int endY;

    ContentsList<ItemStack> items = new ContentsList<>();

    GuiType guiType;

    Sound sound;

    /**
     * ページメニューを作るときはこっち。
     * @param startX startX 0 - 8
     * @param startY startY 0 - 5
     * @param endX endX 0 - 8
     * @param endY endY 0 - 5
     * @param name %page%を使用すると置き換えられます。
     * @param rows 行。 0 - 5
     * @param guiType GuiType
     */
    public GuiCreator(int startX, int startY, int endX, int endY, String name, int rows, GuiType guiType) {
        if(rows<endY) { throw new IllegalArgumentException("There are not enough rows."); }
        inventory = Bukkit.createInventory(null, rows*9, name);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.guiType = guiType;
    }

    /**
     * ワンページのみだったらこっち。
     * @param name %page%を使用すると置き換えられます。
     * @param rows 行。 0 - 5
     * @param guiType GuiType
     */
    public GuiCreator(String name, int rows, GuiType guiType) {
        startX = 0;
        startY = 0;
        endX = 8;
        endY = rows;
        inventory = Bukkit.createInventory(null, rows*9, name);
        this.guiType = guiType;
    }

    public GuiCreator setItem(ItemStack item, int x, int y) {
        inventory.setItem(y*9+x, item);
        return this;
    }

    public GuiCreator setItem(GuiItem item, int x, int y) {
        inventory.setItem(y*9+x, item.getItemStack());
        GuiManage.addActionItem(item);
        return this;
    }

    public GuiCreator addItem(ItemStack item, int x, int y) {
        int nowRow = startY*9 + startX;
        int skip = startX+(8-endX);
        for(int i = 0;i<(endY-startY)*(endX-startX);i++) {
            if(nowRow%endX==0) { nowRow += skip; }
            if(inventory.getItem(nowRow)==null) {
                inventory.setItem(nowRow, item);
            }
            nowRow++;
        }
        return this;
    }

    public GuiCreator addItem(GuiItem item) {
        GuiManage.addActionItem(item);
        items.add(item.getItemStack());
        return this;
    }

    public GuiCreator addItem(ItemStack item) {
        items.add(item);
        return this;
    }

    private GuiCreator addItemToInv(ItemStack item) {
        int nowRow = (startY*9) + startX;
        int skip = startX+(8-endX);
        for(int i = 0;i<(endY-startY)*(endX-startX);i++) {
            if(((int) Math.ceil(nowRow/9.0))*9+endX==nowRow) { nowRow+=skip; }
            if(inventory.getItem(nowRow)==null) {
                inventory.setItem(nowRow, item);
                return this;
            }
            nowRow++;
        }
        return this;
    }

    public GuiCreator clear() {
        int nowRow = (startY*9) + startX;
        int skip = startX+(8-endX);
        for(int i = 0;i<(endY-startY)*(endX-startX);i++) {
            if(((int) Math.ceil(nowRow/9.0))*9+endX==nowRow) { nowRow+=skip; }
            inventory.setItem(nowRow, null);
            nowRow++;
        }
        return this;
    }

    public GuiCreator setOpenSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public void open(Player player, int page) {
        int size = (endY-startY)*(endX-startX);
        int max = (int) Math.ceil((double) (items.size()-1)/size);
        if(page<0||page>max) { return; }
        this.clear();
        items.slice(page*size, (page*size-1)+size-1).forEach(this::addItemToInv);
        player.openInventory(inventory);
        if(sound!=null) { player.getWorld().playSound(player.getLocation(), sound, 1F, 1F); }
    }

    public void open(Player player) {
        player.openInventory(inventory);
        if(sound!=null) { player.getWorld().playSound(player.getLocation(), sound, 1F, 1F); }
    }
}
