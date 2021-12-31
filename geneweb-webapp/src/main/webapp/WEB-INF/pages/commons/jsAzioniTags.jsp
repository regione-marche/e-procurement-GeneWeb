<%
  /*
   * Created on 25-ott-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // Pagina che contiene le azioni generali dei tags
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">

function showConfermaPopUp(tipo,classe,variabileKey,fnConferma,fnAnnulla ){
	<%/**************************************************************	 
		Funzione per visualizzare un messaggio di conferma gestito attraverso 
		una classe java
		@param classe Classe Java che gestisce il messaggio
		@param variabileKey Variabile che contiene i campi chiave
		@param fnConferma Funzione javascript da lanciare alla conferma
		@param fnAnnulla Funzione javascript da lanciare all'annullamaneto
		@author Marco Franceschin
		@changelog
			30.11.2007: M.F. Prima Versione
	***************************************************************/%>
	// Inizializzazioni
	var lAction=document.forms[0].action;
	var lTarget=document.forms[0].target;
	var lNewAction="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=commons/conferma-popup.jsp";
	
	if(fnConferma==null)fnConferma="";
	if(fnAnnulla==null)fnAnnulla="";
	
	lNewAction+="&tipo="+tipo;
	lNewAction+="&classe="+classe;
	lNewAction+="&keyVar="+variabileKey;
	lNewAction+="&scriptConferma="+fnConferma;
	lNewAction+="&scriptAnnulla="+fnAnnulla;
	
	document.forms[0].action=lNewAction;
	win=openPopUpCustom(null, null, "confermaMsg", 600, 300, "yes", "yes");
	document.forms[0].isPopUp.value="1";
	document.forms[0].target=win.name;
	document.forms[0].submit();
	if(win!=null)
		win.focus();
	// Ripristino l'azione
	document.forms[0].action=lAction;
	document.forms[0].target=lTarget;
	document.forms[0].isPopUp.value="0";
}

