import java.util.ListIterator;

import org.w3c.dom.Node;

/**
 * A linked list of character data objects.
 * (Actually, a list of Node objects, each holding a reference to a character
 * data object.
 * However, users of this class are not aware of the Node objects. As far as
 * they are concerned,
 * the class represents a list of CharData objects. Likewise, the API of the
 * class does not
 * mention the existence of the Node objects).
 */
public class List {

    // Points to the first node in this list
    private Node first;

    // The number of elements in this list
    private int size;

    /** Constructs an empty list. */
    public List() {
        first = null;
        size = 0;
    }

    /** Returns the number of elements in this list. */
    public int getSize() {
        return size;
    }

    /** Returns the first element in the list */
    public CharData getFirst() {
        return first.cp;
    }

    /**
     * GIVE Adds a CharData object with the given character to the beginning of this
     * list.
     */
    public void addFirst(char chr) {
        CharData newCharData = new CharData(chr);
        Node newNode = new Node(newCharData, this.first);
        this.first = newNode;
        size++;
    }

    /** GIVE Textual representation of this list. */
    public String toString() {
        Node curr = first;
        StringBuilder str = new StringBuilder("(");
        while (curr != null) {
            str.append(current + " ");
            curr = curr.next;
        }
        return str.substring(0, str.length() - 1)+ ")";
    }

    /**
     * Returns the index of the first CharData object in this list
     * that has the same chr value as the given char,
     * or -1 if there is no such object in this list.
     */
    public int indexOf(char chr) {
        Node curr = first;
        int index = 0;
        while (curr != null) {
            if (curr.cp.chr == chr) {
                return index;
            }
            curr = curr.next;
            index++;
        }
        return -1;
    }

    /**
     * If the given character exists in one of the CharData objects in this list,
     * increments its counter. Otherwise, adds a new CharData object with the
     * given chr to the beginning of this list.
     */
    public void update(char chr) {
        int index=this.indexOf(chr);
        if (index >= 0) {
            this.get(index).count++;
        } else {
            this.addFirst(chr);
        }
    }

    /**
     * GIVE If the given character exists in one of the CharData objects
     * in this list, removes this CharData object from the list and returns
     * true. Otherwise, returns false.
     */
    public boolean remove(char chr) {
        Node prev = null, curr = first;
        if (curr == chr) {
            first = first.next;
            size--;
            return true;
        }
        while (curr != null && curr != chr) {
            prev = curr;
            curr = curr.next;
        }
        if (curr == null) {
            return false; 
        }
        prev.next = curr.next;
        size--;
        return true;
    }

    /**
     * Returns the CharData object at the specified index in this list.
     * If the index is negative or is greater than the size of this list,
     * throws an IndexOutOfBoundsException.
     */
    public CharData get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node curr = first;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.cp;
    }

    /**
     * Returns an array of CharData objects, containing all the CharData objects in
     * this list.
     */
    public CharData[] toArray() {
        CharData[] array = new CharData[size];
        Node curr = first;
        int i = 0;
        while (curr != null) {
            array[i++] = curr.cp;
            curr = curr.next;
        }
        return array;
    }

    /**
     * Returns an iterator over the elements in this list, starting at the given
     * index.
     */
    public ListIterator listIterator(int index) {
        // If the list is empty, there is nothing to iterate
        if (size == 0)
            return null;
        // Gets the element in position index of this list
        Node current = first;
        int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        // Returns an iterator that starts in that element
        return new ListIterator(current);
    }
}