<%/*
   * Created on 24-lug-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>


<tiles:insert definition=".dettaglioNoTabDef" flush="true">

<tiles:put name="head" type="string">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />

<script type="text/javascript">

<!--

	var arrayProprieta = null;
	
	<%-- I valori attualmente gestiti sono:  --%>
	<%-- p		password  --%>
	<%-- b		valore booleano  --%>
	var tipoProprieta = null;

	// Azioni di pagina

	function gestisciSubmit(){
		var continua = true;
		//Controllo del titolo del documento associato
		
		if(continua)
			document.formProprieta.submit();
	}

	function annulla() {
		document.location.href='ApriPagina.do?'+csrfToken+'&href=geneweb/w_config/dettConfig.jsp&detail=${param.detail}';
	}

	$(document).ready(function() {
		if (arrayProprieta != null && arrayProprieta.length > 0 &&
				tipoProprieta != null && tipoProprieta.length > 0   &&
				arrayProprieta.length <= tipoProprieta.length) {
			
			$.ajax({
				url: '${pageContext.request.contextPath}/GetProprieta.do',
				type: 'POST',
				async: false,
				dataType: 'json',
				data: { arrayProp: arrayProprieta },
				success: function(data) {
					if (data && data.length > 0) {
						var indice = 0;
						$.map( data, function( item ) {
							if ("passw" == tipoProprieta[indice]) {
								if ("" != "" + item.valore) {
									$( ("#prop"  + (indice+1)) ).html("Password impostata");
								} else {
									$( ("#prop"  + (indice+1)) ).html("Password non impostata");
								}
							} if ("b" == tipoProprieta[indice]) {
								$( ("#titoloProp" + (indice+1)) ).attr('title', item.chiave);
								$( ("#codapp" + (indice+1)) ).val(item.codapp);
								$( ("#chiave"+ (indice+1)) ).val(item.chiave);
								var valore =item.valore;
								if(valore=="" || valore== null)
									valore='1';
								$( ("#prop"  + (indice+1)) ).find('option[value="' + valore + '"]').attr("selected",true);
							}else {
								$( ("#titoloProp" + (indice+1)) ).attr('title', item.chiave);
								$( ("#codapp" + (indice+1)) ).val(item.codapp);
								$( ("#chiave"+ (indice+1)) ).val(item.chiave);
								$( ("#prop"  + (indice+1)) ).val(item.valore);
							}
							indice++;
						});
					}
				},
				error: function() {
					alert("Errore nel caricamento della propriet&agrave; (codapp=" + codiceApplicazione + "-chiave=" + chiave );
				}
			});
		}
	});
	
-->
</script>
	</tiles:put>

	<tiles:put name="azioniContesto" type="string">
		<gene:template file="menuAzioni-template.jsp">
		<%
			/* Inseriti i tag per la gestione dell' history:
			 * il template 'menuAzioni-template.jsp' e' un file vuoto, ma e' stato definito 
			 * solo perche' i tag <gene:insert>, <gene:historyAdd> richiedono di essere 
			 * definiti all'interno del tag <gene:template>
			 */
		%>
		</gene:template>
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:gestisciSubmit();" tabindex="1502" title="Salva">Salva</a>
			</td>
		</tr>		
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annulla();" tabindex="1503" title="Annulla">Annulla</a>
			</td>
		</tr>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
	</tiles:put>

	<jsp:include page="./titoloConfig.jsp" />

	<tiles:put name="titoloMaschera" type="string" value="${requestScope.titolo}" />
	 
	<tiles:put name="dettaglio" type="string">

	<form action="${contextPath}/SalvaConfigurazione.do?detail=${param.detail}" name="formProprieta" method="post" >
		
		<table class="dettaglio-notab">
		
			<jsp:include page="./modConfig${param.detail}.jsp" />
			
			<tr>
				<td class="comandi-dettaglio" colspan="2" >
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:gestisciSubmit()">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla()">
					&nbsp;
				</td>
			</tr>
		</table>
	
	</form>
	</tiles:put>

</tiles:insert>
