/** 
 * ���ڽ������ַ���ת���� 
 * @param dateString ����Ϊ2017-02-16��2017/02/16��2017.02.16 
 * @returns {Date} ���ض�Ӧ�����ڶ��� 
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
 * ���ڱȽϴ�С 
 * compareDateString����dateString������1�� 
 * ���ڷ���0�� 
 * compareDateStringС��dateString������-1 
 * @param dateString ���� 
 * @param compareDateString �Ƚϵ����� 
 */  
function dateCompare(dateString, compareDateString){  
    if(isEmpty(dateString)){  
        alert("dateString����Ϊ��");  
        return;  
    }  
    if(isEmpty(compareDateString)){  
        alert("compareDateString����Ϊ��");  
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
 * �ж������Ƿ��������ڣ��������ڷ���true���񷵻�false 
 * @param dateString �����ַ��� 
 * @param startDateString ���俪ʼ�����ַ��� 
 * @param endDateString ������������ַ��� 
 * @returns {Number} 
 */  
function isDateBetween(dateString, startDateString, endDateString){  
    if(isEmpty(dateString)){  
        alert("dateString����Ϊ��");  
        return;  
    }  
    if(isEmpty(startDateString)){  
        alert("startDateString����Ϊ��");  
        return;  
    }  
    if(isEmpty(endDateString)){  
        alert("endDateString����Ϊ��");  
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
 * �ж���������[startDateCompareString,endDateCompareString]�Ƿ���ȫ�ڱ������������[startDateString,endDateString] 
 * ��[startDateString,endDateString]�����Ƿ���ȫ������[startDateCompareString,endDateCompareString]���� 
 * �������ڷ���true���񷵻�false 
 * @param startDateString ��ѡ��Ŀ�ʼ���ڣ��������Ŀ�ʼ���� 
 * @param endDateString ��ѡ��Ľ������ڣ��������Ľ������� 
 * @param startDateCompareString �ȽϵĿ�ʼ���� 
 * @param endDateCompareString �ȽϵĽ������� 
 * @returns {Boolean} 
 */  
function isDatesBetween(startDateString, endDateString,  
        startDateCompareString, endDateCompareString){  
    if(isEmpty(startDateString)){  
        alert("startDateString����Ϊ��");  
        return;  
    }  
    if(isEmpty(endDateString)){  
        alert("endDateString����Ϊ��");  
        return;  
    }  
    if(isEmpty(startDateCompareString)){  
        alert("startDateCompareString����Ϊ��");  
        return;  
    }  
    if(isEmpty(endDateCompareString)){  
        alert("endDateCompareString����Ϊ��");  
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