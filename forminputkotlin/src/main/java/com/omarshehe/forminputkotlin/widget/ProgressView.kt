package com.omarshehe.forminputkotlin.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.omarshehe.forminputkotlin.R
import java.lang.Float.NaN
import kotlin.properties.Delegates

class ProgressView(context: Context, attrs: AttributeSet) : TextInputLayout(context, attrs) {
    var view:TextInputLayout? = null

    private var paint: Paint = Paint()
    private var progressWeight: Int = 0
    private var progress: Int by Delegates.observable(0) { _, _, _ ->
        invalidate()
    }

    var xLastPosition: Float = NaN
    private var xStart: Float = NaN
    var xEnd: Float = NaN
    private var yPosition: Float = NaN

    var locOnDraw = false

    init {
        view = TextInputLayout(context)
        paint.apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 10f
            isAntiAlias = true
        }
    }

    fun updateProgress(mProgress:Int,color: Int = R.color.colorRed) {
        paint.color = ContextCompat.getColor(context, color)
        progress=mProgress
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        xStart = x - 8
        progressWeight = width.div(4)
        yPosition = height - 4.toFloat()

        if (xLastPosition.isNaN()) xLastPosition = xStart
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!locOnDraw) {
            xEnd = xStart + (progress * progressWeight)
            animateValue()
        } else canvas?.drawLine(xStart, yPosition, xEnd, yPosition, paint)
    }

    private fun animateValue() {
        locOnDraw = true

        val animator = ValueAnimator.ofFloat(xLastPosition, xEnd)

        animator.duration = 300

        animator.addUpdateListener { anim ->
            xEnd = anim.animatedValue as Float
            invalidate()
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Handler().postDelayed({
                    xLastPosition = xEnd
                    locOnDraw = false
                }, 100)
            }
        })

        animator.start()
    }
}
