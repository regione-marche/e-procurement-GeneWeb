<%
/*
 * Created on 27-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA
 // DI LISTA GRUPPI CONTENENTE LA SEZIONE RELATIVA AI TAB
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaParametrizzata">
	<fmt:message key="label.tags.uffint.multiplo" />
</c:set>

<c:set var="listaTabSelezionabili"
	value="${fn:join(gestoreTab.tabSelezionabili,'#')}#" />

<table class="contenitore-tabs">
	<tr>
		<td>
			<elda:tab contextPath="${pageContext.request.contextPath}" tabindex="2000">
			<c:if test='${gene:checkProt(pageContext, "PAGE.VIS.GENE.USRSYS-Scheda.DATIGEN")}'>
				<elda:voceTab descrizione="Dettaglio" attivo='${gestoreTab.tabAttivo eq "Dettaglio"}' selezionabile='${fn:contains(listaTabSelezionabili, "Dettaglio#")}' href="schedaVisualizza(${idOggetto})"/>
			</c:if>
			<c:if test='${gene:checkProt(pageContext, "PAGE.VIS.GENE.USRSYS-Scheda.W_PROFILI")}'>
				<elda:voceTab descrizione="Profili" attivo='${gestoreTab.tabAttivo eq "Profili"}' selezionabile='${fn:contains(listaTabSelezionabili, "Profili#")}' href="listaProfiliAccount(${idOggetto})"/>
			</c:if>
			<c:if test='${applicationScope.gruppiDisabilitati ne "1"}' >
				<c:if test='${gene:checkProt(pageContext, "PAGE.VIS.GENE.USRSYS-Scheda.W_GRUPPI")}'>
					<elda:voceTab descrizione="Gruppi" attivo='${gestoreTab.tabAttivo eq "Gruppi"}' selezionabile='${fn:contains(listaTabSelezionabili, "Gruppi#")}' href="listaGruppiAccount(${idOggetto})"/>
				</c:if>
			</c:if>
			<c:if test='${uffintAbilitati eq "1" && !requestScope.nascondiUffint}' >
				<c:if test='${gene:checkProt(pageContext, "PAGE.VIS.GENE.USRSYS-Scheda.UFFINT")}'>
					<elda:voceTab descrizione="${nomeEntitaParametrizzata}" attivo='${gestoreTab.tabAttivo eq "Uffici intestatari"}' selezionabile='${fn:contains(listaTabSelezionabili, "Uffici intestatari#")}' href="listaUfficiIntestatariAccount(${idOggetto})"/>
				</c:if>
			</c:if>
				<c:if test='${gene:checkProt(pageContext, "PAGE.VIS.GENE.USRSYS-Scheda.TECNI")}'>
					<elda:voceTab descrizione="Tecnici" attivo='${gestoreTab.tabAttivo eq "Tecnici"}' selezionabile='${fn:contains(listaTabSelezionabili, "Tecnici#")}' href="schedaTecniciAccount(${idOggetto})"/>
				</c:if>
			
			</elda:tab>
		</td>
	</tr>
</table>