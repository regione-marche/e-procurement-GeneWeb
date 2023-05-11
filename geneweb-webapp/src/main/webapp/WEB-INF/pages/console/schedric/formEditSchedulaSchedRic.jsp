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

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI TROVA RICERCHE
 // CONTENENTE IL FORM PER IMPOSTARE I DATI DELLA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<html:form action="/SalvaSchedulazioneSchedRic" >
	<table class="dettaglio-tab">
		<tr id="freq">
		  <td class="etichetta-dato">Frequenza (*)</td>
	      <td class="valore-dato"> 
	      	<html:select property="tipo" onchange="javascript:nascondiPerTipo();">
		      	<html:options collection="listaTipo" property="tipoTabellato" labelProperty="descTabellato" />
	      	</html:select>
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
	    
	    <tr id="giornoG">
	      <td class="etichetta-dato">Esegui l'operazione (*)</td>
	      <td class="valore-dato"> 
	      	<html:radio property="radioGiornoG" styleId="radioGiornoG0" value="0" onclick="javascript:gestioneGiorni();"/>&nbsp;ogni giorno, e ad intervalli di 
      	<html:select property="ripetiDopoMinuti" >
      		<option value=""></option>
      		<html:options collection="listaIntervalli" property="tipoTabellato" labelProperty="descTabellato" />
      	</html:select><br>
					<html:radio property="radioGiornoG" styleId="radioGiornoG1" value="1" onclick="javascript:gestioneGiorni();"/>&nbsp;ogni
	      	<html:select property="giorno" >
	      		<option value=""></option>
		      	<html:options collection="listaGiorniAnno" property="tipoTabellato" labelProperty="descTabellato" />
	      	</html:select>&nbsp;giorni
	      </td>
	    </tr>
	    
	    <tr id="data">
	      <td class="etichetta-dato">Data di inizio (*)</td>
	      <td class="valore-dato"> 
	      	<input type="text" name="dataPrimaEsec" id="dataPrimaEsec" onblur="javascript:controllaInputData(this);" value="${schedRicForm.dataPrimaEsec}" class="data">
						&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
	      </td>
	    </tr>
	    
	    <tr id="settimana">
	       <td class="etichetta-dato">Esegui l'operazione (*)</td>
	      <td class="valore-dato">
	      	Ogni&nbsp; 
	      	<html:select property="settimanaS" styleId="settimanaS">
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
	      			<td>&nbsp;</td>
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
	    
    	<tr id="giornoM">
	      <td class="etichetta-dato">Esegui l'operazione (*)</td>
	      <td class="valore-dato"> 
   				<html:radio property="radioGiornoM" styleId="radioGiornoM1" value="1" onclick="javascript:gestioneGiorniMese();"/>&nbsp;giorno
   				&nbsp;
   				<html:select property="giorniMese" >
   					<option value=""></option>
	      		<html:options collection="listaGiorniMese" property="tipoTabellato" labelProperty="descTabellato" />
	      	</html:select> del mese
	      	<br>
   				<html:radio property="radioGiornoM" styleId="radioGiornoM0" value="0" onclick="javascript:gestioneGiorniMese();"/>&nbsp;ogni
   				&nbsp;
   				<html:select property="settimanaM" styleId="settimanaM">
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
	      				<html:checkbox  name="schedRicForm" property="opzioneMarzo">Marzo</html:checkbox>
	      			</td>
	      			<td>
	      				<html:checkbox  name="schedRicForm" property="opzioneAprile">Aprile</html:checkbox>
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
	      				<html:checkbox name="schedRicForm" property="opzioneSettembre">Settembre</html:checkbox>
	      			</td>
	      			<td>
	      				<html:checkbox name="schedRicForm" property="opzioneOttobre">Ottobre</html:checkbox>
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
	        <INPUT type="button" class="bottone-azione" value="Salva" title="Salva" onclick="javascript:gestisciSubmit();">
	        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModifiche();">
	        &nbsp;
	      </td>
	    </tr>
	</table>
	<html:hidden property="metodo" value="salvaSchedulazione"/>
	<html:hidden property="idSchedRic"/>
	<html:hidden property="idRicerca"/>
	<html:hidden property="settimana" value=""/>
	<html:hidden property="owner"/>
	<html:hidden property="esecutore"/>
	<html:hidden property="attivo"/>
	<html:hidden property="nome"/>
<c:if test="${! empty schedRicForm.formato}"> 
	<html:hidden property="formato"/>
</c:if>
	<html:hidden property="email"/>
	<html:hidden property="dataUltEsec" />
	<html:hidden property="noOutputVuoto" />
	<html:hidden property="codiceApplicazione"/>
</html:form>