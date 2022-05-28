package top.okfly.auth.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    // use for invalidate
    Long id;
    Long userId;
    List<String> roles;
    long expire;

    @JsonIgnore
    String tokenString;

}
