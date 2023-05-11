<%
/*
 * Created on: 08-mar-2007
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="SchedaStoUffint" >
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Gestione storico anagrafe uffici" />
	<gene:redefineInsert name="corpo">
		<jsp:include page="sto_uffint-interno-scheda.jsp" />
  </gene:redefineInsert>
</gene:template>
