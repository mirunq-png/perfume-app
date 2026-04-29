document.addEventListener('DOMContentLoaded', () =>
{
    loadPerfumes();
});

async function loadPerfumes()
{
    try
    {
        const response = await fetch('api/perfume');
        const perfumes = await response.json();
        const tableBody = document.getElementById('perfume-table-body');
        tableBody.innerHTML = '';
        perfumes.forEach(p =>
        {
            const topNotes = p.notes.filter(n => n.layer === 'TOP').map(n => n.name).join(', ') || 'None';
            const heartNotes = p.notes.filter(n => n.layer === 'HEART').map(n => n.name).join(', ') || 'None';
            const baseNotes = p.notes.filter(n => n.layer === 'BASE').map(n => n.name).join(', ') || 'None';
            const row = `
                <tr>
                    <td>${p.brand}</td>
                    <td>${p.name}</td>
                    <td>${p.ml}</td>
                    <td class="notes-column-container">
                        <div class="note-col"><strong>Top</strong><br>${topNotes}</div>
                        <div class="note-col"><strong>Heart</strong><br>${heartNotes}</div>
                        <div class="note-col"><strong>Base</strong><br>${baseNotes}</div>
                    </td>
                    <td>${p.rating || 'N/A'}</td>
                </tr>
            `;
            tableBody.insertAdjacentHTML('beforeend', row);
        });

    } catch (error)
    {
        console.error("api fail: ", error);
    }
}