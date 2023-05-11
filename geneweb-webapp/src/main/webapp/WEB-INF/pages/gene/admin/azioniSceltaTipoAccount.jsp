<%/*
   * Created on 30-giu-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDITING DEL 
  // DETTAGLIO DI UN GRUPPO RELATIVA ALLE AZIONI DI CONTESTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>	
			<td class="vocemenulaterale"><a href="javascript:avanti();" 
					tabindex="1500" title="Avanti">Avanti</a></td>    
		</tr>	
		<tr>
			<td class="vocemenulaterale"><a href="javascript:annulla();"  
					tabindex="1501" title="Annulla">Annulla</a></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>	
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
