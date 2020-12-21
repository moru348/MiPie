package me.moru3.mipie;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import me.moru3.marstools.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuCreator {
    Inventory base;

    int startX;
    int startY;
    int endX;
    int endY;

    ContentsList<ItemStack> contents = new ContentsList<>();

    ContentsList<Integer> contentSlot = new ContentsList<>();

    ContentsMap<MenuButton, Pair<Integer, Integer>> buttons = new ContentsMap<>();

    ContentsMap<ItemStack, GuiItem> actions = new ContentsMap<>();

    int size;
    int now;
    int rows;
    String title;

    Sound sound;

    MenuType guiType;

    /**
     * This is when creating a page menu.
     * @param startX startX 0 - 8
     * @param startY startY 0 - 5
     * @param endX endX 0 - 8
     * @param endY endY 0 - 5
     * @param title Replaced by using %page%.
     * @param rows line 0 - 5
     * @param guiType MenuType
     */
    public MenuCreator(int startX, int startY, int endX, int endY, String title, int rows, MenuType guiType) {
        if(rows<endY) { throw new IllegalArgumentException("There are not enough rows."); }
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.guiType = guiType;
        this.size = (endY-startY)*(endX+1-startX)-1;
        this.rows = rows;
        this.base = Bukkit.createInventory(null, rows, title);
        this.title = title;
        int nowSlot = (startY*9) + startX;
        int skip = startX+(8-endX);
        for(int i = 0;i< size+1;i++) {
            if((((int) Math.ceil(nowSlot/9.0))-1)*9+endX+1==nowSlot) { nowSlot+=skip; }
            contentSlot.add(nowSlot);
            nowSlot++;
        }
    }

    /**
     * If it's only one page, this is it.
     * @param title Replaced by using %page%.
     * @param rows line 0 - 5
     * @param guiType MenuType
     */
    public MenuCreator(String title, int rows, MenuType guiType) {
        this(0, 0, 8, rows, title, rows, guiType);
    }
    
    public MenuCreator setItem(GuiItem guiItem, int x, int y) {
        base.setItem(y*9+x, guiItem.getItemStack());
        addActionItem(guiItem);
        return this;
    }

    public MenuCreator setItem(GuiItem guiItem, int slot) {
        base.setItem(slot, guiItem.getItemStack());
        addActionItem(guiItem);
        return this;
    }

    public MenuCreator setItem(ItemStack itemStack, int x, int y) {
        base.setItem(y*9+x, itemStack);
        return this;
    }

    public MenuCreator setItem(ItemStack itemStack, int slot) {
        base.setItem(slot, itemStack);
        return this;
    }

    public MenuCreator addContents(GuiItem guiItem) {
        addActionItem(guiItem);
        contents.add(guiItem.getItemStack());
        return this;
    }

    private ContentsMap<Integer, ItemStack> getContents(int page) {
        return contentSlot.coalesce(contents.slice((page-1)*size+page-1, (page-1)*size+size+page-1));
    }

    private Inventory build() {
        Inventory result = Bukkit.createInventory(null, rows, title);
        result.setContents(base.getContents());
        return result;
    }

    private Inventory build(int page) {
        Inventory result = Bukkit.createInventory(null, rows, title);
        result.setContents(base.getContents());
        getContents(page).forEach(result::setItem);
        return result;
    }

    public void open(Player player) {
        if(guiType==MenuType.MULTIPLE_MENU) { open(player, 0); return; }
        MenuManage.addActionItem(player, actions);
        player.openInventory(build());
        if(sound!=null) { player.getWorld().playSound(player.getLocation(), sound, 1F, 1F); }
    }

    public void open(Player player, int page) {
        if(guiType==MenuType.ONE_MENU) { open(player); return; }
        MenuManage.addActionItem(player, actions);
        player.openInventory(build(page));
        if(sound!=null) { player.getWorld().playSound(player.getLocation(), sound, 1F, 1F); }
    }

    public void open(Player player, int page, Sound sound) {
        if(guiType==MenuType.ONE_MENU) { open(player); return; }
        this.sound = sound;
        MenuManage.addActionItem(player, actions);
        player.openInventory(build(page));
        if(sound!=null) { player.getWorld().playSound(player.getLocation(), sound, 1F, 1F); }
    }

    public void open(Player player, Sound sound) {
        if(guiType==MenuType.MULTIPLE_MENU) { open(player, 0, sound); return; }
        this.sound = sound;
        MenuManage.addActionItem(player, actions);
        player.openInventory(build());
        if(sound!=null) { player.getWorld().playSound(player.getLocation(), sound, 1F, 1F); }
    }

    public void next(Player player) {
        open(player, ++now);
    }

    public void back(Player player) {
        open(player, --now);
    }

    public void addActionItem(GuiItem item) { actions.put(item.getItemStack(), item); }
}
