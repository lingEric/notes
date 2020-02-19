//日期格式化，返回值形式为yy-mm-dd
var date1 = new Date("January 12,2006 22:19:35"); 
var date2 = new Date("January 12,2006"); 
var date3 = new Date(2006,0,12,22,19,35); 
var date4 = new Date(2006,0,12); 
var date5 = new Date(1137075575000);
var date6 = new Date();

// console.log(date1);
// console.log(date2);
// console.log(date3);
// console.log(date4);
// console.log(date5);
// console.log(date6);

//时间格式化
console.log("时间格式化timeFormat():\n"+timeFormat(date1));
function timeFormat(date) {
    if (!date || typeof(date) === "string") {
        this.error("参数异常，请检查...");
    }
    var y = date.getFullYear(); //年
    var m = date.getMonth() + 1; //月
    var d = date.getDate(); //日

    return y + "-" + m + "-" + d;
}

//获取这周的周一
console.log("获取指定日期所在星期的周一getFirstDayOfWeek():\n"+getFirstDayOfWeek(new Date()));
function getFirstDayOfWeek (date) {

    var weekday = date.getDay()||7; //获取星期几,getDay()返回值是 0（周日） 到 6（周六） 之间的一个整数。0||7为7，即weekday的值为1-7

    date.setDate(date.getDate()-weekday+1);//往前算（weekday-1）天，年份、月份会自动变化
    return timeFormat(date);
}

//获取当月第一天
console.log("获取指定日期所在月份的第一天getFirstDayOfMonth():\n"+getFirstDayOfMonth(new Date()));
function getFirstDayOfMonth (date) {
    date.setDate(1);
    return timeFormat(date);
}

//获取当季第一天
function getFirstDayOfSeason (date) {
    var month = date.getMonth();
    if(month <3 ){
        date.setMonth(0);
    }else if(2 < month && month < 6){
        date.setMonth(3);
    }else if(5 < month && month < 9){
        date.setMonth(6);
    }else if(8 < month && month < 11){
        date.setMonth(9);
    }
    date.setDate(1);
    return timeFormat(date);
}

//获取当年第一天
function getFirstDayOfYear (date) {
    date.setDate(1);
    date.setMonth(0);
    return timeFormat(date);
}



