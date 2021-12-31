/*
 * Created on 25-09-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 * 
 */
/***********************************************************
  Created by Marco Franceschin
  
  Sorgente per la gestione dei controlli e dei calcoli automatici sulle maschere
  utilizzate dai programmi
 ***********************************************************/

 /** Variabile utilizzata per impostare o meno la visibilita dei messaggi*/
var msgVisibili;
/** 
	Variabile che identifica il debug
	0	Nessun messaggio di debug
	1	Solo errori
	2	Solo errori e warning
	3	Tutti i messaggi
*/
var debugJavascript=3;

/** Puntatore all'ultimo oggetto form creato */
var activeForm=null;

/** Nome del form archivio che richiede l'apertura della popup lista dati archivio */
var activeArchivioForm = null;

/** Variabile globale di tipo booleana, che di default, permette di saltare i
    controlli di obbligatorieta' dei campi obbligatori presenti all'interno delle
    sezioni dinamiche.
    Se in una scheda in modifica questa variabile viene settata a true, al momento
    del salvataggio viene lanciata la funzione JS 'astratta' denominata
    controlloValorizzazioneSezioniDinamicheVisualizzate(), la quale deve essere
    definita nella scheda stessa.
    Ovviamente settare a true questa variabile deve venir fatto in pagine in cui
    sono presenti i campi obbligatori all'interno di sezioni dinamiche.
    Per ulteriori dettagli vedere il commento della funzione schedaConfermaPopUp()
    nel file jsAzioniTags.jsp.
  */
var controlloSezioniDinamiche = false;

/** Creazione di un array che conterra' un numero di oggetti pari al numero di
    sezioni dinamiche presenti nella pagina. Gli oggetti dell'array saranno di
    tipo SezioneDinamicaObj. Questo array e' una variabile JS globale, che viene
    usata dalla funzione controlloValorizzazioneSezioniDinamicheVisualizzate 
    (definita nel file forms.js), quindi il suo nome NON deve essere modificato
    per nessun motivo.
  */
var arraySezioniDinamicheObj = new Array();

/** elenco dei campi archivio presente nella pagina */
var elencoArchivi=new Array();

function getObjectById(id){
	/**************************************************************
		Funzione che estrae un oggetto in funzione del suo identificativo
		@param
		@return
		@author Marco Franceschin
		@changelog
			26.09.2006: M.F. Prima Versione
	***************************************************************/
	var retObj=null;
	if(document.getElementById!=null){
  	retObj=document.getElementById(id);
  }else{
  	try{
    	retObj=eval("document.all."+id);
    }catch(e){
    }
  }
	return retObj;
}

function showObj(objId,show){
	/**************************************************************	 
		Funzione che nasconde o visualizza un oggetto
		@param
			objId
				Identificativo dell'oggetto
			show
				Flag per dire se visualizzare (true di default)
		@author Marco Franceschin
		@changelog
			26.09.2006: M.F. Prima Versione
			22.11/2007 M.F. Gestione per Firefix
	***************************************************************/
	// Inizializzaziono
	var obj=getObjectById(objId);
	if(obj!=null){
		if(show==null)
			show=obj.style.display=="none";
		if(obj.style.display=="none"){
			if(!show)return
		}else{
			if(show)return
		}
		
		if(show){
			obj.style.display="";
		}else{
			obj.style.display="none";
		}
	}
}

function isObjShow(objId){
	var obj = getObjectById(objId);
	var result = false;
	if(obj != null){
		if(obj.style.display != "none")
			result = true;
	}
	return result;
}

function onOffMsgFlag(isOn){
	/**************************************************************	 
		Funzione che rende visibile o invisibile i messaggi javascript
		@param
		@return
		@author Marco Franceschin
		@changelog
			26.09.2006: M.F. Prima Versione
	***************************************************************/
	// Inverto la selezione della visibilita' dei messaggi
	msgVisibili=isOn;
	showObj("msgimgon",msgVisibili);
	showObj("msgimgoff",!msgVisibili);
	showObj("msglog",msgVisibili);
} 

function onOffMsg(){
	/**************************************************************	 
		Funzione che rende visibile o invisibile i messaggi javascript
		@param
		@return
		@author Marco Franceschin
		@changelog
			26.09.2006: M.F. Prima Versione
	***************************************************************/
	// Inverto la selezione della visibilit? dei messaggi
	onOffMsgFlag(!msgVisibili);
} 

function outMsg(msg,tipo){
  /**************************************************************
  		Funzione che scrive un messaggio nell'html.
      Per funzionare deve esistere una sessione DIV di nome: msglog
      e id: msglog  	 
  	@param
    	msg
      	Messaggio
      tipo
      	Tipo di messaggio
  	@author Marco Franceschin
  	@changelog
  		26.09.2006: M.F. Prima Versione
  ***************************************************************/
  var divObj=null;
  var inHtml="";
  
  divObj=getObjectById('msglog');
  
  // Se l'elemento e' stato trovato allora appendo il messaggio
  if (divObj != null) {
  	inHtml=divObj.innerHTML;
    if (tipo != null) {
    	switch (tipo) {
      	case "ERR": // Errore 
        	inHtml+="<li class=\"errori-javascript-err\" >ERRORE: ";
			if (debugJavascript > 0)
				showObj("msgcontainer",true);
        	break;
        case "WAR": // Warning
        	inHtml+="<li class=\"errori-javascript-war\">ATTENZIONE: ";
			if (debugJavascript > 1)
				showObj("msgcontainer",true);
        	break;
        default:
    		inHtml+="<li class=\"errori-javascript-msg\">"+tipo+": ";
			if (debugJavascript>2)
				showObj("msgcontainer",true);
    	}
    } else {
		if (debugJavascript > 2)
			showObj("msgcontainer",true);
	    inHtml += "<li class=\"errori-javascript-msg\">";
	}
    inHtml+="<small>"+msg+"</small>";
    inHtml+="</li>\n";
    divObj.innerHTML=inHtml;
  }
}

function outMsgDebug(msg){
	outMsg(msg);
}


function clearMsg(){
/***********************************************************
	Eseguo il clear di tutti i messaggi a video.
	@changelog
		23/10/2006 M.F. Prima versione
 ***********************************************************/

  
	divObj=getObjectById('msglog');
  // Se l'elemento ? stato trovato allora appendo il messaggio
  if(divObj!=null){
		showObj("msgcontainer",false);
  	divObj.innerHTML="";
  }
	onOffMsgFlag(false);
	
}

/***********************************************************
		Oggetto utilizzato per il passaggio di una variabile come reference 
    ad una funzione. (questo perche javascript non passa mail le stringhe come 
    references).
	@changelog
		27.09.2006 M.F. Prima versione
 ***********************************************************/
function Ref(initialval){
	var value;
	this.value=initialval;
	
	this.setValue=function(value){
		this.value = value;
	}
}

function formatNumber(val,format,leftAppend){
  /**************************************************************
  		Funzione che esegue la trasformazione di un numero con una stringa adeguatamente 
      formattata
  	@param 
    	val
      	Val?ore da convertira
      format
      	Decimale che indica numero di cifre decimali e numero di decimali. Es. 100.2
      leftAppend
      	Eventuale carattere da appendere a sinistra 
  	@return
    	Stringa formattata
  	@author Marco Franceschin
  	@changelog
  		26.09.2006: M.F. Prima Versione
		02.10.2006 M.F. Modifica per il ritrovo dei decimali che sbagliava
		14.12.2006 M.F. Modifica per l'errore del calcolo dei decimali che sbagliava
		07.01.2008 M.F. Aggiunta dellarrottondamento sul numero editato
		14.01.2008 M.F. Risoluzione di problemi con il format number se trasformato in numeric per verifica decimali
		24.10.2014 S.S. Fix in approssimazione di numeri negativi e migliorie stilistiche minimali al codice
  ***************************************************************/
	// formato del tipo nn.mm dove nn e ?l numero delle cifre mentre mm e il numero di decimali
	var nn=Math.floor(format);
	var mm=(format*10)%10;
	var ret="";
	var dec="";
	val=new String(Math.round(val*Math.pow(10, mm))/Math.pow(10, mm));
	//outMsg("Format: "+nn+"."+mm+"="+val,"WAR");
	
	if(val.indexOf(".")>=0){
		ret=val.substring(0,val.indexOf("."));
	}else{
		ret=val;
	}
	
	while(ret.substring(0,1)=="0"){
		ret=ret.substring(1);
	}
	if(leftAppend!=null){
		leftAppend=leftAppend.substring(0,1);
		while (ret.length < nn) {
			ret=leftAppend+ret;
		}
	}
	if(mm>0){
		if(val.indexOf(".")>=0){
			dec=val.substring(val.indexOf(".")+1);
		}else{
			dec="";
		}
		//outMsg(dec);
		while (dec.length < mm) {
			dec=dec+"0";
		}
		//outMsg("Dec:"+dec);
		ret=ret+"."+dec.substr(0,mm);
		if(eval(dec.substr(mm,1))>4){
			ret=eval(ret)+Math.pow(10, - mm);
			ret=String(ret);
			if(ret.indexOf(".")<0) ret+=".";
			ret+="0000000000";
			ret=ret.substring(0,ret.indexOf(".")+mm+1);
		}
		
	}
	//outMsg("Dec: "+dec+" Formato: "+val+" nn: "+nn+" mm: "+mm+" LeftChar:"+leftAppend+" = "+ret);
	if(ret.substring(0,1)==".") ret="0"+ret;
	return ret;
	
}
//
//	Eventuali funzioni di validazione sui campi singoli
//
function isNumericStr(sText, decimal)
{
	var ValidChars = ".0123456789";
	var Char;
	var findDec=false;
	var idx, i;
	if(!decimal)
		ValidChars = "0123456789";
	for (i = (sText.charAt(0)=='-' ? 1:0); i < sText.length ; i++){ 
		Char = sText.charAt(i); 
		idx=ValidChars.indexOf(Char);
		if ( idx == -1){
			return false;
		}
		if(decimal && idx == 0){
			if(findDec)
				return false;
			else
				findDec=true;
		}
	}
	return true;
}
// Verifica di un numerico
function checkNumber(val,msg){
	var valore;
	if(!isNumericStr(val.value,false))
		return false;
	try{
		valore=val.value;
	}catch(e){
	}
	val.value=String(Math.floor(valore));
	return true;
}
// Verifica di un importo
function checkMoney(val,msg,obj){
	var valore;
	if(!isNumericStr(val.value,true)) {
		msg.value = "I caratteri ammessi sono le cifre, il punto come\nseparatore decimale ed eventualmente il segno";
		return false;
	}
	try{
		valore=val.value;
	}catch(e){
	}
	if(obj!=null){
		dec=eval(String(obj.len)+"."+String(obj.dec));
		val.value=formatNumber(valore,dec);
	}
	else
		val.value=String(valore);
	return true;
}
// Verifica di un decimale: rispetto all'importo money non si applicano riformattazioni
function checkFloat(val,msg,obj){
	var valore;
	if(!isNumericStr(val.value,true)) {
		msg.value = "I caratteri ammessi sono le cifre, il punto come\nseparatore decimale ed eventualmente il segno";
		return false;
	}
	try{
		valore=val.value;
	}catch(e){
	}
	if (obj != null)
		val.value=String(round(parseFloat(valore), obj.dec));
	else 
		val.value=String(valore);
	return true;
}

