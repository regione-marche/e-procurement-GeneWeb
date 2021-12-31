<%    /*
       * Created on 19-ott-2007
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

/************************************************************************************
	Aggiunta delle sezioni collegate 1 a 1 con l'entita chiamante

	input (param):
		entitaParent 				Nome dell'entita di partenza.
		joinWhere					Where da aggiungere alla join per la selezione dall'entità principale
		joinFrom					Altre tabelle per la join con l'entità principale
*************************************************************************************/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="lWhere" value="" />
<c:set var="lFrom" value="" />
<c:if test="${not empty param.joinWhere}">
	<c:set var="lWhere" value="${param.joinWhere} and " />
	<c:if test="${not empty param.joinFrom}">
		<c:set var="lFrom" value="${param.joinFrom}" />
	</c:if>
</c:if>

<gene:callFunction obj="it.eldasoft.gene.tags.functions.GestioneAttributiFunction" parametro="${param.entitaParent}" />

<c:set var="dynEntSchema" value="DYNENT_SCHEMA_${param.entitaParent}" scope="page"/>
<c:set var="dynEntName" value="DYNENT_NAME_${param.entitaParent}" scope="page"/>
<c:set var="dynEntPgName" value="DYNENT_PGNAME_${param.entitaParent}" scope="page"/>
<c:set var="dynEntDesc" value="DYNENT_DESC_${param.entitaParent}" scope="page"/>

<c:set var="elencoCampi" value="elencoCampi_${param.entitaParent}" scope="page"/>
<c:set var="campiChiavi" value="campiChiavi_${param.entitaParent}" scope="page"/>
<c:set var="whereKeys" value="whereKeys_${param.entitaParent}" scope="page" />
<c:set var="campiCondizioni" value="campiCondizioni_${param.entitaParent}" scope="page"/>
<c:set var="funzioniCalcoliScheda" value="funzioniCalcoliScheda_${param.entitaParent}" scope="page"/>
<c:set var="campiCollegatiTitoli" value="campiCollegatiTitoli_${param.entitaParent}" scope="page"/>
<c:set var="funzioniCampiCollegatiTitoli" value="funzioniCampiCollegatiTitoli_${param.entitaParent}" scope="page"/>

<c:set var="visSezione" value="true"/>
<c:set var="editSezione" value=""/>
<c:if test="${requestScope.gestisciProtezioniGenAttributi}">
	<c:set var="testVisualizzaSezione" value="SEZ.VIS.${requestScope['SCHEMA_REQUEST']}.${requestScope['ID_MASCHERA_REQUEST']}"/>
	<c:set var="testEditSezione" value="SEZ.MOD.${requestScope['SCHEMA_REQUEST']}.${requestScope['ID_MASCHERA_REQUEST']}"/>
	<c:if test="${! empty requestScope['ID_PAGINA_REQUEST']}">
		<c:set var="testVisualizzaSezione" value="${testVisualizzaSezione}.${requestScope['ID_PAGINA_REQUEST']}"/>
		<c:set var="testEditSezione" value="${testEditSezione}.${requestScope['ID_PAGINA_REQUEST']}"/>
	</c:if>
	<c:set var="testVisualizzaSezione" value="${testVisualizzaSezione}.X${param.entitaParent}"/>
	<c:set var="testEditSezione" value="${testEditSezione}.X${param.entitaParent}"/>
	<c:set var="visSezione" value="${gene:checkProt(pageContext, testVisualizzaSezione)}"/>
	<c:set var="editSezione" value="${gene:checkProt(pageContext, testEditSezione)}"/>
</c:if>

<c:set var="titolo" value="${requestScope[dynEntPgName]}"/>
<c:if test="${empty titolo}">
<c:set var="titolo" value="${requestScope[dynEntDesc]}"/>
</c:if>

<c:set var="sezioneAttributiGenericiVisibile"  value="${!empty requestScope[dynEntName] && visSezione}" scope="request"/>

