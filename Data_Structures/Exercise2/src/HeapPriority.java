public class HeapPriority implements HeapPriorityInterface<City>{
    public City[] heap;
    public int[] id_heap = new int[1000];
    public int size_counter = 0;
    private int default_capacity = 5;
    public CityCompare compare= new  CityCompare();

    public HeapPriority(){
        this.heap= new City[default_capacity];
    }
    
    public boolean isEmpty(){
       return (size_counter == 0);

    }

    public int size(){
        return size_counter;
    }

    protected void resize(){
        City[] newHeap =  new City[heap.length*2];
        for (int i = 0; i <= size_counter; i++){
            newHeap[i] = heap[i];
        }
        heap = newHeap;

    }

    protected void swap(int i, int j){
        City temp = heap[i];
        int temp_i = id_heap[heap[i].getID()];
        id_heap[heap[i].getID()] = id_heap[heap[j].getID()];
        heap[i] = heap[j];
        id_heap[heap[j].getID()] = temp_i;
        heap[j] = temp;
    }

    public City max(){
        return heap[1];
    }

    protected void sink(int k, int N) {
        while (2*k <= N) {
        int j = 2*k;
        if (j < N && !compare.compareCity(heap[j], heap[j+1])) j++;
        if (compare.compareCity(heap[k], heap[j])) break;
        swap(k, j); k = j; }
        }

    protected void swim(int k) {
        while (k > 1 && !compare.compareCity(heap[k/2],heap[k])) {
        swap(k, k/2); k = k/2; } }


        
    public void insert(City x){
        size_counter++;
        double a = 0.75*default_capacity;
        int b = (int) a;
        if(size_counter>b){
            resize();
        }
        heap[size_counter] = x;
        id_heap[x.getID()] = size_counter;
        swim(size_counter);
    }

    public City getmax(){
        City temp = heap[1];
        swap(1,size_counter);
        size_counter--;
        sink(1,size_counter);
        return temp;
    }

    public City remove(int id){
        int temp_position = id_heap[id];
        City temp_city = heap[temp_position];
        swap(temp_position,size_counter);
        size_counter--;
        sink(temp_position,size_counter);
        return temp_city;
    } 
}