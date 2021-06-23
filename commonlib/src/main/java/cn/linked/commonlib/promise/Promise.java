package cn.linked.commonlib.promise;

/**
 *  T 表示传入该Promise的值类型
 *  V 表示该Promise执行callback后返回的类型
 *
 *  fulfilled --> fulfilledCallback ---执行成功-- fulfilled
 *                                             |
 *                                             --出现异常-- rejected
 * */
public class Promise<V> {

    private PromiseState state = PromiseState.PENDING;

    private PromiseCallback fulfilledCallback;
    private PromiseCallback rejectedCallback;

    private V value;
    private Throwable error;
    private Promise<?> nextCallback;

    public Promise() { }

    public static <U> Promise<U> resolvePromise(U value) {
        Promise<U> promise = new Promise<>();
        promise.doCallback(value, null, PromiseState.FULFILLED);
        return promise;
    }

    public static <U> Promise<U> rejectPromise(Throwable value) {
        Promise<U> promise = new Promise<>();
        promise.doCallback(null, value, PromiseState.REJECTED);
        return promise;
    }

    public Promise<V> resolve(V value) {
        if(this.state == PromiseState.PENDING) {
            this.doCallback(value, null, PromiseState.FULFILLED);
        }
        return this;
    }

    public Promise<V> reject(Throwable value) {
        if(this.state == PromiseState.PENDING) {
            this.doCallback(null, value, PromiseState.REJECTED);
        }
        return this;
    }

    private void setState(PromiseState state) {
        synchronized (this) {
            this.state = state;
            this.notifyAll();
        }
    }

    public Promise<V> sync() {
        if(this.state == PromiseState.PENDING) {
            synchronized (this) {
                try {
                    if(this.state == PromiseState.PENDING) {
                        this.wait();
                    }
                }catch (InterruptedException ignored) { }
            }
        }
        return this;
    }

    public <Y> Promise<Y> catchError(PromiseCallback<Throwable,Y> reject) {
        return then(null, reject);
    }

    public <Y> Promise<Y> then(PromiseCallback<V,Y> resolve) {
        return then(resolve, null);
    }

    public <Y> Promise<Y> then(PromiseCallback<V,Y> resolve, PromiseCallback<Throwable,Y> reject) {
        Promise<Y> promise = new Promise<>();
        promise.fulfilledCallback = resolve;
        promise.rejectedCallback = reject;
        this.nextCallback = promise;
        if(this.state != PromiseState.PENDING) {
            promise.doCallback(this.value, this.error, this.state);
        }
        return promise;
    }

    private <T> void doCallback(T value,Throwable error, PromiseState preState) {
        PromiseCallback<?,V> callback = null;
        if(preState == PromiseState.FULFILLED) { callback = fulfilledCallback; }
        else if(preState == PromiseState.REJECTED) { callback = rejectedCallback; }
        if(callback != null) {
            try {
                if(preState == PromiseState.FULFILLED) { this.value = (V) this.fulfilledCallback.callback(value); }
                else { this.value = (V) this.rejectedCallback.callback(error); }
                setState(PromiseState.FULFILLED);
            }catch (Throwable e) {
                this.error = e;
                setState(PromiseState.REJECTED);
            }
        }else {
            setState(preState);
            if(this.state == PromiseState.FULFILLED) { this.value = (V) value; }
            else if(this.state == PromiseState.REJECTED) { this.error = error; }
        }
        if(nextCallback != null) {
            nextCallback.doCallback(this.value, this.error, this.state);
        }
    }

    public interface PromiseCallback<T,V> {
        V callback(T value);
    }

}
