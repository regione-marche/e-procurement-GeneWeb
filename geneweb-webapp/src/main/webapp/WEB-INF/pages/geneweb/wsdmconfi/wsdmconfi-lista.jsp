<%/*
   * Created on 17-ott-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<% //Si cancella dalla sessione il valore delle password per determinare le chiavi di cifratura delle buste %>

<c:set var="codapp" value="${sessionScope.moduloAttivo}"/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="WSDMCONFI-lista">
	<gene:setString name="titoloMaschera" value="Lista configurazioni di integrazione con sistema di protocollazione e gestione documentale"/>

	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
		
  	<%// Creo la lista per gare e torn mediante la vista %>
	<table class="lista">
		<tr>
			<td>
				<gene:formLista entita="WSDMCONFI" where="codapp = '${codapp}'" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="2" >
				<gene:campoLista title="Opzioni" width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza dettaglio" href="javascript:visualizzaConf('${datiRiga.WSDMCONFI_ID}','${datiRiga.WSDMCONFI_DESCRI}');"/>
							<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica intestazione" />
							<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina" />
						</gene:PopUp>
						<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
							<input type="checkbox" name="keys" value="${chiaveRiga}" />
						</c:if>
					</c:if>
				</gene:campoLista>
				<gene:campoLista campo="ID" headerClass="sortable" visibile="false" href="javascript:visualizzaConf('${datiRiga.WSDMCONFI_ID}','${datiRiga.WSDMCONFI_DESCRI}');"/>
				<gene:campoLista campo="CODAPP" headerClass="sortable" visibile="false"/>
				<gene:campoLista campo="DESCRI" headerClass="sortable" href="javascript:visualizzaConf('${datiRiga.WSDMCONFI_ID}','${datiRiga.WSDMCONFI_DESCRI}');"/>
				<gene:campoLista title="Default" campo="DEFAULT" headerClass="sortable" campoFittizio="true" definizione="T10;0;;;" width="100">
					<c:set var="defaultConf" value="${gene:callFunction3('it.eldasoft.gene.tags.functions.IsDefaultConfigFunction',  pageContext, datiRiga.WSDMCONFI_ID, codapp)}"/>
					<c:if test="${defaultConf eq 'defaultNonAttiva'}">
						<c:set var="defaultConf" value="Sì <img src='${pageContext.request.contextPath}/img/conf_allert.png' title='Configurazione non utilizzata' />"/>
					</c:if>
					<span>${defaultConf}</span>
				</gene:campoLista>
				<input type="hidden" name="genere" value="${genere}" />
				<input type="hidden" name="keyAdd" value="${addKeyRiga}" />
				<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}" />
				<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}" />
				
				</gene:formLista>
			</td>
		</tr>
		<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" /></tr>
		
	<gene:redefineInsert name="listaNuovo">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:listaNuovo();" title="Inserisci" tabindex="1501">
					${gene:resource("label.tags.template.lista.listaNuovo")}</a></td>
		</tr>
	</gene:redefineInsert>
	</table>

	<form name="formListaConfipro" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="geneweb/wsdmconfi/wsdmconfi-pagine-scheda.jsp" />
		<input type="hidden" name="entita" value="WSDMCONFI" />
		<input type="hidden" name="idconfi" value="" /> 
		<input type="hidden" name="descri" value="" /> 
		<input type="hidden" name="codapp" value="${codapp}" /> 
		<input type="hidden" name="tabAttivo" value="datigen" /> 
		<input type="hidden" name="risultatiPerPagina" value="20" />
		<jsp:include page="/WEB-INF/pages/commons/csrf.jsp" />
	</form>
		
  </gene:redefineInsert>
  <style>
  .contenitore-errori-arealavoro{
  	display:none;
  }
  </style>
	<gene:javaScript>
	// Visualizzazione del dettaglio
	function visualizzaConf(id,descri){
		document.formListaConfipro.idconfi.value=id;
		document.formListaConfipro.descri.value=descri;
		bloccaRichiesteServer();
		document.formListaConfipro.submit();
	}
  </gene:javaScript>
</gene:template>