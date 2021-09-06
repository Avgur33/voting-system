package ru.javaops.topjava2.web.restaurant;

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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava2.error.ErrorInfo;
import ru.javaops.topjava2.error.NotFoundException;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.repository.MenuRepository;
import ru.javaops.topjava2.repository.RestaurantRepository;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaops.topjava2.util.validation.ValidationUtil.assureIdConsistent;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Tag(name = "AdminRestaurantController")
@CacheConfig(cacheNames = "restaurants")
public class AdminRestaurantController {
    private final RestaurantRepository repository;
    private final MenuRepository menuRepository;
    public final static String REST_URL = "/api/admin/restaurants";

    @Operation(
            summary = "Get restaurant by restaurant id",
            parameters = {
                    @Parameter(name = "id",
                            description = "The id of restaurant that needs to be fetched. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "The restaurant",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Restaurant.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
                    @ApiResponse(responseCode = "423", description = "Locked",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(@PathVariable int id) {
        log.info("get {}", id);
        return ResponseEntity.ok(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entity Restaurant with id=" + id + " not found")));
    }

    @Operation(
            summary = "Delete restaurant",
            parameters = {
                    @Parameter(name = "id",
                            description = "The id of restaurant that needs to be deleted. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class)))
            })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(cacheNames = {"rootVote","userVote","restaurants","dishes"},allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("Restaurant delete {}", id);
        menuRepository.deleteByRestaurantId(id);
        repository.deleteExisted(id);
    }

    @Operation(
            summary = "Create restaurant",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Create the restaurant",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Restaurant.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class)))
            })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    //@CacheEvict(allEntries = true)
    @CacheEvict(cacheNames = {"rootVote","userVote","restaurants","dishes"},allEntries = true)
    public ResponseEntity<Restaurant> creatWithLocation(@RequestBody @Valid Restaurant rest) {
        log.info("create {}", rest);
        checkNew(rest);
        Restaurant created = repository.save(rest);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(
            summary = "Update restaurant",
            parameters = {
                    @Parameter(name = "id",
                            description = "The id of restaurant that needs to be deleted. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Update the restaurant",
                            content = @Content()),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class))),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorInfo.class)))
            })

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = {"rootVote","userVote","restaurants","dishes"},allEntries = true)
    public void update(@RequestBody @Valid Restaurant rest, @PathVariable int id) {
        log.info("update {} with id={}", rest, id);
        assureIdConsistent(rest, id);
        repository.save(rest);
    }
    //https://stackoverflow.com/questions/60002234/how-to-annotate-array-of-objects-response-in-swagger
    @Operation(
            summary = "Get all restaurants",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of restaurants",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Restaurant.class)))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
            })

    @GetMapping
    @Cacheable
    public List<Restaurant> getAll() {
        log.info("Restaurant getAll");
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
