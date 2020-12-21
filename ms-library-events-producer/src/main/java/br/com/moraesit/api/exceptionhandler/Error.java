package br.com.moraesit.api.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

    private Integer status;
    private OffsetDateTime data;
    private String detalhe;
    private String mensagem;
    private List<ErrorField> fields;

    @Data
    @Builder
    public static class ErrorField {
        private String nome;
        private String mensagem;
    }
}