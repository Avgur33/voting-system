package ru.javaops.topjava2.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.hibernate.validator.constraints.Range;
import ru.javaops.topjava2.HasId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantTo extends NamedTo implements HasId {

    @NotBlank
    @Size(min = 5,max = 100)
    String location;

    @NotNull
    @Range(min = 0, max = Integer.MAX_VALUE)
    Integer votes;

    Map<LocalDate,Long> votesHistory;

    public RestaurantTo(Integer id, String name, String location, Integer votes, Map<LocalDate,Long> votesHistory) {
        super(id, name);
        this.location = location;
        this.votes = votes;
        this.votesHistory = votesHistory;
    }

}
