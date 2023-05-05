import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

public class Dynamic_Median extends HeapPriority{
    private int counter = 0;
    private int default_capacity = 100; 

    public Dynamic_Median(){
        super();
        this.heap = new City[default_capacity];
    }
   
    public void insert(City x){
        City median = new City(); 
        counter++;
        double a = 0.75*default_capacity;
        int b = (int) a;
        if(counter>b){
            resize();
        }
        heap[counter] = x;
        swim(counter);
        sort(heap, counter);
        if(counter % 2 == 0){
           median = heap[(counter/2)];
        }
        else{
            median = heap[(counter/2)+1];
        }
        
        if(counter % 5 == 0){
            System.out.println(median.getName());
        }
    }
    
    protected void swap(int i, int j){
        City temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
        
    private void sort(City[] heap, int count){
        City[] New_heap = new City[count+1];
        int temp_counter = count;
        for(int i = 1; i <= count; i++){
            City temp = heap[1];
            swap(1,temp_counter);
            temp_counter--;
            sink(1,temp_counter);
            New_heap[i] = temp;
        }       
        for(int i = 1; i <= count; i++){
            heap[i] = New_heap[i];
        }
    }

    public void printName(){
        for(int i=1;i<=15;i++){
            System.out.println(heap[i].getName());
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Dynamic_Median a = new Dynamic_Median();
        int counter = 0;
        String text;
        boolean flag = true;
        System.out.println("Give the name of the txt file!");
        Scanner scan = new Scanner(System.in);
        text = scan.nextLine();
        try{
            File counter_text = new File(text);
            Scanner counter_reader = new Scanner(counter_text);
            while(counter_reader.hasNextLine()){
                counter++;
                counter_reader.nextLine();
            }
            counter_reader.close();
        }
        catch(FileNotFoundException e){
            System.out.println("No text file found!");
            flag = false;
        }
        if (flag){
            File cities_text = new File("a.txt");
            Scanner reader = new Scanner(cities_text);            
            for(int i = 0; i < counter; i++){
                City c1 = new City();
                c1.setID(Integer.parseInt(reader.next()));
                c1.setName(reader.next());
                c1.setPopulation(Integer.parseInt(reader.next()));
                c1.setCovidCases(Integer.parseInt(reader.next()));;
                a.insert(c1);   
            }          
        }
    }
}
