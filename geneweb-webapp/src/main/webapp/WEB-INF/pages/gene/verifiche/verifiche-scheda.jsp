<%
			/*
       * Created on: 11.52 01/06/2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
      /*
				Descrizione:
					Scheda di astra
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<!-- inserisco il mio tag -->
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="VerificheScheda" >
	<c:set var="entita" value="VERIFICHE" />
	<c:set var="descrizione" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetDescrizioneTipoVerificaFunction", pageContext, key)}'/>
	<!-- Settaggio delle stringhe utilizate nel template
	<gene:setString name="titoloMaschera" value="Verifica art.80 ID=${gene:getValCampo(key, 'VERIFICHE.ID')}"/>
	 -->
	 
	<gene:setString name="titoloMaschera" value="${descrizione}"/>
	<gene:redefineInsert name="corpo">
	
		<gene:formPagine gestisciProtezioni="true" >
			<c:if test='${param.archiviati ne 1}'>
				<gene:pagina title="Dati generali" idProtezioni="DATIGEN">
					<jsp:include page="verifica-interno-scheda.jsp" />
				</gene:pagina>
			</c:if>			
			<gene:pagina title="Documenti verifiche" idProtezioni="DOCVERIF" >
				<jsp:include page="verifica-listaDocumenti.jsp" />
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>