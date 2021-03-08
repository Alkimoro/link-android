package cn.linked.baselib.entity;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Cookie;

@Getter
@Setter
public class AppCookie {
    private String name;
    private String value;
    private long expiresAt;
    private String domain;
    private String path;
    private boolean secure;
    private boolean httpOnly;

    public Cookie toOkHttpCookie() {
        try {
            Cookie.Builder builder = new Cookie.Builder();
            builder.name(name)
                    .value(value)
                    .expiresAt(expiresAt)
                    .domain(domain)
                    .path(path);
            if (secure) {
                builder.secure();
            }
            if (httpOnly) {
                builder.httpOnly();
            }
            return builder.build();
        }catch (Exception e) {
            return null;
        }
    }

}
