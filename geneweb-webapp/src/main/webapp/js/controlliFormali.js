/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

// ALL'INTERNO DI QUESTO FILE VANNO INSERITE TUTTE E SOLE LE FUNZIONI CHE
// EFFETTUANO I CONTROLLI SUL CAMPO IN INPUT PASSATO COME PARAMETRO,
// POSIZIONANO IL FOCUS SU TALE CAMPO IN CASO DI DATO NON VALIDO, ED EMETTONO
// UN MESSAGGIO DI ERRORE.
// TUTTE LE FUNZIONI RESTITUISCONO UN ESITO true/false.

  //////////////////////////////////////////
  // Nome:        trim
  // Descrizione: Funzione che rimuove gli spazi iniziali e finali della stringa
  //              usando le RegEx
  //	unCampoInput - oggetto di tipo input text da controllare
  //////////////////////////////////////////	
	function trim(unCampoDiInput) {
		return trimStringa(unCampoDiInput.value);
	}

  //////////////////////////////////////////
  // Nome:        trimStringa
  // Descrizione: Funzione che rimuove gli spazi iniziali e finali della stringa
  //              usando le RegEx
  //	valoreStringa - stringa da controllare
  //////////////////////////////////////////	
	function trimStringa(valoreStringa){
		var x = valoreStringa;
	  x=x.replace(/^\s*(.*)/, "$1");
	  x=x.replace(/(.*?)\s*$/, "$1");
	  return x;
	}


  //////////////////////////////////////////
  // Nome:        controllaCampoInputObbligatorio
  // Descrizione: Controlla se 'unCampoInput' e' stato popolato o meno. Se non popolato presenta un alert.
	// Argomenti:
  //	unCampoInput - oggetto di tipo input text da controllare
  //  descrizioneCampo - nome del campo di input obbligatorio
  //////////////////////////////////////////
	function controllaCampoInputObbligatorio(unCampoInput, descrizioneCampo){
	  var valore = trim(unCampoInput);
	  if(valore==""){
	    alert("Il campo '" + descrizioneCampo + "' prevede un valore obbligatorio");
	    unCampoInput.focus();
	    return false;
	  }
	  return true;
	}

  //////////////////////////////////////////
  // Nome:        controllaCampoInputNoCarSpeciali
  // Descrizione: Controlla se 'unCampoInput' e' costituito da lettere, numeri e il carattere '_' 
	// Argomenti:
  //	unCampoInput - oggetto di tipo input text da controllare
  //  descrizioneCampo - nome del campo di input che si sta controllando
  //////////////////////////////////////////
	function controllaCampoInputNoCarSpeciali(unCampoInput, descrizioneCampo){
		var valore = trim(unCampoInput);

	  var caratteriAmmessi = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
	  var primoCarattere = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	  var result = true;
  	if(primoCarattere.indexOf(valore.charAt(0)) < 0){
  		result = false;
	  	alert('Errore: il primo carattere del campo '+ descrizioneCampo +' deve essere una lettera.');
	  	unCampoInput.focus();
  	}
  	var index=1;
  	while(index < valore.length & result){
	  	if(caratteriAmmessi.indexOf(valore.charAt(index)) < 0){
		  	result = false;
	  		alert('Errore: \"'+ valore.charAt(index) + '\" carattere non ammesso nel campo '+ descrizioneCampo +'.');
	  		unCampoInput.focus();
	  	} else {
	  		index = index+1;
	  	} 
	  }
	  return result;
	}
	
  //////////////////////////////////////////
  // Nome:        controllaNomeFileNoCarSpeciali
  // Descrizione: Controlla se 'unCampoInput' e' costituito da lettere, numeri e i caratteri '_', '-', ' ', '.'
  //              e che 'unCampoInput' contenga, al massimo, una volta il carattere '.' (per l'estensione)
	// Argomenti:
  //	unCampoInput - oggetto di tipo input text da controllare
  //  descrizioneCampo - nome del campo di input che si sta controllando
  // Note: questa funzione e' adatta al controllo dei caratteri di cui e' costituito il nome di un file
  //////////////////////////////////////////
	function controllaNomeFileNoCarSpeciali(unCampoInput, descrizioneCampo){
		var valore = String(unCampoInput.value);
		var result = true;
	  var caratteriAmmessi = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_- .";
  	var index=0;
  	
  	while(index < valore.length & result){
	  	if(caratteriAmmessi.indexOf(valore.charAt(index)) < 0){
		  	result = false;
	  		alert('Errore: il carattere \"'+ valore.charAt(index) + '\" non e\' ammesso nel campo ' + descrizioneCampo);
	  	} else {
	  		index = index+1;
	  	}
	  }
	  if(result){
	  	if(valore.indexOf(".") != valore.lastIndexOf(".")){
	  		alert("Errore: il campo " + descrizioneCampo + " puo' contenere solamente una volta in carattere '.'");
	  		result = false;
	  	}
	  }
	  return result;
	}
	
  //////////////////////////////////////////
  // Nome:        controllaLunghezzaInput
  // Descrizione: Controlla se 'unCampoInput' e' costituito da lettere, numeri e il carattere '_' 
	// Argomenti:
  //	unCampoInput - oggetto di tipo input text da controllare
  //  descrizioneCampo - nome del campo di input che si sta controllando
  //////////////////////////////////////////
	function	controllaLunghezzaInput(unCampoInput, descrizioneCampo, lunghezzaCampo){
		var valore = trim(unCampoInput);
		result = true;
		if(valore.length > lunghezzaCampo){
			alert('Errore: numero eccessivo di caratteri nel campo \"' + descrizioneCampo +  '\"  \nLunghezza massima ' + lunghezzaCampo + ' caratteri.');
			result = false;
		}
		return result;
	}
	
	//////////////////////////////////////////
  // Nome:        isIntero
  // Descrizione: Controlla se la stringa in ingresso e' un numero intero (positivo o negativo)
	// Argomenti:
  //	stringa        - valore da controllare
  //	segnoAccettato - true se può essere presente + o - in testa, false altrimenti. Default: true
  //	almenoUnDigit  - true se si richiede sia presente almeno una cifra, false altrimenti. Default: true
  //////////////////////////////////////////
	function isIntero(stringa,segnoAccettato,almenoUnDigit){
		if (segnoAccettato == null) segnoAccettato = true;
		if (almenoUnDigit == null) almenoUnDigit = true;
	  if (stringa == "") return true; //una campo vuoto va comunque bene
	    
		// nel caso di utilizzo del segno, consento che il primo carattere sia proprio il + o il -
		if (segnoAccettato && (stringa.charAt(0) == '+' || stringa.charAt(0) == '-')) stringa = stringa.substring(1);
	  var numeroCifre = stringa.length;
	  // con la modifica di numero cifre che segue si fanno fallire i check su numeri costituiti al max dal solo segno,
	  // in cui viene richiesto almeno una cifra, quali i numeri interi
	  if (almenoUnDigit && numeroCifre == 0) numeroCifre = 1;
		var oggettoEspressioneRegolare = new RegExp("^[0-9]{"+numeroCifre+"}$");
		return oggettoEspressioneRegolare.test(stringa);
	}

	//////////////////////////////////////////
  // Nome:        isFloating
  // Descrizione: Controlla se la stringa in ingresso e' un floating, cioe' o un
  // 							numero con la virgola (es.: 12,34) o un intero (es: 12 == 12,0)
	// Argomenti:
  //	stringa - valore da controllare
  //////////////////////////////////////////
	function isFloating(stringa){
	  var esito = false;
	  var pos = stringa.indexOf(",");
	  if(pos >= 0){
	  	var parteIntera = stringa.substring(0, pos);
	  	var parteDecimale = stringa.substring(pos + 1, stringa.length);
	  	// se esiste solo la parte intera + virgola, allora nella parte intera
	  	// non posso avere solo l'eventuale segno + o - ma devo avere almeno una cifra
	  	if (parteDecimale.length == 0)
		    esito = isIntero(parteIntera, true, true);
		  else
		    esito = isIntero(parteIntera, true, false);
	    if(esito){
	      esito = isIntero(parteDecimale, false, false);
	    }
	  } else {
	    esito = isIntero(stringa);
	  }
	  return esito;
	}
	
	/////////////////////////////////////////////////////////
	// 
	// Controlla se il campo di input contiene una data ? corretta (vedi anni bisestili,
	// giorni inesistenti o mesi inesistenti, ....
	// la data in input ? corretta se ? nel formato GG/MM/AAAA
	// Argomenti:
  //	unCampoInput - oggetto di tipo input text da controllare
	/////////////////////////////////////////////////////////
	function isData(unCampoInput)
	{
	  var esito  = false;
	  var valore = unCampoInput.value;
	  if(valore=="") //una data vuota va comunque bene
	    esito = true;
	  else
	  {
	    if (isDataFormatoValido(valore))
	    {
	      if (isDataInFormatoValidoCorretta(valore))
	        esito = true;
	      else
	      {
	        alert("La data non e' valida");
	        unCampoInput.focus();
	      }
	    }
	    else
	    {
	      alert("La data deve essere nel formato GG/MM/AAAA");
	      unCampoInput.focus();
	    }
	  }
	  return esito;
	}
	
	function isDataFormatoValido(stringa)
	{
		var oggettoEspressioneRegolare = new RegExp("^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$");
		return oggettoEspressioneRegolare.test(stringa);
	}
	
	// i giorni sono sempre composti da 2 cifre e vanno da 01 a 31
	function isGiornoFormatoValido(stringa)
	{
		var oggettoEspressioneRegolare = new RegExp("^0[1-9]$|^[12][0-9]$|^3[01]$");
		return oggettoEspressioneRegolare.test(stringa);
	}
	
	// i mesi sono sempre composti da 2 cifre e vanno da 01 a 12
	function isMeseFormatoValido(stringa)
	{
		var oggettoEspressioneRegolare = new RegExp("^0[1-9]$|^1[012]$");
		return oggettoEspressioneRegolare.test(stringa);
	}
	
	// gli anni sono sempre composti da 4 cifre e vanno da 1900 a 2099
	function isAnnoFormatoValido(stringa)
	{
		var oggettoEspressioneRegolare = new RegExp("^19[0-9]{2}$|^20[0-9]{2}$");
		return oggettoEspressioneRegolare.test(stringa);
	}
	
	/////////////////////////////////////////////////////////
	// CONTROLLO DI SEMANTICA
	// Controlla se la stringa in input contenente una data ? corretta (vedi anni bisestili,
	// giorni inesistenti o mesi inesistenti, ....
	// la data in input ? corretta se ? nel formato GG/MM/AAAA
	// Argomenti:
  //	unaDataScrittaCorretta - valore da controllare
	/////////////////////////////////////////////////////////
	function isDataInFormatoValidoCorretta(unaDataScrittaCorretta)
	{
	  var regExpPerEliminazioneZeroIniziale = /^0/g; //elimina lo zero iniziale nei giorni e nei mesi
	  var pezziData = unaDataScrittaCorretta.split("/");
	  var giorno    = pezziData[0];
	  var mese      = pezziData[1];
	  var anno      = pezziData[2];
	  var esito     = false;
	
	  // qui controllo se ogni singola parte ? scritta bene con il range corretto
	  // gli anni vanno da 1900 a 2099, i mesi da 01 a 12, i giorni da 01 a 31
	  if (isGiornoFormatoValido(giorno) && isMeseFormatoValido(mese) && isAnnoFormatoValido(anno))
	  {
	    var giornoIntero = parseInt(giorno.replace(regExpPerEliminazioneZeroIniziale,""));
	    var meseIntero   = parseInt(mese.replace(regExpPerEliminazioneZeroIniziale,""));
	    var annoIntero   = parseInt(anno);
	
	    // predispongo il numero massimo di giorni per ogni mese dell'anno individuato
	    var giorniMese = new Array(12+1);
	    giorniMese[1]=giorniMese[3]=giorniMese[5]=giorniMese[7]=giorniMese[8]=giorniMese[10]=giorniMese[12]=31;
	    giorniMese[4]=giorniMese[6]=giorniMese[9]=giorniMese[11]=30;
	    if ((annoIntero%4)==0) giorniMese[2]=29;
	    else giorniMese[2]=28;
	    // controllo se il giorno ? un giorno valido per il mese in oggetto
	    if (giornoIntero<=giorniMese[meseIntero])
	      esito = true;
	    else
	      esito = false;
	  }
	  return esito;
	}
	
	/////////////////////////////////////////////////////////
	// CONTROLLO DI SEMANTICA
	// Controlla se la stringa in input contenente una email corretta 
	// Argomenti:
  //	unCampoInput - valore da controllare
	/////////////////////////////////////////////////////////
	function isEmailFormatoValido(unCampoInput) 
	{
	    var mail = unCampoInput.value;
	    var caratteriAmmessi = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/;
	    var esito = false
	 
	    if (!mail.match(caratteriAmmessi)) {
	        alert("L'indirizzo email non e' sintatticamente valido.");
	        unCampoInput.focus();
	    } else {
	        esito = true;
	    }
	    
	    return esito;
	}


	/////////////////////////////////////////////////////////
	// CONTROLLO DI SEMANTICA
	// Controlla se la stringa in ingresso alla funzione rappresenta un indirizzo
	// email valido al livello sintattico
	// Argomenti:
  //	 valoreMail - valore della stringa controllare
  // Ritorna true se la stringa rappresenta un indirizzo email valido, false
  // altrimenti
	/////////////////////////////////////////////////////////
	function isFormatoEmailValido(valoreMail){
	  var caratteriAmmessi = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/;
	  var esito = false
	  if(valoreMail.match(caratteriAmmessi)){
       esito = true;
    }
	  return esito;
	}

	
	//////////////////////////////////////////
  // Nome:        controllaCampoPassword
  // Descrizione: Controlla se 'unCampoPassword' rispetto alle seguenti regole:
  //							1. i caratteri di cui la password e' costituita sono tutti ammessi
  //							2. se controlloRestrizioni e' true, vengono inoltre controllati 
  //								 se la lunghezza della password maggiore o uguale a "lunghezzaCampo"
	//								 e almeno due caratteri sono numerici
	// Argomenti:
  //	unCampoPassword - oggetto di tipo input text da controllare
  //  lunghezzaCampo - lunghezza minima del campo
  //  controlloRestrizioni - variabile booleana che se valorizzata a true, impone il controllo della sintassi della password,
  //												 cioe' almeno 8 caratteri di cui 2 numerici.
  //////////////////////////////////////////
	function controllaCampoPassword(unCampoPassword,lunghezzaCampo, controlloRestrizioni){
	
		var valore = unCampoPassword.value;
		var caratteriAmmessi = " ~#\"$%&'()*+,-./0123456789:;<=>?!@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_abcdefghijklmnopqrstuvwxyz";
		var result = true;
		
		// controllo dei caratteri di cui la password e' costituita
  	var index = 0;
  	while(index < valore.length & result){
	  	if(caratteriAmmessi.indexOf(valore.charAt(index)) < 0){
		  	result = false;
	  		alert('Errore: \"'+ valore.charAt(index) + '\" carattere non ammesso nel campo password.');
	  		unCampoPassword.focus();
	  	} else {
	  		index = index+1;
	  	}
	  }
	  
	  if(controlloRestrizioni){
		  if(valore.length < lunghezzaCampo){
				alert('Errore: numero minimo di caratteri nel campo password non raggiunto \nLunghezza minima ' + lunghezzaCampo + ' caratteri.');
				unCampoPassword.focus();
				result = false;
			}
		  
	    index=0;  
	    var numInteri = false;
	    var maiuscole = false;
	    var oggettoEspressioneRegolare = new RegExp("^[0-9]$");
	    var maiuscoleEspressioneRegolare = new RegExp("^[A-Z]$");
		
			while(index < valore.length && !(maiuscole && numInteri)){
				if (oggettoEspressioneRegolare.test(valore.charAt(index))){
					numInteri = true;
				}
				if(maiuscoleEspressioneRegolare.test(valore.charAt(index))){
					maiuscole = true;
				}
				index = index + 1;
			}
			if(result && !(numInteri && maiuscole)) {
				result = false;
				alert('Errore: la password deve contenere almeno 1 carattere numerico e una lettera maiuscola.');
				unCampoPassword.focus();
			}	
			
		}
	  return result;
	}

	/////////////////////////////////////////////////////////////////////////////
  // Nome:        controllaCampoPasswordObbligatorio
  // Descrizione: Controlla se 'unCampoInput' usato per specificare una password
  //							e' stato valorizzato o meno. Se non valorizzato presenta un alert.
	// Argomenti:
  //	unCampoInput - oggetto di tipo input text da controllare
  //  descrizioneCampo - nome del campo di input obbligatorio
  //
  // Osservazione: questa funzione rispetto alla funzione 'controllaCampoInputObbligatorio'
  //							 non effettua il trim del campo
  /////////////////////////////////////////////////////////////////////////////
	function controllaCampoPasswordObbligatorio(unCampoInput, descrizioneCampo){
	  var valore = unCampoInput.value;
	  if(valore==""){
	    alert("Il campo '" + descrizioneCampo + "' prevede un valore obbligatorio");
	    unCampoInput.focus();
	    return false;
	  }
	  return true;
	}
