package ru.javaops.topjava2.to;


import lombok.Value;

import java.util.Map;

@Value
public class MenuTo {

    Integer id;
    String name;
    String location;
    Map<String,Integer> dishes;

    public MenuTo(Integer id, String name, String location, Map<String, Integer> dishes) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.dishes = dishes;
    }
}
