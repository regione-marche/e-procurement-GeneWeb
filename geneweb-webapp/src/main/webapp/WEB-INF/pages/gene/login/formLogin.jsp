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
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LOGIN CONTENENTE IL FORM
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<tiles:insert attribute="head" />
<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />
-->
</script>
</HEAD>

<BODY <tiles:getAsString name="eventiDiPagina" /> class="body-login">
	<TABLE class="arealayout">
		<TBODY>
		<TR class="testata">
			<TD colspan="2"><tiles:insert attribute="testata" /></TD>
		</TR>
		<TR class="menuprincipale">
			<TD colspan="2"></TD>
		</TR>
		<TR>
			<TD colspan="2" align="center">
				<table class="contenitore-login">
					<tr>
						<td width=374 height=237
							style="BACKGROUND: url(${contextPath}/img/${applicationScope.pathImg}sfondo-login.png)">
							<table border=0>
								<tr>
									<td width=110>&nbsp;</td>
									<td><html:form action="/Login"
										onsubmit="javascript:return gestisciSubmit(this);">
										<table class="dati-login">
											<tr>
												<td class="etichetta-login">UTENTE</td>
												<td align="right"><html:text property="username"
														styleClass="testo" size="15" maxlength="60" /></td>
											</tr>
											<tr>
												<td class="etichetta-login">PASSWORD</td>
												<td align="right"><html:password property="password"
														styleClass="testo" size="15" maxlength="30" /></td>
											</tr>
											<tr>
												<td colspan="2">&nbsp;</td>
											</tr>
											<tr>
												<td colspan="2" align="right"><html:submit
														styleClass="bottone-login" value="Entra" /> <html:reset
														styleClass="bottone-login" onclick="document.location.href='${pageContext.request.contextPath}';" value="Annulla" /></td>
											</tr>
											<tr>
												<td colspan="2">&nbsp;</td>
											</tr>
											<c:if test="${applicationScope.attivaAccessoAnonimo == 1}">
												<tr>
													<td colspan="2" class="etichetta-login"><a
														href="${pageContext.request.contextPath}/LoginAnonimo.do">${applicationScope.etichettaAccessoAnonimo}</a>
													</td>
												</tr>
											</c:if>
											<c:if
												test="${(applicationScope.attivaFormRegistrazione == 1) && !(empty applicationScope.paginaRegistrazione) && !(fn:contains(listaOpzioniDisponibili, 'OP100#'))}">
												<tr>
													<td colspan="2" class="etichetta-login"><a
														href="${applicationScope.paginaRegistrazione}">Registrati</a>
													</td>
												</tr>
											</c:if>

											<c:if
												test="${applicationScope.attivaFormRecuperaPassword == 1 }">
												<tr>
													<td colspan="2" class="etichetta-login"><a
														href="recupera-password.jsp">Hai dimenticato la
															password?</a></td>
												</tr>
											</c:if>

											<c:if test="${applicationScope.attivaFormAssistenza != 0}">
												<tr>
													<td colspan="2" class="etichetta-login"><a
														href="${pageContext.request.contextPath}/RichiestaAssistenza.do">Richiedi
															assistenza</a></td>
												</tr>
											</c:if>

											<jsp:include page="customLinkLoginPage.jsp" />

										</table>
									</html:form></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</TD>
		</TR>
		<tr>
			<td colspan="2" align="center"><br>
				<jsp:include page="/WEB-INF/pages/gene/login/infoCustom.jsp" />
			</td>
		</tr>
	</TBODY>
	</TABLE>
</BODY>

</HTML>
				