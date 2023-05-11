/*
 * Created on 7-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

//////////////////////////////////////////
// Nome:        general.js
// Descrizione: file Javascript con funzioni e variabili di utilita' generale
// Dipendenze:  nessuna
//////////////////////////////////////////


// variabili generali booleane per il test del browser; il nome e' autoesplicativo del browser individuato
var ie4=document.all&&navigator.userAgent.indexOf("Opera")==-1;
var ns6=document.getElementById&&!document.all;
var ns4=document.layers;
var ie10 = false;
/*@cc_on
	if (/^10/.test(@_jscript_version)) {
		ie10 = true;
	}
@*/

  //////////////////////////////////////////
  // Nome:        modificaParametroRicercaIniziaPer
  // Descrizione: esegue la modifica del valore del campo di input passato 
  //              come argomento in modo da porre il carattere speciale "%"
  //              solo in coda, per poter eseguire filtri nelle ricerche
  //              che iniziano per il valore del campo di input stesso
  // Argomenti:
  //	aCodice - nome o id del campo di input del quale modificare il valore
  //
  // Ritorna:     <e' una procedura>
  //
  // Analisi:     documento 5349_2693_08_01_Analisi.doc, paragrafo 13.1
  //////////////////////////////////////////
  function modificaParametroRicercaIniziaPer(aCodice) {
    var linpOggetto = null;
    if (document.getElementById)
      linpOggetto = document.getElementById(aCodice);
    else if (document.all)
      linpOggetto = eval ("document.all."+aCodice);
    if (linpOggetto.value.length>0) {
      var valore = pulisciStringaDaCarattereSpeciale(linpOggetto.value,"%");
      linpOggetto.value = valore + "%";
    }
  }

  //////////////////////////////////////////
  // Nome:        modificaParametroRicercaTerminaPer
  // Descrizione: esegue la modifica del valore del campo di input passato 
  //              come argomento in modo da porre il carattere speciale "%"
  //              solo in testa, per poter eseguire filtri nelle ricerche
  //              che terminano per il valore del campo di input stesso
  // Argomenti:
  //	aCodice - nome o id del campo di input del quale modificare il valore
  //
  // Ritorna:     <e' una procedura>
  //
  // Analisi:     documento 5349_2693_08_01_Analisi.doc, paragrafo 13.2
  //////////////////////////////////////////
  function modificaParametroRicercaTerminaPer(aCodice) {
    var linpOggetto = null;
    if (document.getElementById)
      linpOggetto = document.getElementById(aCodice);
    else if (document.all)
      linpOggetto = eval ("document.all."+aCodice);
    if (linpOggetto.value.length>0) {
      var valore = pulisciStringaDaCarattereSpeciale(linpOggetto.value,"%");
      linpOggetto.value = "%" + valore;
    }
  }

  //////////////////////////////////////////
  // Nome:        modificaParametroRicercaContiene
  // Descrizione: esegue la modifica del valore del campo di input passato 
  //              come argomento in modo da porre il carattere speciale "%"
  //              in testa ed in coda, per poter eseguire filtri nelle ricerche
  //              che contengono il valore del campo di input stesso
  // Argomenti:
  //	aCodice - nome o id del campo di input del quale modificare il valore
  //
  // Ritorna:     <e' una procedura>
  //
  // Analisi:     documento 5349_2693_08_01_Analisi.doc, paragrafo 13.3
  //////////////////////////////////////////
  function modificaParametroRicercaContiene(aCodice) {
    var linpOggetto = null;
    if (document.getElementById)
      linpOggetto = document.getElementById(aCodice);
    else if (document.all)
      linpOggetto = eval ("document.all."+aCodice);
    if (linpOggetto.value.length>0) {
      var valore = pulisciStringaDaCarattereSpeciale(linpOggetto.value,"%");
      linpOggetto.value = "%" + valore + "%";
    }
  }

  //////////////////////////////////////////
  // Nome:        pulisciStringaDaCarattereSpeciale
  // Descrizione: pulisce la stringa in input di tutte le occorrenze del carattere
  //              passato come argomento
  // Argomenti:
  //	aStringa   - stringa da ripulire
  //	aCarattere - carattere da eliminare
  //
  // Ritorna:     aStringa ripulita di tutte le occorrenze di aCarattere
  //
  // Analisi:     documento 5349_2693_08_01_Analisi.doc, paragrafo 13.1
  //////////////////////////////////////////
  function pulisciStringaDaCarattereSpeciale(aStringa, aCarattere) {
    var liIndiceCarattere = aStringa.indexOf(aCarattere);
    while (liIndiceCarattere != -1) {
      aStringa = aStringa.substring(0, liIndiceCarattere) + aStringa.substring(liIndiceCarattere + 1, aStringa.length);
      liIndiceCarattere = aStringa.indexOf(aCarattere);
    }
    return aStringa;
  }

  //////////////////////////////////////////
  // Nome:        sostituisciPrimaOccorenza
  // Descrizione: sostituisce la prima occorenza del 'testoDaSostituire' con il
  //              nuovoTesto
  // Argomenti:
  //    str               - stringa su cui operare la sostituzione
  //    testoDaSostituire - stringa da sostituire;
  //    nuovoTesto        - nuova stringa da inserire in str;
  // Ritorna:     la stringa 'str' con il testo sostituito se 'testoDaSostituire' e'
  //              presente in 'str', altrimenti ritorna -1
  //////////////////////////////////////////
  function sostituisciPrimaOccorenza(str, testoDaSostituire, nuovoTesto){
    var txtDaSostituire = new String(testoDaSostituire);
    var nuovoTxt = new String(nuovoTesto);
    var tmp = new String(str);
    if(tmp.indexOf(txtDaSostituire) >= 0){
      tmp = tmp.substring(0,tmp.indexOf(txtDaSostituire)) + nuovoTxt + str.substring(tmp.indexOf(txtDaSostituire) + txtDaSostituire.length, tmp.length);
    }
    return tmp;
  }

  //////////////////////////////////////////
  // Nome:        getAbsoluteTop
  // Descrizione: ritorna la coordinata y nel browser per l'oggetto in input
  // Argomenti:
  //	aSender   - oggetto per il quale calcolare la coordinata y dall'alto
  //
  // Ritorna:     intero indicante la posizione in pixel dell'oggetto rispetto 
  //              il margine superiore
  //////////////////////////////////////////
  function getAbsoluteTop(aSender) {
    var ris = 0;
    if(!aSender.offsetParent) return 0;
    ris = aSender.offsetTop + getAbsoluteTop(aSender.offsetParent);
    return ris;
  }

  //////////////////////////////////////////
  // Nome:        getAbsoluteLeft
  // Descrizione: ritorna la coordinata x nel browser per l'oggetto in input
  // Argomenti:
  //	aSender   - oggetto per il quale calcolare la coordinata x da sinistra
  //
  // Ritorna:     intero indicante la posizione in pixel dell'oggetto rispetto 
  //              il margine sinistro
  //////////////////////////////////////////
  function getAbsoluteLeft(aSender) {
    var ris = 0;
    if(!aSender.offsetParent) return 0;
    ris = aSender.offsetLeft + getAbsoluteLeft(aSender.offsetParent);
    return ris;
  }


  //////////////////////////////////////////
  // Nome:        selezionaTutti
  // Descrizione: seleziona tutti i checkbox presenti nella pagina
  // Argomenti:
  //	achkArrayCheckBox - array di checkbox presenti in un form 
  //  o singolo checkbox, per il quale settare tutti i check
  //
  // Ritorna:     achkArrayCheckBox con tutti i check selezionati
  //////////////////////////////////////////
  function selezionaTutti(achkArrayCheckBox) {
    if (achkArrayCheckBox) {
		  var arrayLen = "" + achkArrayCheckBox.length;
		  if(arrayLen != 'undefined') {
		    for (i = 0; i < achkArrayCheckBox.length; i++) {
		    	if(! achkArrayCheckBox[i].disabled)
			      achkArrayCheckBox[i].checked = true;
		    }
		  } else {
		  	if(! achkArrayCheckBox.disabled)
		      achkArrayCheckBox.checked = true;
		  }
    }
  }

  //////////////////////////////////////////
  // Nome:        deselezionaTutti
  // Descrizione: deseleziona tutti i checkbox presenti nella pagina
  // Argomenti:
  //	achkArrayCheckBox - array di checkboxarray di checkbox presenti 
  //  in un form o singolo checkbox, per il quale deselezionare tutti 
  //  i check
  // Ritorna:     achkArrayCheckBox con tutti i check deselezionati
  //////////////////////////////////////////
  function deselezionaTutti(achkArrayCheckBox) {
    if (achkArrayCheckBox) {
      var arrayLen = "" + achkArrayCheckBox.length;
	  if(arrayLen != 'undefined') {
  	  for (i = 0; i < achkArrayCheckBox.length; i++) {
  	      achkArrayCheckBox[i].checked = false;
    	  }
      } else {
        if (achkArrayCheckBox)
          achkArrayCheckBox.checked = false;
      }	    
    }
  }
  
  //////////////////////////////////////////
  // Nome:        about
  // Descrizione: apre una popup con l'about dell'applicazione
  //////////////////////////////////////////
  function about(applicazione) {
    var w = 500;
    var h = 400;
    var l = Math.floor((screen.width-w)/2);
    var t = Math.floor((screen.height-h)/2); 
    var popup = window.open(applicazione + '/InformazioniSu.do','popup_about','width=' + w + ',height=' + h + ',top=' + t + ',left=' + l + ',status=no,resizable=yes,scrollbars=yes');
    popup.focus();
  }

  
  //////////////////////////////////////////
  // Nome:        accediAltroApplicativoLista
  // Descrizione: apre una popup con la lista degli applicativi
  ////////////////////////////////////////// 
  function accediAltroApplicativoLista(applicazione) {
	    var w = 500;
	    var h = 550;
	    var popup = window.open(applicazione + '/ListaApplicativi.do','accediAltroApplicativoLista','width=' + w + ',height=' + h + ',status=no,resizable=yes,scrollbars=yes');
	    popup.focus();
  }
  
  
  //////////////////////////////////////////
  // Nome:        contaCheckSelezionati
  // Descrizione: conta il numero di checkbox selezionate nell'oggetto in input
  //////////////////////////////////////////
  function contaCheckSelezionati(objArrayCheckBox) {
    var numeroSelezionati = 0;
    if (objArrayCheckBox) {
      var arrayLen = "" + objArrayCheckBox.length;
        if (arrayLen != 'undefined') {
          for (i = 0; i < objArrayCheckBox.length; i++) {
            if (objArrayCheckBox[i].checked)
              numeroSelezionati++;
          }
        } else {
          if (objArrayCheckBox.checked) numeroSelezionati++;
	    }	
    }
    return numeroSelezionati;
  }

  //////////////////////////////////////////
  // Nome:        aggiornaOpzioniSelect
  // Descrizione: Aggiorna il select passato 
  // Argomenti:
  //	nomeArray          - nome dell'array con cui aggiornare i 'value' della select passata come terzo argomento
  //    nomeArrayDescr     - nome dell'array con cui aggiornare i 'text' della select passata come terzo argomento
  //    selectDaAggiornare - nome della select da aggiornare
  //
  // 23/09/2014: modificata modalita' di aggiornamento mediante jQuery per prevenire problemi con IE dalla 8 in su 
  // per cui la select risultava bloccata e non selezionabile (dovuto al set del text a partire da un array)
  //////////////////////////////////////////
  function aggiornaOpzioniSelect(nomeArrayCodice, nomeArrayDescr, selectDaAggiornare) {
    var arrayCodici = eval(nomeArrayCodice);
    var arrayDescrizioni = eval(nomeArrayDescr);
    
	$(selectDaAggiornare).children().remove();
	for (c=0; c < arrayCodici.length; c++) {
		$(selectDaAggiornare).append($('<option>', {
			value: arrayCodici[c],
			text: arrayDescrizioni[c]
		}));
	}
  }

  //////////////////////////////////////////
  // Nome:        addOption
  // Descrizione: Aggiorna la select passata aggiungendo una opzione in coda con i dati in input
  // Argomenti:
  //	theSel   - oggetto di tipo select in cui inserire una nuova opzione
  //	theText  - descrizione della nuova opzione
  //	theValue - valore della nuova opzione
  //////////////////////////////////////////
  function addOption(theSel, theText, theValue) {
    var newOpt = new Option(theText, theValue);
    var selLength = theSel.length;
    theSel.options[selLength] = newOpt;
  }

  //////////////////////////////////////////
  // Nome:        deleteOption
  // Descrizione: Aggiorna la select passata eliminando l'opzione individuata dall'indice in input
  // Argomenti:
  //	theSel   - oggetto di tipo select in cui eliminare una opzione
  //	theIndex - indice della opzione da eliminare
  //////////////////////////////////////////
  function deleteOption(theSel, theIndex) {
    var selLength = theSel.length;
    if(selLength > 0 && theIndex < selLength) {
      theSel.options[theIndex] = null;
    }
  }

  //////////////////////////////////////////
  // Nome:        copyOptions
  // Descrizione: Aggiorna una select aggiungendo le opzioni selezionate in un'altra select
  // Argomenti:
  //	theSelFrom - oggetto di tipo select da cui estrarre le opzioni selezionate
  //	theSelTo   - oggetto di tipo select in cui inserire in coda le opzioni
  //////////////////////////////////////////
  function copyOptions(theSelFrom, theSelTo) {
    var selLength = theSelFrom.length;
    var selectedText = new Array();
    var selectedValues = new Array();
    var selectedCount = 0;

    // Find the selected Options in reverse order
    for (var i = selLength - 1; i >= 0; i--) {
      if(theSelFrom.options[i].selected && theSelFrom.options[i].value != "") {
        selectedText[selectedCount] = theSelFrom.options[i].text;
        selectedValues[selectedCount] = theSelFrom.options[i].value;
        selectedCount++;
      }
    }
  
    // Add the selected text/values in reverse order.
    // This will add the Options to the 'to' Select
    // in the same order as they were in the 'from' Select.
    for(i = selectedCount - 1; i >= 0; i--) {
      addOption(theSelTo, selectedText[i], selectedValues[i]);
    }  
  }

  //////////////////////////////////////////
  // Nome:        copyAllOptions
  // Descrizione: Aggiorna una select aggiungendo tutte le opzioni di un'altra select
  // Argomenti:
  //	theSelFrom - oggetto di tipo select da cui estrarre tutte le opzioni
  //	theSelTo   - oggetto di tipo select in cui inserire in coda le opzioni
  //////////////////////////////////////////
  function copyAllOptions(theSelFrom, theSelTo) {
    var selLength = theSelFrom.length;
    var selectedText = new Array();
    var selectedValues = new Array();
    var selectedCount = 0;

    // Find the selected Options in reverse order
    for(var i = selLength - 1; i >= 0; i--) {
      if (theSelFrom.options[i].value != "") {
        selectedText[selectedCount] = theSelFrom.options[i].text;
        selectedValues[selectedCount] = theSelFrom.options[i].value;
        selectedCount++;
      }
    }

    // Add all the text/values in reverse order.
    // This will add the Options to the 'to' Select
    // in the same order as they were in the 'from' Select.
    for(i = selectedCount - 1; i >= 0; i--) {
      addOption(theSelTo, selectedText[i], selectedValues[i]);
    }
  }

  //////////////////////////////////////////
  // Nome:        deleteOptions
  // Descrizione: Aggiorna una select eliminando le opzioni selezionate
  // Argomenti:
  //	theSelFrom - oggetto di tipo select da cui eliminare le opzioni
  //////////////////////////////////////////
  function deleteOptions(theSelFrom) {
    var selLength = theSelFrom.length;

    // Find the selected Options in reverse order
    // and delete them from the 'from' Select.
    for(var i = selLength - 1; i >= 0; i--) {
      if(theSelFrom.options[i].selected) {
        deleteOption(theSelFrom, i);
      }
    }
  }

  //////////////////////////////////////////
  // Nome:        deleteAllOptions
  // Descrizione: Aggiorna una select eliminando tutte le opzioni
  // Argomenti:
  //	theSelFrom - oggetto di tipo select da cui eliminare le opzioni
  //////////////////////////////////////////
  function deleteAllOptions(theSelFrom) {
    var selLength = theSelFrom.length;

    // Find the selected Options in reverse order
    // and delete them from the 'from' Select.
    for(var i = selLength - 1; i >= 0; i--) {
      deleteOption(theSelFrom, i);
    }
  }

  //////////////////////////////////////////
  // Nome:        moveUpOptions
  // Descrizione: Aggiorna una select spostando tutte le opzioni selezionate su di una posizione.
  // Nel caso in cui un'opzione sia gia' giunta alla prima riga, le opzioni selezionate tenderanno
  // a comprimersi nelle prime posizioni spostando mano a mano le opzioni non selezionate e comprese
  // tra le opzioni selezionate in posizioni successive a tutti gli elementi selezionati.
  // Argomenti:
  //	theSel - oggetto di tipo select in cui spostare i campi verso l'alto
  //////////////////////////////////////////
  function moveUpOptions(theSel) {
    var selLength = theSel.length;
    var newPosLastObjMoved = -1;
    var tmpValue = null;
    var tmpText = null;

    for (var i = 0; i < selLength; i++) {
      if (theSel.options[i].selected && theSel.options[i].value != "") {
        if (i > 0) {
          if (i != (newPosLastObjMoved+1)) {
            // sposto l'elemento se esiste uno spazio occupabile sopra
            // e se non e' il primo elemento
            tmpValue = theSel.options[i-1].value;
            tmpText = theSel.options[i-1].text;
            theSel.options[i-1].value = theSel.options[i].value;
            theSel.options[i-1].text = theSel.options[i].text;
            theSel.options[i].value = tmpValue;
            theSel.options[i].text = tmpText;
            theSel.options[i-1].selected = true;
            theSel.options[i].selected = false;
            newPosLastObjMoved = i-1;
          } else {
            newPosLastObjMoved = i;
          }
        } else {
          newPosLastObjMoved = i;
        }
      }
    }
  }

  //////////////////////////////////////////
  // Nome:        moveDownOptions
  // Descrizione: Aggiorna una select spostando tutte le opzioni selezionate gia' di una posizione.
  // Nel caso in cui un'opzione sia gia' giunta all'ultima riga, le opzioni selezionate tenderanno
  // a comprimersi nelle ultime posizioni spostando mano a mano le opzioni non selezionate e comprese
  // tra le opzioni selezionate in posizioni precedenti a tutti gli elementi selezionati.
  // Argomenti:
  //	theSel - oggetto di tipo select in cui spostare i campi verso il basso
  //////////////////////////////////////////
  function moveDownOptions(theSel) {
    var selLength = theSel.length;
    var newPosLastObjMoved = -1;
    var tmpValue = null;
    var tmpText = null;

    for (var i = selLength - 1; i  >= 0 ; i--) {
      if (theSel.options[i].selected && theSel.options[i].value != "") {
        if (i < (selLength - 1)) {
          if (i != (newPosLastObjMoved-1)) {
            // sposto l'elemento se esiste uno spazio occupabile sotto
            // e se non e' l'ultimo elemento
            tmpValue = theSel.options[i+1].value;
            tmpText = theSel.options[i+1].text;
            theSel.options[i+1].value = theSel.options[i].value;
            theSel.options[i+1].text = theSel.options[i].text;
            theSel.options[i].value = tmpValue;
            theSel.options[i].text = tmpText;
            theSel.options[i+1].selected = true;
            theSel.options[i].selected = false;
            newPosLastObjMoved = i+1;
          } else {
            newPosLastObjMoved = i;
          }
        } else {
          newPosLastObjMoved = i;
        }
      }
    }
  }
	
	//////////////////////////////////////////
  // Nome:        moveOptions
  // Descrizione: Aggiorna sia la select sorgente che la select di destinazione,
  //              rimuovendo le opzioni selezionati nella select sorgente ed 
  //						  inserendole nella select di destinazione
  // Argomenti:
  //	theSelFrom - oggetto di tipo select da cui estrarre tutte le opzioni
  //	theSelTo   - oggetto di tipo select in cui inserire in coda le opzioni
  //////////////////////////////////////////
  function moveOptions(theSelFrom, theSelTo) {
  	copyOptions(theSelFrom, theSelTo);
	deleteOptions(theSelFrom);
  }
	
	//////////////////////////////////////////
  // Nome:        moveAllOptions
  // Descrizione: Aggiorna sia la select sorgente che la select di destinazione,
  //              rimuovendo tutte le opzioni della select sorgente ed 
  //						  inserendole nella select di destinazione
  // Argomenti:
  //	theSelFrom - oggetto di tipo select da cui estrarre tutte le opzioni
  //	theSelTo   - oggetto di tipo select in cui inserire in coda le opzioni
  //////////////////////////////////////////
  function moveAllOptions(theSelFrom, theSelTo) {
  	copyAllOptions(theSelFrom, theSelTo);
	deleteAllOptions(theSelFrom);
  }
	
	/**************************************************************
	 Funzione che esegue il lancio delle popUp con l'elenco dei modelli da generare
	 @param applicationPath path dell'applicazione
	 @param entita Nome dell'entita di partenza del testo
	 @param chiavi Elenco dei campi chiave divisi da ;. Es. "CODLAV;NAPPAL"
	 @param valori Oggetto contenente il valore o stringa con le chiavi (sempre divise da ;)
	 @return Visualizza una popUp con la lista dei modelli per la selezione di quello da eseguire
	 
	 @author Marco Franceschin
	 @changelog
	 	14.09.2006 M.F. Prima Versione
		18.09.2006 M.F. Se i valori non sono un'oggetto allora aggiungo ! all'inizio cosi la lista dei modelli da comporre non legge come oggetto
		12.12.2006 M.F.  Apertura della finestra di popUp con la funzione centralizzata
	***************************************************************/
	function compositoreModelli(applicationPath,entita,chiavi,valori){
		oggetto=null;
		try{
			oggetto=eval('document.'+valori);
		}catch(e){
		}
		if(oggetto==null){
			valori='!'+valori;
		}
		openPopUpAction(applicationPath+'/geneGenmod/FwLeggiValoriChiavi.do', csrfToken+'&entita='+entita+'&nomeChiavi='+chiavi+'&valori='+valori,"");
	}
	
	/**************************************************************
	Funzione che esegue la conversione di un valore per il compositore. 
	Aggiunge il carattere eliminatore \ e lo aggiunge prima dei caratteri speciali \ e ;
	 @param	Valore da convertire
	 @return Stringa divisa da ;
	 
	 @author Marco Franceschin
	 @changelog
	 	15.09.2006: M.F. Prima Versione
	***************************************************************/
	function convertiValorePerCompo(valore){
		valore=valore.replace('\\','\\\\');
		valore=valore.replace(';','\\;');	
		return valore;
	}
	
