<%
/*
 * Created on 19-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA 
 // ARGOMENTI CONTENENTE IL FORM PER IL SETTING DEI PARAMETRI IN FASE DI 
 // ESTRAZIONE DELLA RICERCA CON PROSPETTO (DURANTE LA CREAZIONE/MODIFICA DELLA 
 // RICERCA DALL'AREA APPLICATIVA 'GENERATORE RICERCHE').
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="arrayParametriRicerca" value="${sessionScope.recordDettParametriPerEstrazione}"/>

<html:form action="/SalvaParametriProspetto" >
	<table class="ricerca">	
	<c:set var="indice" value="0" />
	<c:forEach items="${listaParametri}" var="parametro" varStatus="ciclo">
		<c:set var="tipoPar" value="${fn:trim(parametro.tipo)}" />
		<c:choose>
			<c:when test='${tipoPar eq "U"}'>
				<input type="hidden" name="parametriModello" id="parametro${ciclo.index}" value="${idAccount}">
			</c:when>
			<c:otherwise>
			  <tr>
		      <td class="etichetta-dato">
		      	${fn:trim(parametro.nome)}<c:if test="${fn:trim(parametro.obbligatorio eq 1)}"> (*)</c:if>
		      </td>
		      <td class="valore-dato">
						<c:if test='${tipoPar eq "D"}'>
							<c:choose>
								<c:when test='${fn:length(listaParametri) gt 1}' >
									<c:set var="nomeArrayPar" value="parametriModello[${ciclo.index}]" />
								</c:when>
								<c:otherwise>
									<c:set var="nomeArrayPar" value="parametriModello" />
								</c:otherwise>
							</c:choose>
							<input type="text" name="parametriModello" id="parametro${ciclo.index}" value="${listaValori[ciclo.index]}" class="data">
							&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
						</c:if>
						<c:if test='${tipoPar eq "M"}'>
							<select name="parametriModello" id="parametro${ciclo.index}">
								<option value="">&nbsp;</option>
								<c:set var="arrayCodVal" value="${fn:split(parametro.menu, '|')}" />
								<c:forEach items="${arrayCodVal}" var="opzione" varStatus="j">
									<option value="${(j.index+1)}" <c:if test="${listaValori[ciclo.index] eq (j.index+1)}">selected="selected"</c:if>>${opzione}</option>
								</c:forEach>
							</select>
						</c:if>
						<c:if test='${tipoPar eq "I"}'>
							<input type="text" name="parametriModello" id="parametro${ciclo.index}" value="${listaValori[ciclo.index]}"><span class="formatoParametro">&nbsp;(NNN)</span>
						</c:if>
						<c:if test='${tipoPar eq "F"}'>
							<input type="text" name="parametriModello" id="parametro${ciclo.index}" value="${listaValori[ciclo.index]}"><span class="formatoParametro">&nbsp;(NNN,NN)</span>
						</c:if>
						<c:if test='${tipoPar eq "S"}'>
							<input type="text" name="parametriModello" id="parametro${ciclo.index}" value="${listaValori[ciclo.index]}">
						</c:if>
						<c:if test='${tipoPar eq "N"}'>
							<textarea name="parametriModello" id="parametro${ciclo.index}" rows="8" cols="64" >${listaValori[ciclo.index]}</textarea>
						</c:if>
						<c:if test='${tipoPar eq "T"}'>
							<html:select property="parametriModello" styleId="parametro${ciclo.index}">
								<option value="">&nbsp;</option>
								<c:set var="listatab" value="lista${parametro.tabellato}"/>
								<c:forEach items="${requestScope[listatab]}" var="opzione" varStatus="j">
								<option value="${opzione.tipoTabellato}" <c:if test="${listaValori[ciclo.index] eq opzione.tipoTabellato}">selected="selected"</c:if>>${opzione.descTabellato}</option>
								</c:forEach>
							</html:select>
						</c:if>
						<c:if test="${!empty parametro.descrizione}">
							<div class="note">
								<c:out value="${parametro.descrizione} "  escapeXml="false"/>
							</div>
						</c:if>

					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</c:forEach>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
   			<INPUT type="button" class="bottone-azione" value="Esegui report" title="Esegui estrazione report" onclick="javascript:gestisciSubmit();" >
		<c:choose>
			<c:when test='${fn:containsIgnoreCase(parametriProspettoForm.paginaSorgente, "dettaglioRicerca")}' >
				<INPUT type="button" class="bottone-azione" value="Torna a dettaglio" title="Torna a dettaglio report" onclick="javascript:vaiDettaglioRicerca(1);" >
			</c:when>
			<c:otherwise>
				<INPUT type="button" class="bottone-azione" value="Torna a a lista" title="Torna a a lista report" onclick="javascript:vaiDettaglioRicerca(1);" >
			</c:otherwise>
		</c:choose>
	      &nbsp;
	    </td>
	  </tr>
	</table>

	<input type="hidden" name="metodo" value="salvaEComponiModello"/> 
	<html:hidden name="parametriProspettoForm" property="idModello"/>
	<html:hidden name="parametriProspettoForm" property="nomeModello"/> 
	<html:hidden name="parametriProspettoForm" property="entita"/> 
	<html:hidden name="parametriProspettoForm" property="nomeChiavi"/> 
	<html:hidden name="parametriProspettoForm" property="valori"/>
	<html:hidden name="parametriProspettoForm" property="idProspetto"/>
	<c:forEach items="${parametriProspettoForm.valChiavi}" var="chiave">
		<input type="hidden" name="valChiavi" value="${chiave}"/>
	</c:forEach>
	<html:hidden name="parametriProspettoForm" property="noFiltroEntitaPrincipale"/>
	<html:hidden name="parametriProspettoForm" property="fileComposto"/>
	<html:hidden name="parametriProspettoForm" property="paginaSorgente"/>
</html:form>

<script type="text/javascript">
<!--
	$('textarea[name="parametriModello"]').bind('input propertychange', function() {checkInputLength( $(this)[0], 512)});
-->
</script>  
