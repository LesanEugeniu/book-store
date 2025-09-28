package md.usm.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentDto(

        Long orderId,

        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
        String cardNumber,

        @NotBlank(message = "Card holder name is required")
        String cardHolder,

        @NotBlank(message = "Expiry month is required")
        @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Expiry month must be between 01 and 12")
        String expiryMonth,

        @NotBlank(message = "Expiry year is required")
        @Pattern(regexp = "^(\\d{2}|\\d{4})$", message = "Expiry year must be two or four digits")
        String expiryYear,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
        String cvv,

        @NotBlank(message = "Zip code is required")
        @Pattern(regexp = "\\d{4,10}", message = "Zip code must be between 4 and 10 digits")
        String zipCode
) {
}
