const express = require('express');
const app = express();
var mysql = require('mysql');
var dbconfig = require('./config/database.js');
var connection = mysql.createConnection(dbconfig);
var bodyParser = require('body-parser');


let jwt = require("jsonwebtoken");
let secretObj = require("./config/jwt.js");

var accessing_user_email = "";

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
      console.log("올바른 이메일 형식");
      res.write("emailCorrect");
      res.end();
    }
  });
});

app.post('/login', (req, res) => {

  let token;

  var inputData;
  
  req.on('data', (data) => {
    inputData = JSON.parse(data);
  });

  req.on('end', () => {

    token = jwt.sign({
      email : inputData.userEmail  // 토큰의 내용
    },
    secretObj.secret,   // 비밀 키
    {
      expiresIn : '5m'  // 유효 시간은 5분
    });

   // 로그인
   console.log("로그인시도 이메일 : " + inputData.userEmail);
   console.log("로그인시도 비밀번호 : " + inputData.userPassword);

   connection.query('SELECT * FROM user WHERE email = ?', [inputData.userEmail], function(error, results, fields) {
     if(error) {
       console.log("error ocurred", error);
       res.end();
     }
     else 
     {
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
         res.end(); v
       } else {
         console.log("회원가입 완료")
         console.log(" ");
         res.end();
       }
     })

                
   });

//   res.write("OK!");
//   res.end();
});

var shopNumberCount;

// 등록된 매장수 확인
app.post('/shopNumber', (req,res) => {

  console.log("등록된 매장개수 확인중");
  connection.query("SELECT COUNT(*) as cnt from shop", function(error, result, cnt) {
    
    console.log("등록된 매장수 : " + result[0].cnt);
    shopNumberCount = result[0].cnt;
    res.write(String(result[0].cnt));
    res.end();

  });
})


// 매장이름
app.post('/getShopName', (req,res) => {
  var allShopName = "";

  console.log("매장이름 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopName = results[0].shopName;
      }
      else
      {
        allShopName = allShopName + "/" + results[j].shopName;
      }
    };
    };

    console.log("allShopName : " + allShopName);
    res.write(String(allShopName));
    res.end();
  });

});

app.post('/getShopProfile', (req,res) => {
  
  var allShopProfile = "";

  console.log("매장프로필사진 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopProfile =results[0].shopProfileImage;
      }
      else
      {
        allShopProfile = allShopProfile + "/" + results[j].shopProfileImage;
      }
    };
    };

    console.log("allShopProfile : " + allShopProfile);
    res.write(String(allShopProfile));
    res.end();
  });

});

app.post('/getShopReq1', (req,res) => {
  
  var allShopInfo = "";

  console.log("매장대표사진 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopInfo =results[0].shopRepresentation1;
      }
      else
      {
        allShopInfo = allShopInfo + "/" + results[j].shopRepresentation1;
      }
    };
    };

    console.log("allShopInfo : " + allShopInfo);
    res.write(String(allShopInfo));
    res.end();
  });

});

app.post('/getShopReq2', (req,res) => {
  
  var allShopInfo = "";

  console.log("매장대표사진 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopInfo =results[0].shopRepresentation2;
      }
      else
      {
        allShopInfo = allShopInfo + "/" + results[j].shopRepresentation2;
      }
    };
    };

    console.log("allShopInfo : " + allShopInfo);
    res.write(String(allShopInfo));
    res.end();
  });

});

app.post('/getShopReq3', (req,res) => {
  
  var allShopInfo = "";

  console.log("매장대표사진 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopInfo =results[0].shopRepresentation3;
      }
      else
      {
        allShopInfo = allShopInfo + "/" + results[j].shopRepresentation3;
      }
    };
    };

    console.log("allShopInfo : " + allShopInfo);
    res.write(String(allShopInfo));
    res.end();
  });

});

app.post('/getShopFloor', (req,res) => {
  
  var allShopFloor = "";

  console.log("매장 층 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopFloor =results[0].shopFloor;
      }
      else
      {
        allShopFloor = allShopFloor + "/" + results[j].shopFloor;
      }
    };
    };

    console.log("allShopFloor : " + allShopFloor);
    
    res.write(String(allShopFloor));
    res.end();
  });
});

app.post('/getShopRocation', (req,res) => {
  
  var allShopInfo = "";

  console.log("매장대표사진 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopInfo =results[0].shopRocation;
      }
      else
      {
        allShopInfo = allShopInfo + "/" + results[j].shopRocation;
      }
    };
    };

    console.log("allShopInfo : " + allShopInfo);
    res.write(String(allShopInfo));
    res.end();
  });
});
app.post('/getShopBuilding', (req,res) => {
  
  var allShopInfo = "";

  console.log("매장건물이름 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopInfo =results[0].shopBuilding;
      }
      else
      {
        allShopInfo = allShopInfo + "/" + results[j].shopBuilding;
      }
    };
    };

    console.log("allShopInfo : " + allShopInfo);
    res.write(String(allShopInfo));
    res.end();
  });
});

