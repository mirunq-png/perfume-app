const params = new URLSearchParams(window.location.search);
const perfumeId = params.get("id");

async function loadBrands(currentBrand) {
    const select = document.getElementById("existingBrand");
    try {
        const response = await fetch("api/brands");
        if (!response.ok) throw new Error("Server error");
        const brands = await response.json();
        select.innerHTML = `<option value="">...</option>`;
        brands.forEach(brand => {
            const option = document.createElement("option");
            option.value = formatText(brand);
            option.textContent = formatText(brand);
            if (formatText(brand) === currentBrand)
                option.selected = true;
            select.appendChild(option);
        });
        select.appendChild(new Option("[+] Add a new brand...", "NEW"));
    } catch (e) {
        select.innerHTML = `<option value="">-- Could not load brands --</option>`;
        console.error("Failed to load brands:", e);
    }
}

function checkBrandSelection() {
    const val = document.getElementById("existingBrand").value;
    document.getElementById("newBrandContainer").classList.toggle("hidden", val !== "NEW");
}

async function loadPerfume()
{
    if (!perfumeId)
    {
        showMessage("No perfume ID provided.", "error");
        return;
    }
    try
    {
        const response = await fetch(`api/perfume?fetch=${perfumeId}`);
        if (!response.ok) throw new Error("Not found");
        const p = await response.json();
        document.getElementById("name").value = formatText(p.name) || "";
        document.getElementById("rating").value = p.rating || "";
        const mlSelect = document.getElementById("ml");
        for (let opt of mlSelect.options)
            if (parseInt(opt.value) === p.ml)
                opt.selected = true;
        const typeSelect = document.getElementById("type");
        for (let opt of typeSelect.options)
            if (opt.value === p.type)
                opt.selected = true;
        const topNotes=p.notes.filter(n=> n.layer === "TOP").map(n => n.name).join(", ");
        const heartNotes=p.notes.filter(n => n.layer === "HEART").map(n => n.name).join(", ");
        const baseNotes=p.notes.filter(n => n.layer === "BASE").map(n => n.name).join(", ");
        document.getElementById("topNotes").value=formatText(topNotes);
        document.getElementById("heartNotes").value=formatText(heartNotes);
        document.getElementById("baseNotes").value=formatText(baseNotes);
        const seasonCheckboxes = document.querySelectorAll("#seasons-container input[type='checkbox']");
        seasonCheckboxes.forEach(cb =>{cb.checked = p.seasons.includes(cb.value);});
        await loadBrands(formatText(p.brand));
    } catch (e)
    {
        showMessage("Could not load perfume data.", "error");
        console.error(e);
    }
}

//put
document.getElementById("perfumeForm").addEventListener("submit", async function (e)
{
    e.preventDefault();
    const name = document.getElementById("name").value.trim();
    const brandSelect = document.getElementById("existingBrand").value;
    const brand = brandSelect === "NEW" ? document.getElementById("newBrand").value.trim() : brandSelect;
    const ml = parseInt(document.getElementById("ml").value);
    const type = document.getElementById("type").value;
    const topNotes= document.getElementById("topNotes").value.trim();
    const heartNotes= document.getElementById("heartNotes").value.trim();
    const baseNotes= document.getElementById("baseNotes").value.trim();
    const ratingRaw= document.getElementById("rating").value.trim();
    const rating = ratingRaw !== "" ? parseFloat(ratingRaw) : null;
    const seasonCheckboxes = document.querySelectorAll("#seasons-container input[type='checkbox']:checked");
    const seasons= Array.from(seasonCheckboxes).map(cb => cb.value).join(", ");
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
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (response.status === 200)
            showMessage("Perfume updated successfully!", "success");
        else
        {
            const data = await response.json();
            showMessage(data.error || "Something went wrong.", "error");
        }
    } catch (e)
    {
        showMessage("Could not reach the server.", "error");
        console.error(e);
    }
});

//delete
document.getElementById("deleteBtn").addEventListener("click", async function ()
{
    if (!confirm("Are you sure you want to delete this perfume? This cannot be undone."))
        return;
    try
    {
        const response = await fetch(`api/perfume?id=${perfumeId}`, { method: "DELETE" });
        if (response.status === 200)
        {
            resetPageContents();
            showMessage("Perfume deleted successfully!", "success");
            setTimeout(() => window.location.href = "collection.html", 3000);
        } else
        {
            const data = await response.json();
            showMessage(data.error || "Could not delete perfume.", "error");
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
    document.getElementById("newBrandContainer").classList.add("hidden");
    loadBrands();
}
loadPerfume();