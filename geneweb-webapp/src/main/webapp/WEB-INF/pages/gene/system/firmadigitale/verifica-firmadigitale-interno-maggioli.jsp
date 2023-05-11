<%
/*
 * Created on: 16/06/2016
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>


	<c:set var="contextPath" value="${pageContext.request.contextPath}" />
	<c:set var="risposta" value="" />
	
	<gene:redefineInsert name="head" >
		<link rel="stylesheet" href="${contextPath}/css/jquery/treeview/jquery.treeview.css">
		<script type="text/javascript" src="${contextPath}/js/jquery.cookie.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.treeview.js"></script>
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value='Verifica firma digitale' />
	<gene:redefineInsert name="documentiAzioni" />

	<gene:redefineInsert name="corpo">
		<c:set var="idprg" value="${param.idprg}" />
		<c:set var="iddocdig" value="${param.iddocdig}" />
		<c:set var="opreload" value="${param.opreload}" />
		<c:set var="ckdate" value="${param.ckdate}" />
		
		<c:set var="result" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.GetSbustamentoFileFirmatoFunction",pageContext,idprg,iddocdig,ckdate)}'/>



		<table class="dettaglio-notab">
			<c:choose>
				<c:when test="${state eq 'NO-DATA-FOUND'}">
					<tr>
						<td colspan="2" style="padding-right: 25px; padding-left: 5px; padding-top: 15px; padding-bottom: 15px; color: #FF0000">
							<br>
							<b>Il documento non &egrave; presente in banca dati</b>
							<br>
							<br>	
						</td>
					</tr>
				</c:when>
				<c:when test="${state eq 'DATE-PARSE-EXCEPTION'}">
					<tr>
						<td colspan="2" style="padding-right: 25px; padding-left: 5px; padding-top: 15px; padding-bottom: 15px; color: #FF0000">
							<br>
							<b>La data indicata per il controllo di attendibilit&agrave; dei certificati non rispetta il formato previsto (yyyyMMdd HH:mm:ss)</b>
							<br>
							<br>	
						</td>
					</tr>
					<tr>
						<td colspan="2"><b><br>Download dei documenti<b></td>
					</tr>
					<c:choose>
						<c:when test="${!empty dignomdoc_tsd}">
							<c:set var="estensione" value="tsd"/>
							<c:set var="nomeDoc" value="${dignomdoc_tsd}"/>
						</c:when>
						<c:when test="${!empty dignomdoc_p7m}">
							<c:set var="estensione" value="p7m"/>
							<c:set var="nomeDoc" value="${dignomdoc_p7m}"/>
						</c:when>
					</c:choose>
					<tr>
						<td class="etichetta-dato">Documento</td>
						<td class="valore-dato">
							<a href="javascript:downloadDocumentoFirmato('${idprg}', '${iddocdig}', '${estensione }');">
								${nomeDoc}	
							</a>
						</td>
					</tr>
				</c:when>
				<c:when test="${state eq 'ERROR'}">
					<tr>
						<td colspan="2" style="padding-right: 25px; padding-left: 5px; padding-top: 15px; padding-bottom: 15px; color: #FF0000">
							<br>
							<b>${message}</b>
							<br>
							<br>	
						</td>
					</tr>
					<tr>
						<td colspan="2"><b><br>Download dei documenti<b></td>
					</tr>
					<c:choose>
						<c:when test="${!empty dignomdoc_tsd}">
							<c:set var="estensione" value="tsd"/>
							<c:set var="nomeDoc" value="${dignomdoc_tsd}"/>
						</c:when>
						<c:when test="${!empty dignomdoc_p7m}">
							<c:set var="estensione" value="p7m"/>
							<c:set var="nomeDoc" value="${dignomdoc_p7m}"/>
						</c:when>
					</c:choose>
					<tr>
						<td class="etichetta-dato">Documento</td>
						<td class="valore-dato">
							<a href="javascript:downloadDocumentoFirmato('${idprg}', '${iddocdig}', '${estensione }');">
								${nomeDoc}	
							</a>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
	
					<tr>
						<td colspan="1"><b><br>Download dei documenti<b></td>
						<td>
							<a id="linkVerificaFirmaDigitale" style="float:right;" href="javascript:verificaFirmaDigitale('${opreload}','${idprg}','${iddocdig}');">
								<img src="/Appalti/img/firmaRemota.png" title="Verifica firma digitale del documento" alt="Verifica firma digitale del documento" width="16" height="16">
								<span title="Verifica firma digitale del documento">Verifica firma digitale</span>
							</a>
						</td>
					</tr>
					<c:if test="${!empty dignomdoc_tsd}">
						<tr>
							<td class="etichetta-dato">Documento marcato</td>
							<td class="valore-dato">
								<a href="javascript:downloadDocumentoFirmato('${idprg}', '${iddocdig}', 'tsd');">
									${dignomdoc_tsd}	
								</a>
							</td>
						</tr>
					</c:if>
					<c:if test="${!empty dignomdoc_p7m}">
						<tr>
							<td class="etichetta-dato">Documento firmato</td>
							<td class="valore-dato">
								<a href="javascript:downloadDocumentoFirmato('${idprg}', '${iddocdig}', 'p7m');">
									${dignomdoc_p7m}	
								</a>
								
							</td>
						</tr>
					</c:if>
					<tr>
						<td class="etichetta-dato">Documento contenuto</td>
						<td class="valore-dato">
							<a href="javascript:downloadDocumentoFirmato('${idprg}', '${iddocdig}', 'doc');">
								${dignomdoc_doc}
							</a>
						</td>
					</tr>
					<tr id="trVerifica">
						<td colspan="2" id="tdVerifica"></td>
					</tr>
					
				</c:otherwise>
			</c:choose>
			<tr class="comandi-dettaglio">
				<td class="comandi-dettaglio" colspan="2">
					<c:choose>
						<c:when test='${param.jspParent eq "scheda"}'>
							<input type="button" value="Indietro" title="Indietro" class="bottone-azione" onclick="javascript:historyVaiIndietroDi(1);"/>
						</c:when>
						<c:when test='${param.jspParent eq "popUp"}'>
							<input type="button" class="bottone-azione" value='Esci' title='Esci' onclick="javascript:window.close();">
						</c:when>
					</c:choose>
					&nbsp;
				</td>
			</tr>
		</table>
		<div id="loading">
			<div id="avanzamento"><div id="avanzamento-label"></div></div>
		</div>
	</gene:redefineInsert>

	<gene:javaScript>
	
		$("#trVerifica").hide();
		
		
		$("#gray").treeview({
			animated: "slow",
			control: "#treecontrol"
		});
	
		$("#grayTimeStamp").treeview({
			animated: "slow",
			control: "#treecontrolTimeStamp"
		});
		
		function downloadDocumentoFirmato(idprg, iddocdig, type) {
			var href = "${pageContext.request.contextPath}/DownloadDocumentoFirmato.do";
			document.location.href = href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&type=" + type;
		}
		function verificaFirmaDigitale(opreload, idprg, iddocdig) {
			_wait();
			$.ajax({
					type: "POST",
					dataType: "text",
					url: "VerificaFirmaServizioEsterno.do",
					data : {
						idprg: idprg,
						iddocdig: iddocdig
					},
					success: function(res) {
						if (res) {
							
							$('#tdVerifica').append(res);
							
							$("#trVerifica").show();
							
							$("#gray").treeview({
								animated: "slow",
								control: "#treecontrol"
							});
						
							$("#grayTimeStamp").treeview({
								animated: "slow",
								control: "#treecontrolTimeStamp"
							});
							
							$("#linkVerificaFirmaDigitale").hide();
						}else{
							
							$("#trVerifica").hide();
						}
					},
					error: function(e) {
						alert('error');
					},
					complete: function() {
						_nowait();
						if(opreload=='1'){
							window.opener.historyReloadWithoutPopUps();
						}
					}
				});
		}
		
		function _wait() {
			document.getElementById('bloccaScreen').style.visibility = 'visible';
			$('#bloccaScreen').css("width", $(document).width());
			$('#bloccaScreen').css("height", $(document).height());
			document.getElementById('wait').style.visibility = 'visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200 });
		}

		function _nowait() {
			document.getElementById('bloccaScreen').style.visibility = 'hidden';
			document.getElementById('wait').style.visibility = 'hidden';
		}		
		
		
	</gene:javaScript>



