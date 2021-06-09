package com.graveno.alphalab.app.customprogress

import android.content.Context

import android.content.res.TypedArray

import android.graphics.*

import android.graphics.Paint.Cap

import android.os.Bundle

import android.os.Parcelable

import android.util.AttributeSet

import android.view.MotionEvent

import android.view.View

import androidx.core.content.ContextCompat



/**

 * fun : [CircularProgressBar]

 * - desc : it is a view class which is responsible for showing the custom progress bar with frustrating obtuse quadrant.

 * - if the progress bar is not matching with the provided drawable/bitmap icons in pointer head:

 *  - then try to adjust the stroke of progress bar

 *  - try to adjust the height/width of the drawable/bitmap icon.

 *  - try to adjust the [mInnerCircleHeight] and [mInnerCircleWidth], in [onMeasure].

 *  - if all of the above fails, then change the design.

 * - also don't try all attr avail with this view class, as they can led to disorient the progress in rare case.

 * - in orderr to call the view class :

 *  - in xml : [com.mazroid.testapp].[CircularProgressBar]

 *  - sample xml : <com.mazroid.testapp.CircularSeekBar

android:id="@+id/cir_progress"

android:layout_width="200dp"

android:layout_height="200dp"

android:padding="30dp"

app:cs_circle_style = "butt"

app:cs_progress = "90"

app:cs_max = "100"

android:visibility="gone"    //magic

app:cs_circle_stroke_width="32dp"

app:cs_pointer_alpha_ontouch ="10"

app:cs_circle_color="@android:color/darker_gray"

app:cs_circle_progress_color = "@android:color/holo_orange_dark"

app:cs_pointer_color = "@android:color/holo_orange_dark"

app:layout_constraintTop_toBottomOf="@+id/btn_click"

app:layout_constraintStart_toStartOf="parent"

app:layout_constraintEnd_toEndOf="parent"

app:layout_constraintBottom_toBottomOf="parent"/>

 *  - in kt/java : val [circularSeekBar] : [CircularProgressBar] = [CircularProgressBar].

 *  - [Note] : dont try to change any value as it will result in crashing of view or worse, disorientation of progressbar.

 */

class CircularProgressBar : View {

    /**

     * Used to scale the dp units to pixels

     */

    private val DPTOPX_SCALE = resources.displayMetrics.density



    /**

     * Minimum touch target size in DP. 48dp is the Android design recommendation

     */

    private val MIN_TOUCH_TARGET_DP = 48f



    /**

     * `Paint` instance used to draw the inactive circle.

     */

    private var mCirclePaint: Paint? = null

    private var mInnerCirclePaint: Paint? = null



    /**

     * `Paint` instance used to draw the circle fill.

     */

    private var mCircleFillPaint: Paint? = null



    /**

     * `Paint` instance used to draw the active circle (represents progress).

     */

    private var mCircleProgressPaint: Paint? = null

    private var mInnerCircleProgressPaint: Paint? = null



    /**

     * `Paint` instance used to draw the glow from the active circle.

     */

    private var mCircleProgressGlowPaint: Paint? = null



    /**

     * `Paint` instance used to draw the center of the pointer.

     * Note: This is broken on 4.0+, as BlurMasks do not work with hardware acceleration.

     */

    private var mPointerPaint: Paint? = null



    private var mOuterPaint: Paint? = null



    /**

     * `Paint` instance used to draw the halo of the pointer.

     * Note: The halo is the part that changes transparency.

     */

    private var mPointerHaloPaint: Paint? = null



    /**

     * `Paint` instance used to draw the border of the pointer, outside of the halo.

     */

    private var mPointerHaloBorderPaint: Paint? = null



    /**

     * The style of the circle, can be butt, round or square.

     */

    private var mCircleStyle: Cap? = null



    /**

     * current in negative half cycle.

     */

    private var isInNegativeHalf = false



    /**

     * The width of the circle (in pixels).

     */

    private var mCircleStrokeWidth = 0f



    /**

     * The X radius of the circle (in pixels).

     */

    private var mCircleXRadius = 0f



    /**

     * The Y radius of the circle (in pixels).

     */

    private var mCircleYRadius = 0f



    /**

     * sets drawable to seekbar

     */

    private lateinit var mPointerIcon : Bitmap

    private var mPointerDrawable : Int = DEFAULT_POINTER_ICON

    private lateinit var mCirclePointerIcon : Bitmap



    /**

     * If disable pointer, we can't seek the progress.

     */

    private var mDisablePointer = false



    /**

     * The radius of the pointer (in pixels).

     */

    private var mPointerStrokeWidth = 0f



    /**

     * The width of the pointer halo (in pixels).

     */

    private var mPointerHaloWidth = 0f



    /**

     * The width of the pointer halo border (in pixels).

     */

    private var mPointerHaloBorderWidth = 0f



    /**

     * Angle of the pointer arc.

     * Default is 0, the pointer is a circle when angle is 0 and the style is round.

     * Can not less then 0. can not longer than 360.

     */

    private var mPointerAngle = 0f



    /**

     * Start angle of the CircularSeekBar.

     * Note: If mStartAngle and mEndAngle are set to the same angle, 0.1 is subtracted

     * from the mEndAngle to make the circle function properly.

     */

    private var mStartAngle = 0f



    /**

     * End angle of the CircularSeekBar.

     * Note: If mStartAngle and mEndAngle are set to the same angle, 0.1 is subtracted

     * from the mEndAngle to make the circle function properly.

     */

    private var mEndAngle = 0f



    /**

     * `RectF` that represents the circle (or ellipse) of the seekbar.

     */

    private val mCircleRectF = RectF()

    private val mOuterCircleRectf = RectF()

    private val mInnerCircleRectf = RectF()



    /**

     * Holds the color value for `mPointerPaint` before the `Paint` instance is created.

     */

    private var mPointerColor = DEFAULT_POINTER_COLOR



    /**

     * Holds the color value for `mPointerHaloPaint` before the `Paint` instance is created.

     */

    private var mPointerHaloColor = DEFAULT_POINTER_HALO_COLOR



    /**

     * Holds the color value for `mPointerHaloPaint` before the `Paint` instance is created.

     */

    private var mPointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH



    /**

     * Holds the color value for `mCirclePaint` before the `Paint` instance is created.

     */

    private var mCircleColor = DEFAULT_CIRCLE_COLOR



    /**

     * Holds the color value for `mCircleFillPaint` before the `Paint` instance is created.

     */

    private var mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR



    /**

     * Holds the color value for `mCircleProgressPaint` before the `Paint` instance is created.

     */

    private var mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR



    /**

     * Holds the alpha value for `mPointerHaloPaint`.

     */

    private var mPointerAlpha = DEFAULT_POINTER_ALPHA



    /**

     * Holds the OnTouch alpha value for `mPointerHaloPaint`.

     */

    private var mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH



    /**

     * Distance (in degrees) that the the circle/semi-circle makes up.

     * This amount represents the max of the circle in degrees.

     */

    private var mTotalCircleDegrees = 0f



    /**

     * Distance (in degrees) that the current progress makes up in the circle.

     */