app.post('/getShopCategory', (req,res) => {
  
  var allShopCategory = "";

  console.log("매장 품목 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopCategory =results[0].shopCategory;
      }
      else
      {
        allShopCategory = allShopCategory + "/" + results[j].shopCategory;
      }
    };
    };

    console.log("allShopCategory : " + allShopCategory);
    
    res.write(String(allShopCategory));
    res.end();
  });
});

app.post('/getShopStyle', (req,res) => {
  
  var allShopStyle = "";

  console.log("매장 스타일 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopStyle =results[0].shopStyle;
      }
      else
      {
        allShopStyle = allShopStyle + "/" + results[j].shopStyle;
      }
    };
    };

    console.log("allShopStyle : " + allShopStyle);
    
    res.write(String(allShopStyle));
    res.end();
  });
});

app.post('/getShopIntro', (req,res) => {
  
  var allShopIntro = "";

  console.log("매장 소개 얻기");
  connection.query("SELECT * FROM shop", function(error, results) {
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < shopNumberCount; j++)
    {
      if(j == 0)
      {
        allShopIntro =results[0].shopIntro;
      }
      else
      {
        allShopIntro = allShopIntro + "/" + results[j].shopIntro;
      }
    };
    };

    console.log("allShopIntro : " + allShopIntro);
    res.write(String(allShopIntro));
    res.end();
  });
});


app.post('/getShopProduction', (req, res) => {
  var inputData;
  var shopProduction = "";

  req.on('data', (data) => {
    inputData = JSON.parse(data);
  });

  req.on('end', () => {
    console.log("매장이름 : " + 
    inputData.shopName);

    connection.query("select shopProduction from shop where shopName = ?", inputData.shopName, function(error, results) {
      
      if(error)
      {
        console.log("error 발생");
        console.log(error);
        res.end();
      }
      else if(results[0].shopProduction == null)
      {
        console.log("등록된 상품없음");
        res.end();
      }
      else
      {
        shopProduction = results[0].shopProduction;
        console.log("등록된 상품 목록 : " + shopProduction);
        res.write(String(shopProduction));
        res.end();
      }
    });
  
});
});

app.post('/getProductionInfo', (req, res) => {
  var inputData;
  var productionInfo = "";

  req.on('data', (data) => {
    inputData = JSON.parse(data);
  });

  req.on('end', () => {

    connection.query("select productionName from production where productionURL = ?", inputData.productionURL, function(error, results) {
      
      if(error)
      {
        console.log("error 발생");
        console.log(error);
        res.end();
      }
      else if(results[0].productionName == null)
      {
        console.log("등록된 상품없음");
        res.end();
      }
      else
      {
        productionInfo = results[0] .productionName + "|";
      }
    });

      connection.query("select productionSize from production where productionURL = ?",inputData.productionURL, function(error, results) {
        if(error)
        {
          console.log("error 발생");
          console.log(error);
          res.end();
        }
        else if(results[0].productionSize == null)
        {
          console.log("등록된 상품없음");
          res.end();
        }
        else{
          productionInfo =productionInfo + results[0].productionSize + "|";
        }
      });

      connection.query("select productionPrice from production where productionURL = ?",inputData.productionURL, function(error, results) {
        if(error)
        {
          console.log("error 발생");
          console.log(error);
          res.end();
        }
        else if(results[0].productionPrice == null)
        {
          console.log("등록된 상품없음");
          res.end();
        }
        else{
          productionInfo = productionInfo + results[0].productionPrice + "|";
        }
      });

      connection.query("select productionIntro from production where productionURL = ?", inputData.productionURL, function(error, results) {
        if(error)
        {
          console.log("error 발생");
          console.log(error);
          res.end();
        }
        else if(results[0].productionIntro == null)
        {
          console.log("등록된 상품없음");
          res.end();
        }
        else 
        {
          productionInfo = productionInfo + results[0].productionIntro;
        }

        console.log(productionInfo);
        res.write(String(productionInfo));
        res.end();
    });
});
});

