const { Console } = require('console')
const express = require('express')
const path = require('path')
const app = express()
const port = 8080

var users = [
    {username: "koularis", 
    password: "qwerty"},
    {username: "vousnakis", 
    password: "123456"}
]

var sessions = []

var shoppingCarts = {}

function checkLogin(credentials, sessions) {
    for (let i=0;i<sessions.length;i++){
        if (sessions[i].username == credentials[0]) {
            return sessions[i].sessionId == credentials[1]
        }
    }
    return false
}

app.listen(port)

/* 
    Serve static content from directory "public",
    it will be accessible under path /, 
    e.g. http://localhost:8080/index.html
*/
app.use(express.static('public'))

// parse url-encoded content from body
app.use(express.urlencoded({ extended: false }))

// parse application/json content from body
app.use(express.json())

// serve index.html as content root
app.get('/', function(req, res){

    var options = {
        root: path.join(__dirname, 'public')
    }

    res.sendFile('index.html', options, function(err){
        console.log(err)
    })
})

app.post("/login", function(req, res) {
    const user = req.body.username
    const passw = req.body.password

    var flag = false
    for (let i=0;i<users.length;i++) {
        if (user == users[i].username && passw == users[i].password){
            console.log("login successful")
            flag = true
            const { v4: uuidv4 } = require('uuid');
            const id = uuidv4()
            for (let i=0;i<sessions.length;i++) {
                if (user == sessions[i].username) {
                    sessions.pop(sessions[i])
                    delete shoppingCarts[user]
                }
            }
            sessions.push({username: user, sessionId: id})
            res.send({"status": 200, "sessionId": id})
            break
        }
    }
    if (!flag) {
        console.log("login unsuccessful")
        res.send({"status": 401})
    }
})

app.post("/addtocart", function(req, res) {
    const credentials = [req.body.username, req.body.sessionId]
    flag = checkLogin(credentials, sessions)
    if (flag) {
        flag2 = false
        res.send({"status": 200})
        let product = req.body
        delete product.password
        delete product.sessionId
        delete product.username
        if (credentials[0] in shoppingCarts) {
            for (let i=0;i<shoppingCarts[credentials[0]].length;i++) {
                if (shoppingCarts[credentials[0]][i].id == product.id) {
                    flag2 = true
                    shoppingCarts[credentials[0]][i].quantity += 1
                }
            }
            if (!flag2) {
                product.quantity = 1
                shoppingCarts[credentials[0]].push(product)
            }
        }else {
            product.quantity = 1
            shoppingCarts[credentials[0]] = [product]
        }
    }else {
        res.send({"status": 401})
    }
})

app.post("/showcartsize", function(req, res) {
    const credentials = [req.body.username, req.body.sessionId]
    flag = checkLogin(credentials, sessions)
    if (flag) {
        var size = 0
        if (credentials[0] in shoppingCarts) {
            for (let i=0;i<shoppingCarts[credentials[0]].length;i++) {
                size += shoppingCarts[credentials[0]][i].quantity
            }
        }else {
            size = 0
        }
        res.send({"status": 200, "size": size})
    }else {
        res.send({"status": 401})
    }
})

app.post("/showcart", function(req, res) {
    const credentials = [req.body.username, req.body.sessionId]
    userShoppingCart = shoppingCarts[credentials[0]]
    if (typeof userShoppingCart === "undefined") {
        res.send({"status": 204})
    }else {
        finalCart = {"cartItems" : [], "totalCost": 0, "status": 200}
        var finalCost = 0
        for (let i=0;i<userShoppingCart.length;i++) {
            finalCart["cartItems"].push({"title": userShoppingCart[i].title, "cost": userShoppingCart[i].cost, "quantity": userShoppingCart[i].quantity})
            finalCost += userShoppingCart[i].cost * userShoppingCart[i].quantity
        }
        finalCart["totalCost"] = finalCost
        res.send(finalCart)
        console.log(finalCart)
    }
})