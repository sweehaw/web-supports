package io.github.sweehaw.websupports.page;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author sweehaw
 */
@Data
@AllArgsConstructor
public class OperatorCriteria {

    public String operator;
    public Object value;
}
