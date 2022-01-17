package com.example.hw4

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

class ClockView(
    context: Context,
    attributesSet: AttributeSet?,
    defStyleAttr: Int
) : View(context, attributesSet, defStyleAttr) {

    private var circleColor by Delegates.notNull<Int>()
    private var secondHandColor by Delegates.notNull<Int>()
    private var minuteHandColor by Delegates.notNull<Int>()
    private var hourHandColor by Delegates.notNull<Int>()
    private lateinit var circlePaint: Paint
    private lateinit var secondHandPaint: Paint
    private lateinit var minuteHandPaint: Paint
    private lateinit var hourHandPaint: Paint
    private lateinit var textPaint: Paint
    private var circleWidth: Float = 6f
    private var secondHandWidth: Float = 3f
    private var hourHandWidth: Float = 7f
    private var minuteHandWidth: Float = 5f
    private var numeralsFontWidth: Float = 3f
    private val numerals = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private var numeralSpacing: Int = 40
    private var numeralsFontSize: Int = 15
    private var handTruncation = 0
    private var hourHandTruncation = 0
    private var radius = 0
    private var rect = Rect()

    constructor(context: Context, attributesSet: AttributeSet?) : this(
        context,
        attributesSet,
        R.attr.clockFieldStyle
    )
    constructor(context: Context) : this(context, null)

    init {
        if (attributesSet != null) {
            initAttributes(attributesSet, defStyleAttr)
        } else {
            initDefaultColors()
        }
        initPaints()
    }

    private fun initPaints() {
        numeralsFontSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, numeralsFontSize.toFloat(), resources.displayMetrics).toInt()
        val min = height.coerceAtMost(width)
        radius = 200
        handTruncation = min / 8
        hourHandTruncation = min / 7

        circlePaint = Paint().apply {
            color = circleColor
            strokeWidth = circleWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        secondHandPaint = Paint().apply {
            color = secondHandColor
            style = Paint.Style.FILL
            strokeWidth = secondHandWidth
            isAntiAlias = true
        }
        minuteHandPaint = Paint().apply {
            color = minuteHandColor
            style = Paint.Style.FILL
            strokeWidth = minuteHandWidth
            isAntiAlias = true
        }
        hourHandPaint = Paint().apply {
            color = hourHandColor
            style = Paint.Style.FILL
            strokeWidth = hourHandWidth
            isAntiAlias = true
        }
        textPaint = Paint().apply {
            color = circleColor
            strokeWidth = numeralsFontWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    private fun initAttributes(attributesSet: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attributesSet, R.styleable.ClockView, defStyleAttr, 0)
        circleColor = typedArray.getColor(R.styleable.ClockView_circleColor, CIRCLE_COLOR)
        secondHandColor = typedArray.getColor(R.styleable.ClockView_secondHandColor, SECOND_HAND_COLOR)
        minuteHandColor = typedArray.getColor(R.styleable.ClockView_minuteHandColor, MINUTE_HAND_COLOR)
        hourHandColor = typedArray.getColor(R.styleable.ClockView_hourHandColor, HOUR_HAND_COLOR)



        typedArray.recycle()
    }

    private fun initDefaultColors() {
        circleColor = CIRCLE_COLOR
        secondHandColor = SECOND_HAND_COLOR
        minuteHandColor = MINUTE_HAND_COLOR
        hourHandColor = HOUR_HAND_COLOR
    }

    companion object {
        const val CIRCLE_COLOR = Color.GRAY
        const val SECOND_HAND_COLOR = Color.WHITE
        const val MINUTE_HAND_COLOR = Color.BLUE
        const val HOUR_HAND_COLOR = Color.RED

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawCircle(canvas)
        drawHands(canvas)
        drawNumerals(canvas)
        drawLines(canvas)

        invalidate()
    }

    private fun drawNumerals(canvas: Canvas?) {
        for (number in numerals) {
            val temp = number.toString()
            textPaint.getTextBounds(temp, 0, temp.length, rect)
            textPaint.textSize = numeralsFontSize.toFloat()
            val angle = Math.PI / 6 * (number - 3)
            val x = (width / 2 + cos(angle) * (radius - numeralSpacing) - rect.width() / 2).toFloat()
            val y = (height / 2 + sin(angle) * (radius - numeralSpacing) + rect.height() / 2).toFloat()
            canvas?.drawText(temp, x, y, textPaint)
        }
    }

    private fun drawHand(canvas: Canvas?, loc: Float, isHour: Boolean, isSecond: Boolean) {
        val angle = Math.PI * loc / 30 - Math.PI / 2
        val handRadius = if (isHour) {
            radius - handTruncation - hourHandTruncation
        } else {
            radius - handTruncation
        }

        when {
            isHour -> {
                canvas?.drawLine((width / 2 - cos(angle) * handRadius * 0.1).toFloat(), (height / 2 - sin(angle) * handRadius*0.1).toFloat(), (width / 2 + cos(angle) * handRadius * 0.4).toFloat(), (height / 2 + sin(angle) * handRadius*0.4).toFloat(), hourHandPaint)
            }
            isSecond -> {
                canvas?.drawLine(width / 2f, height / 2f, (width / 2 + cos(angle) * handRadius * 0.6).toFloat(), (height / 2 + sin(angle) * handRadius * 0.6).toFloat(), secondHandPaint)
            }
            else -> {
                canvas?.drawLine((width / 2 - cos(angle) * handRadius * 0.1).toFloat(), (height / 2 - sin(angle) * handRadius*0.1).toFloat(), (width / 2 + cos(angle) * handRadius * 0.6).toFloat(), (height / 2 + sin(angle) * handRadius * 0.6).toFloat(), minuteHandPaint)
            }
        }
    }

    private fun drawHands(canvas: Canvas?) {
        val calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY).toFloat()
        val minute = calendar.get(Calendar.MINUTE).toFloat()
        val second = calendar.get(Calendar.SECOND).toFloat()
        hour = if (hour > 12) hour - 12 else hour
        drawHand(canvas, (hour + minute / 60) * 5, true, isSecond = false)
        drawHand(canvas, (minute + second / 60), false, isSecond = false)
        drawHand(canvas, second, false, isSecond = true)
    }

    private fun drawCircle(canvas: Canvas?) {
        canvas?.drawCircle(width / 2f, height / 2f, radius.toFloat(), circlePaint)
    }

    private fun drawLines(canvas: Canvas?) {
        for (number in numerals) {
            val angle = Math.PI / 6 * (number - 3)
            val x = (width / 2 + cos(angle) * (radius-15)).toFloat()
            val y = (height / 2 + sin(angle) * (radius-15)).toFloat()
            canvas?.drawLine(x,y,(width / 2 + cos(angle) * (radius+15)).toFloat(),(height / 2 + sin(angle) * (radius+15)).toFloat(),circlePaint)
        }
    }


}