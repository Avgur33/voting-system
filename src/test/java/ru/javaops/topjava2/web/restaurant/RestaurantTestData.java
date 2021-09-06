package ru.javaops.topjava2.web.restaurant;

import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.to.RestaurantTo;
import ru.javaops.topjava2.util.RestaurantUtil;
import ru.javaops.topjava2.web.AbstractTestData;
import ru.javaops.topjava2.web.MatcherFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RestaurantTestData extends AbstractTestData {

    public static final MatcherFactory.Matcher<Restaurant> MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "votes", "dishes");

    public static final MatcherFactory.Matcher<RestaurantTo> MATCHER_RESTAURANT_TO =
            MatcherFactory.usingEqualsComparator(RestaurantTo.class);

    public static final Restaurant rest1 = new Restaurant(REST1_ID, "Burger King", "Moscow");
    public static final Restaurant rest2 = new Restaurant(REST1_ID + 1, "Pho Bo", "Moscow");
    public static final Restaurant rest3 = new Restaurant(REST1_ID + 2, "KFC", "Moscow");

    public static final RestaurantTo restTo1 = RestaurantUtil.createTo(rest1, 1, Map.of(LocalDate.now(), 1L));
    public static final RestaurantTo restTo2 = RestaurantUtil.createTo(rest2, 1, Map.of(LocalDate.now(), 1L));

    public static final RestaurantTo restTo1History =
            RestaurantUtil.createTo(rest1, 1, Map.of(LocalDate.now().minusDays(1), 1L, LocalDate.now(), 1L));

    public static final RestaurantTo restTo2History =
            RestaurantUtil.createTo(rest2, 1, Map.of(LocalDate.now().minusDays(1), 1L, LocalDate.now(), 1L));

    public static final List<RestaurantTo> voteResult = List.of(restTo1, restTo2);
    public static final List<RestaurantTo> historyResult = List.of(restTo1History, restTo2History);

    public static final List<Restaurant> restaurants = List.of(rest1, rest2, rest3);

    public static Restaurant getNew() {
        return new Restaurant(null, "New Restaurant", "Moscow");
    }

    public static Restaurant getUpdated() {
        return new Restaurant(REST1_ID, "Updated Restaurant", "Moscow");
    }
}
