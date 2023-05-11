<%
	/*
	 * Created on: 08-mar-2007
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
	/* Interno della scheda del centro di costo */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<gene:formScheda entita="STO_UFFINT" gestisciProtezioni="true"
	gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreSTO_UFFINT">
	<gene:gruppoCampi idProtezioni="GEN">
		<c:if test='${modo eq "NUOVO"}'>
			<gene:campoScheda>
				<td colspan="2"><br>Attenzione, alcuni dati sensibili sono stati modificati.<br> Se si vuole procedere con l'archiviazione dei dati precedenti nello <b>storico anagrafe uffici</b> premere <b>"Salva"</b> altrimenti premere <b>"Annulla"</b><br><br></td>
			</gene:campoScheda>
		</c:if>
		<gene:campoScheda campo="ID" visibile="false"/>
		<gene:campoScheda campo="CODEIN" visibile="false" defaultValue="${empty storicoUffint?null:storicoUffint.codice}"/>
		<fmt:formatDate value='${storicoUffint.dataFineValidita}' type='DATE' pattern='dd/MM/yyyy' var="now" scope="page"/>
		<gene:campoScheda campo="DATA_FINE_VALIDITA" obbligatorio="true" defaultValue="${now}"/>
		<gene:campoScheda campo="NOMEIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.denominazione}"/>
		<gene:campoScheda campo="VIAEIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.indirizzo}"/>
		<gene:campoScheda campo="NCIEIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.civico}"/>
		<gene:campoScheda campo="CODCIT" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.codiceIstat}"/>
		<gene:campoScheda campo="CITEIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.localita}"/>
		<gene:campoScheda campo="PROEIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.provincia}"/>
		<gene:campoScheda campo="CAPEIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.cap}"/>
		<gene:campoScheda campo="CODNAZ" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.codiceNazione}"/>
		<gene:campoScheda campo="TELEIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.telefono}"/>
		<gene:campoScheda campo="FAXEIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.fax}"/>
		<gene:campoScheda campo="CFEIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.codiceFiscale}"/>
		<gene:campoScheda campo="TIPOIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.tipoAmministrazione}"/>
		<gene:campoScheda campo="EMAIIN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.email}"/>
		<gene:campoScheda campo="EMAI2IN" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.pec}"/>
		<gene:campoScheda campo="ISCUC" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.iscuc}"/>
		<gene:campoScheda campo="CFANAC" modificabile="false" defaultValue="${empty storicoUffint?null:storicoUffint.cfAnac}"/>
	</gene:gruppoCampi>

	<jsp:include
		page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="entitaParent" value="STO_UFFINT" />
	</jsp:include>

	<gene:campoScheda>	
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
				<c:when test='${modo eq "NUOVO"}'>
					<gene:insert name="pulsanteSalva">
						<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma();">
					</gene:insert>
					<gene:insert name="pulsanteAnnulla">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:window.close();">
					</gene:insert>
				</c:when>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>

	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
	
</gene:formScheda>
<c:remove var="storicoUffint" scope="session" />
<gene:javaScript>

<c:if test='${modo eq "VISUALIZZA" && isPopUp eq 1}'>
window.opener.historyReload();
window.close();
</c:if>

</gene:javaScript>
