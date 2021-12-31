<%
/*
 * Created on 28-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
 // DATI GENERALI DI UNA RICERCA (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA 
 // SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<%@ page import="java.util.Vector,
                it.eldasoft.utils.metadata.domain.Schema,
                it.eldasoft.utils.metadata.domain.Tabella" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">

<%
  /* 
   *Generazione degli array javascript contenenti elenco schemi e 
   * elenco tabelle relative a ciascuno schema.		
   */
  
	  Vector elencoSchemi = (Vector) request.getAttribute("elencoSchemi");

		String listaSchemi = "var listaSchemi = new Array (";
		for(int i=0; i < elencoSchemi.size(); i++){
		  if(i>0)
		    listaSchemi += ",\n";
		  listaSchemi += "\"" + ((Schema)elencoSchemi.elementAt(i)).getCodice().replaceAll("[\"]","\\\\\"") + "\"";;
		}
		listaSchemi += ");\n";
		
    Vector elencoTabelle = null;
    Schema schema = null;
    Tabella tabella = null;
    String dichiarazioneListaSelect = null;
    String dichiarazioneValueSelect = null;
    for (int i = 0; i< elencoSchemi.size(); i++) {
      schema = (Schema) elencoSchemi.elementAt(i);
      dichiarazioneListaSelect = "var " + schema.getCodice() + " = new Array ( '',";
      dichiarazioneValueSelect = "var valoriTabelle" + i + " = new Array ( '',";
      elencoTabelle = (Vector) request.getAttribute("elencoTabelle" + schema.getCodice());
      for (int j = 0; j < elencoTabelle.size(); j++) {
        tabella = (Tabella) elencoTabelle.elementAt(j);
        if (j>0) {
          dichiarazioneListaSelect += ",\n";
          dichiarazioneValueSelect += ",\n";
        }
        dichiarazioneListaSelect += "\"" + tabella.getCodiceMnemonico() + " - " + tabella.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneValueSelect += "\"" + tabella.getNomeTabella().replaceAll("[\"]","\\\\\"") + "\"";
      }
      dichiarazioneListaSelect += ");\n";
      dichiarazioneValueSelect += ");\n";
	%>

	<%=dichiarazioneListaSelect%>
	<%=dichiarazioneValueSelect%>
	<%
	    }
  %>
	<%=listaSchemi%>


<!-- 

	// Azioni invocate dal menu contestuale

	function apriTrovaRicerche(){
		document.location.href='InitTrovaRicerche.do?'+csrfToken;
	}
	
	// Azioni invocate dal tab menu



	// Azioni di pagina

	function gestisciSubmit() {
	  // In questa funzione eseguo l'update della form
		var esito = true;
			
		if (esito && !controllaCampoInputObbligatorio(document.datiGenProspettoForm.tipoRicerca, 'Tipo report')){
		  esito = false;
		}
		if (esito && !controllaCampoInputObbligatorio(document.datiGenProspettoForm.nome, 'Titolo')){
		  esito = false;
		}
		  if (esito && !controllaCampoInputNoCarSpeciali(document.datiGenProspettoForm.codReportWS, 'Pubblica il report come servizio con codice')){
			  esito = false;
			}
		// Se si è in inserimento controllo che sia stato modificato il file
		if(document.datiGenProspettoForm.nomeFile.value==""){
			// alert(document.datiGenProspettoForm.selezioneFile.value);
			if (esito && !controllaCampoInputObbligatorio(document.datiGenProspettoForm.selezioneFile, 'File')){
			  esito = false;
			}
		}
		<%
		// Se si e' in modifica e si vuole caricate un nuovo file verifico che tale file abbia nome identico all'originale
		//if(datiGenProspettoForm.metodo.value == 'updateProspetto'){
		//	if(datiGenProspettoForm.selezioneFile.value != ''){
		//		var nomeFile = datiGenProspettoForm.selezioneFile.value;
		//		nomeFile = nomeFile.substring(nomeFile.lastIndexOf('\\')+1);
		//		nomeFile = nomeFile.substring(nomeFile.lastIndexOf('/')+1);
		//		if(nomeFile != datiGenProspettoForm.nomeFile.value){
		//			alert("Il nome del file da caricare deve rimanere identico a quello usato in fase di definizione del modello.");
		//			esito = false;
		//		}
		//	}
		//}%>
		
		if (document.datiGenProspettoForm.tipoFonteDati.value == 0) {
			if (esito && !controllaCampoInputObbligatorio(document.datiGenProspettoForm.schemaPrinc, 'Schema principale')){
				esito = false;
			}
			
			if (esito && !controllaCampoInputObbligatorio(document.datiGenProspettoForm.entPrinc, 'Argomento principale')){
				esito = false;
			}
		} else {
			if (esito && !controllaCampoInputObbligatorio(document.datiGenProspettoForm.idRicercaSrc, 'Report origine dati')){
				esito = false;
			}
		}
		
		
		if (esito){
			bloccaRichiesteServer();
			document.datiGenProspettoForm.submit();
		}
	}
	
	function annulla(){
		bloccaRichiesteServer();
		document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
	}

	function annullaModifiche(id){
		bloccaRichiesteServer();
		document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=visualizza&idRicerca='+id;
	}

	function aggiornaOpzioniSelectTabella(indice) {
		var nomeArrayValue = 'valoriTabelle' + (indice-1);
		var nomeArrayText = document.datiGenProspettoForm.schemaPrinc.options[indice].value;
		var selectDaAggiornare = document.datiGenProspettoForm.entPrinc;

		if (indice == 0) {
			selectDaAggiornare.length = 1;
			selectDaAggiornare.options[0].text = "";
			selectDaAggiornare.options[0].value = "";
		} else {
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, selectDaAggiornare);
			selectDaAggiornare.selectedIndex = 0;
		}
	}
	
	function initComboTabella() {
		var indice = document.datiGenProspettoForm.schemaPrinc.selectedIndex;
		if (indice > 0) {
			var nomeArrayValue = 'valoriTabelle' + (indice-1);
			var nomeArrayText = document.datiGenProspettoForm.schemaPrinc.options[indice].value;
			var selectDaAggiornare = document.datiGenProspettoForm.entPrinc;
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, selectDaAggiornare);
			var arrayValori = eval(nomeArrayValue);
			for(var index=1; index < arrayValori.length; index++){
				if(arrayValori[index] == "<c:out value='${datiGenProspettoForm.entPrinc}'/>"){
					document.datiGenProspettoForm.entPrinc.value = arrayValori[index];
					break;
				}
			}
		}
	}
	
	function initSorgenteDati() {
			if (document.datiGenProspettoForm.tipoFonteDati.value == 0) {
				document.getElementById("rowReport").style.display = "none";
				document.getElementById("rowSchema").style.display = "";
				document.getElementById("rowArgomento").style.display = "";
				document.datiGenProspettoForm.idRicercaSrc.selectedIndex = 0;
			} else {
				document.getElementById("rowReport").style.display = "";
				document.getElementById("rowSchema").style.display = "none";
				document.getElementById("rowArgomento").style.display = "none";
				document.datiGenProspettoForm.schemaPrinc.selectedIndex = 0;
				document.datiGenProspettoForm.entPrinc.selectedIndex = 0;
				aggiornaOpzioniSelectTabella(0);
			}
	}


-->
</script>