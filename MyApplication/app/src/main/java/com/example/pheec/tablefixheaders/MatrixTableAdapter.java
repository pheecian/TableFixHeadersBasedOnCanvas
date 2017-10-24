package com.example.pheec.tablefixheaders;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;

/**
 * Created by pheec on 2017/10/24.
 */

public class MatrixTableAdapter<T> extends BaseTableAdapter {

    private final static int WIDTH_DIP = 25;
    private final static int HEIGHT_DIP = 15;

    private final Context context;

    private T[][] table;

    private final int width;
    private final int height;


    private int colorTextNormal;
    private int colorBorderGrid;
    private int colorBorder;


    public MatrixTableAdapter(Context context) {
        this(context, null);
    }

    public MatrixTableAdapter(Context context, T[][] table) {
        this.context = context;
        Resources r = context.getResources();

        width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH_DIP, r.getDisplayMetrics()));
        height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT_DIP, r.getDisplayMetrics()));

        setInformation(table);


        colorTextNormal = R.color.colorTextNormal;
        colorBorderGrid = R.color.colorBorderGrid;
        colorBorder =  R.color.colorBorder;

    }

    public void setInformation(T[][] table) {
        this.table = table;
    }

    @Override
    public int getRowCount() {
        return 500;
    }

    @Override
    public int getColumnCount() {
        return 50;
    }



    @Override
    public int getHeight(int row) {
        return height;
    }

    @Override
    public int getWidth(int column) {
        return width;
    }

    @Override
    public ViewPlaceHolder getView(int row, int column, ViewPlaceHolder convertView) {

        final ViewPlaceHolder view;
        switch (getItemViewType(row, column)) {
            case CORNER_HEAD:
                view = getCornerHeader(convertView);
                break;
            case COLUMN_HEAD://header
                view = getColumnHeader(column, convertView);
                break;
            case ROW_HEAD://lefter
                view = getRowHeader(row, convertView);
                break;
            case BODY://body
                view = getBody(row, column, convertView);
                break;

            default:
                throw new RuntimeException("wtf?");
        }
        return view;
    }


    @Override
    public ViewType getItemViewType(int row, int column) {
        final ViewType itemViewType;
        if (row == -1 && column == -1) {
            itemViewType = ViewType.CORNER_HEAD;
        } else if (row == -1) {
            itemViewType = ViewType.COLUMN_HEAD;
        } else if (column == -1) {
            itemViewType = ViewType.ROW_HEAD;
        } else {
            itemViewType = ViewType.BODY;
        }
        return itemViewType;
    }


    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getMergeId(int row, int column) {
        if (row == 0 && column == 0 )return 1;
        if (row == 0 && column == 1 )return 1;
        if (row == 1 && column == 0 )return 1;
        if (row == 1 && column == 1 )return 1;
        if (row == 10 && column == 10 )return 1;
        if (row == 10 && column == 11 )return 1;
        if (row == 11 && column == 10 )return 1;
        if (row == 11 && column == 11 )return 1;
        return 0;
    }


    private ViewPlaceHolder getCornerHeader(ViewPlaceHolder convertView) {
        if (convertView == null) {
            convertView = new ViewPlaceHolder();
            convertView.setTag(R.id.inflated, true);
        }
        convertView.setText("");
        convertView.setBgColor(Color.GRAY);
        convertView.setFontColor(colorTextNormal);
        convertView.setFontStyle(FontStyle.NORMAL);
        convertView.setStrikethrough(false);
        convertView.setUnderline(false);
        convertView.setLeftBoarderShow(false);
        convertView.setRightBoarder(BorderStyle.SOLID);
        convertView.setRightBoarderColor(colorBorderGrid);
        convertView.setTopBoarder(BorderStyle.SOLID);
        convertView.setTopBoarderColor(colorBorder);
        convertView.setBottomBoarder(BorderStyle.SOLID);
        convertView.setBottomBoarderColor(colorBorderGrid);
        return convertView;
    }

    private ViewPlaceHolder getColumnHeader(final int column, ViewPlaceHolder convertView) {
        if (convertView == null) {
            convertView = new ViewPlaceHolder();
            convertView.setTag(R.id.inflated, true);
        }

        if (column < 0) {
            convertView.setText("");
            convertView.setBgColor(Color.GRAY);
            convertView.setTopBoarderShow(false);
            convertView.setLeftBoarder(BorderStyle.EMPTY);
            convertView.setRightBoarder(BorderStyle.EMPTY);
            convertView.setBottomBoarder(BorderStyle.EMPTY);
            return convertView;
        }
        convertView.setBgColor(Color.GRAY);
        convertView.setText(ColumnNameHelper.getColumnName(column));
        convertView.setTextSize(30);
        convertView.setVAlign(VerticalAlign.NORMAL);
        convertView.setHAlign(HorizontalAlign.NORMAL);
        convertView.setFontStyle(FontStyle.NORMAL);
        convertView.setStrikethrough(false);
        convertView.setUnderline(false);
        convertView.setLeftBoarder(BorderStyle.SOLID);
        convertView.setLeftBoarderColor(colorBorderGrid);
        convertView.setRightBoarder(BorderStyle.SOLID);
        convertView.setRightBoarderColor(colorBorderGrid);
        convertView.setTopBoarder(BorderStyle.SOLID);
        convertView.setTopBoarderColor(colorBorder);
        convertView.setBottomBoarder(BorderStyle.SOLID);
        convertView.setBottomBoarderColor(colorBorderGrid);
        convertView.setFontColor(Color.BLACK);
        return convertView;
    }

    private ViewPlaceHolder getRowHeader(final int row, ViewPlaceHolder convertView) {
        if (convertView == null) {
            convertView = new ViewPlaceHolder();
            convertView.setTag(R.id.inflated, true);
        }

        if (row < 0 ) {
            convertView.setText("");
            convertView.setBgColor(Color.GRAY);
            convertView.setLeftBoarderShow(false);
            convertView.setRightBoarder(BorderStyle.EMPTY);
            convertView.setTopBoarder(BorderStyle.EMPTY);
            convertView.setBottomBoarder(BorderStyle.EMPTY);
            return convertView;
        }
        convertView.setBgColor(Color.GRAY);
        convertView.setText(RowNameHelper.getRowName(row));
        convertView.setTextSize(30);

        convertView.setVAlign(VerticalAlign.NORMAL);
        convertView.setHAlign(HorizontalAlign.NORMAL);
        convertView.setFontStyle(FontStyle.NORMAL);
        convertView.setStrikethrough(false);
        convertView.setUnderline(false);
        convertView.setLeftBoarderShow(false);
        convertView.setRightBoarder(BorderStyle.SOLID);
        convertView.setRightBoarderColor(colorBorderGrid);
        convertView.setTopBoarder(BorderStyle.SOLID);
        convertView.setTopBoarderColor(colorBorderGrid);
        convertView.setBottomBoarder(BorderStyle.SOLID);
        convertView.setBottomBoarderColor(colorBorderGrid);
        convertView.setFontColor(Color.BLACK);
        return convertView;
    }

    private ViewPlaceHolder getBody(final int row, final int column, ViewPlaceHolder convertView) {
        if (convertView == null) {
            convertView = new ViewPlaceHolder();

            convertView.setTag(R.id.inflated, true);
        }

        convertView.setText(Integer.toString(row) + Integer.toString(column));
        convertView.setTextSize(20);
        convertView.setBgColor(Color.WHITE);
        convertView.setFontColor(Color.BLACK);

        return convertView;
    }

}
