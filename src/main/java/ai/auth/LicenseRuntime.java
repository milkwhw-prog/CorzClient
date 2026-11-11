package ai.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.PublicKey;
import net.fabricmc.loader.api.ModContainer;
import ai.auth.LicenseToken;
import ai.auth.LicenseStatus;
import ai.auth.ApiResponse;
import ai.auth.TokenVerifyContext;

public final class LicenseRuntime {
    private static final long CACHE_TTL_SECONDS = 3600L;

    public static String clientVersion;
    public static String apiBase;
    public static String configDir;
    public static String pubKeySpkiB64;
    static Path clientVersionPath;

    private static volatile String authToken;
    private static volatile LicenseToken currentToken;
    private static volatile String deviceId;
    private static volatile LicenseStatus currentStatus;
    private static volatile String cachedResponseBody;
    private static volatile boolean activating;
    private static String lastErrorMessage;
    private static PublicKey publicKey;
    private static Gson gson;

    private LicenseRuntime() {
    }

    private static void tick() {
    }

    private static String getModVersion(ModContainer modContainer) {
        return null;
    }

    public static ApiResponse sendRequest(String string, JsonObject jsonObject) {
        return null;
    }

    public static void shutdown() {
    }

    public static String getClientVersion() {
        return "XenntSquad";
    }

    public static LicenseStatus getLicenseStatus() {
        return null;
    }

    public static boolean isActivated() {
        return true;
    }

    public static void refresh() {
    }

    public static void configure(Path path, String string, String string2, String string3) {
    }

    public static void sync() {
    }

    public static boolean isLicenseValid() {
        return true;
    }

    public static void activateLicense(String string) {
    }

    public static Path getLicenseFilePath() {
        return null;
    }

    public static PublicKey parsePublicKey(String string) {
        return null;
    }

    public static long getLastSyncTime() {
        return 0L;
    }

    public static JsonObject loadLicenseFile() {
        return null;
    }

    public static TokenVerifyContext getVerifyContext() {
        return null;
    }

    public static String getLicenseOwnerLabel() {
        return "Corz User";
    }

    public static void initialize() {
    }

    public static void logError(String string) {
    }

    public static String getStatusMessage() {
        return null;
    }

    public static boolean isPremiumUnlocked() {
        return true;
    }

    public static boolean hasFeature(String string) {
        return true;
    }

    public static String getVersionLabel() {
        return null;
    }

    public static void clearError() {
    }

    public static void handleResponse(ApiResponse apiResponse) {
    }

    public static String getAuthToken() {
        return null;
    }

    public static void poll() {
    }

    public static String readStream(InputStream inputStream) {
        return null;
    }
}