/* Variabile utilizzata per definire che suesta finestra fa parte delle finestre generali dell'applicativo  */
var finestraGeneraleEldasoft="EldaSoft";

function isWindowEldasoft(win){
	/***********************************************************
		Funzione che verifica che una finestra sia una finestra del tipo eldasoft
		@param win
			Finestra da verificare
		@changelog
			26/02/2007 M.F. Prima versione
	 ***********************************************************/

	try{
		if(win.finestraGeneraleEldasoft=="EldaSoft")
			return true;
	}catch(e){
	}
	return false;
}

function getNumeroPopUp(){
	/***********************************************************
		Funzione che ritrova che livello di popUp ? la finestra
		@changelog
			22/11/2006 M.F. Prima versione
			26/02/2007 M.F. Aggiunta del controllo delle variabile che identifica una finestra eldasoft
	 ***********************************************************/
	var numOpener=0;
	var wOpener;
	
	try{
		wOpener=window;
		while(wOpener!=null && isWindowEldasoft(wOpener)){
			wOpener=wOpener.opener;
			if(wOpener!=null && isWindowEldasoft(wOpener))
				numOpener++;
		}
	}catch(e){
	}
	return numOpener;
}
/***********************************************************
	Funzione che si incarica ad andare ad una determinata posizione sull'history
	@param numero
		Numero dell'history nel quale spostarsi
	@changelog
		27/10/2006 M.F. Prima versione
 ***********************************************************/
