package cn.linked.baselib.repository;

import androidx.annotation.NonNull;

import java.util.concurrent.ConcurrentHashMap;

import cn.linked.baselib.config.Properties;
import cn.linked.baselib.repository.entry.BaseRepository;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private static Retrofit INSTANCE;
    private static ConcurrentHashMap<Class<?>, Object> serviceMap = new ConcurrentHashMap<>();

    public static Retrofit getRetrofit() {
        if(INSTANCE == null) {
            synchronized (RetrofitManager.class) {
                if(INSTANCE == null) {
                    INSTANCE = init();
                }
            }
        }
        return INSTANCE;
    }

    private static Retrofit init() {
        return new Retrofit.Builder()
                .baseUrl(Properties.getBaseURL())
                .client(OkHttpClientManager.getOkHttpClient())
                .callbackExecutor(BaseRepository.getExecutorService())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T getService(@NonNull Class<T> service) {
        if(serviceMap.containsKey(service)) {
            return (T) serviceMap.get(service);
        }else {
            T serviceInstance = getRetrofit().create(service);
            serviceMap.put(service, serviceInstance);
            return serviceInstance;
        }
    }

}
