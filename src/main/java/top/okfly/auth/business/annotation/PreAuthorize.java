package top.okfly.auth.business.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuthorize {
    //TODO setting permit or reject action here
}