function historyVaiA(numero){
	closePopUps();
	bloccaRichiesteServer();
	window.location=contextPath+'/History.do?metodo=vaia&numero='+numero+'&numeroPopUp='+getNumeroPopUp();
}

function historyVaiIndietroDi(numeroPassi){
	closePopUps();
	bloccaRichiesteServer();
	window.location=contextPath+'/History.do?metodo=vaiIndietroDi&numero='+numeroPassi+'&numeroPopUp='+getNumeroPopUp();
}

function historyBack(){
	closePopUps();
	bloccaRichiesteServer();
	window.location=contextPath+'/History.do?metodo=back&numeroPopUp='+getNumeroPopUp();
}
function historyReload(){
	closePopUps();
	bloccaRichiesteServer();
	window.location=contextPath+'/History.do?metodo=reload&numeroPopUp='+getNumeroPopUp();
}
function historyReloadWithoutPopUps(){
	bloccaRichiesteServer();
	window.location=contextPath+'/History.do?metodo=reload';
}

function callObjFn(objName,functionName,param){
	/***********************************************************
		Funzione che chiama una funzione in un oggetto
		@changelog
			31/10/2006 M.F. Prima versione
	 ***********************************************************/
	 var obj;
	 
	 try{
		// A questo punto chiamo la vera e propria funzione
		return eval(objName+'.'+functionName+'(param);');
	 }catch(e){
		outMsg(objName+"."+functionName+"("+param+"):"+e.message,"ERR");
	 }
}

	//////////////////////////////////////////
  // Nome:        openPopUp
  // Descrizione: Funzione per l'apertura di una finestra di popup al centro
  //							dello schermo di dimensioni standard (700 x 500), ridimensionabile
  //							e scrollabile.
  //							Su tale finestra viene sempre invocata la action ApriPopup.do,
  //							con gli eventuali parametri specificati nell'argomento href.
  // Argomenti:
  //	href      - stringa dei parametri da passare alla popup in apertura
  //  name		  - nome della finestra
  // 
  // Osservazione:
  // href va a costituire l'URL, che verra' invocata dalla popup. Esempio:
  //
  // var par = "metodo=stampaBozza&key=" + chiave";
  // openPopUp(par, "popupDiEsempio");
  // 
  // La popup viene chiamata invocando la seguente URL:
  //
  // ${pageContext.request.contextPath}/ApriPopup.do?metodo=stampaBozza&key=" + chiave
  //
  //////////////////////////////////////////
