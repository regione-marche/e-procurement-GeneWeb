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

      // PAGINA CHE CONTIENE LA DEFINIZIONE DELLE VOCI DEI MENU COMUNI A TUTTE LE APPLICAZIONI
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld" prefix="csrf" %>

	<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
	<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
	
	<c:set var="contextPath" value="${pageContext.request.contextPath}" />
	
	<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
	<c:if test='${gene:checkProt(pageContext,"MENU.VIS.ARCHIVI") and (sessionScope.moduloAttivo ne "W0" and sessionScope.moduloAttivo ne "MANPRO")}' >
 	  <td id="menuArchivi">
  	  <c:choose>
	  	  <c:when test='${isNavigazioneDisattiva eq "1"}'>
		  		<span id="lnavbarArchivi"><c:out value="Archivi" /></span>
		  	</c:when>
		  	<c:otherwise>
		  	  <a id="lnavbarArchivi" href="javascript:showSubmenuNavbar('lnavbarArchivi',linksetSubMenuArchivi);" tabindex="1240">Archivi</a>
		  	</c:otherwise>
  	  </c:choose>
 	  </td>
	</c:if>
	<c:if test='${gene:checkProt(pageContext,"MENU.VIS.REPORT")}' >
 	  <td id="menuDocUsoGenerale">
  	  <c:choose>
	  	  <c:when test='${isNavigazioneDisattiva eq "1"}'>
		  		<span id="lnavbarDocUsoGenerale"><c:out value="Report" /></span>
		  	</c:when>
		  	<c:otherwise>
  	  		<a id="lnavbarDocUsoGenerale" href="javascript:showSubmenuNavbar('lnavbarDocUsoGenerale',linksetSubMenuDocUsoGenerale);" tabindex="1250">Report</a>
		  	</c:otherwise>
  	  </c:choose>
 	  </td>
 	</c:if>
	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP1#") ||fn:contains(listaOpzioniDisponibili, "OP2#")}'>
	  <!-- F.D. 27/02/2007 cambia la gestione dei menù: vengono abilitati i menù in base alle opzioni utente (ou) -->  	  
	  <c:if test='${!fn:contains(listaOpzioniUtenteAbilitate, "ou30#") and gene:checkProt(pageContext,"MENU.VIS.STRUMENTI")}'>
	  	  <td id="menuComuneStrumenti">
		  	  <c:choose>
			  	  <c:when test='${isNavigazioneDisattiva eq "1"}'>
				  		<span><c:out value="Strumenti" /></span>
				  	</c:when>
				  	<c:otherwise>
				  	  <a id="lnavbarStrumenti" href="javascript:showSubmenuNavbar('lnavbarStrumenti',linksetSubMenuStrumenti);" tabindex="1260">Strumenti</a>
				  	</c:otherwise>
		  	  </c:choose>
	  	  </td>
	  </c:if>
  </c:if>
  <!-- F.D. 27/02/2007 cambia la gestione dei menù: vengono abilitati i menù in base alle opzioni utente (ou) -->
	<c:if test='${gene:checkProt(pageContext,"MENU.VIS.UTILITA")}' >
 	  <td id="menuUtilita">
  	  <c:choose>
	  	  <c:when test='${isNavigazioneDisattiva eq "1"}'>
		  		<span>Utilit&agrave;</span>
		  	</c:when>
		  	<c:otherwise>
		  	  <a id="lnavbarUtilita" href="javascript:showSubmenuNavbar('lnavbarUtilita',linksetSubMenuUtilita);" tabindex="1270">Utilit&agrave;</a>
		  	</c:otherwise>
  	  </c:choose>
		</td>
	</c:if>
	
	<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou89#") && gene:checkProt(pageContext,"MENU.VIS.AMMINISTRAZIONE")}' >
 	  <td id="menuAmministrazione" style="text-align: right" >
  	  <c:choose>
	  	  <c:when test='${isNavigazioneDisattiva eq "1"}'>
		  		<span><img alt="Amministrazione" src="${contextPath}/img/Setting-10-16px-gray.png"></span>
		  	</c:when>
		  	<c:otherwise>
		  	  <a id="lnavbarAmministrazione" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/system/dettCfgAmministrazione.jsp';" tabindex="1290">
		  	  	<img alt="Amministrazione" src="${contextPath}/img/Setting-10-16px-white.png">
		  	  </a>
		  	</c:otherwise>
  	  </c:choose>
		</td>
	</c:if>