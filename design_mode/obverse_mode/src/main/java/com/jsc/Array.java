package com.jsc;

public class Array {

    public static void quickSort(int[] arr, int low, int high) {

        int i, j, t;
        int tem;
        if (low > high) {
            return;
        }

        i = low;
        j = high;

        tem = arr[i];
        while (i < j) {
            while (arr[i] <= tem && i < j) {
                i++;
            }
            while (arr[j] >= tem && i < j) {
                j--;
            }
            if (i < j) {
                t = arr[i];
                arr[i] = arr[j];
                arr[j] = t;
            }
        }
        arr[low] = arr[i];
        arr[i] = tem;

        quickSort(arr, low, i - 1);
        quickSort(arr, i + 1, high);

    }

    public static void main(String[] args) {
        int[] arr = {10, 7, 2, 4, 7, 62, 3, 4, 2, 1, 8, 9, 19};
        quickSort(arr, 0, arr.length - 1);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }
}
