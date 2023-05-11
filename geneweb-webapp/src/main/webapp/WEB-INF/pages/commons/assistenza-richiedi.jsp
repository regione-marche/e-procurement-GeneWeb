
<%
  /*
			 * Created on 28-set-2012
			 *
			 * Copyright (c) EldaSoft S.p.A.
			 * Tutti i diritti sono riservati.
			 *
			 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
			 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
			 * aver prima formalizzato un accordo specifico con EldaSoft.
			 */

			// CONTIENE LA PAGINA CON LA FORM PER LA RICHIESTA DI ASSISTENZA
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template file="menuAzioni-template.jsp">
	<gene:historyClear/>
</gene:template>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript"
	src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/jquery.realperson.min.js"></script>
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/jquery/jquery.realperson.css">
<script type="text/javascript">
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />

  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;
 
  
  function hideSovrapposizioni() {
    //hideSubmenuNavbar();
    hideMenuPopup();
    hideSubmenuNavbar();
  }
  
  function invia() {
    var esito = true;

    if (esito && !controllaCampoInputObbligatorio(assistenzaForm.denominazioneEnte, 'Nominativo Ente/Amministrazione')){
      esito = false;
    }
    if (esito && !controllaCampoInputObbligatorio(assistenzaForm.nomeRichiedente, 'Referente (cognome e nome) da contattare')){
      esito = false;
    }
    if (esito && !controllaCampoInputObbligatorio(assistenzaForm.mailRichiedente, 'Email')){
        esito = false;
    }
	if (esito && !controllaEmail(document.assistenzaForm.mailRichiedente,'document.assistenzaForm.mailRichiedente')) {
        esito = false;
	}
    if (esito && !controllaCampoInputObbligatorio(assistenzaForm.oggetto, 'Tipologia di richiesta')){
        esito = false;
    }
    if (esito && !controllaCampoInputObbligatorio(assistenzaForm.captcha, 'Codice di controllo')){
        esito = false;
    }
    
    //ricavo informazioni sul cliente dell'utente
    var nVer = navigator.appVersion;
    var nAgt = navigator.userAgent;
    var browserName  = navigator.appName;
    var fullVersion  = ''+parseFloat(navigator.appVersion); 
    var majorVersion = parseInt(navigator.appVersion,10);
    var nameOffset,verOffset,ix;

    // In Opera, the true version is after "Opera" or after "Version"
    if ((verOffset=nAgt.indexOf("Opera"))!=-1) {
     browserName = "Opera";
     fullVersion = nAgt.substring(verOffset+6);
     if ((verOffset=nAgt.indexOf("Version"))!=-1) 
       fullVersion = nAgt.substring(verOffset+8);
    }
    // In MSIE, the true version is after "MSIE" in userAgent
    else if ((verOffset=nAgt.indexOf("MSIE"))!=-1) {
     browserName = "Microsoft Internet Explorer";
     fullVersion = nAgt.substring(verOffset+5);
    }
    // In Chrome, the true version is after "Chrome" 
    else if ((verOffset=nAgt.indexOf("Chrome"))!=-1) {
     browserName = "Chrome";
     fullVersion = nAgt.substring(verOffset+7);
    }
    // In Safari, the true version is after "Safari" or after "Version" 
    else if ((verOffset=nAgt.indexOf("Safari"))!=-1) {
     browserName = "Safari";
     fullVersion = nAgt.substring(verOffset+7);
     if ((verOffset=nAgt.indexOf("Version"))!=-1) 
       fullVersion = nAgt.substring(verOffset+8);
    }
    // In Firefox, the true version is after "Firefox" 
    else if ((verOffset=nAgt.indexOf("Firefox"))!=-1) {
     browserName = "Firefox";
     fullVersion = nAgt.substring(verOffset+8);
    }
    // In most other browsers, "name/version" is at the end of userAgent 
    else if ( (nameOffset=nAgt.lastIndexOf(' ')+1) < 
              (verOffset=nAgt.lastIndexOf('/')) ) {
     browserName = nAgt.substring(nameOffset,verOffset);
     fullVersion = nAgt.substring(verOffset+1);
     if (browserName.toLowerCase()==browserName.toUpperCase()) {
      browserName = navigator.appName;
     }
    }
    // trim the fullVersion string at semicolon/space if present
    if ((ix=fullVersion.indexOf(";"))!=-1)
       fullVersion=fullVersion.substring(0,ix);
    if ((ix=fullVersion.indexOf(" "))!=-1)
       fullVersion=fullVersion.substring(0,ix);

    majorVersion = parseInt(''+fullVersion,10);
    if (isNaN(majorVersion)) {
     fullVersion  = ''+parseFloat(navigator.appVersion); 
     majorVersion = parseInt(navigator.appVersion,10);
    }
    
    document.assistenzaForm.infoSystem.value= 'Browser name  = '+browserName+'\r\n'
    +'Full version  = '+fullVersion+'\r\n'
    +'Major version = '+majorVersion+'\r\n'
    +'navigator.appName = '+navigator.appName+'\r\n'
    +'navigator.userAgent = '+navigator.userAgent;

    if (esito){
      bloccaRichiesteServer();
      document.assistenzaForm.submit();
    }
  }
  
	function controllaEmail(unCampoDiInput,stringaCampo) {
		var esito = false;
		
		if (unCampoDiInput.value != "" && !isFormatoEmailValido(unCampoDiInput.value)) {
			alert("L'indirizzo email non e' sintatticamente valido.");
			if(ie4){
	  			unCampoDiInput.select();
  				unCampoDiInput.focus();
  			} else {
  			<%// Si e' dovuto differenziare il javascript per un bug
			// presente in Firefox 2.0 relativo all'esecuzione delle funzioni 
			// focus() e select() su un oggetto dopo all'evento onblur	del 
			// oggetto stesso%>
	 				setTimeout(stringaCampo + ".select()",125);
 					setTimeout(stringaCampo + ".focus()",125);
  			}
  		} else
  			esito = true;
  			
  		return esito;
	}
	
	// connette il plugin per la gestione captcha al campo effettivo
	$(document).ready(function(){
		$("#captcha").realperson({regenerate: 'Cambia immagine'});
		$("#captcha").css("text-transform","uppercase");
	});
  
