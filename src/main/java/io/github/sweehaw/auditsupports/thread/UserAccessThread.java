package io.github.sweehaw.auditsupports.thread;

import io.github.sweehaw.auditsupports.enums.AuditAction;
import io.github.sweehaw.auditsupports.tools.AuditUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;

import java.util.HashMap;

/**
 * @author sweehaw
 */
@Slf4j
@RequiredArgsConstructor
public class UserAccessThread extends Thread {

    public final CrudRepository repo;
    public final Object object1;
    public final Object object2;

    @Override
    public void run() {

        try {

            HashMap<AuditAction, String> map = AuditUtils.getObjectFields(this.object2);
            Object o = AuditUtils.setObjectFields(object1, map);

            this.repo.save(o);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }
}