function isBisestile(year){
	/**************************************************************	 
		Funzione che verifica se un anno ? un anno bisestile
		@param year Anno da verificare
		@return true se bisestile; false se non bisestile
		@author Marco Franceschin
		@changelog
			19.06.2006: M.F. Prima Versione
	***************************************************************/
	if(((year % 4)==0 && !((year % 100)==0)) || ( year % 400)==0 )
		return true;
	return false;
}
// Verifica di una data
function checkData(val,msg){
	// in: Ref val, Ref msg 
	// Questa dunzione di incarica di verificare che una data sia nel formato 
	// dd.mm.yyyy [HH:MM:SS]
	// Utilizzo un oggetto data per verificare la data
	var monthLength = new Array(31,28,31,30,31,30,31,31,30,31,30,31);
	if(msg instanceof Ref)
		msg.value="Il valore del campo deve essere nel formato dd/mm/yyyy. Es. 26/09/2006";
	try{
		var div;
		if(val.value.indexOf("/")>=0)
			div=val.value.split("/");
		else if(val.value.indexOf(".")>=0)
			div=val.value.split(".");
		var year=eval(div[2]);
		var month=eval(div[1]);
		var day=eval(div[0]);
	}catch(e){
		return false;
	}
	// Verifico il mese
	if(month<1||month>12)
		return false;
	// Se si tratta di un anno a 2 cifre setto l'anno giusto
	if(year<100){
		if(year<30)
			year+=2000;
		else
			year+=1900;
	}
	// Verifico l'anno
	if(year<1900 || year>3000)
		return false;
	if(isBisestile(year))
		monthLength[1]=29;
	// Verifico i giorni
	if(day<1||day>monthLength[month-1])
		return false;
	
	//outMsg("Y="+year+" M="+month+" D="+day);
	// Setto il valore come deve effettivamente essere
	val.value=formatNumber(day,2,'0')+"/"+formatNumber(month,2,'0')+"/"+formatNumber(year,4);    
	return true;
}

function checkOra(valore, messaggio){
	if(messaggio instanceof Ref)
		messaggio.value="Il valore del campo deve essere nel formato hh:mm. Es. 15:37";

	var result = true;
	var timePattern = null;
	if(valore.value.indexOf(":") > 0)
		timePattern = /^(\d{1,2}):(\d{0,2})$/; // RegExp per validare l'ora espressa nel formato hh:mm
	else if(valore.value.indexOf(".") > 0)
		timePattern = /^(\d{1,2}).(\d{0,2})$/; // RegExp per validare l'ora espressa nel formato hh.mm
	else 
		timePattern = /^(\d{1,2})$/; // RegExp per validare l'ora espressa nel formato hh (senza minuti)
		
	if(timePattern != null){
		var matchArray = valore.value.match(timePattern);
		if (matchArray == null) {
		  result = false;
		} else {
			if(matchArray.length > 2){
				var hour = matchArray[1];
			  var minute = matchArray[2];
				if(hour < 0  || hour > 23) {
		  	  messaggio.value="L' ora deve essere tra compresa tra 0 e 23";
			  	result = false;
			  }
				if(result && (minute < 0 || minute > 59)){
			  	messaggio.value="I minuti devono essere compresi tra 0 e 59";
			 		result = false;
		  	}
			} else {
				var hour = matchArray[1];
				var minute = 0;
				if(hour < 0  || hour > 23) {
    		  	   messaggio.value="L' ora deve essere tra compresa tra 0 e 23";
    			   result = false;
                }
			}
		  valore.value = formatNumber(hour,2,'0') + ":" + formatNumber(minute,2,'0');
		}
	} else {
		result = false;
	}
	return result;
}

function setPostValueToInput(nome,valore){
	var obj=getObjectById(nome);
	if(obj!=null){
		//alert("obj: "+obj+"="+valore);
		obj.value=valore;
	}
}
function setValueToInput(obj,valore){
	
	// Con internet explorer devo settare il valore in post altrimenti blocca l'onChange
	if(ie4){
		if(valore instanceof String)
			valore=valore.replace("\"","\\\"");
		setTimeout("setPostValueToInput(\""+obj.name+"\",\""+valore+"\");",10);
	}else{
		obj.value=valore;
	}
}

function CampoObj(obj) {
	var className;
	this.className="CampoObj";
	/**************************************************************
	 Classe che gestisce un campo di una form
	 @author Marco Franceschin
	 @changelog
		26.09.2006: M.F. Prima Versione
	***************************************************************/
	// Vecchio valore e valore nuovo
	var oldVal;
	var originalVal;
	var preChange;
	var obj;
	var archivio=null;
	// Tipo di campo:
	// T testo normale
	// N Numerico
	// F Decimale
	// D Data
	var tipo="";
	// Dimensione
	var len=100;
	// Decimali
	var dec=0;
	// Eventuale funzione di validazione
	var fnValidazione=null;
	// Puntatore all'elemento Form
	var form;
	// Dominio del campo
	var dominio=null;
	
	this.setDominio=function(aDominio){
		this.dominio=aDominio;
	}

	this.setValueView=function(viewVal){
		
		var view=getObjectById(obj.name+"edit");
		if(view!=null){
      view.value=this.getValue();
      return;
    }
    view=getObjectById(obj.name+"view");
		// Se ho ritrovato l'elemento setto il valore
		if(view!=null){
			view.innerHTML=viewVal;
		}
	}
  //
  //	Funzione che esegue il flus del value sui dati vecchi
  //
  this.flush=function(){
		//outMsgDebug("CampoObj.flush: oldVal: "+this.oldVal+" val: "+this.getValue());
  	this.oldVal=this.getValue();
		this.setValueView(this.getValue());
  }
	//
	//	Funzione che ripristina il valore precedente
	//
	this.reset=function(){
		//outMsgDebug("CampoObj.reset: "+obj.name+"oldVal: "+this.oldVal+" val: "+this.getValue());
		this.setValue(this.oldVal);
	}
	//
	// Funzione che estrae il valore del campo
	//
	this.getValue=function(){
		if(this.obj.type=="checkbox"){
			if(this.obj.checked)
				return "1";
			else
				return "2";
		}
		return this.obj.value;
	}
	//
	//	Funzione che setta il valore del campo
	//
	this.setValue=function(valore,impostaPre,subito){
		/***********************************************************
			Funzione che setta il valore del campo
			@param valore Valore del campo da impostare
			@param impostaPre Flag per impostare il valore precedente
				default true
			@param subito Flag per dire di settare immediatamente il valore (perche su internet explorer se viene settato subito allora blocca onChange).
				default false
			@changelog
				01/12/2006 M.F. Prima versione
		 ***********************************************************/
		 if(this.obj.type=="checkbox"){
			if(valore=="1")
				this.obj.checked=true;
			else
				this.obj.checked=false;
		}else{
			
			if(subito==null || !subito ){	
				setValueToInput(this.obj,valore);
			}else
				this.obj.value=valore;
		}
		
    if(impostaPre==null || impostaPre){
			this.preChange=valore;
    }
	}
	
//	//
//	//		Funzione che scatta durante la modifica
//	//
//	this.onChanging=function(){
//		// {M.F. 14.11.2006} Se si tratta di internet explorer devo togliere un carattere
//		lLen=this.len - (ie4 ? 1: 0);
//		if(this.tipo=="F")
//			lLen+=1+this.dec;
//		//this.obj.blur();
//		lVal=this.getValue();
//		
//		if(lVal.length>lLen){
//			//alert("superato "+lLen+" dim: prima:"+ this.preChange+" adesso: "+lVal);
//			// Si ha superato la lunghezza minima
//			this.setValue(this.preChange.substr(0,lLen));
//			return false;
//		}
//		this.preChange=this.getValue();
//		return true;
//	}

	//
	//	Funzione che viene chiamata al cambiamento del dato
	//
	this.onChange=function(aSetOld){
		var lVal;
		var msg;
		
		
		lVal=this.getValue();
		
		//outMsgDebug("onChange: "+this.obj.name+" old: "+this.oldVal+" new:" + lVal+ " original: "+this.originalVal);
		// Non eseguo il controllo se il valore ? vuoto
		
		if(lVal!="" && this.fnValidazione!=null){
			var msg=new Ref("");
			var refVal=new Ref(lVal);
			
			if(!this.fnValidazione(refVal,msg,this)){
				alert("Attenzione: \nSi e' inserito un valore di campo non consentito !\n"+
					msg.value);
				// Ripristino il valore precedente
				this.setValue(this.oldVal);
				return false;
			}
			// Se il valore ? stato cambiato allora setto il valore appena cambiato
			if(refVal.value!=lVal){
				lVal=refVal.value;
				this.setValue(lVal,true,true);
			}
		}
		
		// Salvo il vecchio valore
    if(aSetOld==null || aSetOld){
			//outMsgDebug("CampoObj.onChange: oldVal: "+this.oldVal+"<-"+lVal);
			this.oldVal=lVal;
    }
			
		
		this.preChange=lVal;
		return true;
	}
	//
	//	funzione che imposta un tipo di campo
	//
	this.setTipo=function(tipoCampo){
		var lval;
		var llen=100,ldec=0;
		if(tipoCampo.length>1){
			tmp=tipoCampo.substr(1);
			try{
				lval=eval(tmp);
				llen=Math.floor(lval);
				ldec=(lval*10)%10;
			}catch(e){
			}
			this.len=llen;
			this.dec=ldec;
			//alert(this.obj.name+" Dec:"+this.len+"."+this.dec);
		}
		switch(tipoCampo.substr(0,1)){
			case "T": // Testo normale
				//this.obj.maxlength=this.len;
				if(this.dominio == "ORA") {
					this.fnValidazione=checkOra;
				}
				if(this.dominio != "CLOB") {
					this.obj.setAttribute('maxlength', this.len);
				}
				this.tipo="T";
				break;
			case "N": // Numerico
				this.fnValidazione=checkNumber;			
				this.tipo="N";
				this.obj.setAttribute('maxlength', this.len);
				break;
			case "F": // Decimale
				if (this.dominio == "MONEY" || this.dominio == "MONEY5")
					this.fnValidazione=checkMoney;
				else
					this.fnValidazione=checkFloat;
				this.obj.setAttribute('maxlength', this.len+1+this.dec);
				this.tipo="F";
				break;
			case "D": // Data
				this.fnValidazione=checkData;
				this.tipo="D";
				// Setto al massimo 10 caratteri
				this.len=10;
				this.obj.setAttribute('maxlength', this.len);
				break;
		}
	}
	
	this.setFnValidazione=function(funcValidazione){
		this.fnValidazione = funcValidazione;
	}

	// Blocco di codice che esegue l'inizializzazione del campo
	this.obj=obj;
	this.oldVal=this.getValue();
	this.originalVal=this.oldVal;
	this.preChange=this.oldVal;	
	
}

