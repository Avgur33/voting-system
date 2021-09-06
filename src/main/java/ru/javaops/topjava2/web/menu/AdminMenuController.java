package ru.javaops.topjava2.web.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava2.error.NotFoundException;
import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.model.Menu;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.repository.DishRepository;
import ru.javaops.topjava2.repository.MenuRepository;
import ru.javaops.topjava2.repository.RestaurantRepository;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.javaops.topjava2.util.DateUtil.endDateUtil;
import static ru.javaops.topjava2.util.DateUtil.startDateUtil;
import static ru.javaops.topjava2.util.validation.ValidationUtil.assureIdConsistent;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkCurrentDate;

@RestController
@RequestMapping(value = AdminMenuController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Validated
@Tag(name = "AdminMenuController")
public class AdminMenuController {
    public final static String REST_URL = "/api/admin/restaurants/{restaurantId}/menu";

    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;

    @Operation(
            summary = "Create menu for the restaurant",
            description = "Number of dishes must be between 2 and 5",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 3 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "3")}),
                            required = true),
                    @Parameter(name = "forDate",
                            description = "For date. Format yyyy-MM-dd.",
                            content = @Content(examples = {@ExampleObject(value = "2022-02-21")}))
            }
    )
    @PostMapping()
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Menu> creatWithLocation(@PathVariable int restaurantId,
                                                  @RequestParam @Nullable
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forDate,
                                                  @RequestParam List<Integer> dishes) {
        log.info("create menu for the Restaurant id = {}", restaurantId);
        if ((dishes.size() < 2) || (dishes.size() > 5)) {
            throw new NotFoundException("Wrong dishes number");
        }

        if (forDate != null) {
            checkCurrentDate(forDate);
        }

        Restaurant rest = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant with id= " + restaurantId + " not found"));

        List<Dish> dishList = dishes.stream()
                .map(id -> dishRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException("Dish with id= " + id + " not found")))
                .toList();

        Menu menu = new Menu(null, forDate == null ? LocalDate.now() : forDate, rest, dishList);

        Menu created = menuRepository.save(menu);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(
            summary = "Delete menu for the restaurant",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 2 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "2")}),
                            required = true),
                    @Parameter(name = "id",
                            description = "The id of menu which needs to be deleted. Use 5 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "5")}),
                            required = true)
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = {"rootVote","userVote"},allEntries = true)
    public void delete(@PathVariable Integer restaurantId, @PathVariable Integer id) {

        log.info("Menu delete {}", id);
        Menu menu = menuRepository
                .findByIdWithRestaurant(id)
                .orElseThrow(() -> new NotFoundException(" Entity Menu with id = " + id + " not found"));
        assureIdConsistent(menu.getRestaurant(), restaurantId);
        menuRepository.deleteExisted(id);
    }

    @Operation(
            summary = "Get menu for the restaurant",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true),
                    @Parameter(name = "id",
                            description = "The id of menu which needs to be deleted. Use 4 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "4")}),
                            required = true)
            }
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Menu> get(@PathVariable Integer restaurantId, @PathVariable Integer id) {
        log.info("get Menu by ID for restaurant {}", restaurantId);
        Menu menu = menuRepository
                .findByIdWithRestaurant(id)
                .orElseThrow(() -> new NotFoundException(" Entity Menu with id = " + id + " not found"));
        assureIdConsistent(menu.getRestaurant(), restaurantId);
        return ResponseEntity.ok(menu);
    }

    @Operation(
            summary = "Get menu for the restaurant for today by restaurantId",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true)
            }
    )

    @GetMapping("/by")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Menu> getBy(@PathVariable Integer restaurantId) {
        log.info("get Menu by ID for restaurant {}", restaurantId);
        Menu menu = menuRepository
                .findByRestaurantId(restaurantId)
                .orElseThrow(() -> new NotFoundException(" Entity Menu for today not found"));
        return ResponseEntity.ok(menu);
    }

    @Operation(
            summary = "Get All menus for the restaurant",
            description = "You can use filter between Start date and End Date inclusive",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true),
                    @Parameter(name = "startDate",
                            description = "Start date. Format yyyy-MM-dd.",
                            content = @Content(examples = {@ExampleObject(value = "2020-02-21")})),
                    @Parameter(name = "endDate",
                            description = "End date. Format yyyy-MM-dd.",
                            content = @Content(examples = {@ExampleObject(value = "2022-02-21")}))
            }
    )
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Menu>> getAll(@PathVariable Integer restaurantId,
                                             @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("get Menu by ID for restaurant {}", restaurantId);
        List<Menu> menus = menuRepository.findAllByRestaurant(restaurantId, startDateUtil(startDate), endDateUtil(endDate));
        return ResponseEntity.ok(menus);
    }

    @Operation(
            summary = "Patch menu for the restaurant",
            description = "You can update menu only for today. For test use 1,2,3 ids for dishes." +
                    "Number of dishes must be between 2 and 5. Dish must be in db.",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true),
                    @Parameter(name = "id",
                            description = "The id of menu which needs to be updated. Use 4 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "4")}),
                            required = true)
            }
    )

    @PatchMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void update(@PathVariable int restaurantId, @PathVariable int id, @RequestParam List<Integer> dishes) {
        log.info("update Menu with id={}", id);
        if ((dishes.size() < 2) || (dishes.size() > 5)) {
            throw new NotFoundException("Wrong dishes number");
        }

        Menu menu = menuRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Menu with id=" + id + " not found"));

        checkCurrentDate(menu.getForDate());
        assureIdConsistent(menu.getRestaurant(), restaurantId);

        List<Dish> dishList = dishes.stream()
                .map(i -> dishRepository
                        .findById(i)
                        .orElseThrow(() -> new NotFoundException("Dish with id=" + i + " not found")))
                .toList();
        menu.setDishes(dishList);
    }
}
