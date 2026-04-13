package com.electromart.controller;

import com.electromart.dto.AddressDto;
import com.electromart.service.AddressService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public AddressDto createAddress(@Valid @RequestBody AddressDto addressDto) {
        log.info("Create address API called for pincode {}", addressDto.getPincode());
        AddressDto response = addressService.createAddress(addressDto);
        log.info("Create address API completed with id {}", response.getId());
        return response;
    }

    @GetMapping
    public List<AddressDto> getAddresses() {
        log.info("List addresses API called");
        List<AddressDto> response = addressService.getAddressesForCurrentUser();
        log.info("List addresses API completed with {} addresses", response.size());
        return response;
    }

    @PutMapping("/{id}")
    public AddressDto updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDto addressDto) {
        log.info("Update address API called for id {}", id);
        AddressDto response = addressService.updateAddress(id, addressDto);
        log.info("Update address API completed for id {}", id);
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        log.info("Delete address API called for id {}", id);
        addressService.deleteAddress(id);
        log.info("Delete address API completed for id {}", id);
    }
}
