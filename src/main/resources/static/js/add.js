async function loadBrands() {
    const select = document.getElementById("existingBrand");
    try {
        const response = await fetch("api/brands");
        if (!response.ok) throw new Error("Server error");
        const brands = await response.json();
        select.innerHTML = `<option value="">...</option>`;
        brands.forEach(brand => {
            const option = document.createElement("option");
            option.value = brand;
            option.textContent = formatText(brand);
            select.appendChild(option);
        });
        select.appendChild(new Option("+ Add a new brand...", "NEW"));
    } catch (e) {
        select.innerHTML = `<option value="">-- Could not load brands --</option>`;
        console.error("Failed to load brands:", e);
    }
}

function checkBrandSelection() {
    const val = document.getElementById("existingBrand").value;
    document.getElementById("newBrandContainer").classList.toggle("hidden", val !== "NEW");
}

// actual form
document.getElementById("perfumeForm").addEventListener("submit", async function (e)
{
    e.preventDefault();

    const name = document.getElementById("name").value.trim();
    const brandSelect = document.getElementById("existingBrand").value;
    const brand = brandSelect === "NEW"? document.getElementById("newBrand").value.trim(): brandSelect;
    const ml = parseInt(document.getElementById("ml").value);
    const type = document.getElementById("type").value;
    const topNotes = document.getElementById("topNotes").value.trim();
    const heartNotes = document.getElementById("heartNotes").value.trim();
    const baseNotes = document.getElementById("baseNotes").value.trim();
    const ratingRaw = document.getElementById("rating").value.trim();
    const rating = ratingRaw !== "" ? parseFloat(ratingRaw) : null;
    const seasonCheckboxes = document.querySelectorAll("#seasons-container input[type='checkbox']:checked");
    const seasons = Array.from(seasonCheckboxes).map(cb => cb.value).join(", ");
    //validation
    if (!name)
    {
        showMessage("Perfume name is required.", "error");
        return;
    }
    if (!brand)
    {
        showMessage("Please select or enter a brand.", "error");
        return;
    }
    if (!baseNotes)
    {
        showMessage("Base notes are required.", "error");
        return;
    }
    if (seasonCheckboxes.length === 0)
    {
        showMessage("Please select at least one season.", "error");
        return;
    }
    if (ratingRaw === "" || isNaN(rating) || rating < 0 || rating > 10)
    {
        showMessage("Rating must be a real number between 0 and 10.", "error");
        return;
    }
    //grabbing the data
    const payload =
    {
        brand,
        name,
        ml,
        type,
        topNotes,
        heartNotes,
        baseNotes,
        seasons,
        rating
    };
    try
    {
        const response = await fetch("api/perfume",
        {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (response.status === 201)
        {
            showMessage("Perfume added successfully!", "success");
            resetPageContents();
        }
        else if (response.status === 400)
        {
            showMessage("This perfume is already in your collection.", "error");
            resetPageContents();
        }
        else if (response.status === 200)
        {
            showMessage("This perfume was previously in your collection and has been re-enabled.", "info");
            resetPageContents();
        }
        else
        {
            const data = await response.json();
            showMessage(data.error || "Something went wrong.", "error");
            resetPageContents();
        }
    } catch (e)
    {
        showMessage("Could not reach the server.", "error");
        console.error(e);
    }
});

function showMessage(text, type)
{
    let msg = document.getElementById("formMessage");
    if (!msg)
    {
        msg = document.createElement("p");
        msg.id = "formMessage";
        document.querySelector(".form-container").appendChild(msg);
    }
    msg.textContent = text;
    msg.className = "form-message " + type;
}
function resetPageContents()
{
    document.getElementById("perfumeForm").reset();
    document.getElementById("scraperUrl").value = "";
    document.getElementById("newBrandContainer").classList.add("hidden");
    loadBrands();
}
let loadingInterval = null;
async function fetchFragranticaData()
{
    const urlInput = document.getElementById('scraperUrl');
    const status = document.getElementById('autofillStatus');
    const btn = document.getElementById('btnFetch');
    const url = urlInput.value.trim();
    if (!url)
    {
        status.textContent = 'Please paste a Fragrantica URL first.';
        return;
    }
    btn.disabled = true;
    startLoadingAnimation(status);
    try
    {
        const response = await fetch(`api/import/fragrantica?url=${encodeURIComponent(url)}`);
        if (!response.ok)
            throw new Error(`Server responded with ${response.status}`);
        const data = await response.json();
        const imported = [];
        const missing = [];
        const reviewIds = [];

        if (data.perfumeName)
        {
            document.getElementById('name').value = data.perfumeName;
            imported.push('name');
        }
        else
            missing.push('name');

        if (data.brandName)
        {
            const brandSelect = document.getElementById('existingBrand');
            const match = Array.from(brandSelect.options)
                .find(opt => opt.value.toLowerCase() === data.brandName.toLowerCase());
            if (match)
                brandSelect.value = match.value;
            else
            {
                brandSelect.value = 'NEW';
                document.getElementById('newBrand').value = data.brandName;
            }
            checkBrandSelection();
            imported.push('brand');
        }
        else missing.push('brand');

        if (data.type)
        {
            const typeSelect = document.getElementById('type');
            const match = Array.from(typeSelect.options)
                .find(opt => opt.value.toLowerCase() === data.type.toLowerCase());
            if (match)
            {
                typeSelect.value = match.value;
                imported.push('type');
            } else
                missing.push('type (unrecognized value, please select manually)');
        }
        else
            missing.push('type');

        if (data.rating != null && !isNaN(data.rating))
        {
            document.getElementById('rating').value = data.rating;
            imported.push('rating');
        }
        else
            missing.push('rating');

        if (data.topNotes)
        {
            document.getElementById('topNotes').value = data.topNotes;
            imported.push('top notes');
        }
        else
            missing.push('top notes');

        if (data.heartNotes)
        {
            document.getElementById('heartNotes').value = data.heartNotes;
            imported.push('heart notes');
        }
        else
            missing.push('heart notes');

        if (data.baseNotes)
        {
            document.getElementById('baseNotes').value = data.baseNotes;
            imported.push('base notes');
        }
        else
            missing.push('base notes');

        if (Array.isArray(data.seasons) && data.seasons.length > 0)
        {
            const checkboxes = document.querySelectorAll('#seasons-container input[type="checkbox"]');
            checkboxes.forEach(cb =>
            {
                cb.checked = data.seasons.some(s => s.toUpperCase() === cb.value.toUpperCase());
            });
            imported.push('seasons');
        }
        else
            missing.push('seasons');
        missing.push('ml'); // this data isn't grabbed from fragrantica

        let summary = '';
        if (imported.length > 0)
        {
            summary += `Imported: ${imported.join(', ')}. `;
            summary += `\nPlease check: ${missing.join(', ')}.`;
        }
        showMessage(summary || 'Nothing could be imported from that URL.', missing.length > 0 ? 'info' : 'success');
    } catch (e)
    {
        showMessage('Could not load data from that URL.', 'error');
        console.error('Autofill failed:', e);
    } finally
    {
        stopLoadingAnimation();
        btn.disabled = false;
    }

    function startLoadingAnimation(el)
    {
        let dots = 0;
        el.textContent = 'Please wait';
        loadingInterval = setInterval(() =>
        {
            dots = (dots + 1) % 4; // cycles 0,1,2,3
            el.textContent = 'Please wait' + '.'.repeat(dots);
        }, 400);
    }

    function stopLoadingAnimation()
    {
        clearInterval(loadingInterval);
        loadingInterval = null;
        document.getElementById('autofillStatus').textContent = '';
    }
}

loadBrands();