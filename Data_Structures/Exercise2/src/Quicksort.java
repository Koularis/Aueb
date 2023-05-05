public class Quicksort {
    CityCompare uri = new CityCompare();

    void sort(City[] cit, int left, int right) 
    { 
        if (left < right) 
        { 
            /* pi is partitioning index, arr[pi] is  
              now at right place */
            int pi = partition(cit, left, right); 
  
            // Recursively sort elements before 
            // partition and after partition 
            sort(cit, left, pi-1); 
            sort(cit, pi+1, right); 
        } 
    } 



    private int partition(City[] cit, int left, int right) 
    { 
        City pivot = cit[right];  
        int i = (left-1); // index of smaller element 
        for (int j=left; j<right; j++) 
        { 
            // If current element is smaller than the pivot 
            if (uri.compareCity(cit[j], pivot))
            { 
                i++; 
  
                // swap arr[i] and arr[j] 
				City temp = cit[i];
				cit[i] = cit[j];
				cit[j] = temp;
            } 
        } 
  
        // swap arr[i+1] and arr[high] (or pivot)
		City temp = cit[i + 1];
		cit[i + 1] = cit[right];
		cit[right] = temp; 
  
        return i+1; 
    } 
}