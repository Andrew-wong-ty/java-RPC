package com.example.rpc.examples;

public class MyMathImpl implements MyMath {

    @Override
    public Integer Add(Integer x, Integer y) {
        return x+y;
    }

    @Override
    public Point AddPoint(Point x, Point y) {
        return new Point(x.getX()+y.getX(), x.getY()+y.getY());
    }
}