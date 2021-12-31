<%/*
       * Created on 15-giu-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLA PAGINA DI ERRORI GENERALI
    %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<HTML>
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<tiles:insert attribute="head" />
</HEAD>

<BODY <tiles:getAsString name="eventiDiPagina" /> >
<TABLE class="arealayout">
	<TBODY>
		<TR class="testata">
			<TD colspan="2"><tiles:insert attribute="testata" /></TD>
		</TR>
		
		<TR class="menuprincipale">
			<TD colspan="2"></TD>
		</TR>

		<TR>
			<TD colspan="2">
			<center>

			<table class="contenitore-errori">
				<tr>
					<td>
					<table class="dati-errore">
						<tr>
							<td class="errore-generale">ATTENZIONE</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td colspan="2">
							<logic:messagesPresent message="true">
							<P>
								<html:messages id="error" message="true" property="error">
								<font color="red"><c:out value="${error}"/></font><br/>
								</html:messages>
							</P>
							</logic:messagesPresent>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</center>
			</TD>
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

