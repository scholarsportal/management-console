/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duracloud.account.db.util;

import org.duracloud.account.db.model.RabbitMQConfig;
import org.springframework.security.access.annotation.Secured;

/**
 * RabbitMQ Configuration management.
 *
 * @author Andy Foster
 * Date: 2021/05/06
 */
public interface RabbitMQConfigService {

    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public RabbitMQConfig get(Long id);

    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public void set(Long id,
                    String host,
                    Integer port,
                    String vhost,
                    String username,
                    String password);

}
