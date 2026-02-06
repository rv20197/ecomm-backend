package com.vatsalrajgor.eCommerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = SafeHtmlValidator.class)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface SafeHtml {
    String message() default "Invalid input: contains potentially malicious HTML";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