function ValoreView(valore,valView){
	var className;
	this.className="ValoreView";
	/**************************************************************	 
		Classe con il valore con sggiunta anche il valore per la visualizzazione
		@param valore Valore effettivo
		@param valView Valore da mettere per la visualizzazione
		@author Marco Franceschin
		@changelog
			14.11.2007: M.F. Prima Versione
	***************************************************************/
	var val;
	var view;
	this.val=valore;
	this.view=valView;
	
}
function FormObj(form){
	var className;
	this.className="FormObj";
	/**************************************************************
	Classe che gestisce una form
	 @author Marco Franceschin
	 @changelog
		26.09.2006: M.F. Prima Versione
	***************************************************************/
	var campi=new Array();
	// Elenco dei controlli
	var checks=new Array();
	// Elenco dei campi calcolati
	var functions=new Array();
	// Elenco delle funzioni sulle proprieta
	var functionsHtml=new Array();
	// Puntatore alla form
	var parentForm=form;
	// Stack di modifica
	var stackMod=null;
	// Campo cambiato che ha fatto cambiare la nodifica
	var campoChanged=null;
	
	//
	// Funzione che estrae un elemento dal nome
	//
	this.getCampo=function(nomeOggetto){
		// Funzione che si verifica quando viene cambiato un oggetto
		// delle form
		for(i=0; i<campi.length; i++){
		
			try{
				if(nomeOggetto==campi[i].obj.name){
					// Trovato l'oggetto. chiamo il change sull'oggetto
					return campi[i];
				}
			}catch(e){
				outMsgDebug("ERR: "+i+" "+nomeOggetto+" err: "+e.message);
			}
			
		}
		return null;
	}
	
	this.setValue=function(nomeCampo,valore, verifica){
		/***********************************************************
			Funzione che esegue il settaggio del valore di un campo
			@param nomeCampo
				Nome del campo
			@param valore
				Valore del campo
			@param verifica Flag per dire di eseguire i relativii calcoli e verifiche (true di default)
			@changelog
				07/11/2006 M.F. Prima versione
				01/12/2006 M.F. Aggiunta del calcolo al settaggio del valore
		 ***********************************************************/
		var ok=true;
		var lObj=this.getCampo(nomeCampo);
		var setStack=false;
		var localStack=null;

		if(this.stackMod==null){
			this.stackMod=";";
			setStack=true;
		}else{
			localStack=this.stackMod;
		}
		// Non setto il campo se gia settato
		if(this.stackMod.indexOf(";"+nomeCampo+";")>0)
			return true;
		this.stackMod+=nomeCampo+";";
		if(lObj!=null){
			var objVal=null;

			if(valore instanceof Object && valore.className !=null && valore.className=="ValoreView"){
				objVal=valore;
				valore=valore.val;
			}
			if(verifica==null)
				verifica=true;

			var preValue=lObj.getValue();

			// alert("Setto: "+nomeCampo+"="+valore);
			lObj.setValue(valore,true,true);
			if(verifica)
				ok=this.checkCampo(nomeCampo); //,false);
			//outMsg("ok? "+ok);
			// Aggiungo gli eventuali calcoli
			if(ok && verifica){
				//outMsg("eseguo calcoli");
				if(!this.calcolaCampi(nomeCampo,localStack))
					ok=false;
			}
			if(ok && verifica){
				//outMsg("eseguo calcoli HTML");
				if(!this.calcolaHtml(nomeCampo))
					ok=false;
			}
			//outMsgDebug("----");
			if(ok || ! verifica){
				lObj.flush();
				if(objVal != null){
					lObj.setValueView(objVal.view);
				}
			}//else{
				//lObj.reset();
			//}

			//outMsgDebug("setValue: "+nomeCampo+"="+valore+" ok ? "+ok+" preValue: "+preValue+" verifica? "+verifica);
		}
		this.stackMod=this.stackMod.replace(nomeCampo+";","");
		if(setStack)this.stackMod=null;
		return ok;
	}
  
  //
  //	Funzione che gestisce i controlli su un campo
  //
  this.checkCampo=function(nomeCampo,verificaNonObbligatori){
  	var dim,check;
    var lObj=this.getCampo(nomeCampo);
		var i;
    // Se non trovo il campo restituisco true (come fosse valido)
    if(lObj==null)
    	return true;
  	dim=checks.length;
  	// Ciclo tutti i controlli
  	for(i=0;i<dim;i++){
  		check=checks[i];
  		// Verifico che il check sia sul campo voluto
 			if(check.nome==nomeCampo){
 				// Se non e' verificato calcolo il messaggio e lo stampo a video
 				if(!check.check()){
       		try{
						lmsg=this.replaceValuesEval(check.msg,nomeCampo);
          }catch(e){
 	        	outMsg("FormObj.onChange:"+e.message,"ERR");
   	      }
     	    // Verifico se si tratta di una formula obbligatoria
       	  //outMsg("OnChange: "+check.obblig);
          if(!check.obblig){
 	        	if(verificaNonObbligatori==null || verificaNonObbligatori){
   	        	if(!confirm(lmsg+"\nConfermi ugualmente ?")){
     	        	lObj.reset();
       	      	return false;
         	    }
           	}
          } else {
			     	if(check.onsubmit){
			     	// Il controllo del campo deve avvenire subito prima del submit
			     	// del campo e rimando il check del campo alla funzione
			     	// FormObj.onsubmit() e quindi in questo caso esco ritornando true
			     	// come se il controllo del campo (effettuato poco sopra con
			     	// l'istruzione check.check()) abbia dato esito true
			     		return true;
  	       	} else {
    	     		// Il controllo del campo deve avvenire all'onchange del campo. In
    	     		// questo il controllo del campo (effettuato poco sopra con
			     	  // l'istruzione check.check()) ha dato esito false, allora mostro
			     	  // il messaggio di errore, resetto il campo e ritorno false
							alert(lmsg);
						  lObj.reset();
              return false;
          	}
						//alert(lmsg);
						//lObj.reset();
            //return false;
            // siccome ? stato commentato il reset e l'uscita con errore, e siccome
            // non deve scattare niente, quantomeno esco con true
            //return true;
          }
  			}
  		}
  	}
    return true;
  }

	//
	// Funzione che gestisce un cambiamento di un campo
	//
	this.onChange=function (obj){
		
		// Funzione che si verifica quando viene cambiato un oggetto delle form
		var lObj=this.getCampo(obj.name);
		var fn;
		var ok=true;
		var setStack=false;
		var localStack=null;
		
		this.stackMod=";";
		setStack=true;
		localStack=this.stackMod;
		
		// Non setto il campo se gia settato
		if(this.stackMod.indexOf(";"+obj.name+";")>0)
			return true;
		this.stackMod+=obj.name+";";
		
		if(lObj!=null){
			this.campoChanged=obj.name;
			if(!lObj.onChange(false))
				return false;

			if(!this.checkCampo(obj.name)){
				if(setStack)
					this.stackMod=null;
				return false;
			}
			
			// {MF021106} Aggiungo il richiamo della modifica all'archivio se esistente
			if(lObj.archivio!=null){
				if(!lObj.archivio.onChange(lObj.obj.name)){
					if(setStack)this.stackMod=null;
						return false;
				}
			}
			// A questo punto eseguo il flush del valore come voluto
			
			// Lancio il ricalcolo sui campi collegati
      if(!this.calcolaCampi(obj.name,localStack))
				ok=false;
			if(ok && !this.calcolaHtml(obj.name))
				ok=false;
			
			//outMsgDebug("change: "+lObj.obj.name+" ok ?"+ok);
			
			if(ok)
				lObj.flush();
			//else
			//	lObj.reset();
		}
		if(setStack)this.stackMod=null;
		return ok;
	}
	
//	this.onChanging=function(obj){
//		var lObj=this.getCampo(obj.name);
//		if(lObj!=null){
//			return lObj.onChanging();
//		}
//		return true;
//	}

	//
	//	Funzione che imposta un tipo di campo
	//
	this.setTipo=function(nomeCampo,tipoCampo){
		// Non setto il tipo se non conforme
		if(tipoCampo=="")
			return;
		var lObj=this.getCampo(nomeCampo);
		
		if(lObj!=null){
			
			lObj.setTipo(tipoCampo);
		}else if(this.getCampo(nomeCampo+".0")!=null){			
			// Setto il tipo per tutti gli elementi
			var i=0;
			do{
				lObj=this.getCampo(nomeCampo+"."+i);
				if(lObj!=null)
					lObj.setTipo(tipoCampo);
				i++;
			}while(lObj!=null);
		}else{
			outMsg("SetTipo : "+nomeCampo+"="+tipoCampo+" il campo non esiste nella form !","ERR");
		}
	}
	//
	//	Funzione che imposta un tipo di dominio
	//
	this.setDominio=function(nomeCampo,tipoDominio){
		// Non setto il dominio se non conforme
		if(tipoDominio=="")
			return;
		var lObj=this.getCampo(nomeCampo);
		
		if(lObj!=null){
			
			lObj.setDominio(tipoDominio);
		}else if(this.getCampo(nomeCampo+".0")!=null){			
			// Setto il tipo per tutti gli elementi
			var i=0;
			do{
				lObj=this.getCampo(nomeCampo+"."+i);
				if(lObj!=null)
					lObj.setDominio(tipoDominio);
				i++;
			}while(lObj!=null);
		}else{
			outMsg("SetDominio : "+nomeCampo+"="+tipoDominio+" il campo non esiste nella form !","ERR");
		}
	}
	//
	//	funzione che estrae il valore di un campo
	//
	this.getValue=function(nomeCampo){
		var lObj=this.getCampo(nomeCampo);
		if(lObj!=null){
			return lObj.getValue();
		}
		return "";
	}
	//
	//		Funzione che aggiunge un espressione di controllo
	//
	this.addCheck=function(acampo,aformula,amsg,aobblig,aonsubmit){
		// Aggiungo il campo solo se esiste
		if(this.getCampo(acampo)!=null){
			checks.push(new Check(acampo,aformula,amsg,aobblig,aonsubmit,this));
		}
	}
	
  //
  //	Funzione che fa il replace dei valori e se la stringa inizia con ! allora 
  //	interpreta anche l'eventuale formula
  //
  this.replaceValuesEval=function(stringa,nome){
  	var lret;
    lret=this.replaceValues(stringa,nome);
		//outMsgDebug("replaceValuesEval: "+stringa+"="+lret);
    if(lret.substr(0,1)=="!"){
    	try{
    		//L.G.21/08/09: riga modificata per ovviare ad un errore javascript che si otteneva
    		//nel caso si doveva valutare la variabile lret.substr(1), il cui valore
    		//era relativo ad una textarea con il testo suddiviso su piu' righe (cioe' il testo 
    		//presenta i caratteri '\r' e '\n'). Infatti se alla funzione eval si applica una stringa
    		//che presenta i suddetti caratteri si verifica l'errore JS di stringa non terminata.
    		//Per ovviare, si sostituiscono tutti i caratteri '\r' e '\n' con il carattere ' '
	    	lret=eval(lret.substr(1).replace(/\r/g, " ").replace(/\n/g, " "));
      }catch(e){
      	outMsg("replaceValuesEval errore nella formula: "+stringa+"--><b>"+lret+"</b><br/>Del campo:"+nome+"<br/>"+e.message,"ERR");
        throw e.message;       	
      }
    }
    return lret;
  }
  //
	// Funzione che esegue il replace dei valori in una stringa
  //
	this.replaceValues=function(stringa,nome){
  	// Inizializzazioni
  	var i, k, lval, returnval, ldivs;

		if(stringa.indexOf("#")>=0){
			stringa=stringa.replace("[#]","[<_DIV_>]");
			//alert("Converto Stringa:"+stringa);
			if(stringa.indexOf("#") != stringa.lastIndexOf("#")){ // se la stringa contiene piu' di una volta il carattere #
				ldivs=stringa.split("#");
				returnval="";
				for(i=0;i<ldivs.length;i+=2){
					returnval+=ldivs[i];
					if(ldivs[i+1]=="")ldivs[i+1]=nome;
					lval=this.getValue(ldivs[i+1]);
					//alert("Campo: "+ldivs[i+1]+"="+lval)
					// Trasformo il valore per metterlo all'interno di un eventuale stringa
					if(lval.indexOf("\\")>=0 || lval.indexOf("\"")>=0){
						ltmp="";
						for(k=0;k<lval.length;k++){
							switch(lval.substr(k,1)){
								case "\\":
								case "\"":
									ltmp+="\\";
									break
							}
							ltmp+=lval.substr(k,1);
						}
						lval=ltmp;
					}
					returnval+=lval;
					
				}
				//alert("Ret="+returnval);
			}
			if(returnval != null && returnval != "")
				return returnval.replace("[<_DIV_>]","#");
			else
				return stringa.replace("[<_DIV_>]","#");
		}
		return stringa;
	}
	
	this.calcola=function(nomeCampo, conVerifica){
		/**************************************************************	 
			Funzione che esegue il calcolo esistente su un determinato campo
			@param nomeCampo Nome del campo su cui eseguire il calcolo
			@param conVerifica Flag che dice di ricalcolare anche le formule collegate (default ? false))
			@author Marco Franceschin
			@changelog
				14.11.2007: M.F. Prima Versione
		***************************************************************/
		var i,fn;
		
		if(conVerifica==null)
			conVerifica=false;
		// Scorro tutte le funzioni esistenti
		for(i=0;i<functions.length;i++){
			// Se una formula ha fatto scattare il calcolo non continua
    	fn=functions[i];
			if(fn.nome==nomeCampo){
				if(fn.calcola(conVerifica)){
					if(conVerifica)
		       	 return this.calcolaCampi(nomeCampo,";"+nomeCampo+";");
					return true;
				}else
					return false;
			}
		}
		return false;
	}
  this.calcolaCampi=function(campoMod,stack){
    /**************************************************************
    		Funzione che esegui il ricalcolo di tutti i campi collegati alla modifica
        di un campo.
    	@param
      	campoMod
        	Campo che ? stato modificato
        stack
        	Stack di calcolo per impedire il loop infinito
    	@author Marco Franceschin
    	@changelog
    		26.09.2006: M.F. Prima Versione
    ***************************************************************/
    // Inizializzazioni
    var i,fn;
    if(stack==null){
    	stack=";";
		}
		// Se il calcolo ? nello stack di calcolo allora non lo rieseguo altrimenti
    // si va a loop infinito facendo spaccare il browser
    if(stack.indexOf(";"+campoMod+";")>=0)
    	return true;
		stack+=campoMod+";";
		// Scorro tutte le formule per il calcolo
    for(i=0;i<functions.length;i++){
			// Se una formula ha fatto scattare il calcolo non continua
    	fn=functions[i];
      // Verifico se la formula viene scatenata alla modifica del campo
    	if(String(";"+fn.campimod.join(";")+";").indexOf(";"+campoMod+";")>=0){
				// Prima di eseguire il calcolo verifico che non si trovi gia nello stack
				if(stack.indexOf(";"+fn.nome+";")<0){
					// Eseguo il calcolo solo se la destinazione non ? gia nello stack
					//outMsgDebug("Calcolo: "+fn.nome+" <- "+fn.formula+" ["+campoMod+"] "+stack);
		      // A questo punto devo calcolare la formula
		      if(fn.calcola()){
		       	// Se il calcolo ? andato a buon fine 
		         // richiamo l'eventuale calcolo delle sottoformula
		         this.calcolaCampi(fn.nome,stack);
					}else{
						return false;
					}
					stack+=fn.nome+";";
				}
      }
    }
		return true;
  }
  this.addCalcolo=function(acampo,aformula,acampiMod){
    /**************************************************************
    		Funzione che aggiunge un ricalcolo dulla form
    	@param
      	acampo
        	Nome del campo
        aformula
        	Formula per il ricalcolo del campo
        acampiMod
        	Campi che scatenano la modifica
    	@return
      	true Ok; false se c'? un errore
    	@author Marco Franceschin
    	@changelog
    		26.09.2006: M.F. Prima Versione
    ***************************************************************/
    // Aggiungo il campo solo se esiste
		if(this.getCampo(acampo)!=null){
    	// Aggingo il calcolo
			functions.push(new Calcolo(acampo,aformula,acampiMod,this));
      return false;
		}
    return false;    
  }
	
	/***********************************************************
		Funzioni che gestiscono il calcolo delle proprieta HTML
		@changelog
			07/11/2006 M.F. Prima versione
	 ***********************************************************/

	this.calcolaHtml=function(nomeCampo){
		var i, fn;
		for(i=0;i<functionsHtml.length;i++){
			fn=functionsHtml[i];
			if(!fn.calcola(nomeCampo))
				return false;
		}
		return true;
	}
	this.addCalcoloHtml=function(fn,campichanged, calcolaStart){
		// Aggiungo il calcolo
		var lcal=new CalcoloHtml(fn, campichanged, calcolaStart, this);
		functionsHtml.push(lcal);
		lcal.calcola(null);
	}
	
	this.onsubmit=function(){
		/***********************************************************
			Funzione che scatta prima del SUBMIT 
			@changelog
				24/10/2006 M.F. Prima versione
		 ***********************************************************/

		var ritorno=true;
		//alert("onSubmit:"+parentForm.metodo.value);
		// Se siamo in update allora verifico prima tutti i check
		if(parentForm.metodo.value == "update"){
			var dim,check,i;
			
			clearMsg();
			//outMsg("Inizio Controllo: "+checks.length);
			dim=checks.length;
	  	// Ciclo tutti i controlli
	  	for(i=0;i<dim;i++){
				
	  		check=checks[i];
				//outMsg(i+":"+check.nome+"->"+check.formula+" obblig: "+check.obblig);
				// Se il controllo e' obbligatorio al submit del form allora lo verifico
				// si effettua il controllo se ? obbligatorio, va fatto al submit, il campo ? visibile, e non ? relativo a
				// una sezione a scheda multipla visibile ma interamente vuota (per cui ? come se fosse nascosta)
	  		if(check.obblig && check.onsubmit && (document.getElementById("row" + check.nome) != null && document.getElementById("row" + check.nome).style.display != "none") && !check.skipControlloObblig){
					if(!check.check()) {
						try {
							lmsg=this.replaceValuesEval(check.msg,check.nome);
						} catch(e) {
							outMsg("FormObj.onChange:"+e.message,"ERR");
						}
						outMsg("<a href=\"javascript:selezionaCampo('"+check.nome+"');\""+
							" title=\"Seleziona il campo\" style=\"color: #ff0000;\">"+lmsg+"</<>","ERR");
						// Stampo il messaggio
						ritorno=false;
					}
	  		}
	  	}
			if(!ritorno){
				onOffMsgFlag(true);
				alert("Si sono verificati degli errori durante i controlli sui campi !");
			}
	    return ritorno;
		}
		return ritorno;
	}

	this.getChecksArray=function(){
		return checks;
	}
	
	this.addCampo=function(campo){
		var idx=campi.length;
		campi[idx]=new CampoObj(campo);
		campi[idx].form=this;
	}
	
	this.getFormName=function(){
		return parentForm.name;
	}
	
	// Inizializzazione della classe FormObj
	
	for(i=0; i<form.elements.length; i++){
		// Se l'oggetto ? ok allora lo aggiungo (solo per il primo elemento dell'arrai)
		var idx=campi.length;
		campi[idx]=new CampoObj(form.elements[i]);
		campi[idx].form=this;
		//outMsgDebug("Add: "+idx+"<-"+form.elements[i].name);
	}
	activeForm=this;

}

