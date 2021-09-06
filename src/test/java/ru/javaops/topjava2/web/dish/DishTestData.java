package ru.javaops.topjava2.web.dish;

import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.web.AbstractTestData;
import ru.javaops.topjava2.web.MatcherFactory;

import java.util.List;

public class DishTestData extends AbstractTestData {

    public static final MatcherFactory.Matcher<Dish> MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Dish.class,  "restaurant");

    public static final Dish dish1 = new Dish(DISH1_ID, "burger 1", 100, null);
    public static final Dish dish2 = new Dish(DISH1_ID + 1, "burger 2", 200, null);
    public static final Dish dish3 = new Dish(DISH1_ID + 2, "burger 3", 300, null);
    public static final Dish dish4 = new Dish(DISH1_ID + 3, "pho bo 1", 400, null);
    public static final Dish dish5 = new Dish(DISH1_ID + 4, "pho bo 2", 500, null);
    public static final Dish dish6 = new Dish(DISH1_ID + 5, "pho bo 3", 600, null);
    public static final Dish dish7 = new Dish(DISH1_ID + 6, "chicken 1", 700, null);
    public static final Dish dish8 = new Dish(DISH1_ID + 7, "chicken 2", 800, null);
    public static final Dish dish9 = new Dish(DISH1_ID + 8, "chicken 3", 900, null);

    public static final List<Dish> dishesMenu1 = List.of(dish1, dish2, dish3);
    public static final List<Dish> dishesMenu2 = List.of(dish4, dish5, dish6);

    public static final List<Dish> allDishesOfRestaurant1 = List.of(dish1, dish2, dish3);

    public static Dish getNew() {
        return new Dish(null, "New Dish", 1000, null);
    }
    public static Dish getUpdated(){return new Dish(DISH1_ID, "Updated Dish", 1000, null);}
}
