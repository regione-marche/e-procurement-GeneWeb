
<%
	/*
	 * Created on 09-mar-2016
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
	
	<c:choose>
		<c:when test='${!empty param.modalita}'>
			<c:set var="modalita" value="${param.modalita}" />
		</c:when>
		<c:otherwise>
			<c:set var="modalita" value="${modalita}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${not empty param.wsdmProtocollo && not empty param.wsdmDocumentale}">
			<c:set var="where" value=" (sistema = ? or sistema = ?)"/>
			<c:set var="parametri" value="T:${param.wsdmProtocollo};T:${param.wsdmDocumentale}"/>
		</c:when>
		<c:when test="${not empty param.wsdmDocumentale}">
			<c:set var="where" value=" sistema = ? "/>
			<c:set var="parametri" value="T:${param.wsdmDocumentale}"/>
		</c:when>
		<c:otherwise>
			<c:set var="where" value=" sistema = ? "/>
			<c:set var="parametri" value="T:${param.wsdmProtocollo}"/>
		</c:otherwise>
	</c:choose>
	<c:if test="${not empty where and not empty sessionScope.moduloAttivo}">
		<c:set var="where" value="${where} and codapp = ?"/>
		<c:set var="parametri" value="${parametri};T:${sessionScope.moduloAttivo}"/>
	</c:if>
	
	<c:set var="where" value="${where} and (isarchi!='1' or isarchi is null)"/>
	
	${gene:callFunction4("it.eldasoft.gene.tags.functions.ImpostazioneFiltroFunction", pageContext, "WSDMCFTAB", where, parametri)}
	
	<table class="dettaglio-tab">
	
		<tr>
			<td>
			<gene:formLista entita="WSDMCFTAB" distinct="true" pagesize="20" sortColumn="2" tableclass="datilista" gestisciProtezioni="true" >
				<gene:campoLista title="Opzioni" width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza dettaglio" href="javascript:apriDettaglio('${datiRiga.WSDMCFTAB_CODICE}','${datiRiga.WSDMCFTAB_ID}','${datiRiga.WSDMCFTAB_SISTEMA}');"/>
						</gene:PopUp>
					</c:if>
				</gene:campoLista>
				<gene:campoLista campo="ID" visibile="false"/>
				<gene:campoLista campo="CODICE" href="javascript:apriDettaglio('${datiRiga.WSDMCFTAB_CODICE}','${datiRiga.WSDMCFTAB_ID}','${datiRiga.WSDMCFTAB_SISTEMA}');"/>
				<gene:campoLista campo="DESCRI"/>
				<gene:campoLista campo="SISTEMA"/>
				<gene:campoLista title="N. valori" campo="COUNT" campoFittizio="true" definizione="T10;0;;;" width="80">
					<c:set var="countTab" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetCountTabellatiFunction", pageContext, datiRiga.WSDMCFTAB_ID, param.idconfi)}'/>
					${countTab}
				</gene:campoLista>
				<gene:campoLista campo="CODAPP" visibile="false"/>
				<input type="hidden" name="idconfi" value="${param.idconfi}" >
				<input type="hidden" name="wsdmProtocollo" value="${param.wsdmProtocollo}"/>
				<input type="hidden" name="wsdmDocumentale" value="${param.wsdmDocumentale}"/>
						
			</gene:formLista>
			</td>
		</tr>	
		
		<form name="listaTAB" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="" /> 
			<input type="hidden" name="codice" value="" />
			<input type="hidden" name="idcftab" value="" />
			<input type="hidden" name="idconfi" value="${param.idconfi}" >
			<input type="hidden" name="metodo" value="apri" />
			<input type="hidden" name="sistema" value="" />
		</form>
		
		<gene:redefineInsert name="listaNuovo" />
		<gene:redefineInsert name="listaEliminaSelezione" />
		
	</table>
	<script type="text/javascript">
		
		$('table.datilista > thead > tr').find('th:eq(1)').css('width','150px');
		$('table.datilista > tbody > tr').find('td:eq(1)').css('width','150px');
		$('table.datilista > thead > tr').find('th:eq(3)').css('width','100px');
		$('table.datilista > tbody > tr').find('td:eq(3)').css('width','100px');
		
		function apriDettaglio(codice,id,sistema) {
			document.listaTAB.href.value = "/geneweb/wsdmtab/wsdmtab-lista.jsp";
			document.listaTAB.idcftab.value = id;
			document.listaTAB.codice.value = codice;
			document.listaTAB.sistema.value = sistema;
			document.listaTAB.submit();
		}
		
	</script>
	
	<style type="text/css">
	
	#titCOUNT{
		width:80px;
	}
	</style>