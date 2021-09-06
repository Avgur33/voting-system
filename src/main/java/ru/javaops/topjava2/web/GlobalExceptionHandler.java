package ru.javaops.topjava2.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.javaops.topjava2.error.AppException;
import ru.javaops.topjava2.error.LateTimeException;
import ru.javaops.topjava2.error.NotFoundException;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String EXCEPTION_DUPLICATE_EMAIL = "User with this email already exists";
    public static final String EXCEPTION_DUPLICATE_VOTE = "exception.vote.duplicate";
    public static final String EXCEPTION_DUPLICATE_RESTAURANT = "exception.restaurant.duplicate";
    public static final String EXCEPTION_DUPLICATE_DISH = "exception.dish.duplicate";
    public static final String EXCEPTION_DUPLICATE_MENU = "exception.menu.duplicate";
    public static final String EXCEPTION_REMOVAL_ORDER = "exception.dish delete menu first";

    private static final Map<String, String> CONSTRAINS_I18N_MAP = Map.of(
            "vote_unique_reg_date_user_id_idx", EXCEPTION_DUPLICATE_VOTE,
            "restaurant_unique_name_location_idx", EXCEPTION_DUPLICATE_RESTAURANT,
            "dish_unique_name_restaurant_idx", EXCEPTION_DUPLICATE_DISH,
            "menu_unique_for_date_restaurant_id_idx", EXCEPTION_DUPLICATE_MENU,
            "public.menu_dishes foreign key(dishes_id)", EXCEPTION_REMOVAL_ORDER
    );

    private final ErrorAttributes errorAttributes;

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NotNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appException(WebRequest request, AppException ex) {
        log.error("ApplicationException", ex);
        return createResponseEntity(getDefaultBody(request, ex.getOptions(), null), ex.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> argumentException(WebRequest request, IllegalArgumentException ex) {
        log.error("ApplicationException", ex);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.defaults(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    //422
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> persistException(WebRequest request, NotFoundException ex) {
        log.error("EntityNotFoundException ", ex);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }
    //423
    @ExceptionHandler(LateTimeException.class)
    public ResponseEntity<?> lateException(WebRequest request, LateTimeException ex) {
        log.error("LateTimeException", ex);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.LOCKED);
    }
    //422
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> persistException(WebRequest request, EntityNotFoundException ex) {
        log.error("EntityNotFoundException ", ex);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }
    //400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> typeMismatchException(WebRequest request, MethodArgumentTypeMismatchException ex) {
        log.error("IllegalArgumentException ", ex);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.defaults(), "Invalid data format of " + ex.getName()), HttpStatus.BAD_REQUEST);
    }
    //422
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflictException(WebRequest request, DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException ", ex);
        String rootMsg = ex.getMessage();
        if (rootMsg != null) {
            String lowerCaseMsg = rootMsg.toLowerCase();
            for (Map.Entry<String, String> entry : CONSTRAINS_I18N_MAP.entrySet()) {
                if (lowerCaseMsg.contains(entry.getKey())) {
                    return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.defaults(), entry.getValue()), HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }
        }
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<Object> handleBindingErrors(BindingResult result, WebRequest request) {
        String msg = result.getFieldErrors().stream()
                .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.joining("\n"));
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.defaults(), msg), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private Map<String, Object> getDefaultBody(WebRequest request, ErrorAttributeOptions options, String msg) {
        Map<String, Object> body = errorAttributes.getErrorAttributes(request, options);
        if (msg != null) {
            body.put("message", msg);
        }
        return body;
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> createResponseEntity(Map<String, Object> body, HttpStatus status) {
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        return (ResponseEntity<T>) ResponseEntity.status(status).body(body);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        log.error("Exception", ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