    private var mProgressDegrees = 0f



    /**

     * `Path` used to draw the circle/semi-circle.

     */

    private var mCirclePath: Path? = null

    private var mInnerCirclePath: Path? = null



    /**

     * `Path` used to draw the progress on the circle.

     */

    private var mCircleProgressPath: Path? = null

    private var mInnerCircleProgressPath: Path? = null



    /**

     * `Path` used to draw the pointer arc on the circle.

     */

    private var mCirclePointerPath: Path? = null

    private var mOuterPointerPath: Path? = null



    /**

     * Max value that this CircularSeekBar is representing.

     */

    private var mMax = 0f



    /**

     * Progress value that this CircularSeekBar is representing.

     */

    private var mProgress = 0f



    /**

     * Used for enabling/disabling the negative progress bar.

     */

    var isNegativeEnabled = false



    /**

     * If true, then the user can specify the X and Y radii.

     * If false, then the View itself determines the size of the CircularSeekBar.

     */

    private var mCustomRadii = false



    /**

     * Maintain a perfect circle (equal x and y radius), regardless of view or custom attributes.

     * The smaller of the two radii will always be used in this case.

     * The default is to be a circle and not an ellipse, due to the behavior of the ellipse.

     */

    private var mMaintainEqualCircle = false



    /**

     * Once a user has touched the circle, this determines if moving outside the circle is able

     * to change the position of the pointer (and in turn, the progress).

     */

    private var mMoveOutsideCircle = false



    //temp for dev..

    //somewhere global

    var iCurStep = 0 // current step

    //don't forget to initialize

    var pathMoveAlong = Path()



    /**

     * Matrix transform is use for positioning the bitmap/drawable icon with progressbar.

     */

    val mxTransform = Matrix()

    val mxCircleTransform = Matrix()



    /**

     * Used for enabling/disabling the lock option for easier hitting of the 0 progress mark.

     */

    var isLockEnabled = true



    /**

     * Used for when the user moves beyond the start of the circle when moving counter clockwise.

     * Makes it easier to hit the 0 progress mark.

     */

    private var lockAtStart = true



    /**

     * Used for when the user moves beyond the end of the circle when moving clockwise.

     * Makes it easier to hit the 100% (max) progress mark.

     */

    private var lockAtEnd = false



    /**

     * When the user is touching the circle on ACTION_DOWN, this is set to true.

     * Used when touching the CircularSeekBar.

     */

    private var mUserIsMovingPointer = false



    /**

     * Represents the clockwise distance from `mStartAngle` to the touch angle.

     * Used when touching the CircularSeekBar.

     */

    private var cwDistanceFromStart = 0f



    /**

     * Represents the counter-clockwise distance from `mStartAngle` to the touch angle.

     * Used when touching the CircularSeekBar.

     */

    private var ccwDistanceFromStart = 0f



    /**

     * Represents the clockwise distance from `mEndAngle` to the touch angle.

     * Used when touching the CircularSeekBar.

     */

    private var cwDistanceFromEnd = 0f



    /**

     * Represents the counter-clockwise distance from `mEndAngle` to the touch angle.

     * Used when touching the CircularSeekBar.

     * Currently unused, but kept just in case.

     */

    private var ccwDistanceFromEnd = 0f



    /**

     * The previous touch action value for `cwDistanceFromStart`.

     * Used when touching the CircularSeekBar.

     */

    private var lastCWDistanceFromStart = 0f



    /**

     * Represents the clockwise distance from `mPointerPosition` to the touch angle.

     * Used when touching the CircularSeekBar.

     */

    private var cwDistanceFromPointer = 0f



    /**

     * Represents the counter-clockwise distance from `mPointerPosition` to the touch angle.

     * Used when touching the CircularSeekBar.

     */

    private var ccwDistanceFromPointer = 0f



    /**

     * True if the user is moving clockwise around the circle, false if moving counter-clockwise.

     * Used when touching the CircularSeekBar.

     */

    private var mIsMovingCW = false



    /**

     * The width of the circle used in the `RectF` that is used to draw it.

     * Based on either the View width or the custom X radius.

     */

    private var mCircleWidth = 0f

    private var mInnerCircleWidth = 0f



    /**

     * The height of the circle used in the `RectF` that is used to draw it.

     * Based on either the View width or the custom Y radius.

     */

    private var mCircleHeight = 0f

    private var mInnerCircleHeight = 0f



    /**

     * Represents the progress mark on the circle, in geometric degrees.

     * This is not provided by the user; it is calculated;

     */

    private var mPointerPosition = 0f



    /**

     * Pointer position in terms of X and Y coordinates.

     */

    private val mPointerPositionXY = FloatArray(2)



    /**

     * Listener.

     */

    private var mOnCircularSeekBarChangeListener: OnCircularSeekBarChangeListener? = null



    /**

     * Initialize the CircularSeekBar with the attributes from the XML style.

     * Uses the defaults defined at the top of this file when an attribute is not specified by the user.

     * @param attrArray TypedArray containing the attributes.

     */

