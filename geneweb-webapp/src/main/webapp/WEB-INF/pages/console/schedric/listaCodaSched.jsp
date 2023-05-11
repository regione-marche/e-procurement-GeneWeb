<%
/*
 * Created on 03-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA GRUPPI 
 // CONTENENTE LA EFFETTIVA LISTA DEI GRUPPI E LE RELATIVE FUNZIONALITA' 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />


	<table class="lista">
		<tr>
			<td>
				<display:table name="listaCodaSched" id="codaSchedForm" class="datilista" requestURI="TrovaCodaSched.do" pagesize="${requestScope.risultatiPerPagina}" sort="list">
	 			    <display:column property="dataEsec" title="Data&nbsp;di&nbsp;esecuzione" sortable="true" headerClass="sortable" decorator="it.eldasoft.gene.commons.web.displaytag.DataOraDecorator"></display:column>
					<display:column property="nomeSchedRic" title="Schedulazione" sortable="true" headerClass="sortable" >  </display:column>
					<display:column title="Report" sortable="true" headerClass="sortable">
						<c:choose>
							<c:when test='${(! empty codaSchedForm.nomeFile )}'>
								<a href="DettaglioCodaSched.do?metodo=download&idCodaSched=${codaSchedForm.idCodaSched}">
									<c:out value='${codaSchedForm.nomeRicerca}'/>
								</a>
							</c:when>
							<c:otherwise>
								<c:out value='${codaSchedForm.nomeRicerca}'/>
							</c:otherwise>
						</c:choose>
					</display:column>
					<display:column property="descStato" title="Stato" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="msg" title="Messaggio" sortable="true" headerClass="sortable" >  </display:column>
					<display:column class="associadati" title="" style="width:18px">
						<c:if test='${(codaSchedForm.stato ne 0 )}'>
							<a href='javascript:elimina(${codaSchedForm.idCodaSched});' Title='Elimina'>
								<img src='${pageContext.request.contextPath}/img/trash.gif' height='15' width='15' alt='Elimina'>
							</a>
						</c:if>
	 			    </display:column>
				</display:table>
			</td>
		</tr>
		
	</table>

