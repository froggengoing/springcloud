package com.froggengo.bsort;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class SortSolution {

    @Test
    public void test(){
        int[] nums = new int[100000];
        for(int i=0;i<nums.length-1;i++){
            nums[i]= new Random().nextInt();
        }
        long start = System.currentTimeMillis();
        System.out.println(start);
        sortArray(nums);
        System.out.println(Arrays.toString(nums));
        long end = System.currentTimeMillis();
        System.out.println(end-start);

    }
    public int[] sortArray(int[] nums) {
        String solutionType="Heap";
        if("Bubble".equals(solutionType)){
            for(int i=0;i<nums.length-1;i++){
                for(int j=0;j<nums.length-1-i;j++){
                    System.out.println("循环");
                    if(nums[j]>nums[j+1]){
                        System.out.println("交换"+nums[j]+"-"+nums[j+1]);
                        int temp=nums[j];
                        nums[j]=nums[j+1];
                        nums[j+1]=temp;
                    }
                }
            }
            return nums;
        }
        if("Selection".equals(solutionType)){
            for(int i=0; i<nums.length-1;i++){
                int minIndex=i;
                for(int j=i+1;j<nums.length;j++){//注意不减一
                    if(nums[j]<nums[minIndex]){
                        minIndex=j;
                    }
                }
                if(minIndex !=i){
                    int temp=nums[minIndex];
                    nums[minIndex]=nums[i];
                    nums[i]=temp;
                }
            }
            return nums;
        }
        if("Insert".equals(solutionType)){
            for(int i=0;i<nums.length-1;i++){
                for(int j=i+1;j>0;j--){//改为while实现
                    if(nums[j]<nums[j-1]){
                        int  temp=nums[j];
                        nums[j]=nums[j-1];
                        nums[j-1]=temp;
                    }else{
                        break;
                    }
                }
            }
            return nums;
        }
        if("Heap".equals(solutionType)){
            for(int currentlength=nums.length-1;currentlength>0;currentlength--){
                for(int i=currentlength/2-1;i>=0;i--){
                    selectMax(nums,i,currentlength);
                }
                if(currentlength>1||(currentlength==1 &&nums[0]>nums[1])){
                    swap(nums,0,currentlength);
                }

            }


        }
        return null;
    }
    public void selectMax(int[] n,int index,int currentlength){
        int maxindex=index;
        if(2*index+2<=currentlength &&     n[index]<n[2*index+2]){
            maxindex=index*2+2;
            swap(n,maxindex,index);
            selectMax(n,maxindex,currentlength);
        }
        if(2*index+1<=currentlength && n[index]<n[2*index+1]){
            maxindex=index*2+1;
            swap(n,maxindex,index);
            selectMax(n,maxindex,currentlength);
        }
    }
    private void swap(int[] ns,int maxIndex,int index){
        int temp=ns[maxIndex];
        ns[maxIndex]=ns[index];
        ns[index]=temp;
    }


    class Solution {
        public int[] sortArray(int[] nums) {
            String solutionType="Heap";
            if("Bubble".equals(solutionType)){
                for(int i=0;i<nums.length-1;i++){
                    for(int j=0;j<nums.length-1-i;j++){
                        System.out.println("循环");
                        if(nums[j]>nums[j+1]){
                            System.out.println("交换"+nums[j]+"-"+nums[j+1]);
                            int temp=nums[j];
                            nums[j]=nums[j+1];
                            nums[j+1]=temp;
                        }
                    }
                }
                return nums;
            }
            if("Selection".equals(solutionType)){
                for(int i=0; i<nums.length-1;i++){
                    int minIndex=i;
                    for(int j=i+1;j<nums.length;j++){//注意不减一
                        if(nums[j]<nums[minIndex]){
                            minIndex=j;
                        }
                    }
                    if(minIndex !=i){
                        int temp=nums[minIndex];
                        nums[minIndex]=nums[i];
                        nums[i]=temp;
                    }
                }
                return nums;
            }
            if("Insert".equals(solutionType)){
                for(int i=0;i<nums.length-1;i++){
                    for(int j=i+1;j>0;j--){//改为while实现
                        if(nums[j]<nums[j-1]){
                            int  temp=nums[j];
                            nums[j]=nums[j-1];
                            nums[j-1]=temp;
                        }else{
                            break;
                        }
                    }
                }
                return nums;
            }
            if("Heap".equals(solutionType)){
                for(int currentlength=nums.length-1;currentlength>=nums.length/2;currentlength--){
                    for(int i=currentlength/2-1;i>=0;i--){
                        selectMax(nums,i,currentlength);
                    }
                    swap(nums,0,currentlength);
                }
                return nums;

            }
            return null;
        }
        public void selectMax(int[] n,int index,int currentlength){
            int maxindex=index;
            if(2*index+2<=currentlength &&     n[index]<n[2*index+2]){
                maxindex=index*2+2;
                swap(n,maxindex,index);
                selectMax(n,maxindex,currentlength);
            }
            if(2*index+1<=currentlength && n[index]<n[2*index+1]){
                maxindex=index*2+1;
                swap(n,maxindex,index);
                selectMax(n,maxindex,currentlength);
            }
        }
        private void swap(int[] ns,int maxIndex,int index){
            int temp=ns[maxIndex];
            ns[maxIndex]=ns[index];
            ns[index]=temp;
        }



    }
}
