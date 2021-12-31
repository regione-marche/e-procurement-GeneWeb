<%
/*
 * Created on 18-ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA GRUPPI 
 // CONTENENTE LA EFFETTIVA LISTA DEI GRUPPI E LE RELATIVE FUNZIONALITA' 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="idAccountCorrente" value="${profiloUtente.id}" scope="request"/>
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
		
<form name="listaAccount" action="ListaAccount.do">			
	<table class="lista">
		<tr>
			<td>
				<display:table name="listaAccount" defaultsort="3" id="account" class="datilista" sort="list" pagesize="${requestScope.risultatiPerPagina}" requestURI="TrovaAccount.do">
					<display:column class="associadati" title="Opzioni<br><center><a href='javascript:selezionaTutti(document.listaAccount.id);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.listaAccount.id);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
						<c:choose>
						<c:when test='${account.flagLdap == 0}'>
							<c:choose>
								<c:when test='${account.utenteDisabilitato == 1}'>
									<c:choose>
										<c:when test='${((!fn:contains(listaOpzioniUtenteAbilitate, "ou89#")) && (fn:contains(account.opzioniUtente, "ou89|"))) || (account.idAccount eq 48) || (account.idAccount eq 49) || (account.idAccount eq 50)}'>
											<A id="l${account.idAccount}" href="javascript:showMenuPopup('l${account.idAccount}',generaPopupListaOpzioniRecordAttivaSenzaEliminazione('${account.idAccount}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
										</c:when>
										<c:otherwise>
											<A id="l${account.idAccount}" href="javascript:showMenuPopup('l${account.idAccount}',generaPopupListaOpzioniRecordAttiva('${account.idAccount}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test='${((!fn:contains(listaOpzioniUtenteAbilitate, "ou89#")) && (fn:contains(account.opzioniUtente, "ou89|"))) || (account.idAccount eq 48) || (account.idAccount eq 49) || (account.idAccount eq 50)}'>
											<A id="l${account.idAccount}" href="javascript:showMenuPopup('l${account.idAccount}',generaPopupListaOpzioniRecordDisattivaSenzaEliminazione('${account.idAccount}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
										</c:when>
										<c:otherwise>
											<A id="l${account.idAccount}" href="javascript:showMenuPopup('l${account.idAccount}',generaPopupListaOpzioniRecordDisattiva('${account.idAccount}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test='${account.utenteDisabilitato == 1}'>
									<c:choose>
										<c:when test='${((!fn:contains(listaOpzioniUtenteAbilitate, "ou89#")) && (fn:contains(account.opzioniUtente, "ou89|"))) || (account.idAccount eq 48) || (account.idAccount eq 49) || (account.idAccount eq 50)}'>
											<A id="l${account.idAccount}" href="javascript:showMenuPopup('l${account.idAccount}',generaPopupListaOpzioniRecordLdapAttivaSenzaEliminazione('${account.idAccount}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
										</c:when>
										<c:otherwise>
											<A id="l${account.idAccount}" href="javascript:showMenuPopup('l${account.idAccount}',generaPopupListaOpzioniRecordLdapAttiva('${account.idAccount}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test='${((!fn:contains(listaOpzioniUtenteAbilitate, "ou89#")) && (fn:contains(account.opzioniUtente, "ou89|"))) || (account.idAccount eq 48) || (account.idAccount eq 49) || (account.idAccount eq 50)}'>
											<A id="l${account.idAccount}" href="javascript:showMenuPopup('l${account.idAccount}',generaPopupListaOpzioniRecordLdapDisattivaSenzaEliminazione('${account.idAccount}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
										</c:when>
										<c:otherwise>
											<A id="l${account.idAccount}" href="javascript:showMenuPopup('l${account.idAccount}',generaPopupListaOpzioniRecordLdapDisattiva('${account.idAccount}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
						</c:choose>
					<c:if test='${(gene:checkProt(pageContext,"FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADELSEL")) && !((!fn:contains(listaOpzioniUtenteAbilitate, "ou89#")) && (fn:contains(account.opzioniUtente, "ou89|"))) && ((account.idAccount ne 48) && (account.idAccount ne 49) && (account.idAccount ne 50))}'>
						<input type="checkbox" name="id" value="${account.idAccount}"/>
					</c:if>
					</display:column>

					<display:column property="idAccount" title="Codice" sortable="true"	headerClass="sortable" href="DettaglioAccount.do?metodo=visualizza&" paramId="idAccount" paramProperty="idAccount" />
					<display:column property="nome" title="Descrizione" sortable="true" headerClass="sortable" />
					<display:column property="login" title="Nome utente" sortable="true" headerClass="sortable" />
					<display:column property="utenteDisabilitato" title="Utente disabilitato" sortable="true" headerClass="sortable" decorator="it.eldasoft.gene.commons.web.displaytag.IntBooleanDecorator"/>
					<c:if test='${fn:contains(listaOpzioniDisponibili, "OP100#")}'>
						<display:column property="flagLdap" title="Utente LDAP" sortable="true" headerClass="sortable" decorator="it.eldasoft.gene.commons.web.displaytag.IntBooleanDecorator"/>
					</c:if>
					<c:if test="${! empty hashUffAppartenenza }">
						<display:column title="Ufficio appartenenza" sortable="true" headerClass="sortable" >
							<c:out value="${hashUffAppartenenza[account.ufficioAppartenenza].descTabellato}" />
						</display:column>
					</c:if>
					<display:column property="email" title="E-Mail" sortable="true" headerClass="sortable" />
				</display:table>
			</td>
		</tr>
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADELSEL")}'>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:eliminaSelez()">&nbsp;
			</td>
		</tr>
	</c:if>
	</table>
	<input type="hidden" name="metodo" id="metodo" value="eliminaSelez">
	<input type="hidden" name="keys" id="keys" value="">
</form>