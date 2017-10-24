package com.example.pheec.tablefixheaders;

/**
 * Created by pheec on 2017/10/24.
 */

public class ColumnNameHelper {
    final static String[] Element = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static String getColumnName(int column) {
        StringBuffer result = new StringBuffer();
        while (column >= 0) {
            int remain = column % 26;
            result.append(Element[remain]);
            column /= 26;
            column--;
        }
        result.reverse();
        return result.toString();
    }
}

