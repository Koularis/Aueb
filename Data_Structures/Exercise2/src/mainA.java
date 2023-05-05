import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.File;

public class mainA {

    public static void main(String args[]) throws FileNotFoundException {
        String text;
        int k;
        int counter = 0;
        boolean flag = true;
        System.out.println("Give the name of the txt file!");
        Scanner scan = new Scanner(System.in);
        text = scan.nextLine();
        System.out.println("Give the number of the cities you want to print!");
        k = scan.nextInt();
        scan.close();
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
            City[] cities = new City[counter];
            for(int i = 0; i < counter; i++){
                City c1 = new City();
                c1.setID(Integer.parseInt(reader.next()));
                c1.setName(reader.next());
                c1.setPopulation(Integer.parseInt(reader.next()));
                c1.setCovidCases(Integer.parseInt(reader.next()));
                cities[i] = c1;
            }
            reader.close();
            Quicksort b = new Quicksort();
            b.sort(cities,0,cities.length - 1);
            if(k<= counter){
                System.out.println("The top " + k + " cities are:");
                for(int i = 0; i < k; i++){
                    System.out.println(cities[i].getName());
                }
            }
            else{
                System.out.println("The top " + counter + " cities are:");
                for(int i = 0; i < counter; i++){
                    System.out.println(cities[i].getName());
                }
                System.out.println("\nYou asked for too many cities!");
                int u = 5;
                u = u/2;
                System.out.println(u);
            }
        }
    }
}




