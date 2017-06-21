## RxCacheLoaderHelper
### 1. 初始化
java:
```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RxCacheLoaderHelper.INSTANCE.init(getApplicationContext());

    }
}
```
kotlin:
```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        RxCacheLoaderHelper.init(getApplicationContext())
    }
}
```
### 2. 使用
java:
```java
        RxCacheLoaderHelper.INSTANCE
          .loadFromMemoryFirst(this,"http://xxxx", SongCategoriesResponse.class)
          .subscribe(new Subscriber<SongCategoriesResponse>() {
            @Override
            public void onCompleted() {
                Log.d("DADA","onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.d("DADA","onError:"+e.getMessage());
            }

            @Override
            public void onNext(SongCategoriesResponse songCategoriesResponse) {
                Log.d("DADA","songCategoriesResponse:"+songCategoriesResponse);
            }
        });
```
kotlin:
```kotlin
        RxCacheLoaderHelper
                .loadFromMemoryFirst(this, "http://xxxxx", SongCategoriesResponse::class.java)
                .subscribe(object:Subscriber<SongCategoriesResponse>(){
                    override fun onCompleted() {
                        Log.d("DADA","onCompleted")
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        Log.d("DADA","onError:"+e?.message)
                    }

                    override fun onNext(t: SongCategoriesResponse?) {
                        Log.d("DADA","loadFromMemoryFirst:"+t)
                    }
                })
```

### 3. 预设置策略
#### 3.1 LoadFromMemoryFirstPolicy
> load顺序 memory, disk , network
#### 3.2 LoadFromNetworkFirstPolicy
> load顺序 network, memory, disk
#### 3.3 LoadFromAllAtTheSameTimePolicy
> 同时从network,memory,disk获取数据，取值优先级为network>memory>disk

### 4. 自定义策略
> 实现`ILoaderPolicy`，使用RxCacheLoaderHelper$setDefaultCachePolicy 修改默认策略，或者使用load接口传入策略
### 5. 自定义加载器
> 实现`ICacheLoader`，使用RxCacheLoaderHelper$setLoaderPolicy修改默认loader(`请在调用init前设置默认loader`)，或者使用load接口传入loader(`注意这钟方法需要自行管理loader`)