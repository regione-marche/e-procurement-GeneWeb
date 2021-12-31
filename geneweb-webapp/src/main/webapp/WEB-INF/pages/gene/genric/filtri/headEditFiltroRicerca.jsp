<%/*
   * Created on 18-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI MODIFICA DI UN FILTRO DELLA RICERCA IN ANALISI
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
<c:when test='${fn:length(filtroRicercaForm.aliasTabella) gt 0}' > 
	<c:set var="mneTabella" value='${filtroRicercaForm.aliasTabella}' />
</c:when>
<c:otherwise> 
	<c:set var="mneTabella" value="" />
</c:otherwise>
</c:choose>

<c:choose>
<c:when test='${fn:length(filtroRicercaForm.mnemonicoCampo) gt 0}' > 
	<c:set var="mneCampo" value='${filtroRicercaForm.mnemonicoCampo}' />
</c:when>
<c:otherwise> 
	<c:set var="mneCampo" value="" />
</c:otherwise>
</c:choose>

<c:choose>
<c:when test='${fn:length(filtroRicercaForm.aliasTabellaConfronto) gt 0}' > 
	<c:set var="mneTabellaConfronto" value='${filtroRicercaForm.aliasTabellaConfronto}' />
</c:when>
<c:otherwise> 
	<c:set var="mneTabellaConfronto" value="" />
</c:otherwise>
</c:choose>

<c:choose>
<c:when test='${fn:length(filtroRicercaForm.mnemonicoCampoConfronto) gt 0}' > 
	<c:set var="mneCampoConfronto" value='${filtroRicercaForm.mnemonicoCampoConfronto}' />
</c:when>
<c:otherwise> 
	<c:set var="mneCampoConfronto" value="" />
</c:otherwise>
</c:choose>

<c:choose>
<c:when test='${fn:length(filtroRicercaForm.operatore) gt 0}' > 
	<c:set var="operatore" value='${filtroRicercaForm.operatore}' />
</c:when>
<c:otherwise> 
	<c:set var="operatore" value="" />
</c:otherwise>
</c:choose>

<c:choose>
<c:when test='${fn:length(filtroRicercaForm.tipoConfronto) gt 0}' > 
	<c:set var="tipoConfronto" value='${filtroRicercaForm.tipoConfronto}' />
</c:when>
<c:otherwise> 
	<c:set var="tipoConfronto" value="" />
</c:otherwise>
</c:choose>

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>
<%@ page import="java.util.Vector,
                it.eldasoft.utils.metadata.domain.Campo,
                it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm,
                it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm" %>

<script type="text/javascript">
<!-- 

	// Creazione menù dei campi
	
	<%
  /* 
   * Generazione degli array javascript contenenti elenco tabelle e 
   * elenco campi..
   */
  
	  Vector elencoTabelle = (Vector) request.getAttribute("elencoTabelle");

		String listaTabelle = "var listaTabelle = new Array (";
		for (int i=0; i < elencoTabelle.size(); i++) {
		  if (i>0)
		    listaTabelle += ",\n";
		  listaTabelle += "\"" + ((TabellaRicercaForm)elencoTabelle.elementAt(i)).getNomeTabellaUnivoco().replaceAll("[\"]","\\\\\"") + "\"";;
		  //listaTabelle += "\"" + ((TabellaRicercaForm)elencoTabelle.elementAt(i)).getAliasTabella().replaceAll("[\"]","\\\\\"") + "\"";;
		}
		listaTabelle += ");\n";
		
    Vector elencoCampi = null;
    TabellaRicercaForm tabella = null;
    Campo campo = null;
    String dichiarazioneListaSelect = null;
    String dichiarazioneValueSelect = null;
    String dichiarazioneCodiciSelect = null;
    String dichiarazioneTabellatiSelect = null;
    for (int i = 0; i < elencoTabelle.size(); i++) {
      tabella = (TabellaRicercaForm) elencoTabelle.elementAt(i);
      dichiarazioneListaSelect = "var " + tabella.getNomeTabellaUnivoco() + " = new Array ( '',";
      dichiarazioneValueSelect = "var valoriCampi" + i + " = new Array ( '',";
      dichiarazioneCodiciSelect = "var codiciCampi" + i + " = new Array ( '',";
      dichiarazioneTabellatiSelect = "var tabellatiCampi" + i + " = new Array ( '',";
      elencoCampi = (Vector) request.getAttribute("elencoCampi" + tabella.getNomeTabellaUnivoco());
      for (int j = 0; j < elencoCampi.size(); j++) {
        campo = (Campo) elencoCampi.elementAt(j);
        if (j>0) {
          dichiarazioneListaSelect += ",\n";
          dichiarazioneValueSelect += ",\n";
          dichiarazioneCodiciSelect += ",\n";
          dichiarazioneTabellatiSelect += ",\n";
        }
        dichiarazioneListaSelect += "\"" + campo.getCodiceMnemonico() + " - " + campo.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneValueSelect += "\"" + campo.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneCodiciSelect += "\"" + campo.getCodiceMnemonico() + "\"";
        dichiarazioneTabellatiSelect += "\"" + campo.getCodiceTabellato() + "\"";
      }
      dichiarazioneListaSelect += ");\n";
      dichiarazioneValueSelect += ");\n";
      dichiarazioneCodiciSelect += ");\n";
      dichiarazioneTabellatiSelect += ");\n";
	%>

	<%=dichiarazioneListaSelect%>
	<%=dichiarazioneValueSelect%>
	<%=dichiarazioneCodiciSelect%>
	<%=dichiarazioneTabellatiSelect%>
	<%
	    }
  %>
	<%=listaTabelle%>
	
	<% 
		//Generazione del Javascript per la popup di menu:

    for (int i = 0; i < elencoTabelle.size(); i++) {
      tabella = (TabellaRicercaForm) elencoTabelle.elementAt(i);
      elencoCampi = (Vector) request.getAttribute("elencoCampi" + tabella.getNomeTabellaUnivoco());
      for (int j = 0; j < elencoCampi.size(); j++) {
        String strLinkset = null;
        campo = (Campo) elencoCampi.elementAt(j);
        strLinkset = "linksetJsPopUp" + campo.getCodiceMnemonico() + " = \"\"\n";
        strLinkset += "linksetJsPopUp" + campo.getCodiceMnemonico() + "+=creaVocePopUpChiusura(\"" + request.getContextPath() + "/\");\n";
        strLinkset += "linksetJsPopUp" + campo.getCodiceMnemonico() + "+=creaPopUpSubmenu(\"javascript:helpMnemonico('" + campo.getCodiceMnemonico() + "');hideMenuPopup();\",0,\"&nbsp Informazioni campo\");\n";
        
        String strLinksetTabellato = "";
        String strLinksetListaValori = "";

        strLinksetTabellato = "linksetJsPopUpTabellatoCampo" + campo.getCodiceMnemonico() + " = \"\"\n";
        strLinksetTabellato += "linksetJsPopUpTabellatoCampo" + campo.getCodiceMnemonico() + " += creaVocePopUpChiusura(\"" + request.getContextPath() + "/\");\n";
      	strLinksetTabellato += "linksetJsPopUpTabellatoCampo" + campo.getCodiceMnemonico() + " += creaPopUpSubmenu(\"javascript:helpListaValori();hideMenuPopup();\",0,\"&nbsp Guida \");\n";
      	if (campo.getCodiceTabellato()!= null) {          
      	  strLinksetTabellato += "linksetJsPopUpTabellatoCampo" + campo.getCodiceMnemonico() + " += creaPopUpSubmenu(\"javascript:helpListaValoriTabellati('" + campo.getCodiceTabellato() + "');hideMenuPopup();\",0,\"&nbsp Valori campo\");\n";
      	  strLinksetListaValori = "linksetJsPopUpListaValoriTabellato" + campo.getCodiceMnemonico() + " = \"\"\n";
      	  strLinksetListaValori += "linksetJsPopUpListaValoriTabellato" + campo.getCodiceMnemonico() + " += creaVocePopUpChiusura(\"" + request.getContextPath() + "/\");\n";
      	  strLinksetListaValori += "linksetJsPopUpListaValoriTabellato" + campo.getCodiceMnemonico() + " += creaPopUpSubmenu(\"javascript:helpListaValoriTabellati('" + campo.getCodiceTabellato() + "');hideMenuPopup();\",0,\"&nbsp Valori campo\");\n";
      	}
      	
%>
        <%=strLinkset %>
        <%=strLinksetTabellato %>
        <%=strLinksetListaValori %>
<%    }
    }
