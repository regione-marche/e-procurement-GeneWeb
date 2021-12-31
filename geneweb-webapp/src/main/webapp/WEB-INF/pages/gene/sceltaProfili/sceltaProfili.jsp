<%/*
       * Created on 16-ott-2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'INTERA PAGINA PER LA SCELTA DEL PROFILO CON CUI
      // ACCEDERE ALL'APPLICAZIONE
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="moduloAttivo" value="${sessionScope.moduloAttivo}" scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="profiloUtente" value="${sessionScope.profiloUtente}" scope="request"/>
<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />

<HTML>
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

	function gestisciSubmit(codPro){
		document.setProfiloForm.profilo.value = codPro;
		document.setProfiloForm.submit();
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
				<c:if test='${(! empty moduloAttivo) and (! empty profiloUtente) and (isNavigazioneDisattiva ne "1")}' >
					<a href="javascript:goHome('${moduloAttivo}');" title="Torna alla homepage" tabindex="10">
				</c:if>
						<img src="${contextPath}/img/banner_logo.png" alt="Torna alla homepage" title="Torna alla homepage"><c:if test='${(! empty moduloAttivo) and (! empty profiloUtente) and (isNavigazioneDisattiva ne "1")}' ></a></c:if>
				</div>
			</TD>
		</TR>
		<TR class="menuprincipale">
			<TD colspan="2"></TD>
		</TR>
		<TR>
			<TD class="menuazioni">
				<div id="menulaterale">			
				</div>
			</TD>
			<TD class="arealavoro">
				<jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" >
					<jsp:param name="hideOpzioni" value="true"/>
					<jsp:param name="hideHistory" value="true"/>
				</jsp:include>

				<div class="titolomaschera">Selezione profilo applicativo</div>
				<table class="dettaglio-home">
				  <tr>
				  	<td height="100" width="75" valign="middle">
				  		<center><img alt="Profili" src="${contextPath}/img/logoProfilo.gif"></center>
				  	</td>
				  	<td height="100" valign="middle">
				  		La lista di seguito proposta presenta i profili validi con i quali è possibile accedere all'applicazione.
				  		Selezionare il nome del profilo desiderato per entrare nell'applicativo.
				  	</td>
				  </tr>
				<c:forEach items="${listaProfiliAccount}" var="profilo" varStatus="indice">
				  <tr>
				  	<td colspan="2" class="voce">
                    <p>
				    					<b>
								    		<a class="link-generico" href="javascript:gestisciSubmit('${profilo.codiceProfilo}');" tabindex="${2001 + indice.index}">${profilo.nome}</a>
							        </b>
							      </p>
							      <p>
							      	${profilo.descrizione}
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
					<jsp:param value="sceltaProfili" name="paginaChiamante"/>
				</jsp:include>
			</div>
			</TD>
		</TR>

	</TBODY>
</TABLE>
<form name="setProfiloForm" method="post" action="${contextPath}/SetProfilo.do">
	<input type="hidden" name="profilo" />
</form>
</BODY>
</HTML>
