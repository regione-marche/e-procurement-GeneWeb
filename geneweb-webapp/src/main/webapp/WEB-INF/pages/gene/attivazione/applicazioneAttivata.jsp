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
<!DOCTYPE html>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>



<html>
	<head>
		<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
		
		<script type="text/javascript">
			<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />
			
			$(window).on("load", function (){
				
				var t = setInterval(calcolaTempoResiduo,1000);
				
				function calcolaTempoResiduo() {
					var tempoResiduo = $("#tempoResiduo").text();
					tempoResiduo = tempoResiduo - 1;
					$("#tempoResiduo").text(tempoResiduo);
					if (tempoResiduo <= 0) {
						clearTimeout(t);
						reindirizzaLogin();	
					}
				}
				
				function reindirizzaLogin() {
					window.location.href="InitLogin.do";
				}
				
				$("#reindirizzaLogin").on("click",
					function() {
						reindirizzaLogin();
					}
				);
			});
			
		</script>
		
	</head>
	
	<body onload="setVariables();checkLocation();">
		<table class="arealayout">
			<tbody>
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
					<td colspan="2" class="arealavoro" align="center">
						<div class="contenitore-arealavoro">
							<div class="titolomaschera">
								<br>
								Attivazione applicativo
								<br>
								<br>
							</div>
							<div class="contenitore-dettaglio">
								<table class="dettaglio-notab">
									<tbody>
										<tr>
											<td colspan="2" style="text-align: center; border-bottom: 0px;">
												<br>
												<b>L'applicativo &egrave; stato attivato con successo.</b>
												<br> 
												<br>
												Tra <span id="tempoResiduo">10</span> secondi sarai reindirizzato alla maschera di <b><a id="reindirizzaLogin" class="link-generico">login</a></b>.
												<br>
												<br>	
											</td>
										</tr>
									</tbody>
								</table>		
							</div>
						</div>
					</td>
				</tr>	
			</tbody>
		</table>
	</body>
</html>