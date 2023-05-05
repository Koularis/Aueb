public class City implements Cityinterface{
    private String name;
    private int ID, population, CovidCases;
    
    public int getID(){
        return ID;
    }

    public String getName(){
        return name;
    }
    
    public int getPopulation(){
        return population;
    }

    public int getCovidCases(){
        return CovidCases;
    }

    public void  setID(int ID){
        this.ID = ID;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPopulation(int population){
        this.population = population;
    }
    public void setCovidCases(int CovidCases){
        this.CovidCases = CovidCases;
    }

    public double calculateDensity(int CovidCases,int population){
        double val = population/CovidCases;
        val = 50000/val;
        return Math.round(val * 100.0) / 100.0;
    }

}
