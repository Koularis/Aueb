import java.util.Scanner; 
public class DNAPalindrome {
	//checks if the whole sequence is acceptable or not
    public static boolean isAcceptable2(char a){
        if( a == 'T' || a == 'A' || a == 'C' || a == 'G'){
            return true;
        }
        return false;
    }

    public static void main(String args[]){
        boolean flag = true;
        String dna;
        StringDoubleEndedQueueImpl dna_seq = new StringDoubleEndedQueueImpl();
        Scanner scan = new Scanner(System.in);
        System.out.println("Please write the DNA sequence!");
        dna = scan.nextLine();
        for (int i=0;i<dna.length();i++){
            char dna_index = dna.charAt(i);
            if(isAcceptable2(dna_index)){
                String str_dna = String.valueOf(dna_index);
                switch(str_dna){
                    case "T":
                        str_dna = "A";
                        break;
                    case "A":
                        str_dna = "T";
                        break;
                    case "C":
                        str_dna = "G";
                        break;
                    case "G":
                        str_dna = "C";
                        break;
                }
                    //by adding each letter in the last position of the list they get inverted
					dna_seq.addFirst(str_dna);
            
            }
            else{
                flag = false;
                break;
            }
            
        }
        if(flag){
            int size = dna_seq.size();
            for(int i=0;i<size;i++){
				//we get the first element in the list and compare it with the first element of the string if they all match in the end the sequence is palindromic
                String ret = dna_seq.removeFirst();
                if(ret.equals(String.valueOf(dna.charAt(i)))){
                    if(i == size - 1){
                        System.out.println("Your sequence is palindromic!");
                    }
                    continue;                    
                }
                else{

                    System.out.println("Your sequence is not palindromic!");
                    break;
                }
            }
            
        }
        else{
            System.out.println("Invalid sequence!");
        }
    }
}
    
