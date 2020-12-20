package me.moru3.mipie;

import me.moru3.marstools.ContentsList;
import me.moru3.marstools.ContentsMap;
import me.moru3.marstools.Pair;
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

    ContentsMap<MenuButton, Pair<Integer, Integer>> buttons = new ContentsMap<>();

    ContentsMap<ItemStack, GuiItem> actions = new ContentsMap<>();

    int max;
    int size;
    int now;

    GuiType guiType;

    Sound sound;

    /**
     * This is when creating a page menu.
     * @param startX startX 0 - 8
     * @param startY startY 0 - 5
     * @param endX endX 0 - 8
     * @param endY endY 0 - 5
     * @param name Replaced by using %page%.
     * @param rows line 0 - 5
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
        this.size = (endY-startY)*(endX+1-startX)-1;
    }

    /**
     * If it's only one page, this is it.
     * @param name Replaced by using %page%.
     * @param rows line 0 - 5
     * @param guiType GuiType
     */
    public GuiCreator(String name, int rows, GuiType guiType) {
        startX = 0;
        startY = 0;
        endX = 8;
        endY = rows;
        inventory = Bukkit.createInventory(null, rows*9, name);
        this.guiType = guiType;
        this.size = (endY-startY)*(endX+1-startX);
    }

    public GuiCreator setItem(ItemStack item, int x, int y) {
        inventory.setItem(y*9+x, item);
        return this;
    }

    public GuiCreator setItem(GuiItem item, int x, int y) {
        inventory.setItem(y*9+x, item.getItemStack());
        addActionItem(item);
        return this;
    }

    public GuiCreator setButton(MenuButton menuButton, int x, int y) {
        buttons.put(menuButton, new Pair<>(x, y));
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
        addActionItem(item);
        max = (int) Math.ceil((double) (items.size()-1)/size);
        items.add(item.getItemStack());
        return this;
    }

    public GuiCreator addItem(ItemStack item) {
        max = (int) Math.ceil((double) (items.size()-1)/size);
        items.add(item);
        return this;
    }



    private GuiCreator addItemToInv(ItemStack item) {
        int nowRow = (startY*9) + startX;
        int skip = startX+(8-endX);
        for(int i = 0;i<(endY-startY)*(endX+1-startX);i++) {
            if((((int) Math.ceil(nowRow/9.0))-1)*9+endX+1==nowRow) { nowRow+=skip; }
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
        for(int i = 0;i<(endY-startY)*(endX+1-startX);i++) {
            if((((int) Math.ceil(nowRow/9.0))-1)*9+endX+1==nowRow) { nowRow+=skip; }
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
        if(guiType==GuiType.ONE_MENU) { open(player); return; }
        Pair<Integer, Integer> button = buttons.get(MenuButton.BACK);
        if(page!=0) {
            this.setItem(new GuiItem(GuiManage.getBackItem()).addConsumer(this::back), button.first(), button.second());
        } else {
            this.setItem(new GuiItem(GuiManage.getNoBackItem()), button.first(), button.second());
        }
        Pair<Integer, Integer> button2 = buttons.get(MenuButton.NEXT);
        if(page<max) {
            this.setItem(new GuiItem(GuiManage.getNextItem()).addConsumer(this::next), button2.first(), button2.second());
        } else {
            this.setItem(new GuiItem(GuiManage.getNoNextItem()), button2.first(), button2.second());
        }
        GuiManage.addActionItem(player, actions);
        now = page;
        if(page<0||page>max) { return; }
        this.clear();
        items.slice((page-1)*size+page-1, (page-1)*size+size+page-1).forEach(this::addItemToInv);
        player.openInventory(inventory);
        if(sound!=null) { player.getWorld().playSound(player.getLocation(), sound, 1F, 1F); }
    }

    public void next(Player player) {
        open(player, now++);
        this.clear();
    }

    public void back(Player player) {
        open(player, now--);
    }

    public void open(Player player) {
        GuiManage.addActionItem(player, actions);
        if(guiType==GuiType.MULTIPLE_MENU) { open(player, 0); return; }
        player.openInventory(inventory);
        if(sound!=null) { player.getWorld().playSound(player.getLocation(), sound, 1F, 1F); }
    }

    public void addActionItem(GuiItem item) { actions.put(item.getItemStack(), item); }
}