function Check(anome,aformula,amsg,aobblig,aonsubmit,aform){
	var className;
	this.className="Check";
	var nome;
	var formula;
	var msg;
	var obblig;
	var onsubmit;
	var skipControlloObblig;
	var form;
	
	this.check=function(){
		
		var retCheck=true;
		try{
      retCheck=this.form.replaceValuesEval("!"+this.formula,this.nome);
		}catch(e){
			outMsg("Check.check: Errore nella formula: "+this.formula+"<br/>"+e.message,"ERR");
		}
    //outMsg("Check.check: formula="+this.formula+", msg="+this.msg+"obblig="+this.obblig+" result: "+retCheck);
		// Ritorna true se la formula e' stata validata, false altrimenti
		return retCheck;
	}
	
	// Inizializzazione
	this.nome=anome;
	this.formula=aformula;
	this.msg=amsg;
	this.obblig=aobblig;
	this.form=aform;
	this.onsubmit=aonsubmit;
	this.skipControlloObblig=false;
	
	this.getNome=function(){
		return this.nome;
	}
	
	this.isObbligatorio=function(){
		return this.obblig;
	}

	this.setSkipControlloObblig=function(skip){
		this.skipControlloObblig = skip;
	}
}

function Calcolo(acampo, aformula, acampimod, aform){
	var className;
	this.className="Calcolo";
	/**************************************************************
  		Oggetto che gestisce i calcoli automatici sui campi della
      form
  	@changelog
  		26.09.2006: M.F. Prima Versione
  ***************************************************************/
	
	var nome;
  var formula;
  var campimod;
  var form;
  
  this.calcola=function(aConVerifica){
  	/**************************************************************
	 			Funzione che esegue il calcolo del campo e lo setta all'oggetto
    	@author Marco Franceschin
    	@changelog
    		26.09.2006: M.F. Prima Versione
    ***************************************************************/
    var lris,obj;
		var ret=true;
		var conVerifica;
		
		conVerifica=aConVerifica == null ? true : aConVerifica;
		//outMsgDebug("calcola: "+this.nome+" = "+this.formula+" ["+this.campimod+"]");
    // Eseguo il 
    try {
	    lris=this.form.replaceValuesEval("!"+this.formula);
			/*
			if(lris instanceof Object && lris.className=="ValoreView")
				outMsgDebug("calcolo: "+this.nome+"="+lris.val+" view: "+lris.view);
			else
				outMsgDebug("calcolo: "+this.nome+"="+lris);
			*/
			// Setto il campo con le varie verifiche d'integrit?
			if(!this.form.setValue(this.nome,lris,conVerifica))
				ret=false;
    }catch(e){
    	// Stampo l'errore 
    	outMsg("Calcolo.calcola:<b>"+lris+"</b>"+e.message,"ERR");
    }
		//outMsg("Calcolo.calcola: nome="+this.nome+", formula="+this.formula+", campimod="+this.campimod+" ok? "+ret);
    return ret;
  }
  
  // Inizializzo il calcolo
  this.form=aform;
  this.nome=acampo;
  this.formula=aformula;
  this.campimod=acampimod;

}

