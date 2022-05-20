package uz.gxteam.variant.interceptor

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import uz.gxteam.variant.utils.AppConstant.ACCESSTOKEN
import uz.gxteam.variant.utils.AppConstant.COMPANYNAME
import uz.gxteam.variant.utils.AppConstant.EMPTYTEXT
import uz.gxteam.variant.utils.AppConstant.LANGUAGE
import uz.gxteam.variant.utils.AppConstant.OLDTOKEN
import uz.gxteam.variant.utils.AppConstant.PASSWORDAPP
import uz.gxteam.variant.utils.AppConstant.REFRESHTOKEN
import uz.gxteam.variant.utils.AppConstant.THEME
import uz.gxteam.variant.utils.AppConstant.THEMECOLOR
import uz.gxteam.variant.utils.AppConstant.TOKENTYPE
import uz.gxteam.variant.utils.AppConstant.USERDATA
import javax.inject.Inject

/** this is class SharedPreference create and save storage data accessToken and refreshToken and token type **/

class MySharedPreference @Inject constructor(@ApplicationContext private val context: Context){
    private val NAME = COMPANYNAME
    private val MODE = Context.MODE_PRIVATE
    private val preferences: SharedPreferences = context.getSharedPreferences(NAME, MODE)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    /** clear sharedPreference **/
    fun clear(){
        var edite = preferences.edit()
        edite.remove(ACCESSTOKEN)
        edite.remove(REFRESHTOKEN)
        edite.remove(TOKENTYPE)
        edite.remove(TOKENTYPE)
        edite.apply()
    }

    /** passwordApp **/
    var passwordApp: String?
        get() = preferences.getString(PASSWORDAPP, EMPTYTEXT)
        set(value) = preferences.edit {
            if (value != null) {
                it.putString(PASSWORDAPP, value)
            }
        }

    /** passwordApp **/

    /** save accessToken **/
    var accessToken: String?
        get() = preferences.getString(ACCESSTOKEN, EMPTYTEXT)
        set(value) = preferences.edit {
            if (value != null) {
                it.putString(ACCESSTOKEN, value)
            }
        }
    /** save refreshToken **/
    var refreshToken: String?
        get() = preferences.getString(REFRESHTOKEN, EMPTYTEXT)
        set(value) = preferences.edit {
            if (value != null) {
                it.putString(REFRESHTOKEN, value)
            }
        }
    /** save tokenType **/
    var tokenType: String?
        get() = preferences.getString(TOKENTYPE, EMPTYTEXT)
        set(value) = preferences.edit {
            if (value != null) {
                it.putString(TOKENTYPE, value)
            }
        }

    /** User Data **/
    var userData: String?
        get() = preferences.getString(USERDATA, EMPTYTEXT)
        set(value) = preferences.edit {
            if (value != null) {
                it.putString(USERDATA, value)
            }
        }
    /** User Data **/

    /** User Data **/
    var oldToken: String?
        get() = preferences.getString(OLDTOKEN, EMPTYTEXT)
        set(value) = preferences.edit {
            if (value != null) {
                it.putString(OLDTOKEN, value)
            }
        }
    /** User Data **/


    var language: String?
        get() = preferences.getString(LANGUAGE, "uz")
        set(value) = preferences.edit {
            if (value != null) {
                it.putString(LANGUAGE, value)
            }
        }


    var theme: Boolean?
        get() = preferences.getBoolean(THEME, false)
        set(value) = preferences.edit {
            if (value != null) {
                it.putBoolean(THEME, value)
            }
        }



    var theme_color: String?
        get() = preferences.getString(THEMECOLOR, EMPTYTEXT)
        set(value) = preferences.edit {
            if (value != null) { it.putString(THEMECOLOR, value) }
        }
}