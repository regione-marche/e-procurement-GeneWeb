<%--
/*
 * Created on: 07-mag-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
Form di ricerca degli eventi associabili ad uno scadenzario

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_EVENTISCADENZ-trova">

	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo='Ricerca eventi per scadenzario' id="ricerca" />
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Ricerca eventi per scadenzario"/>

	<gene:redefineInsert name="corpo">
	  	<gene:formTrova entita="G_EVENTISCADENZ" gestisciProtezioni="true" filtro="PRG='${moduloAttivo}'">
			<gene:campoTrova campo="COD" />
			<gene:campoTrova campo="ENT"/>
			<gene:campoTrova campo="DISCR"/>
			<gene:campoTrova campo="TIT"/>
			<gene:campoTrova campo="DESCR"/>
		</gene:formTrova>
	</gene:redefineInsert>
</gene:template>
