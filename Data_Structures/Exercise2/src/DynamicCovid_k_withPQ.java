import java.util.Scanner;
import java.lang.Math;
import java.io.FileNotFoundException;
import java.io.File;
class DynamicCovid_k_withPQ extends HeapPriority{
    int k;
    int default_capacity;
    City min;
    int child_position;
    int size_counter = 0;
    public CityCompare compare= new  CityCompare();    
    public DynamicCovid_k_withPQ(int k){
        super();
        this.k = k;
        this.default_capacity = 2*k;
        this.heap = new City[default_capacity];
        this.child_position = (int) Math.pow(2,(int)(Math.log(k) / Math.log(2)));
    }

    public void insert(City x) {
        if (size_counter < k)
        {
            size_counter++;
            heap[size_counter] = x;
            swim(size_counter);
        }
        else
        {
            heap[k + 1] = x;
            min = heap[child_position];
            int min_pos = child_position;
            for (int i = child_position + 1; i <= k; i++) 
            {
                if (compare.compareCity(min,heap[i]))
                {
                    min = heap[i];
                    min_pos = i;
                }
            }
            if (compare.compareCity(x,min))
            {
                swap(k + 1, min_pos);
                swim(min_pos);
                heap[k+1]= null;
            }
        }
        
   }

   public void print(){
       System.out.println("Top " + k + " cities are:");
        for(int i = 1; i <= k; i++){
            System.out.println(heap[i].getName());
       }
   }

            public static void main(String[] args) throws FileNotFoundException {
                int counter = 0;
                int k;
                String text;
                boolean flag = true;
                System.out.println("Give the name of the txt file!");
                Scanner scan = new Scanner(System.in);
                text = scan.nextLine();
                System.out.println("Give the number of the cities you want to print!");
                k = scan.nextInt();
                scan.close();
                DynamicCovid_k_withPQ a = new DynamicCovid_k_withPQ(k);
                try{
                    File try_text = new File(text);
                    Scanner try_reader = new Scanner(try_text);
                    while(try_reader.hasNextLine()){
                        counter++;
                        try_reader.nextLine();
                    }
                    try_reader.close(); 
                }
                catch(FileNotFoundException e){
                    System.out.println("No text file found!");
                    flag = false;
                }
                if (flag){
                    File cities_text = new File(text);
                    Scanner reader = new Scanner(cities_text);
                    for(int i = 1; i <= counter; i++){
                        City c1 = new City();
                        c1.setID(Integer.parseInt(reader.next()));
                        c1.setName(reader.next());
                        c1.setPopulation(Integer.parseInt(reader.next()));
                        c1.setCovidCases(Integer.parseInt(reader.next()));
                        a.insert(c1);             
                    }
                
                    reader.close();
                }
                a.print();
            }
}
