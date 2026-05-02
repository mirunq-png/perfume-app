document.addEventListener('DOMContentLoaded', () =>
{
    loadPerfumes();
}); // waits

function formatText(input)
{
    return input.toLowerCase().split(' ').map(word=>word.charAt(0).toUpperCase()+word.slice(1)).join(' ');
}

async function loadPerfumes()
{
    try
    {
        const response = await fetch('api/perfume');
        const perfumes = await response.json();
        const tableBody = document.getElementById('perfume-table-body');
        tableBody.innerHTML = ''; //eraser before new data
        perfumes.forEach(p =>
        {
        console.log(p);
            const topNotes = p.notes.filter(n => n.layer === 'TOP').map(n => n.name).join(', ') || '-';
            const heartNotes = p.notes.filter(n => n.layer === 'HEART').map(n => n.name).join(', ') || '-';
            const baseNotes = p.notes.filter(n => n.layer === 'BASE').map(n => n.name).join(', ') || '-';
            const row = `
                <tr id="row-${p.parfum_id}">
                    <td><input type="radio" name="perfume-select" onclick="handleSelect(this)"></td>
                    <td>${formatText(p.brand)}</td>
                    <td>${formatText(p.name)}</td>
                    <td>${p.ml} ml</td>
                    <td class="notes-column-container">
                        <div class="note-cols">
                            <div class="note-col"><strong>Top</strong> ${formatText(topNotes)}</div>
                            <div class="note-col"><strong>Heart</strong> ${formatText(heartNotes)}</div>
                            <div class="note-col"><strong>Base</strong> ${formatText(baseNotes)}</div>
                        </div>
                    </td>
                    <td style="vertical-align: middle;">
                        ${p.rating ? p.rating + '/10' : 'N/A'}
                    </td>
                    <td style="vertical-align: middle;">
                                <div class="action-buttons" style="display:none;">
                                    <button onclick="window.location.href='layer.html?id=${p.id}'">Layer</button>
                                    <button onclick="edit(${p.id})">Edit</button>
                                </div>
                            </td>
                </tr>
            `;
            tableBody.insertAdjacentHTML('beforeend', row); //beforeend=top to bottom addition
        });

    } catch (error)
    {
        console.error("api fail: ", error);
    }
}

function handleSelect(radioInput) {
    // hides [Layer] [Edit]
    document.querySelectorAll('.action-buttons').forEach(div => {
        div.style.display = 'none';
    });
    document.querySelectorAll('#perfume-table-body tr').forEach(row => {
        row.classList.remove('selected-row');
    });

    //shows
    const selectedRow = radioInput.closest('tr');
    selectedRow.classList.add('selected-row');
    const buttonsDiv = selectedRow.querySelector('.action-buttons');
    buttonsDiv.style.display = 'inline-flex';
}