package ai.auth;

import com.google.gson.JsonObject;
import java.security.PublicKey;
import java.util.Set;
import java.util.regex.Pattern;
import ai.auth.TokenVerifyContext;

public final class LicenseToken {
    public static final int CURRENT_VERSION = 1;
    private static final long MAX_EXPIRY_EPOCH = 4102444800L;
    private static final long MIN_ISSUED_EPOCH = 1704067200L;
    private static final int MAX_TOKEN_LENGTH = 8192;

    public String device;
    public String audience;
    public String issuer;
    public String tokenVersion;
    public String clientVersion;
    public String exp;
    public String iat;
    public long activationId;
    public long licenseExpiresAt;
    public int type;
    public Long features;
    public Set<String> licenseId;

    private static Pattern tokenPattern;
    private static Pattern devicePattern;
    private static Pattern audiencePattern;

    private LicenseToken(int type, String device, String audience, String tokenVersion, String iat,
            String exp, String clientVersion, long activationId, long licenseExpiresAt, Long features,
            String issuer, Set<String> licenseId) {
    }

    public static int getInt(JsonObject jsonObject, String string) {
        return 0;
    }

    public boolean isExpired(long l) {
        return true;
    }

    public static String decodePayload(String string, PublicKey publicKey) {
        return null;
    }

    public boolean isNotYetValid(long l) {
        return true;
    }

    public static boolean isValidFormat(String string) {
        return true;
    }

    public static long getLong(JsonObject jsonObject, String string) {
        return 0L;
    }

    public static String getString(JsonObject jsonObject, String string) {
        return null;
    }

    public static LicenseToken parse(String string, PublicKey publicKey, TokenVerifyContext tokenVerifyContext) {
        return null;
    }

    public boolean isWithinGracePeriod(long l) {
        return true;
    }

    public boolean hasFeature(String string) {
        return true;
    }
}
