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
#### 2.1 文本配置获取(Json格式)
`默认使用Logansquare`
>需要在gradle中添加相关支持以及bean文件添加相关注解

java:
```java
        RxCacheLoaderHelper.INSTANCE
          .loadFromMemoryFirst(this,"http://xxxx", SongCategoriesResponse.class)
          .subscribe(new Subscriber<Object>() {
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
            public void onNext(Object obj) {
               SongCategoriesResponse songCategoriesResponse = (SongCategoriesResponse)obj
               Log.d("DADA","songCategoriesResponse:"+songCategoriesResponse);
            }
        });
```
kotlin:
```kotlin
        RxCacheLoaderHelper
                .loadFromMemoryFirst(this, "http://xxxxx", SongCategoriesResponse::class.java)
                .subscribe(
                { t -> if( t is SongCategoriesResponse ) Log.e("DADA","loadFromMemoryFirst:"+t) }, 
                {  e->  e?.printStackTrace()
                   Log.e("DADA","onError:"+e?.message) }, 
                   {     Log.e("DADA","onCompleted") })
```
#### 2.2 图片获取
java:
```java
        RxCacheLoaderHelper.INSTANCE
                .loadImage(this, "http://xxxxx", imageView,R.drawable.defualt_icon);
```
kotlin:
```kotlin
        RxCacheLoaderHelper
                .loadImage(this, "http://xxxxx", imageView,R.drawable.defualt_icon)
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