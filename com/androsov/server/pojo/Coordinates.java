package com.androsov.server.pojo;

public class Coordinates {
    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    private final double x;
    private final Double y;

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
}
