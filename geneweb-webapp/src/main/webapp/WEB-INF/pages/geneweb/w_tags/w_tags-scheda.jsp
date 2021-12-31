<%/*
       * Created on 17/12/2019
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W_TAGS-scheda" schema="GENEWEB">

	<c:set var="entita" value="W_TAGS"/>
	<gene:setString name="titoloMaschera" value='Configurazione etichette'/>
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
		
			<gene:pagina title="Dati generali della configurazione" idProtezioni="W_TAGS">
			
			
				<gene:redefineInsert name="head" >
					<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery/bcPicker.css" >
					<script type="text/javascript" src="${contextPath}/js/bcPicker.js"></script>
				</gene:redefineInsert>
			
				<gene:formScheda entita="W_TAGS" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreW_TAGS">
					<gene:campoScheda campoFittizio="true" campo="MODOAPERTURA" value="${modo}" definizione="T20;0" visibile="false"/>
					<gene:campoScheda campo="CODAPP" visibile="false" defaultValue="${sessionScope.moduloAttivo}"/>
					<gene:campoScheda title="Codice integrazione" campo="TAGCOD" obbligatorio="true" modificabile="${modo eq 'NUOVO'}"/>
					<gene:campoScheda title="Etichetta integrazione" campo="TAGVIEW" obbligatorio="true"/>
					<gene:campoScheda title="Descrizione" campo="TAGDESC" obbligatorio="true"/>
					<gene:campoScheda title="Colore dello sfondo" campo="TAGCOLOR" definizione="T6;0" visibile="false"/>
					<gene:campoScheda>
						<td class="etichetta-dato">Colore dello sfondo</td>
						<td class="valore-dato"><div style="border: 1px solid gray; width: 20px;" id="TAGCOLOR"></div></td>
					</gene:campoScheda>
					<gene:campoScheda title="Colore del bordo" campo="TAGBORDERCOLOR" definizione="T6;0" visibile="false"/>
					<gene:campoScheda>
						<td class="etichetta-dato">Colore del bordo</td>
						<td class="valore-dato"><div style="border: 1px solid gray; width: 20px;" id="TAGBORDERCOLOR"></div></td>
					</gene:campoScheda>
					<gene:campoScheda title="Etichette visibili nelle maschere ?" campo="TAGVIS" />
					<gene:campoScheda title="Lista dei profili" campo="TAGPROFILI" visibile="false"/>
					
					<gene:campoScheda>
						<td class="etichetta-dato">Lista dei profili in cui visualizzare le etichette
							<br><br>
							<span style="font-style: italic;">
								Se nella lista non &egrave; presente alcun profilo, le etichette saranno visibili sempre indipendentemente dal profilo scelto in fase di accesso all'applicativo.
							</span>
						</td>
						<td class="valore-dato">
							<div id="divProfili"></div>
						</td>
					</gene:campoScheda> 
					
					<gene:campoScheda>
						<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
					</gene:campoScheda>
				</gene:formScheda>
				
				<c:choose>
					<c:when test="${modo eq 'VISUALIZZA'}">
						<script type="text/javascript">
							$("#TAGCOLOR").bcPicker({
								defaultColor : "${datiRiga.W_TAGS_TAGCOLOR}"
							});
					
							$("#TAGBORDERCOLOR").bcPicker({
								defaultColor : "${datiRiga.W_TAGS_TAGBORDERCOLOR}"
							});
							$("#TAGCOLOR").children().on('click', function(){ return false; });
							$("#TAGBORDERCOLOR").children().on('click', function(){ return false; });
							
						</script>
					</c:when>
					<c:otherwise>
						<script type="text/javascript">
							$("#TAGCOLOR").bcPicker({
								defaultColor : "${datiRiga.W_TAGS_TAGCOLOR}"
							});
					
							$("#TAGBORDERCOLOR").bcPicker({
								defaultColor : "${datiRiga.W_TAGS_TAGBORDERCOLOR}"
							});
							$("#TAGCOLOR .bcPicker-palette").on("click", ".bcPicker-color", function(){
								var color = $(this).css("background-color");
								var hex = $.fn.bcPicker.toHex(color);
								$("#W_TAGS_TAGCOLOR").val(hex);
							})
							
							$("#TAGBORDERCOLOR .bcPicker-palette").on("click", ".bcPicker-color", function(){
								var color = $(this).css("background-color");
								var hex = $.fn.bcPicker.toHex(color);
								$("#W_TAGS_TAGBORDERCOLOR").val(hex);
							})
							
							$(window).on("load", function (){
	
								$("#W_TAGS_TAGCOD").alphanum({
									allow               : '',    // Allow extra characters
								    disallow            : '',    // Disallow extra characters
								    allowSpace          : false,  // Allow the space character
								    allowNewline        : false,  // Allow the newline character \n ascii 10
								    allowNumeric        : true,  // Allow digits 0-9
								    allowUpper          : true,  // Allow upper case characters
								    allowLower          : true,  // Allow lower case characters
								    allowCaseless       : false,  // Allow characters that do not have both upper & lower variants eg Arabic or Chinese
								    allowLatin          : true,  // a-z A-Z
								    allowOtherCharSets  : false, // eg é, Á, Arabic, Chinese etc
								    forceUpper          : false, // Convert lower case characters to upper case
								    forceLower          : false, // Convert upper case characters to lower case
								    allowPlus           : false,  // Allow the + sign
								    allowMinus          : false,  // Allow the - sign
								    allowThouSep        : false,  // Allow the thousands separator, default is the comma eg 12,000
								    allowDecSep         : false,  // Allow the decimal separator, default is the fullstop eg 3.141
								    allowLeadingSpaces  : false
								});
	
							});	
							
							
							
						</script>
					</c:otherwise>
				
				</c:choose>
				
				<gene:javaScript>
					$(window).on("load",function (){
				
							function _wait() {
								document.getElementById('bloccaScreen').style.visibility='visible';
								$('#bloccaScreen').css("width",$(document).width());
								$('#bloccaScreen').css("height",$(document).height());
								document.getElementById('wait').style.visibility='visible';
								$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
							}
					
							function _nowait() {
								document.getElementById('bloccaScreen').style.visibility='hidden';
								document.getElementById('wait').style.visibility='hidden';
							}
						
							_leggiFiltroProfilo();
						
							function _leggiFiltroProfilo() {
								_wait();
								var codapp = $("#W_TAGS_CODAPP").val();
								var tagprofili = $("#W_TAGS_TAGPROFILI").val();
							
								$.ajax({
									type: "POST",
									dataType: "json",
									async: false,
									beforeSend: function(x) {
										if(x && x.overrideMimeType) {
											x.overrideMimeType("application/json;charset=UTF-8");
										}
									},
									url: "GetWTagsProfilo.do?codapp=" + codapp + "&tagprofili=" + tagprofili,
									success: function(json){
										if (json) {
											$.map(json, function( profilo ) {
												var _div = $("<div/>");
												if ($("#MODOAPERTURA").val() == "VISUALIZZA") {
													if (profilo.associato == "true") {
														var _span = $("<span>");
														_span.text(profilo.nome + " [" + profilo.cod_profilo + "]");
														_div.append(_span);
													}
												} else {
													var _ck = $("<input/>",{"type":"checkbox", "id": profilo.cod_profilo});
													_ck.attr("cod_profilo",profilo.cod_profilo);
													_ck.on("click",function(){
													    _salvaFiltroProfilo();
													});
													if (profilo.associato == "true") _ck.attr("checked","checked");
													if ($("#MODOAPERTURA").val() == "VISUALIZZA") _ck.attr("disabled","disabled");
													_div.append(_ck).append("&nbsp;").append(profilo.nome + " [" + profilo.cod_profilo + "]");
												}
												$("#divProfili").append(_div);
		
											});
										}
									},
									error: function(e){
										var messaggio = "Errore durante la lettura delle configurazioni di filtro sui profili";
										alert(messaggio);
									},
									complete: function() {
										_nowait();	
						            }
								});
							}
							
							function _salvaFiltroProfilo() {
								_wait();
								var listaprofili = "";
								$("#divProfili").find('div > input:checked').each(function () {
									if (listaprofili.length > 0) listaprofili += ",";
									listaprofili += $(this).attr("cod_profilo");
								});
								$("#W_TAGS_TAGPROFILI").val(listaprofili);
								_nowait();
							}
							
						});
				
				</gene:javaScript>
				
			</gene:pagina>
			
			<gene:pagina title="Lista delle etichette" idProtezioni="W_TAGSLIST">
			
				<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
			
				<gene:redefineInsert name="head" >
					<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >
					<script type="text/javascript" src="${contextPath}/js/jquery.dataTables.min.js"></script>
					<script type="text/javascript" src="${contextPath}/js/jquery.wtagslist.gestione.js"></script>
					
					<style type="text/css">
					
						TABLE.scheda {
							margin-top: 5px;
							margin-bottom: 5px;
							padding: 0px;
							font-size: 11px;
							border-collapse: collapse;
							border-left: 1px solid #A0AABA;
							border-top: 1px solid #A0AABA;
							border-right: 1px solid #A0AABA;
						}
				
						TABLE.scheda TR.intestazione {
							background-color: #EFEFEF;
							border-bottom: 1px solid #A0AABA;
						}
						
						TABLE.scheda TR.intestazione TD, TABLE.scheda TR.intestazione TH {
							padding: 5 2 5 2;
							text-align: center;
							font-weight: bold;
							border-left: 1px solid #A0AABA;
							border-right: 1px solid #A0AABA;
							border-top: 1px solid #A0AABA;
							border-bottom: 1px solid #A0AABA;
							height: 30px;
						}
					
						TABLE.scheda TR.sezione {
							background-color: #EFEFEF;
							border-bottom: 1px solid #A0AABA;
						}
						
						TABLE.scheda TR.sezione TD, TABLE.scheda TR.sezione TH {
							padding: 5 2 5 2;
							text-align: left;
							font-weight: bold;
							height: 25px;
						}
					
						TABLE.scheda TR {
							background-color: #FFFFFF;
						}
				
						TABLE.scheda TR TD {
							padding-left: 3px;
							padding-top: 1px;
							padding-bottom: 1px;
							padding-right: 3px;
							text-align: left;
							border-left: 1px solid #A0AABA;
							border-right: 1px solid #A0AABA;
							border-top: 1px solid #A0AABA;
							border-bottom: 1px solid #A0AABA;
							height: 22px;
							font: 11px Verdana, Arial, Helvetica, sans-serif;
						}
						
						TABLE.scheda TR.intestazione TH.ck, TABLE.scheda TR TD.ck {
							width: 22px;
							text-align: center;
						}
						
						img.img_titolo {
							padding-left: 8px;
							padding-right: 8px;
							width: 24px;
							height: 24px;
							vertical-align: middle;
						}
						
						.dataTables_length, .dataTables_filter {
							padding-bottom: 5px;
						}
							
						div.tooltip {
							width: 300px;
							margin-top: 3px;
							margin-bottom:3px;
							border: 1px solid #A0AABA;
							padding: 10px;
							display: none;
							position: absolute;
							z-index: 1000;
							background-color: #F4F4F4;
						}
			
							
					</style>
					
				</gene:redefineInsert>
				
			
				<gene:formScheda entita="W_TAGS" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreW_TAGS">

					<gene:campoScheda campoFittizio="true" campo="MODOAPERTURA" value="${modo}" definizione="T20;0" visibile="false"/>
					<gene:campoScheda campo="CODAPP" visibile="false" modificabile="false"/>
					<gene:campoScheda campo="TAGCOD" visibile="false" modificabile="false"/>
					<gene:campoScheda title="Modificabile ?" visibile="false" modificabile="false"/>
					
					<gene:campoScheda>
						<td colspan="2">
							<br>
							<div id="tabellaW_TAGSLISTContainer" style="margin-left:8px; width: 98%"></div>
						</td>
					</gene:campoScheda>

					<gene:campoScheda>	
						<td class="comandi-dettaglio">
							<c:choose>
								<c:when test='${modo eq "VISUALIZZA"}'>
									<INPUT type="button" id="pulsantemodificaetichette" class="bottone-azione" value='Modifica' title='Modifica'>
								</c:when>
								<c:otherwise>
									<INPUT type="button" id="pulsantesalvamodificheetichette" class="bottone-azione" value="Salva" title="Salva">
									<INPUT type="button" id="pulsanteannullamodificheetichette" class="bottone-azione" value="Annulla" title="Annulla">
								</c:otherwise>
							</c:choose>
							&nbsp;
						</td>
					</gene:campoScheda>
					
 					<gene:redefineInsert name="schedaModifica"/>
					<gene:redefineInsert name="schedaNuovo"/>
					<gene:redefineInsert name="schedaAnnulla"/>
					<gene:redefineInsert name="schedaConferma"/>
					
					<gene:redefineInsert name="addToAzioni" >
						<tr>
							<c:choose>
						        <c:when test='${modo eq "VISUALIZZA"}'>
						        	<tr>
							        	<td class="vocemenulaterale">
											<a href="#" id="menumodificaetichette" title="Modifica" tabindex="1512">Modifica</a>
									  	</td>
									  </tr>
						        </c:when>
							    <c:otherwise>
							    	<tr>
								       	<td class="vocemenulaterale">
											<a href="#" id="menusalvamodificheetichette" title="Salva" tabindex="1512">Salva</a>
									  	</td>
									</tr>
							       	<tr>
								       	<td class="vocemenulaterale">
											<a href="#" id="menuannullamodificheetichette" title="Annulla" tabindex="1512">Annulla</a>
									  	</td>
									 </tr>		  	
							    </c:otherwise>
							</c:choose>
						</tr>
					</gene:redefineInsert>
					
				</gene:formScheda>
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>
	<gene:redefineInsert name="modelliPredisposti"/>
	
	
</gene:template>




