package com.csc.common.enums;

import com.csc.common.utils.character.PinYinUtils;

import java.util.Objects;

/**
 * @Description: 异常状态码
 * @Author: csc
 * @create: 2022/11/24
 */
public enum ApplicationStatus implements BaseEnums<Integer> {
    OK(0, "SUCCESS", "SUCCESS"),
    //100000所有默认异常
    EXCEPTION(100000, "网络异常，请稍后再试", "Network exception, please try again later."),
    ILLEGAL_ACCESS(100001, "非法访问", "Illegal Access"),
    ILLEGAL_ARGUMENT(100002, "非法参数", "Illegal Parameter"),
    ILLEGAL_DATA(100003, "非法数据", "Illegal Data"),

    //登录状态验证失败--只用于session、token
    ACCOUNT_NOT_EXIT(200003, "账号不存在", "Account does not exist."),
    PASSWORD_ERROR(200004, "账号/密码错误", ""),
    AUTH_EXCEPTION(200000, "登录状态已失效", "Login status verification failed."),

    ;


    /**
     * 状态码
     */
    private final int code;
    /**
     * 描述字段
     */
    private final String message;
    /**
     * 英文描述
     */
    private final String englishMessage;

    ApplicationStatus(int status, String message, String englishMessage) {
        this.code = status;
        this.message = message;
        this.englishMessage = englishMessage;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getMessage(String language) {
        return message;
        //return getMessage(LanguageType.getBy(language));
    }

    public String getMessage(LanguageType languageType) {
        if (Objects.isNull(languageType)) {
            return message;
        }
        if (LanguageType.ZH_HANT.equals(languageType)) {
            return PinYinUtils.convertToTraditionalChinese(message);
        }
        if (LanguageType.EN.equals(languageType)) {
            return this.englishMessage;
        }
        return message;
    }
}
