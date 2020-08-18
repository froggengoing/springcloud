package com.froggengo.alpractise;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class P1_TwoSum {
    public static void main(String[] args) {
        int[] num=new int[]{1,3,4,5,6,7,11,8,9,20,12,14,33};
        //方法1
        int[] res = Solution1.twoSum(num, 25);
        System.out.println(res[0]);
        System.out.println(res[1]);
        //-----方法2
        int[] res2 = Solution2.twoSum(num, 25);
        System.out.println(res2[0]);
        System.out.println(res2[1]);
        //--方法3
        int[] res3 = Solution2.twoSum(num, 25);
        System.out.println(res3[0]);
        System.out.println(res3[1]);
    }
}
//给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
//
// 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
//
//
//
// 示例:
//
// 给定 nums = [2, 7, 11, 15], target = 9
//
//因为 nums[0] + nums[1] = 2 + 7 = 9
//所以返回 [0, 1]
//
// Related Topics 数组 哈希表


//leetcode submit region begin(Prohibit modification and deletion)

/**
 * 1、两层循环
 */
class Solution1 {
    public static int[] twoSum(int[] nums, int target) {
        out:
        for (int i = 0,len=nums.length; i < len; i++) {
            for (int j = i+1; j < len; j++) {
                if(nums[i]+nums[j]==target) {
                    return new int[]{i,j};
                }
            }
        }
        return new int[2];
    }
}
//leetcode submit region end(Prohibit modification and deletion)

/**
 * 2、遍历两次
 */
class Solution2 {
    public static int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int len = nums.length;
        IntStream.range(0, len).forEach(n->map.put(nums[n],n));
        int[] res=new int[2];
        IntStream.range(0,len).anyMatch(n->{
            int key = target - nums[n];
            //bug:且元素不能是本身
            boolean isExist = map.containsKey(key) && map.get(key)!=n;
            if(isExist){
                res[0]=n;
                res[1]=map.get(key);
            }
            return isExist;
        });
        return res;
    }
}

/**
 * 遍历一次
 */
class Solution3 {
    public static int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int len = nums.length;
        //IntStream.range(0, len).forEach(n->map.put(nums[n],n));
        int[] res=new int[2];
        IntStream.range(0,len).anyMatch(n->{
            int key = target - nums[n];
            //bug:且元素不能是本身
            boolean isExist = map.containsKey(key) && map.get(key)!=n;
            if(isExist){
                res[0]=n;
                res[1]=map.get(key);
            }
            map.put(key,n);
            return isExist;
        });
        return res;
    }
}