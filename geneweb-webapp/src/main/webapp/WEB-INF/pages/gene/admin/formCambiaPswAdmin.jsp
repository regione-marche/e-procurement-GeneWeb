<%/*
       * Created on 19-giu-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI CAMBIO PASSWORD CONTENENTE IL FORM
      %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html:form action="/CambiaPasswordAdmin">
	<TABLE class="dettaglio-notab">
		<html:hidden property="idAccount"/>
		<html:hidden property="metodo" value="modifica" />
		<TR>
			<TD class="etichetta-dato">Nuova Password <c:if test='${controlloPasswordUtenteAttivo or "1" ne passwordNullable}'>(*)</c:if></TD>
			<TD class="valore-dato"><html:password property="nuovaPassword"
				styleClass="testo" size="15" maxlength="30" /></TD>
		</TR>

		<TR>
			<TD class="etichetta-dato">Conferma Nuova Password <c:if test='${controlloPasswordUtenteAttivo or "1" ne passwordNullable}'>(*)</c:if></TD>
			<TD class="valore-dato"><html:password
				property="confermaNuovaPassword" styleClass="testo" size="15" maxlength="30" /></TD>
		</TR>

		<TR>
			<TD class="comandi-dettaglio" colSpan="2">
			<INPUT type="button" class="bottone-azione" value="Salva" onclick="javascript:gestisciSubmit()">
			&nbsp;
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();" >
	        &nbsp;
			</TD>
		</TR>
	</TABLE>
</html:form>