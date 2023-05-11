<%
	/*
	 * Created on 11-Gen-2011
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

<%-- chiave fittizia per bypassare il fatto che si apre la scheda da un link --%>
<c:set var="key" value="W_LOGEVENTI.IDEVENTO=N:0" scope="request"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="CONTATORE-ACCESSI" schema="GENE">
	<gene:setString name="titoloMaschera" value='Statistiche Accessi' />
	<gene:redefineInsert name="corpo">
	
	<gene:formScheda entita="W_LOGEVENTI" plugin="it.eldasoft.gene.tags.gestori.plugin.GestoreStatisticaAccessi" tableClass="dettaglio-notab-width-50" >
		
		<%--campo inserito per bypassare il fatto che si apre la scheda da un link --%>
		<gene:campoScheda entita="W_LOGEVENTI" campo="IDEVENTO" visibile="false"/>

		<c:forEach items="${datiStatistiche}" var="datiAnno" varStatus="stato">	
		
			<gene:campoScheda>			
				<c:forEach items="${datiAnno[0]}" var="intestazione" varStatus="stato2">
					<c:choose>
						<c:when test="${stato2.first}">
							<td class="etichetta-dato"><b>Profilo Accesso</b></td>
						</c:when>
						<c:when test="${stato2.last}">
							<td class="etichetta-dato" width="80"><b>Totale ${intestazione}</b></td>
						</c:when>
						<c:otherwise>
							<td class="etichetta-dato"><b>${intestazione}</b></td>
						</c:otherwise>
					</c:choose>					
				</c:forEach>
			</gene:campoScheda>
			
			<c:forEach items="${datiAnno[1]}" var="profili" varStatus="stato3">		
				<gene:campoScheda>
					<c:forEach items="${profili}" var="datoProfilo" varStatus="stato4">
						<c:choose>
							<c:when test="${stato4.first}">
								<td class="etichetta-dato">${datoProfilo}</td>
							</c:when>
							<c:otherwise>
								<td align="center">
									<c:choose>
										<c:when test="${datoProfilo eq '0'}">-</c:when>
										<c:otherwise>${datoProfilo}</c:otherwise>
									</c:choose>
								</td>
							</c:otherwise>
						</c:choose>						
					</c:forEach>
				</gene:campoScheda>
			</c:forEach>
			
			<gene:campoScheda><td colspan="14">&nbsp;<td></gene:campoScheda>		
			
		</c:forEach>
		
		<gene:redefineInsert name="schedaNuovo" />
		<gene:redefineInsert name="schedaModifica" />
	
	</gene:formScheda>
		
	</gene:redefineInsert>
</gene:template>		