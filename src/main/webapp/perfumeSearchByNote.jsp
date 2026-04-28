<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="mirunq_png.perfumeapp.model.Perfume" %>
<%@ page import="mirunq_png.perfumeapp.model.Note" %>
<%@ page import="mirunq_png.perfumeapp.model.Season" %>
<%@ page import="mirunq_png.perfumeapp.utility.StringUtils" %>
<html>
<head>
    <title>Search Results</title>
    <style>
        body {
            font-family: serif;
            margin: 20px;
        }
        .form-container {
            border: 1px solid #ddd;
            padding: 20px;
            max-width: 400px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], input[type="number"], select {
            font-family: serif;
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            box-sizing: border-box;
        }
        button {
            font-family: serif;
            padding: 10px 15px;
            background-color: #f2f2f2;
            border: 1px solid #ddd;
            cursor: pointer;
        }
        button:hover {
            background-color: #f5f5f5;
        }
        .back-link {
            display: inline-block;
            margin-top: 15px;
            text-decoration: underline;
            color: #0000EE;
        }
        .hidden {
            display: none;
        }
        .result-box {
            border: 1px solid #ddd;
            padding: 20px;
            margin-bottom: 15px;
            max-width: 450px;
            background-color: #fafafa;
        }
        .section-title {
            margin-bottom: 5px;
            margin-top: 15px;
            color: #333;
            border-bottom: 1px solid #ddd;
            padding-bottom: 3px;
            font-size: 1.1em;
        }
        .highlight-pink {
                    color: #ff1493;
                    font-weight: bold;
                }
        ul { margin-top: 5px; }
    </style>
</head>
<body>
    <%
        String searchNote = (String) request.getAttribute("searchNote");
        List<Perfume> perfumes = (List<Perfume>) request.getAttribute("perfumes");
    %>
    <a href="index.html" class="back-link">Return to main page</a>
    <h3>Search Results for: <span class="highlight-pink"><%= searchNote != null ? searchNote : "..." %></span></h3>

    <%
        if (perfumes == null || perfumes.isEmpty()) {
    %>
        <p>No perfumes found containing the note: <strong><%= searchNote %></strong></p>
    <%
        } else {
            for (Perfume p : perfumes) {
    %>
        <div class="result-box">
            <h3 style="margin-top: 0;"><%= StringUtils.formatText(p.getBrand()) %> - <%= StringUtils.formatText(p.getName()) %></h3>

            <p><strong>Concentration:</strong> <%= p.getType() != null ? p.getType() : "N/A" %></p>
            <p><strong>Amount:</strong> <%= p.getMl() %> ml</p>
            <p><strong>Rating:</strong> <%= (int)p.getRating() %> / 10</p>

            <h4 class="section-title">Best Seasons:</h4>
            <ul>
                <%
                    Set<Season> seasons = p.getSeasons();
                    if (seasons == null || seasons.isEmpty()) {
                %>
                    <li>No seasons assigned yet.</li>
                <% } else {
                    for (Season s : seasons) {
                %>
                    <li><%= StringUtils.formatText(s.name()) %></li>
                <% } } %>
            </ul>

            <h4 class="section-title">Notes:</h4>
                        <ul>
                            <%
                                List<Note> notes = p.getNotes();
                                if (notes == null || notes.isEmpty()) {
                            %>
                                <li>No notes assigned yet.</li>
                            <% } else {
                                for (Note n : notes) {
                                    boolean isMatch = searchNote != null &&
                                                      n.getName().toLowerCase().contains(searchNote.toLowerCase().trim());
                            %>
                                <li>
                                    <% if (isMatch) { %>
                                        <span class="highlight-pink"><%= StringUtils.formatText(n.getName()) %></span>
                                    <% } else { %>
                                        <strong><%= StringUtils.formatText(n.getName()) %></strong>
                                    <% } %>
                                    <span style="color: #666; font-size: 0.9em;">(<%= n.getLayer() %>)</span>
                                </li>
                            <% } } %>
                        </ul>
        </div>
    <%
            }
        }
    %>

    <a href="index.html" class="back-link">Return to main page</a>

</body>
</html>