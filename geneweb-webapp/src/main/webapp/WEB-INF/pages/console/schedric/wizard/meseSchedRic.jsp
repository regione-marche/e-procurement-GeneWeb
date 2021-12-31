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
				<b>Pianificazione ogni mese</b>
				<span class="info-wizard">
					Selezionare l'ora, il giorno per l'avvio del report
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
    <tr id="giorno">
      <td class="etichetta-dato">Esegui l'operazione (*)</td>
      <td class="valore-dato"> 
 				<html:radio property="radioGiorno" styleId="radioGiorno0" value="0" onclick="javascript:gestioneGiorniMese();"/>&nbsp;giorno
 				&nbsp;
 				<html:select property="giorniMese" >
      		<option value=""></option>
					<html:options collection="listaGiorni" property="tipoTabellato" labelProperty="descTabellato" />
			  </html:select> del mese
				<br>
    		<html:radio property="radioGiorno" styleId="radioGiorno1" value="1" onclick="javascript:gestioneGiorniMese();"/>&nbsp;ogni
    		&nbsp;
 				<html:select property="settimana" >
 					<option value=""></option>
	      	<html:options collection="listaSettimana" property="tipoTabellato" labelProperty="descTabellato" />
  	   	</html:select>
   			&nbsp;
  			<html:select property="giorniSettimana" >
  				<option value=""></option>
      		<html:options collection="listaGiorniSettimana" property="tipoTabellato" labelProperty="descTabellato" />
	     	</html:select> del mese
      </td>
    </tr>
		<tr id="mesi">
      <td class="etichetta-dato">Nei seguenti mesi (*)</td>
      <td class="valore-dato"> 
      	<table class="dettaglio-noBorderBottom">
      		<tr>
      			<td>
      				<html:checkbox name="schedRicForm" property="opzioneGennaio">Gennaio</html:checkbox>
      			</td>
      			<td>
      				<html:checkbox name="schedRicForm" property="opzioneFebbraio">Febbraio</html:checkbox>
      			</td>
      			<td>
      				<html:checkbox name="schedRicForm" property="opzioneMarzo">Marzo</html:checkbox>
      			</td>
      			<td>
      				<html:checkbox name="schedRicForm" property="opzioneAprile">Aprile</html:checkbox>
      			</td>
      		</tr>
      		<tr>
      			<td>
      				<html:checkbox name="schedRicForm" property="opzioneMaggio">Maggio</html:checkbox>
      			</td>
      			<td>
      				<html:checkbox name="schedRicForm" property="opzioneGiugno">Giugno</html:checkbox>
      			</td>
      			<td>
      				<html:checkbox  name="schedRicForm" property="opzioneLuglio">Luglio</html:checkbox>
      			</td>
      			<td>
      				<html:checkbox  name="schedRicForm" property="opzioneAgosto">Agosto</html:checkbox>
      			</td>
      		</tr>
      		<tr>
      			<td>
      				<html:checkbox  name="schedRicForm" property="opzioneSettembre">Settembre</html:checkbox>
      			</td>
      			<td>
      				<html:checkbox  name="schedRicForm" property="opzioneOttobre">Ottobre</html:checkbox>
      			</td>
      			<td>
      				<html:checkbox  name="schedRicForm" property="opzioneNovembre">Novembre</html:checkbox>
      			</td>
      			<td>
      				<html:checkbox  name="schedRicForm" property="opzioneDicembre">Dicembre</html:checkbox>
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
				<html:hidden property="opzioneLunedi"/>
				<html:hidden property="opzioneMartedi"/>
				<html:hidden property="opzioneMercoledi"/>
				<html:hidden property="opzioneGiovedi"/>
				<html:hidden property="opzioneVenerdi"/>
				<html:hidden property="opzioneSabato"/>
				<html:hidden property="opzioneDomenica"/>
		   	<html:hidden property="giorno"/>
		   	<html:hidden property="descFormato"/>
		<html:hidden property="noOutputVuoto" />
		<html:hidden property="codiceApplicazione"/>
		<html:hidden property="ripetiDopoMinuti"/>
      	<html:hidden property="metodo" value="salvaFrequenzaScelta"/>
	 	    <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
 		    <INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
      </td>
    </tr>
	</table>
</html:form>