    private fun initAttributes(attrArray: TypedArray) {

        mCircleXRadius = attrArray.getDimension(

            R.styleable.cs_CircularSeekBar_cs_circle_x_radius,

            DEFAULT_CIRCLE_X_RADIUS

        )

        mCircleYRadius = attrArray.getDimension(

            R.styleable.cs_CircularSeekBar_cs_circle_y_radius,

            DEFAULT_CIRCLE_Y_RADIUS

        )

        mPointerStrokeWidth = attrArray.getDimension(

            R.styleable.cs_CircularSeekBar_cs_pointer_stroke_width,

            DEFAULT_POINTER_STROKE_WIDTH

        )



        mPointerHaloWidth = attrArray.getDimension(

            R.styleable.cs_CircularSeekBar_cs_pointer_halo_width,

            DEFAULT_POINTER_HALO_WIDTH

        )

        mPointerHaloBorderWidth = attrArray.getDimension(

            R.styleable.cs_CircularSeekBar_cs_pointer_halo_border_width,

            DEFAULT_POINTER_HALO_BORDER_WIDTH

        )

        mCircleStrokeWidth = attrArray.getDimension(

            R.styleable.cs_CircularSeekBar_cs_circle_stroke_width,

            DEFAULT_CIRCLE_STROKE_WIDTH

        )

        val circleStyle =

            attrArray.getInt(R.styleable.cs_CircularSeekBar_cs_circle_style, DEFAULT_CIRCLE_STYLE)

        mCircleStyle = Cap.values()[circleStyle]

        mPointerColor = attrArray.getColor(

            R.styleable.cs_CircularSeekBar_cs_pointer_color,

            DEFAULT_POINTER_COLOR

        )

        mPointerDrawable = attrArray.getInt(

            R.styleable.cs_CircularSeekBar_cs_pointer_icon,

            DEFAULT_POINTER_ICON

        )

        mPointerHaloColor = attrArray.getColor(

            R.styleable.cs_CircularSeekBar_cs_pointer_halo_color,

            DEFAULT_POINTER_HALO_COLOR

        )

        mPointerHaloColorOnTouch = attrArray.getColor(

            R.styleable.cs_CircularSeekBar_cs_pointer_halo_color_ontouch,

            DEFAULT_POINTER_HALO_COLOR_ONTOUCH

        )

        mCircleColor =

            attrArray.getColor(R.styleable.cs_CircularSeekBar_cs_circle_color, DEFAULT_CIRCLE_COLOR)

        mCircleProgressColor = attrArray.getColor(

            R.styleable.cs_CircularSeekBar_cs_circle_progress_color,

            DEFAULT_CIRCLE_PROGRESS_COLOR

        )

        mCircleFillColor = attrArray.getColor(

            R.styleable.cs_CircularSeekBar_cs_circle_fill,

            DEFAULT_CIRCLE_FILL_COLOR

        )

        mPointerAlpha = Color.alpha(mPointerHaloColor)

        mPointerAlphaOnTouch = attrArray.getInt(

            R.styleable.cs_CircularSeekBar_cs_pointer_alpha_ontouch,

            DEFAULT_POINTER_ALPHA_ONTOUCH

        )

        if (mPointerAlphaOnTouch > 255 || mPointerAlphaOnTouch < 0) {

            mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH

        }

        mMax = attrArray.getInt(R.styleable.cs_CircularSeekBar_cs_max, DEFAULT_MAX).toFloat()

        mProgress =

            attrArray.getInt(R.styleable.cs_CircularSeekBar_cs_progress, DEFAULT_PROGRESS).toFloat()

        mCustomRadii = attrArray.getBoolean(

            R.styleable.cs_CircularSeekBar_cs_use_custom_radii,

            DEFAULT_USE_CUSTOM_RADII

        )

        mMaintainEqualCircle = attrArray.getBoolean(

            R.styleable.cs_CircularSeekBar_cs_maintain_equal_circle,

            DEFAULT_MAINTAIN_EQUAL_CIRCLE

        )

        mMoveOutsideCircle = attrArray.getBoolean(

            R.styleable.cs_CircularSeekBar_cs_move_outside_circle,

            DEFAULT_MOVE_OUTSIDE_CIRCLE

        )

        isLockEnabled = attrArray.getBoolean(

            R.styleable.cs_CircularSeekBar_cs_lock_enabled,

            DEFAULT_LOCK_ENABLED

        )

        mDisablePointer = attrArray.getBoolean(

            R.styleable.cs_CircularSeekBar_cs_disable_pointer,

            DEFAULT_DISABLE_POINTER

        )

        isNegativeEnabled = attrArray.getBoolean(

            R.styleable.cs_CircularSeekBar_cs_negative_enabled,

            DEFAULT_NEGATIVE_ENABLED

        )

        isInNegativeHalf = false



        // Modulo 360 right now to avoid constant conversion

        mStartAngle = (360f + attrArray.getFloat(

            R.styleable.cs_CircularSeekBar_cs_start_angle,

            DEFAULT_START_ANGLE

        ) % 360f) % 360f

        mEndAngle = (360f + attrArray.getFloat(

            R.styleable.cs_CircularSeekBar_cs_end_angle,

            DEFAULT_END_ANGLE

        ) % 360f) % 360f



        // Disable negative progress if is semi-oval.

        if (mStartAngle != mEndAngle) {

            isNegativeEnabled = false

        }

        if (mStartAngle % 360f == mEndAngle % 360f) {

            //mStartAngle = mStartAngle + 1f;

            mEndAngle = mEndAngle - SMALL_DEGREE_BIAS

        }



//        mPointerIcon = ContextCompat.getDrawable(context, R.drawable.ic_pointer)!!

        mPointerIcon = if (mPointerDrawable == null){

            getBitmap(R.drawable.ic_pointer_head)!!

        } else getBitmap(mPointerDrawable)!!

        mCirclePointerIcon = getBitmap(R.drawable.ic_pointer)!!



        // Modulo 360 right now to avoid constant conversion

        mPointerAngle =

            (360f + attrArray.getFloat(

                R.styleable.cs_CircularSeekBar_cs_pointer_angle,

                DEFAULT_POINTER_ANGLE

            ) % 360f) % 30f

        if (mPointerAngle == 0f) {

            mPointerAngle = SMALL_DEGREE_BIAS

        }

        if (mDisablePointer) {

            mPointerStrokeWidth = 0f

            mPointerHaloWidth = 0f

            mPointerHaloBorderWidth = 0f

        }

    }



    /**

     * this will returns the bitmap file from drawable xml file as those files dont havea any bitmap associated with them.

     */

    private fun getBitmap(drawableRes: Int): Bitmap? {

        val drawable = ContextCompat.getDrawable(context, drawableRes)

        val canvas = Canvas()

        val bitmap = Bitmap.createBitmap(

            drawable!!.intrinsicWidth,

            drawable.intrinsicHeight,

            Bitmap.Config.ARGB_8888

        )

        canvas.setBitmap(bitmap)

        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        drawable.draw(canvas)

        return bitmap

    }



    /**

     * Initializes the `Paint` objects with the appropriate styles.

     */

