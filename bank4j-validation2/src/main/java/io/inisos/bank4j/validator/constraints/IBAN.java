package io.inisos.bank4j.validator.constraints;

import io.inisos.bank4j.validator.constraintvalidators.IBANValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = IBANValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface IBAN {
    String message() default "{io.inisos.bank4j.validator.constraints.IBAN.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
