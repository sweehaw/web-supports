package io.github.sweehaw.websupports.util;


import io.github.sweehaw.auditsupports.annotation.CreatedDate;
import io.github.sweehaw.auditsupports.annotation.LastModifiedBy;
import io.github.sweehaw.auditsupports.annotation.LastModifiedDate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.persistence.Id;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sweehaw
 */
public class MyBeanUtils extends BeanUtils {

    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames(Object source) {

        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();

        for (PropertyDescriptor p : pds) {

            LastModifiedDate lastModifiedDate = p.getReadMethod() != null ? p.getReadMethod().getAnnotation(LastModifiedDate.class) : null;
            LastModifiedBy lastModifiedBy = p.getReadMethod() != null ? p.getReadMethod().getAnnotation(LastModifiedBy.class) : null;
            CreatedDate createdDate = p.getReadMethod() != null ? p.getReadMethod().getAnnotation(CreatedDate.class) : null;
            Id id = p.getReadMethod() != null ? p.getReadMethod().getAnnotation(Id.class) : null;

            if (lastModifiedDate != null || lastModifiedBy != null || createdDate != null || id != null) {
                emptyNames.add(p.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
