package com.caldremch.common.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.caldremch.common.utils.DensityUtil

/**
 *
 * @author Caldremch
 *
 * @date 2019-06-13 17:26
 *
 * @email caldremch@163.com
 *
 * @describe
 *
 **/
class WxRecordBtn : View {

    private val TAG: String = WxRecordBtn::class.java.simpleName
    private val DURATION: Long = 250
    private lateinit var paint: Paint


    private var downTime: Long = 0;
    private var upTime: Long = 0;
    private val RECORD_TIME: Long = 10 * 1000;
    private val perTime: Long = 10 * 1000;
    /**
     * 最大录制秒数
     */
    private var seconds: Long = 0;
    private var isRecord: Boolean = false;

    private var animator: ValueAnimator? = null
    private var animatorBigger: ValueAnimator? = null

    private var animatorSweepAngle: ValueAnimator? = null

    private var animatorOutCircle: ValueAnimator? = null
    private var animatorOutCircleToRaw: ValueAnimator? = null

    /**
     * 原半径 (小圆)
     */
    private var rawRadius: Float = 0f;
    /**
     * 变化半径 (小圆)
     */
    private var varRadius: Float = 0f;
    /**
     * 最小半径  (小圆)
     */
    private var minRadius: Float = 0f;


    /**
     * 原半径 (大圆)
     */
    private var rawBiggerRadius: Float = 0f;
    /**
     * 变化半径 (大圆)
     */
    private var varBiggerRadius: Float = 0f;
    /**
     * 最大半径  (大圆)
     */
    private var maxBiggerRadius: Float = 0f;

