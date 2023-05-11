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

 // PAGINA CHE CONTIENE I BOTTONI DEL WIZARD
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<%// Aggiunto il controllo delle protezioni %>
<td class="comandi-dettaglio" colSpan="2">
	<c:choose>
	<c:when test='${modo eq "NUOVO"}'>
		<gene:insert name="pulsanteAnnulla">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:wizardAnnulla();">
			&nbsp;&nbsp;&nbsp;
		</gene:insert>
		<gene:insert name="pulsanteIndietro">
			<span id="btnIndietro">
				&nbsp;
				<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:wizardIndietro();">
			</span>
		</gene:insert>
		<gene:insert name="pulsanteAvanti">
			<span id="btnAvanti">
				&nbsp;
				<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:wizardAvanti();">
			</span>
		</gene:insert>
		<gene:insert name="pulsanteFine">
			<span id="btnFine">
				&nbsp;
				<INPUT type="button" class="bottone-azione" value="Fine" title="Fine" onclick="javascript:wizardFine();">
			</span>
		</gene:insert>
		<gene:insert name="pulsanteSalva">
			<span id="btnSalva">
				&nbsp;
				<INPUT type="button" class="bottone-azione" value="Salva" title="Salva" onclick="javascript:wizardSalva()">
			</span>
		</gene:insert>
	</c:when>
	</c:choose>
	&nbsp;
</td>