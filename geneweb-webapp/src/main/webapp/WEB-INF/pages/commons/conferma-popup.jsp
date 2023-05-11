<%
			/*
       * Created on: 9.22 30/11/2007
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
					Maschera di conferma
					IN:
						classe
							Classe di gestione del messaggio di conferma popup
						keyVar
							Variabile nel response che ha la chiave. Se non esiste viene chiamata una sola volta
						scriptConferma
							Funzione javascript da chiamare alla conferma (sulla finestra chiamante)
						scriptAnnulla
							Funzione javascript da chiamare all'annulla  (sulla finestra chiamante)
						tipo
							Tipo di messaggio: Per ora gestito "elimina" e "salva"
							il tipo viene aggiunto per l eventuale resource in funzione del livello de'errore
							i resource utilizzati sono:
							label.tags.confermaPopUp.title 
								Titolo
							label.tags.confermaPopUp.msg
								Messaggio a livello di messaggi (viene fatto il replace del {0} con i messaggi scritti)
							label.tags.confermaPopUp.war
								Messaggio a livello di attenzione (viene fatto il replace del {0} con i messaggi scritti)
							label.tags.confermaPopUp.err
								Messaggio a livello di errore (viene fatto il replace del {0} con i messaggi scritti)
				Creato da:
					Marco Franceschin
			*/
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="popup-message-template.jsp">
	<c:set var="msg" value='${gene:callFunction3(param.classe,pageContext,param.keyVar,param.tipo)}' />
	<gene:setString name="titoloMaschera" value='${titoloMessaggio}'/>
	
	<gene:redefineInsert name="corpo">
		<br>${msg}
  </gene:redefineInsert>
	<%/* Se ci sono errori elimino l opportunita di confermare l eliminazion*/%>
	<gene:redefineInsert name="buttons">
		<c:if test='${bottoniPagina eq "3" or bottoniPagina eq "1"}'>
			<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
		</c:if>
		<c:if test='${bottoniPagina eq "3" or bottoniPagina eq "2"}'>
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
		</c:if>
	</gene:redefineInsert>
	
	
	<gene:javaScript>
		function conferma(){
			<c:if test="${not empty param.scriptConferma}">
				opener.${param.scriptConferma}();
			</c:if>
			window.close();
		}
		function annulla(){
			<c:if test="${not empty param.scriptAnnulla}" >
				opener.${param.scriptAnnulla}();
			</c:if>
			window.close();
		}
		<c:if test='${autoEseguiPagina and bottoniPagina == 1 }'>
			conferma();
		</c:if>
	</gene:javaScript>
</gene:template>
