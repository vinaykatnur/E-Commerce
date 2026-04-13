package com.electromart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long id;
    private Long userId;

    @NotBlank(message = "Full name is required.")
    private String fullName;

    @NotBlank(message = "Phone number is required.")
    private String phoneNumber;

    @NotBlank(message = "Address line 1 is required.")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required.")
    private String city;

    @NotBlank(message = "State is required.")
    private String state;

    @NotBlank(message = "Pincode is required.")
    @Pattern(regexp = "\\d{6}", message = "Pincode must be a 6-digit number.")
    private String pincode;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "Default flag is required.")
    private Boolean isDefault;
}
