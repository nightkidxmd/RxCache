package com.dreamland.rxcache.utils.ext

import android.content.Context
import com.bluelinelabs.logansquare.LoganSquare

/**
 * Created by XMD on 2017/9/5.
 */
fun <T> Context.assetsParseJson(path:String,clazz: Class<T>):T =
        LoganSquare.parse(this.assets.open(path),clazz)

fun <T> Context.assetsParseJsonList(path:String,clazz: Class<T>):MutableList<T> =
        LoganSquare.parseList(this.assets.open(path),clazz)

fun <T> Context.assetsParseJsonMap(path:String,clazz: Class<T>):MutableMap<String,T> =
        LoganSquare.parseMap(this.assets.open(path),clazz)