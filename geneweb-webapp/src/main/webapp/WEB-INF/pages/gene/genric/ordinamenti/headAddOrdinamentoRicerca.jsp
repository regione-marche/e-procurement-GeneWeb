<%/*
   * Created on 12-ago-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI CREAZIONE DI UN NUOVO ORDINAMENTO DA AGGIUNGERE AD UNA RICERCA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
<c:when test='${fn:length(ordinamentoRicercaForm.mnemonicoSchema) gt 0}' > 
	<c:set var="mneSchema" value='${ordinamentoRicercaForm.mnemonicoSchema}' />
</c:when>
<c:otherwise> 
	<c:set var="mneSchema" value="" />
</c:otherwise>
</c:choose>

<c:choose>
<c:when test='${fn:length(ordinamentoRicercaForm.mnemonicoTabella) gt 0}' > 
	<c:set var="mneTabella" value='${ordinamentoRicercaForm.mnemonicoTabella}' />
</c:when>
<c:otherwise> 
	<c:set var="mneTabella" value="" />
</c:otherwise>
</c:choose>

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<%@ page import="java.util.Vector,
                it.eldasoft.utils.metadata.domain.Campo,
                it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm" %>

<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale
	
	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab){
		document.location.href = 'CambiaTabRicerca.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	function gestisciSubmit(){
		var esito = true;
		if (esito && !controllaCampoInputObbligatorio(document.ordinamentoRicercaForm.aliasTabella, 'Tabella')){
		 	esito = false;
		}
		if (esito && !controllaCampoInputObbligatorio(document.ordinamentoRicercaForm.descrizioneCampo, 'Campo')){
		 	esito = false;
		}
		if (esito){
		 	document.ordinamentoRicercaForm.submit();
			bloccaRichiesteServer();
		}
	}
	
	function annulla(){
		bloccaRichiesteServer();
		cambiaTab('ORD');
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
		  listaTabelle += "\"" + ((TabellaRicercaForm)elencoTabelle.elementAt(i)).getAliasTabella().replaceAll("[\"]","\\\\\"") + "\"";;
		}
		listaTabelle += ");\n";
		
    Vector elencoCampi = null;
    TabellaRicercaForm tabella = null;
    Campo campo = null;
    String dichiarazioneListaSelect = null;
    String dichiarazioneValueSelect = null;
    String dichiarazioneCodiciSelect = null;
    for (int i = 0; i < elencoTabelle.size(); i++) {
      tabella = (TabellaRicercaForm) elencoTabelle.elementAt(i);
      dichiarazioneListaSelect = "var " + tabella.getAliasTabella() + " = new Array ( '',";
      dichiarazioneValueSelect = "var valoriCampi" + i + " = new Array ( '',";
      dichiarazioneCodiciSelect = "var codiciCampi" + i + " = new Array ( '',";
      elencoCampi = (Vector) request.getAttribute("elencoCampi" + tabella.getAliasTabella());
      for (int j = 0; j < elencoCampi.size(); j++) {
        campo = (Campo) elencoCampi.elementAt(j);
        if (j>0) {
          dichiarazioneListaSelect += ",\n";
          dichiarazioneValueSelect += ",\n";
          dichiarazioneCodiciSelect += ",\n";
        }
        dichiarazioneListaSelect += "\"" + campo.getCodiceMnemonico() + " - " + campo.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneValueSelect += "\"" + campo.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneCodiciSelect += "\"" + campo.getCodiceMnemonico() + "\"";
      }
      dichiarazioneListaSelect += ");\n";
      dichiarazioneValueSelect += ");\n";
      dichiarazioneCodiciSelect += ");\n";
	%>

	<%=dichiarazioneListaSelect%>
	<%=dichiarazioneValueSelect%>
	<%=dichiarazioneCodiciSelect%>
	<%
	    }
  %>
	<%=listaTabelle%>
	

	function aggiornaOpzioniSelectCampo(indice) {
		var nomeArrayValue = 'valoriCampi' + (indice-1);
		var nomeArrayText = document.ordinamentoRicercaForm.aliasTabella.options[indice].value;
		var selectDaAggiornare = document.ordinamentoRicercaForm.descrizioneCampo;
	  
		if (indice == 0) {
			selectDaAggiornare.length = 1;
			selectDaAggiornare.options[0].text = "";
			selectDaAggiornare.options[0].value = "";
		} else {
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, selectDaAggiornare);
			selectDaAggiornare.selectedIndex = 0;
			document.ordinamentoRicercaForm.mnemonicoCampo.value = "";
		}
	}

	function aggiornaCampiInput(indiceSchemaSelezionato, indiceSelezionato) {
		var arrayValori = eval("codiciCampi"+(indiceSchemaSelezionato-1)); //tolgo il primo elemento vuoto
		document.ordinamentoRicercaForm.mnemonicoCampo.value = arrayValori[indiceSelezionato];
	}

	function selezionaCombo(){
		var aliasTabella = "<c:out value='${aliasTabella}'/>";
		var mneCampo = "<c:out value='${mneCampo}'/>";
		if( aliasTabella != "" && mneCampo != ""){

			var pos = 0;
			var len = listaCampi.length;
			for(var i=0;  i < len; i++){
				if(document.ordinamentoRicercaForm.aliasTabella[i].value == aliasTabella){
					pos = i-1;
				}
			}
			var arrayValori = eval("codiciCampi"+pos); //tolgo il primo elemento vuoto

			aggiornaOpzioniSelect(pos);

			for(var index=0; index < arrayValori.length; index++){
				if(arrayValori[index] == mneTab){
					document.ordinamentoRicercaForm.mnemonicoCampo.value = arrayValori[index];
					document.ordinamentoRicercaForm.descrizioneCampo[index].selected = true;
					break;
				}
			}
		}
	}

-->
</script>

<script type="text/javascript">
<!--

	selezionaCombo();

-->
</script>
