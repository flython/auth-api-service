package top.okfly.auth.infra.exception;

import lombok.AllArgsConstructor;

public class UserException extends RuntimeException {

    private UserException(Situation situation) {
        super(situation.msg);
    }

    private UserException(Situation situation, String message) {
        super(message);
    }

    public static UserException of(Situation situation) {
        return new UserException(situation);
    }

    @AllArgsConstructor
    public enum Situation {
        USER_EXIST("USER ALREADY EXISTS"),
        DELETE_FAIL_NOT_EXIST("USER DOESN'T EXIST"),
        USERNAME_OR_PWD_ERROR("USER NAME OR PASSWORD INCORRECT"),
        ;

        public String msg;
    }


}
