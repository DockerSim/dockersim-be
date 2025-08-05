    package com.dockersim.exception.code;


    import org.springframework.http.HttpStatus;

    public interface ErrorCode {
        HttpStatus getStatus();
        String getCode();
        String getTemplate();
        default String getMessage(Object... args) {
            String template = getTemplate();
            if (args == null || args.length == 0) {
                return template;
            }
            return String.format(template, args);
        }
    }