package com.awesomeJdk.practise.eDatastructure;

import java.util.LinkedList;

/**
 * @see java.util.TreeSet
 * https://www.geeksforgeeks.org/binary-tree-data-structure/
 * https://www.geeksforgeeks.org/avl-with-duplicate-keys/
 * https://www.studytonight.com/data-structures/binary-search-tree
 * @param <E>
 */
public class Class3_AVLBinaryTree<E> {
        // An AVL tree node
        static class Node {
            int key;
            Node left;
            Node right;
            int height;
            int count;
        }

        // A utility function to get height of the tree
        static int height(Node N)
        {
            if (N == null)
                return 0;
            return N.height;
        }

        // A utility function to get maximum of two integers
        static int max(int a, int b)
        {
            return (a > b) ? a : b;
        }

        /* Helper function that allocates a new node with the given key and
    null left and right pointers. */
        static Node newNode(int key)
        {
            Node node = new Node();
            node.key = key;
            node.left =null;
            node.right =null;
            node.height=1;
            node.count =1;
            return node;
        }
        // A utility function to right rotate subtree rooted with y
        // See the diagram given above.
        static Node rightRotate(Node y){
            Node x = y.left;
            Node xright = x.right;

            y.left = xright;
            x.right = y;
/*            x.height = y.height;
            y.height = x.height ;*/
                // Update heights
            y.height = max(height(y.left), height(y.right)) + 1;
            x.height = max(height(x.left), height(x.right)) + 1;
            return x;
        }
        // A utility function to left rotate subtree rooted with x
        // See the diagram given above.
        static Node leftRotate(Node x)
        {
            Node y = x.right;
            Node yleft = y.left;

            x.right = yleft;
            y.left = x;

            y.height = max(height(y.left), height(y.right)) + 1;
            x.height = max(height(x.left), height(x.right)) + 1;
            return y;
        }
        // Get Balance factor of node N
        static int getBalance(Node N)
        {
            if (N == null)
                return 0;
            return height(N.left) - height(N.right);
        }
        public  static Node insert(Node node, int key)
        {
            /*1.  Perform the normal BST rotation */
            if (node == null)
                return newNode(key);
            // If key already exists in BST, increment count and return
            if (key == node.key) {
                (node.count)++;
                return node;
            }

            /* Otherwise, recur down the tree */
            if (key < node.key)
                node.left = insert(node.left, key);
            else
                node.right = insert(node.right, key);
            /* 2. Update height of this ancestor node */
            node.height = max(height(node.left), height(node.right)) + 1;
            int balance = getBalance(node);
            // Left Left Case
            if (balance > 1 && key < node.left.key)
                return rightRotate(node);

            // Right Right Case
            if (balance < -1 && key > node.right.key)
                return leftRotate(node);
            /**
             *                  6                           6                       5
             *             3               =>        5             =>   3              6
             *                  5                    3
             */
            /**
             *          6                       3
             *    3             =>                  6
             *          5                          5
             */
            // Left Right Case
            if (balance > 1 && key > node.left.key) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
            /**
             *          3                       3                                                 4
             *                6   =>               4                        =>          3           6
             *             4                                6
             */
            // Right Left Case
            if (balance < -1 && key < node.right.key) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }

            return node;
        }
        public static void preOrder(Node node){
            if (node != null){
                    System.out.print(node.key +"("+node.count+")  ");
                    preOrder(node.left);
                    preOrder(node.right);
            }
        }
        static void preOrderBystack(Node node){
            LinkedList<Node> list = new LinkedList<>();
            Node cur=node;
            while(cur !=null || !list.isEmpty()){
                if(cur !=null){
                    Node temp = cur;
                    System.out.print(temp.key +"("+temp.count+")  ");
                    cur = temp.left;
                   if(temp.right!=null)
                       list.push(temp.right);
                }else if(!list.isEmpty()){
                    cur = list.pop();
                }

            }
        }

        static void  postOrder(Node node){
            if (node != null){
                postOrder(node.left);
                postOrder(node.right);
                System.out.print(node.key +"("+node.count+")  ");
            }
        }
    static void  inOrder(Node node){
        if (node != null){
            inOrder(node.left);
            System.out.print(node.key +"("+node.count+")  ");
            inOrder(node.right);
        }
    }
    /* Driver program to test above function*/
    public static void main(String args[])
    {
        Node root = null;

        /* Coning tree given in the above figure */
        root = insert(root, 9);
        root = insert(root, 5);
        root = insert(root, 10);
        root = insert(root, 5);
        root = insert(root, 9);
        root = insert(root, 3);
        root = insert(root, 17);

        System.out.printf("Pre order traversal of the constructed AVL tree is \n");
        preOrder(root);
        System.out.println("");
        preOrderBystack(root);
        System.out.println("");
        postOrder(root);
        System.out.println("");
        inOrder(root);
    }

}
/**
 *                  9
 *              5        10
 *                  7           17
 */

/*9(2)  5(2)  7(1)  10(1)  17(1)
5(2)  7(1)  10(1)  17(1)  9(2) 错误
7(1)  5(2)  17(1)  10(1)  9(2)
5  7   9  10 17
*/