%>


	// Azioni invocate dal menu contestuale

	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab) {
		document.location.href = 'CambiaTabRicerca.do?'+csrfToken+'&tab=' + codiceTab;
	}

	var setDisplayNotCaseSensitive = 'none';
	function initPageFiltro() {
<c:if test='${applicationScope.attivaCaseSensitive}'>
			setDisplayNotCaseSensitive = '';
</c:if>
			document.getElementById("spNotCaseSensitive").style.display = setDisplayNotCaseSensitive;
  	}

	function helpCampo(tipo) {
		var contenuto = "";
		var nomeObj = "";
		if (tipo == "campo") {
			contenuto = document.filtroRicercaForm.mnemonicoCampo.value;
			nomeObj = 'jsPopUpCAMPO';
		} else if (tipo == "campoConfr") {
			contenuto = document.filtroRicercaForm.mnemonicoCampoConfronto.value;
			nomeObj = 'jsPopUpCAMPO_CONFR';
		}
		if (contenuto != "")
			showMenuPopup(nomeObj, eval("linksetJsPopUp" + contenuto));
	}
	
	function helpTabellatoCampo(tipo) {
		var contenuto = "";
		var nomeObj = "";
		contenuto = document.filtroRicercaForm.mnemonicoCampo.value;
		nomeObj = 'jsPopUpHELPLISTAVALORIFILTRO';
		
		if (contenuto != "") {
			showMenuPopup(nomeObj, eval("linksetJsPopUpTabellatoCampo" + contenuto));
		} 
	}
	
	function helpListaValoriCampo() {
		var contenuto = "";
		var nomeObj = "";
		contenuto = document.filtroRicercaForm.mnemonicoCampo.value;
		nomeObj = 'jsPopUpHELPLISTAVALORITABELLATO';
		if (contenuto != "") {
			showMenuPopup(nomeObj, eval("linksetJsPopUpListaValoriTabellato" + contenuto));
		}
		
	}
	
	function helpListaValori() {
		var operatore = new String(document.filtroRicercaForm.operatore.value);
		var action = "${contextPath}/geneGenric/ApriHelpListaValori.do";
		var href = String("operatore=" + operatore);
		openPopUpActionCustom(action, href, "listaValori", 580, 350, "no", "yes");
	}
	
	function helpListaValoriTabellati(tabellato) {
		var action = "${contextPath}/GetTabellato.do";
		var href = String("tabellato="+tabellato+"&funzione=aggiungi&apici=0");
		//if (document.filtroRicercaForm.operatore.value == "IN" || document.filtroRicercaForm.operatore.value == "NOT IN") 
		//	href += "1";
		//else
		//	href += "0";
		openPopUpActionCustom(action, href, "listaValoriTabellati", 580, 350, "no", "yes");
	}
	
	// gestisce la valorizzazione del campo valore con i valori che vengono dalla popup
	
	function aggiungi(valore) {
		if (document.filtroRicercaForm.operatore.value == "IN" || document.filtroRicercaForm.operatore.value == "NOT IN") {
			var valoreIniziale = document.filtroRicercaForm.valoreConfronto.value;
			if (valoreIniziale.length > 0)
				document.filtroRicercaForm.valoreConfronto.value = valoreIniziale + ", " + valore;
			else
				document.filtroRicercaForm.valoreConfronto.value = valore;
		} else 
			document.filtroRicercaForm.valoreConfronto.value = valore;
	}
	
	// Azioni di pagina

	function gestisciSubmit() {
		var esito = false;
		//alert(document.filtroRicercaForm.operatore.value);
    
		if (document.filtroRicercaForm.operatore.value != "") {
    
			if (strGrpOp1.indexOf(document.filtroRicercaForm.operatore.value) >= 0) {
		 	  esito = true; //nessun controllo necessario
	    } else if (strGrpOp2.indexOf(document.filtroRicercaForm.operatore.value) >= 0 
	    						&& document.filtroRicercaForm.aliasTabella.value != "" 
	    						&& document.filtroRicercaForm.mnemonicoCampo.value != "") {
	     		esito = true;
	    } else if (strGrpOp3.indexOf(document.filtroRicercaForm.operatore.value) >= 0) {

				if ( document.filtroRicercaForm.aliasTabella.value != "" &&
			       document.filtroRicercaForm.mnemonicoCampo.value != "" &&
			       ( ( document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>
							   && document.filtroRicercaForm.aliasTabellaConfronto.value != ""
			   			   && document.filtroRicercaForm.mnemonicoCampoConfronto.value != ""
			         ) ||  (
			           document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_VALORE%>
			 					 && document.filtroRicercaForm.valoreConfronto.value != ""
			         ) || (
			           document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO%>
			         ) || (
			       		 document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_DATA_ODIERNA%>
			         ) || (
				         document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_UTENTE_CONNESSO%>
				       ) || (
				         document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_UFFICIO_INTESTATARIO%>
			         )
			       )
				) {
					esito = true;
				}
			}
  	}
    if (esito) {
    	if (document.filtroRicercaForm.operatore.value == 'IN' || document.filtroRicercaForm.operatore.value == 'NOT IN') {
    		if (trim(document.filtroRicercaForm.valoreConfronto) == "''") {
    			alert('La lista deve contenere almeno un valore');
    			esito = false;
    		} else {
	    		document.getElementById("selectTipoConfr").disabled = false;
	    	}
    	}
    	if (esito) {
    		bloccaRichiesteServer();
			  document.filtroRicercaForm.submit();
			}
		} else {
			alert("E' necessario popolare tutti i campi presenti nella pagina.");
		}
	}
	
	function annulla() {
		bloccaRichiesteServer();
		cambiaTab('FIL');
	}
	
	function aggiornaOpzioniSelectCampo(indice, selectDaAggiornare) {
		//alert('opzioni');
		var nomeArrayValue = 'valoriCampi' + (indice-1);
		var nomeArrayText = document.filtroRicercaForm.aliasTabella.options[indice].value;
		if (indice == 0) {
			selectDaAggiornare.length = 1;
			selectDaAggiornare.options[0].text = "";
			selectDaAggiornare.options[0].value = "";
		} else {
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, selectDaAggiornare);
			selectDaAggiornare.selectedIndex = 0;
			selectDaAggiornare.value = "";
		}
	}
	
	
	
	var notDefined = -1;
	var grpOp1 = 1;
 	var grpOp2 = 2;
 	var grpOp3 = 3;
 	var strGrpOp1 = "()ANDORNOT";
 	var strGrpOp2 = "IS NULLIS NOT NULL";
 	var strGrpOp3 = ">=<=<>NOT LIKEINNOT IN";
	var ultimoOperatore = notDefined;
	var ultimoConfronto = notDefined;
	
	// scatta alla selezione del campo dalla dropdown
	function aggiornaCampiInput(indiceSchemaSelezionato, indiceSelezionato, mnemonicoCampoDaAggiornare) {
		
		var arrayValori = eval("codiciCampi"+(indiceSchemaSelezionato-1)); //tolgo il primo elemento vuoto
		var mnemonicoCampo = eval(mnemonicoCampoDaAggiornare);
		mnemonicoCampo.value = arrayValori[indiceSelezionato];
		var arrayTabellati = eval("tabellatiCampi"+(indiceSchemaSelezionato-1)); //tolgo il primo elemento vuoto
		document.getElementById("tabellato").value = arrayTabellati[indiceSelezionato];
		//alert('valore tabellato:' + document.getElementById("tabellato").value);
		
		if (arrayTabellati[indiceSelezionato]!= "null" ) {
			if ("INNOT IN".indexOf(document.filtroRicercaForm.operatore.value) >=0) {
				document.getElementById("popupHelpListaValoriEGuida").style.display = '';
				document.getElementById("popupHelpListaValori").style.display = 'none';
				}
			else {
				document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
				document.getElementById("popupHelpListaValori").style.display = '';
				
				}
		} else {
			if ("INNOT IN".indexOf(document.filtroRicercaForm.operatore.value) >=0) {
				document.getElementById("popupHelpListaValoriEGuida").style.display = '';
				document.getElementById("popupHelpListaValori").style.display = 'none';
			} else {
				document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
				document.getElementById("popupHelpListaValori").style.display = 'none';
			}
		}
	}
	
  function impostaAbilitazioniCampi() {
		with (document.filtroRicercaForm) {
  	
  		if (operatore.value == "" || strGrpOp1.indexOf(operatore.value) >=0) {
		  	//Gruppo Operatori n. 1 () AND OR NOT
				//alert('operatore gruppo 1');
				document.getElementById("tabella").style.display = 'none';
	    	document.getElementById("campo").style.display = 'none';
	    	document.getElementById("tipoConfr").style.display = 'none';
	    	document.getElementById("tabellaConfr").style.display = 'none';
	    	document.getElementById("campoConfr").style.display = 'none';
	    	document.getElementById("valore").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
	    	document.getElementById("spNotCaseSensitive").style.display = 'none';
						
		    aliasTabella.selectedIndex = 0;
		    aliasTabella.value = "";
		    descrizioneCampo.selectedIndex = 0;
		    mnemonicoCampo.value = "";
		    tipoConfronto.selectedindex = 0;
		    tipoConfronto.value = "";
		    aliasTabellaConfronto.selectedIndex = 0;
		    aliasTabellaConfronto.value = "";
		    descrizioneCampoConfronto.selectedIdex = 0;
		    mnemonicoCampoConfronto.value = "";
		    valoreConfronto.value = "";
		    notCaseSensitive.checked = false;
		    
		    //parametroConfronto.value = "";      
      
		    ultimoOperatore = grpOp1;
			ultimoConfronto = notDefined;	
      
		} else if (strGrpOp2.indexOf(operatore.value) >=0) {
	  	//Gruppo Operatori n. 2 IS NULL IS NOT NULL
			//alert('operatore gruppo 2');		
			if (ultimoOperatore == grpOp3) {
				//alert('ultimo operatore gruppo 3');
				document.getElementById("tipoConfr").style.display = 'none';
				document.getElementById("tabellaConfr").style.display = 'none';
    		document.getElementById("campoConfr").style.display = 'none';
    		document.getElementById("valore").style.display = 'none';
    		document.getElementById("parametro").style.display = 'none';
    		document.getElementById("spNotCaseSensitive").style.display = 'none';
			
        aliasTabellaConfronto.selectedIndex = 0;
        aliasTabellaConfronto.value = "";
        descrizioneCampoConfronto.selectedIdex = 0;
        mnemonicoCampoConfronto.value = "";
        valoreConfronto.value = "";
        notCaseSensitive.checked = false;
	      //parametroConfronto.selectedIdex = 0;
	      //parametroConfronto.value = "";			

				//Riattivo le opzioni precedentemente disabilitate
				document.getElementById("selectTipoConfr").disabled = false;
				//valoreConfronto.value = "";
				tipoConfronto.selectedIndex = 0;
				document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';

			}	else if (ultimoOperatore == notDefined || ultimoOperatore == grpOp1) {
				//alert('ultimo operatore gruppo 1 o non definito');
				document.getElementById("tabella").style.display = '';
	    	document.getElementById("campo").style.display = '';
	    	document.getElementById("tipoConfr").style.display = 'none';
	    	document.getElementById("tabellaConfr").style.display = 'none';
	    	document.getElementById("campoConfr").style.display = 'none';
	    	document.getElementById("tipoConfr").style.display = 'none';
	    	document.getElementById("valore").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
	    	
				aliasTabella.selectedIndex = 0;
		    aliasTabella.value = "";
		    descrizioneCampo.selectedIndex = 0;
		    mnemonicoCampo.value = "";
		    tipoConfronto.selectedindex = 0;
		    tipoConfronto.value = "";
		    aliasTabellaConfronto.selectedIndex = 0;
		    aliasTabellaConfronto.value = "";
		    descrizioneCampoConfronto.selectedIdex = 0;
		    mnemonicoCampoConfronto.value = "";
		    valoreConfronto.value = "";
		    //parametroConfronto.selectedIdex = 0;
		    //parametroConfronto.value = "";  
			}
			ultimoOperatore = grpOp2;
			ultimoConfronto = notDefined;

    } else { 
			//Gruppo Operatori n. 3  >= <= < > NOT LIKE IN NOT IN
			//alert('operatore gruppo 3');
    	if (document.getElementById("tabellato").value != "null") {
 				if (operatore.value == "IN" || operatore.value == "NOT IN") {
					document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
					document.getElementById("popupHelpListaValori").style.display = '';
				} else {
					document.getElementById("popupHelpListaValoriEGuida").style.display = '';
					document.getElementById("popupHelpListaValori").style.display = 'none';
				}
			} else {
				if (operatore.value == "IN" || operatore.value == "NOT IN") {
					document.getElementById("popupHelpListaValoriEGuida").style.display = '';
					document.getElementById("popupHelpListaValori").style.display = 'none';
				} else {
					document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
					document.getElementById("popupHelpListaValori").style.display = 'none';
				}	
			}  
				
			if (ultimoOperatore == grpOp2) {
				//alert('ultimo operatore gruppo 2');
				document.getElementById("tipoConfr").style.display = '';
	    	document.getElementById("tabellaConfr").style.display = 'none';
	    	document.getElementById("campoConfr").style.display = 'none';
	    	document.getElementById("valore").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
	    	//document.getElementById("spNotCaseSensitive").style.display = '';
   			document.getElementById("spNotCaseSensitive").style.display = setDisplayNotCaseSensitive;
				
				tipoConfronto.selectedindex = 0;
				tipoConfronto.value = "";
				aliasTabellaConfronto.selectedIndex = 0;
				aliasTabellaConfronto.value = "";
				descrizioneCampoConfronto.selectedIdex = 0;
				mnemonicoCampoConfronto.value = "";
				valoreConfronto.value = "";
				notCaseSensitive.checked = false;
				//parametroConfronto.selectedIdex = 0;
      	//parametroConfronto.value = "";
	      
			} else if (ultimoOperatore == notDefined || ultimoOperatore == grpOp1) {
				//alert('operatore gruppo 1 o non definito');
				document.getElementById("tabella").style.display = '';
	    	document.getElementById("campo").style.display = '';
	    	document.getElementById("tipoConfr").style.display = '';
	    	document.getElementById("tabellaConfr").style.display = 'none';
	    	document.getElementById("campoConfr").style.display = 'none';
	    	document.getElementById("valore").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
	    	//document.getElementById("spNotCaseSensitive").style.display = '';
   			document.getElementById("spNotCaseSensitive").style.display = setDisplayNotCaseSensitive;
	    	
				aliasTabella.selectedIndex = 0;
				aliasTabella.value = "";
				descrizioneCampo.selectedIndex = 0;
				mnemonicoCampo.value = "";
				tipoConfronto.selectedindex = 0;
				tipoConfronto.value = "";
				aliasTabellaConfronto.selectedIndex = 0;
				aliasTabellaConfronto.value = "";
				descrizioneCampoConfronto.selectedIdex = 0;
      	mnemonicoCampoConfronto.value = "";
      	valoreConfronto.value = "";
		    notCaseSensitive.checked = false;
				//parametroConfronto.selectedIdex = 0;
   			//parametroConfronto.value = "";    
	    		
   		} else if (operatore.value == "IN" || operatore.value == "NOT IN") {
				//alert('operatore in not in');
			<% 
			 /* Gestione dell'operatore IN: 
			  * - si disattivano due opzioni dalla select "tipoConfronto";
		 	  * - si deseleziona un eventuale opzione dalla select "tipoConfronto";
			  * - si visualizza l'oggetto ;
			  * - si nascondo gli oggetti "valore", "tabellaConfr", "campoConfr",
			  *   "parametro" e si resettano i loro valori;
	 		 */ %>
      				
      	tipoConfronto.selectedIndex = 2;
				document.getElementById("selectTipoConfr").disabled = true;
				
	    	document.getElementById("valore").style.display = '';
	    	document.getElementById("tabellaConfr").style.display = 'none';
	    	document.getElementById("campoConfr").style.display = 'none';
 	  		document.getElementById("parametro").style.display = 'none';
  	  	//document.getElementById("spNotCaseSensitive").style.display = '';
			      	
	    	//valoreConfronto.value = "";
	    	aliasTabellaConfronto.selectedIndex = 0;
				aliasTabellaConfronto.value = "";
    		descrizioneCampoConfronto.selectedIndex = 0;
    		mnemonicoCampoConfronto.value = "";
	    	//notCaseSensitive.checked = false;
		    		
   			document.getElementById("popupHelpListaValoriEGuida").style.display = '';
				document.getElementById("popupHelpListaValori").style.display = 'none';

   		} else {
				//alert('tutti gli altri');
				document.getElementById("selectTipoConfr").disabled = false;
				//valoreConfronto.value = "";
				//tipoConfronto.selectedIndex = 0;
				if (document.getElementById("tabellato").value != "null") {
					document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
					document.getElementById("popupHelpListaValori").style.display = '';
				} else {
					document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
					document.getElementById("popupHelpListaValori").style.display = 'none';	
				}
				//document.getElementById("spNotCaseSensitive").style.display = '';
				//notCaseSensitive.checked = false;
			}
	    
    	ultimoOperatore = grpOp3;
    	}  
    }
  }

  function impostaAbilitazioniCampiConfronto() {
  	with (document.filtroRicercaForm) {
	    
	    if (tipoConfronto.value == "<%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>") {
				
				if (ultimoConfronto != <%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>) {
					document.getElementById("tabellaConfr").style.display = '';
		  	  document.getElementById("campoConfr").style.display = '';
		  	}
		    	
		    document.getElementById("valore").style.display = 'none';
		    document.getElementById("parametro").style.display = 'none';
		    	
		    aliasTabellaConfronto.selectedIndex = 0;
		    descrizioneCampoConfronto.selectedIndex = 0;
		    mnemonicoCampoConfronto.value = "";
		    valoreConfronto.value = "";
				//parametroConfronto.selectedIndex = 0;
				//parametroConfronto.value = "";
					
				ultimoConfronto = <%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>;
				
	    } else if (tipoConfronto.value == "<%=FiltroRicercaForm.TIPO_CONFRONTO_VALORE%>") {
	
	     	document.getElementById("valore").style.display = '';
				valoreConfronto.value = '';
				document.getElementById("tabellaConfr").style.display = 'none';
				document.getElementById("campoConfr").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
	    	
		    aliasTabellaConfronto.selectedIndex = 0;			
	    	aliasTabellaConfronto.value = "";
		    descrizioneCampoConfronto.selectedIndex = 0;			
	    	mnemonicoCampoConfronto.value = "";
		    //parametroConfronto.selectedIndex = 0;			
	    	//parametroConfronto.value = "";
	
				ultimoConfronto = <%=FiltroRicercaForm.TIPO_CONFRONTO_VALORE%>;
			
	    } else if (tipoConfronto.value == "<%=FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO%>") {
	    
	    	document.getElementById("parametro").style.display = '';
	    	document.getElementById("tabellaConfr").style.display = 'none';
				document.getElementById("campoConfr").style.display = 'none';
	     	document.getElementById("valore").style.display = 'none';
	     	
	      aliasTabellaConfronto.selectedIndex = 0;			
	      aliasTabellaConfronto.value = "";
	      descrizioneCampoConfronto.selectedIndex = 0;			
	      mnemonicoCampoConfronto.value = "";
	      valoreConfronto.value = "";
	      
	 			ultimoConfronto = <%=FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO%>;
	    } else if (tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_DATA_ODIERNA%>) {
								 
				document.getElementById("valore").style.display = 'none';
				valoreConfronto.value = '';
				document.getElementById("tabellaConfr").style.display = 'none';
				document.getElementById("campoConfr").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
	    	
 	      aliasTabellaConfronto.selectedIndex = 0;			
	      aliasTabellaConfronto.value = "";
	      descrizioneCampoConfronto.selectedIndex = 0;			
	      mnemonicoCampoConfronto.value = "";
	      //parametroConfronto.value = "";
				ultimoConfronto = <%=FiltroRicercaForm.TIPO_CONFRONTO_DATA_ODIERNA%>;

	    } else if (tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_UTENTE_CONNESSO%>) {
			 
				document.getElementById("valore").style.display = 'none';
				valoreConfronto.value = '';
				document.getElementById("tabellaConfr").style.display = 'none';
				document.getElementById("campoConfr").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
	    	
		    aliasTabellaConfronto.selectedIndex = 0;			
	      aliasTabellaConfronto.value = "";
	      descrizioneCampoConfronto.selectedIndex = 0;			
	      mnemonicoCampoConfronto.value = "";
	      //parametroConfronto.value = "";
				ultimoConfronto = <%=FiltroRicercaForm.TIPO_CONFRONTO_UTENTE_CONNESSO%>;

	    } else if (tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_UFFICIO_INTESTATARIO%>) {
			 
				document.getElementById("valore").style.display = 'none';
				valoreConfronto.value = '';
				document.getElementById("tabellaConfr").style.display = 'none';
				document.getElementById("campoConfr").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
	    	
		    aliasTabellaConfronto.selectedIndex = 0;			
	      aliasTabellaConfronto.value = "";
	      descrizioneCampoConfronto.selectedIndex = 0;			
	      mnemonicoCampoConfronto.value = "";
	      //parametroConfronto.value = "";
				ultimoConfronto = <%=FiltroRicercaForm.TIPO_CONFRONTO_UFFICIO_INTESTATARIO%>;
				
	    } else {
	
				document.getElementById("tabellaConfr").style.display = 'none';
				document.getElementById("campoConfr").style.display = 'none';
		   	document.getElementById("valore").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
				aliasTabellaConfronto.selectedIndex = 0;
				aliasTabellaConfronto.value = "";
				descrizioneCampoConfronto.selectedIndex = 0;
				mnemonicoCampoConfronto.value = "";
				valoreConfronto.value = '';
				//parametroConfronto.selectedIndex = 0;			
	      //parametroConfronto.value = "";
	      ultimoConfronto = notDefined;
	    }
    }
  }

	function selezionaCombo() {
		
			
		var aliasTabella = "<c:out value='${mneTabella}'/>";
		var mneCampo = "<c:out value='${mneCampo}'/>";
			
		var aliasTabellaConfr = "<c:out value='${mneTabellaConfronto}'/>";
		var mneCampoConfr = "<c:out value='${mneCampoConfronto}'/>";
		var operatore = "<c:out value='${operatore}' escapeXml='false'/>"; 

		if (strGrpOp1.indexOf(operatore) >= 0 ) {
		
			ultimoOperatore = grpOp1;
			impostaAbilitazioniCampi();
		
		} else if (strGrpOp2.indexOf(operatore) >=0 || strGrpOp3.indexOf(operatore) >= 0) {

			//alert('ccc');
			var posTab = 0;
			var len = listaTabelle.length;
			
			for (var i=0;  i < len; i++) {
				if (listaTabelle[i] == aliasTabella) {
					posTab = i;
				}
			}
			
			var nomeArrayValue = 'valoriCampi' + (posTab);
			var nomeArrayText = document.filtroRicercaForm.aliasTabella.options[posTab+1].value;
			
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, document.filtroRicercaForm.descrizioneCampo);
			
			var posCampo = 0;			
			var arrayValori = eval("codiciCampi"+(posTab));
			
			for (var i=0; i < arrayValori.length; i++) {
			  if (arrayValori[i] == mneCampo) {
			  	posCampo = i;
				}
			}
			
			document.filtroRicercaForm.descrizioneCampo.options[posCampo].selected = true; 
			document.filtroRicercaForm.mnemonicoCampo.value = mneCampo;	
			
			var arrayTabellati = eval("tabellatiCampi"+(posTab)); 
			document.getElementById("tabellato").value = arrayTabellati[posCampo];
			
			if (strGrpOp2.indexOf(operatore) >= 0) {
				
				ultimoOperatore = grpOp2;
				
				document.getElementById("tipoConfr").style.display = 'none';
				document.getElementById("tabellaConfr").style.display = 'none';
    		document.getElementById("campoConfr").style.display = 'none';
    		document.getElementById("valore").style.display = 'none';
    		document.getElementById("parametro").style.display = 'none';
		
		    document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
		    document.filtroRicercaForm.aliasTabellaConfronto.value = "";
		    document.filtroRicercaForm.descrizioneCampoConfronto.selectedIdex = 0;
		    document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
		    document.filtroRicercaForm.valoreConfronto.value = "";
      	//parametroConfronto.selectedIdex = 0;
      	//parametroConfronto.value = "";			
		      	
			}			
			
			if (strGrpOp3.indexOf(operatore) >= 0) {

				var tipoConfr = "<c:out value='${tipoConfronto}'/>";
				
				ultimoOperatore = grpOp3;
				
				if (tipoConfr == <%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>) {

					var posTab1 = 0;
					var len = listaTabelle.length;
					for (var i=0;  i < len; i++) {
						if (listaTabelle[i] == aliasTabellaConfr) {
							posTab1 = i;
						}
					}
					
					var nomeArrayValue = 'valoriCampi' + (posTab1);
					var nomeArrayText = document.filtroRicercaForm.aliasTabellaConfronto.options[posTab1+1].value;
					//alert('aaaa');
					aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, document.filtroRicercaForm.descrizioneCampoConfronto);
					var posCampo1 = 0;			
					var arrayValori = eval("codiciCampi"+(posTab1));
					for (var i=0; i < arrayValori.length; i++) {
					  if (arrayValori[i] == mneCampoConfr) {
					  	posCampo1 = i;
						}
					}
					
					document.filtroRicercaForm.descrizioneCampoConfronto.options[posCampo1].selected = true; 
					document.filtroRicercaForm.mnemonicoCampoConfronto.value = mneCampoConfr;			
					
					document.getElementById("tabellaConfr").style.display = '';
			    document.getElementById("campoConfr").style.display = '';
						    	
			    document.getElementById("valore").style.display = 'none';
			    document.filtroRicercaForm.valoreConfronto.value = "";
	    		document.getElementById("parametro").style.display = 'none';			
		      //parametroConfronto.selectedIdex = 0;
		      //parametroConfronto.value = "";
	
				} else if (tipoConfr == <%=FiltroRicercaForm.TIPO_CONFRONTO_VALORE%>) {
					
					document.getElementById("valore").style.display = '';
					document.getElementById("tabellaConfr").style.display = 'none';
					document.getElementById("campoConfr").style.display = 'none';		
	        document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
	        document.filtroRicercaForm.aliasTabellaConfronto.value = "";
	        document.filtroRicercaForm.descrizioneCampoConfronto.selectedIdex = 0;
	        document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
			    document.getElementById("parametro").style.display = 'none';
		    	if (operatore == "IN") {
	    			document.getElementById("selectTipoConfr").disabled = true;
	    			if (document.getElementById("tabellato").value != "null") {
							document.getElementById("popupHelpListaValoriEGuida").style.display = '';
							document.getElementById("popupHelpListaValori").style.display = 'none';
						} else {
							document.getElementById("popupHelpListaValoriEGuida").style.display = '';
							document.getElementById("popupHelpListaValori").style.display = 'none';	
						}
		    	} else {
		    		if (document.getElementById("tabellato").value != "null") {
							document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
							document.getElementById("popupHelpListaValori").style.display = '';
						} else {
							document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
							document.getElementById("popupHelpListaValori").style.display = 'none';	
						}
			    }

		    	//parametroConfronto.selectedIdex = 0;
			    //parametroConfronto.value = "";	
		      
				} else if (tipoConfr == <%=FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO%>) {
					
			    document.getElementById("parametro").style.display = '';		
					//parametroConfronto.value = ;

					document.getElementById("tabellaConfr").style.display = 'none';
					document.getElementById("campoConfr").style.display = 'none';		
			    document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
				  document.filtroRicercaForm.aliasTabellaConfronto.value = "";
	      	document.filtroRicercaForm.descrizioneCampoConfronto.selectedIdex = 0;
	      	document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
		    	document.getElementById("valore").style.display = 'none';
      		document.filtroRicercaForm.valoreConfronto.value = "";
					
				} else if (tipoConfr == <%=FiltroRicercaForm.TIPO_CONFRONTO_DATA_ODIERNA%>) {

					document.getElementById("parametro").style.display = 'none';		
					//parametroConfronto.value = "";
					document.getElementById("tabellaConfr").style.display = 'none';
					document.getElementById("campoConfr").style.display = 'none';		
			    document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
			    document.filtroRicercaForm.aliasTabellaConfronto.value = "";
			    document.filtroRicercaForm.descrizioneCampoConfronto.selectedIdex = 0;
			    document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
		    	document.getElementById("valore").style.display = 'none';
		    	document.filtroRicercaForm.valoreConfronto.value = "";
				} else if (tipoConfr == <%=FiltroRicercaForm.TIPO_CONFRONTO_UTENTE_CONNESSO%>) {
					
				    document.getElementById("parametro").style.display = 'none';		
						//parametroConfronto.value = ;
						document.getElementById("tabellaConfr").style.display = 'none';
						document.getElementById("campoConfr").style.display = 'none';		
				    document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
					  document.filtroRicercaForm.aliasTabellaConfronto.value = "";
		      	document.filtroRicercaForm.descrizioneCampoConfronto.selectedIdex = 0;
		      	document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
			    	document.getElementById("valore").style.display = 'none';
	      		document.filtroRicercaForm.valoreConfronto.value = "";
						
					} else if (tipoConfr == <%=FiltroRicercaForm.TIPO_CONFRONTO_UFFICIO_INTESTATARIO%>) {

						document.getElementById("parametro").style.display = 'none';
						//parametroConfronto.value = "";
						document.getElementById("tabellaConfr").style.display = 'none';
						document.getElementById("campoConfr").style.display = 'none';		
				    document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
				    document.filtroRicercaForm.aliasTabellaConfronto.value = "";
				    document.filtroRicercaForm.descrizioneCampoConfronto.selectedIdex = 0;
				    document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
			    	document.getElementById("valore").style.display = 'none';
			    	document.filtroRicercaForm.valoreConfronto.value = "";

				}
			}
		}
	}
	
	
	

-->
</script>