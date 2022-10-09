package com.nico.rockertouchview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*
import kotlin.Float

/**
 * 摇杆方向控制View
 */
class RockerTouchView : View {
    /**  定义大圆的画笔  **/
    private lateinit var outerCirclePaint: Paint
    /**  定义小圆的画笔  **/
    private lateinit var innerCirclePaint: Paint
    /**  定义箭头的画笔  **/
    private lateinit var  directionPaint: Paint
    /**  内圆中心坐标X  **/
    private var innerCenterX: Float = 0.0f
    /**  内圆中心坐标Y  **/
    private var innerCenterY: Float = 0.0f
    /**  view中心点X坐标  **/
    private var viewCenterX: Float = 0.0f
    /**  view中心点Y坐标  **/
    private var viewCenterY: Float = 0.0f
    /**  定义View的边长  **/
    private var size: Int = 200
    /**  定义大圆的半径  **/
    private var outerCircleRadius: Double = 10.0
    /**  定义小圆的半径  **/
    private var innerCircleRadius: Double = 2.0
    /**  定义小圆的直径和大圆直径的比例，最小是5，最大是10  **/
    private var proportion: Int = 7
    /**  定义小圆的颜色，默认灰白色  **/
    private var innerCircleColor: Int = Color.parseColor("#C9FFFEFE")
    /**  定义大圆的颜色,默认灰色  **/
    private var outerCircleColor: Int = Color.parseColor("#BCC8C1C1")
    /**  定义箭头的颜色,默认白色  **/
    private var directionColor: Int = Color.parseColor("#FFFFFFFF")
    /**  是否显示方向箭头，默认是四方向  **/
    private var isDisplayDirection: Boolean = true
    /**  返回类型，0:任意角度 1:固定方向，默认为0  **/
    private var returnMode: Int = ReturnMode.ANGLEN.index
    /**  水平或垂直方向可忽略的最小偏移距离比例（大圆半径和可偏移距离的比值），只有方向模式会调用  **/
    private var minIgnoreDistanceRation: Int = 5
    /**  定义返回的listener  **/
    private var rockerTouchViewListener: RockerTouchViewListener? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
        initPaint()
        initCircleParams()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
        initPaint()
        initCircleParams()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
        initPaint()
        initCircleParams()
    }


    /**
     * @param attrs 传入的attrs
     * @param defStyle 传入的defStyle
     */
    private fun init(attrs: AttributeSet?, defStyle: Int) {

        // 加载客户定义的各种参数
        val a = context.obtainStyledAttributes(attrs, R.styleable.RockerTouchView, defStyle, 0)
        innerCircleColor = a.getInt(R.styleable.RockerTouchView_innerCircleColor,Color.parseColor("#C9FFFEFE"))
        outerCircleColor = a.getInt(R.styleable.RockerTouchView_outerCircleColor,Color.parseColor("#BCC8C1C1"))
        directionColor = a.getInt(R.styleable.RockerTouchView_directionColor,Color.parseColor("#FFFFFFFF"))
        isDisplayDirection = a.getBoolean(R.styleable.RockerTouchView_isDisplayDirection,true)
        returnMode = a.getInt(R.styleable.RockerTouchView_returnMode,0)
        if(returnMode != 0 && returnMode != 1 )returnMode = 0       //如果返回模式不为0或1时，默认修改为0
        minIgnoreDistanceRation = a.getInt(R.styleable.RockerTouchView_minIgnoreDistanceRation,5)
        if(minIgnoreDistanceRation < 5 || minIgnoreDistanceRation > 8) minIgnoreDistanceRation = 5      //如果最小偏移距离比例小于5或大于8，修改为默认参数
        proportion = a.getInt(R.styleable.RockerTouchView_proportion,7)
        if(proportion < 5 || proportion > 10) proportion = 5        //如果小圆的直径和大圆直径的比例超过5-10，默认修改为5
        a.recycle()

    }

    /**
     * 初始化Paint的各项参数
     */
    private fun initPaint(){
        innerCirclePaint = Paint()
        outerCirclePaint = Paint()
        directionPaint = Paint()
        innerCirclePaint.isAntiAlias = true    //设置小圆抗锯齿
        outerCirclePaint.isAntiAlias = true    //设置大圆抗锯齿
        directionPaint.isAntiAlias = true      //设置箭头抗锯齿
        innerCirclePaint.style = Paint.Style.FILL    //设置小圆全填充
        outerCirclePaint.style = Paint.Style.FILL    //设置大圆全填充
        directionPaint.strokeWidth = 5f    //设置箭头的宽度
        innerCirclePaint.color = innerCircleColor    //设置小圆颜色
        outerCirclePaint.color = outerCircleColor    //设置大圆颜色
        directionPaint.color = directionColor    //设置箭头颜色
    }

    /**
     * 初始化大圆、小圆的半径，View的Size，view坐标，小圆坐标
     */
    private fun initCircleParams(){
        innerCenterX = (size/2).toFloat()     //获取小圆初始化X坐标，默认在器件中心
        innerCenterY = (size/2).toFloat()      //获取小圆初始化Y坐标，默认在器件中心
        viewCenterX = (size/2).toFloat()       //获取大圆初始化X坐标，默认在器件中心
        viewCenterY = (size/2).toFloat()       //获取大圆初始化Y坐标，默认在器件中心
        outerCircleRadius = (size/2).toDouble()     //获取大圆初始化半径，默认为View的一半
        innerCircleRadius = (size/proportion).toDouble()     //获取小圆初始化半径，默认为View的一半
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(viewCenterX,viewCenterY,outerCircleRadius.toFloat(),outerCirclePaint)
        canvas.drawCircle(innerCenterX,innerCenterY,innerCircleRadius.toFloat(),innerCirclePaint)
        if (isDisplayDirection){

            //绘制顶部箭头
            canvas.drawLine((viewCenterX-outerCircleRadius/10).toFloat(),(2*outerCircleRadius/10).toFloat(),viewCenterX,(outerCircleRadius/10).toFloat(),directionPaint)
            canvas.drawLine(viewCenterX,(outerCircleRadius/10).toFloat(),(viewCenterX+outerCircleRadius/10).toFloat(),(2*outerCircleRadius/10).toFloat(),directionPaint)

            //绘制底部箭头
            canvas.drawLine((viewCenterX-outerCircleRadius/10).toFloat(),(2*outerCircleRadius-2*outerCircleRadius/10).toFloat(),viewCenterX,(2*outerCircleRadius-outerCircleRadius/10).toFloat(),directionPaint)
            canvas.drawLine(viewCenterX,(2*outerCircleRadius-outerCircleRadius/10).toFloat(),(viewCenterX+outerCircleRadius/10).toFloat(),(2*outerCircleRadius-2*outerCircleRadius/10).toFloat(),directionPaint)

            //绘制左边箭头
            canvas.drawLine((2*outerCircleRadius/10).toFloat(),(viewCenterY-outerCircleRadius/10).toFloat(),(outerCircleRadius/10).toFloat(),viewCenterY,directionPaint)
            canvas.drawLine((outerCircleRadius/10).toFloat(),viewCenterY,(2*outerCircleRadius/10).toFloat(),(viewCenterY+outerCircleRadius/10).toFloat(),directionPaint)

            //绘制右边箭头
            canvas.drawLine((2*outerCircleRadius-2*outerCircleRadius/10).toFloat(),(viewCenterY-outerCircleRadius/10).toFloat(),(2*outerCircleRadius-outerCircleRadius/10).toFloat(),viewCenterY,directionPaint)
            canvas.drawLine((2*outerCircleRadius-outerCircleRadius/10).toFloat(),viewCenterY,(2*outerCircleRadius-2*outerCircleRadius/10).toFloat(),(viewCenterY+outerCircleRadius/10).toFloat(),directionPaint)

        }
    }

    /**
     * 重新绘制View的大小
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measureWidth(widthMeasureSpec)
        val heigh = measureHeigh(heightMeasureSpec)
        if (width == heigh){
            size = width
            setMeasuredDimension(size,size)
        }else{
            size = min(width,heigh)
            setMeasuredDimension(size,size)
        }
        initCircleParams()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> handleActionDown(event)
            MotionEvent.ACTION_MOVE -> handleActionMove(event)
            MotionEvent.ACTION_UP -> handleActionUp(event)
            else -> null
        }
        return true

    }

    /**
     * 处理点击事件
     */
    private fun handleActionDown(event: MotionEvent?){
        val pointX = event?.x
        val pointY = event?.y
        if (pointX != null && pointY != null){      //当pointX和pointY不为空的情况下继续执行
            updateDisplay(pointX,pointY,MotionEvent.ACTION_DOWN)
        }
    }

    /**
     * 处理滑动事件
     */
    private fun handleActionMove(event: MotionEvent?){
        val pointX = event?.x
        val pointY = event?.y
        if (pointX != null && pointY != null){      //当pointX和pointY不为空的情况下继续执行
            updateDisplay(pointX,pointY,MotionEvent.ACTION_MOVE)
        }
    }

    /**
     * 处理抬起事件
     */
    private fun handleActionUp(event: MotionEvent?){
        val pointX = event?.x
        val pointY = event?.y
        if (pointX != null && pointY != null){      //当pointX和pointY不为空的情况下继续执行
            updateDisplay(pointX,pointY,MotionEvent.ACTION_UP)
        }
    }

    /**
     * 更新小圆的显示位置
     * @param pointX 小圆新的显示位置X坐标
     * @param pointY 小圆新的显示位置Y坐标
     * @param action 点击的类型
     */
    private fun updateDisplay(pointX: Float, pointY: Float,action: Int){
        val distant: Float
        val angle: Int
        distant = getDistant(viewCenterX,viewCenterY,pointX,pointY)
        angle = getAngle(viewCenterX,viewCenterY,pointX,pointY)
        if(action == MotionEvent.ACTION_UP){        //如果是抬起动作，直接将小圆置于中心
            innerCenterX = viewCenterX
            innerCenterY = viewCenterY
            updateListener(viewCenterX,viewCenterY,rockerTouchViewListener,0,minIgnoreDistanceRation,0.0f)
        }else if(distant < (outerCircleRadius-innerCircleRadius).toFloat()){      //点击的位置在大圆减去小圆的半径内，直接更新小圆的显示位置
            innerCenterX = pointX
            innerCenterY = pointY
            updateListener(pointX,pointY,rockerTouchViewListener,angle,minIgnoreDistanceRation,distant)
            //1.点击的位置大于大圆减去小圆的半径内，并且在大圆半径内，更新小圆与大圆相切
            //2.点击位置大于大圆，action为移动状态，更新小圆与大圆相切
        }else if(distant <= outerCircleRadius.toFloat() || (distant > outerCircleRadius.toFloat() && action == MotionEvent.ACTION_MOVE)){
            innerCenterX = (pointX-viewCenterX)*(outerCircleRadius.toFloat()-innerCircleRadius.toFloat())/distant+viewCenterX
            innerCenterY = (pointY-viewCenterY)*(outerCircleRadius.toFloat()-innerCircleRadius.toFloat())/distant+viewCenterY
            updateListener(pointX,pointY,rockerTouchViewListener,angle,minIgnoreDistanceRation,(outerCircleRadius-innerCircleRadius).toFloat())
        }else{      //其余状态都为默认状态
            innerCenterX = viewCenterX
            innerCenterY = viewCenterY
            updateListener(pointX,pointY,rockerTouchViewListener,0,minIgnoreDistanceRation,0.0f)
        }
        invalidate()
    }

    /**
     * 更新反馈的listener
     * @param pointX 当前点击的X坐标
     * @param pointY 当前点击的Y坐标
     * @param listener 反馈的listener实例
     * @param angle 反馈的角度
     * @param ignoreDistanceRation 最小忽略偏移比例
     * @param innerOuterDistant 小圆圆心到大圆圆心的距离
     */
    private fun updateListener(pointX: Float, pointY: Float,listener: RockerTouchViewListener?,angle: Int,ignoreDistanceRation: Int,innerOuterDistant: Float){
        if(listener != null){
           if(returnMode == 0){     //返回模式为angle，直接返回角度和比例值
               listener.onAllChange(angle,innerOuterDistant/(outerCircleRadius-innerCircleRadius).toFloat())
           }else{       //返回模式为direction，需要判断现在的角度方向
               if(pointY < viewCenterY && (abs(pointX - viewCenterX) <= (outerCircleRadius/ignoreDistanceRation)) && (abs(pointY-viewCenterY) > abs(pointX-viewCenterX))){        //Y点击位置在上方，并且X距离X轴小于最小忽略距离，判断为UP
                   listener.onFourChange(Direction.UP,innerOuterDistant/(outerCircleRadius-innerCircleRadius).toFloat())
               }else if(pointX > viewCenterX && (abs(pointY - viewCenterY) <= (outerCircleRadius/ignoreDistanceRation)) && (abs(pointY-viewCenterY) < abs(pointX-viewCenterX))){        //X点击位置在右方，并且Y距离Y轴小于最小忽略距离，判断为RIGHT
                   listener.onFourChange(Direction.RIGHT,innerOuterDistant/(outerCircleRadius-innerCircleRadius).toFloat())
               }else if(pointY > viewCenterY && (abs(pointX - viewCenterX) <= (outerCircleRadius/ignoreDistanceRation)) && (abs(pointY-viewCenterY) > abs(pointX-viewCenterX))){      //Y点击位置在下方，并且X距离X轴小于最小忽略距离，判断为DOWN
                   listener.onFourChange(Direction.DOWN,innerOuterDistant/(outerCircleRadius-innerCircleRadius).toFloat())
               }else if(pointX < viewCenterX && (abs(pointY - viewCenterY) <= (outerCircleRadius/ignoreDistanceRation)) && (abs(pointY-viewCenterY) < abs(pointX-viewCenterX))){      //X点击位置在左方，并且Y距离Y轴小于最小忽略距离，判断为LEFT
                   listener.onFourChange(Direction.LEFT,innerOuterDistant/(outerCircleRadius-innerCircleRadius).toFloat())
               }else{                                                                   //其余判断为NULL
                   listener.onFourChange(Direction.NULL,innerOuterDistant/(outerCircleRadius-innerCircleRadius).toFloat())
               }
           }
        }
    }

    /**
     * 获取当前点击的角度，正上方为0°，顺时针为0-360°
     * @param centerX 大圆中心X坐标
     * @param centerY 大圆中心Y坐标
     * @param pointX 点击位置X坐标
     * @param pointY 点击位置Y坐标
     */
    private fun getAngle(centerX: Float, centerY: Float, pointX: Float, pointY: Float): Int{
        val distant = getDistant(centerX,centerY,pointX,pointY)     //获取点击位置和大圆中心的间距
        var angle = (asin(abs(pointY-centerY)/distant))*180/ PI     //获取点击位置和大圆X轴的夹角度数，[0，90]
        if (pointX >= centerX && pointY < centerY){     //判断当前点击位置在右上角，计算实际角度
            angle = 90-angle
        }else if(pointX >= centerX && pointY >= centerY){       //判断当前点击位置在右下角，计算实际角度
            angle = 90+angle
        }else if(pointX < centerX && pointY >= centerY){        //判断当前点击位置在左下角，计算实际角度
            angle = 270-angle
        }else{                                                  //判断当前点击位置在上角，计算实际角度
            angle = 270+angle
        }
        return angle.toInt()
    }

    /**
     * 获取点击位置和大圆中心点的距离
     * @param centerX 大圆中心X坐标
     * @param centerY 大圆中心Y坐标
     * @param pointX 点击位置X坐标
     * @param pointY 点击位置Y坐标
     */
    private fun getDistant(centerX: Float, centerY: Float, pointX: Float, pointY: Float): Float {
        return sqrt((pointX-centerX).pow(2)+(pointY-centerY).pow(2))
    }

    /**
     * 获取View的宽度
     * @param measureSpec 传入View的宽度
     */
    private fun measureWidth(measureSpec: Int): Int{
        val mode = MeasureSpec.getMode(measureSpec)
        val value = MeasureSpec.getSize(measureSpec)
        val width = 500
        when(mode){
            MeasureSpec.UNSPECIFIED -> return width
            MeasureSpec.EXACTLY -> return value
            MeasureSpec.AT_MOST -> return value
        }
        return width
    }

    /**
     * 获取View的高度
     * @param measureSpec 传入View的高度
     */
    private fun measureHeigh(measureSpec: Int): Int{
        val mode = MeasureSpec.getMode(measureSpec)
        val value = MeasureSpec.getSize(measureSpec)
        val heigh = 500
        when(mode){
            MeasureSpec.UNSPECIFIED -> return width
            MeasureSpec.EXACTLY -> return value
            MeasureSpec.AT_MOST -> return value
        }
        return heigh
    }

    /**
     * 设置listener
     * @param listener 传入的listener
     */
    public fun setRockerTouchViewListener(listener: RockerTouchViewListener){
        rockerTouchViewListener = listener
    }

    /**
     * 获取小圆的颜色
     */
    public fun getInnerCircleColor(): Int{return innerCircleColor}

    /**
     * 设置小圆的颜色
     * @param color 小圆显示的颜色
     */
    public fun setInnerCircleColor(color: Int){
        innerCircleColor = color
        invalidate()
    }

    /**
     * 获取大圆的颜色
     */
    public fun getOuterCircleColor(): Int{return outerCircleColor}

    /**
     * 设置大圆的颜色
     * @param color 大圆显示的颜色
     */
    public fun setOuterCircleColor(color: Int){
        outerCircleColor = color
        invalidate()
    }

    /**
     * 获取箭头的颜色
     */
    public fun getDirectionColor(): Int{return directionColor}

    /**
     * 设置箭头的颜色
     * @param color 箭头显示的颜色
     */
    public fun setDirectionColor(color: Int){
        directionColor = color
        invalidate()
    }

    /**
     * 获取是否显示箭头
     */
    public fun getDisplayDirection(): Boolean{return isDisplayDirection}

    /**
     * 设置是否显示箭头
     * @param isDisplayDirection 是否显示箭头，boolean类型
     */
    public fun isDisplayDirection(isDisplayDirection: Boolean){
        this.isDisplayDirection = isDisplayDirection
        invalidate()
    }

    /**
     * 获取反馈的模式
     */
    public fun getReturnMode(): ReturnMode{return when(returnMode){0 -> ReturnMode.ANGLEN else -> ReturnMode.DIRECTION }}

    /**
     * 设置反馈的模式
     * @param mode 需要设置的反馈模式，枚举类型
     */
    public fun setReturnMode(mode: ReturnMode){
        returnMode = mode.index
        invalidate()
    }

    /**
     * 获取忽略的最小偏移距离比例
     */
    public fun getMinIgnoreDistanceRation(): Int{return minIgnoreDistanceRation}

    /**
     * 设置忽略的最小偏移距离比例
     * @param distanceRation 忽略的最小偏移距离比例
     */
    public fun setMinIgnoreDistanceRation(distanceRation: Int){
        if(!(distanceRation < 4 || distanceRation > 8))
            minIgnoreDistanceRation = distanceRation
        invalidate()
    }

    /**
     * 获取小圆的直径和大圆直径的比例
     */
    public fun  getProportion(): Int{return proportion}

    /**
     * 设置小圆的直径和大圆直径的比例
     * @param proportion 小圆的直径和大圆直径的比例
     */
    public fun setProportion(proportion: Int){
        if(!(proportion < 5 || proportion > 10))
            this.proportion = proportion
            invalidate()
    }


    public interface RockerTouchViewListener{
        /**
         * 返回任意角度，以及滑动比例
         * @param angle 转动的角度
         * @param percent 当前小圆位置到大圆中心距离与大圆半径的比值
         */
        fun onAllChange(angle: Int, percent: Float)

        /**
         * 返回固定方向
         * @param direction 返回固定的方向，UP,LEFT,RIGHT,DOWM
         * @param percent 当前小圆位置到大圆中心距离与大圆半径的比值
         */
        fun onFourChange(direction: Direction,percent: Float)
    }

    enum class ReturnMode(val index: Int){
        ANGLEN(0),DIRECTION(1)
    }
    enum class Direction{
        UP,LEFT,RIGHT,DOWN,NULL
    }
}