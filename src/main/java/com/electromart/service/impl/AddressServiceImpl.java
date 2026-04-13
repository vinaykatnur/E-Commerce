package com.electromart.service.impl;

import com.electromart.dto.AddressDto;
import com.electromart.entity.Address;
import com.electromart.entity.User;
import com.electromart.exception.AppException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.repository.AddressRepository;
import com.electromart.repository.OrderRepository;
import com.electromart.service.AddressService;
import com.electromart.service.CurrentUserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public AddressDto createAddress(AddressDto addressDto) {
        User currentUser = currentUserService.getCurrentUser();
        boolean shouldBeDefault = Boolean.TRUE.equals(addressDto.getIsDefault())
                || !addressRepository.existsByUserId(currentUser.getId());

        if (shouldBeDefault) {
            clearDefaultAddress(currentUser.getId());
        }

        Address address = mapToEntity(addressDto, new Address());
        address.setUser(currentUser);
        address.setDefault(shouldBeDefault);
        return mapToDto(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesForCurrentUser() {
        Long userId = currentUserService.getCurrentUser().getId();
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public AddressDto updateAddress(Long id, AddressDto addressDto) {
        Address address = getAddressEntityForCurrentUser(id);
        boolean shouldBeDefault = Boolean.TRUE.equals(addressDto.getIsDefault());
        if (shouldBeDefault) {
            clearDefaultAddress(address.getUser().getId());
        }

        mapToEntity(addressDto, address);
        address.setDefault(shouldBeDefault);
        Address savedAddress = addressRepository.save(address);
        ensureAtLeastOneDefault(savedAddress.getUser().getId());
        return mapToDto(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        Address address = getAddressEntityForCurrentUser(id);
        Long userId = address.getUser().getId();
        if (orderRepository.existsByAddressIdAndUserId(id, userId)) {
            throw new AppException("This address is already linked to an order and cannot be deleted.");
        }
        boolean wasDefault = address.isDefault();
        addressRepository.delete(address);

        if (wasDefault) {
            List<Address> remainingAddresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
            if (!remainingAddresses.isEmpty()) {
                remainingAddresses.get(0).setDefault(true);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Address getAddressEntityForCurrentUser(Long id) {
        Long userId = currentUserService.getCurrentUser().getId();
        return addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found."));
    }

    private void clearDefaultAddress(Long userId) {
        addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .forEach(address -> address.setDefault(false));
    }

    private void ensureAtLeastOneDefault(Long userId) {
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        if (addresses.isEmpty()) {
            return;
        }
        boolean hasDefault = addresses.stream().anyMatch(Address::isDefault);
        if (!hasDefault) {
            addresses.get(0).setDefault(true);
        }
    }

    private Address mapToEntity(AddressDto dto, Address address) {
        address.setFullName(dto.getFullName());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPincode(dto.getPincode());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        return address;
    }

    private AddressDto mapToDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .fullName(address.getFullName())
                .phoneNumber(address.getPhoneNumber())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .isDefault(address.isDefault())
                .build();
    }
}
