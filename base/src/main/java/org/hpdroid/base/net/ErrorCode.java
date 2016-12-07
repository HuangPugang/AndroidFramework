package org.hpdroid.base.net;

/**
 * Created by paul on 16/10/31.
 */

public enum ErrorCode {

    kSuccess(0,"请求成功"),
    kParamError(1,"参数不正确"),
    kTokenInvalid(2,"token失效"),
    kUnLogin(3,"用户未登录"),
    kSignInvalid(4,"签名无效"),


    kBikeNotExist(1100,"车辆不存在"),
    kBikeInUse(1101,"车辆被使用"),
    kBikeInRepair(1102,"车辆维修中"),
    kOrderNotExist(1103,"订单不存在"),

    //bike-api
    kScanOpen(1200,"请扫码开锁"),
    kInsufficientBalance(1201,"余额不足"),
    kUnAutonym(1202,"用户未实名认证"),
    kPledgeLack(1203,"用户押金不存在"),

    //user-api
    kJiYanInvalid(1300,"极验校验失败"),
    kPhoneFormInvalid(1301,"手机号码格式不正确"),
    kVCodeInvalids(1302,"验证码不正确"),
    kIDApproveFailed(1303,"身份认证失败"),
    kIDApproveOverTime(1304,"当天身份认证次数超过限制"),
    kIDApproveOnlyOne(1305,"当天您还剩一次身份认证机会")


    ;

    private int code;
    private String desc;

    ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
