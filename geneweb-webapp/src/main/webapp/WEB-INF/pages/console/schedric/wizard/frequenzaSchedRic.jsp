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

<html:form action="Frequenza" >
	<table class="dettaglio-notab">
		<tr>
			<td colspan="2">
				<b>Scelta della periodicità</b>
			</td>
		</tr>
		<tr id="campo">
   	  <td class="valore-dato" colspan=2> 
   	  	Esegui questa operazione:<br>
   	  	<html:radio property="tipo" styleId="tipoG" value="G"/>&nbsp;con cadenza giornaliera<br>
				<html:radio property="tipo" styleId="tipoS" value="S"/>&nbsp;con cadenza settimanale<br>
      	<html:radio property="tipo" styleId="tipoM" value="M"/>&nbsp;con cadenza mensile<br>
      	<html:radio property="tipo" styleId="tipoU" value="U"/>&nbsp;una sola volta<br>    	  	    	  	  
	    </td>
  	</tr>
  	<tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="idRicerca"/>
				<html:hidden property="oraAvvio" />
				<html:hidden property="minutoAvvio" />
				<html:hidden property="dataPrimaEsec" />
      	<html:hidden property="giorno"/>
				<html:hidden property="settimana"/>
				<html:hidden property="radioGiorno"/>		
				<html:hidden property="giorniSettimana" />
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
		<html:hidden property="noOutputVuoto" />
		<html:hidden property="codiceApplicazione"/>
		<html:hidden property="ripetiDopoMinuti"/>
		   	<html:hidden property="descFormato"/>
      	<html:hidden property="email"/>
      	<html:hidden property="metodo" value="salvaFrequenza"/>
	  	  <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
  	  	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
      </td>
    </tr>
	</table>
</html:form>