package com.promocion.horizontalback.shared;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention( RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface LoggedUser {
}
