<%
/*
 * Created on: 09-mar-2016
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

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="W_CONFIG-Trova">

<gene:redefineInsert name="addHistory">
	<c:if test='${param.metodo ne "nuova"}' >
		<gene:historyAdd titolo='Ricerca configurazioni' id="ricerca" />
	</c:if>
</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Ricerca configurazioni"/>

	<gene:redefineInsert name="corpo">
	  	<gene:formTrova entita="W_CONFIG" gestisciProtezioni="true">
	  		<gene:campoTrova campo="SEZIONE" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoSezione"/>
			<gene:campoTrova title="Configurazione" campo="CHIAVE"/>
			<gene:campoTrova campo="VALORE"/>
			<gene:campoTrova campo="DESCRIZIONE"/>
		</gene:formTrova>
	</gene:redefineInsert>
	<gene:redefineInsert name="trovaCreaNuovo"></gene:redefineInsert>
</gene:template>
