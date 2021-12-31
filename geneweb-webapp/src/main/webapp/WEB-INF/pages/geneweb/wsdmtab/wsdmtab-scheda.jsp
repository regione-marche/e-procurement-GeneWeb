
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="WSDMTAB-scheda" schema="GENEWEB">
	
	<c:set var="entitaParent" value='WSDMCONFI' scope="request" />
	<c:set var="idconfi" value='${param.idconfi}' scope="request" />
	
	<c:set var="descri" value="${gene:callFunction2('it.eldasoft.gene.tags.functions.GetDescriWsdmcftabFunction',  pageContext, param.idcftab)}" scope="request"/>
	
	<gene:setString name="titoloMaschera" value='Dettaglio tabellato ${descri}' />
	
	<gene:redefineInsert name="modelliPredisposti" />
	<gene:redefineInsert name="documentiAssociati" />
	<gene:redefineInsert name="noteAvvisi" />
		
	<gene:redefineInsert name="corpo">
		
		<gene:formScheda entita="WSDMTAB" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreWSDMTAB">	
			
			<input type="hidden" name="codice" value="${param.codice}" />
			<input type="hidden" name="codapp" value="${param.codapp}" />
			<input type="hidden" name="idconfi" value="${param.idconfi}" />
			<input type="hidden" name="idcftab" value="${param.idcftab}" />
			<input type="hidden" name="sistema" value="${param.sistema}" />
			
			
			<gene:campoScheda campo="ID" visibile="false"/>
			<gene:campoScheda campo="IDCFTAB" visibile="false" value="${param.idcftab}"/>
			<gene:campoScheda campo="IDCONFI" visibile="false" value="${param.idconfi}"/>
			<gene:campoScheda campo="CODICE" value="${param.codice}" modificabile="false"/>	
			<gene:campoScheda campo="VALORE" obbligatorio="true"/>	
			<gene:campoScheda campo="DESCRI" obbligatorio="true"/>
			<gene:campoScheda campo="NUMORD" />
			<gene:campoScheda campo="ISARCHI" />
			<gene:campoScheda campo="SISTEMA"  visibile="false"  value="${param.sistema}" modificabile="false"/>
			
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>

		</gene:formScheda>

		<gene:javaScript>
		
		var salvataggioOK = '${requestScope.salvataggioOK}';
		if(salvataggioOK != '' && salvataggioOK){
			historyVaiIndietroDi(1);
		}
		
		</gene:javaScript>
		
	</gene:redefineInsert>

</gene:template>
