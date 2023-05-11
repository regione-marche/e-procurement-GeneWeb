<%
/*
 * Created on 30-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA CREAZIONE DI UIN NUOVO FILTRO DA AGGIIUNGERE AD UNA RICERCA BASE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="schedRicForm" value="${schedRicForm}" />

<html:form action="Risultato" >
	<table class="dettaglio-notab">
		<tr>
			<td colspan="2">
				<b>Salvataggio e invio del risultato</b>
				<span class="info-wizard">
			<c:choose>
				<c:when test='${empty schedRicForm.descFormato}'>
					Selezionare l'eventuale formato dei dati estratti ed indicare l'eventuale indirizzo email o la lista degli indirizzi email a cui inoltrare il report
				</c:when>
				<c:otherwise>
					Indicare l'eventuale indirizzo email o la lista degli indirizzi email a cui inoltrare il report
				</c:otherwise>
			</c:choose>
				</span>
			</td>
		</tr>
<c:choose>
	<c:when test='${empty schedRicForm.descFormato}'>
<%/* Nel caso in cui nella seconda pagina del wizard sia stato selezionato
   * un report con modello, il campo schedricForm.descFormato e' stato 
   * valorizzato a 'prospetto'. Questo comporta che non deve essere chiesto il
   * formato del file fornito dalla schedulazione.
	 * Se il campo schedRicForm.descFormato rimane a null, allora nella seconda
	 * pagina del wizard e' stato selezionato un report avanzato o base e quindi 
	 * bisogna che il formato del file che la schedulazione produrra'.
	 */%>
		<tr id="formato">
	    <td class="etichetta-dato">Salva in formato (*)</td>
      <td class="valore-dato"> 
      	<html:select property="formato" >
      		<html:option value=""/>
	      	<html:options collection="listaFormatoSched" property="tipoTabellato" labelProperty="descTabellato" />
      	</html:select>
      </td>
    </tr>
		<tr id="zeroRecord">
	    <td class="etichetta-dato">Non generare un risultato per un report vuoto</td>
      <td class="valore-dato"> 
	      <html:checkbox property="noOutputVuoto" />
      </td>
    </tr>
	</c:when>
	<c:otherwise>
		<html:hidden property="formato" value="-1"/>
		<html:hidden property="noOutputVuoto" value="0"/>
	</c:otherwise>
</c:choose>
    <tr id="email">
      <td class="etichetta-dato">Invia una email a</td>
      <td class="valore-dato"> 
				<html:textarea name="schedRicForm" property="email" rows="7" cols="75" />
		    &nbsp;<a id="jsPopUpHELPLISTAMAIL" href="javascript:showMenuPopup('jsPopUpHELPLISTAMAIL', linksetjsPopUpHelpListaMail);" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></a>
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="idRicerca"/>
      	<html:hidden property="tipo"/>
				<html:hidden property="oraAvvio" />
				<html:hidden property="minutoAvvio" />
				<html:hidden property="dataPrimaEsec" />
		    <html:hidden property="giorno"/>
				<html:hidden property="settimana"/>
				<html:hidden property="radioGiorno"/>
				<html:hidden property="giorniSettimana"/>
				<html:hidden property="opzioneLunedi"/>
				<html:hidden property="opzioneMartedi"/>
				<html:hidden property="opzioneMercoledi"/>
				<html:hidden property="opzioneGiovedi"/>
				<html:hidden property="opzioneVenerdi"/>
				<html:hidden property="opzioneSabato"/>
				<html:hidden property="opzioneDomenica"/>
		    <html:hidden property="giorniMese"/>
				<html:hidden property="opzioneGennaio"/>
				<html:hidden property="opzioneFebbraio"/>
				<html:hidden property="opzioneMarzo"/>
				<html:hidden property="opzioneAprile"/>
				<html:hidden property="opzioneMaggio"/>
				<html:hidden property="opzioneGiugno"/>
				<html:hidden property="opzioneLuglio"/>
				<html:hidden property="opzioneAgosto"/>
				<html:hidden property="opzioneSettembre"/>
				<html:hidden property="opzioneOttobre"/>
				<html:hidden property="opzioneNovembre"/>
				<html:hidden property="opzioneDicembre"/>
		   	<html:hidden property="descFormato" />
      	<html:hidden property="codiceApplicazione"/>
		<html:hidden property="ripetiDopoMinuti"/>
      	<html:hidden property="metodo" value="salvaRisultato"/>
	 	    <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
      	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
      </td>
    </tr>
	</table>
</html:form>