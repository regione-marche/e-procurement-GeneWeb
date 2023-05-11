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
      // PER LA RICERCA DI UN CAMPO DA INSERIRE IN UNA RICERCA
      %>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="lista-imprese-popup">
	<gene:setString name="titoloMaschera" value="Selezione zone di attività"/>
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="IMPR" gestisciProtezioni="true">
			<gene:campoScheda campo="CODIMP" visibile="false"/>
			<c:set var="contatore" value="1"/>
			<c:forEach items="${listaRegioni}" var="regione">
				<gene:campoScheda title="${regione}"  campo="ZONA${contatore}" value="${zoneAttive[contatore-1]}" campoFittizio="true" definizione="T1;;;SN;" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoGrigio" />
				<gene:fnJavaScriptScheda funzione='aggiornaFlagTutte(${contatore})' elencocampi='ZONA${contatore}' />
				<c:set var="contatore" value="${contatore + 1}"/>
			</c:forEach>

			<gene:campoScheda title="Tutte le regioni"  campo="ZONA21" value="" campoFittizio="true" definizione="T1;;;SN;" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoGrigio" />
			<gene:campoScheda>
				<tr>
				    <td class="comandi-dettaglio" colSpan="2">
				      <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:riportaValori();" >
				      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:esci();" >
				      &nbsp;
				    </td>
				</tr>
			</gene:campoScheda>
			<gene:fnJavaScriptScheda funzione='selezionaTutteRegioni()' elencocampi='ZONA21'/>
	</gene:formScheda>
	
	</gene:redefineInsert>
	<gene:javaScript>
	

		var selezioneDaRegione = "false";
		
		function riportaValori(){
			var zone = "";
			var campo = "";
			for (var i=1;i<21;i++){
				campo = "ZONA" + i;
				zone += getValue(campo);
			}
						
			window.opener.activeForm.setValue("IMPR_ZONEAT",zone);
			window.close();
		}
		
		function esci() {
			window.close();
		}
		
		function selezionaTutteRegioni() {
			var valore = getValue("ZONA21");
			if (valore != '0') {
				for (var i=1;i<21;i++){
					campo = "ZONA" + i;
					setValue(campo,valore);
				}
			} 
		}
		
		function aggiornaFlagTutte(idRegione) {
			var valore = getValue("ZONA" + idRegione);
			var valoreTutte = getValue("ZONA21");
			if (valoreTutte != valore)
				setValue("ZONA21",'0');

		}
	</gene:javaScript>
</gene:template>
