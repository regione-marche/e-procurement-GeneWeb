<%
  /*
			 * Created on 25/03/2022
			 *
			 * Copyright (c) EldaSoft S.p.A.
			 * Tutti i diritti sono riservati.
			 *
			 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
			 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
			 * aver prima formalizzato un accordo specifico con EldaSoft.
			 */
%>


<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<gene:template file="scheda-template.jsp">
	<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
	<c:set var="codimp" value="${param.codimp}" />
	<c:set var="status_service" value="${param.status_service}" />
	
	<c:set var="art80statuslist" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "art80.statuslist")}'/>
	<c:set var="art80monitoring" value="true" />
	<c:set var="art80one_shot" value="true" />
	
	<c:if test="${!empty art80statuslist}">
		<c:choose>
			<c:when test="${fn:contains(art80statuslist,'monitoring')}">
				<c:set var="art80monitoring" value="true" />
			</c:when>
			<c:otherwise>
				<c:set var="art80monitoring" value="false" />
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${fn:contains(art80statuslist,'one_shot')}">
				<c:set var="art80one_shot" value="true" />
			</c:when>
			<c:otherwise>
				<c:set var="art80one_shot" value="false" />
			</c:otherwise>
		</c:choose>
	</c:if>

	<gene:setString name="titoloMaschera" value="Art.80 - aggiornamento verifica per l'operatore economico" />

	<gene:redefineInsert name="corpo">
		<form action="${contextPath}/Art80SetServiceOE.do" method="post" name="formArt80SetServiceOE" >
			<input type="hidden" name="codimp" value="${codimp}" />
			<table class="dettaglio-notab">
				<tr>
					<td colspan="2" style="padding: 0 5 0 5;">
						<br>
						<br>
						Richiesta di aggiornamento delle verifiche Art. 80 per la tipologia di controllo "One shot".
						<br>
						<br>
						E' possibile modificare anche la tipologia di controllo da "One shot" a "Monitoraggio".
						<br>
						<br>
						Tipologia di controllo (*): 
						<select name="status_service" id="status_service" >
							<option value="" title="" >&nbsp;</option>
							<c:if test="${art80one_shot eq 'true'}">
								<option value="one_shot" title="One shot" selected="selected" >One shot</option>
							</c:if>
							<c:if test="${art80monitoring eq 'true'}">
								<option value="monitoring" title="Monitoraggio" >Monitoraggio</option>
							</c:if>
						</select>
						<br>
						<br>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Invia richiesta di aggiornamento" title="Invia richiesta di aggiornamento" onclick="javascript:inviadati();">
						<INPUT type="button" class="bottone-azione" value="Indietro" title="Indietro" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>
	<gene:redefineInsert name="addToAzioni" >
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:inviadati();" title="Invia dati" tabindex="1503">Invia richiesta</a>
			</td>
		</tr>
	</gene:redefineInsert>

	<gene:javaScript>

		function inviadati(){
		    var status_service = $("#status_service").val();
		    
		    if (status_service == "") {
		    	alert("Selezionare la tipologia di controllo")
		    } else {
				bloccaRichiesteServer();
				setTimeout("document.formArt80SetServiceOE.submit()", 250);
			}
		}

	</gene:javaScript>
</gene:template>


