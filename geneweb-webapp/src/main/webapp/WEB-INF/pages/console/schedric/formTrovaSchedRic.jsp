<%
/*
 * Created on 13-lug-2006
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
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<html:form action="/TrovaSchedRic"  >
	<table class="ricerca">
    
    <tr>
      <td class="etichetta-dato" >Nome</td>
      <td class="operatore-trova">
	     	<html:select property="operatoreNome" value="${trovaSchedRicForm.operatoreNome}">
		     	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	     	</html:select>      
      </td>
      <td class="valore-dato-trova"> 
      	<html:text property="nome" styleId="nome" size="50" maxlength="50" value="${trovaSchedRicForm.nome}"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Report</td>
      <td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova">
      	<html:select property="idRicerca" value="${trovaSchedRicForm.idRicerca}">
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaRicerche" property="idRicerca" labelProperty="nomeRicerca" />
      	</html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Frequenza</td>
      <td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova">
      	<html:select property="tipo" value="${trovaSchedRicForm.tipo}">
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaTipoSched" property="tipoTabellato" labelProperty="descTabellato" />
      	</html:select>
      </td>
    </tr>
    <tr>
		  <td class="etichetta-dato">Attivo</td>
		  <td class="operatore-trova">&nbsp;</td>
		  <td class="valore-dato-trova">
		  	<html:select property="attivo" value="${trovaSchedRicForm.attivo}">
			     	<html:option value="">&nbsp;</html:option>
			     	<html:option value="1">Si</html:option> 
			     	<html:option value="0">No</html:option> 
		   	</html:select>
		  </td>
		</tr>

    <!-- F.D. in base alla nuova gestione dei permessi nascondo il filtro per gruppo e utente creatore
    per gli utenti che non hanno l'ou62 -->
    <c:choose>
	    <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou62#")}'>
		    <tr>
		      <td class="etichetta-dato">Utente schedulatore</td>
					<td class="operatore-trova">&nbsp;</td>
		      <td class="valore-dato-trova"> 
		      	<html:select property="owner" value="${trovaSchedRicForm.owner}">
		      		<html:option value="">&nbsp;</html:option>
			      	<html:options collection="listaUtenti" property="idAccount" labelProperty="nome" />
		      	</html:select>		    	
		      </td>
		    </tr>
	    </c:when>
	    <c:otherwise>
			<!-- html:hidden property="owner" value="${profiloUtente.id }"/ -->
	    </c:otherwise>
	</c:choose>
    <c:choose>
    	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou62#")}'>
		    <tr>
		      <td class="etichetta-dato">Utente esecutore</td>
					<td class="operatore-trova">&nbsp;</td>
		      <td class="valore-dato-trova">
		      	<html:select property="esecutore" value="${trovaSchedRicForm.esecutore}">
		      		<html:option value="">&nbsp;</html:option>
			      	<html:options collection="listaUtenti" property="idAccount" labelProperty="nome" />
		      	</html:select>
		      </td>
		    </tr>
    	</c:when>
    	<c:otherwise>
			<html:hidden property="esecutore" value="${profiloUtente.id}"/>
    </c:otherwise>
	</c:choose>
	<jsp:include page="/WEB-INF/pages/commons/opzioniTrova.jsp" />
    <tr>
      <td class="comandi-dettaglio" colSpan="3">
        <input type="hidden" name="codiceApplicazione" value="${sessionScope.moduloAttivo}"/>
        <input type="hidden" name="metodo" />
      	<INPUT type="button" class="bottone-azione" value="Trova" title="Trova schedulazioni" onclick="javascript:avviaRicercaRic()">
        <INPUT type="button" class="bottone-azione" value="Reimposta" title="Reset dei campi di ricerca" onclick="javascript:nuovaRicerca()">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>

<script type="text/javascript">
	<c:choose>
		<c:when test="${trovaSchedRicForm.visualizzazioneAvanzata}">
			trovaVisualizzazioneOperatori('visualizza');
		</c:when>
		<c:otherwise>
			trovaVisualizzazioneOperatori('nascondi');	
		</c:otherwise>
	</c:choose>
</script>