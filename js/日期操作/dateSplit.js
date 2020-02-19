Date.prototype.format=function (){
  var s='';
  s+=this.getFullYear()+'-';          // 获取年份。
  s+=(this.getMonth()+1)+"-";         // 获取月份。
  s+= this.getDate();                 // 获取日。
  return(s);                          // 返回日期。
};

//按日查询
function getDayAll(begin,end){
  var dateAllArr = new Array();
  var ab = begin.split("-");
  var ae = end.split("-");
  var db = new Date();
  db.setUTCFullYear(ab[0], ab[1]-1, ab[2]);
  var de = new Date();
  de.setUTCFullYear(ae[0], ae[1]-1, ae[2]);
  var unixDb=db.getTime();
  var unixDe=de.getTime();
  for(var k=unixDb;k<=unixDe;){
      dateAllArr.push((new Date(parseInt(k))).format().toString());
      k=k+24*60*60*1000;
  }
  return dateAllArr;
}

//按周查询
function getWeekAll(begin,end){
  var dateAllArr = new Array();
  var ab = begin.split("-");
  var ae = end.split("-");
  var db = new Date();
  db.setUTCFullYear(ab[0], ab[1]-1, ab[2]);
  var de = new Date();
  de.setUTCFullYear(ae[0], ae[1]-1, ae[2]);
  var unixDb=db.getTime();
  var unixDe=de.getTime();
  for(var k=unixDb;k<=unixDe;){
      dateAllArr.push((new Date(parseInt(k))).format().toString());
      k=k+7*24*60*60*1000;
  }
  return dateAllArr;
}

function getMonthAll(begin,end) {
  var d1 = begin;
  var d2 = end;
  var dateArry = new Array();
  var s1 = d1.split("-");
  var s2 = d2.split("-");
  var mCount = 0;
  if (parseInt(s1[0]) < parseInt(s2[0])) {
      mCount = (parseInt(s2[0]) - parseInt(s1[0])) * 12 + parseInt(s2[1]) - parseInt(s1[1])+1;
  } else {
      mCount = parseInt(s2[1]) - parseInt(s1[1])+1;
  }
  if (mCount > 0) {
      var startM = parseInt(s1[1]);
      var startY = parseInt(s1[0]);
      for (var i = 0; i < mCount; i++) {
          if (startM < 12) {
              dateArry[i] = startY + "-" + (startM>9 ? startM : "0" + startM);
              startM += 1;
          } else {
              dateArry[i] = startY + "-" + (startM > 9 ? startM : "0" + startM);
              startM = 1;
              startY += 1;
          }
      }
  }
  return dateArry;
}

function getYearAll(begin,end) {
  var d1 = begin;
  var d2 = end;
  var dateArry = new Array();
  var s1 = d1.split("-");
  var s2 = d2.split("-");
  var mYearCount = parseInt(s2[0]) - parseInt(s1[0])+1;
  var startY = parseInt(s1[0]);
  for (var i = 0; i < mYearCount;i++) {
      dateArry[i] = startY;
      startY += 1;
  }
  return dateArry;
}

// console.log(getDayAll("2017-02-2","2017-09-08"));
// console.log(getWeekAll("2017-06-04","2017-09-04"));
console.log(getMonthAll("2016-06-01","2017-09-01"));
getYearAll("2003-01-0","2017-01-01")