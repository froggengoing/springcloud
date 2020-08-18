package com.froggengo.alpractise;

import java.util.HashSet;
// 示例 1:
// 输入: "abcabcbb",输出: 3
//解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
// 示例 2:
// 输入: "bbbbb",输出: 1
//解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
// 示例 3:
// 输入: "pwwkew" ,输出: 3
//解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
public class P3_LongestSubstring{
    public static void main(String[] args) {
        System.out.println(P3_Solution.lengthOfLongestSubstring("abcabcbb"));
        System.out.println(P3_Solution.lengthOfLongestSubstring("bbbbb"));
        System.out.println(P3_Solution.lengthOfLongestSubstring("pwwkew"));
        System.out.println(P3_Solution.lengthOfLongestSubstring("abcdefg"));
    }

}
 class P3_Solution {
    public static int lengthOfLongestSubstring(String s) {
        String str=s;
        char[] chars = str.toCharArray();
        HashSet<String> firstSet = new HashSet<>();
        HashSet<String> secSet = new HashSet<>();
        for (int i = 0,len=chars.length; i < len; i++) {
            char a = chars[i];
            String s1 = String.valueOf(a);
            if (secSet.size()==0 && !firstSet.contains(s1)) {
                firstSet.add(s1);
            }else if(!secSet.contains(s1)){
                secSet.add(s1);
            }else{
                if (firstSet.size() >secSet.size()) {
                    secSet =new HashSet<>();
                }else {
                    firstSet =secSet;
                    secSet =new HashSet<>();
                }
            }
        }
        int firstSize = firstSet.size();
        int secSize = secSet.size();
        return firstSize>secSize?firstSize:secSize;
    }
}
