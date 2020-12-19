package me.moru3.mipie;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicReference;

public class Parabola {
    double height;
    double width;
    Location start;
    Location end;
    double ph_const;

    public Parabola(double height, Location start, Location end) {
        this.height = height;
        this.width = start.distance(end);
        this.start = start;
        this.end = end;
        ph_const = height/Math.pow(width/2, 2);
    }

    public void start(Entity entity, Plugin plugin) {
        AtomicReference<Location> location = new AtomicReference<>(start);
        AtomicReference<Double> j = new AtomicReference<>(-(width/2.0));
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if(j.get()<=width/2) { return; }
            location.set(new Location(start.getWorld(), start.getX() + (height / 100) / (width / 100),
                    start.getY() + (ph_const * (start.getY() + end.getY()) * j.get() - ph_const * start.getY() * end.getY()),
                    start.getX() + (width / 100) / (height / 100)));
            entity.teleport(location.get());
            j.updateAndGet(i -> i+=0.1);
        }, 0, 1);
    }
}
