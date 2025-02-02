package com.gray.bird.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.util.HashSet;
import java.util.Set;

import com.gray.bird.common.json.ResourceError;
import com.gray.bird.common.json.ResourceErrorResponse;
import com.gray.bird.common.utils.JsonApiErrorFactory;

// In a Spring Boot application, global exception handling is crucial for maintaining consistency
// and providing a clear, user-friendly response when errors occur. Below is a list of common
// exceptions that you should consider handling in a global exception handler, along with brief
// descriptions of each:
// 1. MethodArgumentNotValidException
//
// Description: Thrown when a method argument annotated with @Valid or @Validated fails
// validation (e.g., @NotNull, @Size, etc.). Action: Return a 400 Bad Request response with
// details of the validation errors.
//
// 2. ConstraintViolationException
//
// Description: Thrown when a bean validation constraint fails (e.g., for @NotNull, @Size,
// etc.). It's typically thrown when validating an entity that doesn't pass constraints. Action:
// Return a 400 Bad Request response with details of the violated constraints.
//
// 3. HttpMessageNotReadableException
//
// Description: Thrown when the HTTP request body is not readable or cannot be parsed (e.g.,
// invalid JSON format). Action: Return a 400 Bad Request response indicating that the request
// body is malformed or cannot be processed.
//
// 4. HttpRequestMethodNotSupportedException
//
// Description: Thrown when the HTTP request method (e.g., GET, POST, PUT, DELETE) is not
// supported for a specific endpoint. Action: Return a 405 Method Not Allowed response
// indicating the HTTP method is not supported for the requested resource.
//
// 5. NoHandlerFoundException
//
// Description: Thrown when no handler method is found for the given request (e.g., the URL path
// does not match any controller). Action: Return a 404 Not Found response indicating that the
// resource could not be found.
//
// 6. ResourceNotFoundException (Custom)
//
// Description: This is a custom exception that you can create to indicate that a requested
// resource was not found (e.g., a record in the database). Action: Return a 404 Not Found
// response.
//
// 7. BadRequestException (Custom)
//
// Description: This is a custom exception that you can create for handling bad requests that
// don't necessarily fall under validation but still need to be rejected with a 400 status.
// Action: Return a 400 Bad Request response with a custom message.
//
// 8. AccessDeniedException
//
// Description: Thrown when the user does not have the required permissions to access a specific
// resource (typically when using Spring Security). Action: Return a 403 Forbidden response
// indicating the user doesn't have permission to access the resource.
//
// 9. AuthenticationException
//
// Description: Thrown when authentication fails, typically when using Spring Security.
// Action: Return a 401 Unauthorized response indicating that the user needs to be
// authenticated.
//
// 10. IllegalArgumentException
//
// Description: A runtime exception typically thrown when an illegal argument is passed to a
// method. Action: Return a 400 Bad Request response with details about the invalid argument.
//
// 11. IllegalStateException
//
// Description: Thrown when the application is in an invalid state (e.g., invalid business logic
// flow). Action: Return a 400 Bad Request or 500 Internal Server Error response based on the
// context.
//
// 12. EntityNotFoundException (JPA/Hibernate)
//
// Description: Thrown when an entity (usually in JPA) cannot be found in the database, often
// occurs when attempting to load a non-existing entity. Action: Return a 404 Not Found response
// indicating that the entity could not be found.
//
// 13. DuplicateKeyException (Database-related)
//
// Description: Thrown when an operation violates a database constraint, such as inserting a
// duplicate record where one is not allowed (e.g., unique constraint violation). Action: Return
// a 409 Conflict response indicating that the resource already exists.
//
// 14. DataIntegrityViolationException (Spring Data)
//
// Description: Thrown when a database constraint is violated during an operation (e.g., a
// foreign key constraint violation). Action: Return a 400 Bad Request or 409 Conflict response,
// depending on the error.
//
// 15. TimeoutException
//
// Description: Thrown when an operation exceeds the allowed time, such as when a request to an
// external service times out. Action: Return a 408 Request Timeout response indicating the
// operation took too long.
//
// 16. UnsupportedOperationException
//
// Description: Thrown when an unsupported operation is invoked, typically in cases where
// certain functionality is not implemented. Action: Return a 405 Method Not Allowed response or
// a 501 Not Implemented response.
//
// 17. InternalServerErrorException (Custom)
//
// Description: This is a custom exception you might create to handle general server errors,
// such as unexpected conditions that don't fall into any of the more specific categories.
// Action: Return a 500 Internal Server Error response.
//
// 18. Throwable (Generic Exception)
//
// Description: A very generic fallback that catches any unforeseen exceptions not explicitly
// handled. Action: Return a 500 Internal Server Error response with a generic message such as
// "An unexpected error occurred".
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
	private final JsonApiErrorFactory errorFactory;

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final ApiException exception, HttpServletRequest request) {
		Set<ResourceError> errors = new HashSet<>();
		errors.add(errorFactory.createError(
			INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getReasonPhrase(), exception.getMessage()));
		return ResponseEntity.internalServerError()
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(errors));
	}

	// @Valid
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final MethodArgumentNotValidException exception, HttpServletRequest request) {
		HashSet<ResourceError> errors = new HashSet<>();
		exception.getBindingResult().getFieldErrors().forEach(err -> {
			errors.add(errorFactory.createError(BAD_REQUEST,
				"Argument not valid",
				err.getDefaultMessage(),
				errorFactory.createErrorSource(null, err.getField(), null)));
		});

		return ResponseEntity.badRequest()
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(errors));
	}

	// @NotNull, @Size, etc
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final ConstraintViolationException exception, HttpServletRequest request) {
		HashSet<ResourceError> errors = new HashSet<>();
		exception.getConstraintViolations().forEach(err -> {
			errors.add(errorFactory.createError(BAD_REQUEST,
				"Constraint Violation",
				err.getMessage(),
				errorFactory.createErrorSource(
					err.getRootBean().toString(), err.getPropertyPath().toString(), null)));
		});
		return ResponseEntity.badRequest()
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(errors));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final UnauthorizedException exception, HttpServletRequest request) {
		HttpStatus unauthorized = UNAUTHORIZED;
		return ResponseEntity.status(unauthorized)
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(
				errorFactory.createError(unauthorized, "Unauthorized", exception.getMessage())));
	}

	@ExceptionHandler(InvalidJwtException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final InvalidJwtException exception, HttpServletRequest request) {
		HttpStatus unauthorized = UNAUTHORIZED;
		return ResponseEntity.status(unauthorized)
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(
				errorFactory.createError(unauthorized, "Unauthorized", exception.getMessage())));
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final ExpiredJwtException exception, HttpServletRequest request) {
		HttpStatus unauthorized = UNAUTHORIZED;
		return ResponseEntity.status(unauthorized)
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(
				errorFactory.createError(unauthorized, "Unauthorized", exception.getMessage())));
	}

	@ExceptionHandler(InvalidVerificationTokenException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final InvalidVerificationTokenException exception, HttpServletRequest request) {
		HttpStatus unauthorized = UNAUTHORIZED;
		return ResponseEntity.status(unauthorized)
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(
				errorFactory.createError(unauthorized, "Unauthorized", exception.getMessage())));
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final ConflictException exception, HttpServletRequest request) {
		HttpStatus conflict = CONFLICT;
		return ResponseEntity.status(conflict)
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(
				errorFactory.createError(conflict, "Conflict", exception.getMessage())));
	}

	@ExceptionHandler(CacheException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final CacheException exception, HttpServletRequest request) {
		HttpStatus internalServerError = INTERNAL_SERVER_ERROR;
		return ResponseEntity.status(internalServerError)
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(errorFactory.createError(
				internalServerError, "Internal server error", exception.getMessage())));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ResourceErrorResponse> handleException(
		final ResourceNotFoundException exception, HttpServletRequest request) {
		HttpStatus notFoundError = NOT_FOUND;
		return ResponseEntity.status(notFoundError)
			.contentType(MediaType.APPLICATION_JSON)
			.body(errorFactory.createErrorResponse(
				errorFactory.createError(notFoundError, "Not found", exception.getMessage())));
	}
}
