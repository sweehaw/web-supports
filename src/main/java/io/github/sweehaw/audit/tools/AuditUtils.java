package io.github.sweehaw.audit.tools;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.sweehaw.audit.annotation.*;
import io.github.sweehaw.audit.enums.AuditAction;
import io.github.sweehaw.websupports.util.CommUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author sweehaw
 */
@Slf4j
public class AuditUtils {

    public static Object getOldObject(EntityManager em, Object entity) throws Exception {

        Object pk = null;
        for (Field f : getSuperClass(entity)) {

            JsonProperty jsonProperty = f.getAnnotation(JsonProperty.class);
            Id id = f.getAnnotation(Id.class);

            if (id != null && jsonProperty != null) {
                f.setAccessible(true);
                pk = f.get(entity);
            }
        }

        if (pk != null) {
            return em.find(entity.getClass(), pk);
        }
        return null;
    }

    public static List<Field> getSuperClass(Object o) {

        List<Field> fields = new ArrayList<>(Arrays.asList(o.getClass().getDeclaredFields()));
        List<Field> superFields = new ArrayList<>(Arrays.asList(o.getClass().getSuperclass().getDeclaredFields()));
        fields.addAll(superFields);

        return fields;
    }

    public static HashMap<AuditAction, String> getObjectFields(Object o) throws Exception {

        HashMap<AuditAction, String> map = new HashMap<>();
        for (Field f : getSuperClass(o)) {

            JsonProperty jsonProperty = f.getAnnotation(JsonProperty.class);
            AuditClient auditClient = f.getAnnotation(AuditClient.class);
            Id id = f.getAnnotation(Id.class);

            f.setAccessible(true);

            String fieldName = f.getName();
            Object fieldValue = f.get(o);

            if (jsonProperty != null) {
                fieldName = jsonProperty.value();
            }

            if (fieldValue != null) {

                if (id != null) {

                    HashMap<String, Object> pkMap = new HashMap<>();
                    pkMap.put(fieldName, fieldValue);

                    map.put(AuditAction.USER_ACCESS_PK, pkMap.toString());
                    map.put(AuditAction.USER_ACCESS_PK_VALUE, fieldValue.toString());
                    map.put(AuditAction.USER_ACCESS_ENTITY, o.getClass().getSimpleName());

                } else if (auditClient != null) {

                    AuditAction auditAction = auditClient.value();
                    if (f.getType().isAssignableFrom(String.class)) {

                        switch (auditAction) {

                            case USER_ACCESS_IP:
                                map.put(AuditAction.USER_ACCESS_IP, fieldValue.toString());
                                break;

                            case USER_ACCESS_BROWSER:
                                map.put(AuditAction.USER_ACCESS_BROWSER, fieldValue.toString());
                                break;

                            case USER_ACCESS_ID:
                                map.put(AuditAction.USER_ACCESS_ID, fieldValue.toString());
                                break;

                            case USER_ACCESS_RANDOM_STRING:
                                map.put(AuditAction.USER_ACCESS_RANDOM_STRING, fieldValue.toString());
                                break;

                            case USER_ACCESS_SERVICE:
                                map.put(AuditAction.USER_ACCESS_SERVICE, fieldValue.toString());
                                break;

                            default:
                                break;
                        }
                    } else if (f.getType().isAssignableFrom(HttpServletRequest.class)) {
                        HttpServletRequest request = (HttpServletRequest) fieldValue;
                        map.put(AuditAction.USER_ACCESS_URL, request.getRequestURI());
                    }
                }
            }
        }
        return map;
    }

    public static Object setObjectFields(Object o, HashMap<AuditAction, String> map) throws Exception {

        for (Field f : getSuperClass(o)) {

            AuditServer auditServer = f.getAnnotation(AuditServer.class);

            if (auditServer != null) {
                f.setAccessible(true);
                AuditAction auditAction = auditServer.value();

                switch (auditAction) {
                    case USER_ACCESS_IP:
                        f.set(o, map.get(AuditAction.USER_ACCESS_IP));
                        break;

                    case USER_ACCESS_BROWSER:
                        f.set(o, map.get(AuditAction.USER_ACCESS_BROWSER));
                        break;

                    case USER_ACCESS_URL:
                        f.set(o, map.get(AuditAction.USER_ACCESS_URL));
                        break;

                    case USER_ACCESS_ID:
                        f.set(o, map.get(AuditAction.USER_ACCESS_ID));
                        break;

                    case USER_ACCESS_PK:
                        f.set(o, map.get(AuditAction.USER_ACCESS_PK));
                        break;

                    case USER_ACCESS_ACTION:
                        f.set(o, map.get(AuditAction.USER_ACCESS_ACTION));
                        break;

                    case USER_ACCESS_ENTITY:
                        f.set(o, map.get(AuditAction.USER_ACCESS_ENTITY));
                        break;

                    case USER_ACCESS_SERVICE:
                        f.set(o, map.get(AuditAction.USER_ACCESS_SERVICE));
                        break;

                    case USER_ACCESS_DATE:
                        f.set(o, new Date());
                        break;

                    default:
                        break;
                }
            }
        }
        return o;
    }