    private fun initPaints() {

        mCirclePaint = Paint()

        mCirclePaint!!.isAntiAlias = true

        mCirclePaint!!.isDither = true

        mCirclePaint!!.color = Color.TRANSPARENT

//        mCirclePaint!!.strokeWidth = mCircleStrokeWidth

        mCirclePaint!!.style = Paint.Style.STROKE

        mCirclePaint!!.strokeJoin = Paint.Join.ROUND

        mCirclePaint!!.strokeCap = mCircleStyle



        mInnerCirclePaint = Paint()

        mInnerCirclePaint!!.isAntiAlias = true

        mInnerCirclePaint!!.isDither = true

        mInnerCirclePaint!!.color = mCircleColor

        mInnerCirclePaint!!.strokeWidth = mCircleStrokeWidth

        mInnerCirclePaint!!.style = Paint.Style.STROKE

        mInnerCirclePaint!!.strokeJoin = Paint.Join.ROUND

        mInnerCirclePaint!!.strokeCap = mCircleStyle



//        mCircleFillPaint = Paint()

//        mCircleFillPaint!!.isAntiAlias = true

//        mCircleFillPaint!!.isDither = true

//        mCircleFillPaint!!.color = mCircleFillColor

//        mCircleFillPaint!!.style = Paint.Style.FILL



        mCircleProgressPaint = Paint()

        mCircleProgressPaint!!.isAntiAlias = true

        mCircleProgressPaint!!.isDither = true

        mCircleProgressPaint!!.color = Color.TRANSPARENT

//        mCircleProgressPaint!!.strokeWidth = mCircleStrokeWidth

        mCircleProgressPaint!!.style = Paint.Style.STROKE

        mCircleProgressPaint!!.strokeJoin = Paint.Join.ROUND

        mCircleProgressPaint!!.strokeCap = mCircleStyle



        mInnerCircleProgressPaint = Paint()

        mInnerCircleProgressPaint!!.isAntiAlias = true

        mInnerCircleProgressPaint!!.isDither = true

        mInnerCircleProgressPaint!!.color = mCircleProgressColor

        mInnerCircleProgressPaint!!.strokeWidth = mCircleStrokeWidth

        mInnerCircleProgressPaint!!.style = Paint.Style.STROKE

        mInnerCircleProgressPaint!!.strokeJoin = Paint.Join.ROUND

        mInnerCircleProgressPaint!!.strokeCap = mCircleStyle



        mCircleProgressGlowPaint = Paint()

        mCircleProgressGlowPaint!!.set(mCircleProgressPaint)

        mCircleProgressGlowPaint!!.maskFilter =

            BlurMaskFilter(5f * DPTOPX_SCALE, BlurMaskFilter.Blur.NORMAL)



        mPointerPaint = Paint()

        mPointerPaint!!.isAntiAlias = true

        mPointerPaint!!.isDither = true

        mPointerPaint!!.color = mPointerColor

        mPointerPaint!!.strokeWidth = mPointerStrokeWidth

        mPointerPaint!!.style = Paint.Style.STROKE

        mPointerPaint!!.strokeJoin = Paint.Join.ROUND

        mPointerPaint!!.strokeCap = mCircleStyle



        mOuterPaint = Paint()

        mOuterPaint!!.isAntiAlias = true

        mOuterPaint!!.isDither = true

        mOuterPaint!!.color = mPointerColor

        mOuterPaint!!.strokeWidth = mPointerStrokeWidth

        mOuterPaint!!.style = Paint.Style.STROKE

        mOuterPaint!!.strokeJoin = Paint.Join.ROUND

        mOuterPaint!!.strokeCap = mCircleStyle



        mPointerHaloPaint = Paint()

        mPointerHaloPaint!!.set(mPointerPaint)

        mPointerHaloPaint!!.color = mPointerHaloColor

        mPointerHaloPaint!!.alpha = mPointerAlpha

        mPointerHaloPaint!!.strokeWidth = mPointerStrokeWidth + mPointerHaloWidth * 2f

        mPointerHaloBorderPaint = Paint()

        mPointerHaloBorderPaint!!.set(mPointerPaint)

        mPointerHaloBorderPaint!!.strokeWidth = mPointerHaloBorderWidth

        mPointerHaloBorderPaint!!.style = Paint.Style.STROKE

    }



    /**

     * Calculates the total degrees between mStartAngle and mEndAngle, and sets mTotalCircleDegrees

     * to this value.

     */

    private fun calculateTotalDegrees() {

        mTotalCircleDegrees =

            (360f - (mStartAngle - mEndAngle)) % 360f // Length of the entire circle/arc

        if (mTotalCircleDegrees <= 0f) {

            mTotalCircleDegrees = 360f

        }

    }



    /**

     * Calculate the degrees that the progress represents. Also called the sweep angle.

     * Sets mProgressDegrees to that value.

     */

    private fun calculateProgressDegrees() {

        mProgressDegrees =

            if (isInNegativeHalf) mStartAngle - mPointerPosition else mPointerPosition - mStartAngle // Verified

        mProgressDegrees =

            if (mProgressDegrees < 0) 360f + mProgressDegrees else mProgressDegrees // Verified

    }



    /**

     * Calculate the pointer position (and the end of the progress arc) in degrees.

     * Sets mPointerPosition to that value.

     */

    private fun calculatePointerPosition() {

        val progressPercent = mProgress / mMax

        val progressDegree = progressPercent * mTotalCircleDegrees

        mPointerPosition = mStartAngle + if (isInNegativeHalf) - progressDegree else progressDegree

        mPointerPosition = (if (mPointerPosition < 0) 360f + mPointerPosition else mPointerPosition) % 360f

    }



    /**

     * provides the base(x,y) of circle in center and for progress arc.

     */

    private fun calculatePointerXYPosition() {

        var pinm = PathMeasure(mInnerCircleProgressPath, false)

        var returnvalue = pinm.getPosTan(pinm.length, mPointerPositionXY, null)

        if (!returnvalue) {

            pinm = PathMeasure(mInnerCirclePath, false)

            returnvalue = pinm.getPosTan(0f, mPointerPositionXY, null)

        }



        var pm = PathMeasure(mCircleProgressPath, false)

        var returnValue = pm.getPosTan(pm.length, mPointerPositionXY, null)

        if (!returnValue) {

            pm = PathMeasure(mCirclePath, false)

            returnValue = pm.getPosTan(0f, mPointerPositionXY, null)

        }

    }



    /**

     * Initialize the `Path` objects.

     */

    private fun initPaths() {

        mCirclePath = Path()

        mCircleProgressPath = Path()

        mCirclePointerPath = Path()

        mOuterPointerPath = Path()



        mInnerCircleProgressPath = Path()

        mInnerCirclePath = Path()

    }



    /**

     * Reset the `Path` objects with the appropriate values.

     */

    private fun resetPaths() {

        if (isInNegativeHalf) {

            // beside progress path it self, we also draw a extend arc to math the pointer arc.

            val pointerStart = mPointerPosition - (mPointerAngle) / 2.0f

            mOuterPointerPath!!.reset()

            mOuterPointerPath!!.addArc(mOuterCircleRectf, pointerStart, mPointerAngle)

        } else {

            // beside progress path it self, we also draw a extend arc to math the pointer arc.

            val pointerStart = mPointerPosition - (mPointerAngle) / 2.0f

            mOuterPointerPath!!.reset()

            mOuterPointerPath!!.addArc(mOuterCircleRectf, pointerStart, mPointerAngle)

        }



        if (isInNegativeHalf) {

            mCirclePath!!.reset()

            mCirclePath!!.addArc(

                mCircleRectF,

                (mStartAngle - mTotalCircleDegrees)-2f,

                mTotalCircleDegrees

            )

            mInnerCirclePath!!.reset()

            mInnerCirclePath!!.addArc(

                mInnerCircleRectf,

                mStartAngle - mTotalCircleDegrees,

                mTotalCircleDegrees

            )



            // beside progress path it self, we also draw a extend arc to math the pointer arc.

            val extendStart = mStartAngle - mProgressDegrees - mPointerAngle / 2.0f

            var extendDegrees = mProgressDegrees + mPointerAngle

            if (extendDegrees >= 360f) {

                extendDegrees = 360f - SMALL_DEGREE_BIAS

            }

            mCircleProgressPath!!.reset()

            mCircleProgressPath!!.addArc(mCircleRectF, extendStart, extendDegrees)



            mInnerCircleProgressPath!!.reset()

            mInnerCircleProgressPath!!.addArc(mInnerCircleRectf, extendStart, extendDegrees)



            val pointerStart = mPointerPosition - mPointerAngle / 2.0f

            mCirclePointerPath!!.reset()

            mCirclePointerPath!!.addArc(mCircleRectF, pointerStart-2f, mPointerAngle)

        } else {

            mCirclePath!!.reset()

            mCirclePath!!.addArc(mCircleRectF, mStartAngle-2f, mTotalCircleDegrees)



            mInnerCirclePath!!.reset()

            mInnerCirclePath!!.addArc(mInnerCircleRectf, mStartAngle, mTotalCircleDegrees)



            // beside progress path it self, we also draw a extend arc to math the pointer arc.

            val extendStart = mStartAngle - mPointerAngle / 2.0f

            var extendDegrees = mProgressDegrees + mPointerAngle

            if (extendDegrees >= 360f) {

                extendDegrees = 360f - SMALL_DEGREE_BIAS

            }

            mCircleProgressPath!!.reset()

            mCircleProgressPath!!.addArc(mCircleRectF, extendStart, extendDegrees)



            mInnerCircleProgressPath!!.reset()

            mInnerCircleProgressPath!!.addArc(mInnerCircleRectf, extendStart, extendDegrees)



            val pointerStart = mPointerPosition - mPointerAngle / 2.0f

            mCirclePointerPath!!.reset()

            mCirclePointerPath!!.addArc(mCircleRectF, pointerStart-2f, mPointerAngle)

        }

    }



