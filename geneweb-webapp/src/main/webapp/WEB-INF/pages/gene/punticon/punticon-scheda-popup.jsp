<%
/*
 * Created on: 24-04-2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Scheda a popup per il punto di contatto */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="popup-template.jsp" >
	<gene:setString name="titoloMaschera" value='Punto di contatto' />
	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="PUNTICON" >
			
			<gene:gruppoCampi  >
				<gene:campoScheda  nome="GEN">
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="CODEIN" visibile="false"  />
				<gene:campoScheda campo="NUMPUN" visibile="false" />
				<gene:campoScheda campo="NOMPUN" />
				<gene:campoScheda campo="VIAEIN" />
				<gene:campoScheda campo="NCIEIN" />
				<gene:campoScheda campo="PROEIN" />
				<gene:archivio titolo="Comuni" obbligatorio="false" scollegabile="true"
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CAPEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.PROEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CITEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CODCIT"),"gene/commons/istat-comuni-lista-popup.jsp","")}' 
						scheda="" 
						schedaPopUp="" 
						campi="TB1.TABCOD3;TABSCHE.TABCOD4;TABSCHE.TABDESC;TABSCHE.TABCOD3" 
						chiave="" 
						where='${gene:if(!empty datiRiga.PUNTICON_PROEIN, gene:concat(gene:concat("TB1.TABCOD3 = \'", datiRiga.PUNTICON_PROEIN), "\'"), "")}'  
						formName="formIstat" 
						inseribile="false" >
					<gene:campoScheda campoFittizio="true" campo="COM_PROEIN" definizione="T9" visibile="false"/>
					<gene:campoScheda campo="CAPEIN"/>
					<gene:campoScheda campo="CITEIN"/>
					<gene:campoScheda campo="CODCIT"/>
				</gene:archivio>
				<gene:campoScheda campo="CODNAZ" />
				<gene:campoScheda campo="TELEIN" />
				<gene:campoScheda campo="FAXEIN" />
				<gene:campoScheda campo="EMAIIN"/>
				<gene:campoScheda campo="EMAI2IN" />
				<gene:campoScheda campo="INDWEB"/>
				<gene:campoScheda campo="CODFE"/>
				<gene:archivio titolo="Tecnici" 
					lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CODRES"),"gene/tecni/tecni-lista-popup.jsp","")}' 
					scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
					schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
					campi="TECNI.CODTEC;TECNI.NOMTEC" 
					chiave="PUNTICON_CODRES"
					formName="formResponsabile"
					inseribile="false">
						<gene:campoScheda campo="CODRES" />
						<gene:campoScheda campo="NOMTEC" title="Nome" entita="TECNI" where="TECNI.CODTEC = PUNTICON.CODRES"/>
				</gene:archivio>
			</gene:gruppoCampi>

			
		</gene:formScheda>
  </gene:redefineInsert>
</gene:template>
