<%@ taglib uri="https://owasp.org/www-project-csrfguard/Owasp.CsrfGuard.tld" prefix="csrf" %>
<input type="hidden" name="<csrf:tokenname/>" value="<csrf:tokenvalue/>"/>