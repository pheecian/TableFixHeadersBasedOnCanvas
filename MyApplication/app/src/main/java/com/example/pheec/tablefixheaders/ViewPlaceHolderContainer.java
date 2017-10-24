package com.example.pheec.tablefixheaders;

import java.util.Stack;

/**
 * Created by pheec on 2017/10/24.
 */

public class ViewPlaceHolderContainer {


    private Stack<ViewPlaceHolder>[] views;

    /**
     * Constructor
     *
     * @param size The number of types of view to recycle.
     */
    @SuppressWarnings("unchecked")
    public ViewPlaceHolderContainer(int size) {
        views = new Stack[size];
        for (int i = 0; i < size; i++) {
            views[i] = new Stack<ViewPlaceHolder>();
        }
    }

    /**
     * Add a view to the ViewPlaceHolderContainer. This view may be reused in the function

     *
     * @param view A view to add to the ViewPlaceHolderContainer. It can no longer be used.
     * @param type the type of the view.
     */
    public void addRecycledView(ViewPlaceHolder view, ViewType type) {
        switch (type) {
            case CORNER_HEAD:
                views[0].push(view);
                break;
            case COLUMN_HEAD:
                views[1].push(view);
                break;
            case ROW_HEAD:
                views[2].push(view);
                break;
            case BODY:
                views[3].push(view);
                break;
        }
    }

    /**
     * Returns, if exists, a view of the type <code>typeView</code>.
     *
     * @param typeView the type of view that you want.
     * @return a view of the type <code>typeView</code>. <code>null</code> if
     * not found.
     */
    public ViewPlaceHolder getRecycledView(ViewType typeView) {
        try {
            switch (typeView) {
                case CORNER_HEAD:
                    return views[0].pop();
                case COLUMN_HEAD:
                    return views[1].pop();
                case ROW_HEAD:
                    return views[2].pop();
                case BODY:
                    return views[3].pop();
            }

        } catch (java.util.EmptyStackException e) {
            return null;
        }
        return null;
    }
}

