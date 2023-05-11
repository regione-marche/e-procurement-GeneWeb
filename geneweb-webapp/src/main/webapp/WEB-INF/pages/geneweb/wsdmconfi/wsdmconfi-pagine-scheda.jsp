
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="WSDMCONFI-pagine" schema="GENEWEB">

	<c:set var="entita" value="WSDMCONFI" />
	
	<c:set var="entitaParent" value='WSDMCONFI' scope="request" />
	
	<c:choose>
		<c:when test='${!empty param.idconfi}'>
			<c:set var="idconfi" value="${param.idconfi}" />
		</c:when>
		<c:otherwise>
			<c:set var="idconfi" value="${idconfi}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.descri}'>
			<c:set var="descri" value="${param.descri}" />
		</c:when>
		<c:otherwise>
			<c:set var="descri" value="${descri}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.codapp}'>
			<c:set var="codapp" value="${param.codapp}" />
		</c:when>
		<c:otherwise>
			<c:set var="codapp" value="${codapp}" />
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test='${!empty param.modalita}'>
			<c:set var="modalita" value="${param.modalita}" />
		</c:when>
		<c:otherwise>
			<c:set var="modalita" value="${modalita}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.wsdmProtocollo}'>
			<c:set var="wsdmProtocollo" value="${param.wsdmProtocollo}" />
		</c:when>
		<c:otherwise>
			<c:set var="wsdmProtocollo" value="${wsdmProtocollo}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.wsdmDocumentale}'>
			<c:set var="wsdmDocumentale" value="${param.wsdmDocumentale}" />
		</c:when>
		<c:otherwise>
			<c:set var="wsdmDocumentale" value="${wsdmDocumentale}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.tabTabellatiAttiva}'>
			<c:set var="tabTabellatiAttiva" value="${param.tabTabellatiAttiva}" />
		</c:when>
		<c:otherwise>
			<c:set var="tabTabellatiAttiva" value="${tabTabellatiAttiva}" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Dettaglio configurazione '${descri}'" />
	<gene:redefineInsert name="modelliPredisposti" />
	<gene:redefineInsert name="documentiAssociati" />
	<gene:redefineInsert name="noteAvvisi" />
	<script text="javascript">
	
	var innerHtmltab;

	function tabellatiDisable(){
		if(!innerHtmltab){
			innerHtmltab = $("#tabWSDM_TAB").html();
		}
		document.forms[0].tabTabellatiAttiva.value = "false";
		$("#tabWSDM_TAB").html("Dati Tabellati");
	}
	
	function tabellatiEnable(){
		document.forms[0].tabTabellatiAttiva.value = "true";
		if(innerHtmltab){
			innerHtmltab = $("#tabWSDM_TAB").html(innerHtmltab);
		}
	}
	
	</script>
	
	<gene:redefineInsert name="corpo">
			
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Dettaglio configurazione" idProtezioni="WSDM_DETT" selezionabile="${modalita ne 'modifica' or activePage eq '0'}">
			<%  
			//All'interno del dettaglio della configurazione valorizzare i campi wsdmProtocollo e wsdmDocumentale con il rispettivo tipo di sistema remoto, 
			//se non configurati disabilitare il tab "tabellati"
			%>
			
			<c:set var="warning" value="${gene:callFunction3('it.eldasoft.gene.tags.functions.IsDefaultConfigFunction',  pageContext, idconfi, codapp)}" scope="request"/>
			<table class="dettaglio-tab">
			<c:choose>
				<c:when test="${modalita ne 'modifica'}">
					<jsp:include page="..\wsdmconfipro\dettConfigWSDM.jsp">
						<jsp:param name="idconfi" value="${idconfi}"/>
						<jsp:param name="descri" value="${descri}"/>
						<jsp:param name="codapp" value="${codapp}"/>
					</jsp:include>
				</c:when>
				<c:otherwise>
					<jsp:include page="..\wsdmconfipro\modConfigWSDM.jsp">
						<jsp:param name="idconfi" value="${idconfi}"/>
						<jsp:param name="descri" value="${descri}"/>
						<jsp:param name="codapp" value="${codapp}"/>
					</jsp:include>
				</c:otherwise>
			</c:choose>
			</table>
			</gene:pagina>
			<gene:pagina title="Uffici intestatari" idProtezioni="WSDM_UFF">
				<jsp:include page="..\wsdmconfiuff\tabUffintWSDM.jsp">
					<jsp:param name="idconfi" value="${idconfi}"/>
					<jsp:param name="codapp" value="${codapp}"/>
					<jsp:param name="wsdmProtocollo" value="${wsdmProtocollo}"/>
					<jsp:param name="wsdmDocumentale" value="${wsdmDocumentale}"/>
					<jsp:param name="tabTabellatiAttiva" value="${tabTabellatiAttiva}"/>
				</jsp:include>
			</gene:pagina>			
			<gene:pagina title="Dati tabellati" idProtezioni="WSDM_TAB" selezionabile="${tabTabellatiAttiva ne 'false' or activePage eq '0'}">
				<jsp:include page="..\wsdmcftab\tabTabellatiWSDM.jsp">
					<jsp:param name="idconfi" value="${idconfi}"/>
					<jsp:param name="descri" value="${descri}"/>
					<jsp:param name="codapp" value="${codapp}"/>
					<jsp:param name="wsdmProtocollo" value="${wsdmProtocollo}"/>
					<jsp:param name="wsdmDocumentale" value="${wsdmDocumentale}"/>
				</jsp:include>
			</gene:pagina>	
			<%@ include file="wsdmconfi-altriTab.jsp" %>
			<input type="hidden" name="idconfi" value="${idconfi}"/>
			<input type="hidden" name="descri" value="${descri}"/>
			<input type="hidden" name="codapp" value="${codapp}"/>
			<input type="hidden" name="tabTabellatiAttiva" value="${tabTabellatiAttiva}"/>
			<input type="hidden" name="wsdmProtocollo" value="${wsdmProtocollo}"/>
			<input type="hidden" name="wsdmDocumentale" value="${wsdmDocumentale}"/>
			<input type="hidden" name="modalita" value="${modalita}"/>
		</gene:formPagine>
		
	</gene:redefineInsert>

</gene:template>
