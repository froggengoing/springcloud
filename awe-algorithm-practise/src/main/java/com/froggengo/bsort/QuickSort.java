package com.froggengo.bsort;

import org.junit.Test;

import java.util.Arrays;

public class QuickSort {



    @Test
    public  void test(){
/*        int[] arr= new Random(0).ints(100).toArray();
        int i = Arrays.stream(arr).max().getAsInt() - Arrays.stream(arr).min().getAsInt();*/
        int[] arr=new int[10];
        //int[] ints = Arrays.stream(arr).map(n -> (int)(Math.random()*1000)).toArray();//int是转换(int)(运算结果)
        int[]  ints={6,7,8,9,6,6,6,6,1,2,3,4,5};
        System.out.println(Arrays.toString(ints));

        int i=0,j=ints.length-1;
        quickSort(ints,i,j);
        System.out.println(Arrays.toString(ints));
    }

    private void quickSort(int[] arrs, int i, int j) {
        int min=i;
        int max=j;
        if(i>=j) return;
        while(min<max) {
            while (max > min && arrs[max] >= arrs[i]) max--;//等号很重要
            while (min < max && arrs[min] <= arrs[i]) min++;
            if(max>min){
                int temp=arrs[max];
                arrs[max]=arrs[min];
                arrs[min]=temp;
            }
        }
        int temp=arrs[max];
        arrs[max]=arrs[i];
        arrs[i]=temp;
        quickSort(arrs,i,min-1);//没有减一和加一导致Stack Overflow
        quickSort(arrs,max+1,j);
    }
}
