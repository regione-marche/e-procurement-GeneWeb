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
 
Scheda categorie d'iscrizione

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_EVENTISCADENZ-scheda">
	<gene:setString name="titoloMaschera" value="Dettaglio evento per scadenzario" />
	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="G_EVENTISCADENZ" gestisciProtezioni="true">
			<gene:campoScheda campo="COD" keyCheck="true" modificabile='${modoAperturaScheda eq "NUOVO"}' obbligatorio="true" />
			<gene:campoScheda campo="PRG" defaultValue="${moduloAttivo}" visibile="false" />
			<gene:campoScheda campo="ENT" obbligatorio="true" />
			<gene:campoScheda campo="DISCR" obbligatorio="true" />
			<gene:campoScheda campo="TIT" obbligatorio="true" />
			<gene:campoScheda campo="DESCR" />
			<gene:campoScheda campo="FROMVIEW" obbligatorio="true" />
			<gene:campoScheda campo="NOTE" />
			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
		
	</gene:redefineInsert>
</gene:template>
