package ru.javaops.topjava2.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import ru.javaops.topjava2.HasId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VoteTo  extends BaseTo implements HasId {

    @NotNull
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    LocalDate regDate;

    @NotNull
    @DateTimeFormat(iso=DateTimeFormat.ISO.TIME)
    LocalTime regTime;

    @NotBlank
    @Size(min = 2, max = 100)
    String restaurantName;

    @NotBlank
    @Size(min = 2, max = 100)
    String restaurantLocation;

    public VoteTo(Integer id, LocalDate regDate, LocalTime regTime, String restaurantName, String restaurantLocation) {
        super(id);
        this.regDate = regDate;
        this.regTime = regTime;
        this.restaurantName = restaurantName;
        this.restaurantLocation = restaurantLocation;
    }
}
