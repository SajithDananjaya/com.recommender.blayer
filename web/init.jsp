<%-- 
    Document   : init
    Created on : Sep 16, 2016, 6:53:49 PM
    Author     : Sajith
--%>

<%@ include file="header.jsp" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="processes.RecomenderProccess"%>


<div class="col-md-3"></div>
<div class="col-md-6">

    <h3>
         <%
            boolean systemInitiated = RecomenderProccess.initateApplication();
            if (systemInitiated) {
         %>
                <div class="alert alert-success" style="margin-top: 10%; text-align: center">Initiation Completed successful</div>
        <%
        }   else {
        %>
                <div class="alert alert-danger" style="margin-top: 10%; text-align: center">Initiation failed</div>
        <%

        }

        %>
    </h3>
</div>
</div>  
<div class="col-md-3"></div>

</div>

<%@ include file="footer.jsp" %>