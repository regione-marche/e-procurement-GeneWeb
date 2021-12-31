<%
/*
 * Created on: 29-mag-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form di ricerca dei tecnici delle imprese (TEIM) */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="TrovaTeim">
	<gene:setString name="titoloMaschera" value="Ricerca tecnici delle imprese"/>
	<gene:redefineInsert name="corpo">
  	<gene:formTrova entita="TEIM" gestisciProtezioni="true">
		<gene:gruppoCampi >
			<gene:campoTrova campo="CODTIM"/>
			<gene:campoTrova campo="NOMTIM"/>
			<gene:campoTrova campo="CFTIM"/>
			<gene:campoTrova campo="PIVATEI"/>
		</gene:gruppoCampi>
	</gene:formTrova>
	</gene:redefineInsert>
</gene:template>