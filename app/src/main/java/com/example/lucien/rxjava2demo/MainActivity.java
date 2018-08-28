package com.example.lucien.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aa();
        bb();
        cc();
        dd();
    }

    /**
     * Observer中 Disposable 的使用
     * Note:
     * 1. 调用Disposable的dispose()之后，Observer不再接收事件
     * 2. subscribe的参数可以是Observer，它包括四个方法。如果只关心这四个方法中的少数几个，则使用Consumer来代替Observer
     */
    public void aa(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "emit complete");
                emitter.onComplete();
                Log.d(TAG, "emit 4");
                emitter.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            private Disposable mDisposable;
            @Override
            public void onSubscribe(Disposable d) {
                this.mDisposable = d;
                Log.d(TAG,"Observer onSubscribe ");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG,"Observer onNext "+value);
                if(value == 2){
                    mDisposable.dispose();
                    Log.d(TAG,"Observer dispose "+value);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"Observer onError "+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Observer onComplete ");
            }
        });
    }

    public void bb(){
        //map
        Observable.create(new ObservableOnSubscribe<Integer>(){
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "convert to No."+integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG,s);
            }
        });
    }

    public void cc(){
        //flatmap 不保证事件产生顺序和接收顺序一致
        Observable.create(new ObservableOnSubscribe<Integer>(){
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return null;
            }
        }).subscribe(new Consumer<String>(){
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG,s);
            }
        });
    }

    public void dd(){
        //concatMap 保证事件产生顺序和接收顺序一致
        Observable.create(new ObservableOnSubscribe<Integer>(){
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return null;
            }
        }).subscribe(new Consumer<String>(){
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG,s);
            }
        });
    }

    /**
     * Flowable 必须设置BackpressureStrategy
     * Note:
     * 1. BackpressureStrategy.ERROR 时，如果Subscriber如何不能及时处理来自Flowable产生的事件，会抛出 MissingBackpressureException
     * 当在主线程会立刻抛出该异常，而子线程则会等到事件数量积累到128个后抛出该异常，因为要保证不会阻塞主线程
     *
     */
    public void ee(){
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {

            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
