<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="schedRicForm" value="${requestScope.schedRicForm}" scope="request" />

<table class="dettaglio-tab">
  <tr>
    <td class="etichetta-dato">Nome</td>
    <td class="valore-dato"> <c:out value="${schedRicForm.nome}"/> </td>
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
<c:if test='${!empty utenteEsecutore}'>
	<tr>
    <td class="etichetta-dato">Esegui come utente</td>
    <td class="valore-dato">${utenteEsecutore}</td>
  </tr>
</c:if>
	<tr>
    <td class="etichetta-dato" >Data ultima esecuzione</td>
    <td class="valore-dato"><c:out value="${fn:substring(schedRicForm.dataUltEsec, 0, 16)}"/></td>
  </tr>
  <tr>
    <td class="etichetta-dato" >Data prossima esecuzione</td>
    <td class="valore-dato">
	    <c:if test='${! empty schedRicForm.dataProxEsec}'>
	    	<c:out value="${schedRicForm.dataProxEsec}"/> <c:out value="${schedRicForm.strOraAvvio}"/>:<c:out value="${schedRicForm.strMinutoAvvio}"/>
	    </c:if>
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato" >Report</td>
    <td class="valore-dato"><c:out value="${schedRicForm.nomeRicerca}"/></td>
  </tr>
<c:if test='${! empty schedRicForm.descFormato}'>
  <tr>
    <td class="etichetta-dato" >Formato Risultato</td>
    <td class="valore-dato"><c:out value="${schedRicForm.descFormato}"/> </td>
  </tr>
  <tr>
    <td class="etichetta-dato" >Non generare un risultato per un report vuoto</td>
    <td class="valore-dato">
	 	<c:choose>
	 		<c:when test='${schedRicForm.noOutputVuoto}'>
	 			<c:out value='Si' />
	 		</c:when>
	 		<c:otherwise>
	 			<c:out value='No' />
	 	</c:otherwise> 
	 	</c:choose>
    </td>
  </tr>
</c:if>
  <tr>
    <td class="etichetta-dato" >Email</td>
    <td class="valore-dato">${schedRicForm.email}</td>
  </tr>
  <tr>
    <td class="comandi-dettaglio" colSpan="2">
      <INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica dati generali" onclick="javascript:modifica(${schedRicForm.idSchedRic})" >&nbsp;
    </td>
  </tr>
</table>  