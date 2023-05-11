<%
	/*
   * Created on 15-giu-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE LA DEFINIZIONE DELLE SOTTOVOCI DEI MENU COMUNI A TUTTE LE APPLICAZIONI
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<fmt:setBundle basename="AliceResources" />

	<c:set var="contextPath" value="${pageContext.request.contextPath}" />
	<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
	<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

	<c:set var="nomeEntitaParametrizzata">
		<fmt:message key="label.tags.uffint.multiplo" />
	</c:set>

<script type="text/javascript">
 	var linksetSubMenuArchivi = "";
	<c:if test='${gene:checkProt(pageContext, "SUBMENU.VIS.ARCHIVI.Archivio-tecnici")}'>
		linksetSubMenuArchivi += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gene/tecni/tecni-trova.jsp&deftrova=TECNI", 1241, "Archivio tecnici");
	</c:if>
	<c:if test='${gene:checkProt(pageContext, "SUBMENU.VIS.ARCHIVI.Archivio-imprese")}'>
		linksetSubMenuArchivi += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gene/impr/impr-trova.jsp&deftrova=IMPR", 1242, "Archivio imprese");
	</c:if>
	<c:if test='${gene:checkProt(pageContext, "SUBMENU.VIS.ARCHIVI.Archivio-tecnici-imprese")}'>
		linksetSubMenuArchivi += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gene/teim/teim-trova.jsp&deftrova=TEIM", 1243, "Archivio tecnici delle imprese");
	</c:if>
	 <c:if test='${gene:checkProt(pageContext, "SUBMENU.VIS.ARCHIVI.Archivio-utenti")}'>
		linksetSubMenuArchivi += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gene/utent/utent-trova.jsp&deftrova=UTENT", 1244, "Archivio soggetti");
	</c:if>
	<c:if test='${gene:checkProt(pageContext, "SUBMENU.VIS.ARCHIVI.Archivio-uffici-intestatari")}'>
		linksetSubMenuArchivi += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gene/uffint/uffint-trova.jsp&deftrova=UFFINT", 1245, "Archivio ${fn:toLowerCase(nomeEntitaParametrizzata)}");
	</c:if>
  
  var linksetSubMenuDocUsoGenerale = "";
	<c:if test='${not empty sessionScope.profiloAttivo and gene:checkProt(pageContext,"SUBMENU.VIS.REPORT.Report-predefiniti")}'>  
	  linksetSubMenuDocUsoGenerale += creaVoceSubmenu("${contextPath}/geneGenric/ListaRicerchePredefinite.do?"+csrfToken, 1251, "Report predefiniti");
	</c:if>
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.REPORT.Schedulazioni")}'>
	  linksetSubMenuDocUsoGenerale += creaVoceSubmenu("${contextPath}/schedric/InitTrovaSchedRic.do?"+csrfToken, 1252, "Schedulazione report");
	</c:if>
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.REPORT.Risultati-schedulazioni")}'>
	  linksetSubMenuDocUsoGenerale += creaVoceSubmenu("${contextPath}/schedric/InitTrovaCodaSched.do?"+csrfToken, 1253, "Risultati schedulazioni");
	</c:if>
		
  var linksetSubMenuStrumenti = "";
  <!-- F.D. 27/02/2007 cambia la gestione dei menù: vengono abilitati i menù in base alle opzioni utente (ou) -->  	  
  <c:if test='${not empty sessionScope.profiloAttivo && fn:contains(listaOpzioniDisponibili, "OP2#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou48#") || fn:contains(listaOpzioniUtenteAbilitate, "ou49#")) and gene:checkProt(pageContext,"SUBMENU.VIS.STRUMENTI.Generatore-report")}'>	
	  linksetSubMenuStrumenti += creaVoceSubmenu("${contextPath}/geneGenric/InitTrovaRicerche.do?"+csrfToken, 1261, "Generatore report");
	</c:if>
  <!-- F.D. 27/02/2007 cambia la gestione dei menù: vengono abilitati i menù in base alle opzioni utente (ou) -->  	  
  <c:if test='${fn:contains(listaOpzioniDisponibili, "OP1#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou50#") || fn:contains(listaOpzioniUtenteAbilitate, "ou51#")) and gene:checkProt(pageContext,"SUBMENU.VIS.STRUMENTI.Generatore-modelli")}'>
	linksetSubMenuStrumenti += creaVoceSubmenu("${contextPath}/geneGenmod/InitTrovaModelli.do?"+csrfToken, 1262, "Generatore modelli");
  </c:if>
  <c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou232#") and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENEWEB.QuestionariQForm") and (fn:contains(listaOpzioniDisponibili, "OP135#") or fn:contains(listaOpzioniDisponibili, "OP136#"))}'>
	linksetSubMenuStrumenti += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=geneweb/qformlib/qformlib-trova.jsp&deftrova=QFORMLIB", 1263, "Generatore Q-form");
  </c:if>
  	
  var linksetSubMenuUtilita = "";
  <c:if test='${(profiloUtente.utenteLdap == 0 && !profiloUtente.autenticazioneSSO) and gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Cambia-password")}'>
  	linksetSubMenuUtilita += creaVoceSubmenu("${contextPath}/geneAdmin/InitCambiaPasswordAdmin.do?"+csrfToken+"&metodo=cambioBase&provenienza=menu", 1271, "Cambia password");
  </c:if>
  <!-- F.D. 27/02/2007 cambia la gestione dei menù: vengono abilitati i menù in base alle opzioni utente (ou) -->
  <c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") || fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>
	  <c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Gestione-profili")}'>
	  	linksetSubMenuUtilita += creaVoceSubmenu("${contextPath}/geneAdmin/ListaProfili.do?"+csrfToken, 1272, "Gestione profili");
		</c:if>
	  <c:if test='${not empty sessionScope.profiloAttivo && !(1 eq gruppiDisabilitati) and gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Gestione-gruppi")}'>
		  linksetSubMenuUtilita += creaVoceSubmenu("${contextPath}/geneAdmin/ListaGruppi.do?"+csrfToken, 1273, "Gestione gruppi");
	  </c:if>
	  <c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Gestione-utenti-applicativo")}'>
		  linksetSubMenuUtilita += creaVoceSubmenu("${contextPath}/geneAdmin/InitTrovaAccount.do?"+csrfToken, 1274, "Gestione utenti applicativo");
	  </c:if>
  </c:if>
	<c:if test='${not empty sessionScope.profiloAttivo && fn:contains(listaOpzioniUtenteAbilitate, "ou56#")}'>
	  <c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Import-export-report")}'>
	  	linksetSubMenuUtilita += creaVoceSubmenu("${contextPath}/geneGenric/InitFunzAvanzate.do?"+csrfToken, 1275, "Import/Export definizione report");
  	</c:if>
  	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Import-export-modelli")}'>
  		linksetSubMenuUtilita += creaVoceSubmenu("${contextPath}/geneGenmod/InitFunzAvanzateModelli.do?"+csrfToken, 1276, "Import/Export definizione modello");
  	</c:if>
  </c:if>

	<c:if test='${attivaLogEventi eq "1" && gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.StatisticheAccessi")}' >    
		linksetSubMenuUtilita += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=commons/contatoreAccessi.jsp", 1277, "Statistiche accessi");
	</c:if>	
	
	<c:if test='${gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Manuali")}'>
	 	<c:choose>
			<c:when test='${! empty applicationScope.manualeUtente}' >
				<c:set var="pathManual" value="javascript:apriManuale('${contextPath}${applicationScope.manualeUtente}');" />
				linksetSubMenuUtilita += creaVoceSubmenu(${gene:string4Js(pathManual)}, 1278, "Manuale");
			</c:when>
			<c:otherwise>
			linksetSubMenuUtilita += creaVoceSubmenu("${contextPath}/ApriPagina.do?"+csrfToken+"&href=gene/guida.jsp", 1278, "Manuali");
			</c:otherwise>
		</c:choose>
  </c:if>
  
  function apriManuale(manuale){
		var w = 700;
		var h = 500;
		var l = Math.floor((screen.width-w)/2);
		var t = Math.floor((screen.height-h)/2);
		window.open(manuale, "manuale", "toolbar=no,menubar=no,width=" + w + ",height=" + h + ",top=" + t + ",left=" + l + ",resizable=yes,scrollbars=yes");
	}
</script>