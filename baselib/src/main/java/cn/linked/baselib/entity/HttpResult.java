package cn.linked.baselib.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpResult<T> {

    public static final int CODE_SUCCESS = 1000;
    public static final int CODE_FAIL = 1001;
    public static final int CODE_SESSION_INVALID = 1002;

    /**
     * 1000 成功
     * 1001 失败
     * 1002 session过期
     */
    private int code = CODE_FAIL;

    private String msg;
    private String redirectURI;
    private T data;

}
