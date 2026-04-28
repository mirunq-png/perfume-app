<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="mirunq_png.perfumeapp.model.Perfume" %>
<%@ page import="mirunq_png.perfumeapp.model.Note" %>
<%@ page import="mirunq_png.perfumeapp.utility.StringUtils" %>

<html>
<head>
    <title>Perfume Catalog</title>
    <style>
        table { width: 100%; border-collapse: collapse; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        tr:hover { background-color: #f5f5f5; }
    </style>
</head>
<body>
    <h1>Full Perfume Catalog</h1>

    <table>
        <thead>
            <tr>
                <th>Brand</th>
                <th>Name</th>
                <th>View details</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Perfume> perfumes = (List<Perfume>) request.getAttribute("catalog");
                if (perfumes != null) {
                    for (Perfume p : perfumes) {
            %>
                <tr>
                    <td><%= StringUtils.formatText(p.getBrand()) %></td> <td><%= StringUtils.formatText(p.getName()) %></td> <td>
                        <a href="view?perfumeName=<%= p.getName() %>">Click here</a>
                    </td>
                </tr>
            <%
                    }
                }
            %>
        </tbody>
    </table>

    <br>
    <a href="index.html">Return to main page</a>
</body>
</html>