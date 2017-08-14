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
          .load(this,URI.create("http://xxxx"), null, SongCategoriesResponse.class,new LoadFromMemoryFirstPolicy())
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
                .loadFromMemoryFirst(context = this, uri = URI.create("http://xxxx"),clazz = SongCategoriesResponse::class.java)
                .subscribe(
                { t ->  Log.e("DADA","loadFromMemoryFirst:"+t) }, 
                {  e->  e?.printStackTrace()
                   Log.e("DADA","onError:"+e?.message) }, 
                   {     Log.e("DADA","onCompleted") })
```
#### 2.2 Get Image
java:
```java
        RxCacheLoaderHelper.INSTANCE
                .loadImage(this, URI.create("http://xxxx"),URI.create("file:///sdcard/xxxxx"), imageView,R.drawable.defualt_icon);
```
kotlin:
```kotlin
        RxCacheLoaderHelper
                .loadImage(this, URI.create("http://xxxx"),URI.create("assets:///xxxx"), imageView,R.drawable.defualt_icon)
```
### 3. Pre-Policy
#### 3.1 LoadFromMemoryFirstPolicy
> load sequence: memory, disk , network

#### 3.2 LoadFromNetworkFirstPolicy
> load sequence: network, memory, disk

#### 3.3 LoadFromAllAtTheSameTimePolicy
> load from network,memory,disk at the same time and he priority to pick data is network>memory>disk

#### 3.4 LoadDiskOnlyPolicy
> load only from disk

#### 3.5 LoadLocalAndUpdateFromNetwork
> load from local and network at the same time, but only local value will be emitted to user, the net result is only to update local file and will be used next time.

### 4. Custom Made Policy
> implement `ILoaderPolicy`，and set the policy with RxCacheLoaderHelper$setDefaultCachePolicy to modify default policy or pass your policy when call load

### 5. Custom Made Loader
> implement`ICacheLoader`，and modify the default load with RxCacheLoaderHelper$setLoaderPolicy(`call it before init`) or pass your loader when call load(if so you need to manage the loader by yourself)