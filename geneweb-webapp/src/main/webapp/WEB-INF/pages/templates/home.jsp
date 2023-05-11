<%/*
       * Created on 15-giu-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLA HOMEPAGE
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />

<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />


  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
	hideMenuPopup();
    hideSubmenuNavbar();
  }
-->

</script>
<tiles:insert attribute="linkJSSubMenuComune"/>
<tiles:insert attribute="linkJSSubMenuSpecifico"/>
<tiles:insert attribute="head" />
</HEAD>

<BODY <tiles:put name="eventiDiPagina" value="setVariables();checkLocation();initPage();"/> >
<TABLE class="arealayout">
	<TBODY>
		<TR class="testata">
			<TD colspan="2">
				<tiles:insert attribute="testata" />
			</TD>
		</TR>
		<TR class="menuprincipale">
			<TD></TD>

			<TD>
			<table class="contenitore-navbar">
				<tbody>
					<tr>
						<tiles:insert attribute="menuSpecifico" />
						<tiles:insert attribute="menuComune" />
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
			<TD>
			<div id="menulaterale" valign="top">
			</div>
			</TD>			
			<TD class="arealavoro">
				<tiles:insert attribute="areaLavoro" />
			</TD>
		</TR>

		<TR>
			<TD COLSPAN="2">
			<div id="footer">
				<jsp:include page="/WEB-INF/pages/commons/footer.jsp" />
			</div>
			</TD>
		</TR>

	</TBODY>
</TABLE>
<%/* Eseguo l output nel javascript se situazione mista con taglib GENE */%>
<gene:outJavaScript/>
</BODY>
</HTML>
