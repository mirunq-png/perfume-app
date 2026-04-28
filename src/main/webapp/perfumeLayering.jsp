<%@ page import="java.util.List" %>
<%@ page import="mirunq_png.perfumeapp.model.Perfume" %>
<%@ page import="mirunq_png.perfumeapp.utility.StringUtils" %>
<html>
<body>
    <% Perfume base = (Perfume) request.getAttribute("base"); %>
    <h2>Recommendations for <%= base.getName() %></h2>
    <ul>
        <%
            List<Perfume> results = (List<Perfume>) request.getAttribute("results");
            for(Perfume p : results) {
        %>
            <li>
                <strong><%= StringUtils.formatText(p.getBrand()) %> - <%= StringUtils.formatText(p.getName()) %></strong><br>
                <%= base.getLayeringExplanation(p) %> </li>
        <% } %>
    </ul>
    <a href="index.html">Return to main page</a>
</body>
</html>