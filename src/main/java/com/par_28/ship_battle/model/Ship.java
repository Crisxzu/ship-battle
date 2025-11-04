package com.par_28.ship_battle.model;
import java.util.List;
import com.par_28.ship_battle.model.enums.Direction;

abstract class Ship {
    protected Integer length;
    protected String name;
    protected Integer life;
    protected Direction direction;
    protected List<Coordinate> positions;

    Ship(String name, Integer length){
        this.name = name;
        this.length = length;
        this.life = 5;
        this.direction = direction;
        this.positions = positions;
    }
}