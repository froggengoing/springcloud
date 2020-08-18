package com.froggengo.alpractise;

public class P2_TwoListSum {
    //(2 -> 4 -> 3) + (5 -> 6 -> 4)
    //(2 -> 4 -> 3) + (5 -> 6 ->6->5)  7006
    public static void main(String[] args) {
        ListNode node = new ListNode(2);
        ListNode node1 = new ListNode(4);
        ListNode node2 = new ListNode(3);
        ListNode node3 = new ListNode(5);
        ListNode node4 = new ListNode(6);
        ListNode node5 = new ListNode(6);
        ListNode node7 = new ListNode(5);
        node.next= node1;
        node1.next=node2;
        //
        node3.next=node4;
        node4.next=node5;
        node5.next=node7;
        ListNode listNode = addTwoNumbers(node, node3);
        while (listNode!=null){
            System.out.println(listNode.val);
            listNode =listNode.next;
        }


/*
        ListNode node7 = new ListNode(5);
        ListNode node8 = new ListNode(6);
        ListNode node9 = new ListNode(4);
        ListNode res=null;
        ListNode resMID=null;
        res=node7;
        resMID=res.next;
        res = node8;
        System.out.println(res.val);
*/


    }
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode p1=l1;
        ListNode p2=l2;
        ListNode res=new ListNode(0);
        ListNode resMid=res;
        int mid=0;
        int cur;
        while(p1!=null ||p2!=null){
            int s1= p1!=null?p1.val:0;
            int s2= p2!=null?p2.val:0;
            cur = (s1+s2+mid)%10;
            resMid.next = new ListNode(cur);
            mid = (s1+s2+mid)/10;
            p1= p1!=null?p1.next:null;
            p2= p2!=null?p2.next:null;
            resMid =resMid.next;
        }
        return res.next;
    }
}
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
}

