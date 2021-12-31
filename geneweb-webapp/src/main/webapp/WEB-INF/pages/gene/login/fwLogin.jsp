<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<title></title>
</head>
<body onload="javascript:document.forms[0].submit();"> 
   <form action="${pageContext.request.contextPath}/Login.do" method="POST">
	   <jsp:include page="/WEB-INF/pages/commons/csrf.jsp" />
	   <html:hidden property="username" value="${requestScope.username}" />
       <html:hidden property="password" value="${requestScope.password}" />
       <c:forEach items="${param}" var="item">
       <c:if test="${item.key ne 'username' && item.key ne 'password' && item.key ne 'submit'}">
       <input type="hidden" name="${item.key}" value="${item.value}"/>
       </c:if>
       </c:forEach>
   </form>
</body>
</html>
