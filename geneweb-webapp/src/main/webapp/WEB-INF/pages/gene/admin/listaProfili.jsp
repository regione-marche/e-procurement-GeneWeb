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
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA PROFILI 
 // CONTENENTE LA EFFETTIVA LISTA DEI PROFILI 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<display:table name="listaProfiliForm" defaultsort="1" id="profiloForm" class="datilista" requestURI="ListaProfili.do" >
	<display:column property="nome" title="Nome" href="DettaglioProfilo.do?" paramId="codPro"  
					paramProperty="codiceProfilo" sortable="true" headerClass="sortable"></display:column>
	<display:column property="descrizione" title="Descrizione" sortable="true" headerClass="sortable"></display:column>
</display:table>