app.post('/aa', (req,res) => {

  var inputData;
  var searchInfo = "";

  req.on('data', (data) => {
    inputData = JSON.parse(data);
  });

  req.on('end', () => {
   
  console.log("매장 검색중");
    connection.query("select shopName, shopProfileImage  from shop where shopName like concat ('%', ?, '%')", inputData.searchShopName, function(error, results) {
      if(error){
        console.log("error 발생 : " + error);
      }
      else {
      for(var i = 0; i < results.length; i++)
      {
        if(i == 0 )
        {
          searchInfo = results[0].shopName + "|" + results[0].shopProfileImage;
        }
        else {
        searchInfo = searchInfo + "|" + results[i].shopName + "|" + results[i].shopProfileImage;
        }
      } 
    }
    console.log("searchInfo : " + searchInfo);
 
    res.write(String(searchInfo));
    res.end();
   });
  });
});


/*
   connection.query("select shopName from shop where shopName like concat ('%', ?, '%')",
   inputData.searchShopName, function(error, results) {
     if(error){
       console.log("error 발생 : " + error);
     }
     else if(results[0])
     {
       searchInfo = searchInfo + results[0].shopName;
       console.log("searchInfo : " + searchInfo);
       res.write(String(searchInfo));
      }
     else if(!results[0])
     {
      console.log("검색된 매장이름 존재X");
      res.write("noResult");
    }
     res.end();
   });
  });
});
*/


app.post('/getUserProfile', (req,res) => {
  var allUserProfileImage = "";
  var UserContent="";
  var UserId ="";
  var UserImage="";
  var ReviewStoreName="";
  var Like="";
  var Comment="";
  var CommentUser_Id=""
  var allView="";

  console.log("프로필 사진 얻기");
  connection.query("SELECT * FROM review", function(error, results) { //all pulled.
    if(error)
    {
      console.log("에러");
    }
    else{
    for(var j = 0; j < results.length; j++)
    {
      if(j == 0)
      {


    
        UserImage = results[0].User_Image;
        UserId = results[0].User_Id;
        UserContent = results[0].User_Content;
        ReviewStoreName = results[0].Review_StoreName;
        Like = results[0].Like;
        Comment=results[0].Comment;
        CommentUser_Id = results[0].Comment_User_Id;
        allView =  UserImage + "/" + UserId + "/" + UserContent+ "/"+ReviewStoreName+"/"+Like+"/"+Comment+"/"+CommentUser_Id;
      }
      else
      {
        console.log("allImage : " + allUserProfileImage);
      
        UserImage = UserImage+"|"+results[j].User_Image;
        UserId = UserId+"|"+results[j].User_Id;
        UserContent = UserContent+"|"+results[j].User_Content;
        ReviewStoreName = ReviewStoreName+"|"+results[j].Review_StoreName;
      
        Like = Like+"|"+results[j].Like;
        Comment=Comment + "|" + results[j].Comment;

        CommentUser_Id = CommentUser_Id + "|" + results[j].CommentUser_Id;

        console.log("CommentUser_id : " + CommentUser_Id + results[0].Comment_User_Id);

        allView = UserImage + "/" + UserId + "/" + UserContent + "/" +ReviewStoreName+ "/" +Like+"/"+Comment+"/"+CommentUser_Id;
        }
      };
    };
    console.log("allView : " + allView);

    res.write(allView); //그냥 모든 걸 더해서 하나로 보낸 후에 나누면 안 됨?
    res.end();
  });
});

app.post('/getCommentInfo',(req,res)=>{
    var allUserProfileImage = "";
    var Comment="";
    var CommentUser_Id=""
    var allView="";

    console.log("프로필 사진 얻기");
    connection.query("SELECT * FROM review", function(error, results) { //all pulled.
    console.log(results);
    if(error)
    {
      console.log("에러");
    }
    else{
        for(var j = 0; j < results.length; j++)
        {
            if(j == 0)
            {
                console.log("allImage : " + allUserProfileImg);
                Comment=results[0].Comment;
                CommentUser_Id = results[0].CommentUser_Id;
                allView = Comment+"/"+CommentUser_Id;
            }
            else
            {
                console.log("allImage : " + allUserProfileImage);
                Comment=Comment + "|" + results[j].Comment;
                CommentUser_Id = CommentUser_Id + "|" + results[j].CommentUser_Id;
                allView = Comment+"/"+CommentUser_Id;
            }
        };
    };
    console.log("allImage : " + allUserProfileImage);
    res.write(allView); //그냥 모든 걸 더해서 하나로 보낸 후에 나누면 안 됨?
    res.end();
    });
});