function CalcoloHtml(aFormula, aCampiMod, aCalcolaStart, aForm){
	var className;
	this.className="CalcoloHtml";
	/***********************************************************
		Classe che gestisce il calcolo di una propriet? al variare di un campo nella maschera
		In:
			aProprieta Propriet? da settare
			aFormula Formula per il calcolo della propriet?
			aCampiMod Elenco dei campi divisi da ; che fanno scattare il ricalcolo della propriet?
			aForm Form di tipo ObjForm
			aCalcolaStart Flag per dire di eseguire il calcolo anche all'avvio delle formula
		@changelog
			03/11/2006 M.F. Prima versione
	 ***********************************************************/
	var formula;
	var campiMod;
	var form;
	var calcolaStart;
	
	this.calcola=function(campoMod){
		// Eseguo il calcolo solo se scatenato dalla modifica di un campo lincato ad esso
		if((campoMod==null && this.calcolaStart) || (";"+this.campiMod+";").indexOf(";"+campoMod+";")>=0){	
			//outMsg("CalcoloHtml.calcola(\""+campoMod+"\")->");
	    // Eseguo il calcolo
	    try {
		    var ret=this.form.replaceValuesEval("!"+this.formula);
				//alert(ret);
				if(ret != null && ret instanceof Boolean)
					return ret;
	    }catch(e){
	    	// Stampo l'errore 
	    	outMsg("CalcoloPropHtml.calcola:"+e.message,"ERR");
	    }
		}
		return true;
	}
	this.form=aForm;
	this.campiMod=aCampiMod;
	this.formula=aFormula;
	this.calcolaStart=aCalcolaStart;
	//outMsg("new CalcoloHtml(\""+aFormula+"\", \""+aCampiMod+"\", \""+aForm+"\")");
}

function selezionaCampo(nomeCampo){
	/***********************************************************
		Funzione che esegue la selezione di un campo
		@changelog
			23/10/2006 M.F. Prima versione
	 ***********************************************************/
	var obj=getObjectById(nomeCampo);
	if(obj!=null){
		obj.focus();
	}
}

