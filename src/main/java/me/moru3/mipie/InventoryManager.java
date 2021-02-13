package me.moru3.mipie;

import me.moru3.marstools.ContentsList;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryManager {
    public static Boolean delItemInventory(Inventory inv, ItemStack item, int amo) {
        item.setAmount(1);
        AtomicReference<Integer> amount = new AtomicReference<>(0);
        ContentsList<ItemStack> items = new ContentsList<>();
        Arrays.stream(inv.getContents()).filter(Objects::nonNull).forEach(invItem -> {
            ItemStack tempItem = invItem.clone();
            invItem.setAmount(1);
            if(tempItem!=item) { return; }
            items.add(invItem);
            amount.updateAndGet(v -> v + invItem.getAmount());
        });
        if(amount.get()< amo) return false;
        for(ItemStack i : items) {
            if(i.getAmount()==amo) {
                i.setAmount(1);
                return true;
            } else if (i.getAmount()>amo) {
                i.setAmount(i.getAmount()-amo);
                return true;
            } else {
                amo = amo - i.getAmount();
                i.setAmount(1);
            }
        }
        return false;
    }

    public static Boolean hasItem(Inventory inv, ItemStack item, int amo) {
        item.setAmount(1);
        AtomicReference<Integer> amount = new AtomicReference<>(0);
        Arrays.stream(inv.getContents()).filter(Objects::nonNull).forEach(invItem -> {
            ItemStack tempItem = invItem.clone();
            tempItem.setAmount(1);
            if(tempItem!=item) { return; }
            amount.updateAndGet(v -> v + invItem.getAmount());
        });
        return amount.get() >= amo;
    }

    public static Boolean hasInventoryArea(Inventory inv, ItemStack item, int amo) {
        item.setAmount(1);
        AtomicReference<Integer> amount = new AtomicReference<>(0);
        Arrays.stream(inv.getContents()).forEach(invItem -> {
            if (invItem == null) {
                amount.updateAndGet(v -> v + item.getMaxStackSize());
                return;
            }
            ItemStack tempItem = invItem.clone();
            invItem.setAmount(1);
            if (tempItem != item) {
                return;
            }
            amount.updateAndGet(v -> v + invItem.getMaxStackSize() - invItem.getAmount());
        });
        return amount.get() >= amo;
    }

    public static Boolean addItemInventory(Inventory inv, ItemStack item, int amo) {
        item.setAmount(1);
        AtomicReference<Integer> amount = new AtomicReference<>(0);
        ContentsList<ItemStack> items = new ContentsList<>();
        Arrays.stream(inv.getContents()).forEach(invItem -> {
            if(invItem==null) {
                amount.updateAndGet(v -> v + item.getMaxStackSize());
                return;
            }
            ItemStack tempItem = invItem.clone();
            invItem.setAmount(1);
            if(tempItem!=item) { return; }
            amount.updateAndGet(v -> v + invItem.getMaxStackSize()-invItem.getAmount());
            items.add(invItem);
        });
        if(amount.get()<amo) { return false; }
        for(ItemStack i:items.stream().filter(v -> v.getAmount() !=v.getMaxStackSize()).toArray(ItemStack[]::new)) {
            if(i.getMaxStackSize()-i.getAmount()==amo) {
                i.setAmount(i.getMaxStackSize());
                return true;
            } else if (i.getMaxStackSize()-i.getAmount()>amo) {
                i.setAmount(i.getAmount()+amo);
                return true;
            } else {
                amo -= i.getMaxStackSize()-i.getAmount();
                i.setAmount(i.getMaxStackSize());
            }
        }
        while(true) {
            if(amo==item.getMaxStackSize()) {
                item.setAmount(item.getMaxStackSize());
                inv.addItem(item);
                return true;
            } else if(amo<item.getMaxStackSize()) {
                item.setAmount(amo);
                inv.addItem(item);
                return true;
            } else {
                item.setAmount(item.getMaxStackSize());
                inv.addItem(item);
                amo -= item.getMaxStackSize();
            }
        }
    }
    public static void dropItem(Location location, ItemStack item, int amo) {
        while(true) {
            if(amo>item.getMaxStackSize()) {
                item.setAmount(item.getMaxStackSize());
                location.getWorld().dropItemNaturally(location, item).setPickupDelay(0);
            } else {
                item.setAmount(amo);
                location.getWorld().dropItemNaturally(location, item).setPickupDelay(0);
                return;
            }
        }
    }
}
