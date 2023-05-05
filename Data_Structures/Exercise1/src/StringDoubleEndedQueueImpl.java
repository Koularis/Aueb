import java.io.PrintStream;
import java.util.NoSuchElementException;

public class StringDoubleEndedQueueImpl  implements StringDoubleEndedQueue {
    
    protected Node head = null;
    protected Node tail = null;
	//the counter gets incremented each time we create a new node object and gets decreased each time we remove a node object
    protected int counter = 0; 

    public boolean isEmpty(){
        return (head == null);   
    }
    
    public void addFirst(String item){
        Node <String> newNode = new Node<String>(item);
		counter++;
        if (isEmpty()){
            head = newNode;
            tail = newNode;            
        }
        else{
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
    } 

    public String removeFirst() throws NoSuchElementException{
        if (isEmpty()){
            throw new NoSuchElementException();
        }
       else if(head == tail){
            counter = counter - 1;
            String temp = (String)head.item;
            head = null;
            tail = null;
            return temp;
       }
        else{
		    counter = counter - 1;
            String temp = (String)head.item;
            head = head.next;
            head.prev = null;
            return temp;
        }
    }

    public void addLast(String item){
        Node <String> newNode = new Node<String>(item);
		counter++;
        if (isEmpty()){
            head = newNode;
            tail = newNode;
        }
        else{
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
    }

    public String removeLast() throws NoSuchElementException{
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        else if(head == tail){
            counter = counter - 1;
            String temp = (String)head.item;
            head = null;
            tail = null;
            return temp;
       }
        else{
			counter = counter - 1;
            String temp = (String)tail.item;
            tail = tail.prev;
            tail.next = null;
            return temp;
        }
    }

    public String getFirst() throws NoSuchElementException{
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        else{
            return (String)head.item;
        }
    }

    public String getLast() throws NoSuchElementException{
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        else{
            return (String)tail.item;
        }
    }
    
    public void printQueue(PrintStream stream){
        
        if (isEmpty()){
            stream.println("There is nothing to print !");
        }
        else{     
            Node x = head;
            for(int i = 0; i < counter;i++ ){
                stream.println(x.item);
                x = x.next;
            }
        }
    }

    public int size(){
   
        return counter;
    
    }



}