function ArchivioObj(aName,aForm, aLista, aScheda, aSchedaPopUp, aCampi, aKeys, aCampiNoSet, aScollegabile){
	var className;
	this.className="ArchivioObj";
	/***********************************************************
		Oggetto che esegue la gestione dell'archivio
		@changelog
			31/10/2006 M.F. Prima versione
			16/03/2007 M.F. Aggiunta dei campi che non devovo essere settati
			04/03/2010 S.S. Aggiunta flag scollegabile: in caso = true, il reset di un
			campo non causa il reset di tutti i campi, e nel caso di valorizzazione per 
			un filtro causa lo sbiancamento dei campi chiave, invisibili e non modificabili
			del filtro, e mantiene il valore inputato nel campo
	***********************************************************/
	
	var campiArchivio=new Array();
	var keys=new Array();
	var campiNoSet = null;
	var lista;
	var scheda;
	var schedaPopUp;
	var form;
	var formObj;
	var name;
	var scollegabile;
	
	this.fnScheda=function(){
		var valChiavi="";
		var i=0;
		// Verifico che la scheda sia collegata con i campi chiave
		for(i=0;i<keys.length;i++){
			lCampo=this.form.getCampo(keys[i]);
			//outMsg("Campo: "+keys[i]+"-"+lCampo);
			if(lCampo.getValue()==""){
				alert("L'archivio non e' collegato alla scheda !");
				return false;
			}
			if(i>0)
				valChiavi+=";";
			valChiavi+=getTipoCampo(lCampo.obj.name)+":";
			valChiavi+=lCampo.getValue().replace(";","\\;");
		}
		this.formObj.archValueChiave.value=valChiavi;
		this.formObj.metodo.value="scheda";
		this.submit(this.formObj.modo.value == "MODIFICA" || this.formObj.modo.value=="NUOVO");
	}
	
	this.fnLista=function(campo){
		if(campo!=null){
			for(i=0;i<campiArchivio.length;i++){
				if(campiArchivio[i].indexOf(campo)==0){
					
					// Il campo ? stato ritrovato
					campoFind=campiArchivio[i].substring(campo.length+1);
					this.formObj.archCampoChanged.value=campoFind;
					lCampo=this.form.getCampo(campo);
					this.formObj.archValueCampoChanged.value=lCampo.getValue();
					// Ora setto il tipo di campo
					this.formObj.archTipoCampoChanged.value=getTipoCampo(campo);
					if(lCampo.getValue()==""){
						if (!this.scollegabile) {
							// Se sbiancato sbianco tutti i campi collegati all'archivio
							this.sbiancaCampi(0);
						}
						return;
					}
					if (!this.scollegabile) {
						lCampo.setValue(lCampo.oldVal);
					} else {
						// nel caso di archivio scollegabile, sbianco i campi chiave e
						// tutti i campi non visibili (se li ho popolati con valori 
						// dell'archivio, ora che modifico un campo e non son piu' 
						// collegato all'archivio, allora li sbianco) e i visibili 
						// ma non editabili (idem come il precedente caso)
						for (var j = 0; j < keys.length; j++) {
							if (keys[j] != "" && keys[j] != campo)
								this.form.getCampo(keys[j].replace(".", "_")).setValue("");
						}
						this.sbiancaCampi(1);
						this.sbiancaCampi(2);
					}
				}
			}
		}else{
			this.formObj.archCampoChanged.value="";
		}
		this.formObj.metodo.value="lista";
		this.submit(true);
	}
	
	// tipoSbiancamento:
	// 0: cerca su tutti i campi
	// 1: cerca su tutti i campi non visibili
	// 2: cerca su tutti i campi visibili ma non editabili
	this.sbiancaCampi=function(tipoSbiancamento){
		
		lArrayCampi=this.formObj.archCampi.value.split(";");
		for(i11=0;i11<lArrayCampi.length;i11++){
			lCampo=this.form.getCampo(lArrayCampi[i11]);
			var setVal=true;
			if(this.campiNoSet!=null){
				if(this.campiNoSet.indexOf(";"+lArrayCampi[i11]+";")>=0)
					setVal=false;
			} else {
				if(tipoSbiancamento==null) tipoSbiancamento==0;
				setVal=false;
				switch(tipoSbiancamento){
					case 0:
						// tutti i campi vanno settati
						setVal=true;
						break;
					case 1:
						// controlla se il campo non e' visibile
						if (lCampo.obj.type == "hidden" || !isObjShow(lCampo.obj.id)) setVal=true;
						break; 
					case 2:
						// controlla se il campo e' visibile ma non editabile
						if (lCampo.obj.type != "hidden" && isObjShow(lCampo.obj.id) && (lCampo.obj.readonly || lCampo.obj.disabled)) setVal=true;
						break; 
				}
			} 
			//outMsg("Sbianco campo: "+lCampo.obj.name + " =>" + setVal);
			if(setVal)
				this.form.setValue(lArrayCampi[i11],"");			
		}
	}
	
	this.submit=function(isPopup){
		var win=null;
		var numPopUp=-1;
		var lastModo;
		lForm=getObjectById(this.name);
		lTarget=lForm.target;
		if(isPopup){
			win=openPopUp("",this.name);
			lForm.target=win.name;
			lForm.isPopUp.value="1";
			numPopUp=lForm.numeroPopUp.value;
			if(numPopUp=="")
				numPopUp=0;
			else
				numPopUp=eval(numPopUp);
			lForm.numeroPopUp.value=(numPopUp+1);
			lForm.archIsOpenInPopUp.value="1";
		}else
			lForm.archIsOpenInPopUp.value="";
		lastModo=lForm.modo.value;
		lForm.modo.value="VISUALIZZA";
		lForm.submit();
		//alert(lForm.numeroPopUp.value);
		lForm.target=lTarget;
		lForm.modo.value=lastModo;
		lForm.numeroPopUp.value=numPopUp;
		if(win!=null)
			win.focus();
	}
	
	
	this.onChange = function(campo){
		// Prima di aprire la popup dell'archivio bisogna settare la variabile globale
		// activeArchivioForm con il nome del form di tipo archivio a cui il campo e'
		// collegato attraverso il campo hidden archCampi. La ricerca viene effettuata
		// cercando il campo nel campo hidden 'archCampi' presente in ogni form di 
		// tipo archivio
		for(var s=0; s < document.forms.length; s++){
			try {
				if(eval("document." + document.forms[s].name + ".archCampi.value.indexOf(new String('" + campo + "')) >= 0")){
					activeArchivioForm = document.forms[s].name;
				}
			} catch(err) {
				// Oggetto inesistente nell's-esimo form
			}
		}
		this.fnLista(campo);
	}
	
	// Eseguo l'inizializzazione dell'oggetto
	this.lista=aLista;
	this.scheda=aScheda;
	this.schedaPopUp=aSchedaPopUp;
	//outMsg("Length:"+aCampi.length);
	for(i1=0;i1<aCampi.length;i1+=2){
		
		campiArchivio.push(aCampi[i1+1]+";"+aCampi[i1]);
		objCampo=aForm.getCampo(aCampi[i1+1]);
		// Setto l'archivio associato
		objCampo.archivio=this;
		//outMsg(objCampo.obj.name+"="+objCampo.archivio);
	}
	for(i1=0;i1<aKeys.length;i1++){
		//outMsg("Key: "+i1+" "+aKeys[i1]);
		keys.push(aKeys[i1]);
	}
	
	this.campiNoSet=aCampiNoSet;
	this.form=aForm;
	this.name=aName;
	elencoArchivi.push(this);
	this.formObj=getObjectById(this.name);
	this.scollegabile=aScollegabile;
	/*
	outMsg("Nome:"+this.name);
	outMsg("Lista:"+this.lista);
	outMsg("Scheda:"+this.scheda);
	outMsg("Scheda PopUp:"+this.schedaPopUp);
	outMsg("Keys:"+keys.join(";"));
	outMsg("Campi:"+campiArchivio.join(";"));
	*/
}

function getArchivio(nomeArchivio){
	for(i=0;i<elencoArchivi.length;i++){
		if(elencoArchivi[i].name==nomeArchivio)
			return elencoArchivi[i];
	}
	return null;
}
///////////////////////////////////////////////////////////////////////////////////
// Funzioni generali per la selezione e la visualizzazione delle scheda
///////////////////////////////////////////////////////////////////////////////////
function archivioLista(nomeArchivio){

	activeArchivioForm = nomeArchivio;
	// Funzione che visualizza la lista senza filtro
	arch=getArchivio(nomeArchivio);
	arch.fnLista(null);
}

function archivioScheda(nomeArchivio){
	// Funzione che visualizza la scheda dell'archivio
	arch=getArchivio(nomeArchivio);
	arch.fnScheda();
}
function getTipoCampo(campo){
	/***********************************************************
		Funzione che estrae il tipo di campo partendo dal nome del campo
		@changelog
			03/11/2006 M.F. Prima versione
			14/11/2006 M.F. Non prende in considerazione se enumerato
	 ***********************************************************/
	 campoDef=getObjectById("def"+campo);
	 var campoValue = null;
	 if (campoDef == null) {
		 // nel caso in cui si attribuisca un id al campo, si cerca per name
		 campoDef = $('[name^="def'+campo+'"]');
		if (campoDef != null) {
			campoValue = campoDef.val();
		}
	 } else {
		 campoValue = campoDef.value;
	 }
	 if(campoValue!=null){
			// Se ho trovato la definizione del campo allora la estraggo
			arrayDef=campoValue.split(";");
			if(arrayDef.length>=2){
				if(arrayDef[1].substring(0,1)=="E")
					return arrayDef[1].substring(1,2);
				else
					return arrayDef[1].substring(0,1);
			}
	 }
	 // Di default ? testo
	 return "T";
}

function setValue(nomeCampo, valore, check){
	/***********************************************************
		Funzione che esegue il settaggio di un campo.
		@param nomeCampo
			Nome del campo da settare
		@param valore
			Valore da settare sul campo
		@param check
			Flag per dire di eseguire il check. true di default
		@changelog
			22/11/2006 M.F. Prima versione
			23/02/2007 M.F. Per luca se non esiste la form allora lancio un'eccezione 
				(perche sul settaggio da calendario si aspetta l'eccezione)
	 ***********************************************************/
	if(activeForm!=null){
		return activeForm.setValue(nomeCampo, valore, check);
	} else
	  throw "Form non esistente o non inizializzato";
}

  //////////////////////////////////////////
  // Nome:        setValueIfNotEmpty
  // Descrizione: setta il valore di un campo solo se il valore da settare non ? empty
  // Argomenti:
  //	nomeCampo - Nome del campo da settare
  //	valore    - Valore da settare sul campo
  //
  // Ritorna:     <e' una procedura>
  //////////////////////////////////////////
function setValueIfNotEmpty(nomeCampo, valore) {
	if (valore != '')
		setValue(nomeCampo, valore);
}

  //////////////////////////////////////////
  // Nome:        sbiancaCampiSeNonValorizzato
  // Descrizione: sbianca i campi dell'elenco in input se il primo parametro ? vuoto
  // Argomenti:
  //	valoreCampo            - Valore da controllare
  //	elencoCampiDaSbiancare - elenco campi da sbiancare, separati da ";"
  //
  // Ritorna:     <e' una procedura>
  //////////////////////////////////////////
function sbiancaCampiSeNonValorizzato(valoreCampo, elencoCampiDaSbiancare) {
	if (valoreCampo=="") {
		// Se il valore ? vuoto allora eseguo lo sbianco dei campi dell'elenco
		var campi = elencoCampiDaSbiancare.split(";");
		for (var i=0; i < campi.length; i++){
			setValue(campi[i], "");
		}
	}
}

function setNumeroPopUp(){
	var i,numeroPopUp;
	var wOpener;
	var numOpener=getNumeroPopUp();
	
	// Per tutte le form setto il numero del popUp
	for(i=0;i<document.forms.length;i++){
		numeroPopUp=document.forms[i].numeroPopUp;
		if(numeroPopUp!=null){
			numeroPopUp.value=numOpener;
		}
	}
}


// Funzione che esegue l'apertura della popUp con l'help del mnemonico
function helpMnemonico(mnemonico){
	openPopUp("href=commons/helpMnemonico.jsp&mnemonico=" + mnemonico, "helpMenmonico", 600, 500, "no","no");
}

function helpDiPagina(idPagina){
  openPopUp("href=commons/helpDiPagina.jsp&idPagina=" + idPagina, "helpMenmonico", 600, 500, "no","no");
}

