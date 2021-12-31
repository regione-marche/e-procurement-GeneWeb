<%/*
   * Created on 25-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA RELATIVA ALLE
  // AZIONI DI CONTESTO PER LA VISUALIZZAZIONE DEL RISULTATO DI UNA ESTRAZIONE 
  // DI UNA RICERCA "PREDEFINITA".
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<gene:template file="menuAzioni-template.jsp">
<%
	/* Inseriti i tag per la gestione dell' history:
	 * il template 'menuAzioni-template.jsp' e' un file vuoto, ma e' stato definito 
	 * solo perche' i tag <gene:insert>, <gene:historyAdd> richiedono di essere 
	 * definiti all'interno del tag <gene:template>
	 */
%>
<c:set var="titoloHistory" value=""/>
<c:if test="${fn:length(risultatoRicerca.titoloRicerca) gt 0}" >
<c:set var="titoloHistory" value="'${risultatoRicerca.titoloRicerca}'"/>
</c:if>
	<gene:insert name="addHistory">
		<gene:historyAdd titolo="Dati estratti report ${titoloHistory}" id="listaa" />
	</gene:insert>
</gene:template>
		<tr>
			<td class="titolomenulaterale">Strumenti</td>
		</tr>
<c:if test='${not empty requestScope.risultatoRicerca.datiRisultato.numeroRecordTotali and requestScope.risultatoRicerca.datiRisultato.numeroRecordTotali > 0}'>
<c:if test='${(risultatoRicerca.genModelli) and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.W_MODELLI")}'>		
	<c:if test="${requestScope.risultatoRicerca.genModelli}">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:apriPopupGenModelli();" title="Modelli predisposti" tabindex="1515">Modelli predisposti</a>
			</td>
		</tr>
	</c:if>
</c:if>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:stampa();" title="Anteprima di stampa dei dati estratti" tabindex="1516">Anteprima di stampa</a>
			</td>
		</tr>
		<tr>
			<td class="nolinkmenulaterale">
				Export
	      <a href="${contextPath}/geneGenric/EsportaRisultatoRicerca.do?formato=0" title="Esporta dati in CSV"><img alt="Export CSV" src="${contextPath}/img/ico_file_csv.png"></a>
          <a href="${contextPath}/geneGenric/EsportaRisultatoRicerca.do?formato=1" title="Esporta dati in Excel"><img alt="Export Excel" src="${contextPath}/img/ico_file_excel.png"></a>
          <a href="${contextPath}/geneGenric/EsportaRisultatoRicerca.do?formato=2" title="Esporta dati in RTF"><img alt="Export RTF" src="${contextPath}/img/ico_file_rtf.png"></a>
          <a href="${contextPath}/geneGenric/EsportaRisultatoRicerca.do?formato=3" title="Esporta dati in PDF"><img alt="Export PDF" src="${contextPath}/img/ico_file_pdf.png"></a>
				
			</td>
		</tr>
</c:if>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