    /**

     * Initialize the `RectF` objects with the appropriate values.

     */

    private fun resetRects() {

        mCircleRectF[-mCircleWidth, -mCircleHeight, mCircleWidth] = mCircleHeight

        mOuterCircleRectf[-mCircleWidth, -mCircleHeight, mCircleWidth] = mCircleWidth

        mInnerCircleRectf[-mInnerCircleWidth, -mInnerCircleWidth, mInnerCircleWidth] = mInnerCircleWidth

    }





    /**

     * this will provide the visual of progressbar on canvas which is extended to associated view.

     * - you can check it by clicking on code/split/design view tab.

     */

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        canvas.translate((this.width / 2).toFloat(), (this.height / 2).toFloat())

        canvas.drawPath(mCirclePath!!, mCirclePaint!!)

        canvas.drawPath(mCircleProgressPath!!, mCircleProgressGlowPaint!!)

        canvas.drawPath(mCircleProgressPath!!, mCircleProgressPaint!!)

        canvas.drawPath(mInnerCirclePath!!, mInnerCirclePaint!!)

        canvas.drawPath(mInnerCircleProgressPath!!, mCircleProgressGlowPaint!!)

        canvas.drawPath(mInnerCircleProgressPath!!, mInnerCircleProgressPaint!!)

//        canvas.drawPath(mCirclePath!!, mCircleFillPaint!!)

//        mPointerIcon.draw(canvas)

//        canvas.drawBitmap(mPointerIcon, mPointerPosition, mPointerAngle, mOuterPaint)

        if (!mDisablePointer) {

            if (mUserIsMovingPointer) {

                canvas.drawPath(mCirclePointerPath!!, mPointerHaloPaint!!)

            }

            canvas.drawPath(mCirclePointerPath!!, mPointerPaint!!)

            canvas.drawPath(mOuterPointerPath!!, mOuterPaint!!)

            // TODO, find a good way to draw halo border.

//            if (mUserIsMovingPointer) {

//                canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1],

//                        (mPointerStrokeWidth /2f) + mPointerHaloWidth + (mPointerHaloBorderWidth / 2f),

//                        mPointerHaloBorderPaint);

//            }

        }



//        canvas.drawBitmap(mCirclePointerIcon, mxCircleTransform, mOuterPaint!!)