    public static HashMap<String, String> getFieldKeyValue(Object o) throws Exception {

        HashMap<String, String> map = new HashMap<>();
        for (Field f : o.getClass().getDeclaredFields()) {

            LastModifiedDate lastModifiedDate = f.getAnnotation(LastModifiedDate.class);
            LastModifiedBy lastModifiedBy = f.getAnnotation(LastModifiedBy.class);
            JsonProperty jsonProperty = f.getAnnotation(JsonProperty.class);
            CreatedDate createdDate = f.getAnnotation(CreatedDate.class);
            JsonFormat jsonFormat = f.getAnnotation(JsonFormat.class);
            Version version = f.getAnnotation(Version.class);
            Column column = f.getAnnotation(Column.class);

            if (column != null && jsonFormat != null && jsonProperty != null
                    && lastModifiedDate == null
                    && lastModifiedBy == null
                    && createdDate == null
                    && version == null) {

                f.setAccessible(true);
                String jsonName = jsonProperty.value();
                Object fieldValue = f.get(o);

                if (fieldValue == null) {
                    map.put(jsonName, "null");

                } else if (f.getType().isAssignableFrom(String.class)) {
                    map.put(jsonName, fieldValue.toString());

                } else if (f.getType().isAssignableFrom(Integer.class)) {
                    Integer integer = (Integer) fieldValue;
                    map.put(jsonName, Integer.toString(integer));

                } else if (f.getType().isAssignableFrom(BigDecimal.class)) {
                    BigDecimal bigDecimal = (BigDecimal) fieldValue;
                    map.put(jsonName, bigDecimal.toString());

                } else if (f.getType().isAssignableFrom(Long.class)) {
                    Long longValue = (Long) fieldValue;
                    map.put(jsonName, Long.toString(longValue));

                } else if (f.getType().isAssignableFrom(Date.class)) {
                    String format = jsonFormat.pattern();
                    Date date = (Date) fieldValue;
                    map.put(jsonName, CommUtils.formatDate(format, date));
                }
            }
        }
        return map;
    }

    public static void compareOldValueAndNewValue(CrudRepository repo, Integer primaryKey, String userAccessId, Object object, HashMap<String, String> newObjectFields, HashMap<String, String> oldObjectFields) {

        newObjectFields.forEach((jsonName, newValue) -> {

            try {
                Object cloneObject = object.getClass().newInstance();
                String oldValue = oldObjectFields.getOrDefault(jsonName, null);

                if (!newValue.equals(oldValue)) {

                    for (Field f : getSuperClass(cloneObject)) {

                        AuditServer auditServer = f.getAnnotation(AuditServer.class);

                        if (auditServer != null) {
                            f.setAccessible(true);
                            AuditAction auditAction = auditServer.value();

                            switch (auditAction) {
                                case USER_ACCESS_ACTION_ID:
                                    f.set(cloneObject, primaryKey);
                                    break;

                                case USER_ACCESS_FIELD:
                                    f.set(cloneObject, jsonName);
                                    break;

                                case USER_ACCESS_OLD_VALUE:
                                    f.set(cloneObject, oldValue);
                                    break;

                                case USER_ACCESS_NEW_VALUE:
                                    f.set(cloneObject, newValue);
                                    break;

                                case USER_ACCESS_DATE:
                                    f.set(cloneObject, new Date());
                                    break;

                                case USER_ACCESS_ID:
                                    f.set(cloneObject, userAccessId);
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                    repo.save(cloneObject);
                }
            } catch (Exception ex) {
                log.error(ex.toString(), ex);
            }
        });
    }
}

