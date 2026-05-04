const urlParams = new URLSearchParams(window.location.search);
const targetPerfumeId = urlParams.get('id'); //grabs id from url


function formatText(input)
{
    if (!input) return '';
    return input.toLowerCase().split(' ').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
}

async function init()
{
    if (!targetPerfumeId) return;
    const displayHeader = document.getElementById('current-perfume-display');
    displayHeader.innerText = 'Loading...'; // show while waiting
    try
    {
        const response = await fetch(`api/perfume?id=${targetPerfumeId}&limit=1`);
        const data = await response.json();
        displayHeader.innerText = `Matches for: ${formatText(data.baseName)}`;
    } catch (e)
    {
        displayHeader.innerText = 'Could not load perfume name.';
        console.error("Could not load perfume name", e);
    }
}

async function generateRecommendations()
{
    const countInput = document.getElementById('rec-count').value;
    const numberOfRecs = parseInt(countInput);
    const resultsContainer = document.getElementById('results-container-layer');
    resultsContainer.innerHTML = '<p>Finding the perfect matches...</p>';

    if (!targetPerfumeId)
    {
        resultsContainer.innerHTML = '<p>Error: No perfume selected! Go back and try again.</p>';
        return;
    }

    try
    {
        const response = await fetch(`api/perfume?id=${targetPerfumeId}&limit=${numberOfRecs}`);
        if (!response.ok)
            throw new Error("Failed to fetch from API");
        const data = await response.json();
        const displayHeader = document.getElementById('current-perfume-display');
        if (displayHeader)
            displayHeader.innerText = `Matches for: ${formatText(data.baseName)}`;
        if (data.recommendations.length === 0)
        {
            resultsContainer.innerHTML = '<p>No matches found! Try adding more perfumes.</p>';
            return;
        }
        let htmlOutput = `<h3>Found ${data.recommendations.length} matches:</h3>`;
        htmlOutput += `<ul style="list-style-type: none; padding-left: 0;">`;
        data.recommendations.forEach((item, index) =>
        {
            const p = item.perfume;
            const explanation = item.explanation;
            const brand = formatText(p.brand);
            const name = formatText(p.name);
            const score = item.score != null ? `${item.score}% match` : 'N/A';
            htmlOutput += `
                <li class="match-card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                        <strong>[${index + 1}] ${brand} - ${name}</strong>
                        ${score}
                    </div>
                    <details style="cursor: pointer; font-size: 0.9em; color: #555;">
                        <summary style="font-weight: bold; color: #E0588D;">Why this works?</summary>
                        <p style="margin-top: 10px; padding-left: 10px; border-left: 2px solid #ccc; font-style: italic;">
                            ${explanation}
                        </p>
                    </details>
                </li>
            `;
        });

        htmlOutput += `</ul>`;
        resultsContainer.innerHTML = htmlOutput;

    } catch (error)
    {
        console.error("API Error: ", error);
        resultsContainer.innerHTML = '<p>Something went wrong :(</p>';
    }
}
init();