-->
</script>
<jsp:include page="/WEB-INF/pages/commons/jsSubMenuComune.jsp" />
<jsp:include page="/WEB-INF/pages/commons/jsSubMenuSpecifico.jsp" />
<BODY onload="setVariables();checkLocation();initPage();">
	<TABLE class="arealayout">
		<!-- questa definizione dei gruppi di colonne serve a fissare la dimensione
	     dei td in modo da vincolare la posizione iniziale del menù di navigazione
	     sopra l'area lavoro appena al termine del menù contestuale -->
		<colgroup width="150px"></colgroup>
		<colgroup width="*"></colgroup>
		<TBODY>
			<TR class="testata">
				<TD colspan="3"><jsp:include
						page="/WEB-INF/pages/commons/testata.jsp" /></TD>
			</TR>
			<TR class="menuprincipale">
				<TD><img src="${contextPath}/img/spacer-azionicontesto.gif"
					alt=""></TD>
				<c:choose>
					<c:when test="${! empty sessionScope.profiloUtente}">
						<TD>
							<table class="contenitore-navbar">
								<tbody>
									<tr>
										<c:if test="${! empty sessionScope.profiloUtente && ! empty profiloAttivo}">
											<jsp:include page="/WEB-INF/pages/commons/menuSpecifico.jsp" />
											<jsp:include page="/WEB-INF/pages/commons/menuComune.jsp" />
										</c:if>
									</tr>
								</tbody>
							</table> <!-- PARTE NECESSARIA PER VISUALIZZARE I SOTTOMENU DEL MENU PRINCIPALE DI NAVIGAZIONE -->
							<iframe id="iframesubnavmenu" class="gene"></iframe>
							<div id="subnavmenu" class="subnavbarmenuskin"
								onMouseover="highlightSubmenuNavbar(event,'on');"
								onMouseout="highlightSubmenuNavbar(event,'off');"></div>
						</TD>
					</c:when>
					<c:otherwise>
						<TD>&nbsp;</TD>
					</c:otherwise>
				</c:choose>
			</TR>
			<TR>
				<TD class="menuazioni" valign="top">
					<div id="menulaterale"></div>
				</TD>
				<TD class="arealavoro"><c:if
						test="${! empty sessionScope.profiloUtente}">
						<jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" />
					</c:if>

					<div class="contenitore-arealavoro">

						<div class="titolomaschera">Richiesta di assistenza</div>

						<div class="contenitore-errori-arealavoro">
							<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
						</div>

						<div class="contenitore-dettaglio">

							<html:form action="/InviaRichiestaAssistenza" enctype="multipart/form-data">
								<input type="hidden" name="infoSystem" />
								<table class="dettaglio-notab">
									<tr>
										<td colspan="2">
											<div class="info-wizard">
												<p>Benvenuto nella pagina in cui puoi inoltrare una
													richiesta di assistenza per questo prodotto.</p>
												<p>Nel modulo sottostante vanno compilati almeno i campi
													obbligatori, marcati con il carattere asterisco (*).
													Indicare la denominazione dell'ente o dell'amministrazione
													a cui si appartiene, il proprio nome e cognome, l'indirizzo
													mail, il telefono, quindi selezionare la tipologia di
													richiesta di assistenza dalla lista, ed indicare una
													eventuale descrizione aggiuntiva nell'area di testo oppure allegare un file. Al
													termine della compilazione premere il pulsante "Invia
													richiesta".</p>
											</div></td>
									</tr>
									<tr>
										<td class="etichetta-dato">Nominativo
											Ente/Amministrazione (*)</td>
										<td class="valore-dato">
											<html:text property="denominazioneEnte" size="40" styleClass="testo" />
										</td>
									</tr>
									<tr>
										<td class="etichetta-dato">Referente (cognome e nome) da
											contattare (*)</td>
										<td class="valore-dato">
											<html:text property="nomeRichiedente" size="40" styleClass="testo" />
										</td>
									</tr>
									<tr>
										<td class="etichetta-dato">Email (*)</td>
										<td class="valore-dato">
											<html:text property="mailRichiedente" size="40" styleClass="testo" />
										</td>
									</tr>
									<tr>
										<td class="etichetta-dato">Telefono</td>
										<td class="valore-dato">
											<html:text property="telefonoRichiedente" size="40" styleClass="testo" />
										</td>
									</tr>
									<tr>
										<td class="etichetta-dato">Tipologia di richiesta (*)</td>
										<td class="valore-dato"><html:select property="oggetto">
												<html:option value="">--- Seleziona una tipologia di richiesta ---</html:option>
												<html:options name="oggetti" labelName="oggetti" />
											</html:select></td>
									</tr>
									<tr>
										<td class="etichetta-dato">Descrizione</td>
										<td class="valore-dato">
											<html:textarea property="testo" cols="40" rows="5" />
										</td>
									</tr>
									<tr>
										<td class="etichetta-dato">Allega un file</td>
										<td class="valore-dato">
											<input type="file" name="selezioneFile" size="55" onkeydown="return bloccaCaratteriDaTastiera(event);">
										</td>
									</tr>
									<tr>
										<td class="etichetta-dato">Codice di controllo (*)</td>
										<td class="valore-dato" valign="middle">
											<html:text property="captcha" size="40" styleClass="testo" styleId="captcha" />
										</td>
									</tr>
									<tr>
										<td class="comandi-dettaglio" colSpan="2">
											<INPUT type="button" class="bottone-azione" value="Invia" title="Invia il modulo di richiesta" onClick="javascript:invia();" /> &nbsp;
											<c:if test="${empty sessionScope.profiloUtente}">
												&nbsp;&nbsp;&nbsp;
												<INPUT type="button" class="bottone-azione" value="Indietro" title="Torna alla pagina di login" onClick="document.location.href='${contextPath}';" />&nbsp;
											</c:if>
										</td>
								</table>
							</html:form>
						</div>

					</div></TD>
			</TR>

			<TR>
				<TD COLSPAN="2">
					<div id="footer">
						<jsp:include page="/WEB-INF/pages/commons/footer.jsp" />
					</div>
				</TD>
			</TR>

		</TBODY>
	</TABLE>

</BODY>
</HTML>
