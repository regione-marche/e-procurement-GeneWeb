
<%/*
   * Created on 12/11/2014
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE LE INDICAZIONI SUI BROWSER SUPPORTATI
  
  // PARAMETRI
  // usadiv : valorizzarlo a 1 quando il messaggio per i browser supportati va incluso in un div piuttosto che in un table
  // colspan: nel caso di inserimento del messaggio in un table, indicare il numero di colspan da applicare alla cella se diverso da 1
  %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="usadiv" value="0" />
<c:if test="${not empty param.usadiv}">
	<c:set var="usadiv" value="${param.usadiv}" />
 </c:if>
<c:set var="numcolonne" value="1" />
<c:if test="${not empty param.colspan}">
	<c:set var="numcolonne" value="${param.colspan}" />
 </c:if>

<c:choose>
	<c:when test="${usadiv == 1}">
	<div id="browserSupportati" style="display: none;">	
	</c:when>
	<c:otherwise>
	<tr id="browserSupportati" style="display: none;">
		<td class="voce" <c:if test="${numcolonne > 1}">colspan="<c:out value='${numcolonne}'/>"</c:if>>
	</c:otherwise>
</c:choose>

			<table cellspacing="10" cellpadding="5" class="dettaglio-home">
				<tr>
					<td class="sotto-voce" colspan="2">
						<center>
						<div style="width: 600px; background-color: #E3E3E3; padding: 10px; border: 1px; border-color: #7B7B7B;  border-style: solid;">
							<span style="font-size: 16px; vertical-align: bottom;"><img src="${contextPath}/img/Interface-59-32px-arancione.png">&nbsp;&nbsp;</span>
							<span style="font-size: 16px; color: #990000; vertical-align: bottom;"><b>Avviso compatibilit&agrave; del browser</b></span>
							<div >
								<p>
									<h3>Stai utilizzando un browser non supportato o una versione non supportata!</h3>
									<h3>Non &egrave; garantito il corretto funzionamento del sistema.</h3>
									Per favore aggiorna il tuo browser all'ultima versione oppure utilizza uno tra i browser supportati:
								</p>
								<table class="dettaglio-home" style="width: 600px;">
									<tr>
										<td><center>
											<a href="http://www.google.com/chrome" target="_blank"><img src="${contextPath}/img/supported-browsers/google-chrome.png"></a>
											</center>
										</td>
										<td><center>
											<a href="http://www.mozilla.com/firefox" target="_blank"><img src="${contextPath}/img/supported-browsers/mozilla-firefox.png">
											</center>
										</td>
										<td><center>
											<a href="https://www.microsoft.com/it-it/edge" target="_blank"><img src="${contextPath}/img/supported-browsers/microsfot-edge.png">
											</center>
										</td>
									</tr>
									<tr>
										<td><center><br>Google Chrome<br>ultima versione</center><br></td>

										<td><center><br>Mozilla Firefox<br>ultima versione</center><br></td>

										<td><center><br>Microsoft Edge<br>ultima versione</center><br></td>

									</tr>
								</table>
							</div>
						</div>
						</center>
						<br><br>
					</td>
				</tr>
			</table>

<c:choose>
	<c:when test="${usadiv == 1}">
	</div>	
	</c:when>
	<c:otherwise>
		</td>
	</tr>
	</c:otherwise>
</c:choose>

