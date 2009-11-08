package org.duracloud.duradmin.services;

import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.control.ControllerSupport;


public class ServicesManagerFactory {
    public ServicesManager create() throws Exception{
        return new ControllerSupport().getServicesManager();
    }
}
