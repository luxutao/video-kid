package me.m123.video.utils


import android.content.Context
import android.util.Base64
import android.util.Log
import me.m123.video.data.UserInfoToken
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.parseSingle
import org.jetbrains.anko.db.select
import java.util.regex.Pattern

object ToolsHelper {


    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    fun isEmailValid(email: String): Boolean {
        var isValid = false

        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"

        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        if (matcher.matches()) {
            isValid = true
        }
        return isValid
    }

    fun getTicket(path: String): String {
        val enstr = "LRnS4t"
        val timestamp = System.currentTimeMillis() / 1000
        val token = Base64.encodeToString((path+"-"+timestamp.toString()+"-"+enstr).toByteArray(), Base64.DEFAULT)
        return token
    }

    fun getToken(ctx: Context): String {
        val userinfo = ctx.database.use {
            select("users","userid","token","username","last_name","first_name","email","gender","avatar","date_joined","last_login").exec {
                val itemlist: List<UserInfoToken> = parseList(classParser())
                return@exec itemlist
            }
        }
        if (userinfo.count() >= 1) {
            Log.d("userinfo", userinfo.first().toString())

            return "Token " + userinfo.first().token
        }
        return ""
    }
}