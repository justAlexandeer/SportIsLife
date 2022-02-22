package com.myprog.sportislife.ui.customview

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.myprog.sportislife.R
import com.myprog.sportislife.data.Training
import kotlin.math.cos
import kotlin.math.sin

class SunburstChartKT @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0): View(context, attrs, defStyleAttr) {

    private var paintCircle = Paint()
    private var paintLine = Paint()
    private var paintPointFirst = Paint()
    private var paintPointSecond = Paint()
    private var oval = RectF()

    private var viewHeight = 0
    private var viewWidth = 0
    private var radiusCircle = 0.0f
    private var radiusFirstCircle = 0.0f
    private var radiusThirdCircle = 0.0f

    private var cxFirstCircle = 0.0f
    private var cyFirstCircle = 0.0f
    private var cxSecondCircle = 0.0f
    private var cySecondCircle = 0.0f
    private var cxThirdCircle = 0.0f
    private var cyThirdCircle = 0.0f

    private var angleLine1 = 0.0f
    private var angleLine2 = 0.0f
    private var angleLine3 = 0.0f

    private var angleTotalKal = 0.0
    private var angleTotalStep = 0.0
    private var angleTotalTime = 0.0

    private var maxMinutes = 0.0
    private var maxKal = 0.0
    private var maxStep = 0.0

    private val DURATION_ANIMATION = 700L

    private lateinit var allTraining: List<Training>

    init {
        paintCircle.style = Paint.Style.STROKE
        paintCircle.color = ContextCompat.getColor(context, R.color.gray)
        paintCircle.isAntiAlias = true

        paintLine.style = Paint.Style.STROKE

        paintPointFirst.style = Paint.Style.STROKE
        paintPointFirst.isAntiAlias = true

        paintPointSecond.color = Color.WHITE
        paintPointSecond.isAntiAlias = true
    }


    fun setMaxValues(maxMinutes: Double, maxKal: Double, maxStep: Double){
        this.maxMinutes = maxMinutes
        this.maxKal = maxKal
        this.maxStep = maxStep
    }

    fun setListTraining(allTraining: List<Training>){
        this.allTraining = allTraining.toList()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        viewHeight = measuredHeight - paddingTop - paddingBottom
        viewWidth = measuredWidth - paddingStart - paddingBottom

        radiusFirstCircle = measuredWidth * 0.4f // Радиус внешнего кольца, примерно 2/3 ширины экрана
        radiusThirdCircle = radiusFirstCircle * (1 / 3.0f) // Радиус внутреннего кольца, 1/3 от самоего внешнего
        radiusCircle = (radiusFirstCircle - radiusThirdCircle) / 6

        cxFirstCircle = measuredWidth / 2 + (radiusFirstCircle - radiusCircle) * cos(Math.toRadians(-90.0).toFloat())
        cyFirstCircle = measuredHeight / 2 - (radiusFirstCircle - radiusCircle) * sin(Math.toRadians(-90.0).toFloat())

        cxSecondCircle = measuredWidth / 2 + (radiusThirdCircle + radiusCircle * 3) * cos(Math.toRadians(-90.0).toFloat())
        cySecondCircle = measuredHeight / 2 - (radiusThirdCircle + radiusCircle * 3) * sin(Math.toRadians(-90.0).toFloat())

        cxThirdCircle = measuredWidth / 2 + (radiusThirdCircle + radiusCircle) * cos(Math.toRadians(90.0).toFloat())
        cyThirdCircle = measuredHeight / 2 - (radiusThirdCircle + radiusCircle) * sin(Math.toRadians(-90.0).toFloat())

        super.onLayout(changed, left, top, right, bottom)
    }

    fun start(){
        var totalKal = 0
        var stepCounter = 0
        var allTime = 0

        for (training in allTraining) {
            totalKal += training.calories.toInt()
            stepCounter += training.countStep
            allTime += (training.totalTime / 60000).toInt()
        }

        angleTotalKal = totalKal / maxKal * 360
        Log.d("SunburstChart", angleTotalKal.toString())

        //Шаги 100% = 5000
        angleTotalStep = stepCounter / maxStep * 360
        Log.d("SunburstChart", angleTotalStep.toString())

        //Время тренировки = 160 мин
        angleTotalTime = allTime / maxMinutes * 360
        Log.d("SunburstChart", angleTotalTime.toString())

        drawAnimation()
    }

