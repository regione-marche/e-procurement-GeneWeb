
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

	<c:set var="idconfi" value="${param.idconfi}"/>

	<c:choose>
		<c:when test='${!empty param.modalita}'>
			<c:set var="modalita" value="${param.modalita}" />
		</c:when>
		<c:otherwise>
			<c:set var="modalita" value="${modalita}" />
		</c:otherwise>
	</c:choose>
	
	<gene:set name="icone">
		<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
	</gene:set>
	
	<table class="dettaglio-tab-lista">
		
		<c:if test="${modalita ne 'modifica'}">
			<tr>
				<td>
					<gene:formLista entita="WSDMCONFIUFF" where="WSDMCONFIUFF.IDCONFI = ${idconfi}" pagesize="0" sortColumn="0" tableclass="datilista" gestisciProtezioni="true">
						<span>
						<br>Nella pagina sono elencati gli uffici intestatari a cui è riferita la configurazione.
						Se non viene indicato nessun ufficio, la configurazione viene intesa come 'default' e riferita a tutti gli uffici intestatari 
						che non sono esplicitamente associati ad altre configurazioni.
						<br><br>
						</span>
						<gene:campoLista campo="CODEIN" entita="UFFINT" where="UFFINT.CODEIN = WSDMCONFIUFF.CODEIN"/>	
						<gene:campoLista campo="NOMEIN" entita="UFFINT" where="UFFINT.CODEIN = WSDMCONFIUFF.CODEIN"/>
						<gene:campoLista campo="CFEIN" entita="UFFINT" where="UFFINT.CODEIN = WSDMCONFIUFF.CODEIN"/>
					</gene:formLista>
				</td>
			</tr>	
		</c:if>
		
		<c:if test="${modalita eq 'modifica'}">
		
		<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />

			<tr>
				<td>
					<gene:formLista entita="UFFINT" where="" pagesize="0" sortColumn="0" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreListaUffintWsdm">
						<gene:campoLista campo="CODEIN" entita="WSDMCONFIUFF" visibile="false" where="WSDMCONFIUFF.CODEIN = UFFINT.CODEIN AND WSDMCONFIUFF.IDCONFI = ${idconfi}"/>	
						<gene:campoLista title="<center>Associato?<br>${icone}</center>" width="70" >
								<center>
								<input type="checkbox" name="keys" value="${datiRiga.UFFINT_CODEIN}" <c:if test="${not empty datiRiga.WSDMCONFIUFF_CODEIN}">checked="checked"</c:if> />
								</center>
						</gene:campoLista>
						<gene:campoLista campoFittizio="true" campo="ASSOCIATO" visibile="false" value="prova" definizione="T20"/>
						<gene:campoLista campo="CODEIN" entita="UFFINT" />	
						<gene:campoLista campo="NOMEIN" entita="UFFINT" />
						<gene:campoLista campo="CFEIN" entita="UFFINT" />
						<input type="hidden" name="idconfi" value="${param.idconfi}" >
						<input type="hidden" name="descri" value="${param.descri}" >
						<input type="hidden" name="codapp" value="${param.codapp}" >
						<input type="hidden" name="wsdmProtocollo" value="${param.wsdmProtocollo}"/>
						<input type="hidden" name="wsdmDocumentale" value="${param.wsdmDocumentale}"/>
						<input type="hidden" name="tabTabellatiAttiva" value="${param.tabTabellatiAttiva}"/>
					</gene:formLista>
				</td>
			</tr>	
		</c:if>
		
		
		<gene:redefineInsert name="listaNuovo" />
		<gene:redefineInsert name="listaEliminaSelezione" />
		<gene:redefineInsert name="addToAzioni" >
		<c:choose>
			<c:when test="${modalita eq 'modifica'}">
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:salvaAssociazioni()" title="Salva">
						</c:if>
							Salva
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:annullaModAssociazioni()" title="Annulla">
						</c:if>
							Annulla
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:when>	
			<c:otherwise>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:modificaAssociazioni()" title="Modifica associazioni">
						</c:if>
							Modifica associazioni
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:otherwise>			
		</c:choose>
		</gene:redefineInsert>
		
		<tr>
		<c:choose>
			<c:when test="${modalita ne 'modifica'}">
			<td class="comandi-dettaglio" colSpan="2">
				  <INPUT type="button" class="bottone-azione" value="Modifica associazioni" title="Modifica associazioni" onclick="javascript:modificaAssociazioni()"> &nbsp;
				&nbsp;
			</td>
			</c:when>
			<c:otherwise>
			<td class="comandi-dettaglio" colSpan="2">
				  <INPUT type="button" class="bottone-azione" value="Salva" title="Salva" onclick="javascript:salvaAssociazioni()"> 
				  <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModAssociazioni()">
				&nbsp;
			</td>
			</c:otherwise>
		</c:choose>
		</tr>
	</table>
		
	<script type="text/javascript">
		
		function modificaAssociazioni(){
		document.pagineForm.modalita.value="modifica"; 
		document.pagineForm.submit();
		}
		
		function annullaModAssociazioni(){
		document.pagineForm.modalita.value="visualizza"; 
		document.pagineForm.submit();
		}
		
		function salvaAssociazioni(){
			
			var cont = contaCheckSelezionati(document.forms[0].keys);
			var cont2 = document.forms[0].keys.length

			listaConferma();
		}
			
	</script>
	
