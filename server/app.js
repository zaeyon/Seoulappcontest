const express = require('express');
const app = express();
var connect = require('connect');

let jwt = require("jsonwebtoken");
let secretObj = require("./config/jwt.js");

var accessing_user_email = "";

app.use(express.static(__dirname + '/public'));
//app.use(connect.cookieParser());
app.use(connect.logger('dev'));

//app.use(connect.json());
app.use(connect.urlencoded());

require('./routes/routes.js')(app);

app.listen(3000, () => {
  console.log('Example app listening on port 3000!');
});
