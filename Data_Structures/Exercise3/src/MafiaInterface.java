import java.io.FileNotFoundException;

public interface MafiaInterface{
    void insert(Suspect item);
    void load(String filename) throws FileNotFoundException;
    void updateSavings(int AFM, double savings);
    Suspect searchByAFM(int AFM);
    List searchByLastName(String last_name);
    void remove(int AFM);
    double getMeanSavings();
    void printΤopSuspects(int k);
    void printByAFM();
}
