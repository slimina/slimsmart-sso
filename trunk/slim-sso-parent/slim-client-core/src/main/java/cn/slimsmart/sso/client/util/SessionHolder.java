package cn.slimsmart.sso.client.util;

import cn.slimsmart.sso.client.session.UserSession;



public class SessionHolder {

    private static final ThreadLocal<UserSession> threadLocal = new ThreadLocal<UserSession>();


    public static UserSession getUserSession() {
        return threadLocal.get();
    }

    public static void setUserSession(final UserSession userSession) {
        threadLocal.set(userSession);
    }

    public static void clear() {
        threadLocal.set(null);
    }
}
