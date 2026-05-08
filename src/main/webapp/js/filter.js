async function filterByNote()
{
    const noteInput = document.getElementById('searched-note').value;
        const resultsContainer = document.getElementById('results-container-filter1');
        resultsContainer.innerHTML = '<p>Searching the collection...</p>';
        if (!noteInput.trim())
        {
            resultsContainer.innerHTML = '<p>Please enter a note to search for.</p>';
            return;
        }
        try
        {
            const response = await fetch(`api/perfume?note=${encodeURIComponent(noteInput.trim())}`); // encodeURIComponent in case of illegal characters e.g. &
            if (!response.ok)
            throw new Error("Failed to fetch from API");

            const perfumes = await response.json();

            if (perfumes.length === 0)
            {
                resultsContainer.innerHTML = `<p>No perfumes found with the note: <strong>${formatText(noteInput.trim())}</strong></p>`;
                return;
            }

            let htmlOutput = `<h1>Found ${perfumes.length} matches containing '${noteInput}':</h1>`;
            htmlOutput += `<ul style="list-style-type: none; padding-left: 0;">`; // unordered list as container, list items as children

            perfumes.forEach((p, index) =>
            {
                const brand = formatText(p.brand);
                const name = formatText(p.name);
                htmlOutput += `
                    <li class="match-card">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                            <strong>[${index + 1}] ${brand} - ${name}</strong>
                        </div>
                        <div>
                            <span style="font-weight: bold; color: #E0588D;">Notes:</span>
                            ${p.notes.map(n => formatText(n.name)).join(', ')}
                        </div>
                    </li>
                `;
            });
            htmlOutput += `</ul>`; // closing container
            resultsContainer.innerHTML = htmlOutput;

        } catch (error)
        {
            console.error("API Error: ", error);
            resultsContainer.innerHTML = '<p>Something went wrong :(</p>';
        }
}