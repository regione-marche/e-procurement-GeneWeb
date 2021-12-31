<%
/*
 * Created on 04-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
// PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO GRUPPO 
// CONTENENTE I DATI DI UN GRUPPO E LE RELATIVE FUNZIONALITA' IN SOLA VISUALIZZAZIONE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />

<table class="dettaglio-tab">
    <tr>
      <td class="etichetta-dato"> Nome </td>
      <td class="valore-dato"> <c:out value="${gruppoForm.nomeGruppo}" /> </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Descrizione</td> <!-- title="Descrizione" sarebbe un doppione -->
      <td class="valore-dato"> <c:out value="${gruppoForm.descrizione}" /> </td>
    </tr>
    
    <c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
        	<INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica gruppo" onclick="javascript:modifica('<c:out value='${gruppoForm.idGruppo}' />')">
        &nbsp;
      </td>
    </tr>
    </c:if>
</table>