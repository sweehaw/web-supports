package io.github.sweehaw.auditsupports.domain;

import io.github.sweehaw.auditsupports.enums.AuditAction;
import io.github.sweehaw.auditsupports.annotation.CreatedDate;
import io.github.sweehaw.auditsupports.annotation.LastModifiedBy;
import io.github.sweehaw.auditsupports.annotation.LastModifiedDate;
import io.github.sweehaw.auditsupports.tools.AuditUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;


/**
 * @author sweehaw
 */
@Component
@Slf4j
public class AuditListener {

    @PrePersist
    void prePersist(@NotNull Object object) {

        try {

            HashMap<AuditAction, String> map = AuditUtils.getObjectFields(object);

            for (Field f : AuditUtils.getSuperClass(object)) {

                LastModifiedDate lastModifiedDate = f.getAnnotation(LastModifiedDate.class);
                LastModifiedBy lastModifiedBy = f.getAnnotation(LastModifiedBy.class);
                CreatedDate createdDate = f.getAnnotation(CreatedDate.class);

                if (createdDate != null || lastModifiedDate != null) {
                    f.setAccessible(true);
                    f.set(object, new Date());
                } else if (lastModifiedBy != null) {
                    f.setAccessible(true);
                    f.set(object, map.get(AuditAction.USER_ACCESS_ID));
                }
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @PreUpdate
    void preUpdate(@NotNull Object object) {

        try {

            HashMap<AuditAction, String> map = AuditUtils.getObjectFields(object);

            for (Field f : AuditUtils.getSuperClass(object)) {

                LastModifiedDate lastModifiedDate = f.getAnnotation(LastModifiedDate.class);
                LastModifiedBy lastModifiedBy = f.getAnnotation(LastModifiedBy.class);

                if (lastModifiedDate != null) {
                    f.setAccessible(true);
                    f.set(object, new Date());
                } else if (lastModifiedBy != null) {
                    f.setAccessible(true);
                    f.set(object, map.get(AuditAction.USER_ACCESS_ID));
                }
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
