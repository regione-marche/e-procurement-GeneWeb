<%
/*
 * Created on: 6-giu-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form di ricerca dei tecnici */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="TAB46-Trova">

<gene:redefineInsert name="addHistory">
	<c:if test='${param.metodo ne "nuova"}' >
		<gene:historyAdd titolo='Ricerca dati tabellati' id="ricerca" />
	</c:if>
</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Ricerca dati tabellati"/>

	<gene:redefineInsert name="corpo">
	  	<gene:formTrova entita="V_TAB4_TAB6" gestisciProtezioni="true">
			<gene:campoTrova campo="TAB46TIP"/>
			<gene:campoTrova title="Descrizione del tabellato" campo="TAB46DESC" definizione="T50"/>
		</gene:formTrova>
	</gene:redefineInsert>
	<gene:redefineInsert name="trovaCreaNuovo"></gene:redefineInsert>
	
	<gene:javaScript>
		$('[id^="jsPopUpCampo"]').hide();
	</gene:javaScript>
	
	
</gene:template>
