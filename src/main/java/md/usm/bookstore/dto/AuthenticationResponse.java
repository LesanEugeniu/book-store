package md.usm.bookstore.dto;

public record AuthenticationResponse(String accessToken, String refreshToken) {
}
