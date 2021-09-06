package ru.javaops.topjava2.util;

import lombok.experimental.UtilityClass;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.to.RestaurantTo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class RestaurantUtil {
    public static List<RestaurantTo> getTos(Collection<Vote> votes) {
        Map<Restaurant, Long> map = votes.stream()
                .collect(Collectors.groupingBy(Vote::getRestaurant, Collectors.counting()));

        return map.entrySet().stream()
                .map(e -> createTo(e.getKey(), e.getValue().intValue(), Map.of(LocalDate.now(), e.getValue())))
                .toList();
    }
    //https://habr.com/ru/post/348536/
    public static List<RestaurantTo> getHistoryTos(Collection<Vote> votes) {
        Map<Restaurant, Map<LocalDate, Long>> history = votes.stream()
                .collect(Collectors.groupingBy(Vote::getRestaurant,
                         Collectors.groupingBy(Vote::getRegDate,
                         Collectors.counting())));

        return history
                .entrySet().stream()
                .map(e->createTo(e.getKey(),e.getValue().get(LocalDate.now()).intValue(),e.getValue()))
                .toList();
    }

    public static RestaurantTo createTo(Restaurant rest, int votes, Map<LocalDate, Long> history) {
        return new RestaurantTo(rest.getId(), rest.getName(), rest.getLocation(), votes, history);
    }
}