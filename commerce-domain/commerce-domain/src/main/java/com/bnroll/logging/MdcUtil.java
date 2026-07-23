package com.bnroll.logging;


import org.slf4j.MDC;

public final class MdcUtil {

    private MdcUtil() {
    }

    public static void put(String key, Object value) {
        if (key == null || value == null) {
            return;
        }

        MDC.put(key, String.valueOf(value));
    }

    public static String get(String key) {
        return MDC.get(key);
    }

    public static void remove(String key) {
        MDC.remove(key);
    }

    public static void clear() {
        MDC.clear();
    }

    // Convenience methods

    public static void setRequestId(String requestId) {
        put(LoggingConstants.REQUEST_ID, requestId);
    }

    public static void setUserId(Object userId) {
        put(LoggingConstants.USER_ID, userId);
    }

    public static void setTenantId(Object tenantId) {
        put(LoggingConstants.TENANT_ID, tenantId);
    }

    public static void setWorkspaceId(Object workspaceId) {
        put(LoggingConstants.WORKSPACE_ID, workspaceId);
    }

    public static String getRequestId() {
        return get(LoggingConstants.REQUEST_ID);
    }
}