function openPopUp(href, name){
	return openPopUpActionCustom(null, href, name, 700, 500, "yes","yes");
}

  //////////////////////////////////////////
  // Nome:        openPopUpCustom
  // Descrizione: Funzione per l'apertura di una finestra di popup al centro
  //							dello schermo di dimensioni specificate in ingresso
  //							- width = larghezza (pixel)
  //							- heigth = altezza (pixel)
  //							- resizable = finestra ridimensionabile: booleano (true, false)
  //							- scrollable = attiva scrollbar: booleano (true, false).
  //							Su tale finestra viene sempre invocata la action ApriPopup.do,
  //							con gli eventuali parametri specificati nell'argomento href.
  // Argomenti:
  //	href      - stringa dei parametri da passare alla popup in apertura
  //  name		  - nome della finestra
  // 
  // Osservazione:
  // l'argomento va a costituire l'URL, che verra' invocata dalla popup. Esempio:
  //
  // var par = "metodo=stampaBozza&key=" + chiave";
  // openPopUpCustom(par, "popupDiEsempio", 500, 350, "no", "no");
  // 
  // La popup viene chiamata invocando la seguente URL:
  //
  // ${pageContext.request.contextPath}/ApriPopup.do?href=metodo=stampaBozza&key=" + chiave
  //
  //////////////////////////////////////////
