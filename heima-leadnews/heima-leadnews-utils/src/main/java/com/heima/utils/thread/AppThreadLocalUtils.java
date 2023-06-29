package com.heima.utils.thread;

import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;

public class AppThreadLocalUtils {
    private final static ThreadLocal<ApUser> AP_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 存入线程中
     * @param wmUser
     */
    public static void setUser(ApUser apUser) {
        AP_USER_THREAD_LOCAL.set(apUser);
    }

    /**
     * 从线程中获取
     * @return
     */
    public static ApUser getUser() {
        return AP_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理全部
     */
    public static void clear() {
        AP_USER_THREAD_LOCAL.remove();
    }
}
