package top.okfly.auth.infra.exception;

import lombok.AllArgsConstructor;

public class AuthException extends RuntimeException {

    private AuthException(Situation situation) {
        super(situation.msg);
    }

    private AuthException(Situation situation, String message) {
        super(message);
    }

    public static AuthException of(Situation situation) {
        return new AuthException(situation);
    }

    @AllArgsConstructor
    public enum Situation {
        TOKEN_PARSE_ERROR("TOKEN PARSE ERROR"),
        TOKEN_EXPIRED("TOKEN HAS EXPIRED!"),
        ;

        public String msg;
    }


}