function openPopUpCustom(href, name, width, height, resizable, scrollable){
	return openPopUpActionCustom(null, href, name, width, height, resizable, scrollable);
}

  //////////////////////////////////////////
  // Nome:        openPopUpAction
  // Descrizione: Funzione per l'apertura di una finestra di popup al centro
  //							 dello schermo di dimensioni standard (700 x 500), ridimensionabile
  //							e scrollabile.
  //							Su tale finestra viene invocata la action specificata nell'argomento
  //							action, con gli eventuali parametri specificati nell'argomento
  //							href.
  //							
  // Argomenti:
  //	action		- azione da invocare all'apertura della popup (compreso il contextPath)
  //	href      - stringa dei parametri da passare alla popup in apertura
  //  name		  - nome della finestra
  // 
  // Osservazione:
  // i due argomenti action e href vanno a costruire l'URL, che verra' invocata
  // dalla popup. Esempio:
  //
  // var act = "${pageContext.request.contextPath}/cs/StampaDomanda.do";
  // var par = "metodo=stampaBozza&key=" + chiave";
  // openPopUpActionCustom(act, par, 'popupDiEsempio', 500, 350, "no", "no);
  // 
  // La popup viene chiamata invocando la seguente URL:
  //
  // ${pageContext.request.contextPath}/cs/StampaDomanda.do?metodo=stampaBozza&key=" + chiave
  //
  //////////////////////////////////////////
function openPopUpAction(action, href, name){
	return openPopUpActionCustom(action, href, name, 700, 500, "yes","yes");
}

	//////////////////////////////////////////
  // Nome:        openPopUpActionCustom
  // Descrizione: Funzione per l'apertura di una finestra di popup al centro
  //							 dello schermo di dimensioni specificate in ingresso:
  //							- width = larghezza (pixel)
  //							- heigth = altezza (pixel)
  //							- resizable = finestra ridimensionabile: booleano (true, false)
  //							- scrollable = attiva scrollbar: booleano (true, false).
  //							Su tale finestra viene invocata la action specificata nell'argomento
  //							action, con gli eventuali parametri specificati nell'argomento
  //							href.
  //							
  // Argomenti:
  //	action		- azione da invocare all'apertura della popup (compreso il contextPath)
  //	href      - stringa dei parametri da passare alla popup in apertura
  //  name		  - nome della finestra
  //  width     - larghezza della popup
  //  height    - altezza della popup
  //  resizable - finestra ridimensionabile: booleano (true, false)
  //  scrolable - attiva scrollbar booleano (true, false)
  // 
  // Osservazione:
  // i due argomenti action e href vanno a costruire l'URL, che verra' invocata
  // dalla popup. Esempio:
  //
  // var act = "${pageContext.request.contextPath}/cs/StampaDomanda.do";
  // var par = "metodo=stampaBozza&key=" + chiave";
  // openPopUpActionCustom(act, par, 'popupDiEsempio', 500, 350, "no", "no");
  // 
  // La popup viene chiamata invocando la seguente URL:
  //
  // ${pageContext.request.contextPath}/cs/StampaDomanda.do?metodo=stampaBozza&key=" + chiave
  //
  //////////////////////////////////////////
