<%
/*
 * Created on: 31-mar-2017
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* configurazione codifica automatica */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_CONFCOD-scheda")}'/>
<c:set var="tipoSelezionato" value="${gene:if(empty param.TIPOSEL,1,param.TIPOSEL)}"/>

<c:set var="moduloAttivo" value="${sessionScope.moduloAttivo}" scope="request"/>
<c:set var="nomeModulo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.titolo")}'/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_CONFCOD-lista" >
	
    <gene:setString name="titoloMaschera"  value="Configurazione codifica automatica" />
	<c:choose>
	    <c:when test="${param.TIPOSEL == 2}">
	       <c:choose>
			    <c:when test="${moduloAttivo == 'PG'}">
			        <c:set var="filtroCodapp" value="'PG','G1'"/>
			    </c:when>
			    <c:when test="${moduloAttivo == 'PL'}">
			        <c:set var="filtroCodapp" value="'PL','G2'"/>
			    </c:when>
				<c:otherwise>
					<c:set var="filtroCodapp" value="'${sessionScope.moduloAttivo}'"/>
				</c:otherwise>
			</c:choose>
	    </c:when>
	    <c:otherwise>
	        <c:set var="filtroCodapp" value="'G_'"/>
	    </c:otherwise>
	</c:choose>

	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
		<tr>
			<td>
				<c:forEach var="i" begin="1" end="2">
					<input type="radio" name="tipologia" value="${i}" onclick="cambiaTipoCodifica('${i}');" <c:if test="${tipoSelezionato == i}">checked="checked"</c:if>>
					<c:choose>
					    <c:when test="${i == 1}">
					       <c:out value="Archivi"></c:out>
					       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					    </c:when>
					    <c:otherwise>
					        <c:out value="${nomeModulo}"></c:out>
					        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					    </c:otherwise>
					</c:choose>
					
				</c:forEach>
			</td>
		</tr>
		<tr>
			<td>
			<gene:formLista entita="G_CONFCOD" sortColumn="2" pagesize="20" tableclass="datilista"
			gestisciProtezioni="true" where="G_CONFCOD.CODAPP in (${filtroCodapp})" > 
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">					
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_CONFCOD-scheda")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza"/>
							</c:if>
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_CONFCOD-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica"/>
							</c:if>
					</gene:PopUp>
				</gene:campoLista>
				
				<% // Campi veri e propri %>
				<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_CONFCOD-scheda")}'/>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="NUMORD" visibile="false"/>
				<gene:campoLista campo="DESCAM" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" width="300"/>
				<gene:campoLista campo="CHKCODIFICAATTIVA" headerClass="sortable" title="Codifica automatica attiva?" campoFittizio="true" value="${gene:if(empty datiRiga.G_CONFCOD_CODCAL,0,1)}" definizione="T1;;;SN;" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" width="200"/> 
				<gene:campoLista campo="CODCAL" headerClass="sortable" />
				<gene:campoLista campo="CONTAT" headerClass="sortable" />
				<input type="hidden" id="TIPOSEL" name="TIPOSEL" value="${tipoSelezionato}" />
			</gene:formLista>
		</td></tr>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<gene:insert name="addPulsanti"/>
				<gene:insert name="pulsanteListaInserisci"/>
				&nbsp;
			</td>
		</tr>
		</table>
  </gene:redefineInsert>

	<gene:javaScript>
	function cambiaTipoCodifica(tipo) {
		document.location.href="ApriPagina.do?"+csrfToken+"&href=gene/g_confcod/g_confcod-lista.jsp?TIPOSEL="+tipo;
	}
  </gene:javaScript>
</gene:template>
