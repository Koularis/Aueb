console.log("meow")

const password = document.getElementById("password");
const confpassword = document.getElementById("cpassword");
const email = document.getElementById("email")
const bdate = document.getElementById("bdate");
const pnumber = document.getElementById("pnumber");
const form = document.getElementById("form");

form.addEventListener("submit", e => { // upon form submission this block gets triggered
    e.preventDefault();

    validate(); // function to validate user data
    input1 = password.parentElement.parentElement;
    input2 = confpassword.parentElement.parentElement;
    input3 = bdate.parentElement.parentElement;
    input4 = pnumber.parentElement.parentElement;
    input5 = email.parentElement.parentElement;
    if(input1.classList.contains("success") && input2.classList.contains("success") && input3.classList.contains("success") && input4.classList.contains("success") && input5.classList.contains("success")){ // if this statement is true then we submit the form
        document.getElementById("form").submit();
        alert("Registered Succesfully");
    }else{
        alert("Errors in form");
    }
})

const setError = (element, message) => { // sets the specified element an error text and assigns an error class
    const inputControl = element.parentElement.parentElement;
    const errorDisplay = inputControl.querySelector('.error');

    errorDisplay.innerText = message;
    inputControl.classList.add('error');
    inputControl.classList.remove('success');
}

const setSuccess = element => { // removes the error text from the specified element and assigns a success class
    const inputControl = element.parentElement.parentElement;
    const errorDisplay = inputControl.querySelector('.error');

    errorDisplay.innerText = '';
    inputControl.classList.add('success');
    inputControl.classList.remove('error');
};

const isValidEmail = email => { // checks if the email given is of this format
    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

const validate = () => {
    const passwordValue = password.value.trim();
    const passwordConfvalue = confpassword.value.trim();
    const emailValue = email.value.trim();
    const pnumberValue = pnumber.value.trim();
    const bdateValue = bdate.value.trim();
    const bdateIntvalue = parseInt(bdateValue.split("-")[0])
    
    //----------password validation---------

    if(passwordValue === ""){ 
        setError(password, 'Password is required');
    }else if(passwordValue.length < 8){
        setError(password, 'Password must be at least 8 character.');
    }else{
        setSuccess(password);
    }

    if(passwordConfvalue === "") {
        setError(confpassword, 'Please confirm your password');
    } else if (passwordConfvalue !== passwordValue) {
        setError(confpassword, "Passwords don't match");
    } else {
        setSuccess(confpassword);
    }

    //--------------------------------------

    //-----------email validation-----------

    if (!isValidEmail(emailValue)) {
        setError(email, 'Provide a valid email address');
    } else {
        setSuccess(email);
    }

    //--------------------------------------

    //------------date validation-----------

    if(new Date().getFullYear() - bdateIntvalue <= 120){
        setSuccess(bdate);
    }else{
        setError(bdate, "Invalid birthdate");
    }

    //--------------------------------------

    //-----------phone validation-----------

    if(!(isNaN(pnumberValue))){
        if(pnumberValue.length == 10){
            setSuccess(pnumber)
        }else{
            setError(pnumber, "Invalid phone number")
        }
    }else{
        setError(pnumber, "Invalid phone number");
    }

    //--------------------------------------

}