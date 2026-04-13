package com.electromart.service;

import com.electromart.dto.AddressDto;
import com.electromart.entity.Address;
import java.util.List;

public interface AddressService {

    AddressDto createAddress(AddressDto addressDto);

    List<AddressDto> getAddressesForCurrentUser();

    AddressDto updateAddress(Long id, AddressDto addressDto);

    void deleteAddress(Long id);

    Address getAddressEntityForCurrentUser(Long id);
}
