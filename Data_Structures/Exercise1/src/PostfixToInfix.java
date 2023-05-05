import java.util.Scanner; 

public class PostfixToInfix {
    
	//checks if a character from the expression is numeric or not
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
	//checks if the whole expression is acceptable or not
    public static boolean isAcceptable(String str_a){
        int operand_counter = 0;
        int operator_counter = 0;
        for (int i=0;i<str_a.length();i++){        
            char s = str_a.charAt(i);
        if (isNumeric(String.valueOf(s)) || ( s == '+' || s == '-' || s == '*' || s == '/')){ 
           if(isNumeric(String.valueOf(s))){
               operand_counter++;
           }
           else{
               operator_counter++;
           }
            continue;
        
        }
        else{
            return false;
        }
        
    }
	//makes sure that the expression ends with an operand and it starts with two operands
    if((operator_counter + 1) == operand_counter) {
        if(isNumeric(String.valueOf(str_a.charAt(str_a.length()-1))) || (!isNumeric(String.valueOf(str_a.charAt(0))) || (!isNumeric(String.valueOf(str_a.charAt(1)))))){
        return false;   
        }
        else{
        return true;
    }
    
}
    else{
        return false;
    }
}
    
    
    public static void main(String args[]){
        String myval;
        StringDoubleEndedQueueImpl list = new StringDoubleEndedQueueImpl();
        Scanner scan = new Scanner(System.in);
        System.out.println("Please write your expression!");    
        myval = scan.nextLine();
        if (!isAcceptable(myval)){
            System.out.println("Invalid expression!");
        }
        else{    
            for(int i=0;i<myval.length();i++){
                char val_index = myval.charAt(i);
                String newN = String.valueOf(val_index);
                if(isNumeric(newN)){
                    list.addLast(newN);                
                }
                else{
					//when the character is an operator we get the two last operands from the list we store them in two temporary variables and remove them from the list
                    String a = list.getLast();
                    list.removeLast();
                    String b = list.getLast();
                    list.removeLast();
					//lastly we add the last two operands with the operator between them and closed parenthesis
                    list.addLast("(" + b + " " + newN + " " + a + ")");
                }
                
            }
            list.printQueue(System.out);
       }
    }
}   


        


    


