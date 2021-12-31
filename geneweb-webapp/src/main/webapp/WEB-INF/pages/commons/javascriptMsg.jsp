<!-- Parte generale per la visualizzazione dei messaggi javascript-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<div id="msgcontainer" class="errori-javascript-titolo" style="display: none;" >
	<a href="javascript:onOffMsg();" title="Visualizza/nascondi messaggi">
		<span id="msgimgon" style="display: none; border-style: none;" ><img src="${contextPath}/img/jsMsgOn.gif"  alt="" />Messaggi di controllo</span>
		<span id="msgimgoff"><img src="${contextPath}/img/isMsgOff.gif" alt="" />Messaggi di controllo</span>
	</a>
	<span id="msglog" class="errori-javascript-dettaglio" style="display: none;"></span>
</div>