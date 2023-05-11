
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="WSDMCONFI-scheda" schema="GENEWEB">
	
	<c:set var="entitaParent" value='WSDMCONFI' scope="request" />
	<c:set var="idconfi" value='${param.idconfi}' scope="request" />
	
	<gene:setString name="titoloMaschera" value='Dettaglio configurazione WSDM' />
	
	<gene:redefineInsert name="corpo">
		
				<gene:formScheda entita="WSDMCONFI" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreWSDMCONFI">
					
					<gene:campoScheda campo="ID" visibile="false"/>
					<gene:campoScheda campo="CODAPP" visibile="false" value="PG"/>
					<gene:campoScheda campo="DESCRI" obbligatorio="true"/>		
					
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<gene:insert name="addPulsanti"/>
							<c:choose>
							<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
								<gene:insert name="pulsanteSalva">
									<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
								</gene:insert>
								<gene:insert name="pulsanteAnnulla">
									<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
								</gene:insert>

							</c:when>
							<c:otherwise>
								<gene:insert name="pulsanteModifica">
									<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
										<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
									</c:if>
								</gene:insert>
							</c:otherwise>
							</c:choose>
							&nbsp;
						</td>
					</gene:campoScheda>
				</gene:formScheda>
		
	</gene:redefineInsert>
	
	<gene:redefineInsert name="modelliPredisposti" />
	<gene:redefineInsert name="documentiAssociati" />
	<gene:redefineInsert name="noteAvvisi" />
	<gene:redefineInsert name="schedaNuovo"/>
	<gene:redefineInsert name="pulsanteNuovo"/>
	

	
	
	<c:if test="${requestScope.salvataggioOK eq 'true'}">
	<gene:javaScript>
		
		document.formListaConfipro.idconfi.value=$("#WSDMCONFI_ID").val();
		document.formListaConfipro.descri.value=$("#WSDMCONFI_DESCRI").val();
		bloccaRichiesteServer();
		document.formListaConfipro.submit();
		
	</gene:javaScript>
	
	<form name="formListaConfipro" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="geneweb/wsdmconfi/wsdmconfi-pagine-scheda.jsp" />
		<input type="hidden" name="entita" value="WSDMCONFI" />
		<input type="hidden" name="idconfi" value="" /> 
		<input type="hidden" name="descri" value="" /> 
		<input type="hidden" name="codapp" value="${codapp}" /> 
		<input type="hidden" name="tabAttivo" value="datigen" /> 
		<input type="hidden" name="risultatiPerPagina" value="20" />
		<jsp:include page="/WEB-INF/pages/commons/csrf.jsp" />
	</form>
	</c:if>
</gene:template>
