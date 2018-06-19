package io.github.sweehaw.websupports.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author sweehaw
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InvalidIdException extends Exception {

    private String randomString;

    public InvalidIdException(String randomString) {
        super("Invalid ID");
        this.randomString = randomString;
    }
}
