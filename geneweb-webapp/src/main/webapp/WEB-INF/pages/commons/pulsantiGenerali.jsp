<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaParametrizzata">
	<fmt:message key="label.tags.uffint.multiplo" />
</c:set>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />

<c:choose>
	<c:when test='${isNavigazioneDisattiva eq "1"}'>
		<img src="${contextPath}/img/${applicationScope.pathImg}homeDisattiva.gif" alt="Home" />
	</c:when>
	<c:otherwise>
		<html:link tabindex="1496" page="/Home.do" module="">
			<img src="${contextPath}/img/${applicationScope.pathImg}home.gif" alt="Home" title="Torna alla homepage"/>
		</html:link>
	</c:otherwise>
</c:choose>


<c:if test='${sentinellaAccessoAltroApplicativo eq "1"}'>
	<html:link tabindex="1497" href="javascript:accediAltroApplicativoLista('${contextPath}');">
		<img src="${contextPath}/img/${applicationScope.pathImg}accedi.gif" alt="Accedi ad altro applicativo" title="Accedi ad altro applicativo"/>
	</html:link>
</c:if>

<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.Riferimenti-Informazioni")}' >
	<html:link tabindex="1498" href="javascript:about('${contextPath}');">
		<img src="${contextPath}/img/${applicationScope.pathImg}help.gif" alt="Informazioni su ${applicationScope.appTitle}" title="Informazioni su ${applicationScope.appTitle}"/>
	</html:link>
</c:if >
<c:choose>
	<c:when test='${sessionScope.sentinellaCodProfiloUnico eq "1"}'>
		<c:if test='${sentinellaSelezionaUffint eq "1"}'>
			<c:if test='${isNavigazioneDisattiva eq "1"}'>
				<img src="${contextPath}/img/${applicationScope.pathImg}profiloDisattivo.gif" alt="${nomeEntitaParametrizzata}"/>
			</c:if>
			<c:if test='${(empty isNavigazioneDisattiva) or (isNavigazioneDisattiva ne "1")}'>
				<html:link tabindex="1499" page="/CheckUfficioIntestatario.do" module="">
					<img src="${contextPath}/img/${applicationScope.pathImg}profilo.gif" alt="Torna alla scelta ${fn:toLowerCase(nomeEntitaParametrizzata)}" title="Torna alla scelta ${fn:toLowerCase(nomeEntitaParametrizzata)}"/>
				</html:link>
			</c:if>
		</c:if>
		<html:link tabindex="1500" page="/Logout.do" module=""
			onclick="javascript:return confirm('Sei sicuro di volerti disconnettere?');">
			<img src="${contextPath}/img/${applicationScope.pathImg}logout.gif" alt="Disconnetti utente" title="Disconnetti utente"/>
		</html:link>
	</c:when>
	<c:otherwise>
	<c:if test='${isNavigazioneDisattiva eq "1"}'>
		<img src="${contextPath}/img/${applicationScope.pathImg}profiloDisattivo.gif" alt="Profili"/>
	</c:if>
	<c:if test='${(empty isNavigazioneDisattiva) or (isNavigazioneDisattiva ne "1")}'>
		<html:link tabindex="1499" page="/CheckProfilo.do" module="">
			<img src="${contextPath}/img/${applicationScope.pathImg}profilo.gif" alt="Torna alla scelta profili" title="Torna alla scelta profili"/>
		</html:link>
	</c:if>
		<html:link tabindex="1500" page="/Logout.do" module=""
			onclick="javascript:return confirm('Sei sicuro di volerti disconnettere?');">
			<img src="${contextPath}/img/${applicationScope.pathImg}logout.gif" alt="Disconnetti utente" title="Disconnetti utente"/>
		</html:link>
	</c:otherwise>
</c:choose>
&nbsp;




