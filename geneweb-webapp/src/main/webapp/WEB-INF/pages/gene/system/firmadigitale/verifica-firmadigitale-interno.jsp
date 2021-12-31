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
		<c:set var="ckdate" value="${param.ckdate}" />
		
		<c:set var="result" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.GetVerificaFirmaDigitaleFunction",pageContext,idprg,iddocdig,ckdate)}'/>

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
					<x:parse var="xverificaFirmaDigitale" xml="${verificaFirmaDigitaleXML}" />
					<x:if select='count($xverificaFirmaDigitale) > 0'>
					
						<c:if test="${!empty ckdateformat}">
							<tr>
								<td colspan="2"><b><br>Attendibilit&agrave; dei certificati</b></td>
							</tr>
							<tr>
								<td class="etichetta-dato">Lista dei certificati non attendibili alla data del ${ckdateformat}</td>
								<td class="valore-dato">
									<x:choose>
										<x:when select="count($xverificaFirmaDigitale//Certificate[CheckAtDate = 'false']) > 0">
											<x:forEach var="Certificate" select="$xverificaFirmaDigitale//Certificate" varStatus="certificatestatus">
												<x:if select="$Certificate/CheckAtDate = 'false'">
													<span>
														#<x:out select="$Certificate/@Id"/>
														<x:if select="$Certificate/SubjectDN/SURNAME != ''">
															<x:out select="$Certificate/SubjectDN/SURNAME"/>
														</x:if>
														<x:if select="$Certificate/SubjectDN/GIVENNAME != ''">
															<x:out select="$Certificate/SubjectDN/GIVENNAME"/>
														</x:if>
													</span>
													<br>								
												</x:if>										
											</x:forEach>
										</x:when>
										<x:otherwise>
											Tutti i certificati sono attendibili
										</x:otherwise>
									</x:choose>
								</td>
							</tr>	
						</c:if>
					
						<tr>
							<td colspan="2"><b><br>Download dei documenti<b></td>
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
					
						<tr>
							<td colspan="2">
								<b><br>Lista delle firme</b>
								<span id="treecontrol" style="float: right;">
									<a style="text-decoration: none; color:#404040; title="Chiudi tutto" href="#"><img src="${contextPath}/css/jquery/treeview/images/minus.gif" /> Chiudi tutto</a>&nbsp;&nbsp;
									<a style="text-decoration: none; color:#404040; title="Espandi tutto" href="#"><img src="${contextPath}/css/jquery/treeview/images/plus.gif" /> Espandi tutto</a>
								</span>
							</td>
						</tr>
						
						<tr>
							<td colspan="2">
								<ul id="gray" class="treeview-gray">
								<span>
									<c:set var="livelloOld" value="0"/>
									<c:set var="livelli" value="0"/>
								</span>
									<x:forEach var="Certificate" select="$xverificaFirmaDigitale//Certificate" varStatus="certificatestatus">
										<x:set var="livelloNew" select="string($Certificate/Level)"/>
										<c:if test="${livelloOld != livelloNew}">
											<li>
											<c:if test="${not fn:contains(livelloNew, '.')}">
												<span>
													<b>Livello <x:out select = "$Certificate/Level" /></b>
												</span>
											</c:if>
											<x:set var="livelloOld" select="string($Certificate/Level)"/>
											<c:set var="livelli" value="${livelli + 1}"/>
											<ul>
										</c:if>
										<li class="closed">
											<span>
												#<x:out select="$Certificate/@Id"/>
												<x:if select="$Certificate/SubjectDN/SURNAME != ''">
													<x:out select="$Certificate/SubjectDN/SURNAME"/>
												</x:if>
												<x:if select="$Certificate/SubjectDN/GIVENNAME != ''">
													<x:out select="$Certificate/SubjectDN/GIVENNAME"/>
												</x:if>
												<x:if select="$Certificate/SubjectDN/SERIALNUMBER != ''">
													(<x:out select="substring($Certificate/SubjectDN/SERIALNUMBER,4)"/>)
												</x:if>
												<span style="float: right; border-bottom: 1px dotted #555555;">
													<x:if select="$Certificate/Valid = 'false'">
														[Certificato non valido/integro]
													</x:if>
													<x:if select="$Certificate/CheckAtDate = 'false'">
														[Certificato non attendibile]
													</x:if>
													<x:if select="$Certificate/NonRepudiation = 'false'">
														[Certificato privo di validit&agrave; legale]
													</x:if>												
												</span>
											</span>
												
											<ul>
												<table class="dettaglio-notab" style="width:99%; margin-left:5px; margin-top:5px; margin-bottom:20px;">
													<tr>
														<td colspan="2"><i>Dati del firmatario<i></td>
													</tr>
													<x:if select="$Certificate/SubjectDN/T != ''">
														<tr>
															<td class="etichetta-dato">Titolo</td>
															<td class="valore-dato"><x:out select="$Certificate/SubjectDN/T"/></td>
														</tr>
													</x:if>								
													
													<x:if select="$Certificate/SubjectDN/SURNAME != ''">
														<tr>
															<td class="etichetta-dato">Cognome</td>
															<td class="valore-dato"><x:out select="$Certificate/SubjectDN/SURNAME"/></td>
														</tr>
													</x:if>
													
													<x:if select="$Certificate/SubjectDN/GIVENNAME != ''">
														<tr>
															<td class="etichetta-dato">Nome</td>
															<td class="valore-dato"><x:out select="$Certificate/SubjectDN/GIVENNAME"/></td>
														</tr>
													</x:if>
													
													<x:if select="$Certificate/SubjectDN/DN != ''">
														<tr>
															<td class="etichetta-dato">Codice identificativo</td>
															<td class="valore-dato"><x:out select="$Certificate/SubjectDN/DN"/></td>
														</tr>
													</x:if>								
													
													<x:if select="$Certificate/SubjectDN/SERIALNUMBER != ''">
														<tr>
															<td class="etichetta-dato">Codice fiscale</td>
															<td class="valore-dato"><x:out select="substring($Certificate/SubjectDN/SERIALNUMBER,4)"/></td>
														</tr>
													</x:if>
													
													<x:if select="$Certificate/SubjectDN/O != ''">
														<tr>
															<td class="etichetta-dato">Organizzazione</td>
															<td class="valore-dato"><x:out select="$Certificate/SubjectDN/O"/></td>
														</tr>
													</x:if>
													
													<x:if select="$Certificate/SubjectDN/OU != ''">
														<tr>
															<td class="etichetta-dato">Unit&agrave; organizzativa</td>
															<td class="valore-dato"><x:out select="$Certificate/SubjectDN/OU"/></td>
														</tr>								
													</x:if>
													
													<x:if select="$Certificate/SubjectDN/C != ''">
														<tr>
															<td class="etichetta-dato">Nazione</td>
															<td class="valore-dato"><x:out select="$Certificate/SubjectDN/C"/></td>
														</tr>
													</x:if>
													
													<tr>
														<td colspan="2"><i>Dati ente certificatore<i></td>
													</tr>	
													
													<x:if select="$Certificate/IssuerDN/CN != ''">		
														<tr>
															<td class="etichetta-dato">Denominazione</td>
															<td class="valore-dato"><x:out select="$Certificate/IssuerDN/CN"/></td>
														</tr>
													</x:if>
													
													<x:if select="$Certificate/IssuerDN/O != ''">					
														<tr>								
															<td class="etichetta-dato">Organizzazione</td>
															<td class="valore-dato"><x:out select="$Certificate/IssuerDN/O"/></td>
														</tr>
													</x:if>
													
													<x:if select="$Certificate/IssuerDN/OU != ''">	
														<tr>
															<td class="etichetta-dato">Unit&agrave; organizzativa</td>
															<td class="valore-dato"><x:out select="$Certificate/IssuerDN/OU"/></td>
														</tr>
													</x:if>
													
													<x:if select="$Certificate/IssuerDN/C != ''">
														<tr>
															<td class="etichetta-dato">Nazione</td>
															<td class="valore-dato"><x:out select="$Certificate/IssuerDN/C"/></td>
														</tr>
													</x:if>
													
													<tr>
														<td colspan="2"><i>Dati del certificato<i></td>
													</tr>
													
													<x:if select="$Certificate/SerialNumber != ''">
														<tr>
															<td class="etichetta-dato">Numero seriale</td>
															<td class="valore-dato"><x:out select="$Certificate/SerialNumber"/></td>
														</tr>
													</x:if>
													
													<x:if select="$Certificate/NotBefore != ''">
														<tr>
															<td class="etichetta-dato">Certificato valido dal</td>
															<td class="valore-dato"><x:out select="$Certificate/NotBefore"/></td>
														</tr>
													</x:if>
													
													<x:if select="$Certificate/NotAfter != ''">
														<tr>
															<td class="etichetta-dato">Data di scadenza del certificato</td>
															<td class="valore-dato"><x:out select="$Certificate/NotAfter"/></td>
														</tr>
													</x:if>
													
													<tr>
														<td class="etichetta-dato">Data della firma</td>
														<td class="valore-dato"><x:out select="$Certificate/SigningDate"/></td>
													</tr>
													
													<x:if select="$Certificate/DigestAlgOID != ''">																								
														<tr>
															<td class="etichetta-dato">Algoritmo digest</td>
															<td class="valore-dato">
																<x:out select="$Certificate/DigestAlgOID"/><x:if select="$Certificate/DigestAlgOIDDescription != ''"> (<x:out select="$Certificate/DigestAlgOIDDescription"/>)</x:if>
															</td>
														</tr>
													</x:if>
													
													<tr>
														<td class="etichetta-dato">Il certificato &egrave; valido/integro ?</td>
														<td class="valore-dato">
															<x:choose>
																<x:when select="$Certificate/Valid = 'true'">
																	Si
																</x:when>
																<x:otherwise>
																	No							
																</x:otherwise>										
															</x:choose>
														</td>
													</tr>
													
													<c:if test="${!empty ckdateformat}">
														<tr>
															<td class="etichetta-dato">Il certificato &egrave; attendibile alla data del ${ckdateformat} ?</td>
															<td class="valore-dato">
																<x:choose>
																	<x:when select="$Certificate/CheckAtDate = 'true'">
																		Si
																	</x:when>
																	<x:otherwise>
																		No							
																	</x:otherwise>										
																</x:choose>
															</td>
														</tr>
													</c:if>
													
													<tr>
														<td class="etichetta-dato">Il certificato ha validit&agrave; legale ?</td>
														<td class="valore-dato">
															<x:choose>
																<x:when select="$Certificate/NonRepudiation = 'true'">
																	Si
																</x:when>
																<x:otherwise>
																	No, il certificato non ha validit&agrave; legale in quanto non presenta l'attributo di non ripudio	
																</x:otherwise>										
															</x:choose>
														</td>
													</tr>
												</table>
											</ul>
										</li>
									</x:forEach>
									<c:forEach begin="1" end="${livelli}" varStatus="loop">
										</ul>
										</li>
									</c:forEach>
								</ul>
							</td>
						</tr>
					</x:if>
					<c:if test="${! empty verificaMarcheTemporaliXML}">
					
					<x:parse var="xverificaMarcheTemporali" xml="${verificaMarcheTemporaliXML}" />
					<x:if select='count($xverificaMarcheTemporali) > 0'>
						<tr>
							<td colspan="2">
								<b><br>Marche temporali</b>
								<span id="treecontrolTimeStamp" style="float: right;">
									<a style="text-decoration: none; color:#404040; title="Chiudi tutto" href="#"><img src="${contextPath}/css/jquery/treeview/images/minus.gif" /> Chiudi tutto</a>&nbsp;&nbsp;
									<a style="text-decoration: none; color:#404040; title="Espandi tutto" href="#"><img src="${contextPath}/css/jquery/treeview/images/plus.gif" /> Espandi tutto</a>
								</span>
							</td>
						</tr>
						
						<tr>
							<td colspan="2">
								<ul id="grayTimeStamp" class="treeview-gray">
									<x:forEach var="TimeStamp" select="$xverificaMarcheTemporali//TimeStamp" varStatus="timeStampstatus">
										<li class="closed">
											<span>
												#<x:out select="$TimeStamp/@Id"/>
												Data marca: <x:out select="$TimeStamp/GenTime"/>
											</span>
											<ul>
												<table class="dettaglio-notab" style="width:99%; margin-left:5px; margin-top:5px; margin-bottom:20px;">
													<tr>
														<td colspan="2"><i>Dati marca temporale<i></td>
													</tr>
													<tr>
														<td class="etichetta-dato">Seriale</td>
														<td class="valore-dato"><x:out select="$TimeStamp/SerialNumber"/></td>
													</tr>
													<tr>
														<td class="etichetta-dato">Time Stamping Authority</td>
														<td class="valore-dato"><x:out select="$TimeStamp/Tsa"/></td>
													</tr>
													<tr>
														<td class="etichetta-dato">Time Stamping Authority Id Policy</td>
														<td class="valore-dato"><x:out select="$TimeStamp/TsaPolicyId"/></td>
													</tr>
												</table>
											</ul>
										</li>
									</x:forEach>
								</ul>
							</td>
						</tr>
						
					</x:if>
					</c:if>
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
		
	</gene:redefineInsert>

	<gene:javaScript>
	
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
	</gene:javaScript>



