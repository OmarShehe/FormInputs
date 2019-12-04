package com.omarshehe.forminputkotlin.utils

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * Created by omars on 11/5/2019.
 * Author omars
 */
object Density {
    fun dp2px(resources: Resources, dp: Float): Int {
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        return px.roundToInt()
    }
}