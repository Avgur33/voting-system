package ru.javaops.topjava2.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava2.model.Vote;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote>{

    @EntityGraph(attributePaths = {"restaurant"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT v FROM Vote v WHERE v.regDate=current_date")
    List<Vote> getResult();

    @EntityGraph(attributePaths = {"restaurant"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT v FROM Vote v WHERE v.regDate >=:startDate AND v.regDate <=:endDate")
    List<Vote> getResultHistory(LocalDate startDate, LocalDate endDate);

    @EntityGraph(attributePaths = {"restaurant"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT v FROM Vote v WHERE v.userId=:id AND v.regDate=current_date")
    Optional<Vote> findByUserId(int id);

    @EntityGraph(attributePaths = {"restaurant"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT v FROM Vote v WHERE v.userId=:id AND v.regDate >=:startDate AND v.regDate <=:endDate")
    List<Vote> findAllByUserIdFilter(int id, LocalDate startDate, LocalDate endDate);

    //https://www.baeldung.com/jpa-queries-custom-result-with-aggregation-functions
    /*@Query("SELECT new ru.javaops.topjava2.model.RestaurantC(v.restaurant.id,v.restaurant.name,v.restaurant.location, count(v)) FROM Vote v JOIN v.restaurant WHERE v.regDate=current_date GROUP BY v.restaurant.id")
    List<RestaurantTo> getResultVotes();*/


}
