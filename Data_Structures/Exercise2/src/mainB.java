import java.util.Scanner;
import java.lang.Math;
import java.io.FileNotFoundException;
import java.io.File;
public class mainB {
    public static void main(String args[]) throws FileNotFoundException {
        HeapPriority a = new HeapPriority();
        File cities_text = new File("a.txt");
        Scanner reader = new Scanner(cities_text);            
        for(int i = 0; i < 6; i++){
            City c1 = new City();
            c1.setID(Integer.parseInt(reader.next()));
            c1.setName(reader.next());
            c1.setPopulation(Integer.parseInt(reader.next()));
            c1.setCovidCases(Integer.parseInt(reader.next()));
            a.insert(c1);             
        }

    }
}
