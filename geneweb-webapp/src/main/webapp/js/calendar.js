// months as they appear in the selection list
var ARR_MONTHS = ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
		"Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"];
// week day titles as they appear on the calendar
var ARR_WEEKDAYS = ["Do", "Lu", "Ma", "Me", "Gi", "Ve", "Sa"];
// day week starts from (normally 0-Mo or 1-Su)
var NUM_WEEKSTART = 1;

var calendars = [];
var controlToSet;
function calendar(str_date,str_form_name,str_ctrl_name)
{
	this.get_html=cal_get_html;
	this.get_body=cal_get_body;
	this.set_year=cal_set_year;
	this.set_month=cal_set_month;
	this.set_day=cal_set_day;
	this.update=(document.body&&document.body.innerHTML?cal_css_update:cal_rel_update);
	this.id=calendars.length;
	calendars[this.id]=this;
	var re_url=new RegExp('cal'+this.id+'_val=(\\d+)');
	this.dt_current=(re_url.exec(String(window.location))?new Date(new Number(RegExp.$1)):(str_date?cal_parse_date(str_date):cal_date_only()));
	if(!str_form_name)
		return alert('Form name is required parameter of draw method.');
	if(!document.forms[str_form_name])
		return alert("Form with name '"+this.form_name+"' can't be found in the document.");
	this.form_name=str_form_name;
	this.control_name=(str_ctrl_name?str_ctrl_name:'datetime_'+this.id);
	document.write(this.get_html(this.dt_current));
	this.mon_ctrl=document.forms[this.form_name].elements['dt_mon_'+this.id];
	this.year_ctrl=document.forms[this.form_name].elements['dt_year_'+this.id];
        controlToSet = str_ctrl_name;
}
function cal_css_update(dt_datetime)
{
	this.dt_current=dt_datetime;
	var obj_container=(document.all?document.all['cal_body_'+this.id]:document.getElementById('cal_body_'+this.id));
	obj_container.innerHTML=this.get_body(dt_datetime);
	this.mon_ctrl.selectedIndex=dt_datetime.getMonth();
	this.year_ctrl.selectedIndex=dt_datetime.getFullYear()-Number(this.year_ctrl.options[1].text)+1;
}
function cal_rel_update(dt_datetime)
{
	var arr_location=String(window.location).split('?');
	var arr_params=String(arr_location[1]).split('&');
	var num_found=-1;
	for(var i=0;i<arr_params.length;i++)
	{
		if((arr_params[i].split('='))[0]=='cal'+this.id+'_val')
		{
			num_found=i;break;
		}
	}
	arr_params[(num_found==-1?(arr_location[1]?arr_params.length:0):num_found)]='cal'+this.id+'_val='+dt_datetime.valueOf();
	window.location=arr_location[0]+'?'+arr_params.join('&');
}
function cal_set_day(num_datetime)
{
	var dt_newdate=new Date(num_datetime);
	//this.update(cal_validate(dt_newdate)?dt_newdate:this.dt_current);
	var Month = parseInt(dt_newdate.getMonth()+1)< 10?"0"+parseInt(dt_newdate.getMonth()+1):parseInt(dt_newdate.getMonth()+1);
	var Day = dt_newdate.getDate()< 10?"0"+dt_newdate.getDate():dt_newdate.getDate();
	var Year = dt_newdate.getFullYear();
        //var objecttoset = eval("opener.document.forms[0]."+ controlToSet);
        var objecttoset = controlToSet;
				try{
					opener.setValue(objecttoset.name,Day+"/"+Month+"/"+Year);
				}catch(e){
					objecttoset.value = Day+"/"+Month+"/"+Year;
				}
        
	this.close();
}
function cal_set_month()
{
	var dt_newdate=new Date(this.dt_current);
	var num_month=this.mon_ctrl.options[this.mon_ctrl.selectedIndex].value;
	dt_newdate.setMonth(num_month);
	if(num_month!=dt_newdate.getMonth())
		dt_newdate.setDate(0);
	if(cal_validate(dt_newdate))
		this.update(dt_newdate);
	else 
		this.mon_ctrl.selectedIndex=this.dt_current.getMonth();
}
function cal_set_year()
{
	var dt_newdate=new Date(this.dt_current);
	var str_year=this.year_ctrl.options[this.year_ctrl.selectedIndex].text;
	var str_scroll=this.year_ctrl.options[this.year_ctrl.selectedIndex].value;
	var num_year;
	if(str_scroll == "+" || str_scroll == "-")
	{
		num_year=(str_scroll=='+'?this.dt_current.getFullYear()+4:this.dt_current.getFullYear()-4);
	}
	else 
		num_year=new Number(str_year);
	dt_newdate.setFullYear(num_year);
	var num_month=this.mon_ctrl.options[this.mon_ctrl.selectedIndex].value;
	if(num_month!=dt_newdate.getMonth())
	dt_newdate.setDate(0);
	if(!cal_validate(dt_newdate))
	{
		this.year_ctrl.selectedIndex=this.dt_current.getFullYear()-Number(this.year_ctrl.options[1].text)+1;
		return;
	}
	if(str_scroll == "+" || str_scroll == "-")
	{
		this.year_ctrl.length=0;
		this.year_ctrl.options[0]=new Option('<<'+(num_year-4),'-');
		for(var i=1;i<8;i++)
		{
			this.year_ctrl.options[i]=new Option(num_year+i-4);
			this.year_ctrl.options[i].selected=!(i-4);
		}
		this.year_ctrl.options[8]=new Option((num_year+4)+'>>','+');
	}
	this.update(dt_newdate);
}
function cal_limit_min(str_min_date,b_absolute)
{
	this.min_date=(b_absolute?this.parse_date(str_min_date):new Date(this.date.valueOf()-new Number(str_min_date*864e5)));
}
function dts_limit_max(str_max_date,b_absolute)
{
	this.cal_date=(b_absolute?this.parse_date(str_max_date):new Date(this.date.valueOf-new Number(str_max_date*864e5)));
}
function cal_get_html(dt_datetime)
{
	var i,str_buffer=new String('<table cellspacing="0" class="calOuterTable" border="0"><tr>'+'<td>'+'<table cellspacing="0" class="calSelectTable"><tr><td>'+'<select name="dt_mon_'+this.id+'" class="calMonCtrl" onchange="return calendars['+this.id+'].set_month();">');
	for(i=0;i<12;i++)
		str_buffer+='<option value="'+i+'"'+(i==dt_datetime.getMonth()?' selected':'')+'>'+ARR_MONTHS[i]+"</option>\n";
		str_buffer+='</select></td>'+'<td align="right"><select name="dt_year_'+this.id+'" class="calYearCtrl" onchange="return calendars['+this.id+'].set_year();">'+'<option value="-">&lt;&lt;'+(dt_datetime.getFullYear()-4)+"</option>\n";
	for(i=-3;i<4;i++)
		str_buffer+='<option'+(i==0?' selected':'')+'>'+(dt_datetime.getFullYear()+i)+"</option>\n";
		str_buffer+='<option value="+">'+(dt_datetime.getFullYear()+4)+"&gt;&gt;</option>\n"+'</select></td></tr></table></td></tr>'+'<tr><td><div id="cal_body_'+this.id+'">'+this.get_body(dt_datetime)+'</div>'+'</td>'+'</tr></table>';
	return(str_buffer);
}
function cal_get_body(dt_datetime)
{
	var dt_prev_year=new Date(dt_datetime);
	dt_prev_year.setFullYear(dt_prev_year.getFullYear()-1);
	if(dt_prev_year.getDate()!=dt_datetime.getDate())dt_prev_year.setDate(0);
		var dt_next_year=new Date(dt_datetime);
	dt_next_year.setFullYear(dt_next_year.getFullYear()+1);
	if(dt_next_year.getDate()!=dt_datetime.getDate())
		dt_next_year.setDate(0);
	var dt_firstday=new Date(dt_datetime);
	dt_firstday.setDate(1);
	dt_firstday.setDate(1-(7+dt_firstday.getDay()-NUM_WEEKSTART)%7);
	var str_buffer=new String('<table cellspacing="1" cellpadding="2" class="calDaysTable" width="100%">');
	str_buffer+='<tr>';
	for(var n=0;n<7;n++)
		str_buffer+='<td class="calWTitle">'+ARR_WEEKDAYS[(NUM_WEEKSTART+n)%7]+'</td>';
	str_buffer+="</tr>\n";
	var dt_current_day=new Date(dt_firstday);
	while(dt_current_day.getMonth()==dt_datetime.getMonth()||dt_current_day.getMonth()==dt_firstday.getMonth())
	{
		str_buffer+='<tr>';
		for(var n_current_wday=0;n_current_wday<7;n_current_wday++)
		{
			if(dt_current_day.getDate()==dt_datetime.getDate()&&dt_current_day.getMonth()==dt_datetime.getMonth())
				str_buffer+='<td class="calDayCurrent" align="center" valign="middle">';
			else if(dt_current_day.getDay()==0||dt_current_day.getDay()==6)
				str_buffer+='<td class="calDayWeekend" align="center" valign="middle">';
			else 
				str_buffer+='<td class="calDayWorking" align="center" valign="middle">';
			if(dt_current_day.getMonth()==dt_datetime.getMonth())
				str_buffer+='<a href="javascript:cal_set_day('+dt_current_day.valueOf()+');" class="calThisMonth">';
			else 
				str_buffer+='<a href="javascript:cal_set_day('+dt_current_day.valueOf()+');" class="calOtherMonth">';
			str_buffer+=dt_current_day.getDate()+'<!--</a>--></td>';
			dt_current_day.setDate(dt_current_day.getDate()+1);
		}
		str_buffer+="</tr>\n";
	}
	str_buffer+='</table>';
	return(str_buffer);
}
function cal_date_only(dt_datetime)
{

	if(!dt_datetime)
		dt_datetime=new Date();
	dt_datetime.setHours(0);
	dt_datetime.setMinutes(0);
	dt_datetime.setSeconds(0);
	dt_datetime.setMilliseconds(0);
	return dt_datetime;
}
function cal_parse_date(str_date,dt_datetime) {   
    if (str_date.length<10) return new Date(); 
    var year = str_date.substring(6,10);
    var month = str_date.substring(3,5)-1;
    var day = str_date.substring(0,2);

    dt_datetime = new Date(year,month,day);

    dt_datetime.setHours(0);
    dt_datetime.setMinutes(0);
    dt_datetime.setSeconds(0);
    dt_datetime.setMilliseconds(0);    
    
    return dt_datetime;
}       

function cal_validate(dt_datetime)
{
	return true;
}