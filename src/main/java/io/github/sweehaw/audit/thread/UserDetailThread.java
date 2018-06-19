package io.github.sweehaw.audit.thread;

import io.github.sweehaw.audit.tools.AuditUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;

import java.util.HashMap;

/**
 * @author sweehaw
 */
@Slf4j
@RequiredArgsConstructor
public class UserDetailThread extends Thread {

    public final CrudRepository repo;
    public final String action;
    public final Object object;
    public final Object newObject;
    public final Object oldObject;
    public final Integer primaryKey;
    public final String userAccessId;

    @Override
    public void run() {

        try {

            HashMap<String, String> newObjectFields = AuditUtils.getFieldKeyValue(newObject);
            HashMap<String, String> oldObjectFields = AuditUtils.getFieldKeyValue(oldObject);

            AuditUtils.compareOldValueAndNewValue(this.repo, this.primaryKey, this.userAccessId, this.object, newObjectFields, oldObjectFields);

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }
}
