<%
/*
 * Created on 24-lug-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DEL TITOLO DELLE PAGINE DI DETTAGLIO DI UN 
 // DOCUMENTO ASSOCIATO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
	<c:when test='${empty documento.id}' >
 		Nuovo documento associato
 	</c:when>
 	<c:otherwise>
 		Documento associato
 	</c:otherwise>
</c:choose>