<%
/*
 * Created on 26-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DEL TITOLO DELLE PAGINE DI LISTA DEI PERMESSI
 // DEGLI UTENTI SULL'ENTITA' IN ANALSI
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
	<c:when test='${fn:containsIgnoreCase(campoChiave, "CODLAV")}'>
		Condivisione e protezione del lavoro ${valoreChiave}
	</c:when>
	<c:when test='${fn:containsIgnoreCase(campoChiave, "CODGAR")}'>
		<c:choose>
			<c:when test='${genereGara eq "11" }'>
				Condivisione e protezione dell'avviso ${fn:replace(valoreChiave, "$", "")}
			</c:when>
			<c:when test='${genereGara eq "10" }'>
				Condivisione e protezione dell'elenco ${fn:replace(valoreChiave, "$", "")}
			</c:when>
			<c:when test='${genereGara eq "20" }'>
				Condivisione e protezione del catalogo ${fn:replace(valoreChiave, "$", "")}
			</c:when>
			<c:otherwise>
				Condivisione e protezione della gara ${fn:replace(valoreChiave, "$", "")}
			</c:otherwise>
		</c:choose>
		
	</c:when>
</c:choose>
