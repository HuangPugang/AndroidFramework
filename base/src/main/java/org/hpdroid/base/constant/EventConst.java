package org.hpdroid.base.constant;

/**
 * Created by paul on 16/10/11.
 */

public interface EventConst {
    /**
     * 登录
     */
    int EVENT_LOGIN = 1000;

    /**
     * 退出
     */
    int EVENT_LOGOUT=1001;

	/**
	 * 刷新用户信息
     */
    int EVENT_REFRESH_USER_INFO = 1002;

    /**
     * 刷新token
     */
    int EVENT_TOKEN_REFRESH=1003;


    /**
     * TOKEN失效
     */
    int EVENT_TOKEN_ERROR=1004;

	/**
	 * 开锁成功
     */
    int EVENT_UNLOCK_SUCCESS = 1005;

}
