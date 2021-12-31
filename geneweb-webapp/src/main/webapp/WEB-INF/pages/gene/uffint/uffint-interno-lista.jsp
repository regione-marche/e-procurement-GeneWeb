<%
/*
 * Created on: 06-Nov-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista degli uffici intestatari */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

			<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && !(! empty sessionScope.uffint && fn:contains(archiviFiltrati,"UFFINT")) && !fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}'>
					<input type="checkbox" name="keys" value="${chiaveRiga}"  />
				</c:if>
				<% //Aggiunta dei menu sulla riga %> 
				<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.SchedaUffint")}' >
					<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza ${fn:toLowerCase(nomeEntitaSingolaParametrizzata)}"/>
				</c:if>
				<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.SchedaUffint") and gene:checkProtFunz(pageContext, "MOD", "MOD") && !fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}' >
					<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica ${fn:toLowerCase(nomeEntitaSingolaParametrizzata)}" />
				</c:if>
				<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL") && !(! empty sessionScope.uffint && fn:contains(archiviFiltrati,"UFFINT")) && !fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}' >
					<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina ${fn:toLowerCase(nomeEntitaSingolaParametrizzata)}" />
				</c:if>
				<c:if test='${empty datiRiga.UFFINT_DATFIN}' >
					<gene:PopUpItem title="Disattiva" href="javascript:attivazione('${chiaveRigaJava}',2);" />
				</c:if>
				<c:if test='${!empty datiRiga.UFFINT_DATFIN}' >
					<gene:PopUpItem title="Attiva" href="javascript:attivazione('${chiaveRigaJava}',1);" />
				</c:if>
				
				</gene:PopUp>
			</gene:campoLista>
			<% // Campi veri e propri %>

			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="CODEIN" headerClass="sortable" width="90"/>
			<gene:campoLista campo="NOMEIN" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}"/>
			<gene:campoLista campo="CFEIN" headerClass="sortable" width="120"/>
			<gene:campoLista campo="DATFIN" visibile="false"/>