function openPopUpActionCustom(action, href, name, width, height, resizable, scrollable){
	// Come prima cosa chiudo tutte le popup
	// NOTA: attenzione, in caso di chiusura e riapertura della stessa popup, con IE
	// la chiusura non avviene qui ma erroneamente solo alla fine, per cui conviene modificare
	// il nome della prima popup aperta prima di scatenare l'apertura della seconda sulla prima
	closePopUps();

  var w = width;
  var h = height;
  var l = Math.floor((screen.width-w)/2);
	var t = Math.floor((screen.height-h)/2);
	var numOpener = getNumeroPopUp()+1;
	
	l = l - 30 + (numOpener * 30);
	t = t - 50 + (numOpener * 50);
	
	if(name == null || name == "")
		name = "popUp" + numOpener;
		
	var tmpHREF = "";
	
	if(action != null && action != ""){
		tmpHREF = action;
		
		if(href != null && href != "")
			tmpHREF += "?" + href + "&numeroPopUp=" + numOpener;
		else
			tmpHREF += "?numeroPopUp=" + numOpener;
	} else {
		tmpHREF = contextPath + "/ApriPopup.do";
		
		if(href != null && href != "")
			tmpHREF += "?" + href + "&numeroPopUp=" + numOpener;
		else
			tmpHREF = "";
	}

	var popup = window.open(tmpHREF,name, "toolbar=no,menubar=no,width=" + w + ",height=" + h + ",top=" + t + ",left=" + l + ",resizable=" + resizable + ",scrollbars="+scrollable);

	if (popup) {
		popup.focus();
		// Setto la popup corrente con quella appena aperta
		currentPopUp = popup;
	}
	return popup;
}

/*************************************************************************
	Funzione JS per apertura di un file su una popup con la barra dei menu. 
	Tale popup non ha vincoli di focus. 
 *************************************************************************/
  //////////////////////////////////////////
  // Nome:        apriDocumento
  // Descrizione: Apertura su una popup con la barra dei menu del file passato
  // 							in argomento. La popup non vincoli di focus
  // Argomenti:
  //	nomeFile - stringa con path e nome file da aprire
  //////////////////////////////////////////
function apriDocumento(nomeFile){
	var w = 800;
	var h = 600;
	var l = Math.floor((screen.width-w)/2);
	var t = Math.floor((screen.height-h)/2); 
	var href = "";
	if(nomeFile.indexOf(":") < 0)
		href = "\\" + nomeFile;
	else
		var href = nomeFile;

	window.open(href,"","toolbar=no,menubar=yes,width=" + w + ",height=" + h + ",top=" + t + ",left=" + l + ",resizable='true',scrollbars='true'");
}

/***********************************************************
	Funzioni JavaScript per gestire le finestre di tipo popUp
	@changelog
		03/11/2006 M.F. Prima versione
 ***********************************************************/
var currentPopUp=null;

function postFocusToPopUp(){
	if(currentPopUp!=null){
		try{
			currentPopUp.focus();
		}catch(e){
			currentPopUp==null
		}
	}
}
function fnFocus(){
	if(currentPopUp!=null){
		try{
			setTimeout("postFocusToPopUp();",10);
		}catch(e){
		}
	}else{
		var wOpener=window.opener;
		var popUpOpener;
		if(wOpener!=null && isWindowEldasoft(wOpener)){
			wOpener.currentPopUp=window;
		}
	}
}

function closePopUps(){
	// Funzione che chiude i popUp aperti
	try{
		if(currentPopUp!=null){
			currentPopUp.close();
			currentPopUp=null;
		}
	}catch(e){
	}
}


function setCurrentPopUpAtOpener(){
	var wOpener=window.opener;
	if(wOpener!=null && isWindowEldasoft(wOpener)){
		try{
			wOpener.currentPopUp=window;
		} catch(e){
		}
	}
}

function reloadPage(){
	history.go(0);
}

setCurrentPopUpAtOpener();

// Funzione che permette di tornare alla home dato il codice applicazione
function goHome(codApp) {
  window.location=contextPath+'/ApriPagina.do?href=home'+codApp+'.jsp';
}


var bloccoPagina = false;

//////////////////////////////////////////
// Nome:        bloccaRichiesteServer
// Descrizione: svuota il contenuto dei link e disabilita i bottoni in modo da
//              non permettere ulteriori inoltri di richieste al server. Il server
//              elaborera' solo la prima richiesta inviatagli
//
// Ritorna:     <e' una procedura>
//////////////////////////////////////////
function bloccaRichiesteServer() {
	bloccoPagina = true;
}

//////////////////////////////////////////
// Nome:        bloccaCaratteriDaTastiera
// Descrizione: procedura da associare ad un evento per bloccare l'utilizzo della tastiera
//              per scrivere qualsiasi carattere in un campo
// Ritorna:     <e' una procedura>
//////////////////////////////////////////
function bloccaCaratteriDaTastiera(e) {
	var key = window.event ? e.keyCode : e.which;
	e.cancelBubble = true;
	e.returnValue = false;
	return false;
}

function checkParIva(pi){
	/**************************************************************	 
		Funzione che verifica la correttezza di una partita iva
		@param piva Partita iva da controllare
		@return true se esatta; false se ci sono problemi
		@author Marco Franceschin
		@changelog
			05.11.2007: M.F. Prima Versione
	***************************************************************/
	if(pi == null || pi.length ==0)
		return true;
	if(pi.length!=11)
		return false;

	var validi = "0123456789";
	for( i = 0; i < 11; i++ ){
		if( validi.indexOf( pi.charAt(i) ) == -1 )
			return false;
	}
  var s = 0;
	var c;
	for( i = 0; i <= 9; i += 2 )
		s += pi.charCodeAt(i) - '0'.charCodeAt(0);
	for( i = 1; i <= 9; i += 2 ){
			c = 2*( pi.charCodeAt(i) - '0'.charCodeAt(0) );
			if( c > 9 )  c = c - 9;
			s += c;
	}
	// Se il carattere di controllo non corrisponde allora da l'errore
	if( ( 10 - s%10 )%10 != pi.charCodeAt(10) - '0'.charCodeAt(0) )
		return false;
  return true;
}

