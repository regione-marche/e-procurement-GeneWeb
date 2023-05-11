<%/*
   * Created on 26-apr-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE FUNZIONI 
  // JAVASCRIPT DELLA PAGINA DI DEFINIZIONE DELL'ARGOMENTO DI UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<%@ page import="java.util.Vector,
                 it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm" %>

<script type="text/javascript">
<!-- 

	// Azioni di pagina

	function avanti(){
		var tabella = document.getElementById("aliasTabella");
		if(tabella.value == null || tabella.value.length == 0){
			alert("Selezionare un argomento");
			tabella.focus();
		} else {
			bloccaRichiesteServer();
		  document.tabellaRicercaForm.submit();
		}
	}

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}	

<%
	/* 
   *Generazione degli array javascript contenenti elenco tabelle relative allo
   *schema delle viste per le ricerche base.
	 */
  
    Vector elencoTabelle = null;
    TabellaRicercaForm tabella = null;
    String dichiarazioneValueSelect = null;
    String dichiarazioneCodiciSelect = null;
    dichiarazioneValueSelect = "var valoriTabelle = new Array ( '',";
    dichiarazioneCodiciSelect = "var codiciTabelle = new Array ( '',";
    elencoTabelle = (Vector) request.getAttribute("elencoTabelle");
    for (int j = 0; j < elencoTabelle.size(); j++) {
      tabella = (TabellaRicercaForm) elencoTabelle.elementAt(j);
      if (j>0) {
        dichiarazioneValueSelect += ",\n";
        dichiarazioneCodiciSelect += ",\n";
      }
      dichiarazioneValueSelect += "\"" + tabella.getDescrizioneTabella().replaceAll("[\"]","\\\\\"") + "\"";
      dichiarazioneCodiciSelect += "\"" + tabella.getMnemonicoTabella() + "\"";
    }
    dichiarazioneValueSelect += ");\n";
    dichiarazioneCodiciSelect += ");\n";
	%>

	<%=dichiarazioneValueSelect%>
	<%=dichiarazioneCodiciSelect%>

-->
</script>