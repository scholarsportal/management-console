package org.duracloud.mainwebapp.domain.repo;

import java.util.List;

import org.duracloud.mainwebapp.domain.model.Address;

public interface AddressRepository {

    public int saveAddress(Address addr) throws Exception;

    public Address findAddressById(int id) throws Exception;

    public List<Integer> getAddressIds() throws Exception;
}