<c:if test="${requestScope.sezioneAttributiGenericiVisibile}">
			<gene:campoScheda>
				<td colspan="2"><b>${titolo}</b></td>
			</gene:campoScheda>
			<c:forEach items="${requestScope[elencoCampi]}" var="campo">
				<c:choose>
				<c:when test="${campo.titolo}">
					<c:set var="visTitolo" value="false"/>
					<c:set var="isRegolaVisualizzazioneTitolo" value="true"/>
					<c:set var="funzRegoleVisualizzazioneTitolo" value=""/>
					<c:set var="campiRegoleVisualizzazioneTitolo" value=""/>

					<c:if test="${! empty requestScope[campiCollegatiTitoli][campo.nome]}">
						<c:set var="nomiCampiCollegati" value="${fn:split(requestScope[campiCollegatiTitoli][campo.nome], ';')}"/> 
						<c:forEach items="${nomiCampiCollegati}" var="campoCollegato">
							<c:if test="${requestScope.gestisciProtezioniGenAttributi}">
								<c:set var="testVisualizza" value="COLS.VIS.${requestScope[dynEntSchema]}.${requestScope[dynEntName]}.${campoCollegato}"/>
								<c:set var="valutazioneTestVisualizza" value="${gene:checkProt(pageContext, testVisualizza)}"/>
							</c:if>
							<c:if test="${! requestScope.gestisciProtezioniGenAttributi}">
							<c:set var="valutazioneTestVisualizza" value="false"/>
								<c:forEach items="${requestScope[elencoCampi]}" var="sottoCampo">
									<c:if test="${sottoCampo.nome eq campoCollegato}">
										<c:set var="valutazioneTestVisualizza" value="${sottoCampo.visScheda}"/>
									</c:if>
								</c:forEach>
							</c:if>
							<c:if test="${valutazioneTestVisualizza}">
								<c:set var="visTitolo" value="true"/>
								<c:choose>
									<c:when test="${! empty requestScope[funzioniCampiCollegatiTitoli][campoCollegato] && isRegolaVisualizzazioneTitolo}">
										<c:if test="${fn:length(funzRegoleVisualizzazioneTitolo) > 0}">
											<c:set var="funzRegoleVisualizzazioneTitolo" value="${funzRegoleVisualizzazioneTitolo}||"/>									
										</c:if>
										<c:set var="funzRegoleVisualizzazioneTitolo" value="${funzRegoleVisualizzazioneTitolo}${fn:split(requestScope[funzioniCampiCollegatiTitoli][campoCollegato],';')[0]}"/>
										<c:if test="${!fn:contains(campiRegoleVisualizzazioneTitolo, fn:split(requestScope[funzioniCampiCollegatiTitoli][campoCollegato],';')[1])}">
											<c:set var="campiRegoleVisualizzazioneTitolo" value="${campiRegoleVisualizzazioneTitolo}${fn:split(requestScope[funzioniCampiCollegatiTitoli][campoCollegato],';')[1]};"/>
										</c:if>
									</c:when>
									<c:otherwise>
										<c:set var="isRegolaVisualizzazioneTitolo" value="false"/>
										<c:set var="funzRegoleVisualizzazioneTitolo" value=""/>
									</c:otherwise>
								</c:choose> 				
							</c:if>
						</c:forEach>
						<c:if test="${fn:length(funzRegoleVisualizzazioneTitolo) > 0}">
							<gene:fnJavaScriptScheda funzione='showObj("row${campo.nome}", ${funzRegoleVisualizzazioneTitolo})' elencocampi='${campiRegoleVisualizzazioneTitolo}' esegui='true' />
						</c:if>
					</c:if>
					<gene:campoScheda visibile="${visTitolo}" nome="${campo.nome}">
					<td colspan="2"><b>${campo.descr}</b></td>
					</gene:campoScheda>
				</c:when>
				<c:otherwise>
					<c:if test="${requestScope.gestisciProtezioniGenAttributi}">
					<gene:campoScheda title="${campo.descr}" entita="${requestScope[dynEntName]}" campo="${campo.nome}" visibile="${not campo.chiave}" modificabile="${editSezione}" where="${lWhere}${requestScope[whereKeys]}" from="${lFrom}" />
					</c:if>
					<c:if test="${! requestScope.gestisciProtezioniGenAttributi}">
					<gene:campoScheda title="${campo.descr}" entita="${requestScope[dynEntName]}" campo="${campo.nome}" visibile="${(not campo.chiave) && campo.visScheda}" modificabile="${campo.modScheda}" where="${lWhere}${requestScope[whereKeys]}" from="${lFrom}" gestisciProtezioni="false" />
					</c:if>
				</c:otherwise>
				</c:choose>
			</c:forEach>
			<c:forEach items="${requestScope[campiCondizioni]}" var="cmp" >
				<gene:campoScheda entita="${cmp.ent}" campo="${cmp.nome}" visibile="false" where="${requestScope[whereKeys]}"/>
			</c:forEach>
	
	<c:forEach items="${requestScope[funzioniCalcoliScheda]}" var="funzione" >
		<gene:fnJavaScriptScheda funzione='${fn:split(funzione,";")[0]}' elencocampi='${fn:split(funzione,";")[1]}' esegui='true' />
	</c:forEach>
<jsp:include page="js-attributi-generici.jsp" />
</c:if>