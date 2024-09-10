// scripts.js

function showOverviewModal(element) {
    const modal = document.getElementById('modal');
    const title = document.getElementById('modal-title');
    const overview = document.getElementById('modal-overview');

    title.textContent = element.getAttribute('data-title');
    overview.textContent = element.getAttribute('data-overview');

    modal.style.display = 'flex'; // Show the modal
}

function closeModal() {
    const modal = document.getElementById('modal');
    modal.style.display = 'none'; // Hide the modal
}
