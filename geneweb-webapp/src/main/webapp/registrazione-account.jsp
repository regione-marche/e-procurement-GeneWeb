<%/*
       * Created on 23/09/2014
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="result" value="${gene:callFunction('it.eldasoft.gene.tags.functions.GetParametriRegistrazioneUtenteFunction',pageContext)}"/>

<gene:template file="scheda-nomenu-template.jsp">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.realperson.min.js"></script>

		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/ui/std/jquery-ui.css" />
		
		<style type="text/css">
			label.error {
				float: none;
				color: white;
 				background-color: #E40000; 
				padding-left: 5px;
				padding-right: 5px;
				padding-top: 2px;
				padding-bottom: 2px;
				vertical-align: middle;
				margin-left: 5px;
 				border: 1px solid #BA0000; 
 				-moz-border-radius-topleft: 4px; 
 				-webkit-border-top-left-radius: 4px; 
 				-khtml-border-top-left-radius: 4px; 
 				border-top-left-radius: 4px; 
				-moz-border-radius-topright: 4px;
				-webkit-border-top-right-radius: 4px;
				-khtml-border-top-right-radius: 4px;
				border-top-right-radius: 4px;
 				-moz-border-radius-bottomleft: 4px; 
 				-webkit-border-bottom-left-radius: 4px; 
 				-khtml-border-bottom-left-radius: 4px; 
 				border-bottom-left-radius: 4px; 
				-moz-border-radius-bottomright: 4px;
				-webkit-border-bottom-right-radius: 4px;
				-khtml-border-bottom-right-radius: 4px;
				border-bottom-right-radius: 4px;
			}
		 
			.error {
				color:Red;
			}
			
			.realperson-challenge {
				display: inline-block;
				vertical-align: bottom;
				color: #000;
				padding-right: 5px;
			}
			.realperson-text {
				font-family: "Courier New",monospace;
				font-size: 5px;
				font-weight: bold;
				letter-spacing: -1px;
				line-height: 2px;
			}
			.realperson-regen {
				padding-top: 4px;
				font-size: 10px;
				text-align: left;
				padding-left: 10px;
				cursor: pointer;
				font-style: italic;
				color: #454545;
			}
			.realperson-disabled {
				opacity: 0.75;
				filter: Alpha(Opacity=75);
			}
			.realperson-disabled .realperson-regen {
				cursor: default;
			}
			
			.ui-autocomplete {
				max-height: 200px;
				overflow-y: auto;
				overflow-x: hidden;
				max-width: 700px;
			}
			
			.ui-corner-all, .ui-corner-top, .ui-corner-left, .ui-corner-tl { -moz-border-radius-topleft: 0px; -webkit-border-top-left-radius: 0px; -khtml-border-top-left-radius: 0px; border-top-left-radius: 0px; }
			.ui-corner-all, .ui-corner-top, .ui-corner-right, .ui-corner-tr { -moz-border-radius-topright: 0px; -webkit-border-top-right-radius: 0px; -khtml-border-top-right-radius: 0px; border-top-right-radius: 0px; }
			.ui-corner-all, .ui-corner-bottom, .ui-corner-left, .ui-corner-bl { -moz-border-radius-bottomleft: 0px; -webkit-border-bottom-left-radius: 0px; -khtml-border-bottom-left-radius: 0px; border-bottom-left-radius: 0px; }
			.ui-corner-all, .ui-corner-bottom, .ui-corner-right, .ui-corner-br { -moz-border-radius-bottomright: 0px; -webkit-border-bottom-right-radius: 0px; -khtml-border-bottom-right-radius: 0px; border-bottom-right-radius: 0px; }
			.ui-state-hover, .ui-widget-content .ui-state-hover, .ui-widget-header .ui-state-hover, .ui-state-focus, .ui-widget-content .ui-state-focus, .ui-widget-header .ui-state-focus { border: 1px solid #fbcb09; background: #fdf5ce; font-weight: bold; color: #AD3600; }

		</style>
		
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value='Registrazione utente'/>

	<gene:redefineInsert name="corpo">
	<c:choose>
	<c:when test='${requestScope.appBloccata eq "1"}'>
		<div class="errori-javascript-dettaglio">
			<ul>
			<html:messages id="error" message="true" property="error">
				<br>
				<li class="errori-javascript-err">ATTENZIONE:
				<small>Applicazione non ancora attivata</small></li>
				<br>
			</html:messages>
			</ul>
		</div>
	</c:when>
	<c:otherwise>
		<gene:formScheda entita="USRSYS" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreFormRegistrazione" >
			<gene:campoScheda>
				<td colspan="2"><br><b>Dati anagrafici dell'utente</b></td>
			</gene:campoScheda>
			<gene:campoScheda title="Codice dell'anagrafico" campo="CODICE"  campoFittizio="true" definizione="T10;;;;" modificabile='${modoAperturaScheda eq "NUOVO"}' visibile="false" />
			<gene:campoScheda campo="NOME" campoFittizio="true" definizione="T80;;;;" title="Nome" obbligatorio="true" modificabile="${empty requestScope.nome || empty requestScope.cognome}" value="${requestScope.nome}"/>
			<gene:campoScheda campo="COGNOME" campoFittizio="true" definizione="T80;;;;" title="Cognome" obbligatorio="true" modificabile="${empty requestScope.nome || empty requestScope.cognome}" value="${requestScope.cognome}"/>
			<gene:campoScheda campo="TELEFONO" campoFittizio="true" definizione="T50;;;;" title="Telefono"/>
			<gene:campoScheda campo="EMAIL" campoFittizio="true" definizione="T100;;;;" title="E-mail" obbligatorio="true" value="${requestScope.email}"/>
			
			<gene:campoScheda>
				<td colspan="2"><br><b>Registrazione</b></td>
			</gene:campoScheda>
			<c:choose>
				<c:when test='${requestScope.isLoginCF eq "1"}'>
					<c:set var="titleCF" value="Codice Fiscale/Login"/>
					<c:set var="visLogin" value="false"/>
				</c:when>
				<c:otherwise>
					<c:set var="titleCF" value="Codice Fiscale"/>
					<c:set var="visLogin" value="true"/>
				</c:otherwise>
			</c:choose>
			<gene:campoScheda campo="LOGIN" campoFittizio="true" definizione="T16;;;;" title="Login" obbligatorio="true" visibile="${visLogin}" modificabile="${empty requestScope.login}" value="${requestScope.login}" />
			<gene:campoScheda campo="CODFISC" campoFittizio="true" definizione="T16;;;;" title="${titleCF}" obbligatorio="true" visibile='${requestScope.flagLdap ne "3" || !(requestScope.isLoginCF eq "1")}' modificabile="${empty requestScope.codfisc}" value="${requestScope.codfisc}"/>
			<gene:campoScheda campo="SYSCON" visibile="false"/>
			<gene:campoScheda campo="SYSUTE" visibile="false"/>
			<gene:campoScheda campo="SYSNOM" visibile="false" title="Login" entita="USRSYS" where="usrsys.syscon = utent.syscon"/>
			
			<c:choose>
				<c:when test='${requestScope.flagLdap ne "1" && requestScope.flagLdap ne "3"}'>
					<gene:campoScheda campo="SYSPWD" visibile="false" entita="USRSYS" where="usrsys.syscon = utent.syscon" />
					<gene:campoScheda>	
						<td class="etichetta-dato" >Password (*)</td>
						<td class="valore-dato">
							<input type="password" id="password" name="password" styleClass="testo" maxlength="30" size="20" />
							<span style="vertical-align: middle;"><i>&nbsp;(minino 8 caratteri, di cui 2 cifre)</i></span>
						</td>
					</gene:campoScheda>
					<gene:campoScheda>
						<td class="etichetta-dato" >Conferma password (*)</td>
						<td class="valore-dato"> 
							<input type="password" id="confPassword" name="confPassword" styleClass="testo" maxlength="30" size="20" />
						</td>
					</gene:campoScheda>
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="SYSPWD" visibile="false" entita="USRSYS" where="usrsys.syscon = utent.syscon" value=""/>
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="FLAG_LDAP" value="${requestScope.flagLdap}" visibile="false"/>
			<gene:campoScheda campo="DN" campoFittizio="true" definizione="T250;;;;" title="DN" value="${requestScope.dn}" visibile="false"/>
			
			<c:choose>
				<c:when test='${requestScope.ruolo eq "0" }'>
				<gene:campoScheda campo="SYSAB3" title="Ruolo" obbligatorio="true" visibile="false" entita="USRSYS" where="usrsys.syscon = utent.syscon" value="U" />
				</c:when>
				<c:when test='${requestScope.ruolo eq "1" }'>
				<gene:campoScheda campo="SYSAB3" title="Ruolo" obbligatorio="true" entita="USRSYS" where="usrsys.syscon = utent.syscon">
					<gene:addValue value="" descr="" />
					<c:if test='${!empty listaValoriRuolo}'>
						<c:forEach items="${listaValoriRuolo}" var="valoriRuolo">
						<gene:addValue value="${valoriRuolo[0]}"
							descr="${valoriRuolo[1]}" />
						</c:forEach>
					</c:if>
				</gene:campoScheda>			
				</c:when>
				<c:when test='${requestScope.ruolo eq "2" }'>
				<gene:campoScheda campo="SYSABG" title="Ruolo" obbligatorio="true" entita="USRSYS" where="usrsys.syscon = utent.syscon">
					<gene:addValue value="" descr="" />
					<c:if test='${!empty listaValoriRuolo}'>
						<c:forEach items="${listaValoriRuolo}" var="valoriRuolo">
						<gene:addValue value="${valoriRuolo[0]}"
							descr="${valoriRuolo[1]}" />
						</c:forEach>
					</c:if>
				</gene:campoScheda>			
				</c:when>
				<c:when test='${requestScope.ruolo eq "3" }'>
				<gene:campoScheda campo="SYSABC" title="Ruolo" obbligatorio="true" entita="USRSYS" where="usrsys.syscon = utent.syscon">
					<gene:addValue value="" descr="" />
					<c:if test='${!empty listaValoriRuoloContratti}'>
						<c:forEach items="${listaValoriRuoloContratti}" var="valoriRuoloContratti">
						<gene:addValue value="${valoriRuoloContratti[0]}"
							descr="${valoriRuoloContratti[1]}" />
						</c:forEach>
					</c:if>
				</gene:campoScheda>			
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
			
		
			
			<c:if test='${!empty listaProfiliDisponibili}'>
				<gene:campoScheda>
					<td colspan="2"><br><b>Applicativi disponibili</b></td>
				</gene:campoScheda>
				<gene:campoScheda>
					<td class="etichetta-dato">Applicativi (*)</td>
					<td class="valore-dato">
						<c:forEach items="${listaProfiliDisponibili}" step="1" var="valoriProfiliDisponibili" varStatus="ciclo" >
							<input style="vertical-align: middle;" type="checkbox" name="applicativi" value="${valoriProfiliDisponibili[0]}"/>
							<span style="vertical-align: middle;">${valoriProfiliDisponibili[1]}</span>
							<p style="margin-top:0px; padding-left: 24px;"><i>${valoriProfiliDisponibili[2]}</i></p>
						</c:forEach>
						<div style="margin-bottom: 5px;" id="messaggioApplicativi"></div>
					</td>
				</gene:campoScheda>
			</c:if>

			<gene:campoScheda>
				<td colspan="2"><br><b>Ulteriori indicazioni</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="MSGAMM" campoFittizio="true" definizione="T2000;0;;NOTE;" title="Messaggio per l'amministratore" />
			
			
			<c:if test='${requestScope.isUffintAbilitati eq "1" }'>
				<gene:campoScheda>
					<td colspan="2"><br><b>Ente</b></td>
				</gene:campoScheda>
				<gene:campoScheda entita="UFFINT" campo="CODEIN" /> 
				<gene:campoScheda title="Codice fiscale" entita="UFFINT" campo="CFEIN" obbligatorio="true"/>
				<gene:campoScheda addTr="false">
	
				<tr id="rowUFFINT_AVVISO">
					<td colspan="2">
						<br>
						<i>Non esiste alcun ente con il codice fiscale o la partita IVA indicate, valorizzare anche i campi seguenti</i>
						<br>
					</td>
				</tr>
				
				</gene:campoScheda>			
				<gene:campoScheda addTr="false">
					<tr id="rowVIS_UFFINT_NOMEIN">
						<td class="etichetta-dato">Denominazione</td>
						<td class="valore-dato"> 
							<div id="VIS_UFFINT_NOMEIN"></div>
						</td>
					</tr>
				</gene:campoScheda>
				<gene:campoScheda entita="UFFINT" campo="NOMEIN" obbligatorio="true" />
			</c:if>
			
			
			
			<c:if test='${!empty requestScope.isModelloFacSimile}' >
			<gene:campoScheda>
			<td colspan="2">
				<br>
				<b>Scarica il modello di abilitazione al servizio</b>
				<br>
				Per completare la registrazione &egrave; necessario scaricare il presente
				<c:if test='${!empty requestScope.isModelloFacSimile}' >
					<a class="link-229" href="javascript:apriModelloAbilitazioneServizio();" >
				</c:if>
				modello di abilitazione al servizio</a> , compilarlo, firmarlo digitalmente e <br>
				allegarlo alla presente scheda di registrazione (vedi "Allega documenti").
			</td>
			</gene:campoScheda> 
			<gene:campoScheda nome="ALLDOC">
				<td colspan="2">
				<br>
				<b>Allega documenti</b>
				<br>
				Nel caso in cui siano presenti pi&ugrave; documenti &egrave; necessario archiviarli in un unico file (.zip).
				<i><br>Utilizza il pulsante "Scegli file" per caricare i documenti</i>
				</td>
			</gene:campoScheda>
				<gene:campoScheda campo="NOMEDOC" campoFittizio="true" definizione="T50" title="Documento caricato" modificabile="true" visibile="false" />
				<gene:campoScheda title="Nome file" visibile="true">
					<input type="file" name="selezioneFile" id="selezioneFile" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile();'/>
			</gene:campoScheda>
			</c:if>

			<gene:campoScheda> 
				<td colspan="2">
					<br>
					<b>Informativa trattamento dati personali ai sensi del D.Lgs. n. 196/2003</b>
					<br>
					Ai sensi dell'art. 13 del D. Lgs. n. 196/2003 (ex art. 10 della legge n. 675/96), si informa che i dati personali 
					forniti ed acquisiti contestualmente alla registrazione ai servizi scelti, nonche' i dati necessari 
					all'erogazione di tali servizi, saranno trattati, nel rispetto delle garanzie di riservatezza e delle misure di sicurezza 
			        previste dalla normativa vigente attraverso strumenti informatici, telematici e manuali, con logiche strettamente 
			        correlate alle finalita' del trattamento.
			       	<br>	
			       	<br>		       					
					<c:if test="${not empty moduloCondizioniDuso}">
						Dichiaro di aver preso visione e di accettare le condizioni d'uso del sito web. <br>
						(Cliccare <b><a href="${pageContext.request.contextPath}/${moduloCondizioniDuso}" class="link-generico" title="Condizioni d'uso" target="_blank">qui</a></b>
						per scaricare le condizioni d'uso)
						<br>
						<br>
					</c:if>	
					<span style="vertical-align: middle;">Accetto (*)&nbsp;</span><input style="vertical-align: middle;" type="checkbox" name="informativaPrivacy" id="informativaPrivacy" title="Accetto le condizioni d'uso"/>
			 	</td>				
			</gene:campoScheda>	
			
			<gene:campoScheda>
				<td colspan="2"><br><b>Dimostra di non essere un robot</b></td>
			</gene:campoScheda>
			<gene:campoScheda>
				<td class="etichetta-dato">Codice di controllo (*)</td>
				<td class="valore-dato">
					<input type="text" id="realperson" name="realperson" maxlength="15" size="15" />&nbsp;
				</td>
			</gene:campoScheda>
			
			
			<gene:campoScheda>	
				<td colSpan="2">
					<i><br>(*) Campi obbligatori</i>
				</td>
			</gene:campoScheda>
			<gene:campoScheda>	
				<td class="comandi-dettaglio" colSpan="2">
					<gene:insert name="pulsanteSalva">
						<INPUT type="button" class="bottone-azione" value="Registra" title="Registra" onclick="javascript:eseguiSubmit()">
					</gene:insert>
					<gene:insert name="pulsanteAnnulla">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla registrazione" onclick="javascript:annullaScheda()">
					</gene:insert>
					&nbsp;
				</td>
			</gene:campoScheda>
			<gene:fnJavaScriptScheda funzione='upperCase("UFFINT_CFEIN", "#UFFINT_CFEIN#")' elencocampi='UFFINT_CFEIN' esegui="false" />
			<gene:fnJavaScriptScheda funzione='upperCase("CODFISC", "#CODFISC#")' elencocampi='CODFISC' esegui="false" />
		</gene:formScheda>
	</c:otherwise>
	</c:choose>
		

	</gene:redefineInsert>

	<gene:javaScript>
		
		
		
		document.forms[0].encoding="multipart/form-data";
		
			var changeUFFINT = false;	

			document.getElementById("NOME").size= 25;			
			document.getElementById("COGNOME").size= 25;
			document.getElementById("LOGIN").size= 20;
			document.getElementById("TELEFONO").size= 35;
			document.getElementById("EMAIL").size= 50;
			<c:if test='${requestScope.isUffintAbilitati eq "1" }'>
				document.getElementById("UFFINT_NOMEIN").cols= 55;
				document.getElementById("UFFINT_CFEIN").size= 20;
			</c:if>				
			
			
			function gestioneAction() {
				var nuovaAction = contextPath + "/SchedaNoSessione.do?" + csrfToken;
				document.forms[0].action = nuovaAction;
				nuovaAction = "commons/redirect.jsp";
				document.forms[0].jspPathTo.value = nuovaAction;
			}
			
			
			function gestioneSezioneUFFINT(visibile) {
				if (visibile == true) {
					$("#rowUFFINT_AVVISO").show();
					$("#rowUFFINT_NOMEIN").show();
				} else {
					$("#rowUFFINT_AVVISO").hide();
					$("#rowUFFINT_NOMEIN").hide();
					$("#UFFINT_NOMEIN").val("");
				}
			};
			
			
		
			$(document).ready(function()
			{
				$("#CODFISC").css("text-transform","uppercase");
				$("#UFFINT_CFEIN").css("text-transform","uppercase");
				$('#UFFINT_CFEIN').change(function() {
				  getDescrizioneEnte("");
				});
				$('#UFFINT_CFEIN').keyup(function() {
				  getDescrizioneEnte("");
				});
				
				$("#rowUFFINT_CODEIN").hide();							
			
				$("#realperson").css("text-transform","uppercase");
				$("#realperson").realperson({length: 6, regenerate: 'Rigenera codice'});

				jQuery.validator.addMethod("isSelectUffint",
					function(value, element) {return isSelectUffint(value);},
					"Selezionare un valore");

				jQuery.validator.addMethod("controlloCFPIVA",
				function(value, element) {
					return checkCodFis(value);
				},
				"Il valore specificato non è valido");
				
				jQuery.validator.addMethod("isLoginInesistente",
					function(value, element) {return !isLoginEsistente(value);},
					"Login esistente");
								
				jQuery.validator.addMethod("isPasswordCaratteriAmmessi",
					function(value, element) {return isPasswordCaratteriAmmessi(value);},
					"La password contiene caratteri non ammessi");	

				jQuery.validator.addMethod("isPasswordMinimo2Numerici",
					function(value, element) {return isPasswordMinimo2Numerici(value);},
					"La password deve contenere almeno 2 caratteri numerici");
			
				jQuery.validator.addMethod("passwordSimilarityNOME",
					function(value, element) {return !passwordSimilarity(value,$("#NOME").val());},
					"Password simile al nome dell'utente");

				jQuery.validator.addMethod("passwordSimilarityCOGNOME",
					function(value, element) {return !passwordSimilarity(value,$("#COGNOME").val());},
					"Password simile al cognome dell'utente");
					
				jQuery.validator.addMethod("passwordSimilarityCODFISC",
					function(value, element) {return !passwordSimilarity(value,$("#CODFISC").val());},
					"Password simile al codice fiscale / login di registrazione");
				
				
				jQuery.validator.addMethod("isCodiceControlloCorretto",
					function(value, element) {return isCodiceControlloCorretto();},
				"Il codice di controllo non e' corretto");
				
			    $("form[name^='formScheda']").validate(
			    {
			        rules:
			        {
			            NOME: "required",
			            COGNOME: "required",
			            LOGIN: 
		            	{
		            		required: true,
		            		isLoginInesistente: true
		            	},
			            CODFISC: 
		            	{
		            		required: true,
		            		minlength: 16,
		            		controlloCFPIVA: true,
		            		isLoginInesistente: true
		            	},
			            UFFINT_NOMEIN: "required",
			            UFFINT_CFEIN:
			            {
							required: true,
			            	controlloCFPIVA: true,
			            	isSelectUffint: true
			            },
			            password:
			            {
			            	required: true,
			            	isPasswordCaratteriAmmessi: true,
			            	minlength: 8,
			            	isPasswordMinimo2Numerici: true,
			            	passwordSimilarityNOME: true,
			            	passwordSimilarityCOGNOME: true,
			            	passwordSimilarityCODFISC: true
			            },
			            confPassword:
			            {
			            	required: true,
			            	equalTo: "#password"
			            },
			            EMAIL:
			            {
			                required: true,
			                email: true
			            },
			            USRSYS_SYSAB3:
			            {
			                required: true
			            },
			            USRSYS_SYSABG:
			            {
			                required: true
			            },
			            USRSYS_SYSABC:
			            {
			                required: true
			            },
			            applicativi:
			            {
			                required: true
			            },
			            
						selezioneFile:
			            {
			                required: true
			            },
			            informativaPrivacy:
			            {
			            	required: true
			            },
   			            realperson:
			            {
			            	required: true,
			            	minlength: 6,
			            	isCodiceControlloCorretto: true
			            }	            
			        },
			        messages:
			        {
			            NOME: "Inserire il nome",
			            COGNOME: "Inserire il cognome",
			            LOGIN:
			            {
			            	required: "Inserire la login",
			            	isLoginInesistente: "La login indicata e' gia' utilizzata"
			            },
			            CODFISC:
			            {
			            	required: "Inserire il codice fiscale",
			            	controlloCFPIVA: "Il valore specificato non rispetta il formato previsto",
			            	isLoginInesistente: "Il codice fiscale indicato e' gia' utilizzato"
			            },
			            UFFINT_NOMEIN: "Inserire la denominazione",
			            UFFINT_CFEIN:
			           	{
			            	required: "Inserire il codice fiscale o la partita IVA",
			            	controlloCFPIVA: "Il valore specificato non rispetta il formato previsto",
			            	isSelectUffint: "Selezionare un valore dalla lista"
							
			            },
			            password: 
			            {
			            	required: "Inserire la password",
			            	minlength: "La password deve essere lunga almeno 8 caratteri" 
			            },
			            confPassword: 
		            	{
		            	 	required: "Confermare la password",
		            		equalTo: "Le due password non coincidono" 	
		            	},
			            EMAIL:
			            {
			            	required: "Inserire l'indirizzo e-mail",
			            	email: "Inserire un indirizzo e-mail valido"
			            },
			            USRSYS_SYSAB3:
			            {
			            	required: "Inserire il ruolo"
			            },
			            USRSYS_SYSABG:
			            {
			            	required: "Inserire il ruolo"
			            },
			            USRSYS_SYSABC:
			            {
			            	required: "Inserire il ruolo"
			            },
			            applicativi:
			            {
			            	required: "Scegliere almeno un applicativo tra quelli proposti"
			            },
			            selezioneFile:
						{
			            	required: "Inserire i documenti richiesti"
			            },
			            informativaPrivacy:
			            {
			            	required: "Per procedere e' necessario accettare le condizioni"
			            },
			            realperson:
			            {
			            	required: "Digitare il codice di controllo",
			            	minlength: "Digitare tutti i 6 caratteri del codice di controllo"
			            }
			        },
			        errorPlacement: function(error, element) {
				    	if (element.attr("name") == "applicativi") {
				       		error.appendTo( $("#messaggioApplicativi") );
				       	} else {
				       		error.insertAfter(element);
				       	}
					}
				});
			});		
					
		
			function upperCase(campo, valore){
				document.getElementById(campo).value=valore.toUpperCase();
			}
						
			function annullaScheda(){
			
			<c:choose>
				<c:when test='${requestScope.flagLdap ne "3" }'>
					window.location.href="InitLogin.do";
				</c:when>
				<c:otherwise>
					document.location.href="SessionTimeOut.do";
				</c:otherwise>
			</c:choose>
				
			}

			function eseguiSubmit(){
				if ($("form[name^='formScheda']").validate().form()) {
					var eseguiSubmit = false;
					var isLoginCF = false;
					<c:if test='${requestScope.isLoginCF eq "1"}'>
			    		isLoginCF = true;
			    	</c:if>
					<c:choose>
						<c:when test='${requestScope.flagLdap ne "1" && requestScope.flagLdap ne "3"  }'>
							if (controllaCampoPassword(document.forms[0].password,8,true)) {
								document.forms[0].USRSYS_SYSPWD.value = document.forms[0].password.value;
								eseguiSubmit = true;
							}
						</c:when>
						<c:otherwise>
								eseguiSubmit = true;
						</c:otherwise>
					</c:choose>
					 if(eseguiSubmit==true){
					   	if(isLoginCF){
					 		document.forms[0].LOGIN.value = document.forms[0].CODFISC.value;
					 	}
						document.forms[0].USRSYS_SYSNOM.value = document.forms[0].LOGIN.value;
						document.forms[0].metodo.value="update";
						if(activeForm.onsubmit()){
							bloccaRichiesteServer();
							document.forms[0].submit();
						}
					 }
				}
			}
			
			function apriModelloAbilitazioneServizio(){
				var w = 700;
				var h = 500;
				var l = Math.floor((screen.width-w)/2);
				var t = Math.floor((screen.height-h)/2);
				document.location.href='ModelloRegistrazione.do?'+csrfToken+'&metodo=download';
			}
			
			
			function gestioneSezioneUFFINT(visibile) {
				if (visibile == false) {
					$("#rowUFFINT_AVVISO").hide();
					$("#rowUFFINT_NOMEIN").hide();
				}
			};

			
			function scegliFile() {
				var selezioneFile = document.getElementById("selezioneFile").value;
				var lunghezza_stringa=selezioneFile.length;
				var posizione_barra=selezioneFile.lastIndexOf("\\");
				var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
				setValue("NOMEDOC",nome);
			}
			
			function getDescrizioneEnte(valore) {
					var result = false;
	                var cfein = $("#UFFINT_CFEIN").val();
	                if (cfein != "" && checkCodFis(cfein)) {
						$.ajax({
							type: "GET",
							dataType: "json",
							/*async: false,*/
							beforeSend: function(x) {
							if(x && x.overrideMimeType) {
								x.overrideMimeType("application/json;charset=UTF-8");
							   }
							},
							url: "${pageContext.request.contextPath}/GetDescrizioneEnte.do",
							data: "cfein=" + cfein,
							success: function(data){
								if (data.enteEsistente == true) {
									$("#VIS_UFFINT_NOMEIN").html(data.NOMEIN.value);
									$("#rowVIS_UFFINT_NOMEIN").show();
									$("#UFFINT_CODEIN").val(data.CODEIN.value);
									gestioneSezioneUFFINT(false);
									result = true;
								} else {
									$("#VIS_UFFINT_NOMEIN").html("");
									$("#rowVIS_UFFINT_NOMEIN").hide();
									$("#UFFINT_CODEIN").val("");
									gestioneSezioneUFFINT(true);
								}
							},
							error: function(e){
								alert("Ente: errore durante la lettura delle informazioni");
							}
						});
					} else {
						$("#VIS_UFFINT_NOMEIN").html("");
						$("#rowVIS_UFFINT_NOMEIN").hide();
						$("#UFFINT_CODEIN").val("");
					}
					return result;
			}

				if ($("#UFFINT_CFEIN").val() != "") {
					getDescrizioneEnte("");
				} else {
					$("#rowVIS_UFFINT_NOMEIN").hide();
					gestioneSezioneUFFINT(false);
				}
				
				
			function isSelectUffint(cfein) {
					return changeUFFINT;
			}				
				
			function isLoginEsistente(login) {
					var isLoginEsistente = false;
	                $.ajax({
	                    type: "GET",
	                    dataType: "json",
	                    async: false,
	                    beforeSend: function(x) {
	        			if(x && x.overrideMimeType) {
	            			x.overrideMimeType("application/json;charset=UTF-8");
					       }
	    				},
	                    url: "${pageContext.request.contextPath}/IsLoginEsistente.do",
	                    data: "login=" + login,
	                    success: function(data){
	                    	if (data.loginEsistente == true) {
	                        	isLoginEsistente = true;
	                        } else {
	                        	isLoginEsistente = false;
	                        }
	                    },
	                    error: function(e){
	                        alert("Codice fiscale / login: errore durante il controllo di univocita'");
	                    }
	                });
	              return isLoginEsistente;
	        }

			
			
			function isPasswordCaratteriAmmessi(password){
					var caratteriAmmessi = " ~#\"$%&'()*+,-./0123456789:;<=>?!@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_abcdefghijklmnopqrstuvwxyz";
					var result = true;
					var index = 0;
  					while(index < password.length & result){
	  					if(caratteriAmmessi.indexOf(password.charAt(index)) < 0){
		  					result = false;
	  					} else {
	  						index = index+1;
	  					}
	  				}
					return result;
			}
				
				
			function passwordSimilarity(password,similarityValue) {
					var result = false;
					if (similarityValue && password.toLowerCase().match(similarityValue.toLowerCase())) {
						result = true;
					} else {
						result = false;
					}
					return result;
			}
				
				
			function isPasswordMinimo2Numerici(password){
					var result = true;
				  	var index = 0;
				    var numInteri = 0;
				    var oggettoEspressioneRegolare = new RegExp("^[0-9]$");
					while(index < password.length){
						if (oggettoEspressioneRegolare.test(password.charAt(index))){
							numInteri = numInteri + 1
						}
						index = index + 1;
					}
					if(numInteri < 2) {
						result = false;
				  	}		
					return result;
			}
			
	        function isCodiceControlloCorretto() {
					var isCodiceControlloCorretto = false;
	                $.ajax({
	                    type: "GET",
	                    dataType: "json",
	                    async: false,
	                    beforeSend: function(x) {
	        			if(x && x.overrideMimeType) {
	            			x.overrideMimeType("application/json;charset=UTF-8");
					       }
	    				},
	                    url: "${pageContext.request.contextPath}/IsCodiceControlloCorretto.do",
	                    data: "realpersonHash=" + $("input[name=realpersonHash]").val() + "&realperson=" + $("#realperson").val(),
	                    success: function(data){
	                    	if (data.codiceControlloCorretto == true) {
	                        	isCodiceControlloCorretto = true;
	                        } else {
	                        	isCodiceControlloCorretto = false;
	                        }
	                    },
	                    error: function(e){
	                        isCodiceControlloCorretto = false;
	                    }
	                });
	                return isCodiceControlloCorretto;
	        }
	        
	        function SetApplicativiDisponibili(){
				var appdisp = '';
				var pv = ';';
				var isAppDisp = false;
					<c:forEach items="${listaProfiliDisponibili}" var="valoriProfiliDisponibili" varStatus="ciclo">
						if(document.getElementById("APPDISP_${ciclo.index}").checked){
							var app_i = document.getElementById("APPDISP_${ciclo.index}").value;
							var appdisp_i = "${valoriProfiliDisponibili[0]}";
							appdisp += appdisp_i;
							appdisp +=pv;
							isAppDisp = true;
						}
					</c:forEach>
					if(isAppDisp){
						setValue("APPDISP",appdisp);
					}else{
						alert("Segliere almeno uno fra gli applicativi disponibili");
					}
					
				
				return isAppDisp;
			}
			
        	$(function() {
				$("#UFFINT_CFEIN").autocomplete({
					delay: 0,
				    autoFocus: true,
				    position: { 
				    	my : "left top",
				    	at: "left bottom"
				    },
					source: function( request, response ) {
						changeUFFINT = false;
						var cfamm = $("#UFFINT_CFEIN").val();
						$.ajax({
							/*async: false,*/
						    type: "GET",
		                    dataType: "json",
		                    beforeSend: function(x) {
		        			if(x && x.overrideMimeType) {
		            			x.overrideMimeType("application/json;charset=UTF-8");
						       }
		    				},
		                    url: "${pageContext.request.contextPath}/GetListaEnti.do",
		                    data: "cfamm=" + cfamm,
							success: function( data ) {
								if (!data) {
									response([]);
								} else {
									response( $.map( data, function( item ) {
										return {
											label: (item[1].value == "")?item[0].value:item[0].value + " (C.F: " + item[1].value + ")",
											value: (item[1].value == "")?cfamm:item[1].value,
											valueNOMEIN: item[0].value,
											valueCODEIN: item[2].value
										}
									}));
								} 
							},
		                    error: function(e){
		                        alert("Errore durante la lettura della lista degli enti");
		                    }
						});
					},
					minLength: 1,
					select: function( event, ui ) {
						$("#VIS_UFFINT_NOMEIN").html(ui.item.valueNOMEIN);
						$("#rowVIS_UFFINT_NOMEIN").show();
						$("#UFFINT_CODEIN").val(ui.item.valueCODEIN);
						gestioneSezioneUFFINT(false);
						changeUFFINT = true;
					},
					open: function() {
						$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
					},
					close: function() {
						$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
					},
					change: function(event, ui) {
						var cfamm = $("#UFFINT_CFEIN").val();
						$.ajax({
							/*async: false,*/
						    type: "GET",
		                    dataType: "json",
		                    beforeSend: function(x) {
		        			if(x && x.overrideMimeType) {
		            			x.overrideMimeType("application/json;charset=UTF-8");
						       }
		    				},
		                    url: "${pageContext.request.contextPath}/GetListaEnti.do",
		                    data: "cfamm=" + cfamm,
							success: function( data ) {
								if (!data) {
									$("#VIS_UFFINT_NOMEIN").html("");
									$("#rowVIS_UFFINT_NOMEIN").hide();
									$("#UFFINT_CODEIN").val("");
									gestioneSezioneUFFINT(true);
								} 
							},
							error: function(e){
	                        		$("#VIS_UFFINT_NOMEIN").html("");
									$("#rowVIS_UFFINT_NOMEIN").hide();
									$("#UFFINT_CODEIN").val("");
									gestioneSezioneUFFINT(true);
		                    }
						});
					}
				});
			});
			
		</gene:javaScript>
		
</gene:template>