    private lateinit var rectF: RectF
    private var sweepAngleEx: Float = 0f
    private val circlePainWidth: Float = 0f
    private val progressPainWidth: Float = DensityUtil.dp2px(5f).toFloat()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init();
    }


    private fun init() {
        paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#1A000000")
        rectF = RectF();
    }

    override fun onDraw(canvas: Canvas?) {
        rectF.top = progressPainWidth / 2;
        rectF.left = progressPainWidth / 2;
        rectF.bottom = viewMaxHeight - progressPainWidth / 2;
        rectF.right = viewMaxWidth - progressPainWidth / 2;



        rawRadius = (viewShowWidth / 2).toFloat() - (viewShowHeight / (2 * 4)).toFloat();
        minRadius = (rawRadius * 2 / 3).toFloat()

        rawBiggerRadius = (viewShowHeight / 2).toFloat()
        maxBiggerRadius = viewMaxHeight / 2


        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#1A000000")
        paint.strokeWidth = circlePainWidth
        if (isRecord) {
            canvas?.drawCircle((viewMaxWidth / 2).toFloat(), (viewMaxHeight / 2).toFloat(), varBiggerRadius, paint)
            paint.color = Color.WHITE
            canvas?.drawCircle((viewMaxWidth / 2).toFloat(), (viewMaxHeight / 2).toFloat(), varRadius, paint)
            //画进度条
            paint.color = Color.WHITE
            paint.strokeWidth = progressPainWidth
            paint.style = Paint.Style.STROKE
            canvas?.drawArc(rectF, -90f, sweepAngleEx, false, paint)
        } else {
            canvas?.drawCircle((viewMaxWidth / 2).toFloat(), (viewMaxWidth / 2).toFloat(), rawBiggerRadius, paint)
            paint.color = Color.WHITE
            canvas?.drawCircle((viewMaxWidth / 2).toFloat(), (viewMaxHeight / 2).toFloat(), rawRadius, paint)
        }

    }

    private var viewMaxWidth: Float = 0f
    private var viewMaxHeight: Float = 0f

    private var viewShowWidth: Float = 0f
    private var viewShowHeight: Float = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var finalWidth: Int = getMeasre(widthMeasureSpec)
        var finalHeight: Int = getMeasre(heightMeasureSpec)
        viewMaxWidth = finalWidth.toFloat()
        viewMaxHeight = finalHeight.toFloat()
        //保证圆形
        if (viewMaxHeight != viewMaxWidth) {
            viewMaxHeight = viewMaxWidth;
        }

        marginDis = viewMaxWidth * 1 / 3
        viewShowWidth = viewMaxWidth - marginDis
        viewShowHeight = viewShowWidth

        setMeasuredDimension(finalWidth, finalHeight)
    }

    private var marginDis: Float = 0f;

    private fun getMeasre(measureSpec: Int): Int {

        var size: Int = 200;

        var mode = MeasureSpec.getMode(measureSpec);

        when (mode) {

            MeasureSpec.UNSPECIFIED -> {
                size = 200;
            }

            MeasureSpec.AT_MOST -> {
                size = MeasureSpec.getSize(measureSpec)
            }

            MeasureSpec.EXACTLY -> {
                size = MeasureSpec.getSize(measureSpec)
            }
        }

        return size;
    }

    interface OnClick{
        fun takePic()
        fun onRecordStart()
        fun onRecordEnd()
    }

    private var onListener:OnClick? = null

    fun setListener(click:OnClick){
        this.onListener = click
    }

    private var wxBtnhandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {

            if (seconds == RECORD_TIME) {
                finishOrTakePicRecord();
                return;
            }

            if (seconds >= 500) {
                if (!isRecord){
                    onListener?.onRecordStart()
                }
                isRecord = true
                //小圆动画
                minCicleAnim()
                //大圆动画
                outCicleAnim()
                //应该等大圆动画结束后, 再进行录制进度动画
                sweepAngleAim()
            }
            seconds += 100;
            sendEmptyMessageDelayed(0, 100)

        }
    }

    private fun minCicleAnim() {
        if (animator == null) {
            animator = ObjectAnimator.ofFloat(rawRadius, minRadius)
        }

        animator?.let {
            if (varRadius != minRadius && !it.isRunning) {
                it.interpolator = DecelerateInterpolator()
                it.duration = DURATION
                it.repeatCount = 0
                it.start()
                it.addUpdateListener {
                    var value: Float = it.getAnimatedValue() as Float
                    varRadius = value
                    postInvalidate()
                }
            }
        }


    }

    private fun outCicleAnim() {

        if (animatorOutCircle == null) {
            animatorOutCircle = ObjectAnimator.ofFloat(rawBiggerRadius, maxBiggerRadius)
        }

        with(animatorOutCircle) {
            if (varBiggerRadius != maxBiggerRadius && !this?.isRunning!!) {
                interpolator = DecelerateInterpolator()
                duration = DURATION
                repeatCount = 0
                start()
                addUpdateListener {
                    var value: Float = it.getAnimatedValue() as Float
                    varBiggerRadius = value
                    postInvalidate()
                }
            }
        }


    }

    private fun sweepAngleAim() {
        if (animatorSweepAngle == null) {
            animatorSweepAngle = ObjectAnimator.ofFloat(0f, 360f)
        }

        animatorSweepAngle?.let {
            if (sweepAngleEx != 360f && !it.isRunning) {
                it.interpolator = LinearInterpolator()
                it.duration = RECORD_TIME
                it.repeatCount = 0
                it.start()
                it.addUpdateListener {
                    var value: Float = it.getAnimatedValue() as Float
                    sweepAngleEx = value
                    postInvalidate()
                }
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "[ACTION_DOWN] $seconds")
                downTime = System.currentTimeMillis();
                wxBtnhandler.sendEmptyMessage(0)
                varRadius = rawRadius
            }
            MotionEvent.ACTION_UP -> {
                upTime = System.currentTimeMillis();
                Log.d(TAG, "[ACTION_UP] $seconds")
                finishOrTakePicRecord()
            }

        }

        return true
    }

    private fun finishOrTakePicRecord() {
        wxBtnhandler.removeCallbacksAndMessages(null)
        //取消时间录制动画
        sweepAngleEx = 0f;
        if (isRecord) {
            Log.d(TAG, "[ACTION_UP] 录制")
            //取消动画
            cancelAnim(animatorSweepAngle)
            cancelAnim(animator)
            cancelAnim(animatorOutCircle)
            handlerBiggerAnim()
            outCircleAnimToRaw()
            onListener?.onRecordEnd()

        } else {
            isRecord = false
            seconds = 0
            Log.d(TAG, "[ACTION_UP] 拍照")
            onListener?.takePic()
        }

    }

    private fun cancelAnim(animator: ValueAnimator?) {
        animator?.let {
            if (it.isRunning) {
                it.cancel()
            }
        }
    }

    private fun outCircleAnimToRaw() {
        if (animatorOutCircleToRaw == null) {
            animatorOutCircleToRaw = ObjectAnimator.ofFloat(maxBiggerRadius, rawBiggerRadius)
        }

        animatorOutCircleToRaw?.let {

            if (!it.isRunning) {
                it.interpolator = DecelerateInterpolator()
                it.duration = DURATION
                it.repeatCount = 0
                it.start()
                it.addUpdateListener {
                    var value: Float = it.getAnimatedValue() as Float
                    varBiggerRadius = value
                    postInvalidate()
                }

            }

        }


    }

    private fun handlerBiggerAnim() {

        if (animatorBigger == null) {
            animatorBigger = ObjectAnimator.ofFloat(minRadius, rawRadius)
        }

        animatorBigger?.let {

            if (!it.isRunning) {
                it.interpolator = DecelerateInterpolator()
                it.duration = DURATION
                it.repeatCount = 0
                it.start()
                it.addUpdateListener {
                    var value: Float = it.getAnimatedValue() as Float
                    varRadius = value
                    postInvalidate()
                }

                it.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        isRecord = false
                        seconds = 0
                        it.cancel()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }


                })
            }

        }

    }

}