<!-- 
	//////////////////////////////////////////////////////////////////////
	// Azioni del tag della maschera di trova
	//////////////////////////////////////////////////////////////////////
	function trovaEsegui(){
		// Funzione che esegue la trova sulla lista
		// Eseguo il submit delle form
		clearMsg();
		var continua = true;
		var numeroCampi = eval(getValue("campiCount"));
		var i=0;
		for(i; i < numeroCampi; i++){
			if(getTipoCampo("Campo" + i) == "D"){
				var operatoreConfrontoData = getValue("Campo" + i + "_conf");
				if("<.<" == operatoreConfrontoData || "<=.<=" == operatoreConfrontoData){
					if((getValue("Campo" + i + "Da") == "" && getValue("Campo" + i) != "") || (getValue("Campo" + i + "Da") != "" && getValue("Campo" + i) == "")){
						if(getValue("Campo" + i + "Da") == ""){
							outMsg("<a href=\"javascript:selezionaCampo('Campo"+i+"Da');\"" + " title=\"Seleziona il campo\" style=\"color: #ff0000;\">Valorizzare il limite inferiore del filtro sulla data<"+"/a>","ERR");
							continua = false;
						} else if(getValue("Campo" + i) == ""){
							outMsg("<a href=\"javascript:selezionaCampo('Campo"+i+"');\"" + " title=\"Seleziona il campo\" style=\"color: #ff0000;\">Valorizzare il limite superiore del filtro sulla data<"+"/a>","ERR");
							continua = false;
						}
					} else {
						var dataFrom = toDate(getValue("Campo" + i + "Da"));
						var dataTo = toDate(getValue("Campo" + i));
						if(dataFrom > dataTo){
							outMsg("<a href=\"javascript:selezionaCampo('Campo"+i+"Da');\"" + " title=\"Seleziona il campo\" style=\"color: #ff0000;\">Intervallo temporale non valido<"+"/a>","ERR");
							continua = false;
						}
					}
				}
			}
		}

		if(continua){
			document.trova.metodo.value="trova";
			document.trova.submit();
		} else {
			onOffMsgFlag(true);
			alert("Si sono verificati degli errori durante i controlli sui campi");
		}
	}
	
	function trovaNuova(){
		// Funzione che esegue il reset delle trova
		document.trova.metodo.value="nuova";
		document.trova.submit();
	}
	function trovaClear(){
		// Funzione che ripristina i valori precedentemente insertiti
		document.trova.metodo.value="clear";
		document.trova.submit();
	}
	function trovaCreaNuovo(){
		document.trova.jspPathTo.value=document.trova.schedaPerInserimento.value;
		document.trova.metodo.value="nuovo";
		document.trova.submit();
	}

	function trovaVisualizzaDataConfronto(nomeCampo, idCampo, sbiancaCampi){
		var valoreConfronto = getValue(nomeCampo + "_conf");
		if(valoreConfronto.indexOf(".") >= 0){
			if("<.<" == valoreConfronto){
				document.getElementById("span_" + idCampo + "_testo").innerHTML = "&nbsp;data&nbsp;";
				setValue(nomeCampo + "Da_conf", ">");
			} else {
				document.getElementById("span_" + idCampo + "_testo").innerHTML = "&nbsp;data&nbsp;";
				setValue(nomeCampo + "Da_conf", ">=");
			}
			showObj("span_" + idCampo + "Da", true);
			if(sbiancaCampi){
				setValue(nomeCampo, "");
				setValue(nomeCampo + "Da", "");
			}
		} else {
			document.getElementById("span_" + idCampo + "_testo").innerHTML = "";
			showObj("span_" + idCampo + "Da", false);
			if(sbiancaCampi){
				setValue(nomeCampo, "");
				setValue(nomeCampo + "Da", "");
			}
			setValue(nomeCampo + "Da_conf", "");
		}
	}

	//////////////////////////////////////////////////////////////////////
	// Azioni utilizzate dalla maschera a lista
	//////////////////////////////////////////////////////////////////////
	// Qui vengono include le funzioni utilizzate dalla maschera 
	var chiaveRiga;
			
	// Visualizzazione del dettaglio
	function listaVisualizza(){
		document.forms[0].key.value=chiaveRiga;
		document.forms[0].metodo.value="apri";
		document.forms[0].activePage.value="0";
		document.forms[0].submit();
	}

	// Visualizzazione del dettaglio in modifica
	function listaModifica(){
		document.forms[0].key.value=chiaveRiga;
		document.forms[0].metodo.value="modifica";
		document.forms[0].activePage.value="0";
		document.forms[0].submit();
	}
	
	// Variabile per identificare una classe di conferma eliminazione
	var classePopUpElimina=null;
	function listaSetCongermaPopUp(className){
		<%/**************************************************************	 
			Funzione per impostare che l'eliminazione dalla selezione viene trattata con una richiesta di popup
			@author Marco Franceschin
			@changelog
				29.11.2007: M.F. Prima Versione
		***************************************************************/%>
		classePopUpElimina=className;
	}
	
	// Elimina
	function listaElimina(){
		if(classePopUpElimina==null){
			// Eliminazione senza popup di conferma
			if(confirm("Procedere con l'eliminazione?")){
				listaEliminaPopUp();
			}
		}else{
			showConfermaPopUp("elimina",classePopUpElimina,chiaveRiga,"listaEliminaPopUp");
		}
	}
	
	function listaEliminaPopUp(){
		<%/**************************************************************	 
		Eliminazione diretta dell'elemento selezionato 
		@param
		@return
		@author Marco Franceschin
		@changelog
			29.11.2007: M.F. Prima Versione
		***************************************************************/%>
		bloccaRichiesteServer();
		document.forms[0].key.value=chiaveRiga;
		document.forms[0].metodo.value="elimina";
		document.forms[0].submit();
	}
	
	function listaEliminaSelezionePopUp(){
		<%/**************************************************************	 
		Funzione per esecuzione dell'eliminazione delle selezione de popup di conferma
		@author Marco Franceschin
		@changelog
			29.11.2007: M.F. Prima Versione
		***************************************************************/%>
		bloccaRichiesteServer();
		if(chiaveRiga)
			document.forms[0].key.value=chiaveRiga;
		document.forms[0].metodo.value="eliminaSelez";
		document.forms[0].submit();
	}
	
	// Elimina selezione
	function listaEliminaSelezione(){
		var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  if (numeroOggetti == 0) {
	      alert("Nessun elemento selezionato nella lista");
	  } else {
				// Verifico se esiste ina classe per i popup di conferma d'eliminazione
				if(classePopUpElimina==null){
		   	  if (confirm("Sono stati selezionati " + numeroOggetti + " elementi. Procedere con l'eliminazione?")) {
						listaEliminaSelezionePopUp();
					}
				}else{
					showConfermaPopUp("elimina",classePopUpElimina,"keys","listaEliminaSelezionePopUp");
				}
		}
	}
	
	
	
	// Nuovo
	function listaNuovo(){
		//document.forms[0].key.value=chiaveRiga;
		document.forms[0].metodo.value="nuovo";
		document.forms[0].activePage.value="0";
		bloccaRichiesteServer();
		document.forms[0].submit();
	}
	// Ritorna alla ricerca
	function listaRicerca(){
		document.forms[0].key.value=chiaveRiga;
		document.forms[0].metodo.value="ricerca";
		document.forms[0].submit();
	}
	
	// Funzione che si sposta ad una determinata pagina
	function listaVaiAPagina(numpg){
		document.forms[0].metodo.value="leggi";
		document.forms[0].pgVaiA.value=numpg;
		document.forms[0].key.value=document.forms[0].keyParent.value;
		document.forms[0].submit();
	}
	
	// Funzione che esegue l'ordinamento su una determinata colonna (gestito nella formListaTag)
	function listaOrdinaPer(campo){
		document.forms[0].metodo.value="leggi";
		document.forms[0].pgVaiA.value=0;
		document.forms[0].pgSort.value=campo;
		if (document.pagineForm)
			document.forms[0].entita.value=document.pagineForm.entita.value;
		document.forms[0].key.value=document.forms[0].keyParent.value;
		document.forms[0].submit();
	}
	
	// Funzione che esegue la riapertura della pagina visualizzata in modalità modifica
	function listaApriInModifica() {
		document.forms[0].updateLista.value = "1";
		bloccaRichiesteServer();
		listaVaiAPagina(document.forms[0].pgCorrente.value);
	}

	// Funzione che annulla le modifiche alla lista e consente la riapertura della pagina in visualizzazione
	function listaAnnullaModifica(){
		document.forms[0].updateLista.value = "0";
		bloccaRichiesteServer();
		listaVaiAPagina(document.forms[0].pgCorrente.value);
	}
	
	
	// Funzione che esegue l'inoltro della chiamata per salvare i dati modificati nella lista
	function listaConferma() {
		document.forms[0].metodo.value = "updateLista";
		document.forms[0].key.value = document.forms[0].keyParent.value;
		document.forms[0].pgVaiA.value = document.forms[0].pgCorrente.value;
		document.forms[0].updateLista.value = "0";
		bloccaRichiesteServer();
		document.forms[0].submit();
	}
	
	// Funzione che esegue il settaggio dei campi e la chiusura della finestra
	function archivioSeleziona(arrayValori){
		var element;
		var close=true;
		try{
			parentForm=eval('window.opener.activeForm');
		}catch(e){
			outMsg(e.message);
			close=false;
		}
		//lForm=getObjectById('archivioReq');
		// {M.F. 20/11/2006} I campi di collegamento all'archivio sono stati inglobati nella lista
		lForm=document.forms[0];
		lArray=lForm.archCampi.value.split(";");
		for(i=0;i<lArray.length;i++){
			element=parentForm.getCampo(lArray[i]);
			if(element!=null){
				parentForm.setValue(lArray[i],arrayValori[i]);
			}else{
				outMsg("Non esiste la colonna "+lArray[i]+" nella pagina chiamante !");
				close=false;
			}
		}
		if(close)
			window.close();
	}
	
	// Funzione che esegue la selezione di una scheda sull'archivio
	function archivioSelezionaScheda(){
		// Inizializzazioni
		var element;
		var parentForm;
		var formDati=document.forms[0];
		var lClose=true;
		var lArray=archivioSchedaForm.archCampiArchivio.value.split(";");
		var lArrayDest=archivioSchedaForm.archCampi.value.split(";");
		var i;
		var lCampo;
		var campoSurce;
		var campoDest;
		
		try{
			parentForm=eval('window.opener.activeForm');
		}catch(e){
			outMsg(e.message);
			return;
		}
		formDati=eval("local"+formDati.name);
		//alert(formDati);
		// Scorro tutti i campi da settare
		for(i=0;i<lArray.length;i++){
			lCampo=lArray[i].replace(".","_");
			campoSurce=formDati.getCampo(lCampo);
			if(campoSurce!=null){
				campoDest=parentForm.getCampo(lArrayDest[i]);
				if(campoDest!=null){
					parentForm.setValue(lArrayDest[i],campoSurce.getValue());
				}else{
					outMsg("Non esiste la colonna "+lArrayDest[i]+" nella pagina chiamante !");
					lClose=false;
				}
			}else{
				outMsg("Non esiste la colonna "+lCampo+" nella pagina !");
				lClose=false;
			}
			
		}
		if(lClose)
			window.close();
	}
	
	
	
	//////////////////////////////////////////////////////////////////////
	// Comandi esistenti nella scheda
	//////////////////////////////////////////////////////////////////////
	// Classe per la gestione del messaggio di salvataggio
	var classeSchedaConfermaPopUp=null;
	function schedaSetCongermaPopUp(className){
		<%/**************************************************************	 
			Funzione per impostare che l'eliminazione dalla selezione viene trattata con una richiesta di popup
			@author Marco Franceschin
			@changelog
				29.11.2007: M.F. Prima Versione
		***************************************************************/%>
		classeSchedaConfermaPopUp=className;
	}
	
	function schedaModifica(){
		document.forms[0].metodo.value="modifica";
		document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
		bloccaRichiesteServer();
		document.forms[0].submit();
	}
	function schedaAnnulla(){
		document.forms[0].metodo.value="annulla";
		bloccaRichiesteServer();
		document.forms[0].submit();
	}
	
	function schedaConfermaPopUp(){
		
		if(arrayCampiObbligatori != null && arrayCampiObbligatori.length > 0){
		<%
			// La seguente istruzione JS viene eseguita solo se nella jsp e' stata
			// settata a true la variabile globale JS controlloSezioniDinamiche
		%>
      if(controlloSezioniDinamiche){
  		<%// La seguente funzione e' definita nel file forms.js: per i dettagli si 
  		  // rimanda al commento di tale funzione.
  		  // 
				// Questa funzione JS serve per gestire i seguenti casi:
				// - se una sezione dinamica e' stata visualizzata, ma nessuno dei suoi
				//   campi e' stato popolato, il controllo sui eventuali campi obbligatori
				//	 non deve scattare, visto che tale sezione non verra' salvata dal
				//   gestore dell'entita'.
				// - se una sezione dinamica e' nascosta (perche' e' stata cancellata (1), o 
				//   non e' mai stata visualizzata (2), il controllo sui eventuali campi 
				//   obbligatori non deve scattare, visto che tale sezione:
				//   * non verra' salvata dal gestore dell'entita' nel caso (2);
				//   * verra' cancellata nel caso (1).
				//
				// Tale funzione deve effettuare il tipo di controllo appena descritto per
				// tutte le sezioni dinamiche presenti nella jsp, andando a determinare il
				// valore dell'attributo di obbligatorieta' di ciascun oggetto Check
				// relativo ad ogni campo presente nella sezione dinamica  %>

				controlloValorizzazioneSezioniDinamicheVisualizzate(arraySezioniDinamicheObj);
			<%
			  // Per un esempio vedere le seguenti pagine:
				// - \pages\gene\impr\impr-categorieIscrizione.jsp (con campi del generatore attributi)
				// - \pages\gene\impr\impr-raggruppamento.jsp
				// - \pages\gene\impr\impr-interno-scheda.jsp e le jsp in essa incluse:
				//   impr-legaliRappresentanti.jsp e impr-direttoriTecnici.jsp
				// - \pages\gene\impr\impr-interno-scheda.jsp
				// - \pages\lavo\appa\categorie-appalto.jsp
				// - \pages\lavo\   ..... \   fatture            da fare
				// - \pages\lavo\   ..... \   Variazioni tempo   da fare
				%>
      }
		}

		// Eseguo il submit del form, non prima di aver controllato
		// l'obbligatorieta' dei campi presenti
		if(activeForm.onsubmit()){
			bloccaRichiesteServer();
			document.forms[0].submit();
		} else
			return false;
	}

	function schedaConferma(){
		if(classeSchedaConfermaPopUp==null){
			document.forms[0].metodo.value="update";
			schedaConfermaPopUp();
		}else{
			var chiave;
			if(document.forms[0].key.value!="")
				chiave="key";
			else
				chiave="keyParent";
			showConfermaPopUp("salva",classeSchedaConfermaPopUp,"elencoCampi","schedaConfermaPopUp");
		}
	}

	function schedaNuovo(){
		document.forms[0].metodo.value="nuovo";
		document.forms[0].activePage.value="0";
		bloccaRichiesteServer();
		document.forms[0].submit();
	}
	
	//////////////////////////////////////////////////////////////////////
	// Comandi delle maschere a pagine
	//////////////////////////////////////////////////////////////////////
	// Funzione che esegue la selezione di una pagina
	function selezionaPagina(pageNumber){
		document.pagineForm.activePage.value=pageNumber;
		document.pagineForm.submit();
	}
	
	function pagineModifica(){
		alert("Modifica delle pagine");
	}
	function pagineNuovo(){
		alert("Esegue l'inserimento di un nuovo elemento");
	}
	function pagineRicerca(){
		alert("Ritorna alla ricerca");
	}
	function pagineLista(){
		alert("Ritorna alla ricerca");
	}
	
	
	// Lista documenti associati
	function documentiAssociati(){
		var entita,valori;
		var key = "";
		var keyParent = "";
	  try {
      entita = document.forms[0].entita.value;
		  if(document.forms[0].key.value != ''){
        valori = document.forms[0].key.value;
      } else if(document.forms[0].keyParent.value != ''){
        valori = document.forms[0].keyParent.value;
      } else if(document.forms[0].keys.value != ''){
        valori = document.forms[0].keys.value;
		  }
		  
		  var href = contextPath+'/ListaDocumentiAssociati.do?'+csrfToken+'&metodo=visualizza&entita='+entita+'&valori='+valori;
			if(document.forms[0].key.value != '')
				href += '&key='+ document.forms[0].key.value;
			if(document.forms[0].keyParent.value != '')
				href += '&keyParent='+document.forms[0].keyParent.value;
		
			document.location.href = href;
  	//document.location.href=contextPath+'/ListaDocumentiAssociati.do?metodo=visualizza&entita='+entita+'&valori='+valori;
    } catch(e) {
	  }
	}
	
	function noteAvvisi(){
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gene/g_noteavvisi/g_noteavvisi-lista.jsp&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
		document.location.href = href;
	}
	
	function selectAll(){
		/***********************************************************
			DEPRECATO: includere iconeCheckUncheck.jsp

			Funzione che seleziona tutti i campi keys della lista
			@changelog
				14/11/2006 M.F. Prima versione
				18/10/2007 S.S. Modificato in quanto con la lista
				composta da un singolo elemento non funziona niente;
				di conseguenza si richiama quanto già realizzato e 
				testato per la parte generatore ricerche e modelli
		 ***********************************************************/
		selezionaTutti(document.forms[0].keys);
	}

	function deselectAll(){
		/***********************************************************
			DEPRECATO: includere iconeCheckUncheck.jsp

			Funzione che deseleziona tutti i campi keys della lista
			@changelog
				14/11/2006 M.F. Prima versione
				18/10/2007 S.S. Modificato in quanto con la lista
				composta da un singolo elemento non funziona niente;
				di conseguenza si richiama quanto già realizzato e 
				testato per la parte generatore ricerche e modelli		 
		 ***********************************************************/
		deselezionaTutti(document.forms[0].keys);
	}
	
	function modelliPredisposti(){
	/***********************************************************
		Funzione che visualizza la lista per la composizione del modello.
		Questa funzione è utilizzabile solo se parete da tag generali elda
		@changelog
			11/12/2006 M.F. Prima versione
			10/01/2007 M.F. Permetto l'avvio del compositore dalla sola pagina principale
	 ***********************************************************/
	 var entita,valori,pagina="0";
	 try{
		pagina=document.forms[0].activePage.value;
	 }catch(e){
	 }
	 if(pagina==""||pagina==null)
			pagina="0";
		
	 
	 try{
		entita=document.forms[0].entita.value;
		if(document.forms[0].key.value!=''){
			valori=document.forms[0].name+".key";
		}else if(document.forms[0].keyParent.value!=''){
			valori=document.forms[0].name+".keyParent";
		}else if(document.forms[0].keys.value!=''){
			valori=document.forms[0].name+".keys";
		}
		compositoreModelli('${pageContext.request.contextPath}',entita,'',valori);
	 }catch(e){
	 }

	}


	//////////////////////////////////////////////////////////////////////
	// Comandi esistenti nelle pagine di un wizard
	//////////////////////////////////////////////////////////////////////
	function wizardAvanti(){
		// si setta il metodo temporaneamente ad "update" per attivare i controlli di obbligatorietà
		document.forms[0].metodo.value="update";
		// Eseguo il submit con il controllo dei campi obbligatori
		if(activeForm.onsubmit()){
			bloccaRichiesteServer();
			document.forms[0].metodo.value="avanti";
			document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
			document.forms[0].submit();
		}
	}
	function wizardIndietro(){
		bloccaRichiesteServer();
		document.forms[0].metodo.value="indietro";
		document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
		document.forms[0].submit();
	}
	function wizardFine(){
		// si setta il metodo temporaneamente ad "update" per attivare i controlli di obbligatorietà
		document.forms[0].metodo.value="update";
		// Eseguo il submit con il controllo dei campi obbligatori
		if(activeForm.onsubmit()){
			bloccaRichiesteServer();
			document.forms[0].metodo.value="fine";
			document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
			document.forms[0].submit();
		}
	}
	function wizardSalva(){
		if(confirm("Chiudere la creazione guidata e procedere \nal salvataggio dei dati inseriti?")){
			// si setta il metodo temporaneamente ad "update" per attivare i controlli di obbligatorietà
			document.forms[0].metodo.value="update";
			// Eseguo il submit con il controllo dei campi obbligatori
			if(activeForm.onsubmit()){
				bloccaRichiesteServer();
				document.forms[0].metodo.value="salva";
				document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
				document.forms[0].submit();
			}
		}
	}
	function wizardAnnulla(){
		if(confirm("Annullare la creazione guidata?")){
			bloccaRichiesteServer();
			document.forms[0].metodo.value="annulla";
			document.forms[0].submit();
		}
	}
	function wizardExtra(){
		bloccaRichiesteServer();
		document.forms[0].metodo.value="extra";
		document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
		document.forms[0].submit();
	}
	
-->
</script>