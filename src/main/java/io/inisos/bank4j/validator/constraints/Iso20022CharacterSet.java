package io.inisos.bank4j.validator.constraints;

import io.inisos.bank4j.validator.constraintvalidators.Iso20022CharacterSetValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = Iso20022CharacterSetValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface Iso20022CharacterSet {
    String message() default "{io.inisos.bank4j.validator.constraints.ISO20022CharacterSet.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowDiacritics() default false;
}
