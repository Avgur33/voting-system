package ru.javaops.topjava2.web.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import ru.javaops.topjava2.model.User;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaops.topjava2.util.validation.ValidationUtil.assureIdConsistent;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = AdminUserController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "AdminUserController", description = "Controller for manipulating with User. Only for Admin.")
@CacheConfig(cacheNames = "users")
public class AdminUserController extends AbstractUserController {

    static final String REST_URL = "/api/admin/users";

    @Operation(
            summary = "Get user by id",
            parameters = {
                    @Parameter(name = "id",
                            description = "The id of User that needs to be fetched. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true)
            }
    )
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable int id) {
        return super.get(id);
    }

    @Operation(
            summary = "Delete user by id",
            parameters = {
            @Parameter(name = "id",
                    description = "The id of User that needs to be deleted. Use 1 for testing.",
                    content = @Content(examples = {@ExampleObject(value = "1")}),
                    required = true)
    }
    )
    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @Operation(
            summary = "Get all users",
            description = "Sort.Direction.ASC by name and email"
    )
    @GetMapping
    @Cacheable
    public List<User> getAll() {
        log.info("getAll");
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name", "email"));
    }

    @Operation(
            summary = "Create user"
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    public ResponseEntity<User> createWithLocation(@Valid @RequestBody User user) {
        log.info("create {}", user);
        checkNew(user);
        User created = prepareAndSave(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(
            summary = "Update user",
            parameters = {
            @Parameter(name = "id",
                    description = "The id of User that needs to be updated. Use 1 for testing.",
                    content = @Content(examples = {@ExampleObject(value = "1")}),
                    required = true)
    }
    )
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void update(@Valid @RequestBody User user, @PathVariable int id) {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        prepareAndSave(user);
    }

    @Operation(
            summary = "Get user by email",
            parameters = {
            @Parameter(name = "email",
                    description = "The email of User that needs to be fetched.",
                    content = @Content(examples = {@ExampleObject(value = "admin@gmail.com")}),
                    required = true)
    }
    )
    @GetMapping("/by")
    public ResponseEntity<User> getByEmail(@RequestParam String email) {
        log.info("getByEmail {}", email);
        return ResponseEntity.of(repository.getByEmail(email));
    }

    @Operation(
            summary = "Set enable or disable status for user",
            parameters = {
                    @Parameter(name = "id",
                            description = "The id of User that needs to be updated. Use 1 for testing.",
                            content = @Content(examples = {@ExampleObject(value = "1")}),
                            required = true),
                    @Parameter(name = "enabled",
                    description = "enable or disable status for user",
                    content = @Content(examples = {@ExampleObject(value = "true")}),
                    required = true)
    }
    )
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(allEntries = true)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        User user = repository.getById(id);
        user.setEnabled(enabled);
    }
}