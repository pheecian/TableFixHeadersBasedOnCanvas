package com.example.pheec.tablefixheaders;

import java.io.Serializable;

/**
 * Created by pheec on 2017/10/24.
 */

public class CellPosition implements Serializable {
    private int row;
    private int column;

    public CellPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}

