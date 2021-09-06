package ru.javaops.topjava2.web.dish;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava2.error.ErrorInfo;
import ru.javaops.topjava2.error.NotFoundException;
import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.repository.DishRepository;
import ru.javaops.topjava2.repository.RestaurantRepository;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaops.topjava2.util.validation.ValidationUtil.assureIdConsistent;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = AdminDishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "dishes")
@Tag(name = "AdminDishController")
public class AdminDishController {
    private final DishRepository repository;
    private final RestaurantRepository restaurantRepository;
    public final static String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";

    @Operation(
            summary = "Delete dish with ID",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true),
                    @Parameter(name = "id",
                            description = "The id of dish that needs to be deleted. Use 10 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "10")}),
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "The dish was deleted", content = @Content()),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content()),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class)))
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(key="#restaurantId")
    public void delete(@PathVariable Integer restaurantId, @PathVariable Integer id) {

        log.info("Delete dish with id = {}", id);
        Dish dish = getDishById(id);
        assureIdConsistent(dish.getRestaurant(), restaurantId);
        repository.deleteExisted(id);
    }

    @Operation(
            summary = "Get dishes for restaurant",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "The dishes",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Dish.class))))
            }
    )
    @GetMapping()
    @Cacheable(key = "#restaurantId")
    public List<Dish> getAll(@PathVariable Integer restaurantId) {
        log.info("get dishes for restaurant with id = {}", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)){
            throw new NotFoundException("Entity Restaurant with id = " + restaurantId + " not found");
        }
        return repository.getDishesByRestaurantId(restaurantId);
    }

    @Operation(
            summary = "Get dish for the restaurant(restaurantId) by id",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true),
                    @Parameter(name = "id",
                            description = "The id of dish that needs to be fetched. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dish for the restaurant",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Dish.class)))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Dish> get(@PathVariable  Integer restaurantId, @PathVariable Integer id) {
        log.info("get dish by id = {} for restaurant id = {}",id, restaurantId);
        Dish dish = getDishById(id);
        assureIdConsistent(dish.getRestaurant(), restaurantId);
        return ResponseEntity.ok(dish);
    }

    @Operation(
            summary = "Create dish for restaurant",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created dish for the restaurant",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Dish.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
                    @ApiResponse(responseCode = "423", description = "Locked",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class)))
            }
    )

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @CacheEvict(key="#restaurantId")
    public ResponseEntity<Dish> creatWithLocation(@PathVariable  Integer restaurantId, @Valid @RequestBody Dish dish) {
        log.info("create {}", dish);
        checkNew(dish);
        dish.setRestaurant(getRestaurantById(restaurantId));
        Dish created = repository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(
            summary = "Update dish",
            parameters = {
                    @Parameter(name = "restaurantId",
                            description = "The id of restaurant. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true),
                    @Parameter(name = "id",
                            description = "The id of dish that needs to be updated. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Created dish for the restaurant",
                            content = @Content()),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
            }
    )

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(key="#restaurantId")
    public void update(@PathVariable Integer restaurantId, @PathVariable Integer id, @Valid @RequestBody Dish dish) {
        log.info("update {} with id={}", dish, id);
        assureIdConsistent(dish, id);
        Dish updated = getDishById(id);
        assureIdConsistent(updated.getRestaurant(), restaurantId);
        updated.setName(dish.getName());
        updated.setPrice(dish.getPrice());
    }

    private Dish getDishById(Integer id){
        return repository
                .findByIdWithRestaurant(id)
                .orElseThrow(() -> new NotFoundException("Entity Dish with id = " + id + " not found"));
    }

    private Restaurant getRestaurantById(Integer id){
        return restaurantRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Entity Restaurant with id = " + id + " not found"));
    }

}
