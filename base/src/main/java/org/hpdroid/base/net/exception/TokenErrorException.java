package org.hpdroid.base.net.exception;

import org.hpdroid.base.common.bean.EventMessage;
import org.hpdroid.base.constant.EventConst;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by paul on 16/10/31.
 */

public class TokenErrorException extends RuntimeException {

    public TokenErrorException(String message) {
        super(message);
        EventMessage eventMessage = new EventMessage(EventConst.EVENT_TOKEN_ERROR);
        EventBus.getDefault().post(eventMessage);
    }
}
