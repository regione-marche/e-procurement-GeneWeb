<%/*
       * Created on 25-ago-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLA PAGINA DI LISTA
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />

<script type="text/javascript">
	<!--
  /**************************************************************
  Funzione che esegue la composizione di un testo tipo
  @param idModello codice del modello da comporre
  @param nome Nome del modello che viene messo nella descrizione

  @author Marco Franceschin
  @changelog
  19.09.2006: M.F. Prima Versione
  ***************************************************************/
  function componiModello(idModello, nome, riepilogativo ){
    // Eseguo il submit per la composizione del testo
    document.componiModelloForm.idModello.value=idModello;
    document.componiModelloForm.nomeModello.value=nome;
    document.componiModelloForm.riepilogativo.value=riepilogativo;
    document.componiModelloForm.submit();
  }
  
  function impostaFiltro(oggettoCheck) {
    if(oggettoCheck.checked)
      document.componiModelloForm.noFiltroEntitaPrincipale.value = 1;
    else 
      document.componiModelloForm.noFiltroEntitaPrincipale.value = 0;
    document.componiModelloForm.action = "${contextPath}/geneGenmod/ApriElencoModelli.do";
    document.componiModelloForm.submit();
  }

  -->
</script>
</HEAD>

<BODY onload="findTableDatiLista();">

<!-- parte per la selezione del modello -->
<TABLE class="arealayout" >
    <TR>
      <TD class="arealavoro" >
          <div class="titolomaschera">Elenco modelli per la composizione</div>

          <div class="contenitore-errori-arealavoro">
				<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
		  </div>

          <div class="contenitore-popup">
            <!-- Lista dei modelli -->
        <table class="lista">
		<tr>
			<td>
            <display:table name="listaModelli" defaultsort="3" id="modelloForm" class="datilista" requestURI="ApriElencoModelli.do" pagesize="25" sort="list">
              <display:column title="">
                <a href="javascript:componiModello('${modelloForm.idModello}','${modelloForm.nomeModelloPerJs}', ${modelloForm.riepilogativo});" title="Compila modello">
                  <img src="${contextPath}/img/componi_testo.gif" alt="" title="Componi testo" width="21" height="21" border="0" align="middle" />
                </a>
              </display:column>
              <display:column property="tipoModello" title="Tipo documento" sortable="true" headerClass="sortable" />
              <display:column property="nomeModello" title="Nome" sortable="true" headerClass="sortable" style="width: 150px" />
              <display:column property="nomeFile" title="File" sortable="true" headerClass="sortable" />
              <display:column property="descrModello" title="Descrizione" sortable="true" headerClass="sortable" />
            </display:table>
            </td>
        </tr>
			<c:if test='${not empty componiModelloForm.paginaSorgente}'>
				<tr>
					<td class="comandi-dettaglio-sx">
					<c:if test='${not empty componiModelloForm.paginaSorgente}'>
					<input type="checkbox" id="eliminaFiltro" <c:if test="${componiModelloForm.noFiltroEntitaPrincipale eq 1}" >checked</c:if> value="true" onclick="javascript:impostaFiltro(this);">&nbsp;Visualizza tutti i modelli
					&nbsp;&nbsp;&nbsp;
					</c:if>
					</td>
				</tr>
			</c:if>
        <tr>
		    	<td class="comandi-dettaglio">
					<INPUT type="button" class="bottone-azione" value="Esci" title="Esci dalla lista" onclick="javascript:window.close();">&nbsp;
				<td>
			</tr>
		</table>
				</div>
      </TD>
    </TR>
</TABLE>
<!-- Form per la compilazione -->
<html:form action="/CheckParametriModello">
	<html:hidden property="idModello"/>
	<html:hidden property="nomeModello"/>
	<html:hidden property="entita"/>
	<html:hidden property="nomeChiavi"/>
	<html:hidden property="valori"/>
	<html:hidden property="fileComposto"/>
	<html:hidden property="noFiltroEntitaPrincipale"/>
	<html:hidden property="paginaSorgente"/>
	<c:forEach items="${componiModelloForm.valChiavi}" var="chiave">
		<input type="hidden" name="valChiavi" value="${chiave}"/>
	</c:forEach>
	<input type="hidden" name="metodo" value="apriElenco" />
	<html:hidden property="riepilogativo"/>
</html:form>

<!-- INIZIO SQL PER CONDIZIONE DI FILTRO DEI MODELLI RISPETTO ALLA SCHEDA DI PARTENZA -->
<c:forEach items="${listaSqlSelect}" var="filtroModello" varStatus="indice">
	<!-- 
		Modello: ${filtroModello[0]}
		Query di filtro: ${filtroModello[1]}
		Parametri: ${sqlParams}
	-->
</c:forEach>
<!-- FINE SQL PER CONDIZIONE DI FILTRO DEI MODELLI RISPETTO ALLA SCHEDA DI PARTENZA -->
</BODY>
</HTML>