<%/*
       * Created on 17-ott-2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA PAGINA DI CAMBIO PASSWORD PERCHE' SCADUTA DOPO
      // AVER SUPERATO LA LOGIN CON SUCCESSO
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<HTML>
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />

		  
  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    hideMenuPopup();
    hideSubmenuNavbar();
  }
  
	function gestisciSubmit() {
	  var esito = true;

	<c:if test='${! vecchiaPasswordIsNull}'>
	  if (esito && !controllaCampoInputObbligatorio(cambiaPasswordForm.vecchiaPassword, 'Vecchia Password'))
 	    esito = false;
 	</c:if>
	  if (esito && !controllaCampoInputObbligatorio(cambiaPasswordForm.nuovaPassword, 'Nuova Password'))
 	    esito = false;
	  if (esito && !controllaCampoInputObbligatorio(cambiaPasswordForm.confermaNuovaPassword, 'Conferma Nuova Password'))
 	    esito = false;
 	  if (esito && (cambiaPasswordForm.nuovaPassword.value != cambiaPasswordForm.confermaNuovaPassword.value)) {
 	    alert("<fmt:message key="errors.chgPsw.nuovePasswordDiverse"/>");
 	    esito = false;
 	  }

	<c:if test='${! vecchiaPasswordIsNull}'>
 	  if (esito && (cambiaPasswordForm.nuovaPassword.value == cambiaPasswordForm.vecchiaPassword.value)) {
 	    alert("<fmt:message key="errors.chgPsw.nuovaPasswordDiversaPrecedente"/>");
 	    esito = false;
 	  }
	</c:if>
	
	<c:choose>
		<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou89#")}' >
		  if (esito && !controllaCampoPassword(cambiaPasswordForm.nuovaPassword, 14, true)) {
			esito = false;
		  }
		</c:when>
		<c:otherwise>
		  if (esito && !controllaCampoPassword(cambiaPasswordForm.nuovaPassword, 8, true)) {
			esito = false;
		  }
		</c:otherwise>
	</c:choose>

 	  if (esito) {
			bloccaRichiesteServer();
			document.cambiaPasswordForm.metodo.value = 'modificaPasswordScaduta';

 	    document.cambiaPasswordForm.submit();
 	  }
	}
	
	function annulla(){
		if (confirm('Sei sicuro di volerti disconnettere?'))
			document.location.href='${contextPath}/Logout.do';
	}
  
-->
</script>

</HEAD>

<BODY onload='setVariables();checkLocation();' >
<jsp:include page="/WEB-INF/pages/commons/bloccaRichieste.jsp" />
<TABLE class="arealayout">
	<!-- questa definizione dei gruppi di colonne serve a fissare la dimensione
	     dei td in modo da vincolare la posizione iniziale del menù di navigazione
	     sopra l'area lavoro appena al termine del menù contestuale -->
	<colgroup width="150px"></colgroup>
	<colgroup width="*"></colgroup>
	<TBODY>
		<TR class="testata">
			<TD colspan="2">
				<jsp:include page="/WEB-INF/pages/commons/testata.jsp" />
			</TD>
		</TR>
		<TR class="menuprincipale">
			<TD><img src="${contextPath}/img/spacer-azionicontesto.gif" alt=""></TD>
			<TD>
			<!-- PARTE NECESSARIA PER VISUALIZZARE I SOTTOMENU DEL MENU PRINCIPALE DI NAVIGAZIONE -->
			<IFRAME class="gene" id="iframesubnavmenu"></iframe>
			<div id="subnavmenu" class="subnavbarmenuskin"
				onMouseover="highlightSubmenuNavbar(event,'on');"
				onMouseout="highlightSubmenuNavbar(event,'off');"></div>
			</TD>
		</TR>
		<TR>
			<TD class="menuazioni" valign="top">
			<div id="menulaterale" class="menulaterale"
				onMouseover="highlightSubmenuLaterale(event,'on');"
				onMouseout="highlightSubmenuLaterale(event,'off');">
			<table>
				<tbody>
					<tr>
						<td class="titolomenulaterale">Cambio Password: Azioni</td>
					</tr>
					<tr>
						<td class="vocemenulaterale"><a href="javascript:gestisciSubmit();"
							tabindex="1500">Salva</a></td>
					</tr>
					<tr>
						<td class="vocemenulaterale"><a href="javascript:annulla();"
							tabindex="1501">Annulla</a></td>
					</tr>
				</tbody>
			</table>
			</div>
			</TD>

			<TD class="arealavoro">

			<jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" />

			<div class="contenitore-arealavoro">
			<div class="titolomaschera">
				Cambia password <c:if test='${fn:length(nomeOggetto) gt 0}' > - ${nomeOggetto}</c:if>
			</div>
			<div class="contenitore-errori-arealavoro">
				<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
			</div>

			<div class="contenitore-dettaglio">
				<form name="cambiaPasswordForm" action="${contextPath}/geneAdmin/CambiaPasswordScaduta.do" method="post" >
					<html:hidden property="metodo" value="modificaPasswordScaduta" />
					<TABLE class="dettaglio-notab">
						<TR>
							<TD class="etichetta-dato">
								Vecchia Password (*)
							</TD>
							<TD class="valore-dato">
								<input type="password" name="vecchiaPassword" styleClass="testo" size="15" maxlength="30" />
							</TD>
						</TR>
						<TR>
							<TD class="etichetta-dato">
								Nuova Password (*)
							</TD>
							<TD class="valore-dato">
								<input type="password" name="nuovaPassword"	styleClass="testo" size="15" maxlength="30" />
							</TD>
						</TR>
						<TR>
							<TD class="etichetta-dato">
								Conferma Nuova Password (*)
							</TD>
							<TD class="valore-dato">
								<input type="password" name="confermaNuovaPassword" styleClass="testo" size="15" maxlength="30" />
							</TD>
						</TR>
						<TR>
							<TD class="comandi-dettaglio" colSpan="2">
								<INPUT type="button" class="bottone-azione" value="Salva" onclick="javascript:gestisciSubmit();">
								&nbsp;
								<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();" >
						    &nbsp;
							</TD>
						</TR>
					</TABLE>
				</form>
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
				<jsp:include page="/WEB-INF/pages/commons/footer.jsp" />
			</div>
			</TD>
		</TR>
	</TBODY>
</TABLE>

<!-- CAMPO INDISPENSABILE PER ESEGUIRE IL COPIA/INCOLLA NEGLI APPUNTI DEL MNEMONICO CON IE -->
<form action=""><input type="hidden" id="clipboard"></form>
<%/* Eseguo l output nel javascript se situazione mista con taglib GENE */%>
<gene:outJavaScript/>
</BODY>
</HTML>