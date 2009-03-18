
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.duraspace.mainwebapp.domain.model.Address;

public interface AddressRepository {

    public int saveAddress(Address addr) throws Exception;

    public Address findAddressById(int id) throws Exception;

    public List<Integer> getAddressIds() throws Exception;
}
