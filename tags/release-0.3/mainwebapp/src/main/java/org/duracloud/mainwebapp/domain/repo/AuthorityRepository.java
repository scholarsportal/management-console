package org.duracloud.mainwebapp.domain.repo;

import org.duracloud.mainwebapp.domain.model.Authority;

public interface AuthorityRepository {

    void saveAuthority(Authority authority) throws Exception;

}
