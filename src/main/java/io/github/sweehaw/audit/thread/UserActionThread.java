package io.github.sweehaw.audit.thread;

import io.github.sweehaw.audit.enums.AuditAction;
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
public class UserActionThread extends Thread {

    public final CrudRepository repo1;
    public final CrudRepository repo2;
    public final Object object1;
    public final Object object2;
    public final Object newObject;
    public final Object oldObject;
    public final String action;

    @Override
    public void run() {

        try {

            HashMap<AuditAction, String> map = AuditUtils.getObjectFields(this.newObject);
            map.put(AuditAction.USER_ACCESS_ACTION, this.action);

            Object object = AuditUtils.setObjectFields(object1, map);

            this.repo1.save(object);

            HashMap<AuditAction, String> map2 = AuditUtils.getObjectFields(object);

            Integer primaryKey = Integer.parseInt(map2.get(AuditAction.USER_ACCESS_PK_VALUE));
            String userAccessId = map2.get(AuditAction.USER_ACCESS_ID);

            if (this.oldObject != null) {
                new UserDetailThread(
                        this.repo2,
                        this.action,
                        this.object2,
                        this.newObject,
                        this.oldObject,
                        primaryKey,
                        userAccessId
                ).run();
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }
}
