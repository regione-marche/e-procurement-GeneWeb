<%
/*
 * Created on: 16/06/2016
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>

<%-- 
	Questa JSP serve a visualizzare a video le informazioni della firma remota 
	che vengono restituite dal WS di Maggioli e Infocert.
--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />

		<table class="dettaglio-notab">
			<logic:empty name="verificaFirmaForm" property="nomeFile">
				<tr>
					<td colspan="2" style="padding-right: 25px; padding-left: 5px; padding-top: 15px; padding-bottom: 15px; color: #FF0000">
						<br>
						<b>Il documento non &egrave; presente in banca dati</b>
						<br>
						<br>	
					</td>
				</tr>
			</logic:empty>
			<logic:notEmpty name="verificaFirmaForm" property="nomeFile">
				<%-- Firme digitali --%>
				<logic:empty name="verificaFirmaForm" property="firma.signatures">
					<tr>
						<td colspan="2" style="padding-right: 25px; padding-left: 5px; padding-top: 15px; padding-bottom: 15px">
							<br>
							<b>Il documento non &egrave; firmato</b>
							<br>	
						</td>
					</tr>
				</logic:empty>
				
				<logic:notEmpty name="verificaFirmaForm" property="firma.signatures">
					<tr>
						<td colspan="2"><b><br>Attendibilit&agrave; dei certificati</b></td>
					</tr>
					<tr>
						<td class="etichetta-dato">Lista dei certificati non attendibili alla data del <bean:write name="verificaFirmaForm" property="firmacheckts" format="dd/MM/yyyy HH:mm:ss" /></td>
						<td class="valore-dato">
							<logic:notEmpty name="verificaFirmaForm" property="firma.listOfInvalidSignatures">
								<bean:write name="verificaFirmaForm" property="firma.listOfInvalidSignatures" />
							</logic:notEmpty>
							<logic:empty name="verificaFirmaForm" property="firma.listOfInvalidSignatures">
									Tutti i certificati sono attendibili
							</logic:empty>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<b><br>Lista dei certificati</b>
							<span id="treecontrol" style="float: right;">
								<a style="text-decoration: none; color:#404040; title="Chiudi tutto" href="#"><img src="${contextPath}/css/jquery/treeview/images/minus.gif" /> Chiudi tutto</a>&nbsp;&nbsp;
								<a style="text-decoration: none; color:#404040; title="Espandi tutto" href="#"><img src="${contextPath}/css/jquery/treeview/images/plus.gif" /> Espandi tutto</a>
							</span>
						</td>
					</tr>
						<tr>
							<td colspan="2">
								<ul id="gray" class="treeview-gray">
									<logic:iterate id="signature" name="verificaFirmaForm" property="firma.signatures">
										<li>
											<span>
												<b>Livello <bean:write name="signature" property="index"/></b>
											</span>
											<ul>
												<li class="closed">
													<span>
														#<bean:write name="signature" property="index"/> <bean:write name="signature" property="subjectCommonName"/> (<bean:write name="signature" property="subjectSerialNumber"/>)
													</span>
													<span style="float: right; border-bottom: 1px dotted #555555;">
														<logic:equal value="false" name="signature" property="valid">[Certificato non valido/integro o non attendibile]</logic:equal>
													</span>
													<ul>
														<li>
															<table class="dettaglio-notab" style="width:99%; margin-left:5px; margin-top:5px;">
																<tr>
																	<td colspan="2"><i>Dati del firmatario<i></td>
																</tr>
																<logic:notEmpty name="signature" property="subjectCommonName">
																<tr>
																	<td class="etichetta-dato">Cognome e Nome</td>
																	<td class="valore-dato"><bean:write name="signature" property="subjectCommonName"/></td>
																</tr>
																</logic:notEmpty>
																<logic:notEmpty name="signature" property="subjectSerialNumber">
																<tr>
																	<td class="etichetta-dato">Codice fiscale</td>
																	<td class="valore-dato"><bean:write name="signature" property="subjectSerialNumber"/></td>
																</tr>
																</logic:notEmpty>
															</table>
														</li>
														<li>
															<table class="dettaglio-notab" style="width:99%; margin-left:5px; margin-top:5px;">
																<tr>
																	<td colspan="2"><i>Dati ente certificatore<i></td>
																</tr>
																<logic:notEmpty name="signature" property="issuerCommonName">
																<tr>
																	<td class="etichetta-dato">Denominazione</td>
																	<td class="valore-dato"><bean:write name="signature" property="issuerCommonName"/></td>
																</tr>
																</logic:notEmpty>
																<logic:notEmpty name="signature" property="issuerOrganization">
																<tr>
																	<td class="etichetta-dato">Organizzazione</td>
																	<td class="valore-dato"><bean:write name="signature" property="issuerOrganization"/></td>
																</tr>
																</logic:notEmpty>
															</table>
														</li>
														<li>
															<table class="dettaglio-notab" style="width:99%; margin-left:5px; margin-top:5px;">
																<tr>
																	<td colspan="2"><i>Dati del certificato<i></td>
																</tr>
																<logic:notEmpty name="signature" property="serialNumber">
																<tr>
																	<td class="etichetta-dato">Numero seriale</td>
																	<td class="valore-dato"><bean:write name="signature" property="serialNumber"/></td>
																</tr>
																</logic:notEmpty>
																<logic:notEmpty name="signature" property="desValidityIni">
																<tr>
																	<td class="etichetta-dato">Certificato valido dal</td>
																	<td class="valore-dato"><bean:write name="signature" property="desValidityIni"/></td>
																</tr>
																</logic:notEmpty>
																<logic:notEmpty name="signature" property="desValidityFin">
																<tr>
																	<td class="etichetta-dato">Data scadenza certificato</td>
																	<td class="valore-dato"><bean:write name="signature" property="desValidityFin"/></td>
																</tr>
																</logic:notEmpty>
																<logic:notEmpty name="signature" property="signingTimeISO">
																<tr>
																	<td class="etichetta-dato">Data della firma</td>
																	<td class="valore-dato"><bean:write name="signature" property="signingTimeISO" format="dd/MM/yyyy HH:mm:ss"/></td>
																</tr>
																</logic:notEmpty>
																<logic:notEmpty name="signature" property="signerDigestAlgDes">
																<tr>
																	<td class="etichetta-dato">Algoritmo digest</td>
																	<td class="valore-dato"><bean:write name="signature" property="signerDigestAlgDes"/></td>
																</tr>
																</logic:notEmpty>
																<logic:notEmpty name="signature" property="valid">
																<tr>
																	<td class="etichetta-dato">Il certificato è valido/integro?</td>
																	<td class="valore-dato"><bean:write name="signature" property="validItaliano"/></td>
																</tr>
																</logic:notEmpty>
																<logic:notEmpty name="signature" property="valid">
																<tr>
																	<td class="etichetta-dato">Il certificato è valido alla data del <br><bean:write name="verificaFirmaForm" property="firmacheckts" format="dd/MM/yyyy HH:mm:ss"/>?</td>
																	<td class="valore-dato">
																		<bean:write name="signature" property="validItaliano"/>
																		<logic:notEmpty name="signature" property="errorMessage">
																			<br><bean:write name="signature" property="errorMessage"/>
																		</logic:notEmpty>
																	</td>
																</tr>
																</logic:notEmpty>
															</table>
														</li>
													</ul>
												</li>
											</ul>
										</li>
									</logic:iterate>
								</ul>
							</td>
						</tr>
				</logic:notEmpty>
				<%-- Marche temporali digitali --%>
				<logic:notEmpty name="verificaFirmaForm" property="firma.timestamps">
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
								<logic:iterate id="timestamp" name="verificaFirmaForm" property="firma.timestamps">
									<li class="closed">
										<span>
											Data marca: <bean:write name="timestamp" property="timestamp" format="dd/MM/yyyy HH:mm:ss"/>
											<span style="float: right; border-bottom: 1px dotted #555555;">
												<logic:equal value="false" name="timestamp" property="valid">[Firma temporale non valida]</logic:equal>
											</span>
										</span>
										<ul>
											<table class="dettaglio-notab" style="width:99%; margin-left:5px; margin-top:5px; margin-bottom:20px;">
												<tr>
													<td colspan="2"><i>Dati marca temporale<i></td>
												</tr>
												<tr>
													<td class="etichetta-dato">Seriale</td>
													<td class="valore-dato"><bean:write name="timestamp" property="serial" /></td>
												</tr>
												<tr>
													<td class="etichetta-dato">Time Stamping Authority</td>
													<td class="valore-dato"><bean:write name="timestamp" property="tsa" /></td>
												</tr>
												<tr>
													<td class="etichetta-dato">Time Stamping Authority Id Policy</td>
													<td class="valore-dato"><bean:write name="timestamp" property="policyId" /></td>
												</tr>
												<tr>
													<td class="etichetta-dato">Firma temporale valida?</td>
													<td class="valore-dato"><bean:write name="timestamp" property="validItaliano" /></td>
												</tr>
											</table>
										</ul>
									</li>
								</logic:iterate>
							</ul>
						</td>
					</tr>
				</logic:notEmpty>
			</logic:notEmpty>
		</table>
	