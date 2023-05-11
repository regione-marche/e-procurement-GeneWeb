<%/*
       * Created on 22-Set-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA DEFINIZIONE DELLE VOCI DEI MENU COMUNI A TUTTE LE APPLICAZIONI
%>

<!-- Inserisco la Tag Library -->
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="opzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="moduloAttivo" value="${sessionScope.moduloAttivo}" scope="request"/>
<!-- inserisco il mio tag -->
<gene:template file="scheda-template.jsp">
	<!-- Settaggio delle stringhe utilizate nel template -->
	<gene:setString name="titoloMaschera" value="Gestione codici anagrafiche"/>
	
	<gene:sqlSelect nome="valoreCriterioRic" parametri="">
	   	select TAB1.TAB1DESC  from  TAB1 where  TAB1.TAB1COD='C0020' and TAB1.TAB1TIP=1
	</gene:sqlSelect>	
	
	<gene:sqlSelect nome="valoreCriterioTec" parametri="">
	   	select TAB1.TAB1DESC  from  TAB1 where  TAB1.TAB1COD='C0020' and TAB1.TAB1TIP=2
	</gene:sqlSelect>
	
	<gene:sqlSelect nome="valoreContatoreRic" parametri="">
	   	select TAB1.TAB1DESC  from  TAB1 where  TAB1.TAB1COD='C0021' and TAB1.TAB1TIP=1
	</gene:sqlSelect>
	
	<gene:sqlSelect nome="valoreContatoreTec" parametri="">
	   	select TAB1.TAB1DESC  from  TAB1 where  TAB1.TAB1COD='C0021' and TAB1.TAB1TIP=2
	</gene:sqlSelect>
	
	<gene:redefineInsert name="modelliPredisposti"/>
	<gene:redefineInsert name="documentiAssociati"/>
	<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO" }'>
		<gene:redefineInsert name="schedaConferma"/>
		<gene:redefineInsert name="schedaAnnulla"/>
		<gene:redefineInsert name="addToAzioni">
			<gene:insert name="verificaScheda">
				<tr>
						<td class="vocemenulaterale">
							<a href="javascript:verificaScheda();" title="Salva modifiche" tabindex="1500">
								${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
				</tr>
			</gene:insert>
			<gene:insert name="annullaScheda">
				<tr>
						<td class="vocemenulaterale">
							<a href="javascript:schedaAnnulla();" title="Annulla modifiche" tabindex="1500">
							${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}</a></td>
				</tr>
			</gene:insert>
		</gene:redefineInsert>
	</c:if>
	
	<gene:historyClear/>
		
	<!-- Ridefinisco il corpo della ricerca -->
	<gene:redefineInsert name="corpo">
	  	<!-- Creo la form di trova con i campi dell'entità tab1 -->
	  	<gene:formPagine>
				<!-- Prima scheda -->
				<gene:pagina title="Codici anagrafiche" >
					<!-- Dati generali -->
					<gene:formScheda entita="TAB1" where="TAB1.TAB1COD='C0020' and TAB1.TAB1TIP=1" gestore="it.eldasoft.ugc.web.struts.gestori.GestoreCodiciAnagrafiche">
						<gene:campoScheda title="Criterio codifica autom. richiedenti" campo="TAB1DESC" visibile="false"/>
						<gene:campoScheda  entita="NULLA" campoFittizio="true" campo="CriterioRic" title="Criterio codifica autom. richiedenti" definizione="T20" value="${valoreCriterioRic.TAB1DESC}" />
						<gene:campoScheda  entita="NULLA" campoFittizio="true" campo="ContatoreRic" title="Contatore ultimo richiedente" definizione="N10" value="${valoreContatoreRic.TAB1DESC}" />
						<gene:campoScheda  entita="NULLA" campoFittizio="true" campo="CriterioTec" title="Criterio codifica autom. tecnici" definizione="T20" value="${valoreCriterioTec.TAB1DESC}" />
						<gene:campoScheda  entita="NULLA" campoFittizio="true" campo="ContatoreTec" title="Contatore ultimo tecnico" definizione="N10" value="${valoreContatoreTec.TAB1DESC}" />
						<gene:campoScheda>	
							<td class="comandi-dettaglio" colSpan="2">
								<c:choose>
								<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
									<gene:insert name="pulsanteSalva">
										<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:verificaScheda()">
									</gene:insert>
									<gene:insert name="pulsanteAnnulla">
										<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
									</gene:insert>
							
								</c:when>
								<c:otherwise>
									<gene:insert name="pulsanteModifica">
									<INPUT type="button"  class="bottone-azione" value="Modifica" title="Modifica" onclick="javascript:schedaModifica()">
									</gene:insert>
								</c:otherwise>
								</c:choose>
								&nbsp;
							</td>


						</gene:campoScheda>
						<gene:fnJavaScriptScheda funzione="AbilitaContatoreRic('#NULLA_CRITERIORIC#')" elencocampi="NULLA_CRITERIORIC" esegui="true" />
						<gene:fnJavaScriptScheda funzione="AbilitaContatoreTec('#NULLA_CRITERIOTEC#')" elencocampi="NULLA_CRITERIOTEC" esegui="true" />
					</gene:formScheda>
					
						<gene:javaScript>
							function AbilitaContatoreRic(criterioRic) {
								if (criterioRic==""){
									document.forms[0].NULLA_CONTATORERIC.value="";
									document.forms[0].NULLA_CONTATORERIC.style.background="#C0C0C0";
									document.forms[0].NULLA_CONTATORERIC.disabled=true;
								}else {
									document.forms[0].NULLA_CONTATORERIC.disabled=false;
									document.forms[0].NULLA_CONTATORERIC.style.background="white";
								}
								document.forms[0].TAB1_TAB1DESC.value = document.forms[0].NULLA_CRITERIORIC.value;
							}
							
							function AbilitaContatoreTec(criterioTec) {
								if (criterioTec==""){
									document.forms[0].NULLA_CONTATORETEC.value="";
									document.forms[0].NULLA_CONTATORETEC.style.background="#C0C0C0";
									document.forms[0].NULLA_CONTATORETEC.disabled=true;
								}else {
									document.forms[0].NULLA_CONTATORETEC.disabled=false;
									document.forms[0].NULLA_CONTATORETEC.style.background="white";
								}
							}
							
							function verificaSintassiCriterioCodice(value,tipocriterio) {
								var inizioProgr,fineProgr,inizioStr,fineStr,result,carattere,index,messaggio,caratteriammessi;
								var criterio;
								
								// Se il criterio è nullo non eseguo alcun controllo
								if (value=="") return 1;
								
								if (tipocriterio == 1) {
									criterio="Criterio codifica richiedenti.\n";
								} else {
									criterio="Criterio codifica tecnici.\n";
								}
								//determino la posizione dei quattro caratteri ('<', '>' e '"', '"'> che 
						    //contengono a due a due la definizione della stringa fissa e del progressivo
								inizioProgr = value.indexOf("<") + 1;
								fineProgr = value.lastIndexOf(">");
								
								inizioStr = value.indexOf("\"")+1;
   							fineStr = value.lastIndexOf("\"");
    							
   							//Stringa contenente i caratteri ammessi
   							caratteriammessi = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-0123456789§ ./\$_[#]@"
    							
   							result = false;
   							//Il sequente test ritorna true se il criterio del codice verifica uno dei
						    //seguenti casi (da considerare come esempio):
						    //1. "<0000000>\"qUT\""
						    //2. "\"TECN\"<000000>"
						    //3. "<000000>"
						    //e che i caratteri utili a comporre il codice costruiscano una stringa di 
						    //lunghezza pari a 10
						    
						    var isSintassiCodiceCorretta =
						    		(inizioProgr == 1 && fineProgr > 1 && (
						    	  	(inizioStr > fineProgr && fineStr > inizioStr) ||
						  		  	(inizioStr == 0 && fineStr == -1))
						        ) || (
						        inizioStr == 1 && fineStr > 1 && inizioProgr > fineStr && fineProgr > inizioProgr);
						    
						    if(isSintassiCodiceCorretta && (fineProgr - inizioProgr + fineStr - inizioStr <= 10)){
						      result = true;
						    } else if(! isSintassiCodiceCorretta){
						    	alert(criterio + " La sintassi del codice non è corretta");
						    	return -1;
						    } else {
						    	alert(criterio + " Il codice assume un numero di caratteri maggiore di 10.");
						    	return -1;
						    }
							    
						    //Controllo sui caratteri ammessi per la stringa fissa
						    for (index=inizioStr+1; index < fineStr;index ++) {
						    	carattere = value.charAt(index);
						    	if (caratteriammessi.search(carattere)== -1) {
						    		alert(criterio + " La stringa contiene il carattere non ammesso: " +  carattere);
						    		return -1;
						    	} 
						    }
							    
   							//Ciclo per verificare che la stringa del progressivo sia costituita 
						    //interamente da '0' o da '9'
						    carattere = value.charAt(inizioProgr);
						    if(result && ("0" == carattere || "9" == carattere)){
						      index = inizioProgr++;
						      while((index < fineProgr) && result){
						        if(carattere == value.charAt(index)){
						          index++;
						        } else {
						        	alert(criterio + " Sono ammesse sono le sequenze <00..00> e <99..99>");
					          	return -1;
						        }
							    }
							  } else {
							   	alert(criterio + " Sono ammesse sono le sequenze <00..00> e <99..99>");
							    return -1;
							  } 
							  return 1;
							}
							
							
							//var originalBlurCRITERIORIC=document.forms[0].NULLA_CRITERIORIC.onBlur;
							function redirectBlurCRITERIORIC(){
								if (verificaSintassiCriterioCodice(document.forms[0].NULLA_CRITERIORIC.value,1)==-1) {
									document.forms[0].NULLA_CRITERIORIC.focus();
								}
								//originalBlurCRITERIORIC();
							}
							document.forms[0].NULLA_CRITERIORIC.onblur=redirectBlurCRITERIORIC;
							
							//var originalBlurCRITERIOTEC=document.forms[0].NULLA_CRITERIOTEC.onBlur;
							function redirectBlurCRITERIOTEC(){
								if (verificaSintassiCriterioCodice(document.forms[0].NULLA_CRITERIOTEC.value,2)==-1) {
									document.forms[0].NULLA_CRITERIOTEC.focus();
								}
								//originalBlurCRITERIOTEC();
							}
							document.forms[0].NULLA_CRITERIOTEC.onblur=redirectBlurCRITERIOTEC;
							
							function verificaScheda() {
								if ((verificaSintassiCriterioCodice(document.forms[0].NULLA_CRITERIORIC.value,1)>0) && (verificaSintassiCriterioCodice(document.forms[0].NULLA_CRITERIOTEC.value,2)>0)) {
									schedaConferma();
								}
							}
							
						</gene:javaScript>
					
				</gene:pagina>
		</gene:formPagine>    
	</gene:redefineInsert>
</gene:template>