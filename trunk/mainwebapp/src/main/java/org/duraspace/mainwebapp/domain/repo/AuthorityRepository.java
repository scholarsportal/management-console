package org.duraspace.mainwebapp.domain.repo;

import org.duraspace.mainwebapp.domain.model.Authority;


public interface AuthorityRepository {

    void saveAuthority(Authority authority) throws Exception;

}
