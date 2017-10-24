package com.example.pheec.tablefixheaders;

/**
 * Created by pheec on 2017/10/24.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This view shows a table which can scroll in both directions. Also still
 * leaves the headers fixed.
 *
 * @author Brais Gab铆n (InQBarna)
 */
public class TableFixHeaders extends View {

    private BaseTableAdapter adapter;
    private ViewPlaceHolderContainer viewPlaceHolderContainer;
    private int scrollX;
    private int scrollY;
    private int firstRow;
    private int firstColumn;
    private boolean needRelayout;
    private ViewPlaceHolder headView;
    private List<ViewPlaceHolder> rowViewList;
    private List<ViewPlaceHolder> columnViewList;
    private List<List<ViewPlaceHolder>> bodyViewTable;
    // cells outside of screen but merged with cells inside screen
    private List<List<ViewPlaceHolder>> topleftViewForMerge;
    private List<List<ViewPlaceHolder>> topViewForMerge;
    private List<List<ViewPlaceHolder>> leftViewForMerge;
    private Map<Integer, Map<Integer, Integer>> mergeWidthCache;
    private Map<Integer, Map<Integer, Integer>> mergeHeightCache;
    private final Flinger flinger;
    private int touchSlop;
    private final int minimumVelocity;
    private final int maximumVelocity;
    private Paint paint = new Paint();
    private List<Typeface> typefaceList;
    private List<List<Integer>> positionForTextPaint;
    private float[] sparseDotInterval = new float[2];
    private float[] denseDotInterval = new float[2];
    private int cellPaddingHorizontal = 0;
    private int cellPaddingVertical = 0;
    private int rowCount;
    private int columnCount;
    private int[] sumWidth;
    private int[] widths;
    private int[] sumHeight;
    private int[] heights;
    private int width;
    private int height;
    private int formerWidth = 0;//for rotation
    private Bitmap cacheBitmap;
    private Canvas cacheCanvas = new Canvas();
    private Bitmap cacheBitmapBack;
    private Canvas cacheCanvasBack = new Canvas();
    private Boolean canvasReady;
    private int boarderMakeup;
    private int cacheX;
    private int cacheY;
    private List<CellPosition> dirtyCells = new ArrayList<CellPosition>();
    private int topRowForMerge;
    private int leftColumnForMerge;
    private boolean intercept = false;
    private int currentX;
    private int currentY;
    private VelocityTracker velocityTracker;
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public TableFixHeaders(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public TableFixHeaders(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.headView = null;
        this.rowViewList = new ArrayList<>();
        this.columnViewList = new ArrayList<>();
        this.bodyViewTable = new ArrayList<>();
        this.topleftViewForMerge = new ArrayList<>();
        this.topViewForMerge = new ArrayList<>();
        this.leftViewForMerge = new ArrayList<>();
        this.mergeWidthCache = new HashMap<>();
        this.mergeHeightCache = new HashMap<>();
        this.needRelayout = true;
        this.flinger = new Flinger(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        this.touchSlop = configuration.getScaledTouchSlop();
        this.minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        //this.maximumVelocity = 100;
        this.setWillNotDraw(false);
        //paint.setAlpha(255);
        paint.setAntiAlias(true);
        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setDither(false);
        paint.setFilterBitmap(false);
        //setLayerType(LAYER_TYPE_NONE, paint);
        positionForTextPaint = new ArrayList<>();
        typefaceList = new ArrayList<>();
        typefaceList.add(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        typefaceList.add(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        typefaceList.add(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));
        typefaceList.add(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
        typefaceList.add(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
        typefaceList.add(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        typefaceList.add(Typeface.create(Typeface.SERIF, Typeface.ITALIC));
        typefaceList.add(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC));
        typefaceList.add(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        typefaceList.add(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        typefaceList.add(Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC));
        typefaceList.add(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD_ITALIC));
        float density = context.getResources().getDisplayMetrics().density;
        sparseDotInterval[0] = 5.0f * density + 0.5f;
        sparseDotInterval[1] = 5.0f * density + 0.5f;
        denseDotInterval[0] = 3.0f * density + 0.5f;
        denseDotInterval[1] = 3.0f * density + 0.5f;
        cellPaddingHorizontal = (int) context.getResources().getDimension(R.dimen.cell_padding_horizontal);
        cellPaddingVertical = (int) context.getResources().getDimension(R.dimen.cell_padding_vertical);
    }

    /**
     * Returns the adapter currently associated with this widget.
     *
     * @return The adapter used to provide this view's content.
     */
    public TableAdapter getAdapter() {
        return adapter;
    }

    /**
     * Sets the data behind this TableFixHeaders.
     *
     * @param adapter The TableAdapter which is responsible for maintaining the data
     *                backing this list and for producing a view to represent an
     *                item in that data set.
     */
    public void setAdapter(BaseTableAdapter adapter) {

        this.adapter = adapter;
        this.viewPlaceHolderContainer = new ViewPlaceHolderContainer(adapter.getViewTypeCount());
        scrollX = 0;
        scrollY = 0;
        firstColumn = 0;
        firstRow = 0;
        needRelayout = true;
        requestLayout();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!intercept) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if (!flinger.isFinished()) { // If scrolling, then stop now
                        flinger.forceFinished();
                    }
                    currentX = (int) event.getRawX();
                    currentY = (int) event.getRawY();

                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int x2 = Math.abs(currentX - (int) event.getRawX());
                    int y2 = Math.abs(currentY - (int) event.getRawY());
                    if (x2 > touchSlop || y2 > touchSlop) {
                        intercept = true;
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {

                    break;
                }
            }
        } else if (intercept) {

            if (velocityTracker == null) { // If we do not have velocity tracker
                velocityTracker = VelocityTracker.obtain(); // then get one
            }
            velocityTracker.addMovement(event); // add this movement to it

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if (!flinger.isFinished()) { // If scrolling, then stop now
                        flinger.forceFinished();
                    }
                    currentX = (int) event.getRawX();
                    currentY = (int) event.getRawY();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    final int x2 = (int) event.getRawX();
                    final int y2 = (int) event.getRawY();
                    final int diffX = currentX - x2;
                    final int diffY = currentY - y2;
                    currentX = x2;
                    currentY = y2;
                    scrollBy(diffX, diffY);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    final VelocityTracker velocityTracker = this.velocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();
                    int velocityY = (int) velocityTracker.getYVelocity();
                    intercept = false;
                    if (Math.abs(velocityX) > minimumVelocity || Math.abs(velocityY) > minimumVelocity) {
                        if (Math.abs(velocityX) > 2 * Math.abs(velocityY)) {
                            flinger.start(getActualScrollX(), getActualScrollY(), velocityX, 0, getMaxScrollX(), getMaxScrollY());
                        } else if (2 * Math.abs(velocityX) < Math.abs(velocityY)) {
                            flinger.start(getActualScrollX(), getActualScrollY(), 0, velocityY, getMaxScrollX(), getMaxScrollY());
                        } else {
                            flinger.start(getActualScrollX(), getActualScrollY(), velocityX, velocityY, getMaxScrollX(), getMaxScrollY());
                        }
                    } else {
                        if (this.velocityTracker != null) { // If the velocity less than threshold
                            this.velocityTracker.recycle(); // recycle the tracker
                            this.velocityTracker = null;
                        }
                    }
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollBy(x, y, false);
    }




    public void scrollBy(int x, int y, Boolean passive) {
        if (null == adapter) {

            return;
        }
        /*
        if(Math.abs(x) > 2 * Math.abs(y)){
            y=0;
        } else if(2 * Math.abs(x) < Math.abs(y)){
            x=0;
        }*/


        int formerScrollX = scrollX;
        int formerScrollY = scrollY;

        scrollX += x;
        scrollY += y;

        if (needRelayout) {
            return;
        }
        clearForMerge();
        scrollBounds();

        int offsetxForLaterUse = scrollX - formerScrollX;
        int offsetyForLaterUse = scrollY - formerScrollY;

      /*
       * TODO Improve the algorithm. Think big diagonal movements. If we are
       * in the top left corner and scrollBy to the opposite corner. We will
       * have created the views from the top right corner on the X part and we
       * will have eliminated to generate the right at the Y.
       */
        if (scrollX == 0) {
            // no op
        } else if (scrollX > 0) {
            while (widths[firstColumn + 1] < scrollX) {
                if (!rowViewList.isEmpty()) {
                    removeLeft();
                }
                scrollX -= widths[firstColumn + 1];
                firstColumn++;
            }
            while (getFilledWidth() < width) {
                addRight();
            }
        } else {
            while (!rowViewList.isEmpty() && getFilledWidth() - widths[firstColumn + rowViewList.size()] >= width) {
                removeRight();
            }
            if (rowViewList.isEmpty()) {
                while (scrollX < 0) {
                    firstColumn--;
                    scrollX += widths[firstColumn + 1];
                }
                while (getFilledWidth() < width) {
                    addRight();
                }
            } else {
                while (0 > scrollX) {
                    addLeft();
                    firstColumn--;
                    scrollX += widths[firstColumn + 1];
                }
            }
        }

        if (scrollY == 0) {
            // no op
        } else if (scrollY > 0) {
            while (heights[firstRow + 1] < scrollY) {
                if (!columnViewList.isEmpty()) {
                    removeTop();
                }
                scrollY -= heights[firstRow + 1];
                firstRow++;
            }
            while (getFilledHeight() < height) {
                addBottom();
            }
        } else {
            while (!columnViewList.isEmpty() && getFilledHeight() - heights[firstRow + columnViewList.size()] >= height) {
                removeBottom();
            }
            if (columnViewList.isEmpty()) {
                while (scrollY < 0) {
                    firstRow--;
                    scrollY += heights[firstRow + 1];
                }
                while (getFilledHeight() < height) {
                    addBottom();
                }
            } else {
                while (0 > scrollY) {
                    addTop();
                    firstRow--;
                    scrollY += heights[firstRow + 1];
                }
            }
        }

        repositionViews();
        compensateForMerge();
        invalidate();


        awakenScrollBars();

    }



    @Override
    public void scrollTo(int x, int y) {
        if (needRelayout) {
            scrollX = x;
            firstColumn = 0;

            scrollY = y;
            firstRow = 0;
        } else {
            scrollBy(x - sumArray(widths, 1, firstColumn) - scrollX, y - sumArray(heights, 1, firstRow) - scrollY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boarderMakeup++;
        if (canvasReady) {

            //int newcacheX = sumArray(widths,0 ,firstColumn + 1) + scrollX - widths[0];
            int newcacheX = sumWidth(0, firstColumn + 1) + scrollX - widths[0];
            //int newcacheY = sumArray(heights, 0, firstRow + 1) + scrollY - heights[0];
            int newcacheY = sumHeight(0, firstRow + 1) + scrollY - heights[0];
            if (cacheX - newcacheX == 0 && cacheY - newcacheY == 0) {

            } else {
                cacheCanvasBack.drawBitmap(cacheBitmap, -newcacheX + cacheX, -newcacheY + cacheY, null);
                cacheCanvas.drawBitmap(cacheBitmapBack, 0, 0, null);
            }
            for (List<ViewPlaceHolder> listEntry : topleftViewForMerge) {
                for (ViewPlaceHolder entry : listEntry) {
                    if (entry != null)
                        drawView(entry, cacheCanvas);
                }


            }
            for (List<ViewPlaceHolder> listEntry : topViewForMerge) {
                for (ViewPlaceHolder entry : listEntry) {
                    if (entry != null)
                        drawView(entry, cacheCanvas);
                }


            }
            for (List<ViewPlaceHolder> listEntry : leftViewForMerge) {
                for (ViewPlaceHolder entry : listEntry) {
                    if (entry != null)
                        drawView(entry, cacheCanvas);
                }


            }

            //int absoluteX = sumArray(widths,0 ,firstColumn + 1) + scrollX - widths[0];
            //int absoluteY = sumArray(heights, 0, firstRow + 1) + scrollY - heights[0];
            int absoluteX = sumWidth(0, firstColumn + 1) + scrollX - widths[0];
            int absoluteY = sumHeight(0, firstRow + 1) + scrollY - heights[0];
            for (List<ViewPlaceHolder> listEntry : bodyViewTable) {
                for (ViewPlaceHolder entry : listEntry) {
                    boolean needShow = entry.getLeft() + absoluteX > cacheX + widths[0] && entry.getRight() + absoluteX < cacheX + width && entry.getTop() + absoluteY > cacheY + heights[0] && entry.getBottom() + absoluteY < height + cacheY;
                    // due to the performance of Android canvas
                    // repaint cell borders when scroll is finished
                    if (entry != null && flinger.isFinished()) {
                        drawViewBorder(entry, cacheCanvas);
                    }
                    if (entry != null && !needShow) {
                        // always draw cells on the edge of screen
                        drawView(entry, cacheCanvas);
                    } else if (entry != null && isDirty((Integer) entry.getTag(R.id.tag_row), (Integer) entry.getTag(R.id.tag_column))) {
                        // draw dirty cells
                        drawView(entry, cacheCanvas);
                    }
                }


            }
            canvas.drawBitmap(cacheBitmap, 0, 0, null);


            for (ViewPlaceHolder entry : rowViewList) {
                if (entry != null)
                    drawView(entry, canvas);
            }

            for (ViewPlaceHolder entry : columnViewList) {
                if (entry != null) {
                    drawView(entry, canvas);

                }
            }
            if (headView != null)
                drawView(headView, canvas);


            //cacheX = sumArray(widths,0 ,firstColumn + 1) + scrollX - widths[0];
            //cacheY = sumArray(heights, 0, firstRow + 1) + scrollY - heights[0];
            cacheX = sumWidth(0, firstColumn + 1) + scrollX - widths[0];
            cacheY = sumHeight(0, firstRow + 1) + scrollY - heights[0];


            dirtyCells.clear();
            return;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Rect r = new Rect(0, 0, metrics.widthPixels, metrics.heightPixels);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        cacheCanvas.drawRect(r, paint);

        for (List<ViewPlaceHolder> listEntry : topleftViewForMerge) {
            for (ViewPlaceHolder entry : listEntry) {
                if (entry != null)
                    drawView(entry, cacheCanvas);
            }


        }
        for (List<ViewPlaceHolder> listEntry : topViewForMerge) {
            for (ViewPlaceHolder entry : listEntry) {
                if (entry != null)
                    drawView(entry, cacheCanvas);
            }


        }
        for (List<ViewPlaceHolder> listEntry : leftViewForMerge) {
            for (ViewPlaceHolder entry : listEntry) {
                if (entry != null)
                    drawView(entry, cacheCanvas);
            }


        }

        for (List<ViewPlaceHolder> listEntry : bodyViewTable) {
            for (ViewPlaceHolder entry : listEntry) {
                if (entry != null)
                    drawView(entry, cacheCanvas);
            }
        }

        canvas.drawBitmap(cacheBitmap, 0, 0, null);


        for (ViewPlaceHolder entry : rowViewList) {
            if (entry != null)
                drawView(entry, canvas);
        }

        for (ViewPlaceHolder entry : columnViewList) {
            if (entry != null) {
                drawView(entry, canvas);

            }
        }
        if (headView != null)
            drawView(headView, canvas);

        //cacheX = sumArray(widths,0 ,firstColumn + 1) + scrollX - widths[0];
        //cacheY = sumArray(heights, 0, firstRow + 1) + scrollY - heights[0];
        cacheX = sumWidth(0, firstColumn + 1) + scrollX - widths[0];
        cacheY = sumHeight(0, firstRow + 1) + scrollY - heights[0];

        canvasReady = true;

        dirtyCells.clear();
    }

    private void drawViewBorder(ViewPlaceHolder holder, Canvas canvas) {

        if (holder.getLeft() == holder.getRight() && holder.getTop() == holder.getBottom()) {
            return;
        }
        paint.setStyle(Paint.Style.STROKE);
        //paint.setColor(ContextCompat.getColor(getContext(), R.color.bordergray));
        if ((ViewType) holder.getTag(R.id.tag_type_view) != ViewType.BODY) {
            paint.setStrokeWidth((float) 2.0f);
        } else {
            paint.setStrokeWidth((float) 1.0f);

        }
        if (holder.getTopBoarderShow()) {
            Path path;
            switch (holder.getTopBoarder()) {
                case EMPTY:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getLeft(), holder.getTop(), holder.getRight() - 1, holder.getTop(), paint);
                    break;
                case SOLID:
                    paint.setColor(holder.getTopBoarderColor());
                    canvas.drawLine(holder.getLeft(), holder.getTop(), holder.getRight(), holder.getTop(), paint);
                    break;
                case DASHED:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getLeft(), holder.getTop(), holder.getRight(), holder.getTop(), paint);
                    paint.setColor(holder.getTopBoarderColor());
                    path = new Path();
                    path.moveTo(holder.getLeft(), holder.getTop());
                    path.lineTo(holder.getRight(), holder.getTop());


                    DashPathEffect dashPathEffectSparse = new DashPathEffect(sparseDotInterval, 0);

                    paint.setPathEffect(dashPathEffectSparse);

                    canvas.drawPath(path, paint);
                    paint.setPathEffect(null);
                    break;
                case DOTTED:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getLeft(), holder.getTop(), holder.getRight(), holder.getTop(), paint);
                    paint.setColor(holder.getTopBoarderColor());
                    path = new Path();
                    path.moveTo(holder.getLeft(), holder.getTop());
                    path.lineTo(holder.getRight(), holder.getTop());


                    DashPathEffect dashPathEffectDense = new DashPathEffect(denseDotInterval, 0);

                    paint.setPathEffect(dashPathEffectDense);

                    canvas.drawPath(path, paint);
                    paint.setPathEffect(null);
                    break;

            }
        } else {
            paint.setColor(Color.WHITE);
            canvas.drawLine(holder.getLeft(), holder.getTop(), holder.getRight() - 1, holder.getTop(), paint);
        }

        if (holder.getBottomBoarderShow()) {
            Path path;
            switch (holder.getBottomBoarder()) {
                case EMPTY:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getLeft() + 1, holder.getBottom(), holder.getRight(), holder.getBottom(), paint);
                    break;
                case SOLID:
                    paint.setColor(holder.getBottomBoarderColor());
                    canvas.drawLine(holder.getLeft(), holder.getBottom(), holder.getRight(), holder.getBottom(), paint);
                    break;
                case DASHED:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getLeft(), holder.getBottom(), holder.getRight(), holder.getBottom(), paint);
                    paint.setColor(holder.getBottomBoarderColor());
                    path = new Path();
                    path.moveTo(holder.getLeft(), holder.getBottom());
                    path.lineTo(holder.getRight(), holder.getBottom());


                    DashPathEffect dashPathEffectSparse = new DashPathEffect(sparseDotInterval, 0);

                    paint.setPathEffect(dashPathEffectSparse);

                    canvas.drawPath(path, paint);
                    paint.setPathEffect(null);
                    break;
                case DOTTED:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getLeft(), holder.getBottom(), holder.getRight(), holder.getBottom(), paint);
                    paint.setColor(holder.getBottomBoarderColor());
                    path = new Path();
                    path.moveTo(holder.getLeft(), holder.getBottom());
                    path.lineTo(holder.getRight(), holder.getBottom());


                    DashPathEffect dashPathEffectDense = new DashPathEffect(denseDotInterval, 0);

                    paint.setPathEffect(dashPathEffectDense);

                    canvas.drawPath(path, paint);
                    paint.setPathEffect(null);
                    break;

            }
        } else {
            paint.setColor(Color.WHITE);
            canvas.drawLine(holder.getLeft() + 1, holder.getBottom(), holder.getRight(), holder.getBottom(), paint);
        }
        if (holder.getLeftBoarderShow()) {
            Path path;
            switch (holder.getLeftBoarder()) {
                case EMPTY:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getLeft(), holder.getTop() + 1, holder.getLeft(), holder.getBottom(), paint);
                    break;
                case SOLID:
                    paint.setColor(holder.getLeftBoarderColor());
                    canvas.drawLine(holder.getLeft(), holder.getTop(), holder.getLeft(), holder.getBottom(), paint);
                    break;
                case DASHED:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getLeft(), holder.getTop(), holder.getLeft(), holder.getBottom(), paint);
                    paint.setColor(holder.getLeftBoarderColor());
                    path = new Path();
                    path.moveTo(holder.getLeft(), holder.getTop());
                    path.lineTo(holder.getLeft(), holder.getBottom());


                    DashPathEffect dashPathEffectSparse = new DashPathEffect(sparseDotInterval, 0);

                    paint.setPathEffect(dashPathEffectSparse);

                    canvas.drawPath(path, paint);
                    paint.setPathEffect(null);
                    break;
                case DOTTED:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getLeft(), holder.getTop(), holder.getLeft(), holder.getBottom(), paint);
                    paint.setColor(holder.getLeftBoarderColor());
                    path = new Path();
                    path.moveTo(holder.getLeft(), holder.getTop());
                    path.lineTo(holder.getLeft(), holder.getBottom());


                    DashPathEffect dashPathEffectDense = new DashPathEffect(denseDotInterval, 0);

                    paint.setPathEffect(dashPathEffectDense);

                    canvas.drawPath(path, paint);
                    paint.setPathEffect(null);
                    break;

            }
        } else {
            paint.setColor(Color.WHITE);
            canvas.drawLine(holder.getLeft(), holder.getTop() + 1, holder.getLeft(), holder.getBottom(), paint);
        }

        if (holder.getRightBoarderShow()) {
            Path path;
            switch (holder.getRightBoarder()) {
                case EMPTY:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getRight(), holder.getTop(), holder.getRight(), holder.getBottom() - 1, paint);
                    break;
                case SOLID:
                    paint.setColor(holder.getRightBoarderColor());
                    canvas.drawLine(holder.getRight(), holder.getTop(), holder.getRight(), holder.getBottom(), paint);
                    break;
                case DASHED:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getRight(), holder.getTop(), holder.getRight(), holder.getBottom(), paint);
                    paint.setColor(holder.getRightBoarderColor());
                    path = new Path();
                    path.moveTo(holder.getRight(), holder.getTop());
                    path.lineTo(holder.getRight(), holder.getBottom());


                    DashPathEffect dashPathEffectSparse = new DashPathEffect(sparseDotInterval, 0);

                    paint.setPathEffect(dashPathEffectSparse);

                    canvas.drawPath(path, paint);
                    paint.setPathEffect(null);
                    break;
                case DOTTED:
                    paint.setColor(Color.WHITE);
                    canvas.drawLine(holder.getRight(), holder.getTop(), holder.getRight(), holder.getBottom(), paint);
                    paint.setColor(holder.getRightBoarderColor());
                    path = new Path();
                    path.moveTo(holder.getRight(), holder.getTop());
                    path.lineTo(holder.getRight(), holder.getBottom());


                    DashPathEffect dashPathEffectDense = new DashPathEffect(denseDotInterval, 0);

                    paint.setPathEffect(dashPathEffectDense);

                    canvas.drawPath(path, paint);
                    paint.setPathEffect(null);
                    break;

            }
        } else {
            paint.setColor(Color.WHITE);
            canvas.drawLine(holder.getRight(), holder.getTop(), holder.getRight(), holder.getBottom() - 1, paint);
        }
    }

    private void drawView(ViewPlaceHolder holder, Canvas canvas) {

        if (holder.getLeft() == holder.getRight() || holder.getTop() == holder.getBottom()) {
            return;
        }
        drawViewBorder(holder, canvas);
        if (holder.getUnderline()) {
            paint.setFlags(paint.getFlags() | Paint.UNDERLINE_TEXT_FLAG);

        }
        if (holder.getStrikethrough()) {
            paint.setFlags(paint.getFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }




        paint.setStyle(Paint.Style.FILL);
        paint.setColor(holder.getBgColor());
        canvas.drawRect(holder.getLeft() + 1, holder.getTop() + 1, holder.getRight() - 1, holder.getBottom() - 1, paint);
        paint.setColor(holder.getFontColor());
        float textsize = holder.getTextSize();
        paint.setTextSize(textsize);
        int indexBegin = 0;
        switch (holder.getFontFamily()) {
            case SANS_SERIF:
                indexBegin = 0;
                break;
            case SERIF:
                indexBegin = 4;
                break;
            case MONOSPACE:
                indexBegin = 8;
                break;
        }
        switch (holder.getFontStyle()) {
            case NORMAL:
                paint.setTypeface(typefaceList.get(0 + indexBegin));
                break;
            case BOLD:
                paint.setTypeface(typefaceList.get(1 + indexBegin));
                break;
            case ITALIC:
                paint.setTypeface(typefaceList.get(2 + indexBegin));
                break;
            case BOLD_ITALIC:
                paint.setTypeface(typefaceList.get(3 + indexBegin));
                break;


        }
        if (holder.getText() != null) {
            String[] textClip = holder.getText();
            positionForTextPaint.clear();
            int linedis = 2;
            int totalline = 0;
            boolean textWrap = false;
            for (int i = 0; i < textClip.length; i++) {

                List<Integer> entry = new ArrayList<Integer>();
                positionForTextPaint.add(entry);
                int begin = 0;

                int number;

                number = paint.breakText(textClip[i].toCharArray(), begin, textClip[i].length() - begin, holder.getRight() - holder.getLeft() - 2 - 2 * cellPaddingHorizontal, null);

                if (number < textClip[i].length()) {
                    textWrap = true;
                }


                entry.add(number);
                totalline++;

            }
            int ypos = 0;
            switch (holder.getVAlign()) {
                case TOP:
                    ypos = (int) textsize + 1 + holder.getTop();
                    break;
                case NORMAL:
                    int topStart = 1 + holder.getTop();
                    float cellActualHeight = holder.getBottom() - holder.getTop() - 2;
                    float textHeight = (textsize + linedis) * totalline;
                    float aboveEmptyHeight = (cellActualHeight - textHeight) / 2;
                    ypos = (int) (topStart + aboveEmptyHeight - paint.getFontMetrics().ascent);
                    break;
                case BOTTOM:
                    ypos = (int) (holder.getBottom() - paint.getFontMetrics().descent - ((textsize + linedis) * (totalline - 1)));
                    break;
            }
            if (ypos < textsize + 1 + holder.getTop()) {
                ypos = (int) (textsize + 1 + holder.getTop());
            }
            if (ypos > holder.getBottom() - 1) {
                ypos = holder.getBottom() - 1;
            }

            int xpos = 0;

            HorizontalAlign horizontalAlign = textWrap ? HorizontalAlign.LEFT : holder.getHAlign();
            switch (horizontalAlign) {
                case LEFT:
                    xpos = 1 + holder.getLeft() + cellPaddingHorizontal;
                    paint.setTextAlign(Paint.Align.LEFT);
                    break;
                case NORMAL:
                    xpos = (holder.getLeft() + holder.getRight()) / 2;
                    paint.setTextAlign(Paint.Align.CENTER);
                    break;
                case RIGHT:
                    xpos = holder.getRight() - 1 - cellPaddingHorizontal;
                    paint.setTextAlign(Paint.Align.RIGHT);
            }

            boolean needBreak = false;
            for (int index = 0; index < positionForTextPaint.size(); index++) {
                if (needBreak) break;
                List<Integer> listEntry = positionForTextPaint.get(index);
                int begin = 0;
                for (Integer entry : listEntry) {
                    canvas.drawText(textClip[index].toCharArray(), begin, entry, xpos, ypos, paint);
                    begin += entry;
                    ypos += linedis + textsize;
                    if (ypos > holder.getBottom() - 1) {
                        needBreak = true;
                        break;
                    }
                }

            }

        }
        if (holder.getUnderline()) {
            paint.setFlags(paint.getFlags() & (~Paint.UNDERLINE_TEXT_FLAG));

        }
        if (holder.getStrikethrough()) {
            paint.setFlags(paint.getFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        }



    }
    private void resetTable() {
        headView = null;
        rowViewList.clear();
        columnViewList.clear();
        bodyViewTable.clear();


    }

    private ViewPlaceHolder makeView(int row, int column, int w, int h) {
        if (null == adapter) {
            return null;
        }
        final ViewType itemViewType = adapter.getItemViewType(row, column);
        final ViewPlaceHolder recycledView;
        if (itemViewType == ViewType.IGNORE) {
            recycledView = null;
        } else {
            recycledView = viewPlaceHolderContainer.getRecycledView(itemViewType);
        }
        final ViewPlaceHolder view = adapter.getView(row, column, recycledView);
        if (view != null) {
            view.setTag(R.id.tag_type_view, itemViewType);
            view.setTag(R.id.tag_row, row);
            view.setTag(R.id.tag_column, column);

            if ((Boolean) view.getTag(R.id.inflated)) {
                view.setTag(R.id.inflated, false);
            }

            view.setTag(R.id.needLayout, true);
        }
        return view;
    }




    private ViewPlaceHolder makeAndSetup(int row, int column, int left, int top, int right, int bottom) {
        final ViewPlaceHolder view = makeView(row, column, right - left, bottom - top);

        if (view != null) {
            view.setTag(R.id.needLayout, false);
            if (topleftMerge(row, column)) {
                int layoutwidth = mergedWidth(row, column, adapter.getMergeId(row, column));
                int layoutheight = mergedHeight(row, column, adapter.getMergeId(row, column));
                view.layout(left, top, left + layoutwidth, top + layoutheight);
            } else {
                view.layout(left, top, left + 0, top + 0);
                view.setText(null);
            }
        }
        return view;
    }

    private int mergedWidth(int row, int column, int mergeId) {
        if (mergeWidthCache.containsKey(row)) {
            if (mergeWidthCache.get(row).containsKey(column)) {
                return mergeWidthCache.get(row).get(column);
            }
        }
        int result = widths[column + 1];
        if (mergeId == 0) {
            if (!mergeWidthCache.containsKey(row)) {
                Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                map.put(column, result);
                mergeWidthCache.put(row, map);

            } else {
                mergeWidthCache.get(row).put(column, result);
            }
            return result;
        }
        while (adapter.getMergeId(row, column + 1) == mergeId) {
            result += widths[column + 2];
            column++;
        }
        if (!mergeWidthCache.containsKey(row)) {
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            map.put(column, result);
            mergeWidthCache.put(row, map);

        } else {
            mergeWidthCache.get(row).put(column, result);
        }
        return result;

    }

    private int mergedHeight(int row, int column, int mergeId) {
        if (mergeHeightCache.containsKey(row)) {
            if (mergeHeightCache.get(row).containsKey(column)) {
                return mergeHeightCache.get(row).get(column);
            }
        }

        int result = heights[row + 1];
        if (mergeId == 0) {
            if (!mergeHeightCache.containsKey(row)) {
                Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                map.put(column, result);
                mergeHeightCache.put(row, map);

            } else {
                mergeHeightCache.get(row).put(column, result);
            }
            return result;
        }
        while (adapter.getMergeId(row + 1, column) == mergeId) {
            result += heights[row + 2];
            row++;
        }
        if (!mergeHeightCache.containsKey(row)) {
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            map.put(column, result);
            mergeHeightCache.put(row, map);

        } else {
            mergeHeightCache.get(row).put(column, result);
        }
        return result;

    }

    private boolean topleftMerge(int row, int column, int matchMergeId) {
        if (row == -1 || column == -1) return false;
        if (matchMergeId == 0) return false;
        int mergeId = adapter.getMergeId(row, column);
        if (mergeId != matchMergeId) return false;
        if (row == 0) {
            if (column == 0) return true;
            if (adapter.getMergeId(row, column - 1) != mergeId) return true;
        } else if (column == 0) {
            if (adapter.getMergeId(row - 1, column) != mergeId) return true;
        } else {
            if (adapter.getMergeId(row - 1, column) != mergeId && adapter.getMergeId(row, column - 1) != mergeId)
                return true;
        }
        return false;

    }

    private boolean topleftMerge(int row, int column) {
        if (row == -1 || column == -1) return true;
        int mergeId = adapter.getMergeId(row, column);
        if (mergeId == 0) return true;
        if (row == 0) {
            if (column == 0) return true;
            if (adapter.getMergeId(row, column - 1) != mergeId) return true;
        } else if (column == 0) {
            if (adapter.getMergeId(row - 1, column) != mergeId) return true;
        } else {
            if (adapter.getMergeId(row - 1, column) != mergeId && adapter.getMergeId(row, column - 1) != mergeId)
                return true;
        }
        return false;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (needRelayout || changed) {
            needRelayout = false;
            resetTable();

            if (adapter != null) {
                width = r - l;
                height = b - t;

                int left, top, right, bottom;

                right = Math.min(width, sumArray(widths));
                bottom = Math.min(height, sumArray(heights));


                headView = makeAndSetup(-1, -1, 0, 0, widths[0], heights[0]);

                scrollBounds();
                adjustFirstCellsAndScroll();

                left = widths[0] - scrollX;
                for (int i = firstColumn; i < columnCount && left < width; i++) {
                    right = left + widths[i + 1];
                    final ViewPlaceHolder view = makeAndSetup(-1, i, left, 0, right, heights[0]);
                    rowViewList.add(view);
                    left = right;
                }

                top = heights[0] - scrollY;
                for (int i = firstRow; i < rowCount && top < height; i++) {
                    bottom = top + heights[i + 1];
                    final ViewPlaceHolder view = makeAndSetup(i, -1, 0, top, widths[0], bottom);
                    columnViewList.add(view);
                    top = bottom;
                }

                top = heights[0] - scrollY;
                for (int i = firstRow; i < rowCount && top < height; i++) {
                    bottom = top + heights[i + 1];
                    left = widths[0] - scrollX;
                    List<ViewPlaceHolder> list = new ArrayList<ViewPlaceHolder>();
                    for (int j = firstColumn; j < columnCount && left < width; j++) {
                        right = left + widths[j + 1];
                        final ViewPlaceHolder view = makeAndSetup(i, j, left, top, right, bottom);
                        list.add(view);
                        left = right;
                    }
                    bodyViewTable.add(list);
                    top = bottom;
                }

                compensateForMerge();
            }


        }
    }

    private void compensateForMerge() {
        if (null == adapter) {
            return;
        }
        int length = rowViewList.size();
        int mergeId;
        topRowForMerge = Integer.MAX_VALUE;
        for (int i = firstColumn; i < firstColumn + length; i++) {
            mergeId = adapter.getMergeId(firstRow, i);
            if (topRowForMergeForColumn(firstRow, i, mergeId) < topRowForMerge) {
                topRowForMerge = topRowForMergeForColumn(firstRow, i, mergeId);
            }

        }
        length = columnViewList.size();

        leftColumnForMerge = Integer.MAX_VALUE;
        for (int i = firstRow; i < firstRow + length; i++) {
            mergeId = adapter.getMergeId(i, firstColumn);
            if (leftColumnForMergeForRow(i, firstColumn, mergeId) < leftColumnForMerge) {
                leftColumnForMerge = leftColumnForMergeForRow(i, firstColumn, mergeId);
            }

        }


        if (topRowForMerge < firstRow && leftColumnForMerge < firstColumn) {

            for (int i = 0; i < firstRow - topRowForMerge; i++) {
                List<ViewPlaceHolder> oneRow = new ArrayList<ViewPlaceHolder>();
                for (int j = 0; j < firstColumn - leftColumnForMerge; j++) {
                    if (topleftMerge(i + topRowForMerge, j + leftColumnForMerge, adapter.getMergeId(firstRow, firstColumn))) {
                        int top = heights[0] - scrollY;
                        int currentRow = firstRow;
                        while (currentRow > i + topRowForMerge) {
                            top -= heights[currentRow];
                            currentRow--;
                        }
                        int left = widths[0] - scrollX;
                        int currentColumn = firstColumn;
                        while (currentColumn > j + leftColumnForMerge) {
                            left -= widths[currentColumn];
                            currentColumn--;
                        }
                        int layoutwidth = mergedWidth(i + topRowForMerge, j + leftColumnForMerge, adapter.getMergeId(i + topRowForMerge, j + leftColumnForMerge));
                        int layoutheight = mergedHeight(i + topRowForMerge, j + leftColumnForMerge, adapter.getMergeId(i + topRowForMerge, j + leftColumnForMerge));
                        ViewPlaceHolder view = makeView(i + topRowForMerge, j + leftColumnForMerge, layoutwidth, layoutheight);
                        view.layout(left, top, left + layoutwidth, top + layoutheight);
                        oneRow.add(view);
                    } else {
                        oneRow.add(null);
                    }


                }
                topleftViewForMerge.add(oneRow);
            }
        }
        if (topRowForMerge < firstRow) {

            for (int i = 0; i < firstRow - topRowForMerge; i++) {
                List<ViewPlaceHolder> oneRow = new ArrayList<ViewPlaceHolder>();
                for (int j = 0; j < rowViewList.size(); j++) {
                    if (topleftMerge(i + topRowForMerge, j + firstColumn, adapter.getMergeId(firstRow, j + firstColumn))) {
                        int top = heights[0] - scrollY;
                        int currentRow = firstRow;
                        while (currentRow > i + topRowForMerge) {
                            top -= heights[currentRow];
                            currentRow--;
                        }
                        int left = widths[0] - scrollX;

                        for (int k = 0; k < j; k++) {
                            left += widths[k + firstColumn + 1];

                        }
                        int layoutwidth = mergedWidth(i + topRowForMerge, j + firstColumn, adapter.getMergeId(i + topRowForMerge, j + firstColumn));
                        int layoutheight = mergedHeight(i + topRowForMerge, j + firstColumn, adapter.getMergeId(i + topRowForMerge, j + firstColumn));
                        ViewPlaceHolder view = makeView(i + topRowForMerge, j + firstColumn, layoutwidth, layoutheight);
                        view.layout(left, top, left + layoutwidth, top + layoutheight);

                        oneRow.add(view);
                    } else {
                        oneRow.add(null);
                    }


                }
                topViewForMerge.add(oneRow);
            }
        }

        if (leftColumnForMerge < firstColumn) {

            for (int i = 0; i < columnViewList.size(); i++) {
                List<ViewPlaceHolder> oneRow = new ArrayList<ViewPlaceHolder>();
                for (int j = 0; j < firstColumn - leftColumnForMerge; j++) {
                    if (topleftMerge(i + firstRow, j + leftColumnForMerge, adapter.getMergeId(i + firstRow, firstColumn))) {
                        int top = heights[0] - scrollY;

                        for (int k = 0; k < i; k++) {
                            top += heights[k + firstRow + 1];

                        }
                        int left = widths[0] - scrollX;
                        int currentColumn = firstColumn;
                        while (currentColumn > j + leftColumnForMerge) {
                            left -= widths[currentColumn];
                            currentColumn--;
                        }
                        int layoutwidth = mergedWidth(i + firstRow, j + leftColumnForMerge, adapter.getMergeId(i + firstRow, j + leftColumnForMerge));
                        int layoutheight = mergedHeight(i + firstRow, j + leftColumnForMerge, adapter.getMergeId(i + firstRow, j + leftColumnForMerge));
                        ViewPlaceHolder view = makeView(i + firstRow, j + leftColumnForMerge, layoutwidth, layoutheight);
                        view.layout(left, top, left + layoutwidth, top + layoutheight);
                        oneRow.add(view);
                    } else {
                        oneRow.add(null);
                    }


                }
                leftViewForMerge.add(oneRow);
            }
        }

    }

    /**
     * return the top left row of a merged area
     *
     * @param row
     * @param column
     * @param mergeId
     * @return
     */
    private int topRowForMergeForColumn(int row, int column, int mergeId) {
        if (row == 0) return 0;
        if (mergeId == 0) {
            return row;
        }
        while (row > 0 && adapter.getMergeId(row - 1, column) == mergeId) {
            row--;
        }
        return row;
    }

    private int leftColumnForMergeForRow(int row, int column, int mergeId) {
        if (column == 0) return 0;
        if (mergeId == 0) {
            return column;
        }
        while (column > 0 && adapter.getMergeId(row, column - 1) == mergeId) {
            column--;
        }
        return column;
    }


    private void clearForMerge() {
        for (List<ViewPlaceHolder> rowEntry : topleftViewForMerge) {
            for (ViewPlaceHolder entry : rowEntry) {
                removeView(entry);
            }
        }
        for (List<ViewPlaceHolder> rowEntry : topViewForMerge) {
            for (ViewPlaceHolder entry : rowEntry) {
                removeView(entry);
            }
        }
        for (List<ViewPlaceHolder> rowEntry : leftViewForMerge) {
            for (ViewPlaceHolder entry : rowEntry) {
                removeView(entry);
            }
        }

        topleftViewForMerge.clear();
        topViewForMerge.clear();
        leftViewForMerge.clear();
    }






    private void adjustFirstCellsAndScroll() {
        int values[];

        values = adjustFirstCellsAndScroll(scrollX, firstColumn, widths);
        scrollX = values[0];
        firstColumn = values[1];

        values = adjustFirstCellsAndScroll(scrollY, firstRow, heights);
        scrollY = values[0];
        firstRow = values[1];
    }

    private int[] adjustFirstCellsAndScroll(int scroll, int firstCell, int sizes[]) {
        if (scroll == 0) {
            // no op
        } else if (scroll > 0) {
            while (sizes[firstCell + 1] < scroll) {
                firstCell++;
                scroll -= sizes[firstCell];
            }
        } else {
            while (scroll < 0) {
                scroll += sizes[firstCell];
                firstCell--;
            }
        }
        return new int[]{scroll, firstCell};
    }
    private void scrollBounds() {

        scrollX = scrollBounds(scrollX, firstColumn, widths, width);

        scrollY = scrollBounds(scrollY, firstRow, heights, height);
    }

    private int scrollBounds(int desiredScroll, int firstCell, int sizes[], int viewSize) {

        if (desiredScroll == 0) {
            // no op
        } else if (desiredScroll < 0) {
            desiredScroll = Math.max(desiredScroll, -sumArray(sizes, 1, firstCell));
        } else {
            if (viewSize < sizes[0]) {
                desiredScroll = 0;
            } else {
                desiredScroll = Math.min(desiredScroll, Math.max(0, sumArray(sizes, firstCell + 1, sizes.length - 1 - firstCell) + sizes[0] - viewSize));
            }
        }
        return desiredScroll;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //System.out.println("pheecian width" + widthSize + "Height" + heightSize);
        final int w;
        final int h;


        if (adapter != null) {
            this.rowCount = adapter.getRowCount();
            this.columnCount = adapter.getColumnCount();
            widths = new int[columnCount + 1];
            sumWidth = new int[columnCount + 1];

            for (int i = -1; i < columnCount; i++) {
                widths[i + 1] += adapter.getWidth(i);

                if (i == -1) {
                    sumWidth[i + 1] = (int) adapter.getWidth(i);
                } else {
                    sumWidth[i + 1] = (int) adapter.getWidth(i) + sumWidth[i];
                }
            }

            heights = new int[rowCount + 1];
            sumHeight = new int[rowCount + 1];
            for (int i = -1; i < rowCount; i++) {
                heights[i + 1] += adapter.getHeight(i);
                if (i == -1) {
                    sumHeight[i + 1] = (int) adapter.getHeight(i);
                } else {
                    sumHeight[i + 1] = (int) adapter.getHeight(i) + sumHeight[i];
                }
            }

            if (widthMode == MeasureSpec.AT_MOST) {

                w = Math.min(widthSize, sumArray(widths));

                //System.out.println("pheecian atmost");
            } else if (widthMode == MeasureSpec.UNSPECIFIED) {
                w = sumArray(widths);
                //System.out.println("pheecian unspecifi");
            } else {
                w = widthSize;
                int sumArray = sumArray(widths);
                // System.out.println("pheecian other");

            }

            if (heightMode == MeasureSpec.AT_MOST) {


                h = Math.min(heightSize, sumArray(heights));

            } else if (heightMode == MeasureSpec.UNSPECIFIED) {
                h = sumArray(heights);
            } else {
                h = heightSize;
            }
        } else {
            if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
                w = 0;
                h = 0;
            } else {
                w = widthSize;
                h = heightSize;
            }
        }

        if (firstRow >= rowCount || getMaxScrollY() - getActualScrollY() < 0) {
            firstRow = 0;
            scrollY = Integer.MAX_VALUE;
        }
        if (firstColumn >= columnCount || getMaxScrollX() - getActualScrollX() < 0) {
            firstColumn = 0;
            scrollX = Integer.MAX_VALUE;
        }

        setMeasuredDimension(w, h);

        if (formerWidth != w) {


            int longer = metrics.widthPixels > metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;


            cacheBitmap = BitmapHelper.getInstance().getBitmap(1, longer, longer);
            cacheCanvas.setBitmap(cacheBitmap);
            cacheBitmapBack = CachedBitmapHelper.getInstance().getBitmap(1, longer, longer);
            cacheCanvasBack.setBitmap(cacheBitmapBack);


            canvasReady = false;

        }


    }

    public void removeView(ViewPlaceHolder view) {

        if (view != null) {

            final ViewType typeView = (ViewType) view.getTag(R.id.tag_type_view);
            if (typeView != ViewType.IGNORE) {
                viewPlaceHolderContainer.addRecycledView(view, typeView);
            }
        }
    }

    public int getActualScrollX() {
        return scrollX + sumArray(widths, 1, firstColumn);
    }

    public int getActualScrollY() {
        return scrollY + sumArray(heights, 1, firstRow);
    }


    private int getMaxScrollX() {
        return Math.max(0, sumArray(widths) - width);
    }

    private int getMaxScrollY() {
        return Math.max(0, sumArray(heights) - height);
    }

    private int sumArray(int array[]) {
        return sumArray(array, 0, array.length);
    }

    private int sumArray(int array[], int firstIndex, int count) {
        int sum = 0;
        count += firstIndex;
        for (int i = firstIndex; i < count; i++) {
            sum += array[i];
        }
        return sum;
    }




    private int sumHeight(int firstIndex, int count) {
        if (firstIndex == 0) {
            return sumHeight[count - 1];
        }
        int sum = 0;
        count += firstIndex;
        for (int i = firstIndex; i < count; i++) {
            sum += heights[i];
        }
        return sum;
    }

    private boolean isDirty(int row, int column) {
        for (CellPosition entry : dirtyCells) {
            if (row == entry.getRow() && column == entry.getColumn()) {
                return true;
            }
        }
        return false;
    }

    private void removeLeft() {
        removeLeftOrRight(0);
    }

    private void removeRight() {
        removeLeftOrRight(rowViewList.size() - 1);
    }

    private void removeLeftOrRight(int position) {
        removeView(rowViewList.remove(position));
        for (List<ViewPlaceHolder> list : bodyViewTable) {
            removeView(list.remove(position));
        }
    }

    private void addRight() {
        final int size = rowViewList.size();
        addLeftOrRight(firstColumn + size, size);
    }

    private void addLeft() {
        addLeftOrRight(firstColumn - 1, 0);
    }

    private void addLeftOrRight(int column, int index) {

        ViewPlaceHolder view = makeView(-1, column, widths[column + 1], heights[0]);
        rowViewList.add(index, view);

        int i = firstRow;
        for (List<ViewPlaceHolder> list : bodyViewTable) {
            view = makeView(i, column, widths[column + 1], heights[i + 1]);
            list.add(index, view);
            i++;
        }
    }

    private void removeTop() {
        removeTopOrBottom(0);
    }

    private void removeBottom() {
        removeTopOrBottom(columnViewList.size() - 1);
    }

    private void removeTopOrBottom(int position) {
        removeView(columnViewList.remove(position));
        List<ViewPlaceHolder> remove = bodyViewTable.remove(position);
        for (ViewPlaceHolder view : remove) {
            removeView(view);
        }
    }

    private int getFilledWidth() {
        return widths[0] + sumArray(widths, firstColumn + 1, rowViewList.size()) - scrollX;
    }

    private int getFilledHeight() {
        return heights[0] + sumArray(heights, firstRow + 1, columnViewList.size()) - scrollY;
    }


    private void addBottom() {
        final int size = columnViewList.size();
        addTopAndBottom(firstRow + size, size);
    }

    private void addTop() {
        addTopAndBottom(firstRow - 1, 0);
    }

    private void addTopAndBottom(int row, int index) {
        ViewPlaceHolder view = makeView(row, -1, widths[0], heights[row + 1]);
        columnViewList.add(index, view);

        List<ViewPlaceHolder> list = new ArrayList<ViewPlaceHolder>();
        final int size = rowViewList.size() + firstColumn;
        for (int i = firstColumn; i < size; i++) {
            view = makeView(row, i, widths[i + 1], heights[row + 1]);
            list.add(view);
        }
        bodyViewTable.add(index, list);
    }



    private void repositionViews() {

        int left, top, right, bottom, i;

        left = widths[0] - scrollX;
        i = firstColumn;
        for (ViewPlaceHolder view : rowViewList) {
            right = left + widths[++i];
            if (view != null) {
                if ((Boolean) view.getTag(R.id.needLayout)) {
                    view.layout(left, 0, right, heights[0]);
                    view.setTag(R.id.needLayout, false);
                } else {
                    view.offsetLeftAndRight(left - view.getLeft());
                }

            }
            left = right;
        }

        top = heights[0] - scrollY;
        i = firstRow;
        for (ViewPlaceHolder view : columnViewList) {
            bottom = top + heights[++i];
            if (view != null) {
                if ((Boolean) view.getTag(R.id.needLayout)) {
                    view.layout(0, top, widths[0], bottom);
                    view.setTag(R.id.needLayout, false);
                } else {
                    view.offsetTopAndBottom(top - view.getTop());
                }

            }
            top = bottom;
        }

        top = heights[0] - scrollY;
        i = firstRow;
        for (List<ViewPlaceHolder> list : bodyViewTable) {
            bottom = top + heights[++i];
            left = widths[0] - scrollX;
            int j = firstColumn;
            for (ViewPlaceHolder view : list) {
                right = left + widths[++j];
                if ((Boolean) view.getTag(R.id.needLayout)) {
                    if (topleftMerge(i - 1, j - 1)) {

                        int layoutwidth = mergedWidth(i - 1, j - 1, adapter.getMergeId(i - 1, j - 1));
                        int layoutheight = mergedHeight(i - 1, j - 1, adapter.getMergeId(i - 1, j - 1));
                        view.layout(left, top, left + layoutwidth, top + layoutheight);
                        view.setTag(R.id.needLayout, false);

                    } else {
                        view.layout(left, top, left + 0, top + 0);
                        view.setText(null);
                        view.setTag(R.id.needLayout, false);
                    }
                } else {
                    view.offsetLeftAndRight(left - view.getLeft());
                    view.offsetTopAndBottom(top - view.getTop());
                }


                left = right;
            }
            top = bottom;

        }

    }

    // http://stackoverflow.com/a/6219382/842697
    private class Flinger implements Runnable {
        private final Scroller scroller;

        private int lastX = 0;
        private int lastY = 0;

        Flinger(Context context) {
            scroller = new Scroller(context);
        }

        void start(int initX, int initY, int initialVelocityX, int initialVelocityY, int maxX, int maxY) {
            int limit = 20000;
            if (initialVelocityX > limit) {
                initialVelocityX = limit;
            } else if (initialVelocityX < -limit) {
                initialVelocityX = -limit;
            }
            if (initialVelocityY > limit) {
                initialVelocityY = limit;
            } else if (initialVelocityY < -limit) {
                initialVelocityY = -limit;
            }


            scroller.fling(initX, initY, initialVelocityX, initialVelocityY, 0, maxX, 0, maxY);

            lastX = initX;
            lastY = initY;
            post(this);
        }

        public void run() {
            if (scroller.isFinished()) {
                return;
            }

            boolean more = scroller.computeScrollOffset();
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            int diffX = lastX - x;
            int diffY = lastY - y;
            if (diffX != 0 || diffY != 0) {
                scrollBy(diffX, diffY);
                lastX = x;
                lastY = y;
            }

            if (more) {
                post(this);
            }
        }

        boolean isFinished() {
            return scroller.isFinished();
        }

        void forceFinished() {
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
        }
    }

    private int sumWidth(int firstIndex, int count) {
        if (firstIndex == 0) {
            return sumWidth[count - 1];
        }
        int sum = 0;
        count += firstIndex;
        for (int i = firstIndex; i < count; i++) {
            sum += widths[i];
        }
        return sum;
    }



}
