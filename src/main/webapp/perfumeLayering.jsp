<%@ page import="java.util.List" %>
<%@ page import="mirunq_png.perfumeapp.model.Perfume" %>
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
                <strong><%= p.formatText(p.getBrand()) %> - <%= p.formatText(p.getName()) %></strong><br>
                <%= base.getLayeringExplanation(p) %> </li>
        <% } %>
    </ul>
    <a href="index.html">Back</a>
</body>
</html>