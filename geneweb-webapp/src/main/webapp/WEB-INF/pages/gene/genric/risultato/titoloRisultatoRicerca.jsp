<%
/*
 * Created on 22-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DEL TITOLO DELLA PAGINA CONTENENTE I DATI ESTRATTI
 // CON LA RICERCA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

Dati estratti report <c:if test="${fn:length(risultatoRicerca.titoloRicerca) gt 0}">'${risultatoRicerca.titoloRicerca}'</c:if>