<%/*
       * Created on 2-ott-2009
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI UFFICI INTESTATARI
  // DI UN UTENTE CONTENENTE LE AZIONI DI CONTESTO
    %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

		<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
			<tr>
				<td class="titolomenulaterale">Dettaglio: Azioni</td>
			</tr>			
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Scheda.UFFINT.SCHEDAMOD")}' >
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:modificaAssUffintAccount('${idAccount}');" title="Modifica associazioni" tabindex="1500">Modifica associazioni</a></td>
			</tr>
		</c:if>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</c:if>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
