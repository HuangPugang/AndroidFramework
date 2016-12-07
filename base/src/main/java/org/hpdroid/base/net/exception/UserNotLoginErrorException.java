package org.hpdroid.base.net.exception;

import org.hpdroid.base.common.bean.EventMessage;
import org.hpdroid.base.constant.EventConst;
import org.hpdroid.util.ToastHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by luoliuqing on 16/11/7.
 */
public class UserNotLoginErrorException extends RuntimeException {

	public UserNotLoginErrorException(String message) {
		super(message);
		EventMessage eventMessage = new EventMessage(EventConst.EVENT_LOGOUT);
		EventBus.getDefault().post(eventMessage);
		ToastHelper.showMessage(message);
	}
}