package com.zxlee.timeaxisview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by lzx on 2017/1/3 0003 上午 9:31.
 * Description: Time Axis View, as the name shows, draws a horizontal axis, include valid period with icons and invalid period with apostrophe.
 */
public class TimeAxisView extends View {

    private static final String TAG = "TimeAxisView";
    private static final float DEFAULT_TIME_HALF_WIDTH = 35f;
    /**
     * Paints
     */
    private Paint validLineP = new Paint();
    private Paint apostropheP = new Paint();
    private Paint circlePaint = new Paint();
    private Paint timeTxtPaint = new Paint();
    /**
     * Context
     */
    private Context mContext;
    /**
     * This two default params will be used in condition of WRAP_CONTENT
     */
    private int DEFAULT_WIDTH;
    private int DEFAULT_HEIGHT;
    /**
     * Default radius of every circle
     */
    private float radius;
    /**
     * Default time text size
     */
    private float timeTextSize;
    /**
     * Default length of valid line and apostrophe
     */
    private int validPeriodLength;
    private int inValidPeriodLength;
    /**
     * The size of cyclic items
     */
    private int size = 0;
    /**
     * The total length of every cyclic item
     */
    private float everyTotalLength;
    /**
     * Two distance
     * The ready icon and the first circle
     * The finish icon and the last circle
     */
    private static final float DIS_TERMINAL_CIRCLE = 10;
    /**
     * The distance between top icons and its below circles
     * or distance between circles and its below text
     */
    private static final float DIS_TOP_ICON_CIRCLE = 10;
    /**
     * The bitmap list for icons, such as ready, take off, land and finish
     */
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    /**
     * The hightest height between take off icon, forward icon and land icon
     */
    private int iconHeightHightest;
    /**
     * Five index or status of fly icon
     */
    private static final int ICON_INDEX_READY = 0;
    private static final int ICON_INDEX_TAKE_OFF = 1;
    private static final int ICON_INDEX_FORWARD = 2;
    private static final int ICON_INDEX_LAND = 3;
    private static final int ICON_INDEX_FINISH = 4;
    private static final int ICON_INDEX_I = 5;
    /**
     * The listener of letter i
     */
    private OnItemInfoClickListener mOnItemInfoClickListener;

    private RecordEntity mRecord;

