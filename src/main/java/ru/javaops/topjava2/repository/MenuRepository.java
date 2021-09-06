package ru.javaops.topjava2.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava2.model.Menu;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MenuRepository  extends BaseRepository<Menu>{

    @EntityGraph(attributePaths = {"restaurant"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT m FROM Menu m WHERE m.id=:id")
    Optional<Menu> findByIdWithRestaurant(Integer id);

    @EntityGraph(attributePaths = {"restaurant"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT m FROM Menu m WHERE m.forDate=current_date")
    List<Menu> getToday(Pageable pageable);

    @Query("SELECT m FROM Menu m WHERE m.restaurant.id=:restaurantId AND m.forDate >=:startDate AND m.forDate <=:endDate")
    List<Menu> findAllByRestaurant(Integer restaurantId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT m FROM Menu m WHERE m.restaurant.id=:restaurantId AND m.forDate=current_date")
    Optional<Menu> findByRestaurantId(Integer restaurantId);

    @Modifying
    @Query("DELETE FROM Menu m WHERE m.restaurant.id =:id")
    void deleteByRestaurantId(int id);
}
