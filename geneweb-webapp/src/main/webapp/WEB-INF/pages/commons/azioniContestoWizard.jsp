<%/*
   * Created on 21-apr-2008
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE LE AZIONI DI CONTESTO DEL WIZARD
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />

<div 	id="menulaterale" class="menulaterale" 
			onMouseover="highlightSubmenuLaterale(event,'on');"
			onMouseout="highlightSubmenuLaterale(event,'off');">
<table>
	<tbody>
		<tr>
			<td class="titolomenulaterale" title="<gene:getString name="titoloMaschera" defaultVal='${gene:resource("label.tags.template.dettaglio.titolo")}' />">
				${gene:resource("label.tags.template.wizard.titoloAzioni")}</td>
		</tr>
		
		<!-- c:if test='${tipoPagina eq "SCHEDA"}' -->
			<!-- c:choose -->
				<!-- c:when test='${modoAperturaScheda eq "NUOVO" }' -->
					<gene:insert name="wizardIndietro">
					<tr id="linkIndietro">
						<td class="vocemenulaterale">
							<a href="javascript:wizardIndietro();" title="&lt; Indietro" tabindex="1510">
								${gene:resource("label.tags.template.wizard.wizardIndietro")}</a></td>
					</tr>
					</gene:insert>
					<gene:insert name="wizardAvanti">
					<tr id="linkAvanti">
						<td class="vocemenulaterale">
							<a href="javascript:wizardAvanti();" title="Avanti &gt;" tabindex="1520">
								${gene:resource("label.tags.template.wizard.wizardAvanti")}</a></td>
					</tr>
					</gene:insert>
					<gene:insert name="wizardFine">
					<tr id="linkFine">
						<td class="vocemenulaterale">
							<a href="javascript:wizardFine();" title="Fine" tabindex="1530">
								${gene:resource("label.tags.template.wizard.wizardFine")}</a></td>
					</tr>
					</gene:insert>
					<gene:insert name="wizardSalva">
					<tr id="linkSalva">
						<td class="vocemenulaterale">
							<a href="javascript:wizardSalva();" title="Salva" tabindex="1540">
								${gene:resource("label.tags.template.wizard.wizardSalva")}</a></td>
					</tr>
					</gene:insert>
					<gene:insert name="wizardAnnulla">
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:wizardAnnulla();" title="Annulla" tabindex="1550">
								${gene:resource("label.tags.template.wizard.wizardAnnulla")}</a></td>
					</tr>
					</gene:insert>
				<!--  /c:when-->
			<!-- /c:choose -->
		<!-- /c:if -->
		<gene:insert name="addAzioniContestoBottom" />
	</tbody>
</table>
</div>