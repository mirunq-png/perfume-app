<%@ page import="mirunq_png.perfumeapp.model.Perfume" %>
<%@ page import="mirunq_png.perfumeapp.model.Note" %>
<%@ page import="mirunq_png.perfumeapp.model.Season" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>

<html>
<head>
    <title>Perfume Details</title>
</head>
<body>
    <%
        Perfume p = (Perfume) request.getAttribute("perfume");
        if (p != null) {
    %>
        <h1><%= p.formatText(p.getBrand()) %> - <%= p.formatText(p.getName()) %></h1>

        <h3>Best seasons for this scent:</h3>
        <ul>
            <%
                Set<Season> seasons = p.getSeasons();
                if (seasons.isEmpty()) {
            %>
                <li>No seasons assigned yet.</li>
            <% } else {
                for (Season s : seasons) {
            %>
                <li><strong><%= p.formatText(s.name()) %></strong></li>
            <% } } %>
        </ul>

        <h3>Notes & Composition:</h3>
        <ul>
            <%
                List<Note> notes = p.getNotes();
                for (Note n : notes) {
            %>
                <li><%= p.formatText(n.getName()) %> (<%= n.getLayer() %>)</li>
            <% } %>
        </ul>

        <br>
        <a href="index.html">Return to main page</a>
    <% } else { %>
        <p>Perfume details not found.</p>
        <a href="index.html">Try another search</a>
    <% } %>
</body>
</html>