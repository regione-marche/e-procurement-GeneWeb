<%
  /*
			 * Created on 09/04/2018
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

	<gene:redefineInsert name="head" >
		<style type="text/css">
			TABLE.art80 {
				margin: 0;
				margin-top: 5px;
				margin-bottom: 5px;
				padding: 0px;
				width: 100%;
				font-size: 11px;
				border-collapse: collapse;
				border-left: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
			}

			TABLE.art80 TR.tipodocumento {
				background-color: #EFEFEF;
			}
			
			TABLE.art80 TR.tipodocumento TD {
				padding: 6 6 6 6;
				font-weight: bold;
				height: 30px;
			}
		
			TABLE.art80 TR.intestazione {
				background-color: #FFFFFF;
			}
			
			TABLE.art80 TR.intestazione TD {
				text-align: center;
				height: 30px;
			}
		
			TABLE.art80 TR.vuoto {
				background-color: #FFFFFF;
			}
			
			TABLE.art80 TR.vuoto TD{
				padding: 5 0 5 0;
				border-right-style: hidden;
    			border-left-style: hidden;
			}
		
			TABLE.art80 TR {
				background-color: #FFFFFF;
			}
			
			TABLE.art80 TR TD.stato {
				text-align: center;
			}
		
			TABLE.art80 TR TD, TABLE.art80 TR TH {
				padding-top: 2px;
				padding-bottom: 2px;
				padding-right: 6px;
				padding-left: 6px;
				text-align: left;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 22px;
			}
			
		</style>
	</gene:redefineInsert>

	<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
	<c:set var="codimp" value="${param.codimp}" />
	<c:set var="codein" value="${sessionScope.uffint}" />

	<gene:setString name="titoloMaschera" value="Art.80 - dettaglio dei documenti dell'operatore economico ${codimp}"  />
	<c:set var="r" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.Art80ConsultaFunction",pageContext,codimp,codein)}'/>
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
			<c:choose>
				<c:when test="${!empty error}">
					<tr>
						<td colspan="2">
							<div style="color:red; padding: 10 5 10 5; font-size: 12px;">ERRORE: ${error}</div>
						</td>
					</tr>	
				</c:when>
				<c:when test="${id eq 0}">
					<tr>
						<td colspan="2"><br><b>Operatore economico non trovato</b><br><br></td>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="2"><b>Dati generali</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Ragione sociale o denominazione</td>
						<td class="valore-dato">${ragione_sociale}</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Stato documentale</td>
						<td class="valore-dato">${stato_art_80_descrizione}</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Codice fiscale</td>
						<td class="valore-dato">${cf}</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Partita I.V.A.</td>
						<td class="valore-dato"><c:if test="${iva ne 'null'}">${iva}</c:if></td>
					</tr>
					
					<tr>
						<td colspan="2">
							<br>
							<table id="documents" class="art80" width="100%">
								<tbody>
									<c:forEach items="${documents}" step="1" var="doc" varStatus="statusdocument" >
										<tr class="tipodocumento">
											<td colspan="2">
												<b>
												<c:choose>
													<c:when test="${doc[1] eq 1}">
														1. Attestazione ottemperanza legge 68/99 (Art. 80 - Comma 5.I)
													</c:when>
													<c:when test="${doc[1] eq 2}">
														2. Certificato dell'Anagrafe delle sanzioni amministrative dipendenti da reato
													</c:when>										
													<c:when test="${doc[1] eq 3}">
														3. Certificati del Casellario Giudiziale per i soggetti in carica
													</c:when>										
													<c:when test="${doc[1] eq 4}">
														4. Certificati del Casellario Giudiziale per i soggetti cessati nell'anno precedente
													</c:when>
													<c:when test="${doc[1] eq 5}">
														5. Certificazione Antimafia
													</c:when>										
													<c:when test="${doc[1] eq 6}">
														6. Certificazione Regolarit&agrave; Contributiva (DURC INPS)
													</c:when>	
													<c:when test="${doc[1] eq 7}">
														7. Attestazione di regolarit&agrave; fiscale, Art. 80 - Comma 4
													</c:when>
													<c:when test="${doc[1] eq 8}">
														8. Estratto casellario ANAC
													</c:when>										
													<c:when test="${doc[1] eq 9}">
														9. Visura Camerale
													</c:when>
													<c:when test="${doc[1] eq 10}">
														10. Carichi Pendenti
													</c:when>	
													<c:when test="${doc[1] eq 11}">
														11. Certificazione Regolarit&agrave; Contributiva (Cassa Previdenziale di Categoria)
													</c:when>
												</c:choose>
												</b>
											</td>
										</tr>
										
										<tr>
											<td>Stato</td>
											<td>
												<c:choose>
													<c:when test="${doc[2] eq '3'}">
														Non applicabile
													</c:when>
													<c:when test="${doc[2] eq '2'}">
														Nessuna anomalia
													</c:when>
													<c:when test="${doc[2] eq '1'}">
														In lavorazione
													</c:when>
													<c:when test="${doc[2] eq '0'}">
														Riscontrate anomalie
													</c:when>
													<c:otherwise>
														-
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
										
										<tr>
											<td>Richieste</td>
											<td>		
												<c:if test="${empty doc[4]}">-</c:if>								
												<c:forEach items="${doc[4]}" step="1" var="path" varStatus="statuspath">
													<div style="padding-top: 3px; padding-bottom: 3px;">
														<a href='javascript:downloadart80("${path[0]}")' class="link-generico" title="Consulta il documento">${path[0]}</a>
													</div>
												</c:forEach>											
											</td>
										<tr>
											
										<c:choose>
											<c:when test="${doc[1] ge 3 && doc[1] le 4}">
												<tr>
													<td>Documenti positivi</td>
													<td>	
														<c:if test="${empty doc[5]}">-</c:if>
														<c:forEach items="${doc[5]}" step="1" var="path" varStatus="statuspath">
															<div style="padding-top: 3px; padding-bottom: 3px;">
																<a href='javascript:downloadart80("${path[0]}")' class="link-generico" title="Consulta il documento">${path[0]}</a>
															</div>
														</c:forEach>											
													</td>
												</tr>
												<tr>
													<td>Documenti negativi</td>
													<td>
														<c:if test="${empty doc[6]}">-</c:if>
														<c:forEach items="${doc[6]}" step="1" var="path" varStatus="statuspath">
															<div style="padding-top: 3px; padding-bottom: 3px;">
																<a href='javascript:downloadart80("${path[0]}")' class="link-generico" title="Consulta il documento">${path[0]}</a>
															</div>
														</c:forEach>											
													</td>
												</tr>	
											</c:when>
											<c:otherwise>
												<tr>
													<td>Documenti</td>
													<td colspan="2">	
														<c:if test="${empty doc[5]}">-</c:if>
														<c:forEach items="${doc[5]}" step="1" var="path" varStatus="statuspath">
															<div style="padding-top: 3px; padding-bottom: 3px;">
																<a href='javascript:downloadart80("${path[0]}")' class="link-generico" title="Consulta il documento">${path[0]}</a>
															</div>
														</c:forEach>											
													</td>
												</tr>
											</c:otherwise>
										</c:choose>

										<tr>
											<td>Data prima richiesta</td>
											<td>
												<c:choose>
													<c:when test="${empty doc[11]}">
														-
													</c:when>
													<c:otherwise>
														${doc[11]}
													</c:otherwise>
												</c:choose>		
											</td>
										<tr>

										<tr>
											<td>Data ultima richiesta</td>
											<td>
												<c:choose>
													<c:when test="${empty doc[12]}">
														-
													</c:when>
													<c:otherwise>
														${doc[12]}
													</c:otherwise>
												</c:choose>		
											</td>
										<tr>
										
										<tr>
											<td>Data scadenza</td>
											<td>
												<c:choose>
													<c:when test="${empty doc[9]}">
														-
													</c:when>
													<c:otherwise>
														${doc[9]}
													</c:otherwise>
												</c:choose>		
											</td>
										<tr>

										<tr>
											<td>Note</td>
											<td>
												<c:choose>
													<c:when test="${empty doc[13]}">
														-
													</c:when>
													<c:otherwise>
														${doc[13]}
													</c:otherwise>
												</c:choose>		
											</td>
										<tr>

										<tr class="vuoto">
											<td colspan="4">&nbsp;</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
			
			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<INPUT type="button" class="bottone-azione" value="Indietro" title="Indietro" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>
	<gene:redefineInsert name="addToAzioni" />

	<gene:javaScript>

		function downloadart80(path) {
			var link
			$.ajax({
				type: "POST",
				dataType: "json",
				async: false,
				timeout: 3000,
				beforeSend: function(x) {
					if(x && x.overrideMimeType) {
						x.overrideMimeType("application/json;charset=UTF-8");
					}
				},
				url: "Art80Download.do",
				data: {
					"path": path
				},
				success: function(data){
					link = data.link;
				}
			});
			
			if (link != null && link != "") {
				window.location.href=link;
			}
					
		}

	</gene:javaScript>

</gene:template>


