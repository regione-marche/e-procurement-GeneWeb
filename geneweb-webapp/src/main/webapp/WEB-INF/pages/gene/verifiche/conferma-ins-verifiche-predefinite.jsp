<%
/*
 * Created on: 17-dic-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.verificheInserite and requestScope.verificheInserite eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">


<gene:setString name="titoloMaschera" value='Inserisci verifiche predefinite' />
	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="IMPR" gestisciProtezioni="false"  gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreInsertVerifichePredefinite">
																					
		<gene:campoScheda>
			<td>&nbsp;&nbsp;</td>
			<td>
				<c:if test="${true}">
					<br>
					Confermi l'inserimento delle verifiche predefinite per l'impresa ?<br>
					<br>
				</c:if>
				<c:if test="${false}">
					<br>
					<b>Attenzione:</b> non ci sono verifiche predefinite per l'impresa'<br>
					<gene:redefineInsert name="buttons">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
					</gene:redefineInsert>
					<br>
				</c:if>
			</td>
		</gene:campoScheda>

		<gene:campoScheda campo="CODIMP" campoFittizio="true" defaultValue="${param.codimp}" visibile="false" definizione="T21;0"/>
		
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gene/verifiche/conferma-ins-verifiche-predefinite.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>