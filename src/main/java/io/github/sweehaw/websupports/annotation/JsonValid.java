package io.github.sweehaw.websupports.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import io.github.sweehaw.websupports.enums.Ordinality;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
public @interface JsonValid {

    Ordinality ordinality() default Ordinality.OPTIONAL;

    int max() default -1;

    int min() default 0;

    String[] enumStatus() default "";

    String nullMessage() default "Field missing.";

    String minMessage() default "Minimum length is # characters.";

    String maxMessage() default "Maximum length is # characters.";

    String minNumberMessage() default "Minimum number is '#'.";

    String maxNumberMessage() default "Maximum number is '#'.";

    String enumMessage() default "Available value [].";
}
