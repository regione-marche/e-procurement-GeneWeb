<%
/*
 * Created on: 16/06/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template file="scheda-template.jsp">


	<jsp:include page="verifica-firmadigitale-interno.jsp">
		<jsp:param name="jspParent" value="scheda"/>
	</jsp:include>

</gene:template>