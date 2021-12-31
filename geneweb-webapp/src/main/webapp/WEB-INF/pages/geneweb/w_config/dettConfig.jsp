<%/*
   * Created on 13-nov-2014
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // Pagina per la visualizzazione delle proprieta' gestite nella tabella W_CONFIG

%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<tiles:insert definition=".dettaglioNoTabDef" flush="true">

	<tiles:put name="head" type="string">
<script type="text/javascript">
<!--

	var arrayProprieta = null;
	
	<%-- I valori attualmente gestiti sono:  --%>
	<%-- p		password  --%>
	<%-- b		valore booleano  --%>
	var tipoProprieta = null;

	$(document).ready(function() {
		if (arrayProprieta != null && arrayProprieta.length > 0 && 
				tipoProprieta != null && tipoProprieta.length > 0 &&
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
							$( ("#titleProp" + (indice+1)) ).attr('title', item.chiave);
							if ("p" == tipoProprieta[indice]) {
								if ("" != "" + item.valore) {
									$( ("#prop"  + (indice+1)) ).html("Password impostata");
								} else {
									$( ("#prop"  + (indice+1)) ).html("Password non impostata");
								}
							} else if ("b" == tipoProprieta[indice]) {
								var valore =item.valore;
                                if("0" == valore)
								    valore = "No";
                                else
                                	valore = "Si";
                                $( ("#prop"  + (indice+1)) ).html(valore);
							}else {
								$( ("#prop"  + (indice+1)) ).html(item.valore);
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

	// Azioni di pagina
	
	function modifica(){
		document.location.href='ApriPagina.do?'+csrfToken+'&href=geneweb/w_config/modConfig.jsp&detail=${param.detail}';
	}


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
			<gene:insert name="addHistory">
				<gene:historyAdd titolo='Configurazione propriet&agrave;' id="scheda" />
			</gene:insert>
		</gene:template>
		
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:modifica();" tabindex="1501" title="Modifica la configurazione">Modifica</a>
			</td>
		</tr>	
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
		<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />	
	</tiles:put>

	<jsp:include page="./titoloConfig.jsp" />

	<tiles:put name="titoloMaschera" type="string" value="${requestScope.titolo}" />
	 
	<tiles:put name="dettaglio" type="string">

		<table class="dettaglio-notab">

			<jsp:include page="./dettConfig${param.detail}.jsp" />

			<tr>
				<td class="comandi-dettaglio" colspan="2" >
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button" id="btnModificaConfig" class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modifica()" >
					</c:if>
					&nbsp;
				</td>
			</tr>
		</table>

	</tiles:put>

</tiles:insert>
