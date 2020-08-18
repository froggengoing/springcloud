package com.froggengo.bsort;

import java.util.Arrays;

public class MergeSort {

    public static void main(String[] args) {


        int[] arr={8,7,6,5,4,3,2,1};
        int[] temp=new int[arr.length ];
        mergeSort(arr,0,arr.length-1,temp);
        System.out.println(Arrays.toString(temp));
    }

    private static void mergeSort(int[] arr,int left,int right,int[] temp) {
        int mid = (left + right) / 2;
        System.out.println("middle:" + mid);
        if(left < right  ){
            mergeSort(arr,left,mid,temp);
            mergeSort(arr,mid +1 ,right,temp);
        }
        combine(arr,left,right,temp);
    }

    private static void combine(int[] arr, int left, int right, int[] temp) {
        int leftinitial = left;
        int pos=leftinitial;
        int m = (leftinitial + right) / 2 +1;
        //leftinitial 为左子集初始索引
        //mid 为右子集初始索引
        int mid = (leftinitial + right) / 2 +1;


        if(leftinitial ==right){
            return;
        }


        while(pos < right){
            if(arr[leftinitial] <= arr[mid] && leftinitial < m && mid < right){
                temp[pos] = arr[leftinitial];
                pos++;
                leftinitial++;
            }else if (arr[leftinitial] > arr[mid] && leftinitial < m && mid < right) {
                temp[pos] = arr[mid];
                pos++;
                mid++;
            }else if(leftinitial >= m){
                temp[pos] = arr[mid];
                pos++;
                mid++;
            }else if (mid>= right){
                temp[pos] = arr[leftinitial];
                pos++;
                leftinitial++;
            }
        }
        int templeft=left;
        while (templeft <= right){
            System.out.println("大小" +templeft);
            arr[templeft] = temp[templeft];
            templeft++;
        }
    }



}
