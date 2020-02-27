package io.github.sweehaw.websupports.page;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * @author sweehaw
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingDecimalCriteria {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonProperty("decimal_from")
    private BigDecimal startBigDecimal;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonProperty("decimal_to")
    private BigDecimal endOfBigDecimal;

    BigDecimal getStartBigDecimal() {
        return this.startBigDecimal != null
                ? this.startBigDecimal
                : new BigDecimal("0");
    }

    BigDecimal getEndOfBigDecimal() {
        return this.endOfBigDecimal != null
                ? this.endOfBigDecimal
                : new BigDecimal("0");
    }
}