function checkCodFis(cf){
	/**************************************************************	 
		Funzione che verifica la correttezza di un codice fiscale
		@param codice Codice fiscale da verificare
		@return true OK; false codice sbagliato
		@author Marco Franceschin
		@changelog
			05.11.2007: M.F. Prima Versione
	***************************************************************/
	var validi, i, s, set1, set2, setpari, setdisp;
	
	if(cf == null || cf.length ==0)
		return true;
	// Se il primo carattere ? un numero si tratta di una partita iva
	if("1234567890".indexOf(cf.charAt(0))>=0)
		return checkParIva(cf);
	if(cf.length!=16)
		return false;	
	cf = cf.toUpperCase();
	
	validi = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	for( i = 0; i < 16; i++ ){
		// Se non ? tra i caratteri validi da errore
		if( validi.indexOf( cf.charAt(i) ) == -1 )
				return false;
	}
	var set1 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var set2 = "ABCDEFGHIJABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var setpari = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var setdisp = "BAKPLCQDREVOSFTGUHMINJWZYX";
	var s = 0;
	for( i = 1; i <= 13; i += 2 )
			s += setpari.indexOf( set2.charAt( set1.indexOf( cf.charAt(i) )));
	for( i = 0; i <= 14; i += 2 )
			s += setdisp.indexOf( set2.charAt( set1.indexOf( cf.charAt(i) )));
	if( s%26 != cf.charCodeAt(15)-'A'.charCodeAt(0) )
			return false;
	return true;
}

/***************************************************************************
 * Funzione per intercettera e bloccare l'evento di pressione del pulsante
 * INVIO (o ENTER). Funziona sia con IE che con FireFox
 * @param evento scatenato sull'oggetto della pagina HTML
 * @return ritorna false se viene premuto il tasto INVIO (o ENTER)
 * Esempio d'uso:
 * <input type="text" value=...  onkeypress="javascript:return bloccoPulsanteInvio;" />
 *****************************************************************************/
