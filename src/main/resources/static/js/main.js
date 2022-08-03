function exportExcel(){

    var user = [[${filter.userValue}]];
    if (user == null){
        alert("A user must be selected to get excel document.")
    }else {
        exportReportToExcel();
    }

}

function exportReportToExcel() {
    var user = [[${filter.userValue}]];
    var tab_text = ""
    var textRange; var j=0;
    var tab = document.getElementById('table'); // id of table

    tab_text += "<h4 bgcolor='gray'>Maroteknolloji</h4>";
    tab_text += "<table>";
    tab_text += "<tr>" + "User:\t" + user + "</tr>";
    tab_text += "<tr>" + "Date:\t" + getDate() + "</tr>";
    tab_text += "<tr>" + "Total Hours:\t" + getTotalHour() + "</tr>";
    tab_text += "<tr>" + "Total Days:\t" + getAverageHour() + "</tr>";
    tab_text += "</table>";
    tab_text += "<table border='2px'><tr bgcolor='#87AFC6'>";
    for(j = 0 ; j < tab.rows.length ; j++)
    {
        tab_text=tab_text+tab.rows[j].innerHTML+"</tr>";
        //tab_text=tab_text+"</tr>";
    }

    tab_text=tab_text+"</table>";
    tab_text= tab_text.replace(/<A[^>]*>|<\/A>/g, "");//remove if u want links in your table
    tab_text= tab_text.replace(/<img[^>]*>/gi,""); // remove if u want images in your table
    tab_text= tab_text.replace(/<input[^>]*>|<\/input>/gi, ""); // reomves input params

    var ua = window.navigator.userAgent;
    var msie = ua.indexOf("MSIE ");

    if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./))      // If Internet Explorer
    {
        txtArea1.document.open("txt/html","replace");
        txtArea1.document.write(tab_text);
        txtArea1.document.close();
        txtArea1.focus();
        sa=txtArea1.document.execCommand("SaveAs",true,"Say Thanks to Sumit.xls");
    }
    else                 //other browser not tested on IE 11
        sa = window.open('data:application/vnd.ms-excel,' + encodeURIComponent(tab_text));
    return (sa);
}

function getDate(){
    const date = new Date();
    let day = date.getDate();
    let month = date.getMonth() + 1;
    let year = date.getFullYear();
    let currentDate = `${day}-${month}-${year}`;
    return currentDate;
}

function getTotalHour(){
    var tab = document.getElementById('table');
    var hour = 0;
    var value = "";
    for(j = 1 ; j < tab.rows.length ; j++)
    {
        value += tab.rows[j].cells[6].innerHTML;
        hour += parseFloat(tab.rows[j].cells[5].innerHTML);
    }
    return hour;
}
function getAverageHour(){
    return getTotalHour() / 8;
}