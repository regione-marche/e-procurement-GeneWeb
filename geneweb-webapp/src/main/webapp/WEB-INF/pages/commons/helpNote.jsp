<%
			/*
       * Created on: 9.30 26/10/2007
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
					Parte generale con le note aggiuntive dell'utente
				Creato da:
					Marco Franceschin
			*/
			/*
				Parametri della pagina:
					tipoNota
						Tipo della nota
					idObj	
						Identificativo dell'oggetto
					nameInput
						Nome dell'imput che deve contenere il nome dell'oggetto
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:if test='${param.modo eq "MODIFICA" or param.modo eq "NUOVO"}' >
<gene:redefineInsert name="addHistory">
	<script type="text/javascript" 
		src="${pageContext.request.contextPath}/js/jHtmlArea-0.7.5.min.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
		$('#W_NOTE_NOTA').htmlarea({
		toolbar: [
		["html"],
		["bold", "italic", "underline", "strikethrough"],
		        ["increasefontsize", "decreasefontsize"],
		        ["orderedlist", "unorderedlist"],
		        ["indent", "outdent"],
		        ["justifyleft", "justifycenter", "justifyright"],
		        ["link", "unlink", "image", "horizontalrule"],
		        ["cut", "copy", "paste"],
        [
            {
                // The CSS class used to style the <a> tag of the toolbar button
                css: 'clean',

                // The text to use as the <a> tags "Alt" attribute value
                text: 'Cancella il contenuto',

                // The callback function to execute when the toolbar button is clicked
                action: function (btn) {
                    // 'this' = jHtmlArea object
                    // 'btn' = jQuery object that represents the <a> ("anchor") tag for the toolbar button
					$("#W_NOTE_NOTA").val('');
					$("#W_NOTE_NOTA").htmlarea('updateHtmlArea');
                    // Take some action or do something here
                }
            }
        ]
		    ],
		
		    toolbarText: $.extend({}, jHtmlArea.defaultOptions.toolbarText, {
				"html": "Editor HTML/codice sorgente",
				"bold": "Grassetto",
		        "italic": "Corsivo",
		        "underline": "Sottolineato",
		        "strikethrough": "Barrato",
		        "increasefontsize": "Ingrandisci carattere",
		        "decreasefontsize": "Riduci carattere",
		        "orderedlist": "Elenco numerato",
		        "unorderedlist": "Elenco puntato",
		        "indent": "Aumenta rientro",
		        "outdent": "Riduci rientro",
		        "justifyleft": "Allinea testo a sinistra",
		        "justifycenter": "Centra",
		        "justifyright": "Allinea testo a destra",
		        "link": "Inserisci collegamento ipertestuale",
		        "unlink": "Rimuovi collegamento ipertestuale",
		        "image": "Inserisci immagine",
		        "horizontalrule": "Inserisci riga orizzontale",
		        "cut": "Taglia",
		        "copy": "Copia",
		        "paste": "Incolla"
		    })
		});
		$('#W_NOTE_NOTA1').htmlarea({
			toolbar: [
			["html"],
			["bold", "italic", "underline", "strikethrough"],
			        ["increasefontsize", "decreasefontsize"],
			        ["orderedlist", "unorderedlist"],
			        ["indent", "outdent"],
			        ["justifyleft", "justifycenter", "justifyright"],
			        ["link", "unlink", "image", "horizontalrule"],
			        ["cut", "copy", "paste"],
	        [
	            {
	                // The CSS class used to style the <a> tag of the toolbar button
	                css: 'clean',

	                // The text to use as the <a> tags "Alt" attribute value
	                text: 'Cancella il contenuto',

	                // The callback function to execute when the toolbar button is clicked
	                action: function (btn) {
	                    // 'this' = jHtmlArea object
	                    // 'btn' = jQuery object that represents the <a> ("anchor") tag for the toolbar button
						$("#W_NOTE_NOTA1").val('');
						$("#W_NOTE_NOTA1").htmlarea('updateHtmlArea');
	                    // Take some action or do something here
	                }
	            }
	        ]
			    ],
			
			    toolbarText: $.extend({}, jHtmlArea.defaultOptions.toolbarText, {
					"html": "Editor HTML/codice sorgente",
					"bold": "Grassetto",
			        "italic": "Corsivo",
			        "underline": "Sottolineato",
			        "strikethrough": "Barrato",
			        "increasefontsize": "Ingrandisci carattere",
			        "decreasefontsize": "Riduci carattere",
			        "orderedlist": "Elenco numerato",
			        "unorderedlist": "Elenco puntato",
			        "indent": "Aumenta rientro",
			        "outdent": "Riduci rientro",
			        "justifyleft": "Allinea testo a sinistra",
			        "justifycenter": "Centra",
			        "justifyright": "Allinea testo a destra",
			        "link": "Inserisci collegamento ipertestuale",
			        "unlink": "Rimuovi collegamento ipertestuale",
			        "image": "Inserisci immagine",
			        "horizontalrule": "Inserisci riga orizzontale",
			        "cut": "Taglia",
			        "copy": "Copia",
			        "paste": "Incolla"
			    })
			});
		$('#W_NOTE_NOTA2').htmlarea({
			toolbar: [
			["html"],
			["bold", "italic", "underline", "strikethrough"],
			        ["increasefontsize", "decreasefontsize"],
			        ["orderedlist", "unorderedlist"],
			        ["indent", "outdent"],
			        ["justifyleft", "justifycenter", "justifyright"],
			        ["link", "unlink", "image", "horizontalrule"],
			        ["cut", "copy", "paste"],
	        [
	            {
	                // The CSS class used to style the <a> tag of the toolbar button
	                css: 'clean',

	                // The text to use as the <a> tags "Alt" attribute value
	                text: 'Cancella il contenuto',

	                // The callback function to execute when the toolbar button is clicked
	                action: function (btn) {
	                    // 'this' = jHtmlArea object
	                    // 'btn' = jQuery object that represents the <a> ("anchor") tag for the toolbar button
						$("#W_NOTE_NOTA2").val('');
						$("#W_NOTE_NOTA2").htmlarea('updateHtmlArea');
	                    // Take some action or do something here
	                }
	            }
	        ]
			    ],
			
			    toolbarText: $.extend({}, jHtmlArea.defaultOptions.toolbarText, {
					"html": "Editor HTML/codice sorgente",
					"bold": "Grassetto",
			        "italic": "Corsivo",
			        "underline": "Sottolineato",
			        "strikethrough": "Barrato",
			        "increasefontsize": "Ingrandisci carattere",
			        "decreasefontsize": "Riduci carattere",
			        "orderedlist": "Elenco numerato",
			        "unorderedlist": "Elenco puntato",
			        "indent": "Aumenta rientro",
			        "outdent": "Riduci rientro",
			        "justifyleft": "Allinea testo a sinistra",
			        "justifycenter": "Centra",
			        "justifyright": "Allinea testo a destra",
			        "link": "Inserisci collegamento ipertestuale",
			        "unlink": "Rimuovi collegamento ipertestuale",
			        "image": "Inserisci immagine",
			        "horizontalrule": "Inserisci riga orizzontale",
			        "cut": "Taglia",
			        "copy": "Copia",
			        "paste": "Incolla"
			    })
			});
		$('#W_NOTE_NOTA3').htmlarea({
			toolbar: [
			["html"],
			["bold", "italic", "underline", "strikethrough"],
			        ["increasefontsize", "decreasefontsize"],
			        ["orderedlist", "unorderedlist"],
			        ["indent", "outdent"],
			        ["justifyleft", "justifycenter", "justifyright"],
			        ["link", "unlink", "image", "horizontalrule"],
			        ["cut", "copy", "paste"],
	        [
	            {
	                // The CSS class used to style the <a> tag of the toolbar button
	                css: 'clean',

	                // The text to use as the <a> tags "Alt" attribute value
	                text: 'Cancella il contenuto',

	                // The callback function to execute when the toolbar button is clicked
	                action: function (btn) {
	                    // 'this' = jHtmlArea object
	                    // 'btn' = jQuery object that represents the <a> ("anchor") tag for the toolbar button
						$("#W_NOTE_NOTA3").val('');
						$("#W_NOTE_NOTA3").htmlarea('updateHtmlArea');
	                    // Take some action or do something here
	                }
	            }
	        ]
			    ],
			
			    toolbarText: $.extend({}, jHtmlArea.defaultOptions.toolbarText, {
					"html": "Editor HTML/codice sorgente",
					"bold": "Grassetto",
			        "italic": "Corsivo",
			        "underline": "Sottolineato",
			        "strikethrough": "Barrato",
			        "increasefontsize": "Ingrandisci carattere",
			        "decreasefontsize": "Riduci carattere",
			        "orderedlist": "Elenco numerato",
			        "unorderedlist": "Elenco puntato",
			        "indent": "Aumenta rientro",
			        "outdent": "Riduci rientro",
			        "justifyleft": "Allinea testo a sinistra",
			        "justifycenter": "Centra",
			        "justifyright": "Allinea testo a destra",
			        "link": "Inserisci collegamento ipertestuale",
			        "unlink": "Rimuovi collegamento ipertestuale",
			        "image": "Inserisci immagine",
			        "horizontalrule": "Inserisci riga orizzontale",
			        "cut": "Taglia",
			        "copy": "Copia",
			        "paste": "Incolla"
			    })
			});
	});
	</script>
	<link rel="STYLESHEET" type="text/css" href="${pageContext.request.contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
</gene:redefineInsert>
</c:if>
<gene:redefineInsert name="pulsanteNuovo" />


<%/* Aggiungo i javascript per */%>
<c:set var="key" value="W_NOTE.TIPO=N:${param.tipoNota};W_NOTE.OGGETTO=T:${param.idObj}" scope="request" />
<gene:formScheda entita="W_NOTE" where="tipo = #W_NOTE.TIPO# and oggetto = #W_NOTE.OGGETTO# and prog = 0" tableClass="dettaglio-tab-help" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreW_NOTE" >
	<input type="hidden" name="${param.nameInput}" value="${param.idObj}" />
	<gene:campoScheda campo="TIPO" definizione="N1;1" title="tipo" visibile="false" defaultValue="${param.tipoNota}" />
	<gene:campoScheda campo="OGGETTO" definizione="T100;1" title="Oggetto" visibile="false" defaultValue="${param.idObj}" />	
	<gene:campoScheda campo="PROG" definizione="N7;1" title="Progressivo" visibile="false" defaultValue="0" />
	<c:choose>
		<c:when test="${param.tipoNota eq 2}">
		
			<gene:campoScheda >
				<td colspan="2"><b>Note ulteriori per visualizzazione</b></td>						
			</gene:campoScheda>
			<gene:campoScheda nome="W_NOTE_NOTA1">
					<td colspan="2">
						<c:choose>
							<c:when test='${modoAperturaScheda eq "VISUALIZZA"}' >
								${help.nota1}
								<br>
							</c:when>
							<c:otherwise>
								<input type="hidden" name="defW_NOTE_NOTA1" value="W_NOTE.NOTA1;T64000;N;" />
								<textarea id="W_NOTE_NOTA1" name="W_NOTE_NOTA1" 
										title="Nota ulteriore" 
										rows="15" cols="61">${help.nota1}</textarea>
								<br>
							</c:otherwise>
						</c:choose>
					</td>
					
			</gene:campoScheda>
			<gene:campoScheda >
				<td colspan="2"><b>Note ulteriori per inserimento</b></td>						
			</gene:campoScheda>
			<gene:campoScheda nome="W_NOTE_NOTA2">
					<td colspan="2">
						<c:choose>
							<c:when test='${modoAperturaScheda eq "VISUALIZZA"}' >
								${help.nota2}
								<br>
							</c:when>
							<c:otherwise>
								<input type="hidden" name="defW_NOTE_NOTA2" value="W_NOTE.NOTA2;T64000;N;" />
								<textarea id="W_NOTE_NOTA2" name="W_NOTE_NOTA2" 
										title="Nota ulteriore" 
										rows="15" cols="61">${help.nota2}</textarea><br>
							</c:otherwise>
						</c:choose>
					</td>
			</gene:campoScheda>
			<gene:campoScheda >
				<td colspan="2"><b>Note ulteriori per modifica</b></td>						
			</gene:campoScheda>
			<gene:campoScheda nome="W_NOTE_NOTA3">
					<td colspan="2">
						<c:choose>
							<c:when test='${modoAperturaScheda eq "VISUALIZZA"}' >
								${help.nota3}
								
							</c:when>
							<c:otherwise>
								<input type="hidden" name="defW_NOTE_NOTA3" value="W_NOTE.NOTA3;T64000;N;" />
								<textarea id="W_NOTE_NOTA3" name="W_NOTE_NOTA3" 
										title="Nota ulteriore" 
										rows="15" cols="61">${help.nota3}</textarea>
							</c:otherwise>
						</c:choose>
					</td>
			</gene:campoScheda>
			
		
		</c:when>
		<c:otherwise>
			<gene:campoScheda >
				<td colspan="2"><b>Note ulteriori</b></td>						
			</gene:campoScheda>
			<gene:campoScheda nome="W_NOTE_NOTA">
					<td colspan="2">
						<c:choose>
							<c:when test='${modoAperturaScheda eq "VISUALIZZA"}' >
								${help.nota}
							</c:when>
							<c:otherwise>
								<input type="hidden" name="defW_NOTE_NOTA" value="W_NOTE.NOTA;T64000;N;" />
								<textarea id="W_NOTE_NOTA" name="W_NOTE_NOTA" 
										title="Nota ulteriore" 
										rows="15" cols="61">${help.nota}</textarea>
							</c:otherwise>
						</c:choose>
					</td>
			</gene:campoScheda>
		</c:otherwise>
	</c:choose>
		<gene:campoScheda visibile='${fn:contains(listaOpzioniUtenteAbilitate, "ou59#")}' >
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${param.modo eq "MODIFICA"}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</c:when>
				<c:when test='${param.modo eq "NUOVO"}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:selezionaPagina(0)">
				</c:when>
				<c:otherwise>
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
</gene:formScheda>

<gene:javaScript>
	document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
	
	var schedaConfermaOld = schedaConferma;
	schedaConferma=function(){
		<c:choose>
			<c:when test="${param.tipoNota eq 2}">
				var lengthNote = $("#W_NOTE_NOTA1").val().length + $("#W_NOTE_NOTA2").val().length + $("#W_NOTE_NOTA3").val().length;
			</c:when>
			<c:otherwise>
				var lengthNote  = $("#W_NOTE_NOTA").val().length;
			</c:otherwise>
		</c:choose>
		if (lengthNote == 0) {
			// se salvo sbiancando le note allora torno al tab principale (non posso aprire un dettaglio se non ho i record)
			document.forms[0].activePage.value="0";
		}
		schedaConfermaOld();
	};
		
	
	schedaAnnulla=function(){
		document.forms[0].metodo.value="apri";
		document.forms[0].modo.value="VISUALIZZA";
		document.forms[0].activePage.value="1";
		document.forms[0].submit();
		};
	
</gene:javaScript>