        if(mProgress < mMax){

            canvas.drawBitmap(mPointerIcon, mxTransform, mOuterPaint)

            canvas.drawBitmap(mCirclePointerIcon, mxCircleTransform, mOuterPaint)

        } else {

            canvas.drawBitmap(mCirclePointerIcon, mxCircleTransform, mOuterPaint)

            canvas.drawBitmap(mPointerIcon, mxTransform, mOuterPaint)

        }



    }



    /**

     * it will calculate the pointer matrix based on the [mCirclePath].

     */

    private fun calculateBasePointerIconMatrix() {

        val pm = PathMeasure(mCirclePath, false)

//        val fSegmentLen = pm.length / 20 //20 animation steps

        pm.getMatrix(

            0f, mxCircleTransform,

            PathMeasure.POSITION_MATRIX_FLAG + PathMeasure.TANGENT_MATRIX_FLAG

        )

//        if (iCurStep <= 20) {

//

//

//            iCurStep++

//        } else {

//            iCurStep = 0

//        }

    }



    /**

     * it will claculate the pointer matrix based on the [mCirclePointerPath].

     */

    private fun calculatePointerIconMatrix() {

        val pm = PathMeasure(mCirclePointerPath, false)

//        val fSegmentLen = pm.length / 20 //20 animation steps

        pm.getMatrix(

            progress, mxTransform,

            PathMeasure.POSITION_MATRIX_FLAG + PathMeasure.TANGENT_MATRIX_FLAG

        )

    }



    /**

     * Get the progress of the CircularSeekBar.

     * @return The progress of the CircularSeekBar.

     */

    /**

     * Set the progress of the CircularSeekBar.

     * If the progress is the same, then any listener will not receive a onProgressChanged event.

     * @param progress The progress to set the CircularSeekBar to.

     */

    var progress: Float

        get() {

            val progress = mMax * mProgressDegrees / mTotalCircleDegrees

            return if (isInNegativeHalf) -progress else progress

        }

        set(progress) {

            if (mProgress != progress) {

                if (isNegativeEnabled) {

                    if (progress < 0) {

                        mProgress = -progress

                        isInNegativeHalf = true

                    } else {

                        mProgress = progress

                        isInNegativeHalf = false

                    }

                } else {

                    mProgress = progress

                }

                if (mOnCircularSeekBarChangeListener != null) {

                    mOnCircularSeekBarChangeListener!!.onProgressChanged(this, progress, false)

                }

                recalculateAll()

                invalidate()

            }

        }



    /**

     * it is responsible to setting the angle to [mCircleProgressPath], [mPointerIcon] and [mCirclePointerIcon]

     */

    private fun setProgressBasedOnAngle(angle: Float) {

        mPointerPosition = angle

        calculateProgressDegrees()

        mProgress = mMax * mProgressDegrees / mTotalCircleDegrees

    }



    /**

     * it will recalculate and reset the instance of all provided view and progress in [canvas] view

     */

    private fun recalculateAll() {

        calculateTotalDegrees()

        calculatePointerPosition()

        calculateProgressDegrees()

        resetRects()

        resetPaths()

        calculateBasePointerIconMatrix()

        calculatePointerXYPosition()

        calculatePointerIconMatrix()

    }



    /**

     * it will be responsible for measuring the circle path and radius with the view space and makes sure it will be inside the padding.

     */

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        var width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        if (height == 0) height = width

        if (width == 0) width = height

        if (mMaintainEqualCircle) {

            val min = Math.min(width, height)

            setMeasuredDimension(min, min)

        } else {

            setMeasuredDimension(width, height)

        }



        // Set the circle width and height based on the view for the moment

        val padding = Math.max(

            mCircleStrokeWidth / 2f,

            mPointerStrokeWidth / 2 + mPointerHaloWidth + mPointerHaloBorderWidth

        )

        mCircleHeight = height / 2f - padding

        mCircleWidth = width / 2f - padding



        mInnerCircleHeight = height / 2.52f - padding

        mInnerCircleWidth = width / 2.52f - padding



        // If it is not set to use custom

        if (mCustomRadii) {

            // Check to make sure the custom radii are not out of the view. If they are, just use the view values

            if (mCircleYRadius - padding < mCircleHeight) {

                mCircleHeight = mCircleYRadius - padding

            }

            if (mCircleXRadius - padding < mCircleWidth) {

                mCircleWidth = mCircleXRadius - padding

            }

        }

        if (mMaintainEqualCircle) { // Applies regardless of how the values were determined

            val min = Math.min(mCircleHeight, mCircleWidth)

            mCircleHeight = min

            mCircleWidth = min

            val innerMin = Math.min(mInnerCircleHeight, mInnerCircleWidth)

            mInnerCircleHeight = innerMin

            mInnerCircleWidth = innerMin

        }

        recalculateAll()

    }



    /**

     * use it you need to incorporate the touch event...

     */

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (mDisablePointer || !isEnabled) return false



        // Convert coordinates to our internal coordinate system

        val x = event.x - width / 2

        val y = event.y - height / 2



        // Get the distance from the center of the circle in terms of x and y

        val distanceX = mCircleRectF.centerX() - x

        val distanceY = mCircleRectF.centerY() - y



        // Get the distance from the center of the circle in terms of a radius

        val touchEventRadius =

            Math.sqrt(Math.pow(distanceX.toDouble(), 2.0) + Math.pow(distanceY.toDouble(), 2.0))

                .toFloat()

        val minimumTouchTarget =

            MIN_TOUCH_TARGET_DP * DPTOPX_SCALE // Convert minimum touch target into px

        var additionalRadius: Float // Either uses the minimumTouchTarget size or larger if the ring/pointer is larger

        additionalRadius =

            if (mCircleStrokeWidth < minimumTouchTarget) { // If the width is less than the minimumTouchTarget, use the minimumTouchTarget

                minimumTouchTarget / 2

            } else {

                mCircleStrokeWidth / 2 // Otherwise use the width

            }

        val outerRadius = Math.max(

            mCircleHeight,

            mCircleWidth

        ) + additionalRadius // Max outer radius of the circle, including the minimumTouchTarget or wheel width

        val innerRadius = Math.min(

            mCircleHeight,

            mCircleWidth

        ) - additionalRadius // Min inner radius of the circle, including the minimumTouchTarget or wheel width

        additionalRadius =

            if (mPointerStrokeWidth < minimumTouchTarget / 2) { // If the pointer radius is less than the minimumTouchTarget, use the minimumTouchTarget

                minimumTouchTarget / 2

            } else {

                mPointerStrokeWidth // Otherwise use the radius

            }

        var touchAngle: Float

        touchAngle =

            (Math.atan2(y.toDouble(), x.toDouble()) / Math.PI * 180 % 360).toFloat() // Verified

        touchAngle = if (touchAngle < 0) 360 + touchAngle else touchAngle // Verified

        cwDistanceFromStart = touchAngle - mStartAngle // Verified

        cwDistanceFromStart =

            if (cwDistanceFromStart < 0) 360f + cwDistanceFromStart else cwDistanceFromStart // Verified

        ccwDistanceFromStart = 360f - cwDistanceFromStart // Verified

        cwDistanceFromEnd = touchAngle - mEndAngle // Verified

        cwDistanceFromEnd =

            if (cwDistanceFromEnd < 0) 360f + cwDistanceFromEnd else cwDistanceFromEnd // Verified

        ccwDistanceFromEnd = 360f - cwDistanceFromEnd // Verified

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {

                // These are only used for ACTION_DOWN for handling if the pointer was the part that was touched

                val pointerRadiusDegrees = (mPointerStrokeWidth * 180 / (Math.PI * Math.max(

                    mCircleHeight,

                    mCircleWidth

                ))).toFloat()

                val pointerDegrees = Math.max(pointerRadiusDegrees, mPointerAngle / 2f)

                cwDistanceFromPointer = touchAngle - mPointerPosition

                cwDistanceFromPointer =

                    if (cwDistanceFromPointer < 0) 360f + cwDistanceFromPointer else cwDistanceFromPointer

                ccwDistanceFromPointer = 360f - cwDistanceFromPointer

                // This is for if the first touch is on the actual pointer.

                if (touchEventRadius >= innerRadius && touchEventRadius <= outerRadius &&

                    (cwDistanceFromPointer <= pointerDegrees || ccwDistanceFromPointer <= pointerDegrees)

                ) {

                    setProgressBasedOnAngle(mPointerPosition)

                    lastCWDistanceFromStart = cwDistanceFromStart

                    mIsMovingCW = true

                    mPointerHaloPaint!!.alpha = mPointerAlphaOnTouch

                    mPointerHaloPaint!!.color = mPointerHaloColorOnTouch

                    recalculateAll()

                    invalidate()

                    if (mOnCircularSeekBarChangeListener != null) {

                        mOnCircularSeekBarChangeListener!!.onStartTrackingTouch(this)

                    }

                    mUserIsMovingPointer = true

                    lockAtEnd = false

                    lockAtStart = false

                } else if (cwDistanceFromStart > mTotalCircleDegrees) { // If the user is touching outside of the start AND end

                    mUserIsMovingPointer = false

                    return false

                } else if (touchEventRadius >= innerRadius && touchEventRadius <= outerRadius) { // If the user is touching near the circle

                    setProgressBasedOnAngle(touchAngle)

                    lastCWDistanceFromStart = cwDistanceFromStart

                    mIsMovingCW = true

                    mPointerHaloPaint!!.alpha = mPointerAlphaOnTouch

                    mPointerHaloPaint!!.color = mPointerHaloColorOnTouch

                    recalculateAll()

                    invalidate()

                    if (mOnCircularSeekBarChangeListener != null) {

                        mOnCircularSeekBarChangeListener!!.onStartTrackingTouch(this)

                        mOnCircularSeekBarChangeListener!!.onProgressChanged(this, progress, true)

                    }

                    mUserIsMovingPointer = true

                    lockAtEnd = false

                    lockAtStart = false

                } else { // If the user is not touching near the circle

                    mUserIsMovingPointer = false

                    return false

                }

            }

            MotionEvent.ACTION_MOVE -> if (mUserIsMovingPointer) {

                val smallInCircle = mTotalCircleDegrees / 3f

                var cwPointerFromStart = mPointerPosition - mStartAngle

                cwPointerFromStart =

                    if (cwPointerFromStart < 0) cwPointerFromStart + 360f else cwPointerFromStart

                val touchOverStart = ccwDistanceFromStart < smallInCircle

                val touchOverEnd = cwDistanceFromEnd < smallInCircle

                val pointerNearStart = cwPointerFromStart < smallInCircle

                val pointerNearEnd = cwPointerFromStart > mTotalCircleDegrees - smallInCircle

                val progressNearZero = mProgress < mMax / 3f

                val progressNearMax = mProgress > mMax / 3f * 2f

                if (progressNearMax) {  // logic for end lock.

                    if (pointerNearStart) { // negative end

                        lockAtEnd = touchOverStart

                    } else if (pointerNearEnd) {    // positive end

                        lockAtEnd = touchOverEnd

                    }

                } else if (progressNearZero && isNegativeEnabled) {   // logic for negative flip

                    if (touchOverEnd) isInNegativeHalf = false else if (touchOverStart) {

                        isInNegativeHalf = true

                    }

                } else if (progressNearZero) {  // logic for start lock

                    if (pointerNearStart) {

                        lockAtStart = touchOverStart

                    }

                }

                if (lockAtStart && isLockEnabled) {

                    // TODO: Add a check if mProgress is already 0, in which case don't call the listener

                    mProgress = 0f

                    recalculateAll()

                    invalidate()

                    if (mOnCircularSeekBarChangeListener != null) {

                        mOnCircularSeekBarChangeListener!!.onProgressChanged(this, progress, true)

                    }

                } else if (lockAtEnd && isLockEnabled) {

                    mProgress = mMax

                    recalculateAll()

                    invalidate()

                    if (mOnCircularSeekBarChangeListener != null) {

                        mOnCircularSeekBarChangeListener!!.onProgressChanged(this, progress, true)

                    }

                } else if (mMoveOutsideCircle || touchEventRadius <= outerRadius) {

                    if (cwDistanceFromStart <= mTotalCircleDegrees) {

                        setProgressBasedOnAngle(touchAngle)

                    }

                    recalculateAll()

                    invalidate()

                    if (mOnCircularSeekBarChangeListener != null) {

                        mOnCircularSeekBarChangeListener!!.onProgressChanged(this, progress, true)

                    }

                } else {

                    return false

                }

                lastCWDistanceFromStart = cwDistanceFromStart

            } else {

                return false

            }

            MotionEvent.ACTION_UP -> {

                mPointerHaloPaint!!.alpha = mPointerAlpha

                mPointerHaloPaint!!.color = mPointerHaloColor

                if (mUserIsMovingPointer) {

                    mUserIsMovingPointer = false

                    invalidate()

                    if (mOnCircularSeekBarChangeListener != null) {

                        mOnCircularSeekBarChangeListener!!.onStopTrackingTouch(this)

                    }

                } else {

                    return false

                }

            }

            MotionEvent.ACTION_CANCEL -> {

                mPointerHaloPaint!!.alpha = mPointerAlpha

                mPointerHaloPaint!!.color = mPointerHaloColor

                mUserIsMovingPointer = false

                invalidate()

            }

        }

        if (event.action == MotionEvent.ACTION_MOVE && parent != null) {

            parent.requestDisallowInterceptTouchEvent(true)

        }

        return true

    }



    /**

     * it will be called when used init the canvas for view...

     */

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val attrArray =

            context.obtainStyledAttributes(attrs, R.styleable.cs_CircularSeekBar, defStyle, 0)

