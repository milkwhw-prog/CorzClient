package ai.auth;

public final class TokenVerifyContext {
    public String expectedAudience;
    public String expectedIssuer;
    public String expectedDevice;

    public TokenVerifyContext(String issuer, String audience, String deviceDigest) {
    }
}
