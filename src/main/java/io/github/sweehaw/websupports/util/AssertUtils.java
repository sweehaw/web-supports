package io.github.sweehaw.websupports.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.sweehaw.auditsupports.tools.AuditUtils;
import io.github.sweehaw.websupports.annotation.JsonHeader;
import io.github.sweehaw.websupports.annotation.JsonValid;
import io.github.sweehaw.websupports.enums.Ordinality;
import io.github.sweehaw.websupports.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author sweehaw
 */
public class AssertUtils {

    public static void that(Boolean isTrue, Exception e) throws Exception {
        if (isTrue) {
            throw e;
        }
    }

    public static void that(Object o, InvalidValueException e) throws Exception {

        HashMap<String, String> map = new HashMap<>(1);

        String randomString = "randomString";

        for (Field f : AuditUtils.getSuperClass(o)) {

            JsonProperty jsonProperty = f.getAnnotation(JsonProperty.class);
            JsonHeader jsonHeader = f.getAnnotation(JsonHeader.class);
            JsonValid jsonValid = f.getAnnotation(JsonValid.class);

            if (jsonValid != null) {

                f.setAccessible(true);
                Boolean nullable = jsonValid.ordinality().equals(Ordinality.OPTIONAL);

                String fieldName = f.getName();
                Object fieldValue = f.get(o);

                if (jsonProperty != null) {
                    fieldName = jsonProperty.value();
                }

                isValueNull(map, jsonValid, fieldName, fieldValue, nullable);
                isString(map, jsonValid, f, fieldName, fieldValue);
                isEnumStatus(map, jsonValid, f, fieldName, fieldValue);
                isInteger(map, jsonValid, f, fieldName, fieldValue);
                isBigDecimal(map, jsonValid, f, fieldName, fieldValue);
            }

            if (jsonHeader != null) {

                f.setAccessible(true);

                String fieldName = f.getName();
                Object fieldValue = f.get(o);

                if (fieldValue != null && fieldName.equals(randomString)) {
                    e.setRandomString(fieldValue.toString());
                }
            }
        }

        if (map.size() > 0) {
            e.setMap(map);
            throw e;
        }
    }

    private static void isValueNull(HashMap<String, String> map, JsonValid jsonValid, String fieldName, Object fieldValue, Boolean nullable) {

        if (fieldValue == null) {
            if (!nullable) {
                map.put(fieldName, jsonValid.nullMessage());
            }
        }
    }

    private static void isString(HashMap<String, String> map, JsonValid jsonValid, Field f, String fieldName, Object fieldValue) {

        if (f.getType().isAssignableFrom(String.class) && fieldValue != null) {

            Integer fieldLength = fieldValue.toString().length();
            Integer minSize = jsonValid.min();
            Integer maxSize = jsonValid.max();

            Boolean validMaxLength = fieldLength <= maxSize || maxSize < 0;
            Boolean validMinLength = fieldLength >= minSize;

            String minMessage = jsonValid.minMessage().replace("#", minSize + "");
            String maxMessage = jsonValid.maxMessage().replace("#", maxSize + "");

            if (!validMinLength) {
                map.put(fieldName, minMessage);

            } else if (!validMaxLength) {
                map.put(fieldName, maxMessage);

            }
        }
    }

    private static void isEnumStatus(HashMap<String, String> map, JsonValid jsonValid, Field f, String fieldName, Object fieldValue) {

        String[] enumStatus = jsonValid.enumStatus();
        String enumMessage = jsonValid.enumMessage().replace("[]", Arrays.toString(enumStatus));
        Boolean isValidEnum = true;

        if (f.getType().isAssignableFrom(String.class)) {

            for (String s : enumStatus) {

                Boolean isNull = fieldValue == null;
                Boolean isEmpty = s.isEmpty();
                Boolean isValid = s.equals(isNull ? "" : fieldValue.toString());

                if (!isEmpty && !isValid) {
                    isValidEnum = false;
                } else {
                    isValidEnum = true;
                    break;
                }
            }
        }

        if (!isValidEnum) {
            map.put(fieldName, enumMessage);

        }
    }

    private static void isInteger(HashMap<String, String> map, JsonValid jsonValid, Field f, String fieldName, Object fieldValue) {

        if (f.getType().isAssignableFrom(Integer.class) && fieldValue != null) {

            Integer fieldLength = Integer.parseInt(fieldValue.toString());
            Integer minSize = jsonValid.min();
            Integer maxSize = jsonValid.max();

            Boolean validMaxLength = fieldLength <= maxSize || maxSize < 0;
            Boolean validMinLength = fieldLength >= minSize;

            String minMessage = jsonValid.minNumberMessage().replace("#", minSize + "");
            String maxMessage = jsonValid.maxNumberMessage().replace("#", maxSize + "");

            if (!validMinLength) {
                map.put(fieldName, minMessage);

            } else if (!validMaxLength) {
                map.put(fieldName, maxMessage);

            }
        }
    }

    private static void isBigDecimal(HashMap<String, String> map, JsonValid jsonValid, Field f, String fieldName, Object fieldValue) {

        if (f.getType().isAssignableFrom(BigDecimal.class) && fieldValue != null) {

            BigDecimal fieldLength = new BigDecimal(fieldValue.toString());
            BigDecimal minSize = new BigDecimal(jsonValid.min());
            BigDecimal maxSize = new BigDecimal(jsonValid.max());

            Boolean validMaxLength = fieldLength.compareTo(maxSize) < 1 || fieldLength.compareTo(BigDecimal.ZERO) < 0;
            Boolean validMinLength = fieldLength.compareTo(minSize) > 0;

            String minMessage = jsonValid.minNumberMessage().replace("#", minSize + "");
            String maxMessage = jsonValid.maxNumberMessage().replace("#", maxSize + "");

            if (!validMinLength) {
                map.put(fieldName, minMessage);

            } else if (!validMaxLength) {
                map.put(fieldName, maxMessage);

            }
        }
    }
}
