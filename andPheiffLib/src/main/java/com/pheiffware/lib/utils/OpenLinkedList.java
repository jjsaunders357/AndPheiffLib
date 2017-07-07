package com.pheiffware.lib.utils;

/**
 * A linked list class, which allows you to keep references to nodes within the list for fast access later.  This is not full featured at this point, but will be added to as
 * necessary.
 * <p/>
 * D is the type of data being stored. Created by Steve on 5/25/2016.
 */
public class OpenLinkedList<D>
{
    //Points to a special node before the start of the list.  This simplifies most methods.
    private final Node<D> head;
    //Points to a special node after the end of the list.  This simplifies most methods.
    private final Node<D> tail;

    public OpenLinkedList()
    {
        head = new Node<D>(null);
        tail = new Node<D>(null);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Add a node with the given data to the front of the list
     *
     * @param data
     * @return a reference to the created node
     */
    public final Node<D> addToFront(D data)
    {
        Node<D> newNode = new Node<>(data);
        Node<D> oldFirst = head.next;
        newNode.next = oldFirst;
        newNode.prev = head;
        head.next = newNode;
        oldFirst.prev = newNode;
        return newNode;
    }

    /**
     * Add a node with the given data to the back of the list
     *
     * @param data
     * @return a reference to the created node
     */
    public final Node<D> addToBack(D data)
    {
        Node<D> newNode = new Node<>(data);
        Node<D> oldLast = tail.prev;
        newNode.prev = oldLast;
        newNode.next = tail;
        tail.prev = newNode;
        oldLast.next = newNode;
        return newNode;
    }

    /**
     * Move the given node to the back of the list.
     */
    public final void moveToBack(Node<D> node)
    {
        //Already at end, do nothing
        if (node.next == tail)
        {
            return;
        }

        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = tail.prev;
        tail.prev = node;
    }

    /**
     * Get the data value in the 1st element of the list
     *
     * @return
     */
    public D getFirst()
    {
        return head.next.data;
    }

    /**
     * Get the 1st node in the list.
     *
     * @return
     */
    public Node<D> getFirstNode()
    {
        return head.next;
    }

    /**
     * A node containing a value.
     *
     * @param <D>
     */
    public static class Node<D>
    {
        Node<D> prev;
        Node<D> next;
        D data;

        public Node(D data)
        {
            this.data = data;
        }

        public D getData()
        {
            return data;
        }
    }
}