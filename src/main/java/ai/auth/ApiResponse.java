package ai.auth;

import com.google.gson.JsonObject;

public final class ApiResponse {
    private JsonObject body;
    private int status;

    private ApiResponse(int status, JsonObject body) {
    }

    public int status() {
        return 0;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }

    public JsonObject body() {
        return null;
    }

    @Override
    public boolean equals(Object object) {
        return true;
    }
}
