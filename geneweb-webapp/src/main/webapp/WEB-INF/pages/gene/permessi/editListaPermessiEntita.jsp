<%
/*
 * Created on 23-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA DEGLI 
 // UTENTI ASSOCIATI ALL'ENTITA IN ANALISI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<html:form action="/SalvaPermessiEntita" >
	<table class="lista">
	  <tr>
	  	<td>
				<display:table name="listaPermessiEntitaUtenti" defaultsort="3" id="permessoEntitaForm" class="datilista" sort="list" requestURI="ListaPermessiEntita.do">
					<display:column title="<center>Condiviso<br><a href='javascript:selezionaTutteEntita(document.permessiAccountEntitaForm.associa);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutteEntita(document.permessiAccountEntitaForm.associa);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
					<c:choose>
						<c:when test='${not empty permessoEntitaForm.idPermesso}'>
							<input type="hidden" name="idPermesso" value="${permessoEntitaForm.idPermesso}"/>
							<input type="hidden" name="condividiEntita" id="condividiEntita${permessoEntitaForm_rowNum -1}" value="1" />
						</c:when>
						<c:otherwise>
							<input type="hidden" name="idPermesso" value="0" />
							<input type="hidden" name="condividiEntita" id="condividiEntita${permessoEntitaForm_rowNum -1}" value="0"/>
						</c:otherwise>
					</c:choose>
						<input type="checkbox" name="associa" id="associa${permessoEntitaForm_rowNum -1}" value="${permessoEntitaForm_rowNum -1}" <c:if test='${not empty permessoEntitaForm.idPermesso}'>checked="checked"</c:if> onclick="javascript:controlloRiga(${permessoEntitaForm_rowNum - 1});"/>
					</display:column>
					<display:column property="idAccount" title="Codice" sortable="true" headerClass="sortable"> </display:column>
					<display:column property="nome" title="Descrizione" sortable="true" headerClass="sortable"> </display:column>
					<display:column property="login" title="Nome" sortable="true" headerClass="sortable"> </display:column>
					<display:column title="Autorizzazione" sortable="false" headerClass="sortable">
					<c:choose>
						<c:when test='${not empty permessoEntitaForm.autorizzazione or fn:length(permessoEntitaForm.autorizzazione) > 0}'>
							<input type="hidden" name="autorizzazione" id="autorizzazione${permessoEntitaForm_rowNum -1}" value="${permessoEntitaForm.autorizzazione}" />
						</c:when>
						<c:otherwise>
							<input type="hidden" name="autorizzazione" id="autorizzazione${permessoEntitaForm_rowNum -1}" value="0" />
						</c:otherwise>
					</c:choose>
						<input type="hidden" name="idAccount" value="${permessoEntitaForm.idAccount}" />
						<select id="autoriz${permessoEntitaForm_rowNum - 1}" <c:if test='${empty permessoEntitaForm.autorizzazione}'>disabled="true"</c:if> onchange="javascript:setAutorizzazione(${permessoEntitaForm_rowNum - 1});" >
							<c:forEach items="${listaValueAutorizzazioni}" varStatus="indice" >
								<option value="${listaValueAutorizzazioni[indice.index]}" <c:if test='${listaValueAutorizzazioni[indice.index] eq permessoEntitaForm.autorizzazione}'>selected="selected"</c:if>>${listaTextAutorizzazioni[indice.index]}</option>
							</c:forEach>
						</select>
					</display:column>
					<display:column title="Proprietario" sortable="false" >
					<c:choose>
						<c:when test='${not empty permessoEntitaForm.proprietario}'>
							<input type="hidden" name="proprietario"  id="proprietario${permessoEntitaForm_rowNum -1}" value="${permessoEntitaForm.proprietario}" />
						</c:when>
						<c:otherwise>
							<input type="hidden" name="proprietario" id="proprietario${permessoEntitaForm_rowNum -1}" value="2" />
						</c:otherwise>
					</c:choose>
						<input type="checkbox" id="propr${permessoEntitaForm_rowNum - 1}" <c:if test='${permessoEntitaForm.proprietario eq 1}'>checked="checked"</c:if> <c:if test='${empty permessoEntitaForm.proprietario}'>disabled="true"</c:if> onchange="javascript:setProprietario(${permessoEntitaForm_rowNum - 1});"/>
					</display:column>
					<%@ include file="/WEB-INF/pages/gene/permessi/editcampiSpecifici.jsp" %>
					
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	    	<input type="hidden" name="campoChiave" value="${campoChiave}" />
	    	<input type="hidden" name="valoreChiave" value="${valoreChiave}" />
	    	<input type="hidden" name="genereGara" value="${genereGara}" />
	    	<INPUT type="button" class="bottone-azione" value="Salva" 	title="Salva modifiche" 	onclick="javascript:salvaModifiche();">
		    <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annullaModifiche();" >
		    &nbsp;
			</td>
	  </tr>
	</table>
</html:form>
<script type="text/javascript">
<!--

init();

-->
</script>