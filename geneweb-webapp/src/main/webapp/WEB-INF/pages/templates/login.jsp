<%
/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE IL TEMPLATE DELLA PAGINA DI LOGIN

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="tmp" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetProtocolliSSOFunction",  pageContext)}'/>
<c:set var="spidAgid" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "auth.sso.spid.validator")}' scope="request"/>

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<tiles:insert attribute="head" />
<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />
-->
</script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/spid/spid-sp-access-button.min.css" />
	<style type="text/css">
		.card-container {
			  width: 80%;
			  margin: 0 auto;
			  display: flex;
			  flex-wrap: wrap;    
			  justify-content: center;
		}

		.card {
			  margin: 10px;
			  border: 1px solid #ddd;
			  width: 350px;
			  border-radius: 5px;
			  padding: 15px;
			  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
			  text-align: center;
			  background-color: #fff;
		}

		.card h2 {
			font-size: 18px;
			margin: 0 0 10px 0;
		}

		.card hr {
			border: 1px solid #ddd;
		}

		.card p {
			font-size: 14px;
			height: 100px;
			margin-bottom:16px;
		}

		.card a {
			border: none;
			border-radius: 0px;
			cursor: pointer;
			width: 		   	auto;
			margin-bottom: 0px;
		}
	</style>
</HEAD>


<BODY <tiles:getAsString name="eventiDiPagina" /> class="body-login">

<c:set var="isParingSSO" value="${pairingSSO eq 'true'}" scope="request"/>

<TABLE class="arealayout">
	<TBODY>
		<TR class="testata">
			<TD colspan="2"><tiles:insert attribute="testata" /></TD>
		</TR>
		
		<TR class="menuprincipale">
			<TD colspan="2"></TD>
		</TR>
		<tr style="height: 100px"><td></td></tr>
		<TR>
			<TD colspan="2" align="center">
            <logic:messagesPresent message="true">
				<P>
					<html:messages id="error" message="true" property="error">
					<font color="red"><c:out value="${error}"/></font>
					</html:messages>
				</P>
			</logic:messagesPresent>
			</TD>
		</TR>
		
		<TR>
			<TD colspan="2" align="center">
			<div class="card-container">
				<c:forEach var="configurazione" items="${listaConfigurazioniAttive}" varStatus="status">
					
						
							<c:choose>
								<c:when test="${configurazione.name eq 'auth.sso.spid'}">
								<div class="card">
									<h2>Entra con SPID</h2>
									<hr>
									<p>SPID, <b>il Sistema Pubblico di Identità Digitale</b>, è il sistema di accesso che consente di utilizzare, con un'identità digitale unica, i servizi online della Pubblica Amministrazione e dei privati accreditati.</p>
									<c:set var="urlSpid" value="${contextPath}/SpidLogin.do" scope="request"/>
									<jsp:include page="/WEB-INF/pages/gene/login/spid/spid-button.jsp" >
										<jsp:param name="url" value="${urlSpid}" />
									</jsp:include>
								</div>
								</c:when>
								<c:when test="${configurazione.name eq 'auth.sso.spidbusiness'}">
								<div class="card">
									<h2>Entra con SPID professionale</h2>
									<hr>
									<p>SPID per uso professionale è lo SPID "tipo 3" riservato ai liberi professionisti o lo SPID "tipo 4" rilasciato alla persona fisica che opera per conto di una persona giuridica.</p>
									<c:set var="urlSpidBusiness" value="${contextPath}/SpidBusinessLogin.do" scope="request"/>
									<jsp:include page="/WEB-INF/pages/gene/login/spid/spidbusiness-button.jsp" >
										<jsp:param name="urlBusiness" value="${urlSpidBusiness}" />
									</jsp:include>
								</div>
								</c:when>
								<c:when test="${configurazione.name eq 'auth.sso.cie'}">
								<div class="card">
									<h2>Entra con CIE</h2>
									<hr>
									<p>La <b>Carta di Identità Elettronica (CIE)</b> è il documento personale che attesta l'identità del cittadino. Dotata di microprocessore, oltre a comprovare l'identità personale, permette l'accesso ai servizi digitali della Pubblica Amministrazione.</p>
									<a href="${contextPath}/CieLogin.do" class="italia-it-button italia-it-button-size-m button-spid">
										<span class="italia-it-button-icon"><img src="img/Logo_CIE_ID.svg" onerror="this.src='img/spid-button/spid-ico-circle-bb.png'; this.onerror=null;" alt=""></span>
										<span class="italia-it-button-text">Entra con CIE</span>
									</a>
								</div>
								</c:when>
								<c:when test="${fn:indexOf(configurazione.name,'auth.sso.webserver')>-1 && !(isParingSSO && configurazione.loginBasedOnFiscalCode eq 'false') }">
								<div class="card">
									<h2>${configurazione.label}</h2>
									<hr>
									<p>${configurazione.description}</p>
									<c:set var="service" value="${fn:substringAfter(configurazione.name, 'auth.sso.webserver.')}"/>
									<a href="${contextPath}/WebserverLogin${service}.do" class="italia-it-button italia-it-button-size-m button-spid">
										<span class="italia-it-button-text">${configurazione.label}</span>
									</a>
								</div>
								</c:when>
								<c:when test="${configurazione.name eq 'auth.sso.gateway'}">
								<div class="card">
									<h2>${configurazione.label}</h2>
									<hr>
									<p>${configurazione.description}</p>
									<a href="${contextPath}/SSOGatewayLogin.do" class="italia-it-button italia-it-button-size-m button-spid" >
										<span class="italia-it-button-icon"><img src="img/logo_gateway.gif" onerror="this.src='img/spid-button/spid-ico-circle-bb.png'; this.onerror=null;" alt=""></span>
										<span class="italia-it-button-text">${configurazione.label}</span>
									</a>
								</div>
								</c:when>
								<c:when test="${configurazione.name eq 'auth.sso.cohesion'}">
								<div class="card">
									<h2>Entra con Cohesion</h2>
									<hr>
									<p>Il sistema di autenticazione federata della Regione Marche</p>
									<a href="${contextPath}/CohesionLoginResponseAction.do" class="italia-it-button italia-it-button-size-m button-spid">
										<span class="italia-it-button-icon"><img src="img/marche.svg" onerror="this.src='img/spid-button/spid-ico-circle-bb.png'; this.onerror=null;" alt=""></span>
										<span class="italia-it-button-text">Entra con Cohesion</span>
									</a>
								</div>
								</c:when>
								<c:when test="${configurazione.name eq 'auth.form' && !isParingSSO}">
								<div class="card">
									<h2>Entra con Username e Password</h2>
									<hr>
									<p> Se sei in possesso di credenziali applicative puoi accedere tramite l'inserimento di Username e Password nell'apposita form</p>
									<a href="${contextPath}/InitLoginForm.do" class="italia-it-button italia-it-button-size-m button-spid">
										<span class="italia-it-button-text">Entra con l'autenticazione interna</span>
									</a>
								</div>
								</c:when>
							</c:choose>
				</c:forEach>
				</div>
			</TD>
	</TR>
		<tr>
			<td colspan="2" align="center"><br>
				<jsp:include page="/WEB-INF/pages/gene/login/infoCustom.jsp" />
			</td>
		</tr>
		
	<TR>
		<TD COLSPAN="2">
		<div id="footer">
			<jsp:include page="/WEB-INF/pages/commons/footer.jsp" />
		</div>
		</TD>
	</TR>
		
	</TBODY>
</TABLE>

</BODY>

</HTML>

