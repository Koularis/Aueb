public class List {
    protected Node head = null;
    protected Node tail = null;
    private int counter = 0;
    
    public class Node{
        Suspect item;
        Node next;

        public Node(Suspect v){   
            item = v;
            next = null;
        }
    }
    
    public void add(Suspect item){
        Node  newNode = new Node(item);
        counter++;
        if(head == null){
            head = newNode;
        }
        else{
            newNode.next = head;
            head = newNode;
        }
    }

    public void add_top_suspects(Suspect item,int k){
        Suspect comparable = new Suspect();
        Node newn = new Node(item);
        Node x = head;
        if(head == null){
            head = newn;
            counter++;
            return;
        }
        if(k == 1){
            if(comparable.compareSuspects(item, head.item)){
                head = newn;
                return;
            }
            else{return;}
        }
        if(comparable.compareSuspects(x.item, item)){
            if(k > counter){
                newn.next = head;
                head = newn;
                counter++;
                return;
            }
            else{
                newn.next = head.next;
                head = newn;
                return;
            }
        }
        else{
            if(k > counter){
                while(x.next != null){
                    if(comparable.compareSuspects(x.next.item,item)){
                        newn.next = x.next;
                        x.next = newn;
                        counter++;
                        return;
                    }
                    else{ x = x.next;}
                }
                x.next = newn;
                counter++;
                return;
            }
            else{
                while(x.next != null){
                    if(comparable.compareSuspects(x.next.item, item)){
                        newn.next = x.next;
                        x.next = newn;
                        head = head.next;
                        return;
                   }
                   else{x = x.next;}
               }
               if(x.next == null && comparable.compareSuspects(item,x.item)){
                    x.next = newn;
                    head = head.next;
                    return;
               }
            }
        }
              
    }
    
    
    public int size(){
        return counter;
    }

    public void printLastNames(){
        if(counter == 0)System.out.println("No such last name!");
        else if(counter > 5)System.out.println("Too many people with such name!");
        else{
            Node x = head;
            System.out.println("People with the name " + x.item.getLastName() + ":");
            for(int i = 0; i < this.counter; i++){
                System.out.println(x.item);
                x = x.next;
            }
        }
    }

    public void printSuspects(int k){
        System.out.println("");
        System.out.println("These are the top " + k + " suspects in increasing order:" );
        Node x = head;
        while(x != null){
            System.out.println(x.item);
            x = x.next;
        }
        
    }

}

