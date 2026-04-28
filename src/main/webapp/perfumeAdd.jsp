<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Add a New Perfume</title>
    <style>
        body {
            /* Changed to serif to match the details page */
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
            font-family: serif; /* Ensures inputs match the body font */
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
            text-decoration: underline; /* Matches the link style in your image */
            color: #0000EE; /* Standard browser link purple/blue */
        }
        .hidden {
            display: none;
        }
    </style>
    <script>
        function checkBrandSelection() {
            var brandSelect = document.getElementById("existingBrand");
            var newBrandInput = document.getElementById("newBrandContainer");
            if (brandSelect.value === "NEW") {
                newBrandInput.classList.remove("hidden");
                document.getElementById("newBrand").required = true;
            } else {
                newBrandInput.classList.add("hidden");
                document.getElementById("newBrand").required = false;
                document.getElementById("newBrand").value = "";
            }
        }
    </script>
</head>
<body>
    <div class="form-container">
        <h3>Add a New Perfume</h3>

        <form action="add" method="POST">
            <div class="form-group">
                <label for="name">Perfume Name *</label>
                <input type="text" id="name" name="name" required placeholder="e.g. Libre">
            </div>

            <div class="form-group">
                <label for="existingBrand">Brand Name *</label>
                <select id="existingBrand" name="existingBrand" onchange="checkBrandSelection()" required>
                    <option value="">-- Select a Brand --</option>
                    <%
                        List<String> brands = (List<String>) request.getAttribute("brands");
                        if (brands != null) {
                            for (String b : brands) {
                    %>
                                <option value="<%= b %>"><%= b %></option>
                    <%      }
                        }
                    %>
                    <option value="NEW">+ Add a new brand...</option>
                </select>
            </div>

            <div class="form-group hidden" id="newBrandContainer">
                <label for="newBrand">New Brand Name *</label>
                <input type="text" id="newBrand" name="newBrand" placeholder="Type new brand name here">
            </div>

            <div class="form-group">
                <label for="ml">Amount (ml) *</label>
                <select id="ml" name="ml">
                    <option value="5">5 ml</option>
                    <option value="10">10 ml</option>
                    <option value="30">30 ml</option>
                    <option value="50">50 ml</option>
                    <option value="75">75 ml</option>
                    <option value="100">100 ml</option>
                </select>
            </div>

            <div class="form-group">
                <label for="type">Concentration *</label>
                <select id="type" name="type">
                    <option value="EDP">Eau de Parfum (EDP)</option>
                    <option value="EDT">Eau de Toilette (EDT)</option>
                    <option value="BM">Body Mist</option>
                </select>
            </div>
            <div class="form-group">
                <label>Seasons *</label>
                <div>
                    <input type="checkbox" id="winter" name="season" value="WINTER">
                    <label for="winter" style="display:inline; font-weight:normal;">Winter</label>

                    <input type="checkbox" id="spring" name="season" value="SPRING">
                    <label for="spring" style="display:inline; font-weight:normal;">Spring</label>

                    <input type="checkbox" id="summer" name="season" value="SUMMER">
                    <label for="summer" style="display:inline; font-weight:normal;">Summer</label>

                    <input type="checkbox" id="fall" name="season" value="FALL">
                    <label for="fall" style="display:inline; font-weight:normal;">Fall</label>
                </div>
            </div>
            <div class="form-group">
                <label for="topNotes">Top Notes (comma separated)</label>
                <input type="text" id="topNotes" name="topNotes" placeholder="e.g. Lavender, Mandarin">
            </div>

            <div class="form-group">
                <label for="heartNotes">Heart Notes (comma separated)</label>
                <input type="text" id="heartNotes" name="heartNotes" placeholder="e.g. Jasmine, Orange Blossom">
            </div>

            <div class="form-group">
                <label for="baseNotes">Base Notes (comma separated) *</label>
                <input type="text" id="baseNotes" name="baseNotes" placeholder="e.g. Vanilla, Musk" required>
            </div>

            <button type="submit">Save perfume</button>
        </form>
    </div>

    <a href="index.html">Return to main page</a>

</body>
</html>