package me.moru3.mipie;

public class Parabola {
    double height = 0.0;
    double width = 0.0;
    double start = 0.0;
    double end = 0.0;
    double ph_const = 0.0;

    public Parabola(double height, double width, double start, double end) {
        this.height = height;
        this.width = width;
        this.start = start;
        this.end = end;
        ph_const = height/Math.pow(width/2, 2);
    }

    public double getByX(int x) {
        return ph_const*(start + end)*x - ph_const*start*end;
    }
}
