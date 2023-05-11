<%/*
       * Created on 22-set-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLA PAGINA DI RICERCA
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML lang="it">
<HEAD>
<c:if test="${requestScope.forzaRedirect}">
	<meta HTTP-EQUIV="REFRESH" content="0; url=${pageContext.request.contextPath}/ErrorOpenScheda.do"></meta>
</c:if>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript"
  src="${contextPath}/js/forms.js"></script>
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />
  
$(function() {
	$( "input.data" ).datepicker($.datepicker.regional[ "it" ]);
});

window.onfocus=fnFocus;
  
  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    hideMenuPopup();
    hideSubmenuNavbar();
  }
-->
</script>
  <gene:insert name="jsAzioniTags" src="/WEB-INF/pages/commons/jsAzioniTags.jsp" />
  <gene:insert name="head" >
  </gene:insert>
</HEAD>

<BODY onload="setVariables();checkLocation();gestioneAction();">
<jsp:include page="/WEB-INF/pages/commons/bloccaRichieste.jsp" />
<TABLE class="arealayout">
<!-- questa definizione dei gruppi di colonne serve a fissare la dimensione dei td in modo da vincolare la posizione iniziale del menù di navigazione
	     sopra l'area lavoro appena al termine del menù contestuale -->
	<colgroup width="150px"></colgroup>
	<colgroup width="*"></colgroup>
  <TBODY>
    <TR class="testata">
      <TD colspan="2"><gene:insert name="testata" src="/WEB-INF/pages/commons/testata.jsp"/></TD>
    </TR>
		<TR class="menuprincipale" >
			<TD><img src="${contextPath}/img/spacer-azionicontesto.gif" alt=""></TD>
			<TD>
        <table class="contenitore-navbar">
          <tbody>
            <tr>
              &nbsp;
            </tr>
          </tbody>
        </table>

      </TD>
    </TR>
    <TR>
      <TD class="menuazioni" valign="top">
        &nbsp;
      </TD>

      <TD class="arealavoro">

			<jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" />

		  <div class="contenitore-arealavoro">

				<div class="titolomaschera">
					<gene:getString name="titoloMaschera" defaultVal='${gene:resource("label.tags.template.dettaglio.titolo")}' />
				</div>

				<jsp:include page="/WEB-INF/pages/commons/balloon.jsp" />

	      <div class="contenitore-errori-arealavoro"><jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" /></div>
	      <div class="contenitore-errori-arealavoro"><jsp:include page="/WEB-INF/pages/commons/javascriptMsg.jsp" /></div>
	      <div class="contenitore-dettaglio">
	        <gene:insert name="corpo" >
	          <b><big>Qui bisogna inserire il corpo delle ricerca !!!</big></b>
	        </gene:insert>
	      </div>
				<%// Insert tag lasciato per la ridefinizione dei messaggi di debug %>
				<gene:insert name="debug" />
				<gene:insert name="debugDefault" src="/WEB-INF/pages/commons/sviluppo/sviluppo-debug.jsp"/>
      </div>
			

      <!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO -->
      <IFRAME class="gene" id="iframepopmenu"></iframe>
      <div id="popmenu" class="popupmenuskin"
        onMouseover="highlightMenuPopup(event,'on');"
        onMouseout="highlightMenuPopup(event,'off');"></div>

      </TD>
    </TR>
		
		<TR>
			<TD COLSPAN="2">
			<div id="footer">
				<gene:insert name="footer" src="/WEB-INF/pages/commons/footer.jsp" />
			</div>
			</TD>
		</TR>

  </TBODY>
</TABLE>

<!-- CAMPO INDISPENSABILE PER ESEGUIRE IL COPIA/INCOLLA NEGLI APPUNTI DEL MNEMONICO CON IE -->
<form action=""><input type="hidden" id="clipboard"></form>
<gene:outJavaScript/>

</BODY>
</HTML>