<%/*
       * Created on 14-set-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
      // PER LA RICERCA DI UN CAMPO DA INSERIRE IN UNA RICERCA
    %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<div class="contenitore-popup">

<html:form action="/TrovaCampoRicerca" >
<table class="ricerca">
	<tbody>
		<tr>
			<td class="etichetta-dato">Mnemonico</td>
      <td class="operatore-trova">
      	<html:select property="operatoreMnemonicoCampo" >
	      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
      	</html:select>
      </td>
      <td class="valore-dato-trova">
				<html:text property="mnemonicoCampo" styleId="mnemonicoCampo" maxlength="10" />
			</td>
		</tr>
		<tr>
			<td class="etichetta-dato">Descrizione</td>
      <td class="operatore-trova">
      	<html:select property="operatoreDescrizioneCampo" >
	      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
      	</html:select>
      </td>
      <td class="valore-dato-trova">
				<html:text property="descrizioneCampo" styleId="descrizioneCampo" maxlength="60" />
			</td>
		</tr>
		<tr>
			<TD class="comandi-dettaglio" colSpan="3">
			<INPUT type="button" class="bottone-azione" value="Trova campo" title="Trova campo" onclick="javascript:gestisciSubmit();">&nbsp; 
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();">
			&nbsp;</TD>
		</tr>
	</tbody>
</table>
</html:form>
</div>

			<!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO -->
			<IFRAME class="gene" id="iframepopmenu"></iframe>
			<div id="popmenu" class="popupmenuskin"
				onMouseover="highlightMenuPopup(event,'on');"
				onMouseout="highlightMenuPopup(event,'off');"></div>
