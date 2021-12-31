<%/* 
       * Created on 16-set-2012
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'INTERA PAGINA PER LA SCELTA DELL'UFFICIO INTESTATARIO
      // CON CUI ACCEDERE ALL'APPLICAZIONE
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template file="generic-template.jsp" gestisciProtezioni="false" schema="GENE">
	<gene:redefineInsert name="preTitolo">
	  <jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp">
		<jsp:param name="hideHistory" value="1" />
	  </jsp:include>
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">
			<div class="contenitore-arealavoro">
				<div class="titolomaschera">Contattaci</div>
				<div class="dettaglio-home">

				  <div>
				  Questa pagina &egrave; dedicata alle vostre comunicazioni. Se avete domande da fare, volete richiedere informazioni, 
				  fare proposte, o semplicemente esprimere le vostre opinioni, potete contattarci al sito
				  <a href="http://assistenza.maggioli.it" class="link-generico">Assistenza Maggioli</a>
				  </div>
				</div>

			</div>
	</gene:redefineInsert>
</gene:template>