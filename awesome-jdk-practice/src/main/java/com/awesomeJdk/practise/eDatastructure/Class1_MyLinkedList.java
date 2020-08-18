package com.awesomeJdk.practise.eDatastructure;

import org.junit.Test;

/**
 * @see java.util.LinkedList
 * https://www.geeksforgeeks.org/linked-list-set-1-introduction/
 */

/**
 * 示例还差：swap(E a,E b)交互位置方法
 * @param <E>
 */
public class Class1_MyLinkedList<E> {
    Node head;
    int size;
    /**
     * 添加
     * @param element
     * @return
     */
    public void addLast(E element){
        Node<E> cur=head;
        while (cur.next !=null){
            cur=cur.next;
        }
        cur.next=new Node<E>(element);
        size++;
    }
    public void add(int index, E element) {
        checkPosition(index);
        if(index ==1)
            addFirst(element);
        else
            addAfterItem(getNode(index-1),element);
        size++;
    }
    private void addAfterItem(Node<E> pre,E element){
        Node<E> cur = new Node<>(element);
        cur.next =pre.next;;
        pre.next =cur;
    }
    public void addFirst(E e) {
        final Node<E> f=head;
        head = new Node(e);
        head.next =f;
        size++;
    }

    /**
     * 删除第一个节点
     *
     */
    public E removeFirst() {
        Node<E> old = this.head;
        Node<E> next = this.head.next;
        this.head = next;
        size--;
        return old.item;
    }

    /**
     * 第一个节点index为1
     * @param index
     * @return
     */
    public E remove(int index) {
        checkPosition(index);
        Node<E> cur;
        Node<E> prev;
        if(index ==1){
            cur = head;
            head = cur.next;
        }else {
            prev = getNode(index - 1);
            cur = prev.next;
            prev.next = cur.next;
        }
        size--;
        return cur.item;
    }
    public E remove(Object o) {
        Node<E> prev;
        Node<E> cur= head;
        if (cur.item.equals(o)){
            head = cur.next;
        }else {
            do{
                prev = cur;
                cur=prev.next;
            } while (cur !=null && !cur.item.equals(o));
            if(cur==null) return null;
            prev.next =cur.next;
        }
        return cur.item;
    }

    /**
     * 查询
     *
     */
    public E get(int index) {
        checkPosition(index);
        return getNode(index).item;
    }
    public boolean contains(Object o) {
        return indexOf(o)!=-1;
    }
    public boolean search(E x){
        return search(head,x);
    }
    // Checks whether the value x is present
    // in linked list
    private boolean search(Node head, E x)
    {
        if (head == null)
            return false;
        if(x==null&& head.item==null) return true;
        if (x!=null && x.equals(head.item) )
            return true;
        return search(head.next, x);
    }
    public int indexOf(Object o) {
        Node<E> cur=this.head;
        int count=0;
        if(o ==null){
            while (cur !=null){
                count++;
                if(cur.item == null){
                    return count;
                }
                cur =cur.next;
            }
        }else{
            while (cur !=null){
                count++;
                if(o.equals(cur.item)){
                    return count;
                }
                cur =cur.next;
            }
        }
        return -1;
    }
    public int size(){
        return size;
    }
    private static class Node<E> {
        E item;
        Node<E> next;
        //Node<E> prev;
        Node(E element){
            this.item=element;
        }
        Node(Node<E> prev, E element) {
            this.item = element;
            this.next = next;
        }
    }

    public Node<E> getNode(int index){
        checkPosition(index);
        int count=1;
        Node<E> res=head;
        while (count < index){
            res=res.next;
            count++;
        }
        return res;
    }
    //用于测试
    public E getElement(Node<E>  e){
        return e.item;
    }
    private boolean checkPosition(int index){
        if(index<=0 || index >  size)
            throw new IndexOutOfBoundsException("index超出边界");
        return true;
    }
    public void printList(){
        Node e=head;
        while(e !=null){
            System.out.println(e.item);
            e=e.next;
        }
    }

    /**
     * 同一个类，泛型不起作用2D Matrix
     */
    @Test
    public void test1_print(){
        Class1_MyLinkedList<Integer> llist = new Class1_MyLinkedList<Integer>();
        llist.head = new Node("hello");
        Node second = new Node("world");
        Node third = new Node("!!!");
        llist.head.next=second;
        second.next=third;
        llist.printList();
    }
    public static void main(String[] args) {
        Class1_MyLinkedList<Integer> llist = new Class1_MyLinkedList<Integer>();
        llist.addFirst(1);
        llist.addFirst(2);
        llist.addFirst(4);
        llist.add(2,3);
        llist.addLast(99);
        llist.printList();
        System.out.println(llist.removeFirst());
        System.out.println(llist.remove(2));
        System.out.println(llist.remove(3));

    }
    @Test
    public void test2(){
        Class1_MyLinkedList<String> list = new Class1_MyLinkedList<>();
        list.addFirst("wo");
        list.addFirst("shi");
        list.add(2,"shui");
        list.addLast("ne?");
        System.out.println(list.remove("shi"));
        list.printList();
        System.out.println(list.indexOf("wo"));
        list.addLast(null);
        System.out.println(list.indexOf(null));
        System.out.println(list.contains("ne?"));
        System.out.println("递归算法");
        System.out.println(list.search(null));
        System.out.println(list.search("ne?"));
    }
}
