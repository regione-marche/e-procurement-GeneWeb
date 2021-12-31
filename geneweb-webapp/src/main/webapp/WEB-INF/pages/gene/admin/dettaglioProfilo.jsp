<%
/*
 * Created on 02-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
// PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO PROFILO
// CONTENENTE I DATI DI UN PROFILO E LE RELATIVE FUNZIONALITA' IN SOLA VISUALIZZAZIONE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />

<table class="dettaglio-tab">
    <tr>
      <td class="etichetta-dato">Codice</td>
      <td class="valore-dato">${profiloForm.codiceProfilo}</td>
    </tr>
    <tr>
      <td class="etichetta-dato">Nome utente</td>
      <td class="valore-dato">${profiloForm.nome}</td>
    </tr>
    <tr>
      <td class="etichetta-dato">Descrizione</td>
      <td class="valore-dato">${profiloForm.descrizione}</td>
    </tr>
</table>