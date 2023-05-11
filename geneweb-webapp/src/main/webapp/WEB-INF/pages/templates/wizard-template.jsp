<%/*
       * Created on 18-apr-2008
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLA PAGINA DI WIZARD
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:insert name="addHistory">
	<c:if test='${modo ne "NUOVO" and modo ne "MODIFICA" }'>
		<%/*Eseguo il replace per l'eliminazione dell eventuale Update in inserimento */%>
		<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera",gene:resource("label.tags.template.dettaglio.titolo"))}' 
			id='${gene:callFunction("it.eldasoft.gene.tags.functions.OrdinaKeyPerHistoryFunction",key)}' 
			replaceParam='${gene:if(param.metodo eq "update","metodo;apri;modo;VISUALIZZA","")}' />
	</c:if>
</gene:insert>
<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />

<script type="text/javascript" src="${contextPath}/js/forms.js"></script>

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
  <gene:insert name="linkJSSubMenuComune"    src="/WEB-INF/pages/commons/jsSubMenuComune.jsp" />
  <gene:insert name="linkJSSubMenuSpecifico" src="/WEB-INF/pages/commons/jsSubMenuSpecifico.jsp" />
	<gene:insert name="jsAzioniTags" src="/WEB-INF/pages/commons/jsAzioniTags.jsp" />
  <gene:insert name="head" >
  </gene:insert>
</HEAD>

<BODY onload="setVariables();checkLocation();initPage();">
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
              <gene:insert name="menuSpecifico" src="/WEB-INF/pages/commons/menuSpecifico.jsp" />
              <gene:insert name="menuComune" src="/WEB-INF/pages/commons/menuComune.jsp"/>
            </tr>
          </tbody>
        </table>

        <!-- PARTE NECESSARIA PER VISUALIZZARE I SOTTOMENU DEL MENU PRINCIPALE DI NAVIGAZIONE -->
        <IFRAME class="gene" id="iframesubnavmenu"></iframe>
        <div id="subnavmenu" class="subnavbarmenuskin"
          onMouseover="highlightSubmenuNavbar(event,'on');"
          onMouseout="highlightSubmenuNavbar(event,'off');"></div>
      </TD>
    </TR>
    <TR>
      <TD class="menuazioni" valign="top">
        <gene:insert name="azioniContesto" src="/WEB-INF/pages/commons/azioniContestoWizard.jsp" />
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
	      
	        <gene:insert name="corpo" >
	        	<div class="contenitore-dettaglio">
	          <b><big>Qui bisogna inserire il corpo del wizard !!!</big></b>
		      </div>
	        </gene:insert>
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
