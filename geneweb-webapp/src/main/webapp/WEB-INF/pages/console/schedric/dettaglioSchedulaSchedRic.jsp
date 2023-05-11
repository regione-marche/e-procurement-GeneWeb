<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="schedRicForm" value="${requestScope.schedRicForm}" scope="request" />

<table class="dettaglio-tab">
  <tr>
    <td class="etichetta-dato">Frequenza</td>
    <td class="valore-dato"> <c:out value="${schedRicForm.descTipo}"/> </td>
  </tr>
  <tr>
    <td class="etichetta-dato" >Ora di lancio</td>
    <td class="valore-dato"> 
   		<c:out value="${schedRicForm.strOraAvvio}"/>:<c:out value="${schedRicForm.strMinutoAvvio}"/>
    </td>
  </tr>
  <c:if test='${!empty schedRicForm.dataPrimaEsec }'>
	  <c:choose>
	  	<c:when test='${(schedRicForm.tipo eq "U")}'>
		  <tr>
	    	<td class="etichetta-dato" >
	    		Data di lancio
	    	</td>
		    <td class="valore-dato"> 
		   		<c:out value="${schedRicForm.dataPrimaEsec}"/>
		    </td>
		  </tr>
		</c:when>
		<c:when test='${(schedRicForm.tipo eq "G")}'>
		  <tr>
	    	<td class="etichetta-dato" >
	    		Dal giorno
	    	</td>
		    <td class="valore-dato"> 
		   		<c:out value="${schedRicForm.dataPrimaEsec}"/>
		    </td>
		  </tr>
	  	</c:when>
	  </c:choose>
  </c:if>	
  <tr>
    <td class="etichetta-dato" >Impostazioni sulla periodicità</td>
    <td class="valore-dato"> 
   		<c:out value="${schedRicForm.periodoOgni}"/>
    </td>
  </tr>
  <tr>
    <td class="comandi-dettaglio" colSpan="2">
      <INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica dati generali" onclick="javascript:modifica(${schedRicForm.idSchedRic})" >&nbsp;
    </td>
  </tr>
</table>  