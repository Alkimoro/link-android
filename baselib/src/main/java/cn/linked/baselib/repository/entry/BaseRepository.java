package cn.linked.baselib.repository.entry;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.linked.commonlib.promise.Promise;

public class BaseRepository {

    private static ExecutorService executorService;
    public static ExecutorService getExecutorService() {
        if(executorService == null) {
            synchronized (BaseRepository.class) {
                if(executorService == null) {
                    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 10,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(20), new ThreadPoolExecutor.AbortPolicy());
                    threadPoolExecutor.allowCoreThreadTimeOut(true);
                    executorService = threadPoolExecutor;
                }
            }
        }
        return executorService;
    }

    protected void postTask(@NonNull Runnable runnable, Promise<?> promise) {
        try{
            getExecutorService().submit(runnable);
        }catch (RejectedExecutionException e) {
            if(promise != null) { promise.reject(e); }
        }
    }

    protected void postTask(@NonNull Runnable runnable) {
        postTask(runnable, null);
    }

}
