package com.example.pheec.tablefixheaders;

/**
 * Created by pheec on 2017/10/24.
 */


public class RowNameHelper {
    public static String getRowName(int row) {
        if (row < 0) {
            return "";
        }
        return String.valueOf(row + 1);
    }
}
