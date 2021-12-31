<%
/*
 * Created on 26-set-2006
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
 // ESTRAZIONE DELLA RICERCA STESSA (DURANTE LA CREAZIONE/MODIFICA DELLA 
 // RICERCA DALL'AREA APPLICATIVA 'GENERATORE RICERCHE').
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="listaParametri" value="${sessionScope.recordDettRicerca.elencoParametri}" scope="request" />

<html:form action="/SalvaParametriRicerca" >
	<table class="ricerca">	
	
	<c:if test='${requestScope.kronos eq 1}'>
	  <tr>
      <td class="etichetta-dato">
      	Data inizio validit&agrave; (*)
      </td>
      <td class="valore-dato">
					<input type="text" name="KRDATINVAL" value="${requestScope['KRDATINVAL']}" class="data">
					&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
      </td>
    </tr>
	  <tr>
      <td class="etichetta-dato">
      	Data fine validit&agrave; (*)
      </td>
      <td class="valore-dato">
					<input type="text" name="KRDATFINVAL" value="${requestScope['KRDATFINVAL']}" class="data">
					&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
      </td>
    </tr>
	</c:if>
	
	<c:set var="indice" value="0" />
	<c:forEach items="${listaParametri}" var="parametro" varStatus="ciclo">
		<c:set var="tipoPar" value="${fn:trim(parametro.tipoParametro)}" />
	  <tr>
      <td class="etichetta-dato">
      	${fn:trim(parametro.nome)} (*)
      </td>
      <td class="valore-dato">
				<c:if test='${tipoPar eq "D"}'>
					<c:choose>
						<c:when test='${fn:length(listaParametri) gt 1}' >
							<c:set var="nomeArrayPar" value="parametriRicerca[${ciclo.index}]" />
						</c:when>	
						<c:otherwise>
							<c:set var="nomeArrayPar" value="parametriRicerca" />
						</c:otherwise>
					</c:choose>
					<input type="text" name="parametriRicerca" value="${listaValori[ciclo.index]}" class="data">
					&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
					<c:remove var="nomeArrayPar" />
				</c:if>
				<c:if test='${tipoPar eq "T"}'>
					<c:if test='${!empty parametro.tabCod}'>
					<select name="parametriRicerca" >
						<option value="">&nbsp;</option>
						<c:set var="listaTabellato" value="${fn:trim(listaListeTabellati[indice])}"/>
						<c:set var="arrayCodVal" value="${fn:split(listaTabellato, '_')}" />
						<c:forEach items="${arrayCodVal}" varStatus="j" step="2">
							<c:choose>
								<c:when test='${arrayCodVal[j.index] eq listaValori[ciclo.index]}' >
									<option value="${arrayCodVal[j.index]}" selected="selected">${arrayCodVal[j.index+1]}</option>
								</c:when>
								<c:otherwise>
									<option value="${arrayCodVal[j.index]}" >${arrayCodVal[j.index+1]}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>

					<c:set var="indice" value="${indice+1}" />
					</c:if>
					<c:if test='${empty parametro.tabCod}'>
						<c:if test='${requestScope.kronos eq 1}'>
						<c:set var="listaValoriVarUtente" value="listaValori${parametro.codiceParametro}" />
						<c:choose>
						<c:when test="${fn:length(requestScope[listaValoriVarUtente]) == 1}">
					<input type="hidden" name="parametriRicerca" value="${requestScope[listaValoriVarUtente][0].tipoTabellato}">
						</c:when>
						<c:otherwise>
					<select name="parametriRicerca" >
						<option value="">&nbsp;</option>
						<c:forEach items="${requestScope[listaValoriVarUtente]}" var="opzione" varStatus="j">
						<option value="${opzione.tipoTabellato}" <c:if test="${listaValori[ciclo.index] eq opzione.tipoTabellato}">selected="selected"</c:if>>${opzione.descTabellato}</option>
						</c:forEach>
					</select>
						</c:otherwise>
						</c:choose>
						</c:if>
					</c:if>
				</c:if>
				<c:if test='${tipoPar eq "I"}'>
					<input type="text" name="parametriRicerca" value="${listaValori[ciclo.index]}"><span class="formatoParametro">&nbsp;(NNN)</span>
				</c:if>
				<c:if test='${tipoPar eq "F"}'>
					<input type="text" name="parametriRicerca" value="${listaValori[ciclo.index]}"><span class="formatoParametro">&nbsp;(NNN,NN)</span>
				</c:if>
				<c:if test='${tipoPar eq "S"}'>
					<input type="text" name="parametriRicerca" value="${listaValori[ciclo.index]}">
				</c:if>
				<c:if test='${tipoPar eq "UC"}'>
					<c:out value="${sessionScope.profiloUtente.nome} (Id = ${sessionScope.profiloUtente.id})" />
					<input type="hidden" name="parametriRicerca" value="${sessionScope.profiloUtente.id}" >
				</c:if>
				<c:if test='${tipoPar eq "UI"}'>
					<c:out value="${sessionScope.nomeUffint} (${sessionScope.uffint})" />
					<input type="hidden" name="parametriRicerca" value="${sessionScope.uffint}" >
				</c:if>
				<c:if test="${!empty parametro.descrizione}">
					<div class="note">
						<c:out value="${parametro.descrizione} "  escapeXml="false"/>
					</div>
				</c:if>
			</td>
		</tr>
	</c:forEach>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	    	<html:hidden property="numParametri" value="${fn:length(listaParametri)}"/>
	    	<html:hidden property="metodo" value="salvaParametriRicerca"/>
	      <INPUT type="button" id="pulsante" class="bottone-azione" value="Esegui report" title="Esegui report" onclick="javascript:eseguiRicerca();" >
	      <INPUT type="button" class="bottone-azione" value="Reimposta" title="Reimposta" onclick="javascript:svuotaInput();" >
		<c:choose>
			<c:when test='${! empty fromPage && fromPage eq "listaRicerche"}'>
				<INPUT type="button" class="bottone-azione" value="Torna a lista report" title="Torna a lista report" onclick="javascript:vaiListaRicerche();" >
			</c:when>
			<c:otherwise>
	      <INPUT type="button" class="bottone-azione" value="Torna a dettaglio" title="Torna a dettaglio report" onclick="javascript:vaiDettaglioRicerca();" >
      </c:otherwise>
	  </c:choose>
	      &nbsp;
	    </td>
	  </tr>
	</table>
</html:form>