function checkInputLength(unaText,maxLength){
	/**************************************************************	 
		@param	campo
			campo nel quale controllare la grandezza massima
		@param
			Dimensione massima in caratteri
		@return
			true Caratteri apposto
			false Numero di caratteri errato
		@author Marco Franceschin
		@changelog
			15.01.2007: M.F. Prima Versione
	***************************************************************/
	//F.D. 27/02/08 viene commentato il corpo della funzione
	//in quanto ? stato riscontrato un errore nel conteggio per
	//firefox (gli accapo sono conteggiati 1 carattere anzich? 2)
  // Initialise the CaretPosition object
	//var position=getCaretPosition(campoInput);
	//if(campoInput.value.length>maxlength){
	//	campoInput.value=campoInput.value.substring(0,maxlength);
	//	if(position<campoInput.value.length){
	//		setCaretPosition(campoInput,position);
	//	}
	//alert("E' stato raggiunto il limite massimo di "+maxlength+" caratteri consentiti");
    //return false;
	//}
	//return true;
	//F.D. 27/02/08 nuova versione
	var stringa = unaText.value;
	var lunghezza = stringa.length;
	//distinguiamo fra firefox ed explorer
	if (ns6 || ns4) {
		//creo un array effettuando lo split per ogni
		//accapo (la lunghezza dell'array meno 1 sar? il numero di \n)
		var sVett = stringa.split("\n");
		if (sVett.length >1) {
		//aggiungo al conteggio fatto da firefox 1 carattere per ogni \n
			lunghezza += sVett.length -1 ;
		}
		//se lo \n ? l'ultimo carattere inserito devo gestire il fatto 
		//che non ci sono altri caratteri
		if (sVett[sVett.length-1] != '')
			caratteri = maxLength + 1 - sVett.length;
		else
			caratteri = maxLength + 1 - (sVett.length-1);
	}
	else {
	//per explorer ? tutto normale
		caratteri = maxLength;
	}
	if (lunghezza > maxLength) {
		unaText.value = stringa.substring(0,caratteri);
		alert("E' stato raggiunto il limite massimo di " + maxLength + " caratteri consentiti");
    		return false;
		}
	return true;
}

function getCaretPosition (ctrl) {
	/**************************************************************	 
		Funzione che estrae la posizione del cursode nella textarea o nell'input
		@param ctrl
			Controllo da cui estrarre la posizione
		@return	Posizione
		@author Marco Franceschin
		@changelog
			15.01.2007: M.F. Prima Versione
	***************************************************************/
	var CaretPos = 0;
	// IE Support
	//if(ctrl.createTextRange){
	if (document.selection) {

		// The current selection 
		var range = document.selection.createRange(); 
		// We'll use this as a 'dummy' 
		var stored_range = range.duplicate(); 
		// Select all text 
		stored_range.moveToElementText( ctrl ); 
		// Now move 'dummy' end point to end point of original range 
		stored_range.setEndPoint( 'EndToEnd', range ); 
		
		// Now we can calculate start and end points 
		CaretPos = stored_range.text.length ; 
		
	}
	// Firefox support
	else if (ctrl.selectionStart || ctrl.selectionStart == '0')
		CaretPos = ctrl.selectionStart;
	return (CaretPos);

}


function setCaretPosition(ctrl, pos)
{
	/**************************************************************	 
		Funzione che setta la posizione del cursore in un edit
		@param ctrl
			Controllo in cui settare la posizione
		@param  pos
			Posizione da settare
		@author Marco Franceschin
		@changelog
			15.01.2007: M.F. Prima Versione
	***************************************************************/
	
	if(ctrl.setSelectionRange)
	{
		ctrl.focus();
		ctrl.setSelectionRange(pos,pos);
	}else if (ctrl.createTextRange) {
		var range = ctrl.createTextRange();
		// Elimino un carattere per ogni enter
		var txt=range.text.substring(0,pos);
		var posIdx=0;
		do{
			posIdx=txt.indexOf("\n");
			if(posIdx>=0){
				pos --;
				txt=txt.substring(posIdx+1);
			}
		}while(posIdx>=0);
		
		range.collapse(true);
		range.moveEnd('character', pos);
		range.moveStart('character', pos);
		range.select();
	}
}

function getValue(nomeCampo){
	/***********************************************************
		Funzione che estrae il valore di un campo nella form attiva
		@param nomeCampo
			Nome del campo da estrarre
		@return
			Valore del campo. Da un'eccezione se non esiste la form attiva
		@changelog
			28/02/2007 M.F. Prima versione
	 ***********************************************************/
	if(activeForm!=null){
		return activeForm.getValue(nomeCampo);
	} else
	  throw "Form non esistente o non inizializzato";
}

function getOriginalValue(nomeCampo){
	/***********************************************************
		Funzione che estrae il valore di un campo
		@param nomeCampo
			Nome del campo da cui estrarre il valore originale
		@return
			Valore originale del campo
		@changelog
			28/02/2007 M.F. Prima versione
	 ***********************************************************/
	if(activeForm!=null){
		var value=activeForm.getValue("def"+nomeCampo);
		if(value!=""){
			var i;
			for(i=0;i<3;i=i+1){
				if(value.indexOf(";")>=0)
					value=value.substring(value.indexOf(";")+1);
				else{
					value="";
					break;
				}
			}
		}
		return value;
	} else
	  throw "Form non esistente o non inizializzato";
}

function setOriginalValue(nomeCampo, valore){
	/***********************************************************
		Funzione che setta il valore originale di un campo
		@param nomeCampo
			Nome del campo da cui estrarre il valore originale
		@param valore
			Valore da settare al campo
		@return
		@changelog
			23/04/2009 L.G. Prima versione
	 ***********************************************************/
	if(activeForm != null) {
		var value = activeForm.getValue("def" + nomeCampo);
		if(value != ""){
			var tmp = value.split(";");
			tmp[tmp.length-1] = valore;

			value = "";
			for(var i=0; i < tmp.length; i=i+1){
				value += tmp[i] + ";";
			}
			value = value.substring(0, value.length - 1);
			activeForm.setValue("def"+nomeCampo, value);
		}
	} else
	  throw "Form non esistente o non inizializzato";
}

function isValueChanged(nomeCampo){
	/***********************************************************
	 Funzione che determina se il valore di un campo e' stato modificato
	 confronto il valore attuale con quello originale
	 @param nomeCampo
	   Nome del campo a cui si vuole applicare il controllo
	 @return Ritorna il risultato del test value == originale
	 @changelog
	   10/06/2009 L.G. Prima versione 
	 ***********************************************************/
	var originalValue = getOriginalValue(nomeCampo);
	var value = getValue(nomeCampo);
	if(value == originalValue)
		return false;
	else
		return true;
}

/************************************************************************************************************************************************
	FUNZIONI DI CONVERSIONE DEI DATI DI INPUT
************************************************************************************************************************************************/
function formatCurrency(valore,decSep, curSep, decimali){
	/**************************************************************	 
		Funzione che formatta un importo
		@param valore
			Valore
		@param decSep,
			Separatore decimale
		@param curSep
			Separatore delle migliaia
		@return 
			Importo opportunamente formattato
		@author Marco Franceschin
		@changelog
			05.12.2007: M.F. Prima Versione
	***************************************************************/
	if(valore=="" || valore==null || valore == 0 )
		return "";
	if(decimali == null){decimali = 2;}
	var ret=new String(eval(valore).toFixed(decimali));
	var decP=ret.substring(ret.indexOf(".")+1);
	var intP=ret.substring(0,ret.indexOf("."));
	var segno="";
	var i;
	if(decSep==null) decSep=",";
	if(curSep==null) curSep=".";
	if(eval(valore)<0){
		intP=intP.substring(1);
		segno="-";
	}
	
	// Setto la separazione delle migliaia
	ret="";
	for(i=intP.length;i>3;i-=3){
		ret=curSep+intP.substring(i-3,i)+ret;
	}
	ret=intP.substring(0,i)+ret;
	return segno+ret+decSep+decP+" &euro;";
}

function toMoney(valore,decimali){
	/**************************************************************	 
		Funzione che converte un valore numerico in money settando anche l'opportuno valore di visualizzazione
		@param valore 
			Importo
		@param decimali
			Numero di decimali (default ? 2)
		@return
			Oggetto valore view con valore e valore di visualizzazione
		@author Marco Franceschin
		@changelog
			05.12.2007: M.F. Prima Versione
	***************************************************************/
	var viewVal;
	if(valore==0 || valore==null || valore=="0")
		return new ValoreView("", "");
	if(decimali==null)decimali = 2;
	valore=eval(valore).toFixed(decimali);
	if(valore=="" || eval(valore)>0)
		viewVal="<span class=\"importo\">"+formatCurrency(valore,null,null,decimali)+ " </span>";
	else
		viewVal="<span class=\"importoNegativo\">"+formatCurrency(valore,null,null,decimali)+ " </span>";
	return new ValoreView(valore,viewVal);
}

function toNum(valore){
	/**************************************************************	 
		Funzione che converte una stringa in numero. Se vuota la converte in 0
		@param valore
		@return Valore numerico della stringa
		@author Marco Franceschin
		@changelog
			05.12.2007: M.F. Prima Versione
	***************************************************************/
	if(valore=="" || valore == null)
		return 0;
	try{
		return eval(valore);
	}catch(e){
	}
	return 0;
}
function toNumNull(valore){
	/**************************************************************	 
		Funzione che converte una stringa in numero. Se vuota la converte in 0
		@param valore
		@return Valore numerico della stringa
		@author Marco Franceschin
		@changelog
			05.12.2007: M.F. Prima Versione
	***************************************************************/
	var ret="";
	if(valore=="" || valore == null)
		return "";
	try{
		ret=eval(valore);
	}catch(e){
	}
	if(ret==0)
		ret="";
	return ret;
}

function toDate(strData){
	/**************************************************************	 
		Funzione che converte una data in formato stringa in un oggetto Data javascript
		@param strData
			Data nel formato gg.mm.yyyy
		@return Oggetto data.
		@author Marco Franceschin
		@changelog
			19.06.2006: M.F. Prima Versione
	***************************************************************/
	var val=new Ref(strData);
	if(!checkData(val))
		return null;
	//outMsg("toDate("+strData+")="+val.value);
	var div=val.value.split("/");
	return new Date(eval(div[2]),eval(div[1]) - 1,eval(div[0]));
}

  //////////////////////////////////////////
  // Nome:        changeFiltroArchivioComuni
  // Descrizione: modifica la condizione di filtro in un archivio in base 
  //              al valore di provincia selezionato mediante la pagina
  //              gene/commons/istat-comuni-lista-popup.jsp
  // Argomenti:
  //	provincia - valore della provincia su cui filtrare
  //	nomeUnCampoInArchivio - campo della form associato all'archivio 
  //              (solitamente il campo hidden contenente il codice provincia)
  //
  // Ritorna:     <e' una procedura>
  //////////////////////////////////////////
function changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio) {
	if (activeForm == null) throw "Form non esistente o non inizializzato";	
	var objCampo = activeForm.getCampo(nomeUnCampoInArchivio);
	if (provincia != '')
		objCampo.archivio.formObj.archWhereLista.value = "tb1.tabcod3 = '" + provincia + "'";
	else
		objCampo.archivio.formObj.archWhereLista.value = "";
}

function aggiornaNazionalita(comuneNascita, valoreItalia, campoNazione){
 	if(comuneNascita != null && comuneNascita != "") {
 		setValue(campoNazione, valoreItalia);
 	}
}

function isNazionalitaItalia(selectNazionalita){
  var isItalia= "si";
  if(selectNazionalita.selectedIndex >0){
 	 	var testoNazionalita = selectNazionalita.options[selectNazionalita.selectedIndex].text;
 	 	testoNazionalita = testoNazionalita.toUpperCase();
 	 	if("ITALIA" !=  testoNazionalita)
 	 		isItalia = "no";
 	}
 	return isItalia;
}


function checkCodFisNazionalita(cf,selectNazionalita){
	var isItalia = isNazionalitaItalia(selectNazionalita);
	if(isItalia == "si")
		return checkCodFis(cf);
	else
		return true;
}

function checkPivaNazionalita(piva,selectNazionalita){
	var isItalia = isNazionalitaItalia(selectNazionalita);
	if(isItalia == "si"){
		return checkCodFis(piva);
	}else{
	    return checkPivaEuropea(piva);	
	}
}

function checkPivaEuropea(piva){
	//Si deve controllare che i primi due caratteri siano delle lettere per la V.A.T.
	if(piva == null || piva.length ==0)
		return true;
	if(piva.length<4)
		return false;	
	
	piva = piva.toUpperCase();
	var validi = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	for( i = 0; i < 2; i++ ){
		// Se non ? tra i caratteri validi da errore
		if( validi.indexOf( piva.charAt(i) ) == -1 )
				return false;
	}
	return true;
}

  /////////////////////////////////////////////////////////////////////////////
  // 
  // PREMESSA: per sezione dinamica si intende l'insieme delle occorrenze di una
  //           entita figlia presenti nella scheda dell'entita' padre e la
  //					 possibilita' di inserire al piu' 5 occorrenze quando la scheda e'
  //					 aperta in modifica.
  //
  // Oggetto contenente le principali informazioni di una sezione dinamica, in
  // particolare:
  // - array dei nomi dei campi (fittizzi) presenti nella sezione (privi del 
  //   numero progressivo. Es.: se nelle n sezioni della scheda e' ripetuto piu'
  //   volte il campo ENTITA.NOMECAMPO, e se gli attributi id e name di tale
  //   campo sono  ENTITA_NOMECAMPO_<progressivo> l'array dovra' contenere la
  //   stringa "ENTITA_NOMECAMPO_");
  // - numero massimo di sezioni visualizzabili nella pagina (pari alla somma
  //   del numero di occorrenze caricate da DB e di quelle inseribili dall'utente
  //   ogni volta che modifica la scheda (tipicamente 5));
  // - id della riga contenente il titolo della sezione (privo del numero
  //   progressivo). Es.: "rowtitoloLegRap_";
  // Inoltre sono stati definiti i metodi get per accedere agli attributi
  // dell'oggetto.
  //
  // Questo oggetto e' utile per il controllo di obbligatorieta' dei campi delle
  // sezioni dinamiche. In particolare se una sezione e' visualizzata e nessun
  // suo campo e' stato valorizzato, il salvataggio non deve venir bloccato per
  // l'obbligatorieta' dei campi.
  //
  /////////////////////////////////////////////////////////////////////////////
function SezioneDinamicaObj(arrayCampiSezione, maxIdSezioneVisualizzabile, rowTitoloSezione){
	var arrayCampiSezioneDinamica;
	var maxIdSezioneVisualizzabile;
	var rowTitoloSezione;
	
	this.getArrayCampiSezioneDinamica=function(){
		return this.arrayCampiSezioneDinamica;
	}
	
	this.getMaxIdSezione=function(){
		return this.maxIdSezioneVisualizzabile;
	}
	
	this.getTitoloSezioni=function(){
		return this.rowTitoloSezione;
	}
	
	this.arrayCampiSezioneDinamica = arrayCampiSezione;
	this.maxIdSezioneVisualizzabile = maxIdSezioneVisualizzabile;
	this.rowTitoloSezione = rowTitoloSezione;
}

  ////////////////////////////////////////////////////////////////////////////
  // Nome:      : controlloValorizzazioneSezioniDinamicheVisualizzate
  // Descrizione: si controlla l'obbligatorieta' dei campi delle sezioni dinamiche
  //							presenti nella scheda. L'argomento della funzione e' un array
  //							di oggetti di tipo SezioneDinamicaObj. Ciascun oggetto dell'
  //							array viene usato per determinare se per ciascuna delle
  //							occorrenze visualizzate e' valorizzata o meno.
  //							Se una occorrenza e' visualizzata e nessuno dei suoi campi e'
  //							valorizzato, allora NON si deve bloccare il salvataggio della 
  //              scheda per la mancanza di campi obbligatori. Se invece la sezione 
  //							e' visualizzata e ALMENO uno dei suoi campi e'
  //							valorizzato, allora si deve bloccare il salvataggio della 
  //              scheda per la mancanza di campi obbligatori.
  //							Se una occorrenza NON e' visualizzata, allora non si deve 
  //							bloccare il salvataggio della scheda per la mancanza di campi
  //						 obbligatori.
  //							
  // Argomenti  : arraySezDinam = array di oggetti SezioneDinamicaObj, di dimensione
  //							pari al numero delle diverse sezioni dinamiche presenti nella scheda
  // Ritorna    : 
  ////////////////////////////////////////////////////////////////////////////
function controlloValorizzazioneSezioniDinamicheVisualizzate(arraySezDinam){
	if(arraySezDinam != null && arraySezDinam.length > 0){
		for(var i=0; i < arraySezDinam.length; i++){
			
			var arrayCampiSezioneDinamica = arraySezDinam[i].getArrayCampiSezioneDinamica();
			var maxIdSezioneVisualizzabile = arraySezDinam[i].getMaxIdSezione();
			var titoloSezioni = arraySezDinam[i].getTitoloSezioni();

			var arrayChecks = activeForm.getChecksArray();
			var indice = 1;

			var isSezioneValorizzata = false;
			while(indice <= maxIdSezioneVisualizzabile){
				if(document.getElementById(titoloSezioni + indice) && document.getElementById(titoloSezioni + indice).style.display != "none"){
					for(var li=0; li < arrayCampiSezioneDinamica.length && !isSezioneValorizzata; li++){
						if(getValue(arrayCampiSezioneDinamica[li] + indice) == getOriginalValue(arrayCampiSezioneDinamica[li] + indice))
							isSezioneValorizzata = false;
						else
							isSezioneValorizzata = true;
					}

					for(var j=0; j < arrayCampiSezioneDinamica.length; j++){
						for(var la=0; la < arrayChecks.length; la++){
							if(arrayChecks[la].isObbligatorio() && arrayChecks[la].getNome().toUpperCase() == (arrayCampiSezioneDinamica[j] + indice).toUpperCase()){
								arrayChecks[la].setSkipControlloObblig(!isSezioneValorizzata);
							}
						}
					}
				}
				indice++;
				isSezioneValorizzata = false;
			}
		}
	} else {
		alert("L'oggetto JS arraySezioniDinamicheObj non trovato.\n Oggetto is null ? " + (arraySezDinam == null));
	}
}

	// FUNZIONI PER LA GESTIONE DELLE SCHEDE MULTIPLE
	
	function hideElementoSchedaMultipla(id, tipo, campi, sbiancaValori){
		showObj("rowtitolo" + tipo + "_" + id, false);
		for (var i = 0; i < campi.length; i++)
			showObj("row" + campi[i] + id, false);
		if(sbiancaValori){
			for (var i = 0; i < campi.length; i++)
				setValue(campi[i] + id, "");
		}
		document.getElementById("elementoSchedaMultiplaVisibile" + tipo + "_" + id).value = 0;
	}
	
	function showElementoSchedaMultipla(id, tipo, campi, visibilitaCampi){
		if (visibilitaCampi.length != 0 && visibilitaCampi.length != campi.length)
			throw "Le lunghezze degli array campi e visibilita' campi sono diverse";
		showObj("rowtitolo" + tipo + "_" + id, true);
		for (var i = 0; i < campi.length; i++) {
			if (visibilitaCampi.length == 0) {
				showObj("row" + campi[i] + id, true);
			} else {
				showObj("row" + campi[i] + id, visibilitaCampi[i]);
			}
		}
		document.getElementById("elementoSchedaMultiplaVisibile" + tipo + "_" + id).value = 1;
	}

	function showNextElementoSchedaMultipla(tipo, campi, visibilitaCampi){
		var indice = eval("lastId" + tipo + "Visualizzata") + 1;

		if(indice <= eval("maxId" + tipo + "Visualizzabile")){
			eval("lastId"+tipo+"Visualizzata = indice;");
			showElementoSchedaMultipla(indice, tipo, campi, visibilitaCampi);
			setValue("INDICE_" + tipo, indice);
		}
		
		// Quando la variabile 'indice' e' uguale alla variabile globale 
		// 'maxId<tipo>Visualizzabile' allora bisogna nascondere il link
		if(indice == eval("maxId" + tipo + "Visualizzabile")){		
			showObj("rowLinkAdd" + tipo, false);
			showObj("rowMsgLast" + tipo, true);
		}
	}
	
	function delElementoSchedaMultipla(id, label, tipo, campi){
		if(confirm("Procedere con l'eliminazione ?")){
			hideElementoSchedaMultipla(id, tipo, campi, false);
		  setValue(label + id, "1");
		}
	}

	function setModificatoElementoSchedaMultipla(id, label, tipo, campi){
		var occorrenzaModificata = false;
		for(var i=0; i < campi.length && !occorrenzaModificata; i++){
			if(isValueChanged(campi[i] + id)){
				occorrenzaModificata = true;
			}
		}
		if(occorrenzaModificata)
			setValue(label + id, "1");
		else
			setValue(label + id, "0");
	}