    private fun drawAnimation(){
        val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()

        // 1 Кольцо
        val animatorCircle1 = ValueAnimator.ofFloat(-90f, (-angleTotalTime).toFloat() - 90)
        animatorCircle1.duration = DURATION_ANIMATION
        animatorCircle1.interpolator = accelerateDecelerateInterpolator
        animatorCircle1.addUpdateListener { animation ->
            cxFirstCircle = (measuredWidth / 2 + (radiusFirstCircle - radiusCircle) * cos(Math.toRadians((animation.animatedValue as Float)
                    .toDouble()))).toFloat()
            cyFirstCircle = (measuredHeight / 2 - (radiusFirstCircle - radiusCircle) * sin(Math.toRadians((animation.animatedValue as Float)
                    .toDouble()))).toFloat()
            invalidate()
        }

        // 2 кольцо
        val animatorCircle2 = ValueAnimator.ofFloat(-90f, (-angleTotalKal).toFloat() - 90)
        animatorCircle2.duration = DURATION_ANIMATION
        animatorCircle2.interpolator = accelerateDecelerateInterpolator
        animatorCircle2.addUpdateListener { animation ->
            cxSecondCircle = (measuredWidth / 2 + (radiusThirdCircle + radiusCircle * 3) * cos(Math.toRadians((animation.animatedValue as Float)
                    .toDouble()))).toFloat()
            cySecondCircle = (measuredHeight / 2 - (radiusThirdCircle + radiusCircle * 3) * sin(Math.toRadians((animation.animatedValue as Float)
                    .toDouble()))).toFloat()
            invalidate()
        }

        // 3 кольцо
        val animatorCircle3 = ValueAnimator.ofFloat(-90f, (-angleTotalStep).toFloat() - 90)
        animatorCircle3.duration = DURATION_ANIMATION
        animatorCircle3.interpolator = accelerateDecelerateInterpolator
        animatorCircle3.addUpdateListener { animation ->
            cxThirdCircle = (measuredWidth / 2 + (radiusThirdCircle + radiusCircle) * cos(Math.toRadians((animation.animatedValue as Float)
                    .toDouble()))).toFloat()
            cyThirdCircle = (measuredHeight / 2 - (radiusThirdCircle + radiusCircle) * sin(Math.toRadians((animation.animatedValue as Float)
                    .toDouble()))).toFloat()
            invalidate()
        }

        //1 Линия
        val animatorLine1 = ValueAnimator.ofFloat(0f, angleTotalTime.toFloat())
        animatorLine1.duration = DURATION_ANIMATION
        animatorLine1.interpolator = accelerateDecelerateInterpolator
        animatorLine1.addUpdateListener { animation ->
            angleLine1 = animation.animatedValue as Float
            invalidate()
        }

        //2 Линия
        val animatorLine2 = ValueAnimator.ofFloat(0f, angleTotalKal.toFloat())
        animatorLine2.duration = DURATION_ANIMATION
        animatorLine2.interpolator = accelerateDecelerateInterpolator
        animatorLine2.addUpdateListener { animation ->
            angleLine2 = animation.animatedValue as Float
            invalidate()
        }

        //3 Линия
        val animatorLine3 = ValueAnimator.ofFloat(0f, angleTotalStep.toFloat())
        animatorLine3.duration = DURATION_ANIMATION
        animatorLine3.interpolator = accelerateDecelerateInterpolator
        animatorLine3.addUpdateListener { animation ->
            angleLine3 = animation.animatedValue as Float
            invalidate()
        }

        val animatorSet = AnimatorSet()
        animatorSet.play(animatorCircle1).with(animatorCircle2).with(animatorCircle3)
                .with(animatorLine1).with(animatorLine2).with(animatorLine3)
        animatorSet.start()

    }

    override fun onDraw(canvas: Canvas) {
        //Log.d("SunburstChart", "onDraw")

        var left: Float
        var right: Float
        var top: Float
        var bottom: Float
        var radius: Float

        paintCircle.strokeWidth = radiusCircle.toFloat()

        canvas.drawCircle((measuredWidth / 2f), (measuredHeight / 2f), radiusFirstCircle - radiusCircle, paintCircle)
        canvas.drawCircle((measuredWidth / 2f), (measuredHeight / 2f), radiusThirdCircle + radiusCircle * 3, paintCircle)
        canvas.drawCircle((measuredWidth / 2f), (measuredHeight / 2f), radiusThirdCircle + radiusCircle, paintCircle)

        // 3 кольцо
        radius = radiusThirdCircle + radiusCircle
        left = measuredWidth / 2 - radius
        top = measuredHeight / 2 - radius
        right = measuredWidth / 2 + radius
        bottom = measuredHeight / 2 + radius

        paintLine.strokeWidth = radiusCircle
        paintLine.color = ContextCompat.getColor(context, R.color.red)
        oval.set(left, top, right, bottom)
        canvas.drawArc(oval, 90f, angleLine3, false, paintLine)

        // 2 кольцо
        radius = radiusThirdCircle + radiusCircle * 3
        left = measuredWidth / 2 - radius
        top = measuredHeight / 2 - radius
        right = measuredWidth / 2 + radius
        bottom = measuredHeight / 2 + radius

        paintLine.color = ContextCompat.getColor(context, R.color.yellow)
        oval.set(left, top, right, bottom)
        canvas.drawArc(oval, 90f, this.angleLine2, false, paintLine)

        // 1 кольцо
        radius = radiusFirstCircle - radiusCircle
        left = measuredWidth / 2 - radius
        top = measuredHeight / 2 - radius
        right = measuredWidth / 2 + radius
        bottom = measuredHeight / 2 + radius

        paintLine.color = ContextCompat.getColor(context, R.color.blue)
        oval.set(left, top, right, bottom)
        canvas.drawArc(oval, 90f, angleLine1, false, paintLine)

        paintPointFirst.strokeWidth = radiusCircle / 2

        // 1 Точка
        paintPointFirst.color = ContextCompat.getColor(context, R.color.red)
        canvas.drawCircle(cxThirdCircle, cyThirdCircle, radiusCircle / 2, paintPointFirst)
        canvas.drawCircle(cxThirdCircle, cyThirdCircle, radiusCircle / 2, paintPointSecond)

        // 2 Точка
        paintPointFirst.color = ContextCompat.getColor(context, R.color.yellow)
        canvas.drawCircle(cxSecondCircle, cySecondCircle, radiusCircle / 2, paintPointFirst)
        canvas.drawCircle(cxSecondCircle, cySecondCircle, radiusCircle / 2, paintPointSecond)

        // 3 Точка
        paintPointFirst.color = ContextCompat.getColor(context, R.color.blue)
        canvas.drawCircle(cxFirstCircle, cyFirstCircle, radiusCircle / 2, paintPointFirst)
        canvas.drawCircle(cxFirstCircle, cyFirstCircle, radiusCircle / 2, paintPointSecond)

    }
}
