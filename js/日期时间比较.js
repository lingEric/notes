/** 
 * 日期解析，字符串转日期 
 * @param dateString 可以为2017-02-16，2017/02/16，2017.02.16 
 * @returns {Date} 返回对应的日期对象 
 */  
function dateParse(dateString){  
    var SEPARATOR_BAR = "-";  
    var SEPARATOR_SLASH = "/";  
    var SEPARATOR_DOT = ".";  
    var dateArray;  
    if(dateString.indexOf(SEPARATOR_BAR) > -1){  
        dateArray = dateString.split(SEPARATOR_BAR);    
    }else if(dateString.indexOf(SEPARATOR_SLASH) > -1){  
        dateArray = dateString.split(SEPARATOR_SLASH);  
    }else{  
        dateArray = dateString.split(SEPARATOR_DOT);  
    }  
    return new Date(dateArray[0], dateArray[1]-1, dateArray[2]);   
};  
  
/** 
 * 日期比较大小 
 * compareDateString大于dateString，返回1； 
 * 等于返回0； 
 * compareDateString小于dateString，返回-1 
 * @param dateString 日期 
 * @param compareDateString 比较的日期 
 */  
function dateCompare(dateString, compareDateString){  
    if(isEmpty(dateString)){  
        alert("dateString不能为空");  
        return;  
    }  
    if(isEmpty(compareDateString)){  
        alert("compareDateString不能为空");  
        return;  
    }  
    var dateTime = dateParse(dateString).getTime();  
    var compareDateTime = dateParse(compareDateString).getTime();  
    if(compareDateTime > dateTime){  
        return 1;  
    }else if(compareDateTime == dateTime){  
        return 0;  
    }else{  
        return -1;  
    }  
};  
  
/** 
 * 判断日期是否在区间内，在区间内返回true，否返回false 
 * @param dateString 日期字符串 
 * @param startDateString 区间开始日期字符串 
 * @param endDateString 区间结束日期字符串 
 * @returns {Number} 
 */  
function isDateBetween(dateString, startDateString, endDateString){  
    if(isEmpty(dateString)){  
        alert("dateString不能为空");  
        return;  
    }  
    if(isEmpty(startDateString)){  
        alert("startDateString不能为空");  
        return;  
    }  
    if(isEmpty(endDateString)){  
        alert("endDateString不能为空");  
        return;  
    }  
    var flag = false;  
    var startFlag = (dateCompare(dateString, startDateString) < 1);  
    var endFlag = (dateCompare(dateString, endDateString) > -1);  
    if(startFlag && endFlag){  
        flag = true;  
    }  
    return flag;  
};  
  
/** 
 * 判断日期区间[startDateCompareString,endDateCompareString]是否完全在别的日期区间内[startDateString,endDateString] 
 * 即[startDateString,endDateString]区间是否完全包含了[startDateCompareString,endDateCompareString]区间 
 * 在区间内返回true，否返回false 
 * @param startDateString 新选择的开始日期，如输入框的开始日期 
 * @param endDateString 新选择的结束日期，如输入框的结束日期 
 * @param startDateCompareString 比较的开始日期 
 * @param endDateCompareString 比较的结束日期 
 * @returns {Boolean} 
 */  
function isDatesBetween(startDateString, endDateString,  
        startDateCompareString, endDateCompareString){  
    if(isEmpty(startDateString)){  
        alert("startDateString不能为空");  
        return;  
    }  
    if(isEmpty(endDateString)){  
        alert("endDateString不能为空");  
        return;  
    }  
    if(isEmpty(startDateCompareString)){  
        alert("startDateCompareString不能为空");  
        return;  
    }  
    if(isEmpty(endDateCompareString)){  
        alert("endDateCompareString不能为空");  
        return;  
    }  
    var flag = false;  
    var startFlag = (dateCompare(startDateCompareString, startDateString) < 1);  
    var endFlag = (dateCompare(endDateCompareString, endDateString) > -1);  
    if(startFlag && endFlag){  
        flag = true;  
    }  
    return flag;  
};