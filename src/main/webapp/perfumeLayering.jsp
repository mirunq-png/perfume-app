<%@ page import="java.util.List" %>
<%@ page import="mirunq_png.perfumeapp.model.Perfume" %>
<%@ page import="mirunq_png.perfumeapp.utility.StringUtils" %>
<%@ page import="mirunq_png.perfumeapp.service.LayeringService" %>
<html>
<body>
    <% Perfume base = (Perfume) request.getAttribute("base");
        LayeringService service=new LayeringService();
    %>
    <h2>Recommendations for <%= StringUtils.formatText(base.getName()) %></h2>
    <ul>
        <%
            List<Perfume> results = (List<Perfume>) request.getAttribute("results");
            for(Perfume p : results) {
        %>
            <li>
                <strong><%= StringUtils.formatText(p.getBrand()) %> - <%= StringUtils.formatText(p.getName()) %></strong><br>
                <%= service.getExplanation(base,p) %> </li>
        <% } %>
    </ul>
    <a href="index.html">Return to main page</a>
</body>
</html>