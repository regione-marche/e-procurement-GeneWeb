<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaSingolaParametrizzata">
	<fmt:message key="label.tags.uffint.singolo" />
</c:set>

<c:set var="moduloAttivo" value="${sessionScope.moduloAttivo}" scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="profiloUtente" value="${sessionScope.profiloUtente}" scope="request"/>
<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />

<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />

  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    hideSubmenuNavbar();
  }

	function gestisciSubmit(codUffint){
		document.setUfficioIntestatarioForm.uffint.value = codUffint;
		document.setUfficioIntestatarioForm.submit();
	}

-->
</script>
<jsp:include page="/WEB-INF/pages/commons/jsSubMenuComune.jsp"/>
<jsp:include page="/WEB-INF/pages/commons/jsSubMenuSpecifico.jsp"/>
</HEAD>

<BODY onload="setVariables();checkLocation();initPage();" >
<TABLE class="arealayout">
	<TBODY>
		<TR class="testata">
			<TD colspan="2">
				<div class="banner">
					<img src="${contextPath}/img/banner_logo.png" alt="Torna alla homepage di ${applicationScope.appTitle}" title="Torna alla homepage">
				</div>
			</TD>
		</TR>
		<TR class="menuprincipale">
			<TD colspan="2">
			</TD>
		</TR>
		<TR>
			<TD class="menuazioni">
				<div id="menulaterale">			
				</div>
			</TD>
			<TD class="arealavoro">
				<jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" >
					<jsp:param name="hideOpzioni" value="true"/>
				</jsp:include>


			<div class="titolomaschera">Selezione ente</div>
			<div class="contenitore-errori-arealavoro"><jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" /></div>
			
				<table class="dettaglio-home">
				  <tr>
				  	<td height="100" width="75" valign="middle">
				  		<center><img alt="Profili" src="${contextPath}/img/ufficio.gif"></center>
				  	</td>
				  	<td height="100" valign="middle">
				  	Selezionare l'ente per il quale si intende procedere con l'autenticazione.
				  	</td>
				  </tr>
				<c:forEach items="${elencoUfficiIntestatari}" var="ufficio" varStatus="indice">
				  <tr>
				  	<td colspan="2" class="voce">
                    <p>
				    					<b>
								    		<a class="link-generico" href="javascript:gestisciSubmit('${ufficio.codice}');" tabindex="${2001 + indice.index}">${ufficio.nome} <c:if test="${!empty ufficio.dataTermineValidita }"> (DISATTIVATO)</c:if></a>
							        </b>
							      </p>
						</td>
				  </tr>
				</c:forEach>
				</table>
			</TD>
		</TR>

		<TR>
			<TD COLSPAN="2">
			<div id="footer">
				<jsp:include page="/WEB-INF/pages/commons/footer.jsp" >
					<jsp:param value="sceltaUfficioIntestatario" name="paginaChiamante"/>
				</jsp:include>
			</div>
			</TD>
		</TR>

	</TBODY>
</TABLE>
<form name="setUfficioIntestatarioForm" method="post" action="${contextPath}/SetEnteLogin.do">
	<input type="hidden" name="uffint" />
<form>
</BODY>
</HTML>
