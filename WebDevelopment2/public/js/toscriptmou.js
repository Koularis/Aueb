var products = []
var session = {username: "", password: "", sessionId: ""}

async function fill_template() {
  
    var template = Handlebars.compile(document.querySelector("#template").innerHTML);
    const response = await fetch("https://wiki-shop.onrender.com/categories")
    const data = await response.json()
    var filled = template(data[0])
    for (let i = 0; i < data.length-1; i++) {
      filled += template(data[i+1])
    }
    document.querySelector("#output").innerHTML = filled;
}

async function showAllProducts() {
  
  var template = Handlebars.compile(document.querySelector("#template").innerHTML);
  const searchKeys = window.location.search
  const urlParams = new URLSearchParams(searchKeys)
  const param = urlParams.get("categoryId")
  const response = await fetch("https://wiki-shop.onrender.com/categories/" + param + "/products")
  const data = await response.json()
  var filled = template(data[0])
  products.push(data[0])
  for (let i = 0; i < data.length-1; i++) {
    filled += template(data[i+1])
    products.push(data[i+1])
  }
  document.querySelector("#output").innerHTML = filled
}

function scrape(subcategory_id) {

  var template = Handlebars.compile(document.querySelector("#template").innerHTML);
  if (subcategory_id == 0) {
    var filled = template(products[0])
    for (let i = 0; i < products.length-1; i++) {
      filled += template(products[i+1])
    }
    document.querySelector("#output").innerHTML = filled
  }else {
    var filled;
    var flag = false
    if (subcategory_id == products[0].subcategory_id) {
      var filled = template(products[0])
      flag = true
    }
    for (let i=0;i<products.length-1;i++) {
      if (subcategory_id == products[i+1].subcategory_id) {
        filled += template(products[i+1])
      }
    }
    if (!flag) { 
      filled = filled.slice(9)
    }
    document.querySelector("#output").innerHTML = filled
  }
}

function login() {
  
  const user = document.getElementById("username").value
  const passw = document.getElementById("password").value

  const data = {username: user, password: passw}
  fetch("http://localhost:8080/login", {
    method: "POST",
    headers: new Headers({"Content-Type": "application/json"}),
    body: JSON.stringify(data)
  }).then(res => res.json())
    .then(data => {
      if (data.status == 200) {
        let message = document.getElementById("login")
        message.innerHTML = "logged in as " + user
        session.username = user
        session.password = passw 
        session.sessionId = data.sessionId
        // remove = document.getElementById("onlogin")
        // remove.innerHTML = ""
        // remove.classList.remove("login_button");
        // remove.removeAttribute("id")
        var template = Handlebars.compile(document.querySelector("#template3").innerHTML);
        var filled = template(session)
        document.querySelector("#output3").innerHTML = filled
      }else{
        let message = document.getElementById("login")
        message.innerHTML = "invalid credentials"
      }
    })
    .catch(err => console.log(err))
}

function addToCart(id) {
  
  let productToAdd
  for (let i=0;i<products.length;i++) {
    if (id == products[i].id) {
      productToAdd = products[i]
      break
    }
  }
  const data = Object.assign({}, session, productToAdd)
  fetch("http://localhost:8080/addtocart", {
    method: "POST",
    headers: new Headers({"Content-Type": "application/json"}),
    body: JSON.stringify(data)
  }).then(res => res.json())
    .then(data => {
      if (data.status == 200) {
        let message = document.getElementById("added " + id)
        message.innerHTML = "Added"
      }else {
        alert("You must login first")
      }
    })
}

function showCartSize() {
  
  var data = session
  fetch("http://localhost:8080/showcartsize", {
    method: "POST",
    headers: new Headers({"Content-Type": "application/json"}),
    body: JSON.stringify(data)
  }).then(res => res.json())
    .then(data => {
      if (data.status == 200) {
        const size = data.size
        showSize = document.getElementById("showsize")
        showSize.innerHTML = size.toString()
      }else {
        alert("You must login first")
      }
    })
}

function showCart() {

  const searchKeys = window.location.search
  const urlParams = new URLSearchParams(searchKeys)
  const user = urlParams.get("username")
  const session = urlParams.get("sessionId")
  const data = {username: user, sessionId: session}
  fetch("http://localhost:8080/showcart", {
    method: "POST",
    headers: new Headers({"Content-Type": "application/json"}),
    body: JSON.stringify(data)
  }).then(res => res.json())
    .then(data => {
      console.log(data)
      if (data["status"] == 200) {
        var template = Handlebars.compile(document.querySelector("#template").innerHTML);
        var filled = template(data["cartItems"][0])
        for (let i = 0; i < data["cartItems"].length-1; i++) {
          filled += template(data["cartItems"][i+1])
        }
        document.querySelector("#output").innerHTML = filled;
        totalCost = document.getElementById("totalCost")
        totalCost.innerHTML = "Total cost: " + data["totalCost"] + " €"
      }else {
        alert("Your shopping cart is empty")
      }
    }) 
}