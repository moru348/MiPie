package me.moru3.mipie;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

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

    public void start(Entity entity) {
        Location location = start;
        for(double j = -(width/2.0);j<=width/2;j+=0.1) {
            location = new Location(start.getWorld(), start.getX()+(height/100)/(width/100),
                    start.getY()+(ph_const*(start.getY() + end.getY()) * j - ph_const*start.getY()*end.getY()),
                    start.getX()+(width/100)/(height/100));
            entity.teleport(location);
        }
    }
}
