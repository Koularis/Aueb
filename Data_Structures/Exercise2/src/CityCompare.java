public class CityCompare extends City{
    public boolean compareCity(City heap,City heap2){
        double v1 = calculateDensity(heap.getCovidCases(), heap.getPopulation());
        double v2 = calculateDensity(heap2.getCovidCases(), heap2.getPopulation());
        if (v1 == v2){//epistrefei true an to v1 > v2
            if(heap.getName().compareTo(heap2.getName())==0){
                if(heap.getID() < heap2.getID()){
                    return true;
                }
                else{
                    return false;
                }
            }
            else if(heap.getName().compareTo(heap2.getName()) > 0){
                return false;
            }
            else{
                return true;
            }
        }
        else if (v1>v2){
            return true;
        }
        else{
            return false;
        }
    }
}
