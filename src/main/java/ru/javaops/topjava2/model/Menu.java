package ru.javaops.topjava2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu", uniqueConstraints = {@UniqueConstraint(
        columnNames = {"for_date", "restaurant_id"}, name = "menu_unique_for_date_restaurant_id_idx")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

    @Column(name = "for_date", nullable = false, columnDefinition = "date default now()")
    @Schema(description = "For this date", example = "2020-02-20", format = "yyyy-MM-dd")
    @NotNull
    private LocalDate forDate;

    @JoinColumn(name = "restaurant_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference(value = "restaurant-menu")
    @ToString.Exclude
    @Hidden
    private Restaurant restaurant;

    //https://stackoverflow.com/questions/15155587/hibernate-bidirectional-manytomany-delete-issue
    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
    @OrderBy("price DESC")
    @JoinTable(name = "menu_dishes",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "dishes_id"))
    @ToString.Exclude
    //@Hidden
    private List<Dish> dishes = new ArrayList<>();

    public Menu(Integer id, LocalDate forDate, Restaurant restaurant, List<Dish> dishes) {
        super(id);
        this.forDate = forDate;
        this.restaurant = restaurant;
        this.dishes = dishes;
    }
}
