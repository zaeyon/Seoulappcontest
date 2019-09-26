var mysql = require('mysql');
var dbconfig = require('../config/database.js');
var connection = mysql.createConnection(dbconfig);
var bodyParser = require('body-parser');

var fs = require('fs');

// 이메일 유효성 체크 함수
function email_check( email ) {
    
    var regex=/([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
  
    return (regex.test(email));
  
  }

module.exports = function(app) {


app.get('/',function(req,res){
	res.end("Node-File-Upload");

});
app.post('/upload', function(req, res) {
	console.log(req.files.image.originalFilename);
	console.log(req.files.image.path);
		fs.readFile(req.files.image.path, function (err, data){
		var dirname = "/home/rajamalw/Node/file-upload";
		var newPath = dirname + "/uploads/" + 	req.files.image.originalFilename;
		fs.writeFile(newPath, data, function (err) {
		if(err){
		res.json({'response':"Error"});
		}else {
		res.json({'response':"Saved"});
}
});
});
});


app.get('/uploads/:file', function (req, res){
		file = req.params.file;
		var dirname = "/home/rajamalw/Node/file-upload";
		var img = fs.readFileSync(dirname + "/uploads/" + file);
		res.writeHead(200, {'Content-Type': 'image/jpg' });
		res.end(img, 'binary');

});


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
  });
  
  
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
          allShopProfile =results[0].shopProfileImageUrl;
        }
        else
        {
          allShopProfile = allShopProfile + "!!!" + results[j].shopProfileImageUrl;
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
          allShopInfo =results[0].shopRep1ImageUrl;
        }
        else
        {
          allShopInfo = allShopInfo + "!!!" + results[j].shopRep1ImageUrl;
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
          allShopInfo =results[0].shopRep2ImageUrl;
        }
        else
        {
          allShopInfo = allShopInfo + "!!!" + results[j].shopRep2ImageUrl;
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
          allShopInfo =results[0].shopRep3ImageUrl;
        }
        else
        {
          allShopInfo = allShopInfo + "!!!" + results[j].shopRep3ImageUrl;
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
    var searchShopProfile = "";
    var allSearchInfo = "";
  
    req.on('data', (data) => {
      inputData = JSON.parse(data);
    });
  
    req.on('end', () => {
     
    console.log("매장 검색중");
      connection.query("select shopName, shopProfileImage, shopBuilding, shopCategory from shop where shopName like concat ('%', ?, '%')",inputData.searchShopName, function(error, results) {
        if(error){
          console.log("error 발생 : " + error);
        }
        else {
        for(var i = 0; i < results.length; i++)
        {
          if(i == 0 )
          {
            searchShopProfile = results[0].shopProfileImage;
            searchInfo = results[0].shopName + "|" + results[0].shopBuilding + "|" + results[0].shopCategory;
          }
          else {
              searchShopProfile = searchShopProfile + "$" + results[i].shopProfileImage;

              
            searchInfo = searchInfo + "|"+ results[i].shopName + "|" + results[i].shopBuilding + "|" + results[i].shopCategory;
          }
        } 
      }
      allSearchInfo = searchShopProfile + "&&" + searchInfo;

      console.log("searchShopProfile + searchInfo : " + allSearchInfo);
      
      res.write(String(allSearchInfo)); 
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
  
    var Like ="";
    var UserContent="";
    var UserId ="";
    var UserImage="";
    var ReviewStoreName="";
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
          allView =Like + "/"+ UserImage + "/" + UserId + "/" + UserContent+ "/"+ReviewStoreName+"/"+Comment+"/"+CommentUser_Id;
        }
        else
        {
          UserImage = UserImage+"|"+results[j].User_Image;
          UserId = UserId+"|"+results[j].User_Id;
          UserContent = UserContent+"|"+results[j].User_Content;
          ReviewStoreName = ReviewStoreName+"|"+results[j].Review_StoreName;
          Like = Like+"|"+results[j].Like;
          Comment=Comment + "|" + results[j].Comment;
  
          CommentUser_Id = CommentUser_Id + "|" + results[j].Comment_User_Id;
  
          console.log("CommentUser_id : " + CommentUser_Id + results[0].Comment_User_Id);
  
          allView = Like + "/" + UserImage + "/" + UserId + "/" + UserContent + "/" +ReviewStoreName+ "/"+Comment+"/"+CommentUser_Id;
          }
        };
      };
  
      console.log("allView : " + allView);
  
      res.write(allView); //그냥 모든 걸 더해서 하나로 보낸 후에 나누면 안 됨?
      res.end();
    });
  });
  
  app.post('/getCommentInfo',(req,res)=>{
  
    var Comment="";
    var CommentUser_Id=""
    var Distinguish=0;
    var allView="";
  
    console.log("댓글");
    connection.query("SELECT * FROM commenttable", function(error, results) { //all pulled.
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
  
          Comment=results[0].Comment;
          CommentUser_Id = results[0].CommentUser_Id;
          Distinguish = results[0].Distinguish_number;
          allView = Comment+"/"+CommentUser_Id+"/"+Distinguish;
        }
        else
        {
          Comment=Comment + "|" + results[j].Comment;
          CommentUser_Id = CommentUser_Id + "|" + results[j].CommentUser_Id;
          Distinguish = Distinguish+"|"+results[j].Distinguish_number;
          allView = Comment+"/"+CommentUser_Id+"/"+Distinguish;
          }
        };
      };
      console.log(allView);
      console.log("데이터 잘 담음 지금 보내려고 하는 중...");
      res.write(allView); 
      res.end();
  });
  });
  
  app.post('/newShopData0', (req, res) => {
      console.log("post /newShopData0");
      var inputData;
  
      req.on('data', (data) => {
          inputData = JSON.parse(data);
  
      });
  
      req.on('end', () => {
          console.log("newShopData0의 email : " + String(inputData.email));
          connection.query("SELECT ID FROM user WHERE email = ?", inputData.email, function(error, result) {
              if (error) {
                  console.log("newShopData0 " + error);
              } else {
                  console.log("shopID : " + result[0].ID);
              }
  
              res.write(String(result[0].ID));
              res.end();
          })
      })
  })
  
  app.post('/newShopData1', (req, res) => {
      console.log("post /newShopData1");
      var inputData;
      var params;
  
      req.on('data', (data) => {
          inputData = JSON.parse(data);
          params = [
              inputData.email, inputData.name, inputData.building, inputData.floor, inputData.location, inputData.style, inputData.category, inputData.introduction, inputData.profileImg, inputData.repImg1, inputData.repImg2, inputData.repImg3,
              inputData.profileImageUrl, inputData.repImageUrl1, inputData.repImageUrl2,
              inputData.repImageUrl3,
            ]
          ;
      });
  
      req.on('end', () => {
          connection.query("INSERT INTO shop (userEmail, shopName, shopBuilding, shopFloor, shopRocation, shopStyle, shopCategory, shopIntro, shopProfileImage, shopRepresentation1, shopRepresentation2, shopRepresentation3, shopProfileImageUrl, shopRep1ImageUrl, shopRep2ImageUrl, shopRep3ImageUrl ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,?)", params, function(error, result) {
              if (error) {
                  console.log("newShopData1 " + error);
              } else {
                  console.log("매장 정보\n이메일 : " + inputData.email + "\n매장 이름 : " + inputData.name + "\n" + inputData.building + " " + inputData.floor + "층 " + inputData.location + "\n" + inputData.style + " " + inputData.category + "\n" + inputData.introduction);
                  console.log("매장 프로필 이미지 : " + inputData.profileImg + ", 이미지1 : " + inputData.repImg1 + ", 이미지2 : " + inputData.repImg2 + ", 이미지3 : " + inputData.repImg3);
              }
          })
  
          connection.query("UPDATE user SET shop_being = 1 WHERE email = ?", inputData.email, function(error, result) {
              if (error) {
                  console.log("newShopData1 " + error);
              }
          });
          res.end();
      });
  });
  
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
                  console.log("현재 닉네임 불러오기 에러" + error);
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
          });
      });
  });
  
  app.post('/setUploadImg',(req,res)=>{ //review Img 업로드
    console.log("post /setUploadImg");
    var inputData; //JSONTask에서 준 데이터
    var params;
  
    req.on('data', (data) => {
        inputData = JSON.parse(data);
        params = [inputData.UserImg, inputData.email,inputData.UserContent, inputData.StoreName];
        console.log("request from setUploadImg");
    });
  
    req.on('end', () => {
        connection.query("INSERT INTO review SET User_Img = ?, User_Id=? ,User_Content =?, Review_StoreName =? WHERE email = ?", params, function(error, result) {
            if (error) {
                console.log("리뷰 파일 이름 저장 에러");
            } else {
                console.log("리뷰 파일 이름 저장 완료")
            }
            console.log("리뷰 파일 저장");
            res.write("Review file upload finish");
            res.end();
        });
    });
  });
  
  app.post('/getReviewCount', (req, res)=>{
    console.log("access getReviewCount");
    var UserId;
    var number;
  
    connection.query("SELECT * FROM review", (err,data1)=>{
                       UserId = data1[0].User_Id;
                        connection.query("SELECT * FROM review WHERE User_Id =?", [UserId], (err,data2)=>{
                            number = data2[0].Review_Number;
                            res.write(number);
                            res.end();
                });
            })
  });
  
  app.post('/getQnAInfo', (req, res) => {
    console.log("/post getQnAInfo");
    var inputData;
    var QnAInfo = "";
    var QnACount;
  
    req.on('data', (data) => {
      inputData = JSON.parse(data);
    });
  
    req.on('end', () => {
  
      // QnA 불러옴
  
      console.log("QnA불러오는 매장이름 : " + inputData.shopName);
  
      connection.query("SELECT COUNT(*) as cnt from qna where shopName = ?",inputData.shopName, function(error, results, fields) {
        if(error)
        {
          console.log("error : " + error);
        }
        else {
          console.log("매장에 등록된 리뷰 개수 : " + results[0].cnt);
        //QnAInfo = results[0].cnt;
        QnACount = results[0].cnt;
        }
      });
      connection.query('SELECT * from qna where shopName = ?', inputData.shopName, function(error, results, fields) {
        if(error) {
          console.log("error ocurred", error);
          res.send({
            "code":400, 
            "failed":"error ocurred"
          })
          res.end();
        } else if(results.length == 0)
        {
          console.log("해당 매장의 QnA 존재하지 않음");
        }
        else
        {
          console.log("해장 매장의 QnA 존재함"); 
          for(var i = 0; i < QnACount; i++)
          {
            if(i == QnACount-1)
          {
          QnAInfo = QnAInfo + results[i].hostNickname + "|" + results[i].shopName +"|" + results[i].size+"|"+ results[i].answer + "|" +results[i].content +"|"+ results[i].title + "|" + results[i].production + "|" + results[i].userNickname + "|" + results[i].question + "|" + results[i].answerExis;
          }
          else if(i == 0) 
          {
          QnAInfo = results[i].hostNickname + "|" + results[i].shopName +"|" + results[i].size +"|" + results[i].answer + "|"+results[i].content +"|" + results[i].title + "|" + results[i].production + "|" + results[i].userNickname + "|" + results[i].question + "|" + results[i].answerExis + "&&&";
        } else
        {
          QnAInfo = QnAInfo + results[i].hostNickname + "|" + results[i].shopName +"|" + results[i].size + "|" + results[i].answer + "|" +results[i].content + "|"+ results[i].title + "|" + results[i].production + "|" + results[i].userNickname + "|" + results[i].question + "|" + results[i].answerExis + "&&&";
        }
      }
      }
        
        console.log("QnAInfo : " + QnAInfo);
        res.write(String(QnAInfo));
        res.end();
      }); 
    });
  
  });
  
  app.post('/getUserProfile', (req,res) => {
  
    var Like = 0;
    var UserContent="";
    var UserId ="";
    var UserImage="";
    var ReviewStoreName="";
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
          allView = UserImage + "/" + UserId + "/" + UserContent+ "/"+ReviewStoreName+"/"+Comment+"/"+CommentUser_Id;
        }
        else
        {
  
          UserImage = UserImage+"|"+results[j].User_Image;
          UserId = UserId+"|"+results[j].User_Id;
          UserContent = UserContent+"|"+results[j].User_Content;
          ReviewStoreName = ReviewStoreName+"|"+results[j].Review_StoreName;
          Like = Like+"|"+results[j].Like;
      
  
          console.log("CommentUser_id : " + CommentUser_Id + results[0].Comment_User_Id);
  
          allView = Like + "/" + UserImage + "/" + UserId + "/" + UserContent + "/" +ReviewStoreName+ "/"+Comment+"/"+CommentUser_Id;
          }
        };
      };
      console.log("allView : " + allView);
  
      res.write(allView); //그냥 모든 걸 더해서 하나로 보낸 후에 나누면 안 됨?
      res.end();
    });
  });
  
  app.post('/InsertQnAInfo', (req,res) => {
  
    var InputData;
    var hostNickname;
  
  
    req.on('data', (data) => {
      InputData = JSON.parse(data);
  
      console.log(InputData.RegQnAShopName);
    });
  
  
    req.on("end", () => {
  
      connection.query("SELECT hostNickname from shop where shopName = ?", InputData.RegQnAShopName, function(error, results) {
        if(error){
          console.log("error : " + error);
        }
        else if(results[0].hostNickname == null)
        {
          console.log("해당하는 매장이 존재하지 않습니다.");
        }
        else{
          console.log("해당하는 매장 사장 닉네임 results[0].hostNickname : " + results[0].hostNickname);
          hostNickname = results[0].hostNickname;
        }
        });
  
        var QnAInfo = {
          "hostNickname" : hostNickname,
          "shopName" : InputData.RegQnAShopName,
          "answer" : "",
          "content" : InputData.RegQnAContent,
          "title" : InputData.RegQnATitle,
          "production" : InputData.RegQnAProductionName,
          "userNickname" : InputData.RegQnAUserNickname,
          "type" : InputData.RegQnAType,
          "answerExis" : 0
        }
  
        connection.query("INSERT INTO qna SET ?", QnAInfo, function(error, results, fields) {
  
          if(error) {
            console.log("error : " + error);
            res.send({
              "code": 400,
              "failed":"error ocurred"
            });
            res.end();
          } else {
            console.log("문의 등록 완료");
            res.write("success");
            res.end();
          }
  
        });
  
  
  
  
  
      });
  });
  
  app.post('/insertFavoriteShop', (req,res) => {
  
    var InputData ;
    var params;
    
    req.on('data', (data) => {
      InputData = JSON.parse(data);
      
  
      params = {
        "shopName" : InputData.shopName, 
        "userEmail" : InputData.userEmail
      };
    });
  
    req.on('end', () => {
      connection.query("INSERT INTO favorite_shop SET ?",params, function(error, results, fields){
        if(error)
        {
          console.log("error : " + error);
        }
        else {
          console.log("즐겨찾기 추가 성공")
          console.log("매장 이름 : " + InputData.shopName);
          console.log("회원 이메일 : " + InputData.userEmail);
        }
       res.write("insert favorite shop success");
       res.end();   
      });
    });
  });
  
  
  app.post('/deleteFavoriteShop', (req,res)=> {
    var InputData;
    var params;
  
    req.on('data', (data) => {
      InputData = JSON.parse(data);
  
      console.log("InputData.shopName : " + InputData.shopName);
    });
  
      req.on('end', () => {
    connection.query("DELETE from favorite_shop where shopName = ? AND userEmail = ?" ,[InputData.shopName, InputData.userEmail], function(error, results) {
      if(error){
        console.log("error: " + error) 
      } else {
        console.log("즐겨찾기 삭제 완료");
        console.log("매장 이름 : " + InputData.shopName);
        console.log("회원 이메일 : " + InputData.userEmail);
      }
      res.write("favorite shop delete success");
      res.end();
    });
    });
  });
  
  app.post("/insertFavoriteProduction", (req, res)=> {
    var InputData;
    var shopName;
    var params;
    req.on('data', (data) => {
      InputData = JSON.parse(data);
    });
  
    req.on('end', () => {
      connection.query("SELECT shopName from production where productionName = ?", InputData.productionName, function(error, results){
  
        if(error) {
          console.log("error : " + error);
        } else { 
          console.log("해당 매장 이름 : " + results[0].shopName);
          shopName = results[0].shopName;
        }
      params = {
       "productionName":InputData.productionName,
       "shopName":shopName,
       "userEmail":InputData.userEmail  
      }
  
      
      connection.query("INSERT INTO favorite_production SET ?", params, function(error, results) {
  
        if(error){
          console.log("error : " + error);
        }
        else{
          console.log("상품 좋아요 완료");
          console.log("좋아요한 회원 이메일 : " + InputData.userEmail);
          console.log("좋아요한 상품 : " + InputData.productionName);
        }
        res.write("Insert favorite production seccess");
        res.end();
      });
      });
    });
  });
  
  app.post('/StoreComment', (req,res)=>{ //일단 id 제외한 것을 추가해볼 겁니당.

    var inputCmt;
    var params;
  
    req.on('data', (data)=>{
      inputCmt = JSON.parse(data); //inputCmt.Comment
      params =[inputCmt.Comment , inputCmt.nickname, inputCmt.position]
      console.log("I will insert comment into commenttable!");
    })
    req.on('end', ()=>{
      connection.query("INSERT INTO commenttable SET ?",params, (err,result)=>{
          console.log("성공. check DB");
          res.write("plz");
          res.end();
  
      });
    });
  });

  
  app.post('/addLike', (req,res)=>{

    var position;
    var params;
  
    req.on('data', (data)=>{
      position = JSON.parse(data);
      params ={
        "Like":position.like
      }
      console.log("param : ", params);
    })
  
    req.on('end',()=>{
      connection.query("UPDATE review SET Like = ?", params, (err, result)=>{
        res.write("하하하하하");
        res.end();
      }) 
    })
  });

  app.post('/getReviewStory',(req,res)=>{

    var input;
  
    req.on('data', (data)=>{
      input = JSON.parse(data);
      console.log("input"+ input.data1)

    })
    req.on('end',()=>{
    connection.query("SELECT * FROM review WHERE Review_Number =?", input.data1, (err,result)=>{
        if(err)
        {
            console.log("err : " + err);
        }
          console.log("dd" + result[0].User_Content);
          res.write(String(result[0].User_Content));
          
          res.end();
     });
    });
  });


  app.post('/setReviewFile', (req,res)=>{ //err 코드는 꼭 쓰자
    var inputinfo;
    var params;
  
    req.on('data', (data)=>{
      inputinfo = JSON.parse(data);
      params = {
       "User_Id":inputinfo.UserNickName, 
        "User_Content":inputinfo.UserContent, 
        "Review_StoreName":inputinfo.StoreName, 
        "Email":inputinfo.Email,
        "User_Image":inputinfo.UserImg
      }; //키-값일 때 키의 이름은 컬럼명과 동일해야한다!
      console.log(params);
      console.log("데이터 삽입 완료");
    });
  
     req.on('end',()=>{
      connection.query("INSERT INTO review SET ?", params, (err,result)=>{
        if(err) console.log(err); //err는 꼭 console에 출력해봐야 한다.
        res.write("db에 입력 완료");
        res.end();
      });
    });
  });

  app.post('/InsertProductionInfo', (req, res) => {
    var inputData;

    req.on('data', (data) => {
      inputData = JSON.parse(data);
    });

    req.on('end', () => {

      var params = {
        "shopName":inputData.RegProShopName,
        "productionURL":inputData.RegProImageUrl,
        "productionName":inputData.RegProTitle,
        "productionSize":inputData.RegProSize,
        "productionPrice":inputData.RegProPrice,
        "productionIntro":inputData.RegProIntro
      };

      connection.query('INSERT INTO production SET ?', params, function(error, result) {

        if(error)
        {
          console.log("error 발생 : " + error);
          res.write("production register faild");
          res.end();
        }
        else
        {
          console.log("상품 등록 성공 : " + params);
          res.write("production register success");
          res.end();
        }
    });
});




  })



  

};