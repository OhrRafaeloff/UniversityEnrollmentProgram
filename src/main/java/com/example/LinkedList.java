//Ohr Rafaeloff 

package com.example;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LinkedList<T> implements Iterable<T> {
    private Node<T> head; // Reference to the head node of the list
    private int size; // To track the size of the list

    // Inner Node class representing each element in the list
    private static class Node<T> {
        T data; // The data stored in the node
        Node<T> next; // Reference to the next node in the list

        Node(T data) { // Constructor to initialize the node with data
            this.data = data;
            this.next = null;
        }
    }

    // Add node to the end of the list
    public void add(T data) {
        if (data == null) { // Check if data is null and throw an exception
            throw new IllegalArgumentException("Empty values are not allowed.");
        }
        
        Node<T> newNode = new Node<>(data); // Create a new node with the provided data
        if (head == null) { // If the list is empty, set the new node as the head
            head = newNode;
        } else { // Otherwise, traverse to the end and add the new node
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++; // Increment size when a new node is added
    }

    // Delete a node by data
    public boolean remove(T data) {
        if (head == null) return false; // If the list is empty, return false

        // Use Objects.equals for null-safe comparison of data
        if (Objects.equals(head.data, data)) {
            head = head.next; // Update head if the node to remove is the first node
            size--; // Decrement size when a node is deleted
            return true;
        }

        Node<T> current = head;
        while (current.next != null && !Objects.equals(current.next.data, data)) {
            current = current.next; // Traverse the list to find the node to remove
        }

        if (current.next == null) return false; // If the node is not found, return false

        current.next = current.next.next; // Remove the node by skipping over it
        size--; // Decrement size when a node is deleted
        return true;
    }

    // Get data at a specific position
    public T get(int index) {
        if (index < 0 || index >= size) { // Check if index is within bounds
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        Node<T> current = head;
        int count = 0;
        while (current != null) { // Traverse the list to the specified index
            if (count == index) return current.data;
            count++;
            current = current.next;
        }
        return null; // This line should never be reached due to the bounds check above
    }

    // Get size of the list
    public int size() {
        return size; // Return the size field instead of traversing the list
    }

    // Sort the list using a comparator
    public void sort(Comparator<T> comparator) {
        if (comparator == null) { // Check if the comparator is null
            throw new IllegalArgumentException("Comparator cannot be empty.");
        }

        if (head == null || head.next == null) return; // If the list has 0 or 1 node, it's already sorted

        // Bubble sort implementation to sort the linked list
        for (Node<T> current = head; current.next != null; current = current.next) {
            for (Node<T> nextNode = current.next; nextNode != null; nextNode = nextNode.next) {
                if (comparator.compare(current.data, nextNode.data) > 0) {
                    // Swap the data if the current node is greater than the next node
                    T temp = current.data;
                    current.data = nextNode.data;
                    nextNode.data = temp;
                }
            }
        }
    }

    // Display the list as a string representation
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) { // Traverse the list and append each node's data to the StringBuilder
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", "); // Add a comma separator between elements
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString(); // Return the formatted string representation of the list
    }

    // Convert LinkedList to java.util.List for easier use in UI components
    public List<T> toList() {
        List<T> list = new ArrayList<>(size); // Set initial capacity to size for efficiency
        Node<T> current = head;
        while (current != null) { // Traverse the list and add each node's data to the ArrayList
            list.add(current.data);
            current = current.next;
        }
        return Collections.unmodifiableList(list); // Return an unmodifiable list to ensure immutability
    }

    // Iterator to help with enhanced for loops and streams
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head; // Start iteration from the head

            @Override
            public boolean hasNext() {
                return current != null; // Check if there is another node to iterate over
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException(); // Throw exception if there are no more elements
                }
                T data = current.data; // Get the data of the current node
                current = current.next; // Move to the next node
                return data;
            }
        };
    }

    // Remove node by index
    public boolean remove(int index) {
        if (index < 0 || index >= size) { // Check if index is within bounds
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        if (index == 0) { // If removing the head node
            head = head.next; // Update head to the next node
            size--;
            return true;
        }

        Node<T> current = head;
        for (int i = 0; i < index - 1; i++) { // Traverse to the node just before the one to be removed
            current = current.next;
        }

        if (current.next != null) { // Check if the next node exists
            current.next = current.next.next; // Remove the node by skipping over it
            size--;
            return true;
        }

        return false; // Return false if the node could not be removed
    }

    // Implement a stream method to return a Stream of the elements
    public Stream<T> stream() {
        // Create a stream from the linked list using an iterator
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }
}
