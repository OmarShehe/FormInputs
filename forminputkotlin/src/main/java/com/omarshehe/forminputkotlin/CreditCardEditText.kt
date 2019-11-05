package com.omarshehe.forminputkotlin

import android.content.Context
import android.graphics.Canvas
import android.util.SparseArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern


/**
 * Created by omars on 10/16/2019.
 * Author omars
 */
class CreditCardEditText : TextInputEditText {


    private var mCCPatterns: SparseArray<Pattern>? = null
    private val mSeparator = ' '
    private val mDefaultDrawableResId = R.drawable.ic_close_grey //default credit card image
    private var mCurrentDrawableResId = 0
    private var mCurrentDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        init()

    }


    private fun init() {
        if (mCCPatterns == null) {
            mCCPatterns = SparseArray<Pattern>()
            // Without spaces for credit card masking
            mCCPatterns!!.put(R.drawable.ic_close_to_done, Pattern.compile("^4[0-9]{2,12}(?:[0-9]{3})?$"))
            mCCPatterns!!.put(R.drawable.ic_close_grey, Pattern.compile("^5[1-5][0-9]{1,14}$"))
            mCCPatterns!!.put(R.drawable.ic_close_grey, Pattern.compile("^3[47][0-9]{1,13}$"))

        }

    }


    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        if (mCCPatterns == null) {
            init()
        }

        this.setError("Error ",ContextCompat.getDrawable(context,R.drawable.check_green))
        this.setBackgroundResource(R.drawable.bg_txt_square)

        var mDrawableResId = 0
        for (i in 0 until mCCPatterns!!.size()) {
            val key = mCCPatterns!!.keyAt(i)
            // get the object by the key.
            val p = mCCPatterns!!.get(key)
            val m = p.matcher(text)
            if (m.find()) {
                mDrawableResId = key
                break
            }

        }

        if (mDrawableResId > 0 && mDrawableResId != mCurrentDrawableResId) {
            mCurrentDrawableResId = mDrawableResId
        } else if (mDrawableResId == 0) {
            mCurrentDrawableResId = mDefaultDrawableResId
        }



        mCurrentDrawable = ContextCompat.getDrawable(context,R.drawable.ic_close_grey )

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mCurrentDrawable == null) {
            return
        }


        var rightOffset = 0
        if (error != null && error.length > 0) {
            rightOffset = resources.displayMetrics.density.toInt() * 32
        }


        val right = width - paddingRight - rightOffset

        val top = paddingTop
        val bottom = height - paddingBottom
        val ratio =
            mCurrentDrawable!!.intrinsicWidth.toFloat() / mCurrentDrawable!!.intrinsicHeight.toFloat()
        //int left = right - mCurrentDrawable.getIntrinsicWidth(); //If images are correct size.

        val left =
            (right - (bottom - top) * ratio).toInt() //scale image depeding on height available.

        mCurrentDrawable!!.setBounds(left, top, right, bottom)


        mCurrentDrawable!!.draw(canvas)


    }

}