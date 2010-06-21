package org.duracloud.duradmin.security;

import org.duracloud.common.model.Securable;
import org.duracloud.common.model.Credential;
import org.duracloud.security.context.SecurityContextUtil;
import org.duracloud.security.error.NoUserLoggedInException;
import org.springframework.aop.MethodBeforeAdvice;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

/**
 * This class wraps calls to securable objects and logs them in if there is
 * a valid credential in the security context.
 * Otherwise, it logs the object out.
 *
 * @author Andrew Woods
 *         Date: Mar 28, 2010
 */
public class SecurityAdvice implements MethodBeforeAdvice {

    private final Logger log = Logger.getLogger(getClass());

    private SecurityContextUtil securityContextUtil;

    public SecurityAdvice(SecurityContextUtil util) {
        this.securityContextUtil = util;
    }

    public void before(Method method, Object[] objects, Object o)
        throws Throwable {
        String methodClass = method.getDeclaringClass().getCanonicalName();
        String methodName = method.getName();
        log.debug("securing call: '" + methodClass + "." + methodName + "'");

        if (!Securable.class.isAssignableFrom(o.getClass())) {
            log.warn("Unexpected object filtered: " + o.getClass().getName());
            return;
        }

        Securable securable = (Securable) o;
        try {
            Credential credential = securityContextUtil.getCurrentUser();
            securable.login(credential);

        } catch (NoUserLoggedInException e) {
            log.info("No user currently logged in.");
            securable.logout();
        }
    }
}