app.post('/newShopData1', (req, res) => {
    console.log("post /newShopData1");
    var inputData;
    var params;

    req.on('data', (data) => {
        inputData = JSON.parse(data);
        params = [inputData.email, inputData.name, inputData.building, inputData.floor, inputData.location, inputData.style, inputData.category, inputData.introduction];
    });

    req.on('end', () => {
        connection.query("INSERT INTO shop (userEmail, shopName, shopBuilding, shopFloor, shopRocation, shopStyle, shopCategory, shopIntro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", params, function(error, result) {
            if (error) {
                console.log("newShopData1 " + error);
            } else {
                console.log("매장 정보\n이메일 : " + inputData.email + "\n매장 이름 : " + inputData.name + "\n" + inputData.building + " " + inputData.floor + "층 " + inputData.location + "\n" + inputData.style + " " + inputData.category + "\n" + inputData.introduction);
            }

            res.end();
        })

        connection.query("UPDATE user SET shop_being = 1 WHERE email = ?", inputData.email, function(error, result) {
            if (error) {
                console.log("newShopData1 " + error);
            }

            res.end();
        })
    })
})

app.post('/newShopData2', (req, res) => {
    console.log("post /newShopData2");
    var inputData;
    var params;

    req.on('data', (data) => {
    });

    req.on('end', () => {
        console.log("상점 수 : " + shopNumberCount);
        res.end(String(shopNumberCount));
    })
})

app.post('/newShopData3', (req, res) => {
    console.log("post /newShopData3");
    var inputData;
    var params;

    req.on('data', (data) => {
        inputData = JSON.parse(data);
        params = [inputData.profileImage, inputData.rep1, inputData.rep2, inputData.rep3, inputData.shopID];
    });

    req.on('end', () => {
        connection.query("UPDATE shop SET shopProfileImage = ?, shopRepresentation1 = ?, shopRepresentation2 = ?, shopRepresentation3 = ? WHERE ID = ?", params, function(error, result) {
            if (error) {
                console.log("newShopData3 " + error);
            } else {
                console.log("매장 ID : " + inputData.shopID + "\n매장 이미지 : " + inputData.profileImage + "\nReq1 : " + inputData.rep1 + "\nReq2 : " + inputData.rep2 + "\nReq3 : " + inputData.rep3);
            }

            res.end();
        })
    })
})

app.post('/getUserInfo', (req, res) => {
  console.log("post /getUserInfo");
  var inputData;
  var userProfile = "";

  req.on('data', (data) => {
      inputData = JSON.parse(data);
      console.log("request from myPage");
  });

  req.on('end', () => {
    connection.query("SELECT * FROM user where email = ?", inputData.email, function(error, result) {
      if (error) {
          console.log("에러" + error);
      } else if(result[0]){
        console.log("닉네임 : " + result[0].nickname + ", 이미지 : " + result[0].profile_image + ", 매장 여부 : " + result[0].shop_being);
        userProfile = result[0].nickname + "|" + result[0].profile_image + "|" + result[0].shop_being;
      }

      console.log("userProfile : " + userProfile);
      res.write(String(userProfile));
      res.end();
    }); 
  });
});

app.post('/myProfile', (req, res) => {
    console.log("post /myProfile");
    var inputData;
    var userData = "";

    req.on('data', (data) => {
        inputData = JSON.parse(data);
        console.log("request from myProfile");
    });

    req.on('end', () => {
        connection.query("SELECT * FROM user WHERE email = ?", inputData.email, function(error, result) {
            if (error) {
                console.log("현재 닉네임 불러오기 에러");
            } else if (result[0]) {
                console.log("기존 프로필\n닉네임 : " + result[0].nickname + ", 이미지 : " + result[0].profile_image);
                userData = result[0].ID + "|" + result[0].nickname + "|" + result[0].profile_image;
            }
            console.log("userData : " + userData);
            res.write(String(userData));
            res.end();
        });
    });
});

app.post('/setMyProfile', (req, res) => {
    console.log("post /setMyProfile");
    var inputData;
    var params1;
    var params2;

    req.on('data', (data) => {
        inputData = JSON.parse(data);
        params1 = [inputData.fileName, inputData.email];
        params2 = [inputData.newNickname, inputData.email];
        console.log("request from myProfile");
    });

    req.on('end', () => {
        connection.query("UPDATE user SET profile_image = ? WHERE email = ?", params1, function(error, result) {
            if (error) {
                console.log("파일 이름 저장 에러");
            } else {
                console.log("파일 이름 저장 완료")
            }

            res.write("file upload finish");
            res.end();
        });

        connection.query("UPDATE user SET nickname = ? WHERE email = ?", params2, function(error, result) {
            if (error) {
                console.log("닉네임 변경 에러");
            } else {
                console.log("닉네임 변경 완료");
            }
            res.end();
        })
    });
});

app.listen(3000, () => {
  console.log('Example app listening on port 3000!');
});