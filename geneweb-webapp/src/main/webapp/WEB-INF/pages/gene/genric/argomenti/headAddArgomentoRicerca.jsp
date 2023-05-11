<%/*
   * Created on 28-ago-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI CREAZIONE DI UN NUOVO ARGOMENTO DA AGGIUNGERE AD UNA RICERCA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
<c:when test='${fn:length(tabellaRicercaForm.mnemonicoSchema) gt 0}' > 
	<c:set var="mneSchema" value='${tabellaRicercaForm.mnemonicoSchema}' />
</c:when>
<c:otherwise> 
	<c:set var="mneSchema" value="" />
</c:otherwise>
</c:choose>

<c:choose>
<c:when test='${fn:length(tabellaRicercaForm.mnemonicoTabella) gt 0}' > 
	<c:set var="mneTabella" value='${tabellaRicercaForm.mnemonicoTabella}' />
</c:when>
<c:otherwise> 
	<c:set var="mneTabella" value="" />
</c:otherwise>
</c:choose>

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<%@ page import="java.util.List,
                it.eldasoft.utils.metadata.domain.Schema,
                it.eldasoft.utils.metadata.domain.Tabella" %>

<script type="text/javascript">
<!-- 
	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab){
		document.location.href = 'CambiaTabRicerca.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	function gestisciSubmit(){
		var esito = true;
		if (esito && !controllaCampoInputObbligatorio(document.tabellaRicercaForm.mnemonicoSchema, 'Schema')){
			esito = false;
		} else if (esito && !controllaCampoInputObbligatorio(document.tabellaRicercaForm.descrizioneTabella, 'Tabella')){
			esito = false;
		}
		if (esito){
			bloccaRichiesteServer();
			document.tabellaRicercaForm.submit();
		}
	}
	
	function annullaModifica(){
		bloccaRichiesteServer();
		cambiaTab('TAB');
	}

	function modifica(progr){
		document.location.href='EditArgomentoRicerca.do?'+csrfToken+'&metodo=edit&prog=' + prog;
	}
	
	function elimina(progr){
		bloccaRichiesteServer();
		document.location.href='EditArgomentoRicerca.do?'+csrfToken+'&metodo=elimina&prog=' + prog;
	}

<%
	/* 
   *Generazione degli array javascript contenenti elenco schemi e 
   * elenco tabelle relative a ciascuno schema.		
	 */
  
	  List<Schema> elencoSchemi = (List<Schema>) request.getAttribute("elencoSchemi");

		String listaSchemi = "var listaSchemi = new Array (";
		for(int i=0; i < elencoSchemi.size(); i++){
		  if(i>0)
		    listaSchemi += ",\n";
		  listaSchemi += "\"" + ((Schema)elencoSchemi.get(i)).getCodice().replaceAll("[\"]","\\\\\"") + "\"";;
		}
		listaSchemi += ");\n";
		
	List<Tabella> elencoTabelle = null;
    Schema schema = null;
    Tabella tabella = null;
    String dichiarazioneListaSelect = null;
    String dichiarazioneValueSelect = null;
    String dichiarazioneCodiciSelect = null;
    for (int i = 0; i< elencoSchemi.size(); i++) {
      schema = elencoSchemi.get(i);
      dichiarazioneListaSelect = "var " + schema.getCodice() + " = new Array ( '',";
      dichiarazioneValueSelect = "var valoriTabelle" + i + " = new Array ( '',";
      dichiarazioneCodiciSelect = "var codiciTabelle" + i + " = new Array ( '',";
      elencoTabelle = (List<Tabella>) request.getAttribute("elencoTabelle" + schema.getCodice());
      for (int j = 0; j < elencoTabelle.size(); j++) {
        tabella = elencoTabelle.get(j);
        if (j>0) {
          dichiarazioneListaSelect += ",\n";
          dichiarazioneValueSelect += ",\n";
          dichiarazioneCodiciSelect += ",\n";
        }
        dichiarazioneListaSelect += "\"" + tabella.getCodiceMnemonico() + " - " + tabella.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneValueSelect += "\"" + tabella.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneCodiciSelect += "\"" + tabella.getCodiceMnemonico() + "\"";
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
	<%=listaSchemi%>
	
	
	function aggiornaOpzioniSelectTabella(indice) {
		var nomeArrayValue = 'valoriTabelle' + (indice-1);
		var nomeArrayText = document.tabellaRicercaForm.mnemonicoSchema.options[indice].value;
		var selectDaAggiornare = document.tabellaRicercaForm.descrizioneTabella;

		if (indice == 0) {
			selectDaAggiornare.length = 1;
			selectDaAggiornare.options[0].text = "";
			selectDaAggiornare.options[0].value = "";
		} else {
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, selectDaAggiornare);
			selectDaAggiornare.selectedIndex = 0;
			document.tabellaRicercaForm.mnemonicoTabella.value = "";
			document.tabellaRicercaForm.aliasTabella.value = "";
		}
	}

	function aggiornaCampiInput(indiceSchemaSelezionato, indiceSelezionato) {
		var arrayValori = eval("codiciTabelle"+(indiceSchemaSelezionato-1)); //tolgo il primo elemento vuoto
		document.tabellaRicercaForm.mnemonicoTabella.value = arrayValori[indiceSelezionato];
		document.tabellaRicercaForm.aliasTabella.value = "";
	}
	
	function selezionaCombo(){
		var mneSchema = "<c:out value='${mneSchema}'/>";
		var mneTab = "<c:out value='${mneTabella}'/>";
		if( mneTab != "" && mneSchema != ""){
			var pos = 0;
			var len = listaSchemi.length;
			for(var i=0;  i < len; i++){
				if(document.tabellaRicercaForm.mnemonicoSchema[i].value == mneSchema){
					pos = i-1;
				}
			}
			var arrayValori = eval("codiciTabelle"+pos); //tolgo il primo elemento vuoto
			
			aggiornaOpzioniSelect('valoriTabelle'+pos, document.tabellaRicercaForm.mnemonicoSchema.options[pos+1].value, document.tabellaRicercaForm.descrizioneTabella);

			for(var index=0; index < arrayValori.length; index++){
				if(arrayValori[index] == mneTab){
					document.tabellaRicercaForm.mnemonicoTabella.value = arrayValori[index];
					document.tabellaRicercaForm.descrizioneTabella[index].selected = true;
					document.tabellaRicercaForm.aliasTabella.value = "";
					document.tabellaRicercaForm.aliasTabella.focus();
					break;
				}
			}
		}
	}
	
	
-->
</script>