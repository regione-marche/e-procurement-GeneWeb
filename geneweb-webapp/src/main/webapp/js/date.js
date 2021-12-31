function addDays(myDate,days) {
    return new Date(myDate.getTime() + days*24*60*60*1000);
}


function GiornoFestivo (Data)
{
	var giorno,mese,anno,giorno1;
	var myDays= ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]
	
	giorno = Data.getDate();
	giorno1 = Data.getDay();
	mese = Data.getMonth();
	anno = Data.getYear();
																
	if (anno < 1900) anno = parseInt(anno + 1900);
	
	if (myDays[giorno1].toUpperCase() == "SUNDAY") { //Domenica
		return 1;
	}
	else if (giorno == '1' && mese =='0') {  //Capodanno
		return 1;
	}
	else if (giorno == '6' && mese =='0') {  //Epifania
		return 1;
	}
	else if (giorno == '25' && mese =='3') {  //Anniversario Liberazione
		return 1;
	}
	else if (giorno == '1' && mese =='4') {  //Festa del Lavoro
		return 1;
	}
	else if (giorno == '2' && mese =='5') {  //Festa della Repubblica
		return 1;
	}
	else if (giorno == '15' && mese =='7') {  //Ferragosto
		return 1;
	}
	else if (giorno == '1' && mese =='10') {  //Ogni Santi
		return 1;
	}
	else if (giorno == '8' && mese =='11') {  //Immacolata
		return 1;
	}
	else if (giorno == '25' && mese =='11') {  //Natale
		return 1;
	}
	else if (giorno == '26' && mese =='11') {  //Santo Stefano
		return 1;
	}

	else {
		// Calcolo della Pasqua
		var a,b,c,p,q,r;
		var Pasqua,Pasquetta;
		anno = parseInt(anno);
		a=parseInt(anno) % 19;
		b=Math.floor(anno/100);
		c=parseInt(anno) % 100;
		
		p = (19 * a + b - Math.floor(b / 4) - Math.floor((b - Math.floor((b + 8) / 25) + 1) / 3) + 15) % 30 	;

		q = (32 + 2 * ((b % 4) + Math.floor(c / 4)) - p - (c %4)) % 7 ;
			 
		r = (p + q - 7 * Math.floor((a + 11 * p + 22 * q) / 451) + 114);
		
		Pasqua = new Date(anno, Math.floor(r/31) - 1 ,(r%31)+ 1); //Pasquetta
		Pasquetta = addDays(Pasqua,1); //Pasquetta
		
		if (( mese == Pasquetta.getMonth()) && (giorno == Pasquetta.getDate())) {
			return 1;
		}
		else{
			return 0;
		}
		
	}
  return 0;
}

function DateDaStringa(strdata){
	var giorno,mese,anno,data;
	var datePat = /^(\d{1,2})(\/|-|\.)(\d{1,2})(\/|-|\.)(\d{2,4})$/;
	var matchArray = strdata.match(datePat);
									
	giorno = (matchArray[1].length < 2) ? "0" + matchArray[1]: matchArray[1];
	mese = ((matchArray[3].length < 2) ? "0" + matchArray[3]: matchArray[3]);
	anno = ((matchArray[5].length == 2) ? "20" + matchArray[5]: matchArray[5]);
	data = new Date(anno,parseInt(mese -1),giorno);
	return data;
}

function makeArray() {
    for (i = 0; i<makeArray.arguments.length; i++)
        this[i + 1] = makeArray.arguments[i];
}

var months = new makeArray('January','February','March','April',
                           'May','June','July','August','September',
                           'October','November','December');

function nths(day) {
    if (day == 1 || day == 21 || day == 31) return 'st';
    if (day == 2 || day == 22) return 'nd';
    if (day == 3 || day == 23) return 'rd';
    return 'th';
}

function y2k(number) { return (number < 1000) ? number + 1900 : number; }

function monthsahead(datapartenza,noofmonths) {
    var anno = datapartenza.getYear();
    if (anno < 1900) {
    	anno= anno + 1900;
    }
    var data = new Date(anno,datapartenza.getMonth() + noofmonths,datapartenza.getDate(),datapartenza.getHours(),datapartenza.getMinutes(),datapartenza.getSeconds());
    
    return data;
}

function DateToString(sdata)
{
	var anno, mese, giorno,strdata,data;
	data = new Date(sdata);
	if ( parseInt(data.getYear())<1900) {
		anno = parseInt(data.getYear()) + 1900;
	}
	else {
		anno = data.getYear();
	}
	if ( parseInt(data.getDate())<10) {
		giorno = '0' + data.getDate() ;
	}
	else {
		giorno = data.getDate();
	}
	if ( parseInt(data.getMonth() + 1)<10) {
		mese = '0' + parseInt(data.getMonth() + 1) ;
	}
	else {
		mese = parseInt(data.getMonth() + 1);
	}
	strdata = giorno + "/" + mese + "/" + anno;
	return 	strdata;	
}
