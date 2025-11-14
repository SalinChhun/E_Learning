package com.elearning.common.exception;

import com.elearning.common.annotation.CustomErrorFormat;
import com.elearning.common.common.api.*;
import com.elearning.common.components.logging.AppLogManager;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.util.RequestContextUtil;
import com.elearning.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Component
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        return buildResponseEntity(new ApiStatus(BAD_REQUEST.value(), ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildResponseEntity(new ApiStatus(BAD_REQUEST.value(), ex.getMessage()));
    }

    /**
     * Determine the error format based on the handler method annotation
     */
    private CustomErrorFormat.ErrorFormat getErrorFormat() {
        try {
            HttpServletRequest request = RequestContextUtil.getCurrentRequest();
            if (request == null) {
                return CustomErrorFormat.ErrorFormat.DEFAULT;
            }

            // Try to get the handler method from request attributes
            Object handler = request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");

            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;

                // Check method-level annotation first (highest priority)
                CustomErrorFormat methodAnnotation = handlerMethod.getMethodAnnotation(CustomErrorFormat.class);
                if (methodAnnotation != null) {
                    AppLogManager.info("Using method-level error format: " + methodAnnotation.value());
                    return methodAnnotation.value();
                }

                // Check class-level annotation
                CustomErrorFormat classAnnotation = handlerMethod.getBeanType().getAnnotation(CustomErrorFormat.class);
                if (classAnnotation != null) {
                    AppLogManager.info("Using class-level error format: " + classAnnotation.value());
                    return classAnnotation.value();
                }
            }
        } catch (Exception e) {
            // If we can't determine the format, use default
            AppLogManager.error("Error determining custom format, using DEFAULT", e);
        }

        return CustomErrorFormat.ErrorFormat.DEFAULT;
    }

    /**
     * Build response based on the determined format
     */
    private ResponseEntity<Object> buildCustomResponseEntity(ApiStatus apiStatus) {
        CustomErrorFormat.ErrorFormat format = getErrorFormat();

        switch (format) {
            case SIMPLE:
                return new ResponseEntity<>(
                        new SimpleErrorResponse(String.valueOf(apiStatus.getCode()), apiStatus.getMessage()),
                        HttpStatusCode.valueOf(apiStatus.getCode())
                );

            case DETAIL:
                return new ResponseEntity<>(
                        new DetailErrorResponse(apiStatus.getCode(), apiStatus.getMessage()),
                        HttpStatusCode.valueOf(apiStatus.getCode())
                );

            case NESTED:
                return new ResponseEntity<>(
                        new NestedErrorResponse(
                                new NestedErrorResponse.ErrorInfo(String.valueOf(apiStatus.getCode()), apiStatus.getMessage())
                        ),
                        HttpStatusCode.valueOf(apiStatus.getCode())
                );

            default:
                // Use your existing standard format
                return buildResponseEntity(apiStatus);
        }
    }


    /**
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
     *
     * @param ex      HttpMediaTypeNotSupportedException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        AppLogManager.error(ex);

        StringBuilder builder = new StringBuilder();

        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");

        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

        return buildResponseEntity(new ApiStatus(status.value(),builder.toString()));
    }

    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        AppLogManager.error("Method argument not valid", ex);

        StringBuilder sb = new StringBuilder();

        // Get the first field error message
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            sb.append(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        } else if (!ex.getBindingResult().getGlobalErrors().isEmpty()) {
            sb.append(ex.getBindingResult().getGlobalErrors().get(0).getDefaultMessage());
        } else {
            sb.append("Validation failed");
        }

        return buildCustomResponseEntity(new ApiStatus(status.value(), sb.toString()));
    }

    /**
     * Handles jakarta.validation.ConstraintViolationException. Thrown when @Validated fails.
     *
     * @param ex the ConstraintViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex) {

        AppLogManager.error(ex);
        String cleanMessage = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(violation -> violation.getMessage())  // Get only the message part
                .orElse("Validation error");

        return buildResponseEntity(new ApiStatus(BAD_REQUEST.value(), cleanMessage));
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Object> handleValidationException(
            ValidationException ex) {

        AppLogManager.error(ex);
        return buildResponseEntity(new ApiStatus(BAD_REQUEST.value(),ex.getMessage()));

    }


    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex      HttpMessageNotReadableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        AppLogManager.error(ex);

//        String error = "Malformed JSON request";
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, error, ex);

        return buildResponseEntity(new ApiStatus(status.value(),"Malformed JSON request"));
    }

    /**
     * Handle HttpMessageNotWritableException.
     *
     * @param ex      HttpMessageNotWritableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        AppLogManager.error(ex);
        String error = "Error writing JSON output";

        return buildResponseEntity(new ApiStatus(status.value(),error));
    }

//    @Override
//    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//        return super.handleAsyncRequestTimeoutException(ex, headers, status, request);
//    }


    /**
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     *
     * @param ex      MissingServletRequestParameterException
     * @param headers HttpHeaders
     * @param status  HttpStatusCode
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";

        return buildResponseEntity(new ApiStatus(status.value(),error));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
                AppLogManager.error(ex);
//        System.out.println(ex);
//
//        apiError.setMessage(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
//        apiError.setDebugMessage(ExceptionUtils.getStackTrace(ex));

        return buildResponseEntity(new ApiStatus(status.value(),String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL())));
    }

        /**
     * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
     *
     * @param ex the DataIntegrityViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        AppLogManager.error(ex);
        ApiStatus apiStatus = new ApiStatus(StatusCode.INTERNAL_SERVER_ERROR);
        if (ex.getCause() instanceof ConstraintViolationException) {
//            return buildResponseEntity(new ApiStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageHelper.getMessage("500")));
            return buildResponseEntity(apiStatus);
        }

//        return buildResponseEntity(new ApiStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageHelper.getMessage("500")));
          return buildResponseEntity(apiStatus);
    }



    /**
     * Handle Exception, handle generic Exception.class
     *
     * @param ex the Exception
     * @return the ApiError object
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {

        AppLogManager.error(ex);
//        System.out.println(ex);
//        ApiError apiError = new ApiError(BAD_REQUEST);
//
//        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
//        apiError.setDebugMessage(ExceptionUtils.getStackTrace(ex));

        return buildResponseEntity(new ApiStatus(BAD_REQUEST.value(), String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()) ));

    }

    /**
     * Handle HandleBusinessException
     *
     * @param ex BusinessException
     * @return the ApiError object
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(final BusinessException ex) {

        AppLogManager.error(ex);
        StatusCode statusCode = ex.getErrorCode();
        ApiStatus apiStatus = new ApiStatus(statusCode);
        apiStatus.setMessage(StringUtils.defaultIfBlank(ex.getMessage(), statusCode.getMessage()));
//        apiStatus.setMessage(MessageHelper.getMessage(String.valueOf(statusCode.getCode()), statusCode.getMessage()));
        return buildResponseEntity(apiStatus);

    }

    /**
     * Handle handleThrowable
     *
     * @param ex      Throwable
     * @return the ApiError object
     */
    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<Object> handleThrowable(Throwable ex) {
        AppLogManager.error(ex);
//        apiError.setMessage(ex.getMessage());
//        apiError.setDebugMessage(ExceptionUtils.getStackTrace(ex));
        ApiStatus apiStatus = new ApiStatus(StatusCode.INTERNAL_SERVER_ERROR);
        return buildResponseEntity(apiStatus);

    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        AppLogManager.error(ex);
        ApiStatus apiStatus = new ApiStatus(StatusCode.INTERNAL_SERVER_ERROR);
        return buildResponseEntity(apiStatus);
    }

    public ResponseEntity<Object> buildResponseEntity(ApiStatus apiStatus) {
        ApiResponse<Object> apiResponse = new ApiResponse<>(apiStatus, new EmptyJsonResponse());

        return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiStatus.getCode()));
    }


}
