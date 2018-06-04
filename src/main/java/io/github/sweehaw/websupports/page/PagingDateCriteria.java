package io.github.sweehaw.websupports.page;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

/**
 * @author sweehaw
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingDateCriteria {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("start_date")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("end_date")
    private Date endDate;

    Date getStartDate() {

        Calendar cal = Calendar.getInstance();

        if (this.startDate == null) {
            cal.set(Calendar.YEAR, 2017);
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DATE, 1);
        } else {
            cal.setTime(this.startDate);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    Date getEndDate() {

        Calendar cal = Calendar.getInstance();
        cal.setTime(this.endDate == null ? new Date() : this.endDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
}
