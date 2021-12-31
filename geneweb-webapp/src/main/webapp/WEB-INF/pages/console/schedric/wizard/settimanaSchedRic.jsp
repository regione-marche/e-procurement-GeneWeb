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
				<b>Pianificazione ogni settimana</b>
				<span class="info-wizard">
					Selezionare l'ora e il giorno per l'avvio del report
				</span>
			</td>
		</tr>
		<tr id="ore">
	      <td class="etichetta-dato">Ora di avvio (*)</td>
	      <td class="valore-dato"> 
	      	<html:select property="oraAvvio" >
	      		<option value=""></option>
		      	<html:options collection="listaOre" property="tipoTabellato" labelProperty="descTabellato" />
	      	</html:select>&nbsp;:&nbsp;
	      	<html:select property="minutoAvvio" >
	      		<option value=""></option>
		      	<html:options collection="listaMinuti" property="tipoTabellato" labelProperty="descTabellato" />
	      	</html:select>
	      </td>
	    </tr>
	    <tr id="settimana">
	      <td class="etichetta-dato">Esegui l'operazione (*)</td>
	      <td class="valore-dato">
	      	Ogni&nbsp; 
	      	<html:select property="settimana" >
	      		<option value=""></option>
		      	<html:options collection="listaSettimane" property="tipoTabellato" labelProperty="descTabellato" />
	      	</html:select>
	      	&nbsp;settimane
	      </td>
	    </tr>
		<tr id="giorni">
	      <td class="etichetta-dato">Nei seguenti giorni (*)</td>
	      <td class="valore-dato"> 
	      	<table class="dettaglio-noBorderBottom">
	      		<tr>
	      			<td>
	      				<html:checkbox name="schedRicForm" property="opzioneLunedi">Lunedì</html:checkbox>
	      			</td>
	      			<td>
	      				<html:checkbox  name="schedRicForm" property="opzioneMartedi">Martedì</html:checkbox>
	      			</td>
	      			<td>
	      				<html:checkbox  name="schedRicForm" property="opzioneMercoledi">Mercoledì</html:checkbox>
	      			</td>
	      			<td>
	      				&nbsp;
	      			</td>
	      		</tr>
	      		<tr>
	      			<td>
	      				<html:checkbox name="schedRicForm" property="opzioneGiovedi">Giovedì</html:checkbox>
	      			</td>
	      			<td>
	      				<html:checkbox  name="schedRicForm" property="opzioneVenerdi">Venerdì</html:checkbox>
	      			</td>
	      			<td>
	      				<html:checkbox  name="schedRicForm" property="opzioneSabato">Sabato</html:checkbox>
	      			</td>
	      			<td>
	      				<html:checkbox  name="schedRicForm" property="opzioneDomenica">Domenica</html:checkbox>
	      			</td>
	      		</tr>	      		
	      	</table>
	      </td>
	    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="idRicerca"/>
      	<html:hidden property="tipo"/>
				<html:hidden property="dataPrimaEsec" />
      	<html:hidden property="giorno"/>		
				<html:hidden property="radioGiorno"/>
				<html:hidden property="giorniSettimana"/>
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
		   	<html:hidden property="descFormato"/>
		<html:hidden property="noOutputVuoto" />
		<html:hidden property="codiceApplicazione"/>
		<html:hidden property="ripetiDopoMinuti"/>
      	<html:hidden property="email"/>
      	<html:hidden property="metodo" value="salvaFrequenzaScelta"/>
	 	    <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
 		    <INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
      </td>
    </tr>
	</table>
</html:form>