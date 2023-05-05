import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RandomizedBST implements MafiaInterface {
    private TreeNode root = null;
    private double mean_savings = 0; //sum olwn twn savings
    private int counter = 0; // komvoi

    
    private class TreeNode {
        public TreeNode(Suspect item) {
            this.item = item;
        }

        Suspect item;
        TreeNode left;
        TreeNode right;
        int N;
    }

    private boolean less(int item_key, int treenode_item_key){
        if(item_key < treenode_item_key){
            return true;
        }
        return false;
    }

    private boolean equals(int a,int b){
        if(a == b){
            return true;
        }
        else{
            return false;
        }
    }
    
    private TreeNode insertAsRoot(TreeNode head, Suspect item) {
        if (head == null)
            return new TreeNode(item);
        if (Math.random() * (head.N + 1) < 1.0)
            return insertT(head, item);
        if (less(item.key(),head.item.key()))
            head.left = insertAsRoot(head.left, item);
        else
            head.right = insertAsRoot(head.right, item);
        head.N++;
        return head;
    }

    public void insert(Suspect item) {
        if(searchByAFM(item.key()) != null){
            System.out.println("Error: Duplicate AFM. Program is now exiting...");
            System.exit(0);
        }
        root = insertAsRoot(root, item);
    }

    private TreeNode insertT(TreeNode head, Suspect item) {
        if (head == null)
            return new TreeNode(item);
        if (less(item.key(),head.item.key())) {
            head.left = insertT(head.left, item);
            head = rotR(head);
        } else {
            head.right = insertT(head.right, item);
            head = rotL(head);
        }
        return head;
    }

    
    private TreeNode removeR(TreeNode h, int AFM) {
        if (h == null) return null;
        int w = h.item.key();
        if (less(AFM, w)) h.left = removeR(h.left, AFM);
        if (less(w, AFM)) h.right = removeR(h.right, AFM);
        if (equals(AFM, w)) h = joinLR(h.left, h.right);
        return h; } 
    
    public void remove(int AFM) {
        counter--;
        root = removeR(root, AFM); 
    }  
    
    private TreeNode joinLR(TreeNode a, TreeNode b) {       
        if (a == null)
            return b;
        if (b == null)
            return a;
        int N = a.N + b.N;
        if (Math.random() * N < 1.0 * a.N) {
            a.right = joinLR(a.right, b);
            return a;
        } else {
            b.left = joinLR(a, b.left);
            return b;
        }
    }

    private TreeNode rotR(TreeNode head) {
        TreeNode x = head.left;
        head.left = x.right;
        x.right = head;
        return x;
    }

    private TreeNode rotL(TreeNode head) {
        TreeNode x = head.right;
        head.right = x.left;
        x.left = head;
        return x;
    }

    public void load(String filename) throws FileNotFoundException {
        try{
            File try_text = new File(filename);
            Scanner try_reader = new Scanner(try_text);
            while(try_reader.hasNextLine()){
                try_reader.nextLine();
            }
                try_reader.close();
        }
        catch(FileNotFoundException e){
            System.out.println("No text file found!");
        }
        File text = new File(filename);
        Scanner reader = new Scanner(text);
        while(reader.hasNextLine()){
            Suspect x = new Suspect();
            x.setKey(Integer.parseInt(reader.next()));
            x.setFirstName(reader.next());
            x.setLastName(reader.next());
            x.setSavings(Double.parseDouble(reader.next()));
            x.setTaxedIncome(Double.parseDouble(reader.next()));
            this.insert(x);
            counter++;
        }
        reader.close();
    }

    public void updateSavings(int AFM, double savings) {
            searchByAFM(AFM).setSavings(savings); // searchbyafm endodiatetagmenh psaxnei to afm
            System.out.println("Savings updated!");
    }

    public Suspect searchByAFM(int AFM){
        TreeNode head = root;
        while(head != null){
            if(head.item.key() == AFM ){
                return head.item;
            }
            else if(head.item.key() > AFM){
                head = head.left;
            }
            else if(head.item.key() < AFM){                           
                head = head.right;
            }
        }
        return null;            
    }    

    private void traverseRootSBLN(TreeNode head,String last_name,List ln) {
        if(head == null)return;
        traverseRootSBLN(head.left, last_name, ln);
        if(head.item.getLastName().equals(last_name)){ // an vrei to last name to kanei add se mia lista
            ln.add(head.item);
        }
        traverseRootSBLN(head.right, last_name, ln);
    }
        
    void traverseSBLN(String last_name,List ln) { traverseRootSBLN(root,last_name,ln); }

    public List searchByLastName(String last_name){
        List last = new List();
        traverseSBLN(last_name,last); 
        return last;       
    }

    void traverseRootGMS(TreeNode head){
        if(head==null)return;
        traverseRootGMS(head.left);
        this.mean_savings += head.item.getSavings();
        traverseRootGMS(head.right);        
    }

    void traverseGMS() {traverseRootGMS(root);}

    public double getMeanSavings(){
        this.mean_savings = 0;
        traverseGMS(); // mesos oros savings
        return mean_savings/counter;
    }
        
    void traverseRootPBA(TreeNode head){
        if(head == null) return;
        traverseRootPBA(head.left);
        System.out.println(head.item); // print all me endodiatetagmenh
        traverseRootPBA(head.right);
    }

    void traversePBA() {traverseRootPBA(root);}
    
    public void printByAFM() {
        traversePBA();
    }

    void traverseRootPTS(TreeNode head,int k,List top){
        if(head == null) return;
        traverseRootPTS(head.left,k,top);
        top.add_top_suspects(head.item, k);
        traverseRootPTS(head.right,k,top);
    }

    void traversePTS(int k,List top) {traverseRootPTS(root, k,top);}
    
    public void printΤopSuspects(int k) {
        List top_suspects = new List();
        traversePTS(k,top_suspects);
        top_suspects.printSuspects(k);
    }

    public static void main(String args[]) throws FileNotFoundException {
        RandomizedBST a = new RandomizedBST();
        while(true){
            System.out.println("");
            System.out.println("Select an option!");
            System.out.println("1.Load your file with potential suspects.");
            System.out.println("2.Update savings of a suspect.");
            System.out.println("3.Search a suspect by their AFM.");
            System.out.println("4.Print suspects with a certain last name.");
            System.out.println("5.Remove a suspect.");
            System.out.println("6.Print mean savings.");
            System.out.println("7.Print all suspects.");
            System.out.println("8.Print top suspects.");
            System.out.println("9.Exit");
            Scanner scan_obj = new Scanner(System.in);
            int option = scan_obj.nextInt();
            if(option == 1){
                System.out.println("");
                System.out.println("Give us the name of the txt file.");
                String file = scan_obj.next();
                a.load(file);
                System.out.println("File loaded");
            }
            if(option == 2){
                System.out.println("");
                System.out.println("Write the AFM of the suspect, whose savings need to be updated.");
                int AFM_option = scan_obj.nextInt();
                System.out.println("Now write the updated savings.");
                double savings_option = scan_obj.nextDouble();
                a.updateSavings(AFM_option, savings_option);
            }
            if(option == 3){
                System.out.println("");
                System.out.println("Write the AFM you want to search.");
                int AFM_option = scan_obj.nextInt();
                System.out.println(a.searchByAFM(AFM_option));
            }
            if(option == 4){
                System.out.println("");
                System.out.println("Write the last name you want to search.");
                String last_name = scan_obj.next();
                List last_list = a.searchByLastName(last_name);
                last_list.printLastNames();
            }
            if(option == 5){
                System.out.println("");
                System.out.println("Write the AFM of the suspect you want to remove.");
                int AFM_option = scan_obj.nextInt();
                a.remove(AFM_option);
            }
            if(option == 6){
                System.out.println("");
                System.out.print("Mean savings: ");
                System.out.println(a.getMeanSavings());
            }
            if(option == 7){
                System.out.println("");
                System.out.println("List of the suspects by increasing AFM:");
                a.printByAFM();
            }
            if(option == 8){
                System.out.println("");
                System.out.println("How many of the top suspects do you want to print?");
                int suspects_option = scan_obj.nextInt();
                a.printΤopSuspects(suspects_option);
            }
            if(option == 9){
                System.out.println("Now exiting...");
                break;
            }
            System.out.println("");
            System.out.println("Press 1 if you want to continue!");
            int exit_option = scan_obj.nextInt();
            if(exit_option != 1){break;}
        }
    }
}



