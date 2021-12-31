<%/*
       * Created on 6-Giu-2016
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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="TAB5-scheda" schema="GENE">

	<gene:setString name="titoloMaschera" value="${param.titolo}" />
	<c:set var="entita" value="TAB5"/>

	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="TAB5" gestisciProtezioni="true" >
		
			<c:if test="${datiRiga.TAB5_TAB5MOD eq '1'}">
				<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
				<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
			</c:if>
		
			<input type="hidden" name="titolo" value="${param.titolo}" />
			<input type="hidden" name="cod" value="${param.cod}" />
			
			<gene:campoScheda campo="TAB5COD" modificabile="false" defaultValue="${param.cod}"/>
			<gene:campoScheda campo="TAB5NORD" />
			<gene:campoScheda title="Identificativo (alfanumerico)" campo="TAB5TIP" obbligatorio="true" modificabile="${modo eq 'NUOVO'}"/>	
			<gene:campoScheda campo="TAB5DESC" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>	
			<gene:campoScheda campo="TAB5ARC" />
			<gene:campoScheda campo="TAB5MOD" />
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
</gene:template>


