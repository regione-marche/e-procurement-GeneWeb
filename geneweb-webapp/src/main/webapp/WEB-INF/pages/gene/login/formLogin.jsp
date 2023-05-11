<%
/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LOGIN CONTENENTE IL FORM
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

	<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
	<c:set var="isSsoAbilitato" value='${gene:callFunction("it.eldasoft.gene.tags.functions.IsSsoProtocolloAbilitatoFunction", pageContext)}'/>

                <html:form action="/Login" onsubmit="javascript:return gestisciSubmit(this);">
                <table class="dati-login">
                  <tr>
                    <td class="etichetta-login">UTENTE
                    </td>
                    <td align="right"><html:text property="username" styleClass="testo" size="15" maxlength="60"/>
                    </td>
                  </tr>
                  <tr>
                    <td class="etichetta-login">PASSWORD
                    </td>
                    <td align="right"><html:password property="password" styleClass="testo" size="15" maxlength="30"/>
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2">&nbsp;
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2" align="right">
                      <html:submit styleClass="bottone-login" value="Entra" />
                      <html:reset styleClass="bottone-login" value="Annulla"/>
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2">&nbsp;</td>
                  </tr>
               	<c:if test="${applicationScope.attivaAccessoAnonimo == 1}">
                  <tr>
                    <td colspan="2" class="etichetta-login">
	                      <a href="${pageContext.request.contextPath}/LoginAnonimo.do">${applicationScope.etichettaAccessoAnonimo}</a>
                    </td>
                  </tr>
                </c:if>
                <c:if test="${(applicationScope.attivaFormRegistrazione == 1) && !(empty applicationScope.paginaRegistrazione) && !(fn:contains(listaOpzioniDisponibili, 'OP100#')) && (isSsoAbilitato eq 'false') }">
                  <tr>
                    <td colspan="2" class="etichetta-login">
	                      <a href="${applicationScope.paginaRegistrazione}">Registrati</a>
                    </td>
                  </tr>
                </c:if>

				<c:if test="${applicationScope.attivaFormRecuperaPassword == 1 }">
                  <tr>
                    <td colspan="2" class="etichetta-login">
	                      <a href="recupera-password.jsp">Hai dimenticato la password?</a>
                    </td>
                  </tr>
                </c:if>
				
    			<c:if test="${applicationScope.attivaFormAssistenza != 0}">
                  <tr>
                    <td colspan="2" class="etichetta-login">
	                      <a href="${pageContext.request.contextPath}/RichiestaAssistenza.do">Richiedi assistenza</a>
                    </td>
                  </tr>
				</c:if>


								<jsp:include page="customLinkLoginPage.jsp" />

                </table>
                </html:form>
