package com.awesomeJdk.practise.eDatastructure;

/**
 * Input: 2D matrix
 * 1 2 3
 * 4 5 6
 * 7 8 9
 * https://www.geeksforgeeks.org/construct-a-doubly-linked-linked-list-from-2d-matrix/?ref=leftbar-rightbar
 */
public class Class2_2DMatrix<E> {
    static class Node<E> {
        E data;
        Node next;
        Node prev;
        Node up;
        Node down;
    };
}
