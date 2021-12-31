<%/*
       * Created on 14-set-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
      // DELLA PAGINA DI CREAZIONE DI UN NUOVO FILTRO DA AGGIUNGERE AD UNA RICERCA
      %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript"
	src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>
<%@ page
	import="java.util.Vector,it.eldasoft.utils.metadata.domain.Campo,it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm,it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm"%>

<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale
	
	// Azioni invocate dal tab menu
	
	function cambiaTab(codiceTab){
		document.location.href = 'CambiaTabRicerca.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	function gestisciSubmit(){
		var esito = false;
		
    if (document.filtroRicercaForm.operatore.value != "") {
			if (strGrpOp1.indexOf(document.filtroRicercaForm.operatore.value) >= 0) {
		 		esito = true; //nessun controllo necessario
	    } else if (strGrpOp2.indexOf(document.filtroRicercaForm.operatore.value) >= 0 
	    					 && document.filtroRicercaForm.aliasTabella.value != "" 
	    					 && document.filtroRicercaForm.mnemonicoCampo.value != "") {
	    	esito = true;
	    } else if(strGrpOp3.indexOf(document.filtroRicercaForm.operatore.value) >= 0) {

				if (document.filtroRicercaForm.aliasTabella.value != "" && 
			   	  document.filtroRicercaForm.mnemonicoCampo.value != "" &&
		      	( (  document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>
							   && document.filtroRicercaForm.aliasTabellaConfronto.value != ""
		   			  	 && document.filtroRicercaForm.mnemonicoCampoConfronto.value != ""
		        	) ||  (
		          	 document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_VALORE%>
		 				   	 && trim(document.filtroRicercaForm.valoreConfronto) != ""
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

	function initPageFiltro() {
  	document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
		document.getElementById("popupHelpListaValori").style.display = 'none';
  	impostaAbilitazioniCampi();
 	}

	function helpListaValori() {
		var operatore = new String(document.filtroRicercaForm.operatore.value);
		var action = "${contextPath}/geneGenric/ApriHelpListaValori.do";
		var href = String(csrfToken+"&operatore=" + operatore);
		openPopUpActionCustom(action, href, "listaValori", 580, 350, "no", "yes");
	}
	
	function helpListaValoriTabellati(tabellato) {
		var action = "${contextPath}/GetTabellato.do";
		var href = String(csrfToken+"&tabellato="+tabellato+"&funzione=aggiungi&apici=0");
		//if(document.filtroRicercaForm.operatore.value == "IN" || document.filtroRicercaForm.operatore.value == "NOT IN") 
		//	href += "1";
		//else
		//	href += "0";
		openPopUpActionCustom(action, href, "listaValoriTabellati", 580, 350, "no", "yes");
	}
	
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
	
	function helpCampo(tipo) {
		var contenuto = "";
		var nomeObj = "";
		if (tipo == "campo") {
			contenuto = document.filtroRicercaForm.mnemonicoCampo.value;
			nomeObj = 'jsPopUpCAMPO';
		} else if(tipo == "campoConfr") {
			contenuto = document.filtroRicercaForm.mnemonicoCampoConfronto.value;
			nomeObj = 'jsPopUpCAMPO_CONFR';
		}
		if(contenuto != "")
			showMenuPopup(nomeObj, eval("linksetJsPopUp" + contenuto));
	}
	
	function helpTabellatoCampo(tipo) {
		var contenuto = "";
		var nomeObj = "";
		contenuto = document.filtroRicercaForm.mnemonicoCampo.value;
		nomeObj = 'jsPopUpHELPLISTAVALORIFILTRO';
		
		if(contenuto != "")
			showMenuPopup(nomeObj, eval("linksetJsPopUpTabellatoCampo" + contenuto));
	}
	
	function helpListaValoriTabellato() {
		var contenuto = "";
		var nomeObj = "";
		contenuto = document.filtroRicercaForm.mnemonicoCampo.value;
		nomeObj = 'jsPopUpHELPLISTAVALORITABELLATO';
		if(contenuto != "") {
			showMenuPopup(nomeObj, eval("linksetJsPopUpListaValoriTabellato" + contenuto));
		}
	}
	
	
<%
  /* 
   * Generazione degli array javascript contenenti elenco tabelle e 
   * elenco campi..
   */
  
	  Vector elencoTabelle = (Vector) request.getAttribute("elencoTabelle");

		String listaTabelle = "var listaTabelle = new Array (";
		for(int i=0; i < elencoTabelle.size(); i++){
		  if(i>0)
		    listaTabelle += ",\n";
		  listaTabelle += "\"" + ((TabellaRicercaForm)elencoTabelle.elementAt(i)).getNomeTabellaUnivoco().replaceAll("[\"]","\\\\\"") + "\"";;
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
	      	  
	        if (campo.getCodiceTabellato()!= null){

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

	/*linksetjsPopUpHelpListaValoriFiltro = "";
	linksetjsPopUpHelpListaValoriFiltro +=  creaVocePopUpChiusura("${contextPath}/");
	linksetjsPopUpHelpListaValoriFiltro += creaPopUpSubmenu("javascript:helpListaValori();hideMenuPopup();",0,"&nbsp Guida ");*/

	

		
	function aggiornaOpzioniSelectCampo(indice, selectDaAggiornare) {
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
	
	var tabellatoCampo = "";
	
	function aggiornaCampiInput(indiceSchemaSelezionato, indiceSelezionato, mnemonicoCampoDaAggiornare) {
		var arrayValori = eval("codiciCampi"+(indiceSchemaSelezionato-1)); //tolgo il primo elemento vuoto
		var mnemonicoCampo = eval(mnemonicoCampoDaAggiornare);
		mnemonicoCampo.value = arrayValori[indiceSelezionato];
		
		var arrayValoriTabellati = eval("tabellatiCampi"+(indiceSchemaSelezionato-1)); //tolgo il primo elemento vuoto
		
		tabellatoCampo = arrayValoriTabellati[indiceSelezionato];
		document.getElementById("tabellato").value = tabellatoCampo;
		//if (tabellatoCampo != "null")
		//	document.getElementById("popupHelpListaValori").style.display = '';
		//else
		//	document.getElementById("popupHelpListaValori").style.display = 'none';
	
		if (tabellatoCampo!= "null" ) {
			if ("INNOT IN".indexOf(document.filtroRicercaForm.operatore.value) >=0){
				document.getElementById("popupHelpListaValoriEGuida").style.display = '';
				document.getElementById("popupHelpListaValori").style.display = 'none';
				}
			else {
				document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
				document.getElementById("popupHelpListaValori").style.display = '';
				
				}
		} else {
			if ("INNOT IN".indexOf(document.filtroRicercaForm.operatore.value) >=0){
				document.getElementById("popupHelpListaValoriEGuida").style.display = '';
				document.getElementById("popupHelpListaValori").style.display = 'none';
			} else {
				document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
				document.getElementById("popupHelpListaValori").style.display = 'none';
			}
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
	var ultimoOperatoreIn = notDefined;
		
	
  	function impostaAbilitazioniCampi() {
  		var setDisplayNotCaseSensitive = 'none';
<c:if test='${applicationScope.attivaCaseSensitive}'>
			setDisplayNotCaseSensitive = '';
</c:if>
  		if (document.filtroRicercaForm.operatore.value == "" || strGrpOp1.indexOf(document.filtroRicercaForm.operatore.value) >=0) {
		  	//Gruppo Operatori n. 1
				//alert('operatore = null o gruppo 1');
				document.getElementById("tabella").style.display = 'none';
    		document.getElementById("campo").style.display = 'none';
    		document.getElementById("tipoConfr").style.display = 'none';
    		document.getElementById("tabellaConfr").style.display = 'none';
    		document.getElementById("campoConfr").style.display = 'none';
    		document.getElementById("valore").style.display = 'none';
    		document.getElementById("parametro").style.display = 'none';
    		document.getElementById("spNotCaseSensitive").style.display = 'none';
			
      	document.filtroRicercaForm.aliasTabella.selectedIndex = 0;
      	document.filtroRicercaForm.aliasTabella.value = "";
      	document.filtroRicercaForm.descrizioneCampo.selectedIndex = 0;
      	document.filtroRicercaForm.mnemonicoCampo.value = "";
      	document.filtroRicercaForm.tipoConfronto.selectedindex = 0;
      	document.filtroRicercaForm.tipoConfronto.value = "";
      	document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
      	document.filtroRicercaForm.aliasTabellaConfronto.value = "";
      	document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
      	document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
      	document.filtroRicercaForm.valoreConfronto.value = "";
      	document.filtroRicercaForm.notCaseSensitive.checked = false;
     		//document.filtroRicercaForm.parametroConfronto.value = "";      
      
     		//Riattivo le opzioni precedentemente disabilitate
				document.getElementById("selectTipoConfr").disabled = false;
				//document.filtroRicercaForm.valoreConfronto.value = "";
				document.filtroRicercaForm.tipoConfronto.selectedIndex = 0;
      
      	ultimoOperatore = grpOp1;
				ultimoConfronto = notDefined;	

		} else if (strGrpOp2.indexOf(document.filtroRicercaForm.operatore.value) >=0) {
	  	//Gruppo Operatori n. 2 
			//alert('operatori 2');
			if (ultimoOperatore == grpOp3) {
				//alert('ultimo operatore gruppo 3');
				document.filtroRicercaForm.tipoConfronto.selectedindex = 0;
	      document.filtroRicercaForm.tipoConfronto.value = "";
				document.getElementById("tipoConfr").style.display = 'none';
				document.getElementById("tabellaConfr").style.display = 'none';
	    	document.getElementById("campoConfr").style.display = 'none';
	    	document.getElementById("valore").style.display = 'none';
	    	document.getElementById("parametro").style.display = 'none';
    		document.getElementById("spNotCaseSensitive").style.display = 'none';
			
	      document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
	      document.filtroRicercaForm.aliasTabellaConfronto.value = "";
	      document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
	      document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
	      document.filtroRicercaForm.valoreConfronto.value = "";
	      document.filtroRicercaForm.notCaseSensitive.checked = false;
	      //document.filtroRicercaForm.parametroConfronto.value = "";			

				//Riattivo le opzioni precedentemente disabilitate
				document.getElementById("selectTipoConfr").disabled = false;
				//document.filtroRicercaForm.valoreConfronto.value = "";
				document.filtroRicercaForm.tipoConfronto.selectedIndex = 0;
				document.getElementById("popupHelpListaValori").style.display = 'none';
				document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';

      	//document.filtroRicercaForm.tipoConfronto.options[1].disabled = false;
	     	//document.filtroRicercaForm.tipoConfronto.options[3].disabled = false;
      	//document.filtroRicercaForm.tipoConfronto.options[4].disabled = false;
	     		     	
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
	    	
				document.filtroRicercaForm.aliasTabella.selectedIndex = 0;
     		document.filtroRicercaForm.aliasTabella.value = "";
     		document.filtroRicercaForm.descrizioneCampo.selectedIndex = 0;
     		document.filtroRicercaForm.mnemonicoCampo.value = "";
     		document.filtroRicercaForm.tipoConfronto.selectedindex = 0;
     		document.filtroRicercaForm.tipoConfronto.value = "";
     		document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
     		document.filtroRicercaForm.aliasTabellaConfronto.value = "";
     		document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
     		document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
     		document.filtroRicercaForm.valoreConfronto.value = "";
     		//document.filtroRicercaForm.parametroConfronto.value = "";  
			}
			ultimoOperatore = grpOp2;
			ultimoConfronto = notDefined;

    } else { //Gruppo Operatori n. 3 
    
			//alert('operatore gruppo 3');
    		if (document.getElementById("tabellato").value != "null" && document.getElementById("tabellato").value != "") {
    				if (document.filtroRicercaForm.operatore.value == "IN" || document.filtroRicercaForm.operatore.value == "NOT IN") {
						document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
						document.getElementById("popupHelpListaValori").style.display = '';
					} else {
						document.getElementById("popupHelpListaValoriEGuida").style.display = '';
						document.getElementById("popupHelpListaValori").style.display = 'none';
					}
				} else {
				
					if (document.filtroRicercaForm.operatore.value == "IN" || document.filtroRicercaForm.operatore.value == "NOT IN") {
				
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
				
				document.filtroRicercaForm.tipoConfronto.selectedindex = 0;
     		document.filtroRicercaForm.tipoConfronto.value = "";
     		document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
     		document.filtroRicercaForm.aliasTabellaConfronto.value = "";
     		document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
     		document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
     		document.filtroRicercaForm.valoreConfronto.value = "";
     		document.filtroRicercaForm.notCaseSensitive.checked = false;
     		//document.filtroRicercaForm.parametroConfronto.value = "";
	      
			} else if (ultimoOperatore == notDefined || ultimoOperatore == grpOp1){

				//alert('ultimo operatore gruppo 1 o non definito');
				document.getElementById("tabella").style.display = '';
    		document.getElementById("campo").style.display = '';
    		document.getElementById("tipoConfr").style.display = '';
    		document.getElementById("tabellaConfr").style.display = 'none';
    		document.getElementById("campoConfr").style.display = 'none';
    		document.getElementById("valore").style.display = 'none';
    		document.getElementById("parametro").style.display = 'none';
    		//document.getElementById("spNotCaseSensitive").style.display = '';
    		document.getElementById("spNotCaseSensitive").style.display = setDisplayNotCaseSensitive;
	    	
				document.filtroRicercaForm.aliasTabella.selectedIndex = 0;
     		document.filtroRicercaForm.aliasTabella.value = "";
     		document.filtroRicercaForm.descrizioneCampo.selectedIndex = 0;
     		document.filtroRicercaForm.mnemonicoCampo.value = "";
     		document.filtroRicercaForm.tipoConfronto.selectedindex = 0;
     		document.filtroRicercaForm.tipoConfronto.value = "";
     		document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
     		document.filtroRicercaForm.aliasTabellaConfronto.value = "";
     		document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
     		document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
     		document.filtroRicercaForm.valoreConfronto.value = "";
     		document.filtroRicercaForm.notCaseSensitive.checked = false;
     		//document.filtroRicercaForm.parametroConfronto.value = "";
	      
    	} else if(document.filtroRicercaForm.operatore.value == "IN" || document.filtroRicercaForm.operatore.value == "NOT IN"){
			<% 
			 /* Gestione dell'operatore IN: 
			  * - si disattivano due opzioni dalla select "tipoConfronto";
			  * - si deseleziona un eventuale opzione dalla select "tipoConfronto";
			  * - si visualizza l'oggetto ;
			  * - si nascondo gli oggetti "valore", "tabellaConfr", "campoConfr",
			  *   "parametro" e si resettano i loro valori;
			  */ %>
					//alert('ultimo operatore in not in');
      		document.filtroRicercaForm.tipoConfronto.selectedIndex = 2;
					document.getElementById("selectTipoConfr").disabled = true;
				
	    		document.getElementById("valore").style.display = '';
	    		document.getElementById("tabellaConfr").style.display = 'none';
	    		document.getElementById("campoConfr").style.display = 'none';
  	  		document.getElementById("parametro").style.display = 'none';
  	  		//document.getElementById("spNotCaseSensitive").style.display = '';
	      	
	    		//document.filtroRicercaForm.valoreConfronto.value = "";
	    		document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
		    	document.filtroRicercaForm.aliasTabellaConfronto.value = "";
		    	document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
		    	document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
		    	//document.filtroRicercaForm.notCaseSensitive.checked = false;

					ultimoOperatoreIn = true;
					//document.getElementById("popupHelpListaValori").style.display = '';
					document.getElementById("popupHelpListaValoriEGuida").style.display = '';
					document.getElementById("popupHelpListaValori").style.display = 'none';

	    	} else {
	    		//alert('tutti gli altri');
	    		if (ultimoOperatoreIn)
	    			//document.filtroRicercaForm.valoreConfronto.value = "";
					document.getElementById("selectTipoConfr").disabled = false;
					//document.filtroRicercaForm.valoreConfronto.value = "";
					//document.filtroRicercaForm.tipoConfronto.selectedIndex = 0;
					
					//document.getElementById("valore").style.display = 'none';
					//document.getElementById("popupHelpListaValori").style.display = 'none';
					ultimoOperatoreIn = false;
					if (document.getElementById("tabellato").value != "null" && document.getElementById("tabellato").value != "") {
						document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
						document.getElementById("popupHelpListaValori").style.display = '';
					} else {
						document.getElementById("popupHelpListaValoriEGuida").style.display = 'none';
						document.getElementById("popupHelpListaValori").style.display = 'none';	
					}
					
					//document.getElementById("spNotCaseSensitive").style.display = '';
					//document.filtroRicercaForm.notCaseSensitive.checked = false;
			}

	    ultimoOperatore = grpOp3;
    }
  }

  function impostaAbilitazioniCampiConfronto() {

    if (document.filtroRicercaForm.tipoConfronto.value == "<%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>") {
			
			if (ultimoConfronto != <%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>) {
				document.getElementById("tabellaConfr").style.display = '';
  	  		document.getElementById("campoConfr").style.display = '';
  	 	}
    	
    	document.getElementById("valore").style.display = 'none';
    	document.getElementById("parametro").style.display = 'none';
    	
     	document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
     	document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
     	document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
     	document.filtroRicercaForm.valoreConfronto.value = "";
			//document.filtroRicercaForm.parametroConfronto.value = "";
			//document.getElementById("popupHelpListaValori").style.display = 'none';
			
			ultimoConfronto = <%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>;

    } else if (document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_VALORE%> ) {

     	document.getElementById("valore").style.display = '';
     	document.filtroRicercaForm.valoreConfronto.value = '';
     	document.getElementById("tabellaConfr").style.display = 'none';
     	document.getElementById("campoConfr").style.display = 'none';
    	document.getElementById("parametro").style.display = 'none';
    	
    	document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
    	document.filtroRicercaForm.aliasTabellaConfronto.value = "";
    	document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
    	document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
    	//document.filtroRicercaForm.parametroConfronto.value = "";
      
     	//if (tabellatoCampo != "null") {
     	//	document.getElementById("popupHelpListaValori").style.display = '';
     	//}
      
			ultimoConfronto = <%=FiltroRicercaForm.TIPO_CONFRONTO_VALORE%>;

    } else if (document.filtroRicercaForm.tipoConfronto.value == "<%=FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO%>") {
    
    	document.getElementById("parametro").style.display = '';
    	document.getElementById("tabellaConfr").style.display = 'none';
    	document.getElementById("campoConfr").style.display = 'none';
     	document.getElementById("valore").style.display = 'none';
     	
      document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
      document.filtroRicercaForm.aliasTabellaConfronto.value = "";
      document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
      document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
      document.filtroRicercaForm.valoreConfronto.value = "";
      //document.getElementById("popupHelpListaValori").style.display = 'none';
      
 			ultimoConfronto = <%=FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO%>;
 		
    } else if (document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_DATA_ODIERNA%>){

			document.getElementById("valore").style.display = 'none';
			document.filtroRicercaForm.valoreConfronto.value = '';
			document.getElementById("tabellaConfr").style.display = 'none';
			document.getElementById("campoConfr").style.display = 'none';
    	document.getElementById("parametro").style.display = 'none';
    	
      document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;			
      document.filtroRicercaForm.aliasTabellaConfronto.value = "";
      document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;			
      document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
      //document.getElementById("popupHelpListaValori").style.display = 'none';
      //document.filtroRicercaForm.parametroConfronto.value = "";
    } else if (document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_UTENTE_CONNESSO%>) {
    	
    	document.getElementById("valore").style.display = 'none';
			document.filtroRicercaForm.valoreConfronto.value = '';
			document.getElementById("tabellaConfr").style.display = 'none';
			document.getElementById("campoConfr").style.display = 'none';
			document.getElementById("parametro").style.display = 'none';
			
			document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;			
			document.filtroRicercaForm.aliasTabellaConfronto.value = "";
			document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;			
			document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
			//document.getElementById("popupHelpListaValori").style.display = 'none';
			//document.filtroRicercaForm.parametroConfronto.value = "";
			
    } else if (document.filtroRicercaForm.tipoConfronto.value == <%=FiltroRicercaForm.TIPO_CONFRONTO_UFFICIO_INTESTATARIO%>) {
    	
    	document.getElementById("valore").style.display = 'none';
			document.filtroRicercaForm.valoreConfronto.value = '';
			document.getElementById("tabellaConfr").style.display = 'none';
			document.getElementById("campoConfr").style.display = 'none';
			document.getElementById("parametro").style.display = 'none';
			
			document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;			
			document.filtroRicercaForm.aliasTabellaConfronto.value = "";
			document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;			
			document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
			//document.getElementById("popupHelpListaValori").style.display = 'none';
			//document.filtroRicercaForm.parametroConfronto.value = "";
    	
    } else {

			document.getElementById("tabellaConfr").style.display = 'none';
			document.getElementById("campoConfr").style.display = 'none';
	   	document.getElementById("valore").style.display = 'none';
    	document.getElementById("parametro").style.display = 'none';
      document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
			document.filtroRicercaForm.aliasTabellaConfronto.value = "";
			document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
			document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
			document.filtroRicercaForm.valoreConfronto.value = '';
			//document.getElementById("popupHelpListaValori").style.display = 'none';
			//document.filtroRicercaForm.parametroConfronto.value = "";
			ultimoConfronto = notDefined;
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
		} else if(strGrpOp2.indexOf(operatore) >=0 || strGrpOp3.indexOf(operatore) >= 0) {
			
			ultimoOperatore = grpOp2;

			var posTab = 0;
			var len = listaTabelle.length;
			for (var i=0;  i < len; i++){
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
			
				document.getElementById("tipoConfr").style.display = 'none';
				document.getElementById("tabellaConfr").style.display = 'none';
		    document.getElementById("campoConfr").style.display = 'none';
		    document.getElementById("valore").style.display = 'none';
		    document.getElementById("parametro").style.display = 'none';
			
			  document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
			  document.filtroRicercaForm.aliasTabellaConfronto.value = "";
			  document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
			  document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
			  document.filtroRicercaForm.valoreConfronto.value = "";
  		  //document.filtroRicercaForm.parametroConfronto.selectedIndex = 0;
			  //document.filtroRicercaForm.parametroConfronto.value = "";			
			}		
			
			if (strGrpOp3.indexOf(operatore) >= 0) {
				var tipoConfr = "<c:out value='${tipoConfronto}'/>";
				
				ultimoOperatore = grpOp3;
				
				if (tipoConfr == <%=FiltroRicercaForm.TIPO_CONFRONTO_CAMPO%>) {

					var posTab1 = 0;
					var len = listaTabelle.length;
					for(var i=0;  i < len; i++){
						if(listaTabelle[i] == aliasTabellaConfr){
							posTab1 = i;
						}
					}
					
					var nomeArrayValue = 'valoriCampi' + (posTab1);
					var nomeArrayText = document.filtroRicercaForm.aliasTabellaConfronto.options[posTab1+1].value;
					aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, document.filtroRicercaForm.descrizioneCampoConfronto);
					var posCampo1 = 0;			
					var arrayValori = eval("codiciCampi"+(posTab1));
					for(var i=0; i < arrayValori.length; i++){
					  if(arrayValori[i] == mneCampoConfr){
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
		      		//document.filtroRicercaForm.parametroConfronto.selectedIndex = 0;
		      		//document.filtroRicercaForm.parametroConfronto.value = "";
	
				} else if (tipoConfr == <%=FiltroRicercaForm.TIPO_CONFRONTO_VALORE%>) {
					
					document.getElementById("valore").style.display = '';
					document.getElementById("tabellaConfr").style.display = 'none';
					document.getElementById("campoConfr").style.display = 'none';
					document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
      		document.filtroRicercaForm.aliasTabellaConfronto.value = "";
      		document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
      		document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";

	    		document.getElementById("parametro").style.display = 'none';		
					//document.filtroRicercaForm.parametroConfronto.selectedIndex = 0;
      		//document.filtroRicercaForm.parametroConfronto.value = "";	
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
				} if (tipoConfr == <%=FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO%>) {
					
					document.getElementById("parametro").style.display = '';		
					//document.filtroRicercaForm.parametroConfronto.value = ;

					document.getElementById("tabellaConfr").style.display = 'none';
					document.getElementById("campoConfr").style.display = 'none';
					document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex = 0;
					document.filtroRicercaForm.aliasTabellaConfronto.value = "";
					document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex = 0;
					document.filtroRicercaForm.mnemonicoCampoConfronto.value = "";
					document.getElementById("valore").style.display = 'none';
					document.filtroRicercaForm.valoreConfronto.value = "";
				}
			}			
		}
	}

-->
</script>
