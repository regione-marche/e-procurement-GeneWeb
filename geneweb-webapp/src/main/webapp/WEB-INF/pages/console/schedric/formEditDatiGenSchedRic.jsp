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

<html:form action="/SalvaSchedRic" >
	<table class="dettaglio-tab">
	  <tr>
      <td class="etichetta-dato" >Nome (*)</td>
      <td class="valore-dato"> 
      	<html:text property="nome" styleId="nome" size="50" maxlength="100"/>
      </td>
	  </tr>
	  <tr>
		<td class="etichetta-dato">Attivo</td>
		<td class="valore-dato"> 
			<c:choose>
			  <c:when test='${schedRicForm.attivo eq "1"}'>
	  	  	Si
	  	  </c:when>
	  	  <c:otherwise>
	  	  	No
	  	  </c:otherwise>
	  	</c:choose>
		</td>
	  </tr>
<c:choose>
	<c:when test='${! empty listaUtentiEsecutori}'>  	
		<tr id="campo">
      <td class="etichetta-dato">Esegui come utente (*)</td>
      <td class="valore-dato">
				<select name="esecutore">
      		<option value=""></option>
      	<c:forEach items="${listaUtentiEsecutori}" var="account" >
					<option value="${account.idAccount}" <c:if test='${utenteEsecutore eq account.idAccount}'>selected="true" </c:if>>${account.nome}</option>
				</c:forEach>
      	</select>
      </td>
    </tr>
	</c:when>
	<c:otherwise>
		<html:hidden property="esecutore" value="${utenteEsecutore}"/>
	</c:otherwise>
</c:choose>
	  <tr>
		  <td class="etichetta-dato">Data ultima esecuzione</td>
		  <td class="valore-dato"><c:out value="${fn:substring(schedRicForm.dataUltEsec, 0, 16)}"/></td>
		</tr>
		<tr>
		  <td class="etichetta-dato" >Data prossima esecuzione</td>
		  <td class="valore-dato"><c:out value="${schedRicForm.dataProxEsec}"/> <c:out value="${schedRicForm.strOraAvvio}"/>:<c:out value="${schedRicForm.strMinutoAvvio}"/></td>
		</tr>
	  <tr>
	    <td class="etichetta-dato">Report (*)</td>
	    <td class="valore-dato">
	    	<html:select property="idRicerca" onchange="javascript:checkFormato();">
	     		<option value=""></option>
		     	<html:options collection="listaRicerche" property="idRicerca" labelProperty="nomeRicerca" />
	     	</html:select>
	    </td>
	  </tr>
	  <tr id="formatoReport">
	    <td class="etichetta-dato" >Formato (*)</td>
	    <td class="valore-dato"> 
	    	<html:select property="formato" >
	    		<option value="-1"></option>
		    	<html:options collection="listaFormatoSched" property="tipoTabellato" labelProperty="descTabellato" />
	     	</html:select>
	    </td>
	  </tr>
	  <tr id="zeroRecord">
	    <td class="etichetta-dato" >Non generare un risultato per un report vuoto</td>
	    <td class="valore-dato">
	      <html:checkbox property="noOutputVuoto" />
	    </td>
	  </tr>
	  <tr>
	    <td class="etichetta-dato" >Email</td>
	    <td class="valore-dato"> 
		    <html:textarea name="schedRicForm" property="email" rows="7" cols="75" />
		    &nbsp;<a id="jsPopUpHELPLISTAMAIL" href="javascript:showMenuPopup('jsPopUpHELPLISTAMAIL', linksetjsPopUpHelpListaMail);" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></a>
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
	<html:hidden property="metodo" value="salva"/>
	<html:hidden property="idSchedRic"/>
	<html:hidden property="tipo"/>
	<html:hidden property="oraAvvio" />
	<html:hidden property="minutoAvvio" />
	<html:hidden property="dataPrimaEsec" />
	<html:hidden property="dataUltEsec" />
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
	<html:hidden property="owner"/>
 	<html:hidden property="fromPage" value="datiGenSched"/>
	<html:hidden property="codiceApplicazione"/>
	<html:hidden property="ripetiDopoMinuti"/>
</html:form>

<c:if test='${empty schedRicForm.formato}'>
<script type="text/javascript">
<!-- 

	document.getElementById("formatoReport").style.display = 'none';
	document.schedRicForm.formato.value = "";
	document.getElementById("zeroRecord").style.display = 'none';
	document.schedRicForm.noOutputVuoto.checked = false;

-->
</script>
</c:if>