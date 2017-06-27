## RxCacheLoaderHelper
[TOC]
### 1. Initializing
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
### 2. Usage
#### 2.1 get text content (Json format)
`used Logansquare`
>Need add logansquare supported<br>
>[Click to check](https://github.com/bluelinelabs/LoganSquare)

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
#### 2.2 Get Image
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
### 3. Pre-Policy
#### 3.1 LoadFromMemoryFirstPolicy
> load sequence: memory, disk , network
#### 3.2 LoadFromNetworkFirstPolicy
> load sequence: network, memory, disk
#### 3.3 LoadFromAllAtTheSameTimePolicy
> load from network,memory,disk at the same time and he priority to pick data is network>memory>disk

### 4. Custom Made Policy
> implement `ILoaderPolicy`，and set the policy with RxCacheLoaderHelper$setDefaultCachePolicy to modify default policy or pass your policy when call load
### 5. Custom Made Loader
> implement`ICacheLoader`，and modify the default load with RxCacheLoaderHelper$setLoaderPolicy(`call it before init`) or pass your loader when call load(if so you need to manage the loader by yourself)