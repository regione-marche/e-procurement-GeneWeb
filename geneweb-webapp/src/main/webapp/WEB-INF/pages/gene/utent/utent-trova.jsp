<%
/*
 * Created on: 28-Apr-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 /* Form di ricerca degli utenti */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "UTENT")}' />

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="UTENT-trova" >

	<gene:setString name="titoloMaschera" value="Ricerca soggetti"/>

	<gene:redefineInsert name="corpo">
	  	<gene:formTrova entita="UTENT" filtro="${filtroLivelloUtente}" gestisciProtezioni="true" >
			<gene:campoTrova campo="CODUTE"/>
			<gene:campoTrova campo="COGUTE"/>
			<gene:campoTrova campo="NOMEUTE"/>
			<gene:campoTrova campo="NOMUTE"/>
			<gene:campoTrova campo="CFUTE"/>
			<gene:campoTrova campo="PIVAUTE"/>
		</gene:formTrova>
	</gene:redefineInsert>
</gene:template>
