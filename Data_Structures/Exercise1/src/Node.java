public class Node <String>
{
	String item;
    Node next;
    Node prev;

    Node(String v){   
        item = v;
        next = null;
        prev = null;
    }
}