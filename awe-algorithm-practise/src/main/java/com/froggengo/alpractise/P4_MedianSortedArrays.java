package com.froggengo.alpractise;

public class P4_MedianSortedArrays {
    // 1   2  3  4   5  6  7
    //        4  5   6  7  8 9 10
    public static void main(String[] args) {
        int[] num = new int[]{1, 3, 5, 7, 9};
        int[] num1 = new int[]{7, 9, 10, 13, 15};
        double media = P4_Solution.findMedianSortedArrays(num, num1);
        System.out.println(media);
    }
}
class P4_Solution {
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int len1 = nums1.length;
        int len2 = nums2.length;
        if(len1 > len2) findMedianSortedArrays(nums2,nums1);
        int len = len1 + len2;
        findMedian(nums2,nums1,len);
        if(len%2==0){//偶数

        }else{//奇数
            if(len1 > len2) return 0;
        }

        return 0;
    }
    //O((n + m)/2)
    public static double findMedian(int[] nums1, int[] nums2,int len){
        //nums1为短的
        //k为中位数，查找第k-1个和第k个值，len为奇数是n/2+1,len为偶数时为n/2，n/2+1
        int k = len/2+1;
        int len1 = nums1.length-1;
        int len2 = nums2.length-1;
        int i=0;//nums1中的指针
        int j=0;//nums2中的指针
        int value2n=0;
        while(--k >= 0 ){
            if(i==len1) j++;//可优化
            else if(j==len2) i++;//理论上不可能
            else if(nums1[i] < nums2[j]) i++;
            else j++;
            if(k==1){
               // if()
                value2n=max(nums1[i],nums2[j]);
            }
        }

        return  0;
    }
    public static int min(int a ,int b){
        return a>b?b:a;
    }
    public static int max(int a ,int b){
        return a>b?a:b;
    }
}