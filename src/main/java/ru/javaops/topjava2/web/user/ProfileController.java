package ru.javaops.topjava2.web.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava2.model.User;
import ru.javaops.topjava2.to.UserTo;
import ru.javaops.topjava2.util.UserUtil;
import ru.javaops.topjava2.web.AuthUser;

import javax.validation.Valid;
import java.net.URI;

import static ru.javaops.topjava2.util.validation.ValidationUtil.assureIdConsistent;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = ProfileController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "ProfileController", description = "Controller for registration User.")
@CacheConfig(cacheNames = "users")
public class ProfileController extends AbstractUserController {
    static final String REST_URL = "/api/profile";

    @Operation(
            summary = "Get authorized user"
    )
    @GetMapping
    public HttpEntity<User> get(@AuthenticationPrincipal AuthUser authUser) {
        return super.get(authUser.id());
    }

    @Operation(
            summary = "Delete authorized user"
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        super.delete(authUser.id());
    }

    @Operation(
            summary = "Create user"
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    public ResponseEntity<User> register(@Valid @RequestBody UserTo userTo) {
        log.info("register {}", userTo);
        checkNew(userTo);
        User created = prepareAndSave(UserUtil.createNewFromTo(userTo));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(
            summary = "Update authorized user"
    )
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(allEntries = true)
    public void update(@RequestBody @Valid UserTo userTo, @AuthenticationPrincipal AuthUser authUser) {
        assureIdConsistent(userTo, authUser.id());
        User user = repository.getById(userTo.id());
        prepareAndSave(UserUtil.updateFromTo(user, userTo));
    }
}