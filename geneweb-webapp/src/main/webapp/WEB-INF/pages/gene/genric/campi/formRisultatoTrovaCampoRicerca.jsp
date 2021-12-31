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
      // CON LA LISTA DI CAMPI TROVATI E DI CUI UNO SOLO PUò ESSERE INSERITO NELLA RICERCA
    %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>

<div class="contenitore-popup">
<table class="lista">
		<tr>
			<td>
				<display:table name="elencoCampi" defaultsort="2" id="campo" class="datilista" requestURI="TrovaCampoRicerca.do">
					<display:column title="" >
						<a href="javascript:inserisciCampo('${campo.aliasTabella}.${campo.mnemonicoCampo}');"><img alt="Inserisci campo" title="Inserisci campo" src="${contextPath}/img/add.gif" height="16" width="16"></a>	</display:column>
					<display:column property="mnemonicoTabella" title="Argomento" sortable="true" headerClass="sortable"></display:column>
					<display:column property="aliasTabella" title="Tabella" sortable="true" headerClass="sortable"></display:column>
					<display:column property="mnemonicoCampo" title="Campo" sortable="true" headerClass="sortable"></display:column>
					<display:column property="descrizioneCampo" title="Descrizione" sortable="true" headerClass="sortable" >  </display:column>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Trova campo" title="Trova campo" onclick="javascript:trova();" >
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();" >
	        &nbsp;
	     </td>
	  </tr>
	</table>
</div>