//        mPointerIcon = BitmapFactory.decodeResource(context.resources, R.drawable.custom_thumb)

        initAttributes(attrArray)

        attrArray.recycle()

        initPaints()

        initPaths()

    }



    /**

     * view class constructor

     */

    constructor(context: Context?) : super(context) {

        init(null, 0)

    }



    /**

     * view class constructor

     */

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

        init(attrs, 0)

    }



    /**

     * view class constructor

     */

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(

        context,

        attrs,

        defStyle

    ) {

        init(attrs, defStyle)

    }



    /**

     * uses to save instance of all available view for canvas change..

     */

    override fun onSaveInstanceState(): Parcelable? {

        val superState = super.onSaveInstanceState()

        val state = Bundle()

        state.putParcelable("PARENT", superState)

        state.putFloat("MAX", mMax)

        state.putFloat("PROGRESS", mProgress)

        state.putInt("mCircleColor", mCircleColor)

        state.putInt("mCircleProgressColor", mCircleProgressColor)

        state.putInt("mPointerColor", mPointerColor)

        state.putInt("mPointerHaloColor", mPointerHaloColor)

        state.putInt("mPointerHaloColorOnTouch", mPointerHaloColorOnTouch)

        state.putInt("mPointerAlpha", mPointerAlpha)

        state.putInt("mPointerAlphaOnTouch", mPointerAlphaOnTouch)

        state.putFloat("mPointerAngle", mPointerAngle)

        state.putBoolean("mDisablePointer", mDisablePointer)

        state.putBoolean("lockEnabled", isLockEnabled)

        state.putBoolean("negativeEnabled", isNegativeEnabled)

        state.putBoolean("isInNegativeHalf", isInNegativeHalf)

        state.putInt("mCircleStyle", mCircleStyle!!.ordinal)

        return state

    }



    /**

     * restore the value of instances.

     */

    override fun onRestoreInstanceState(state: Parcelable) {

        val savedState = state as Bundle

        val superState = savedState.getParcelable<Parcelable>("PARENT")

        super.onRestoreInstanceState(superState)

        mMax = savedState.getFloat("MAX")

        mProgress = savedState.getFloat("PROGRESS")

        mCircleColor = savedState.getInt("mCircleColor")

        mCircleProgressColor = savedState.getInt("mCircleProgressColor")

        mPointerColor = savedState.getInt("mPointerColor")

        mPointerHaloColor = savedState.getInt("mPointerHaloColor")

        mPointerHaloColorOnTouch = savedState.getInt("mPointerHaloColorOnTouch")

        mPointerAlpha = savedState.getInt("mPointerAlpha")

        mPointerAlphaOnTouch = savedState.getInt("mPointerAlphaOnTouch")

        mPointerAngle = savedState.getFloat("mPointerAngle")

        isLockEnabled = savedState.getBoolean("lockEnabled")

        mDisablePointer = savedState.getBoolean("mDisablePointer")

        isNegativeEnabled = savedState.getBoolean("negativeEnabled")

        isInNegativeHalf = savedState.getBoolean("isInNegativeHalf")

        mCircleStyle = Cap.values()[savedState.getInt("mCircleStyle")]

        initPaints()

        recalculateAll()

    }



    fun setOnSeekBarChangeListener(l: OnCircularSeekBarChangeListener?) {

        mOnCircularSeekBarChangeListener = l

    }



    /**

     * Listener for the CircularSeekBar. Implements the same methods as the normal OnSeekBarChangeListener.

     */

    interface OnCircularSeekBarChangeListener {

        fun onProgressChanged(circularProgressBar: CircularProgressBar?, progress: Float, fromUser: Boolean)

        fun onStopTrackingTouch(progressBar: CircularProgressBar?)

        fun onStartTrackingTouch(progressBar: CircularProgressBar?)

    }



    var circleStyle: Cap?

        get() = mCircleStyle

        set(style) {

            mCircleStyle = style

            initPaints()

            recalculateAll()

            invalidate()

        }



    /**

     * Sets the circle stroke width.

     * @param width the width of the circle

     */

    var circleStrokeWidth: Float

        get() = mCircleStrokeWidth

        set(width) {

            mCircleStrokeWidth = width

            initPaints()

            recalculateAll()

            invalidate()

        }



    //mStartAngle = mStartAngle + 1f;

    var endAngle: Float

        get() = mEndAngle

        set(angle) {

            mEndAngle = angle

            if (mStartAngle % 360f == mEndAngle % 360f) {

                //mStartAngle = mStartAngle + 1f;

                mEndAngle = mEndAngle - SMALL_DEGREE_BIAS

            }

            recalculateAll()

            invalidate()

        }



    //mStartAngle = mStartAngle + 1f;

    var startAngle: Float

        get() = mStartAngle

        set(angle) {

            mStartAngle = angle

            if (mStartAngle % 360f == mEndAngle % 360f) {

                //mStartAngle = mStartAngle + 1f;

                mEndAngle = mEndAngle - SMALL_DEGREE_BIAS

            }

            recalculateAll()

            invalidate()

        }



    /**

     * Gets the circle color.

     * @return An integer color value for the circle

     */

    /**

     * Sets the circle color.

     * @param color the color of the circle

     */

    var circleColor: Int

        get() = mCircleColor

        set(color) {

            mCircleColor = color

            mCirclePaint!!.color = mCircleColor

            invalidate()

        }



    /**

     * Sets the pointer pointer stroke width.

     * @param width the width of the pointer

     */

    var pointerStrokeWidth: Float

        get() = mPointerStrokeWidth

        set(width) {

            mPointerStrokeWidth = width

            initPaints()

            recalculateAll()

            invalidate()

        }

    /**

     * Gets the circle progress color.

     * @return An integer color value for the circle progress

     */

    /**

     * Sets the circle progress color.

     * @param color the color of the circle progress

     */

    var circleProgressColor: Int

        get() = mCircleProgressColor

        set(color) {

            mCircleProgressColor = color

            mCircleProgressPaint!!.color = mCircleProgressColor

            invalidate()

        }

    /**

     * Gets the pointer color.

     * @return An integer color value for the pointer

     */

    /**

     * Sets the pointer color.

     * @param color the color of the pointer

     */

    var pointerColor: Int

        get() = mPointerColor

        set(color) {

            mPointerColor = color

            mPointerPaint!!.color = mPointerColor

            invalidate()

        }

    /**

     * Gets the pointer halo color.

     * @return An integer color value for the pointer halo

     */

    /**

     * Sets the pointer halo color.

     * @param color the color of the pointer halo

     */

    var pointerHaloColor: Int

        get() = mPointerHaloColor

        set(color) {

            mPointerHaloColor = color

            mPointerHaloPaint!!.color = mPointerHaloColor

            invalidate()

        }

    /**

     * Gets the pointer alpha value.

     * @return An integer alpha value for the pointer (0..255)

     */

    /**

     * Sets the pointer alpha.

     * @param alpha the alpha of the pointer

     */

    var pointerAlpha: Int

        get() = mPointerAlpha

        set(alpha) {

            if (alpha >= 0 && alpha <= 255) {

                mPointerAlpha = alpha

                mPointerHaloPaint!!.alpha = mPointerAlpha

                invalidate()

            }

        }

    /**

     * Gets the pointer alpha value when touched.

     * @return An integer alpha value for the pointer (0..255) when touched

     */

    /**

     * Sets the pointer alpha when touched.

     * @param alpha the alpha of the pointer (0..255) when touched

     */

    var pointerAlphaOnTouch: Int

        get() = mPointerAlphaOnTouch

        set(alpha) {

            if (alpha >= 0 && alpha <= 255) {

                mPointerAlphaOnTouch = alpha

            }

        }

    /**

     * Gets the pointer angle.

     * @return Angle for the pointer (0..360)

     */// Modulo 360 right now to avoid constant conversion

    /**

     * Sets the pointer angle.

     * @param angle the angle of the pointer

     */

    var pointerAngle: Float = 0.0f

        get() = mPointerAngle



    /**

     * Sets the circle fill color.

     * @param color the color of the circle fill

     */

    fun setCircleFillColor(color: Int) {

        mCircleFillColor = color

        mCircleFillPaint!!.color = mCircleFillColor

        invalidate()

    }



    /**

     * Gets the circle fill color.

     * @return An integer color value for the circle fill

     */

    fun getCircleFillColor(): Int {

        return mCircleFillColor

    }



    /**

     * Set the max of the CircularSeekBar.

     * If the new max is less than the current progress, then the progress will be set to zero.

     * If the progress is changed as a result, then any listener will receive a onProgressChanged event.

     * @param max The new max for the CircularSeekBar.

     */

    fun setMax(max: Float) {

        if (max > 0) { // Check to make sure it's greater than zero

            if (max <= mProgress) {

                mProgress = 0f // If the new max is less than current progress, set progress to zero

                if (mOnCircularSeekBarChangeListener != null) {

                    mOnCircularSeekBarChangeListener!!.onProgressChanged(

                        this,

                        if (isInNegativeHalf) -mProgress else mProgress,

                        false

                    )

                }

            }

            mMax = max

            recalculateAll()

            invalidate()

        }

    }



    /**

     * Get the current max of the CircularSeekBar.

     * @return Synchronized integer value of the max.

     */

    @Synchronized

    fun getMax(): Float {

        return mMax

    }



    fun getPathCircle(): RectF {

        return mCircleRectF

    }



    companion object {

        /**

         * For some case we need the degree to have small bias to avoid overflow.

         */

        private const val SMALL_DEGREE_BIAS = .1f



        // Default values

        private val DEFAULT_CIRCLE_STYLE = Cap.ROUND.ordinal

        private const val DEFAULT_CIRCLE_X_RADIUS = 30f

        private const val DEFAULT_CIRCLE_Y_RADIUS = 30f

        private const val DEFAULT_POINTER_STROKE_WIDTH = 14f

        private const val DEFAULT_POINTER_HALO_WIDTH = 6f

        private const val DEFAULT_POINTER_HALO_BORDER_WIDTH = 0f

        private const val DEFAULT_CIRCLE_STROKE_WIDTH = 5f

        private const val DEFAULT_START_ANGLE = 270f // Geometric (clockwise, relative to 3 o'clock)

        private const val DEFAULT_END_ANGLE = 270f // Geometric (clockwise, relative to 3 o'clock)

        private const val DEFAULT_POINTER_ANGLE = 0f

        private const val DEFAULT_MAX = 100

        private const val DEFAULT_PROGRESS = 0

        private const val DEFAULT_CIRCLE_COLOR = Color.DKGRAY

        private val DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255)

        private val DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255)

        private val DEFAULT_POINTER_HALO_COLOR = Color.argb(135, 74, 138, 255)

        private val DEFAULT_POINTER_HALO_COLOR_ONTOUCH = Color.argb(135, 74, 138, 255)

        private const val DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT

        private const val DEFAULT_POINTER_ALPHA = 135

        private const val DEFAULT_POINTER_ALPHA_ONTOUCH = 100

        private const val DEFAULT_POINTER_ICON: Int = R.drawable.ic_pointer_head

        private const val DEFAULT_USE_CUSTOM_RADII = false

        private const val DEFAULT_MAINTAIN_EQUAL_CIRCLE = true

        private const val DEFAULT_MOVE_OUTSIDE_CIRCLE = false

        private const val DEFAULT_LOCK_ENABLED = true

        private const val DEFAULT_DISABLE_POINTER = false

        private const val DEFAULT_NEGATIVE_ENABLED = false

    }

}