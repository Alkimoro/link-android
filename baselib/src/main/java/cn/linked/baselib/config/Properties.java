package cn.linked.baselib.config;

import java.io.File;

public class Properties {

    public static final String HIPPY_JS_DIR = "hippyJsSource"+ File.pathSeparator;
    public static final String APP_DATABASE_NAME = "link";

    public static final boolean DEBUG = true;
    public static final boolean ENABLE_HIPPY_ENGINE = false;

    public static final String HTTP_SERVER_URL = "https://www.xxx.com/";
    public static final String HTTP_SERVER_DEBUG_URL = "https://10.0.2.2:8443/";

    public static final String CHAT_CLIENT_HOST = "49.234.70.153";
    public static final int CHAT_CLIENT_PORT = 8090;
    public static final String CHAT_CLIENT_DEBUG_HOST = "10.0.2.2";
    public static final int CHAT_CLIENT_DEBUG_PORT = 8090;

    public static String getBaseURL() {
        String baseUrl = null;
        if(Properties.DEBUG) { baseUrl = Properties.HTTP_SERVER_DEBUG_URL; }
        else { baseUrl = Properties.HTTP_SERVER_URL; }
        return baseUrl;
    }

}
