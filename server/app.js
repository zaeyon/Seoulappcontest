const express = require('express');
const app = express();
var mysql = require('mysql');
var dbconfig = require('./config/database.js');
var connection = mysql.createConnection(dbconfig);

// 이메일 유효성 체크 함수
function email_check( email ) {
    
  var regex=/([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;

  return (regex.test(email));

}

app.post('/emailCheck', (req, res) => {
  var inputData;

  req.on('data', (data) => {
    inputData = JSON.parse(data);
  });

  req.on('end', () => {
    console.log("입력된 이메일 : " + 
    inputData.userEmail);

    if(!email_check(inputData.userEmail))
      {
       console.log("잘못된 이메일 형식");
       res.write("emailWrong");
       res.end(); 
      }
    else
    {
      res.write("emailCorrect");
      res.end();
    }
  });
});

app.post('/login', (req, res) => {
  var inputData;
  
  req.on('data', (data) => {
    inputData = JSON.parse(data);
  });

  req.on('end', () => {
   // 로그인
   console.log("로그인시도 이메일 : " + inputData.userEmail);
   console.log("로그인시도 비밀번호 : " + inputData.userPassword);

   connection.query('SELECT * FROM user WHERE email = ?', [inputData.userEmail], function(error, results, fields) {
     if(error) {
       console.log("error ocurred", error);
       res.end();
     }
     else {
       if(results.length > 0) {
         if(results[0].password == inputData.userPassword) {
           console.log("로그인 성공");
           res.write("loginSuccess");
           res.end();
         } 
         else {
           console.log("등록되지 않은 이메일");
           res.write("passwordNotMatch");
           res.end();
         }
       } else {
         console.log("비밀번호 불일치");
         res.write("emailNotExist");
         res.end();
       }
     }
   });  
  });
});

app.post('/nickname', (req, res) => {
  var inputData;
  
  req.on('data', (data) => {
    inputData = JSON.parse(data);
  });

  req.on('end', () => {
   // 닉네임 중복 검사
   console.log("입력된 닉네임 : " + inputData.nickname);
   
   connection.query("SELECT * FROM user WHERE NICKNAME=?", inputData.nickname, function(err, data) {
    if(inputData.nickname === "")
    {
      res.write("nicknameNull");
      res.end();
    }
    else if(data.length > 0)
    {
     console.log("중복된 닉네임 존재");
     res.write("nicknameFail"); 
     res.end();
    } 
    else if(data.length == 0)
    {
      console.log("중복된 닉네임 없음");
      res.write("nicknameSuccess");
      res.end();
    }
   });
  });
})


app.post('/email', (req, res) => {
  var inputData;

  req.on('data', (data) => {
    inputData = JSON.parse(data);
  });

  req.on('end', () => {
    console.log("이메일 중복 검사");
    console.log("입력된 이메일 : " + inputData.email);
    
    connection.query("SELECT * FROM user WHERE EMAIL = ?", inputData.email, function(err, data) {
      
      if(inputData.email === "")
      {
        res.write("emailNull");
        res.end();
      }
      else if(!email_check(inputData.email))
      {
       console.log("잘못된 이메일 형식");
       res.write("emailWrong");
       res.end(); 
      }
      else if(data.length > 0)
      {
        console.log("중복된 이메일 존재");
        res.write("emailFail");
        res.end();
      }
      else if(data.length == 0)
      {
        console.log("중복된 이메일 없음");
        res.write("emailSuccess");
        res.end();
      }
    });
  });
})


app.post('/post', (req, res) => {
   console.log('Signup requesting user information');
   console.log(" ");
   var inputData;

   req.on('data', (data) => {
     inputData = JSON.parse(data);
   });

   req.on('end', () => {

     // 회원가입 요청 회원 정보 콘솔에 출력
     console.log("nickname : "+inputData.nickname);
     console.log("email : " + inputData.email);
     console.log("password : " + inputData.password);
     console.log("passwordConfirm : " + inputData.passwordConfirm);
     console.log(" ");

     var user = {
       "nickname":inputData.nickname,
       "email":inputData.email,
       "password":inputData.password
     }

     connection.query('INSERT INTO user SET ?', user, function(error, results, fields) {
       if(error) {
         console.log("error ocurred", error);
         res.send({
           "code":400,
           "failed":"error ocurred"
         })
         res.end();
       } else {
         console.log("회원가입 완료")
         console.log(" ");
       }
       res.end();
     })

                
   });

//   res.write("OK!");
//   res.end();
});

app.listen(3000, () => {
  console.log('Example app listening on port 3000!');
});

