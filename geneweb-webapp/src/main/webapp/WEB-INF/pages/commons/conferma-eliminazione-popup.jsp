<%
			/*
       * Created on: 15.37 29/11/2007
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
					Maschera per la conferma d'eliminazione nella lista
				Creato da:
					Marco Franceschin
			*/
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="popup-message-template.jsp">
	<c:set var="msg" value='${gene:callFunction2(param.classe,pageContext,param.lista)}' />
	<gene:setString name="titoloMaschera" value='${titoloMessaggio}'/>
	
	<gene:redefineInsert name="corpo">
		<br>${msg}
  </gene:redefineInsert>
	<%/* Se ci sono errori elimino l opportunita di confermare l eliminazion*/%>
	<c:if test='${errorLevel == 2}' >
		<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
		</gene:redefineInsert>
	</c:if>
	<gene:javaScript>
		function conferma(){
			<c:choose>
				<c:when test='${param.lista eq "0"}'>
					<%// Eliminazione di una sola riga %>
					opener.chiaveRiga="${param.chiaveRiga}";
					opener.listaEliminaPopUp();
					window.close();
				</c:when>
				<c:otherwise>
					<%// Eliminazione di piu righe %>
					opener.chiaveRiga="${param.chiaveRiga}";
					opener.listaEliminaSelezionePopUp()
					windows.close();
				</c:otherwise>
			</c:choose>
			
			
		}
		function annulla(){
			window.close();
		}
	</gene:javaScript>
</gene:template>
