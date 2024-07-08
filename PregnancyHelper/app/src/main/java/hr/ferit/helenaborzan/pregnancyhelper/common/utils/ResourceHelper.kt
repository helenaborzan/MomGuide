package hr.ferit.helenaborzan.pregnancyhelper.common.utils

import android.content.Context

class ResourceHelper(private val context : Context) {
    fun getStringFromResource(id: Int): String {
        return context.getString(id)
    }
}