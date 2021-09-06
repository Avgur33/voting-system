package ru.javaops.topjava2.web.menu;

import ru.javaops.topjava2.model.Menu;
import ru.javaops.topjava2.to.MenuTo;
import ru.javaops.topjava2.web.AbstractTestData;
import ru.javaops.topjava2.web.MatcherFactory;

import java.time.LocalDate;
import java.util.List;

import static ru.javaops.topjava2.util.MenuUtil.getTos;
import static ru.javaops.topjava2.web.dish.DishTestData.*;
import static ru.javaops.topjava2.web.restaurant.RestaurantTestData.*;


public class MenuTestData extends AbstractTestData {

    public static final MatcherFactory.Matcher<Menu> MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Menu.class,  "restaurant","dishes");

    public static final MatcherFactory.Matcher<MenuTo> MATCHER_MENU_TO =
            MatcherFactory.usingEqualsComparator(MenuTo.class);

    //public static final Menu menu1 = new Menu(MENU1_ID, LocalDate.now().minusDays(1), rest1, dishesMenu1);
    public static final Menu menu2 = new Menu(MENU1_ID + 1, LocalDate.now().minusDays(1), rest2, dishesMenu2);
    //public static final Menu menu3 = new Menu(MENU1_ID + 2, LocalDate.now().minusDays(1), rest3, dishesMenu3);
    public static final Menu menu4 = new Menu(MENU1_ID + 3, LocalDate.now(), rest1, dishesMenu1);
    public static final Menu menu5 = new Menu(MENU1_ID + 4, LocalDate.now(), rest2, dishesMenu2);

    public static final List<Menu> allMenusForToday= List.of(menu4,menu5);
    public static final List<Menu> allMenusOfRestaurant2 = List.of(menu2,menu5);

    public static final List<MenuTo> allMenuTosForToday = getTos(allMenusForToday);

}
