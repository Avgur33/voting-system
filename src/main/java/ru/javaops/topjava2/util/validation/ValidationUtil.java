package ru.javaops.topjava2.util.validation;

import lombok.experimental.UtilityClass;
import ru.javaops.topjava2.HasId;
import ru.javaops.topjava2.error.IllegalRequestDataException;
import ru.javaops.topjava2.error.LateTimeException;
import ru.javaops.topjava2.error.NotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;

@UtilityClass
public class ValidationUtil {

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must be new (id=null)");
        }
    }

    //  Conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
    public static void assureIdConsistent(HasId bean, int id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must has id=" + id);
        }
    }

    public static void checkModification(int count, int id) {
        if (count == 0) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
    }

    public static void checkCurrentTime(LocalTime time){
        if (LocalTime.now().isAfter(time)) {
            throw new LateTimeException("Voting end at " + time + " o'clock");
        }
    }

    public static void checkCurrentDate(LocalDate ld){
        if (LocalDate.now().isAfter(ld)) {
            throw new LateTimeException("You can't change old menu");
        }
    }
}