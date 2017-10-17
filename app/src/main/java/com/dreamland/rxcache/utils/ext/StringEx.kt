package com.dreamland.rxcache.utils.ext

import com.bluelinelabs.logansquare.LoganSquare
import java.net.URLDecoder
import java.net.URLEncoder


/**
 * Created by XMD on 2017/8/22.
 */

/**
 * capitalize first letter
 *
 * <pre>
 * capitalizeFirstLetter(null)     =   null;
 * capitalizeFirstLetter("")       =   "";
 * capitalizeFirstLetter("2ab")    =   "2ab"
 * capitalizeFirstLetter("a")      =   "A"
 * capitalizeFirstLetter("ab")     =   "Ab"
 * capitalizeFirstLetter("Abc")    =   "Abc"
 * </pre>
 *
 */
fun String.capitalizeFirstLetter(): String {
    if (isEmpty()) {
        return this
    }
    val c = this[0]
    return if ((c.isLetter() && !c.isUpperCase())) c + this.substring(1) else this
}

fun String.urlEncode(charset: String = "UTF-8") = if (isEmpty()) {
    this
} else {
    URLEncoder.encode(this, charset)
}

fun String.urlDecode(charset: String = "UTF-8") = if (isEmpty()) {
    this
} else {
    URLDecoder.decode(this,charset)
}

/**
 * Using logansquare
 */
fun <T> String.parseJson(clazz: Class<T>): T = LoganSquare.parse(this, clazz)

/**
 * Using logansquare
 */
fun <T> String.parseJsonList(clazz: Class<T>): MutableList<T> = LoganSquare.parseList(this, clazz)

/**
 * Using logansquare
 */
fun <T> String.parseJsonMap(clazz: Class<T>): MutableMap<String, T> = LoganSquare.parseMap(this, clazz)

/**
 * beauty json format
 */
fun String.jsonFormat(initLevel: Int = 0): String {
    var level = initLevel
    val jsonForMatStr = StringBuilder()
    var quotation = false
    if (this.isNotEmpty()) {
        for (i in 0 until this.length) {
            val c = this[i]
            if (level > 0 && jsonForMatStr.isNotEmpty() && '\n' == jsonForMatStr[jsonForMatStr.length - 1]) {
                jsonForMatStr.append(getLevelStr(level))
            }
            when (c) {
                '"' -> {
                    quotation = !quotation
                    jsonForMatStr.append(c)
                }
                '{', '[' -> {
                    if (!quotation) {
                        jsonForMatStr.append(c + "\n")
                        level++
                    } else {
                        jsonForMatStr.append(c)
                    }
                }
                ',' -> {
                    if (!quotation) {
                        jsonForMatStr.append(c + "\n")
                    } else {
                        jsonForMatStr.append(c)
                    }
                }
                '}', ']' -> {
                    if (!quotation) {
                        jsonForMatStr.append("\n")
                        level--
                        jsonForMatStr.append(getLevelStr(level))
                        jsonForMatStr.append(c)
                    } else {
                        jsonForMatStr.append(c)
                    }

                }
                else -> jsonForMatStr.append(c)
            }
        }
    }
    return jsonForMatStr.toString()
}

private fun getLevelStr(level: Int): String {
    val levelStr = StringBuilder()
    for (levelI in 0 until level) {
        levelStr.append("\t")
    }
    return levelStr.toString()
}
