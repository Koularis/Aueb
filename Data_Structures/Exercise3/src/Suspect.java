public class Suspect {
        private int AFM; 
        private String firstName; 
        private String lastName; 
        private double savings; 
        private double taxedIncome; 
        
        public void setFirstName(String firstName){
            this.firstName = firstName;
        }

        public void setLastName(String lastName){
            this.lastName = lastName;
        }

        public void setSavings(double savings){
            this.savings = savings;
        }

        public void setTaxedIncome(double taxedIncome){
            this.taxedIncome = taxedIncome;
        }
        
        public void setKey(int AFM){
            this.AFM = AFM;
        }

        public String getFirstName(){
            return firstName;
        }

        public String getLastName(){
            return lastName;
        }

        public double getSavings(){
            return savings;
        }

        public double getTaxedIncome(){
            return taxedIncome;
        }

        public double priority_info(){
            return savings - taxedIncome; // se periptwsh pou den exei taxed income katw apo 9000 einai kapoios upoptos
        }

        public boolean compareSuspects(Suspect a,Suspect b){
            if(a.taxedIncome < 9000){
                return true;
            }
            else{
                if(a.priority_info() >= b.priority_info() && b.taxedIncome >= 9000){
                    return true;
                }
                else{
                    return false;
                }
            }

        }

        public int key() {return AFM;} 

        public String toString(){
            return this.key() + " " + this.getFirstName() + " " + this.getLastName() + " : " + this.getSavings() + " Savings " + "- " + this.getTaxedIncome() + " Taxed Income";
        }
       
}

