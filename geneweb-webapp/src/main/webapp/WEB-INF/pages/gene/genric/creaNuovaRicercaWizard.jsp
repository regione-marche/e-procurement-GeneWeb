<%
/*
 * Created on 28-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI SELEZIONE DEL
 // TIPO DI RICERCA (IN FASE DI CREAZIONE) CONTENENTE L'ELENCO EFFETTIVO DEI 
 // DATI ESTRATTI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<form action="" name="formRabioBut">
	<table class="dettaglio-notab">
<% // Visualizzo il check del report base solo se la property con il nome dello
   // schema delle viste per le ricerche base
%>

<c:if test='${fn:contains(listaOpzioniDisponibili, "OP98#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou48#") || (fn:contains(listaOpzioniUtenteAbilitate, "ou49#") && fn:contains(listaOpzioniUtenteAbilitate, "ou53#")))}'>	
	<c:if test='${! empty nomeSchemaVista}'>
		<tr>
		 	<td width="8%" align="right" valign="top">
				<input type="radio" name="azione" id="azione" value="0" checked="checked"/>
				<% //il valore 0 corrisponde al codice del tabellato associato al report base %>
			</td>
			<td class="valore-dato">
				<p>
					<b>Creazione guidata Report base</b>
				</p>
				<p>
					Questa modalit&agrave; consente di produrre, in modo semplificato, report in grado di 
					soddisfare le principali esigenze di estrazione dati. Permette di selezionare 
					un argomento di ricerca, inteso come visione estesa (vista) sui dati, di scegliere 
					le informazioni da estrarre, di impostare i criteri di confronto e l'ordinamento 
					con cui proporre le informazioni.
					Il risultato dell'estrazione &egrave; un elenco di dati strutturati secondo i criteri assegnati.
				</p>
			</td>
		</tr>
	</c:if>
</c:if>
<c:if test='${fn:contains(listaOpzioniDisponibili, "OP98#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou48#") || (fn:contains(listaOpzioniUtenteAbilitate, "ou49#") && fn:contains(listaOpzioniUtenteAbilitate, "ou54#")))}'>	
		<tr>
			<td width="8%" align="right" valign="top">
				<input type="radio" name="azione" id="azione" value="1" />
				<% //il valore 1 corrisponde al codice del tabellato associato al report avanzato %>
			</td>
			<td class="valore-dato">
				<p>
					<b>Creazione guidata Report avanzato</b>
				</p>
				<p>
					Questa modalit&agrave; consente di produrre report pi&ugrave; complessi rispetto alla modalit&agrave; base; 
					permette di selezionare pi&ugrave; argomenti su cui estendere la ricerca e di disporre di 
					operatori avanzati da utilizzare sui campi (operatori logici e di confronto, funzioni 
					statistiche  e parametri di filtro).
					Il risultato dell'estrazione &egrave; un elenco di dati strutturati secondo i criteri assegnati.
				</p>
			</td>
		</tr>
</c:if>
<c:if test='${fn:contains(listaOpzioniDisponibili, "OP98#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou48#") || (fn:contains(listaOpzioniUtenteAbilitate, "ou49#") && fn:contains(listaOpzioniUtenteAbilitate, "ou55#"))) && (fn:contains(listaOpzioniDisponibili, "OP1#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou50#") || fn:contains(listaOpzioniUtenteAbilitate, "ou51#")))}'>			
		<tr>
			<td width="8%" align="right" valign="top">
				<input type="radio" name="azione" id="azione" value="2" />
				<% //il valore 2 corrisponde al codice del tabellato associato al report con modello %>
			</td>
			<td class="valore-dato">
				<p>
					<b>Creazione guidata Report con modello</b>
				</p>
				<p>
					Questa modalit&agrave; consente di produrre report molto sofisticati, soddisfacendo esigenze di 
					elaborazione e rappresentazione dei dati complesse, mediante la predisposizione di un 
					modello (file RTF oppure ODT codificato secondo la sintassi del Generatore Modelli). Il risultato 
					dell'estrazione &egrave; un documento che pu&ograve; essere scaricato e aperto con Word.
				</p>
			</td>
		</tr>
</c:if>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCreazione();">&nbsp;&nbsp;&nbsp;&nbsp;
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:creaNuovaRicerca();">&nbsp;
			</td>
		</tr>
	</table>
</form>