<%/*
   * Created on 29-mar-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI CREAZIONE DI UN NUOVO FILTRO DA AGGIUNGERE AD UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>
<%@ page import="java.util.Vector,
                it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm" %>

<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale

	// Azioni di pagina

	function avanti(){
		var esito = true;
		
    if(document.filtroRicercaForm.operatore.value == "" ||
       document.filtroRicercaForm.mnemonicoCampo.value == "" ||
       document.filtroRicercaForm.valoreConfronto.value == ""){
 	 	  esito = false;
  	}
    if(esito){
    	bloccaRichiesteServer();
		  document.filtroRicercaForm.submit();
		} else {
			alert("E' necessario popolare tutti i campi presenti nella pagina.");
		}
	}
	
	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}

	function indietro(){
	<c:choose>
		<c:when test='${fn:length(filtroRicercaForm.progressivo) > 0}'>
			document.location.href = 'WizardBase.do?'+csrfToken+'&pageTo=FIL3';
		</c:when>
		<c:otherwise>
			document.location.href = 'WizardBase.do?'+csrfToken+'&pageTo=FIL1';
		</c:otherwise>
	</c:choose>
	}

	<% 
		//Generazione del Javascript per la popup di menu:

     Vector elencoCampi = (Vector) request.getAttribute("elencoCampi");
     Vector elencoTabellatiCampi = (Vector) request.getAttribute("elencoTabellatiCampi");
     String dichiarazioneTabellatiSelect = "var tabellatiCampi = new Array ();\n";
     
     CampoRicercaForm campo = null;
     for (int j = 0; j < elencoCampi.size(); j++) {
       String strLinkset = null;
       String strLinksetTabellato = "";
       
       campo = (CampoRicercaForm) elencoCampi.elementAt(j);
       strLinkset = "linksetJsPopUp" + campo.getMnemonicoCampo().substring(campo.getMnemonicoCampo().indexOf("_")+1) + " = \"\";\n";
       strLinkset += "linksetJsPopUp" + campo.getMnemonicoCampo().substring(campo.getMnemonicoCampo().indexOf("_")+1) + "+=creaVocePopUpChiusura(\"" + request.getContextPath() + "/\");\n";
       strLinkset += "linksetJsPopUp" + campo.getMnemonicoCampo().substring(campo.getMnemonicoCampo().indexOf("_")+1) + "+=creaPopUpSubmenu(\"javascript:helpMnemonico('" + campo.getMnemonicoCampo().substring(campo.getMnemonicoCampo().indexOf("_")+1) + "');hideMenuPopup();\",0,\"&nbsp Informazioni campo\");\n";
       
       String tabellato = (String)elencoTabellatiCampi.elementAt(j);
       if (tabellato != null && tabellato != "null"){
         strLinksetTabellato = "linksetJsPopUpTabellatoCampo" + campo.getMnemonicoCampo() + " = \"\"\n";
         strLinksetTabellato += "linksetJsPopUpTabellatoCampo" + campo.getMnemonicoCampo() + " += creaVocePopUpChiusura(\"" + request.getContextPath() + "/\");\n";
         strLinksetTabellato += "linksetJsPopUpTabellatoCampo" + campo.getMnemonicoCampo() + " += creaPopUpSubmenu(\"javascript:helpListaValoriTabellati('" + tabellato + "');hideMenuPopup();\",0,\"&nbsp Valori campo\");\n";
       }

       if (j >= 0 && elencoTabellatiCampi != null && elencoTabellatiCampi.size() >= 0) {
         dichiarazioneTabellatiSelect += "tabellatiCampi[" + j + "] = \"" + tabellato + "\"; \n";
       }
       
%>
      <%=strLinkset %>
      <%=strLinksetTabellato %>
<%
		}
%>
  
  <%=dichiarazioneTabellatiSelect%>
  
  function initPageFiltro(){
  	var tabellatoCampo = tabellatiCampi[document.filtroRicercaForm.mnemonicoCampo.selectedIndex - 1];
  	//alert(tabellatoCampo);
  	if (tabellatoCampo == undefined || tabellatoCampo == "null")
  		document.getElementById("popupHelpListaValori").style.display = 'none';
<c:if test='${! applicationScope.attivaCaseSensitive}'>
		document.getElementById("spNotCaseSensitive").style.display = 'none';
</c:if>	
  }

	function helpListaValoriTabellati(tabellato){
		var action = "${contextPath}/GetTabellato.do";
		var href = String("tabellato="+tabellato+"&funzione=aggiungi&apici=0");
		openPopUpActionCustom(action, href, "listaValoriTabellati", 580, 350, "no","yes");
	}
	
	function aggiungi(valore){
			document.filtroRicercaForm.valoreConfronto.value = valore;
	}
	
	function helpTabellatoCampo(tipo){
		var contenuto = "";
		var nomeObj = "";
		contenuto = document.filtroRicercaForm.mnemonicoCampo.value;
		nomeObj = 'jsPopUpHELPLISTAVALORIFILTRO';
		
		if(contenuto != "")
			showMenuPopup(nomeObj, eval("linksetJsPopUpTabellatoCampo" + contenuto));
	}
	
	function helpCampo(){
		var contenuto = "";
		var nomeObj = "";
		contenuto = document.getElementById('mnemonicoCampo').value;
		contenuto = contenuto.substring(contenuto.indexOf("_")+1);
		nomeObj = 'jsPopUpCAMPO';
		if(contenuto != "")
			showMenuPopup(nomeObj, eval("linksetJsPopUp" + contenuto));
	}

	function visualizzaTabellatoCampo(indiceSelezionato){
	
		var tabellatoCampo = tabellatiCampi[indiceSelezionato-1];
		
		if (tabellatoCampo != "" && tabellatoCampo !="null" && indiceSelezionato > 0)
			document.getElementById("popupHelpListaValori").style.display = '';
		else
			document.getElementById("popupHelpListaValori").style.display = 'none';
	}
		
-->
</script>