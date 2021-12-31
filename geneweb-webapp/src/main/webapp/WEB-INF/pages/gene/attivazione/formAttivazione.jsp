<%
	/*
	 * Created on 08-apr-2016
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>



<html>
	<head>
		<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>
		
		<script type="text/javascript">
			<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />
			
			function controllaEmail(unCampoDiInput,stringaCampo) {
				var esito = false;
				
				if (unCampoDiInput.value != "" && !isFormatoEmailValido(unCampoDiInput.value)) {
					alert("L'indirizzo mail del soggetto responsabile per il cliente non e' sintatticamente valido.");
					if(ie4){
			  			unCampoDiInput.select();
		  				unCampoDiInput.focus();
		  			} else {
		  			<% // Si e' dovuto differenziare il javascript per un bug
		  			   // presente in Firefox 2.0 relativo all'esecuzione delle funzioni 
		  			   // focus() e select() su un oggetto dopo all'evento onblur	del 
		  			   // oggetto stesso %>
			 				setTimeout(stringaCampo + ".select()",125);
		 					setTimeout(stringaCampo + ".focus()",125);
		  			}
		  		} else
		  			esito = true;
		  			
		  		return esito;
			}
			
			function attiva(){
				if ($("#codiceCliente").val() != "" && $("acquirenteSW").val() != "" && $("#responsabileCliente").val() != "" && $("#responsabileClienteEmail").val() != "" && $("#accettazioneLicenza").is(":checked")) {
					bloccaRichiesteServer();
			   		document.formAttivazione.submit();
				} else {
					alert("Per procedere è necessario valorizzare tutte le informazioni richieste ed accettare le condizioni d'uso");
				} 
			}
			
			$(window).on("load", function (){
				
				setOpzioniDisponibili();
				
				function setOpzioniDisponibili() {
					var opzioniSelezionate = $('[name^="opzioneSelezionabile_"]:checked');
					var opzioniDisponibili = "";
					opzioniSelezionate.each(function(o) {
						var opz = $(this).val();
						if (opzioniDisponibili != "") opzioniDisponibili += "|";
						opzioniDisponibili += opz;
					});
					$("#opzioniDisponibili").val(opzioniDisponibili);
				}
				
				$('[name^="opzioneSelezionabile_"]').on("change",
					function() {
						setOpzioniDisponibili();
					}
				);
				
			});
			
		</script>
	</head>
	<jsp:include page="/WEB-INF/pages/commons/bloccaRichieste.jsp" />
	<body onload="setVariables();checkLocation();">
		<table class="arealayout">
			<colgroup width="150px"></colgroup>
			<colgroup width="*"></colgroup>
			<tbody>
				<tr class="testata">
					<td colspan="2">
						<jsp:include page="/WEB-INF/pages/commons/testata.jsp"/>
					</td>
				</tr>
				<tr class="menuprincipale">
					<td>
						<img src="${pageContext.request.contextPath}/img/spacer-azionicontesto.gif" alt="">
					</td>
					<td>
		                &nbsp;			
			      	</td>
			    </tr>
				<tr>
					<td class="menuazioni" valign="top">
				    	&nbsp;
				    </td>
					<td class="arealavoro">
						<div class="contenitore-arealavoro">
							<div class="titolomaschera">
								<br>
								Attivazione applicativo
								<br>
								<br>
							</div>
							<div class="contenitore-dettaglio">
								<form id="formAttivazione" name="formAttivazione" method="post" action="${pageContext.request.contextPath}/AttivaApplicazione.do" >
									<table class="dettaglio-notab">
										<tbody>
											<tr>
												<td colspan="2">
													<br>
													Per utilizzare l'applicativo &egrave; necessario effettuare l'attivazione inserendo tutte le informazioni richieste.
													<br>
													<br>			
													<b>Informazioni cliente</b>
												</td>
											</tr>
											
											<tr>
												<td class="etichetta-dato">
													Codice cliente (*)
												</td>
												<td class="valore-dato">
													<input type="text" id="codiceCliente" name="codiceCliente" size="20" value="${codiceCliente}"/>
												</td>
											</tr>
											
											<tr>
												<td class="etichetta-dato">
													Denominazione (*)
												</td>
												<td class="valore-dato">
													<input type="text" id="acquirenteSW" name="acquirenteSW" size="70" value="${acquirenteSW}"/>
												</td>
											</tr>
											
											<tr>
												<td class="etichetta-dato">
													Nominativo del soggetto responsabile per il cliente (*)
												</td>
												<td class="valore-dato">
													<textarea id="responsabileCliente" name="responsabileCliente" style="width: 560px; height: 50px;">${responsabileCliente}</textarea>
												</td>
											</tr>
											
											<tr>
												<td class="etichetta-dato">
													Indirizzo mail del soggetto responsabile per il cliente (*)
												</td>
												<td class="valore-dato">
													<input type="text" id="responsabileClienteEmail" name="responsabileClienteEmail" size="70" value="${responsabileClienteEmail}" onblur="javascript:controllaEmail(this,'document.formAttivazione.responsabileClienteEmail');"/>
												</td>
											</tr>

											<tr>
												<td colspan="2">
													<br>
													<b>Informazioni prodotto e lista opzioni disponibili</b>
												</td>
											</tr>
											<tr>
												<td class="etichetta-dato">
													Prodotto
												</td>
												<td class="valore-dato">
													${prodotto} ${versione}
												</td>
											</tr>
											
											<tr>
												<td class="etichetta-dato">
													Opzioni
												</td>
												<td class="valore-dato">
													<c:choose>
														<c:when test="${empty listaOpzioniSelezionabili}">
															Non ci sono opzioni selezionabili
														</c:when>
														<c:otherwise>
															<c:forEach items="${listaOpzioniSelezionabili}" step="1" var="opzioneSelezionabile" varStatus="status" >
																<c:choose>
																	<c:when test="${opzioneSelezionabile[2] eq 'true'}">
																		<input type="checkbox" checked id="opzioneSelezionabile_${opzioneSelezionabile[0]}" 
																			name="opzioneSelezionabile_${opzioneSelezionabile[0]}"
																			value="${opzioneSelezionabile[0]}"/>${opzioneSelezionabile[1]} [${opzioneSelezionabile[0]}]
																	</c:when>
																	<c:otherwise>
																		<input type="checkbox" id="opzioneSelezionabile_${opzioneSelezionabile[0]}" 
																			name="opzioneSelezionabile_${opzioneSelezionabile[0]}"
																			value="${opzioneSelezionabile[0]}"/>${opzioneSelezionabile[1]} [${opzioneSelezionabile[0]}]
																	</c:otherwise>
																</c:choose>
																<br>
															</c:forEach>
														</c:otherwise>
													</c:choose>
													<input type="hidden" id="opzioniDisponibili" name="opzioniDisponibili" size="70" />
												</td>
											</tr>
											
											<tr>
												<td colspan="2" style="border-bottom: 0px;">
													<br>
													<div><b>Condizioni d'uso</b></div>
													<div style="margin-top: 3px; margin-bottom: 6px;"><textarea readonly style="width: 800px; height: 200px; padding: 5px;">${licenza}</textarea></div>
												</td>
											</tr>
											
											<tr>
												<td colspan="2" class="valore-dato" style="padding-left: 0px;">
													Dichiaro di aver preso visione e di accettare le condizioni sopra descritte (*) <input type="checkbox" id="accettazioneLicenza" name="accettazioneLicenza" />
												</td>
											</tr>
											
											<tr>
												<td colspan="2" class="comandi-dettaglio">
													<input type="button" class="bottone-azione" value="Attiva applicativo" title="Attiva applicativo" onclick="javascript:attiva();" />&nbsp;&nbsp;
												</td>
											</tr>
											
										</tbody>																
									</table>
								</form>							
							</div>
						</div>
					</td>
				</tr>	
			</tbody>
		</table>
	</body>
</html>