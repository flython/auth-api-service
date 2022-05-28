package top.okfly.auth.infra.exception;

import lombok.AllArgsConstructor;

public class RoleException extends RuntimeException {

    private RoleException(Situation situation) {
        super(situation.msg);
    }

    private RoleException(Situation situation, String message) {
        super(message);
    }

    public static RoleException of(Situation situation) {
        return new RoleException(situation);
    }

    @AllArgsConstructor
    public enum Situation {
        ROLE_EXIST("ROLE ALREADY EXISTS"),
        ROLE_NOT_EXIST("ROLE DOESN'T EXIST"),
        ;

        public String msg;
    }


}
