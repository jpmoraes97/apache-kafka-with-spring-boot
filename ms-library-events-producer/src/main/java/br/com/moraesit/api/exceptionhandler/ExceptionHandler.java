package br.com.moraesit.api.exceptionhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String MSG_GENERICA =
            "Ocorreu um erro inesperado no sistema, tente novamente. " +
                    "Se o problema persistir, entre em contato com o administrador do sistema.";

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleValidationInternal(ex, headers, status, request, ex.getBindingResult());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {

        if (body == null) {
            body = Error.builder()
                    .data(OffsetDateTime.now())
                    .status(status.value())
                    .mensagem(MSG_GENERICA)
                    .build();
        } else if (body instanceof String) {
            body = Error.builder()
                    .data(OffsetDateTime.now())
                    .status(status.value())
                    .mensagem(MSG_GENERICA)
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private ResponseEntity<Object> handleValidationInternal(
            Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request, BindingResult bindingResult) {

        String detail = "Um ou mais campos estão inválidos, Faça o preenchimento correto e tente novamente.";

        List<Error.ErrorField> fields = bindingResult.getAllErrors().stream()
                .map(objectError -> {
                    String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
                    String name = objectError.getObjectName();

                    if (objectError instanceof FieldError) {
                        name = ((FieldError) objectError).getField();
                    }

                    return Error.ErrorField.builder()
                            .nome(name)
                            .mensagem(message)
                            .build();

                }).collect(Collectors.toList());

        Error erro = errorBuilder(status, detail)
                .fields(fields)
                .build();

        return handleExceptionInternal(ex, erro, headers, status, request);
    }

    private Error.ErrorBuilder errorBuilder(HttpStatus status, String detalhe) {
        return Error.builder()
                .data(OffsetDateTime.now())
                .detalhe(detalhe)
                .status(status.value());
    }
}