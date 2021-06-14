/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import org.duracloud.account.db.model.RabbitMQConfig;
import org.duracloud.account.db.repo.RabbitMQConfigRepo;
import org.duracloud.account.db.util.RabbitMQConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A service for modifying RabbitMQ settings.
 *
 * @author Andy Foster
 * Date: 2021/05/06
 */
@Component("rabbitMQConfigService")
public class RabbitMQConfigServiceImpl implements RabbitMQConfigService {

    @Autowired
    private RabbitMQConfigRepo repo;

    public void setRepo(RabbitMQConfigRepo repo) {
        this.repo = repo;
    }

    public RabbitMQConfigRepo getRepo() {
        return repo;
    }

    @Override
    public RabbitMQConfig get(Long id) {
        RabbitMQConfig entity = repo.findOne(id);
        return entity;
    }

    @Override
    public void set(Long id,
                    String host,
                    Integer port,
                    String vhost,
                    String username,
                    String password) {
        RabbitMQConfig rmqc = get(id);
        if (null == rmqc) {
            rmqc = new RabbitMQConfig();
        }

        rmqc.setId(id);
        rmqc.setHost(host);
        rmqc.setPort(port);
        rmqc.setVhost(vhost);
        rmqc.setUsername(username);
        rmqc.setPassword(password);

        repo.save(rmqc);
    }
}
