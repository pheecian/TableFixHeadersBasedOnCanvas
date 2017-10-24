package com.example.pheec.tablefixheaders;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pheec on 2017/10/24.
 */

public class ViewPlaceHolder {
    private int left;
    private int right;
    private int top;
    private int bottom;
    private float textsize;
    private Map<Integer, Object> tags;
    private String text;
    private VerticalAlign valign = VerticalAlign.TOP;
    private HorizontalAlign halign = HorizontalAlign.RIGHT;
    private int bgColor;
    private int fontColor;

    private FontStyle fontStyle = FontStyle.NORMAL;
    private FontFamily fontFamily = FontFamily.MONOSPACE;
    private BorderStyle topBoarder = BorderStyle.SOLID;
    private BorderStyle bottomBoarder = BorderStyle.SOLID;
    private BorderStyle leftBoarder = BorderStyle.SOLID;
    private BorderStyle rightBoarder = BorderStyle.SOLID;
    private boolean topBoarderShow = true;
    private boolean bottomBoarderShow = true;
    private boolean leftBoarderShow = true;
    private boolean rightBoarderShow = true;
    private int topBoarderColor = Color.RED;
    private int bottomBoarderColor = Color.RED;
    private int leftBoarderColor = Color.BLUE;
    private int rightBoarderColor = Color.BLUE;

    private boolean underline = true;
    private boolean strikethrough = true;



    public ViewPlaceHolder() {
        this.tags = new HashMap<>();
    }

    public void setTag(Integer tagId, Object obj) {
        tags.put(tagId, obj);
    }

    public Object getTag(Integer tagId) {
        return tags.get(tagId);
    }

    public void layout(int a, int b, int c, int d) {
        this.left = a;
        this.top = b;
        this.right = c;
        this.bottom = d;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextSize(float size) {
        textsize = size;
    }

    public float getTextSize() {
        return textsize;
    }

    public String[] getText() {
        if (text == null) return null;
        String[] clip = text.split("\n");
        //System.out.println("pheecian text" + text + " "+clip.length+ " first"+ clip[0]);
        return clip;
    }

    public void offsetLeftAndRight(int offset) {
        left += offset;
        right += offset;
    }

    public void offsetTopAndBottom(int offset) {
        top += offset;
        bottom += offset;
    }

    public VerticalAlign getVAlign() {
        return this.valign;
    }

    public HorizontalAlign getHAlign() {
        return this.halign;
    }

    public void setVAlign(VerticalAlign valign) {
        this.valign = valign;
    }

    public void setHAlign(HorizontalAlign halign) {
        this.halign = halign;
    }

    public void setBgColor(int color) {
        this.bgColor = color;
    }

    public void setFontColor(int color) {
        this.fontColor = color;
    }

    public int getBgColor() {
        return bgColor;
    }

    public int getFontColor() {
        return fontColor;
    }



    public void setFontStyle(FontStyle style) {
        this.fontStyle = style;
    }

    public FontStyle getFontStyle() {
        return this.fontStyle;
    }

    public void setLeftBoarder(BorderStyle style) {
        this.leftBoarder = style;
    }

    public void setRightBoarder(BorderStyle style) {
        this.rightBoarder = style;
    }

    public void setTopBoarder(BorderStyle style) {
        this.topBoarder = style;
    }

    public void setBottomBoarder(BorderStyle style) {
        this.bottomBoarder = style;
    }

    public void setLeftBoarderShow(boolean show) {
        this.leftBoarderShow = show;
    }

    public void setRightBoarderShow(boolean show) {
        this.rightBoarderShow = show;
    }

    public void setTopBoarderShow(boolean show) {
        this.topBoarderShow = show;
    }

    public void setBottomBoarderShow(boolean show) {
        this.bottomBoarderShow = show;
    }

    public BorderStyle getTopBoarder() {
        return this.topBoarder;
    }

    public BorderStyle getBottomBoarder() {
        return this.bottomBoarder;
    }

    public BorderStyle getLeftBoarder() {
        return this.leftBoarder;
    }

    public BorderStyle getRightBoarder() {
        return this.rightBoarder;
    }

    public boolean getLeftBoarderShow() {
        return this.leftBoarderShow;
    }

    public boolean getRightBoarderShow() {
        return this.rightBoarderShow;
    }

    public boolean getTopBoarderShow() {
        return this.topBoarderShow;
    }

    public boolean getBottomBoarderShow() {
        return this.bottomBoarderShow;
    }

    public void setTopBoarderColor(int color) {
        this.topBoarderColor = color;
    }

    public void setBottomBoarderColor(int color) {
        this.bottomBoarderColor = color;
    }

    public void setLeftBoarderColor(int color) {
        this.leftBoarderColor = color;
    }

    public void setRightBoarderColor(int color) {
        this.rightBoarderColor = color;
    }

    public int getTopBoarderColor() {
        return this.topBoarderColor;
    }

    public int getBottomBoarderColor() {
        return this.bottomBoarderColor;
    }

    public int getLeftBoarderColor() {
        return this.leftBoarderColor;
    }

    public int getRightBoarderColor() {
        return this.rightBoarderColor;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public boolean getUnderline() {
        return this.underline;
    }

    public void setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public boolean getStrikethrough() {
        return this.strikethrough;
    }

    public void setFontFamily(FontFamily fontFamily) {
        this.fontFamily = fontFamily;
    }

    public FontFamily getFontFamily() {
        return this.fontFamily;
    }


}