function bloccoPulsanteInvio(evento){
	return !((window.event && window.event.keyCode == 13) || (evento && evento.which == 13));
}


  
  //////////////////////////////////////////
  // Nome:        round
  // Descrizione: arrotonda il numero in base al numero di decimali indicati come parametro
  // Argomenti:
  //	numero   - numero da arrotondare
  //	decimali - numero di decimali, se negativo si arrotonda alla decina, centinaia, ....
  //
  // Ritorna:     numero arrotondato
  //////////////////////////////////////////
  function round(numero, decimali) {
    var potenza10 = Math.pow(10, decimali);
    return Math.round(numero * potenza10) / potenza10;
  }

  ////////////////////////////////////////////
  // Nome:			toVal
  // Descrizione:	trasforma il dato passato in valore numerico
  // Argomenti:
  //	valore - valore da trasformare
  //
  // Ritorna:		valore numerico
  ///////////////////////////////////////////
  function toVal(valore){
	if(valore=="" || valore==null )
		valore=0;
	else
		valore=eval(valore);
	return valore;
  }
  
  //////////////////////////////////////////
  // Nome:        fillCharLeft
  // Descrizione: inserisce "carattere" alla sinistra di "stringa" fino al riempimento di "len" caratteri
  // Argomenti:
  //	stringa   - stringa da fillare
  //	len       - lunghezza massima della stringa ritornata
  //	carattere - filler
  //
  // Ritorna:     stringa riempita a sinistra con il carattere in input
  //////////////////////////////////////////
	function fillCharLeft(stringa, len, carattere){
		var ret = "";
		var lunghezzaStringa = (stringa != null ? stringa.length : 0); 
		for(var i = 0; i < len - lunghezzaStringa; i++)
			ret += carattere;
		if (lunghezzaStringa > 0)
			ret += stringa;
		return ret.substring(0, len);
	}

  //////////////////////////////////////////
  // Nome:        fillCharRight
  // Descrizione: inserisce "carattere" alla destra di "stringa" fino al riempimento di "len" caratteri
  // Argomenti:
  //	stringa   - stringa da fillare
  //	len       - lunghezza massima della stringa ritornata
  //	carattere - filler
  //
  // Ritorna:     stringa riempita a destra con il carattere in input
  //////////////////////////////////////////
	function fillCharRight(stringa, len, carattere){
		var ret = "";
		var lunghezzaStringa = (stringa != null ? stringa.length : 0); 
		for(var i = 0; i < len - lunghezzaStringa; i++)
			ret += carattere;
		if (lunghezzaStringa > 0)
			ret = stringa + ret;
		return ret.substring(0, len);
	}
  
  //////////////////////////////////////////
  // Nome:        getValueCheckedRadio
  // Descrizione: estrae il valore del radio selezionato
  // Argomenti:
  //	radioName   - name del radiobutton
  //
  // Ritorna:     stringa vuota se il radio non esiste o non ha alcun 
  // valore selezionato, il valore del radio selezionato altrimenti
  //////////////////////////////////////////
	function getValueCheckedRadio(radioName) {
		if(!radioName)
			return "";
		var radioLength = radioName.length;
		if(radioLength == undefined)
			if(radioName.checked)
				return radioName.value;
			else
				return "";
		for(var i = 0; i < radioLength; i++) {
			if(radioName[i].checked) {
				return radioName[i].value;
			}
		}
		return "";
	}
  
  //////////////////////////////////////////
  // Nome:        openPopupGrafico
  // Descrizione: apre la popup ed esegue il submit del form predefinito 
  // (da inserire cmq nella pagina) per la generazione di un grafico
  // Argomenti:
  //	form   - nome del form usato per contenere i dati da inviare per la 
  //           creazione del grafico
  //  width  - larghezza della popup
  //  height - altezza della popup
  //////////////////////////////////////////
	function openPopupGrafico(form, width, height) {
 		var popUpLocal = openPopUpCustom("", "grafico", width, height, "yes", "yes");
		form.target = popUpLocal.name;
		form.submit();
	}
	
	
	
	//////////////////////////////////////////
	// Nome:        trovaVisualizzazioneAvanzata
	// Descrizione: utilizzata dalla checkbox, presente nella form di trova,
	// per abilitare/disabilitare la visualizzazione avanzata degli operatori
	// di trova
	// 
	//////////////////////////////////////////
	function trovaVisualizzazioneAvanzata() {
		var checkboxavanzate = document.getElementById("visualizzazioneAvanzata");
		var test = checkboxavanzate.checked;
		if (test == true) {
			trovaVisualizzazioneOperatori('visualizza');
		} else {
			trovaNuova();
		}
	}
	
	
	//////////////////////////////////////////
	// Nome:        trovaVisualizzazioneOperatori
	// Descrizione: gestisce la visualizzazione degli operatori
	// avanzati nella maschera di trova
	// Argomenti:
	//	vis - stringa ("visualizza" per visualizzare la colonna
	//                  degli operatori)
	//////////////////////////////////////////
	function trovaVisualizzazioneOperatori(vis) {
	
		var tablesricerca = [];
		
		var tables = document.getElementsByTagName('table');
		var ricercaclass = 'ricerca';
		var regularexp = new RegExp('\\b' + ricercaclass + '\\b');
		for (var i=0; i < tables.length; i++) {
			if (regularexp.test(tables[i].className)) {
				tablesricerca.push(tables[i]);
			}
		}
		
		if (tablesricerca.length > 0) {
			var rows = tablesricerca[0].getElementsByTagName('tr');			
		
			for (var row=0; row<rows.length; row++) {
				var cels = rows[row].getElementsByTagName('td');
				if (cels.length >=3) {
					if (vis == "visualizza") {
						cels[1].style.display='';
						cels[2].style.width=478;						
					} else {
						cels[1].style.display='none';
						cels[2].style.width=580;
					}
				}
			}
		}
	}
	
	// GESTIONE DELL'EVIDENZIAZIONE DI UNA RIGA IN UNA LISTA
	
	var arrayOfRolloverClasses = new Array();
	var activeRow = false;
	
	function addTableRolloverEffect(tableId,whichClass)	{
		arrayOfRolloverClasses[tableId] = whichClass;
		
		var tableObj = document.getElementById(tableId);
		var tBody = tableObj.getElementsByTagName('TBODY');
		var rows;
		if(tBody){
			rows = tBody[0].getElementsByTagName('TR');
		}else{
			rows = tableObj.getElementsByTagName('TR');
		}
		for(var no=0;no<rows.length;no++){
			rows[no].onmouseover = highlightTableRow;
			rows[no].onmouseout = resetRowStyle;
		}
	}

	function highlightTableRow() {
		var tableObj = this.parentNode;
		if(tableObj.tagName!='TABLE')tableObj = tableObj.parentNode;

		if(this!=activeRow){
			this.setAttribute('origCl',this.className);
			this.origCl = this.className;
		}
		if (this.className.indexOf('_Hilight2') > 0)
			this.className = arrayOfRolloverClasses[tableObj.id] + '_Hilight2';
		else if (this.className.indexOf('livello') >= 0)
			this.className = arrayOfRolloverClasses[tableObj.id] + '_livello';
		else
			this.className = arrayOfRolloverClasses[tableObj.id];
		
		activeRow = this;
	}
	
	function resetRowStyle() {
		var tableObj = this.parentNode;
		if(tableObj.tagName!='TABLE')tableObj = tableObj.parentNode;
		var origCl = this.getAttribute('origCl');
		if(!origCl) origCl = this.origCl;
		if(origCl) this.className=origCl;
	}

	function findTableDatiLista() {
		var tables = document.getElementsByTagName('table');
		var ricercaclass = 'datilista';
		var regularexp = new RegExp('\\b' + ricercaclass + '\\b');
		for (var i=0; i < tables.length; i++) {
			if (regularexp.test(tables[i].className)) {
				addTableRolloverEffect(tables[i].id,'tableRollOverEffect1');
			}
		}
	}
	
	//////////////////////////////////////////
	// Nome:        scadenzario
	// Descrizione: esegue la chiamata alla pagina per l'apertura della lista attivita' scadenzario
	// Argomenti:
	//	entita - nome entita su cui generare il calendario
	//  chiave - chiave del record da cui aprire lo scadenzario
	//	entitaPrincipaleModificabile - true se si parte da un'entita' modificabile, false altrimenti
	//  discriminante - discriminante sull'entita' per differenziare attivita'/eventi/modelli da proporre
	//////////////////////////////////////////
	function scadenzario(entita, chiave, entitaPrincipaleModificabile, discriminante){
		var href = contextPath + "/ApriPagina.do?href=gene/g_scadenz/g_scadenz-lista.jsp&entitaPartenza=" + entita + "&discriminante=" + discriminante + "&chiave=" + chiave;
		href+="&ScadenzarioModificabile=" + entitaPrincipaleModificabile;
		bloccaRichiesteServer();
		document.location.href = href;
	}

	//////////////////////////////////////////
	// Nome:        calendarioScadenzario
	// Descrizione: esegue la chiamata alla pagina del calendario scadenzario
	// Argomenti:
	//	entita - nome entita su cui generare il calendario; se piu' di una 
	//             separare le entita' con ";"
	//////////////////////////////////////////
	function calendarioScadenzario(entita) {
		bloccaRichiesteServer();
		document.location.href = contextPath + "/CalScadenzario.do?"+csrfToken+"&entita=" + entita;
	}

	function checkBrowser() {
		
		var isBrowserSupportato = true;
		var userAgent = navigator.userAgent;

		var isChrome = userAgent.match(/chrome|chromium|crios/i) && !userAgent.match(/edg/i) && !userAgent.match(/opr\//i);
		var isFirefox = userAgent.match(/firefox|fxios/i);
		var isOpera = userAgent.match(/opr\//i);
		var isEdge = userAgent.match(/edg/i);
		var isSafari = userAgent.match(/safari/i);

		if(!isChrome && !isFirefox && !isOpera && !isEdge && !isSafari){
			isBrowserSupportato = false;
		}
		
		if (!isBrowserSupportato) {
			document.getElementById("browserSupportati").style.display = "";
		}
		
	}
	
	function qformEditor(id,entita,readOnly){
		document.location.href='ApriPagina.do?'+csrfToken+'&href=geneweb/qeditor/qeditor.jsp&ent=' + entita + '&id='+id + '&firstTimer=true&readOnly='+readOnly;
	}
	
	function qformAnteprima(id,entita){
		document.location.href='ApriPagina.do?'+csrfToken+'&href=geneweb/qeditor/anteprimaQform.jsp&ent=' + entita + '&id='+id ;
	}