    public TimeAxisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        //Init two length
        inValidPeriodLength = 100;
        validPeriodLength = Util.dip2px(context, 70);
        //Init self properties
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeAxisView);
        //4 colors for valid period line, invalid apostrophe, common circle and timeColor
        int validPeriodColor = typedArray.getColor(R.styleable.TimeAxisView_color_valid_period, ContextCompat.getColor(context, R.color.green_valid_line));
        int inValidPeriodColor = typedArray.getColor(R.styleable.TimeAxisView_color_invalid_period, Color.GRAY);
        int circleColor = typedArray.getColor(R.styleable.TimeAxisView_color_circle, Color.GRAY);
        int timeColor = typedArray.getColor(R.styleable.TimeAxisView_color_time, Color.GRAY);
        //common cirle radius
        radius = typedArray.getDimension(R.styleable.TimeAxisView_circle_radius, 8);
        //textSize of time
        timeTextSize = typedArray.getDimension(R.styleable.TimeAxisView_text_time_size, 28);
        //init fly status icons
        bitmaps.add(ICON_INDEX_READY, BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.TimeAxisView_img_ready, R.mipmap.ready)));
        bitmaps.add(ICON_INDEX_TAKE_OFF, BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.TimeAxisView_img_up, R.mipmap.take_off)));
        bitmaps.add(ICON_INDEX_FORWARD, BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.TimeAxisView_img_forward, R.mipmap.forward)));
        bitmaps.add(ICON_INDEX_LAND, BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.TimeAxisView_img_down, R.mipmap.land)));
        bitmaps.add(ICON_INDEX_FINISH, BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.TimeAxisView_img_finish, R.mipmap.finish)));
        bitmaps.add(ICON_INDEX_I, BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.TimeAxisView_img_i, R.mipmap.i)));
        typedArray.recycle();

        //valid line paint
        validLineP.setAntiAlias(true);
        validLineP.setDither(true);
        validLineP.setColor(validPeriodColor);
        validLineP.setStyle(Paint.Style.FILL);
        //invalid apostrophe paint
        apostropheP.setAntiAlias(true);
        apostropheP.setDither(true);
        apostropheP.setColor(inValidPeriodColor);
        apostropheP.setStyle(Paint.Style.FILL);
        apostropheP.setTextSize(50);
        //common circle paint
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);
        //time text paint
        timeTxtPaint.setAntiAlias(true);
        timeTxtPaint.setDither(true);
        timeTxtPaint.setColor(timeColor);
        timeTxtPaint.setTextSize(timeTextSize);

        //To make it support padding and calculate default width and height
        initSeveralLength();
    }

    private void initSeveralLength() {
        //calculate the total length of every cyclic item
        everyTotalLength = inValidPeriodLength + radius * 2 + validPeriodLength + radius * 2;
        //calculate the hightest int of top 3 icons
        iconHeightHightest = Util.calculateHightestIcon(bitmaps);
    }

    private void resetDefaultDimension() {
        //calculate the height and width of this view
        DEFAULT_WIDTH = (int) (getPaddingLeft() + bitmaps.get(ICON_INDEX_READY).getWidth() + DIS_TERMINAL_CIRCLE + radius * 2 + everyTotalLength * size + inValidPeriodLength + radius * 2 + DIS_TERMINAL_CIRCLE + bitmaps.get(ICON_INDEX_FORWARD).getWidth() + getPaddingRight());
        DEFAULT_HEIGHT = (int) (getPaddingTop() + iconHeightHightest + DIS_TOP_ICON_CIRCLE + radius * 2 + Math.max(iconHeightHightest / 2, radius + DIS_TOP_ICON_CIRCLE + timeTextSize) + getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //calculate the height and width of this view
        resetDefaultDimension();
        //To make it support WRAP_CONTENT
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        /**
         * At most time we only need to match the property of MeasureSpec.AT_MOST,
         * but sometimes his parent, like scrolled views, will give him a unspecified width or height property,
         * therefore, we also need to match the property of MeasureSpec.UNSPECIFIED.
         */
        if ((widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) && (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED)) {
            setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } else if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(DEFAULT_WIDTH, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(widthSpecSize, DEFAULT_HEIGHT);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        //distance between axis and view top
        int topDistance = (int) (paddingTop + iconHeightHightest + DIS_TOP_ICON_CIRCLE + radius);

        int iconReadyWidth = bitmaps.get(ICON_INDEX_READY).getWidth();
        int iconReadyHeight = bitmaps.get(ICON_INDEX_READY).getHeight();

        int halfTakeOffIconWidth = bitmaps.get(ICON_INDEX_TAKE_OFF).getWidth() / 2;
        int halfLandIconWidth = bitmaps.get(ICON_INDEX_LAND).getWidth() / 2;

        //distance between letter "i" and view top
        int iTopDistance = paddingTop + iconHeightHightest - bitmaps.get(ICON_INDEX_I).getHeight();
        int halfIIconWidth = bitmaps.get(ICON_INDEX_I).getWidth() / 2;

        //set the half with of time text.If there is no data, use default value, or use the first start time to calculate
        float textHalfWidth = size == 0 ? DEFAULT_TIME_HALF_WIDTH : timeTxtPaint.measureText(mRecord.mFlyList.get(0).startTime) / 2;
        //a flag represent the valid line is finished normally or not
        boolean isHalfFlying = false;

        /**
         * At the beginning we need to draw a bitmap and circle
         * which means the time axis starts.
         */
        canvas.drawBitmap(
                bitmaps.get(ICON_INDEX_READY),
                paddingLeft,
                topDistance - iconReadyHeight / 2,
                apostropheP);
        canvas.drawCircle(
                paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius,
                topDistance,
                radius,
                circlePaint);

        /**
         * In the middle part we need to draw four period which include
         * invalid apostrophe, valid start circle, valid line and valid end circle,
         * more important is, cyclic.
         */
        for (int i = 0; i < size; i++) {
            SubSection subSection = mRecord.mFlyList.get(i);
            if (subSection == null) {
                continue;
            }
            //0 apostrophe
            canvas.drawText(
                    ". . .",
                    paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + 20, //add several pixels to make sure it is center between two circles roughly
                    topDistance + 2, //add 2 pixels to keep it is at the same level with other elements
                    apostropheP);
            //1 start circle
            canvas.drawCircle(
                    paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength + radius,
                    topDistance,
                    radius,
                    circlePaint);
            //1.1 a take off bitmap above the start circle
            canvas.drawBitmap(
                    bitmaps.get(ICON_INDEX_TAKE_OFF),
                    paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength + radius - halfTakeOffIconWidth,
                    paddingTop,
                    apostropheP);
            //1.2 time text below the start circle
            String startTime = subSection.startTime;
            if (!TextUtils.isEmpty(startTime)) {
                canvas.drawText(
                        startTime,
                        paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength - textHalfWidth + radius,
                        topDistance + radius + DIS_TOP_ICON_CIRCLE + timeTextSize,
                        timeTxtPaint);
            }
            //2 valid period line
            canvas.drawLine(
                    paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength + radius * 2,
                    topDistance,
                    paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength + radius * 2 + validPeriodLength,
                    topDistance,
                    validLineP);

            /**
             * 3 Draw a land bitmap above the end circle and end time text below the end circle
             *     when had landed
             *     or draw nothing when is running
             */
            if (subSection.flyStatus != 0) {
                //2.1 had landed - draw a icon of letter 'i' center above the valid line
                canvas.drawBitmap(
                        bitmaps.get(ICON_INDEX_I),
                        paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength + radius * 2 + validPeriodLength / 2 - halfIIconWidth,
                        iTopDistance,
                        apostropheP);
                //3.1 had landed - draw end circle
                canvas.drawCircle(
                        paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength + radius * 2 + validPeriodLength + radius,
                        topDistance,
                        radius,
                        circlePaint);
                //3.2 draw land bitmap
                canvas.drawBitmap(
                        bitmaps.get(ICON_INDEX_LAND),
                        paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength + radius * 2 + validPeriodLength + radius - halfLandIconWidth,
                        paddingTop,
                        apostropheP);
                //3.3 draw end time text below the end circle
                String endTime = subSection.endTime;
                if (!TextUtils.isEmpty(endTime)) {
                    canvas.drawText(
                            endTime,
                            paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength + radius * 2 + validPeriodLength - textHalfWidth + radius,
                            topDistance + radius + DIS_TOP_ICON_CIRCLE + timeTextSize,
                            timeTxtPaint);
                }
            } else {
                //3.4 is still flying , draw a forward bitmap above the circle position
                /*canvas.drawBitmap(
                        bitmaps.get(ICON_INDEX_FORWARD),
                        paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + (everyTotalLength) * i + inValidPeriodLength + radius * 2 + validPeriodLength + radius - halfLandIconWidth,
                        paddingTop,
                        apostropheP);*/
            }
        }

        /**
         * Before last we need to draw a apostrophe, a circle and a finish bitmap which means the time axis is over
         * or a forward bitmap which means the time axis is running.
         */
        //apostrophe
        canvas.drawText(
                ". . .",
                paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + everyTotalLength * size + 20, //add several pixels to make sure it is center between two circles roughly
                topDistance + 2, //add 2 pixels to keep it is at the same level with other elements
                apostropheP);
        //the last circle
        canvas.drawCircle(
                paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + everyTotalLength * size + inValidPeriodLength + radius,
                topDistance,
                radius,
                circlePaint);
        /**
         * At last we should draw a forward bitmap if it is running
         * or draw a finish bitmap if is over.
         */
        if (mRecord.isOver) {
            //finish bitmap
            canvas.drawBitmap(
                    bitmaps.get(ICON_INDEX_FINISH),
                    paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + everyTotalLength * size + inValidPeriodLength + radius * 2 + DIS_TERMINAL_CIRCLE,
                    topDistance - bitmaps.get(ICON_INDEX_FINISH).getHeight() / 2,
                    apostropheP);
        } else {
            //forward bitmap
            canvas.drawBitmap(
                    bitmaps.get(ICON_INDEX_FORWARD),
                    paddingLeft + iconReadyWidth + DIS_TERMINAL_CIRCLE + radius * 2 + everyTotalLength * size + inValidPeriodLength + radius * 2 + DIS_TERMINAL_CIRCLE,
                    topDistance - bitmaps.get(ICON_INDEX_FORWARD).getHeight() / 2,
                    apostropheP);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //get the current touch point
        float x = event.getX();
        float y = event.getY();
        //get the fixed y range
        float iIconYTop = getPaddingTop() + iconHeightHightest - bitmaps.get(ICON_INDEX_I).getHeight();
        float iIconYBottom = getPaddingTop() + iconHeightHightest - bitmaps.get(ICON_INDEX_I).getHeight() + bitmaps.get(ICON_INDEX_I).getHeight();
        //record the click index
        int clickIndex = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (y > iIconYTop && y < iIconYBottom) {
                    // traverse the data list to find which the user clicks.
                    for (int i = 0; i < size; i++) {
                        //need to calculate the x range dynamically
                        float iIconXLeft = getPaddingLeft() + bitmaps.get(ICON_INDEX_READY).getWidth() + DIS_TERMINAL_CIRCLE + radius * 2 + inValidPeriodLength + radius * 2 + validPeriodLength / 2 + everyTotalLength * i - bitmaps.get(ICON_INDEX_I).getWidth() / 2;
                        float iIconXRight = getPaddingLeft() + bitmaps.get(ICON_INDEX_READY).getWidth() + DIS_TERMINAL_CIRCLE + radius * 2 + inValidPeriodLength + radius * 2 + validPeriodLength / 2 + everyTotalLength * i + bitmaps.get(ICON_INDEX_I).getWidth() / 2;

                        if (x > iIconXLeft && x < iIconXRight) {
                            clickIndex = i;
                            break;
                        }
                    }

                    if (mOnItemInfoClickListener != null && clickIndex > -1) {
                        mOnItemInfoClickListener.onItemInfoClick(mRecord.mFlyList.get(clickIndex).flyID);
                        //Here click the letter "i", means deal with touch event itself, therefore, this touch event finished.
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        //Deal to it's parent view, maybe it's parent view will continue to dispatch this event.
        return super.onTouchEvent(event);
    }

    public void setData(RecordEntity record) {
        mRecord = record;
        if (mRecord != null && mRecord.mFlyList != null) {
            size = mRecord.mFlyList.size();
        }
        requestLayout();
    }

    public void setOnItemInfoClickListener(OnItemInfoClickListener listener) {
        this.mOnItemInfoClickListener = listener;
    }


}
