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


<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="protocolloSSO" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetProtocolloSSOFunction",  pageContext)}' scope="request"/>
<c:set var="spidAgid" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "sso.spid.validator")}' scope="request"/>

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
					<td width=374 height=237 style="BACKGROUND: url(${contextPath}/img/${applicationScope.pathImg}sfondo-login.png)" >
					<table border=0>
					<tr><td width=110>&nbsp;</td>
					<td><tiles:insert attribute="areaDestra" /></td>
					</tr>
					</table>
				</tr>
			</table>
            <logic:messagesPresent message="true">
				<P>
					<html:messages id="error" message="true" property="error">
					<font color="red"><c:out value="${error}"/></font>
					</html:messages>
				</P>
			</logic:messagesPresent>
			</TD>
		</TR>
		<c:if test='${protocolloSSO eq "5"}'>
			<tr>
				<td colspan="2" align="center"><br>
					<c:set var="urlSpid" value="${pageContext.request.contextPath}/SpidLogin.do" scope="request"/>
					<jsp:include page="/WEB-INF/pages/gene/login/spid/spid-button.jsp" >
						<jsp:param name="url" value="${urlSpid}" />
					</jsp:include>
				</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" align="center"><br>
				<jsp:include page="/WEB-INF/pages/gene/login/infoCustom.jsp" />
			</td>
		</tr>
	</TBODY>
</TABLE>
</BODY>

</HTML>

