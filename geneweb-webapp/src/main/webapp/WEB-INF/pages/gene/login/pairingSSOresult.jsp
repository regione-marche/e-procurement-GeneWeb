<%/*
       * Created on 22-gen-2010
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

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
</HEAD>

<BODY>
<TABLE class="arealayout">
	<TBODY>
		<TR class="testata">
			<TD colspan="2">
				<table class="logo-datiUtente">
					<tr>
						<td width="400">
									<img src="${pageContext.request.contextPath}/img/banner_logo.png" alt="Torna alla homepage di ${applicationScope.appTitle}" title="Torna alla homepage">
						</td>
						<td align="left">
							<span class="titoloApplicativo">&nbsp;</span>
						</td>
						<td width="300">
						</td>
					</tr>
				</table>
			</TD>
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
							
								<html:messages id="error" message="true" property="error">
									<P>
									<font color="red"><c:out value="${error}"/><p>Impossibile completare il processo di accoppiamento delle due utenze</p></font><br>
									</P>
									<P>
									&nbsp;    
									</P>
									<P>
									Accedere al seguente <a href="${pageContext.request.contextPath}/Home.do" title="Selezionare questo collegamento per tornare alla homepage" class="link-generico">link</a> per tornare alla homepage
									</P>
								</html:messages>
								
								<html:messages id="info" message="true" property="info">
									<P>
									<font color="blue"><c:out value="${info}"/></font><br>
									</P>
									<P>
									&nbsp;    
									</P>
									<P>
									Per ricaricare le informazioni aggiornate dell'utente &egrave; necessario disconnettersi dall'applicativo mediante la presente 
									<a href="${pageContext.request.contextPath}/Logout.do" title="Selezionare questo collegamento per disconnettere l'utente" class="link-generico">azione</a>.<br>
									Potrai rieffettuare immediatamente l'accesso all'applicativo tramite l'autenticazione esterna utilizzata per l'operazione.
									</P>
								</html:messages>	
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

	</TBODY>
</TABLE>
</BODY>

</HTML>