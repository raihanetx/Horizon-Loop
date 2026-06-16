let editingNoteCard = null;

const notePopup = document.getElementById('notePopup');
const notePopupCloseBtn = document.getElementById('notePopupCloseBtn');
const noteSaveBtn = document.getElementById('noteSaveBtn');
const noteCancelBtn = document.getElementById('noteCancelBtn');
const notesList = document.getElementById('notesList');
const noteEmptyState = document.getElementById('noteEmptyState');
const notePopupTitle = document.getElementById('notePopupTitle');
const noteInput = document.getElementById('noteInput');

const updateNoteView = () => {
  const hasNotes = notesList && notesList.children.length > 0;
  noteEmptyState.classList.toggle('hidden', hasNotes);
  notesList.classList.toggle('hidden', !hasNotes);
};

const openNoteDropdown = (cardToEdit = null) => {
  editingNoteCard = cardToEdit;
  if (cardToEdit) {
    notePopupTitle.textContent = 'Edit Note';
    noteInput.value = cardToEdit.querySelector('.content-card-text').textContent;
  } else {
    notePopupTitle.textContent = 'Add Note';
    noteInput.value = '';
  }
  notePopup.classList.add('show');
  setTimeout(() => noteInput.focus(), 50);
};

const closeNoteDropdown = () => {
  notePopup.classList.remove('show');
  editingNoteCard = null;
};

const createNoteCard = (id, text, date) => {
  const card = document.createElement('div');
  card.className = 'content-card';
  card.dataset.id = id;

  const wordCount = text.trim().split(/\s+/).filter(Boolean).length;
  const charCount = text.length;
  const dateStr = date || 'Unknown date';

  const shortText = text.length > 40 ? text.substring(0, 40) + '...' : text;

  card.innerHTML = `
    <div class="card-content">
      <div class="card-icon-box"><svg class="w-6 h-6" viewBox="0 0 256 256" fill="currentColor"><path d="M208,32H184V24a8,8,0,0,0-16,0v8H88V24a8,8,0,0,0-16,0v8H48A16,16,0,0,0,32,48V208a16,16,0,0,0,16,16H208a16,16,0,0,0,16-16V48A16,16,0,0,0,208,32ZM72,48v8a8,8,0,0,0,16,0V48h80v8a8,8,0,0,0,16,0V48h24V80H48V48ZM208,208H48V96H208V208Zm-48-56a8,8,0,0,1-8,8H104a8,8,0,0,1,0-16h48A8,8,0,0,1,160,152Z"/></svg></div>
      <div class="min-w-0">
        <h3 class="card-title">${escapeHtml(shortText)}</h3>
        <div class="card-meta">
          <span class="card-meta-text">${dateStr}</span><span class="card-meta-sep"></span><span class="card-meta-text">${wordCount} words</span><span class="card-meta-sep"></span><span class="card-meta-text">${charCount} chars</span>
        </div>
      </div>
    </div>
  `;

  card.addEventListener('click', function(e) {
    if (card.classList.contains('selected')) return;
    openNoteDetail(id, text);
  });

  addLongPress(card);

  return card;
};

const renderNotes = () => {
  if (!notesList) return;
  notesList.innerHTML = '';
  const notes = getNotes();
  notes.forEach(function(note) {
    notesList.appendChild(createNoteCard(note.id, note.text, note.date));
  });
  updateNoteView();
  clearSelection();
};

notePopup.addEventListener('click', function(e) { if (e.target === this) closeNoteDropdown(); });
notePopupCloseBtn.addEventListener('click', closeNoteDropdown);
noteCancelBtn.addEventListener('click', closeNoteDropdown);

noteSaveBtn.addEventListener('click', () => {
  const text = noteInput.value.trim();
  if (!text) return;

  if (editingNoteCard) {
    editingNoteCard.querySelector('.content-card-text').textContent = text;
    updateNote(parseInt(editingNoteCard.dataset.id), text);
  } else {
    const id = generateId();
    addNote({ id: id, text: text, date: new Date().toLocaleDateString() });
    notesList.prepend(createNoteCard(id, text, new Date().toLocaleDateString()));
  }

  updateNoteView();
  closeNoteDropdown();
});

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape' && notePopup.classList.contains('show')) closeNoteDropdown();
});

const noteAddBtn = document.getElementById('noteAddBtn');
if (noteAddBtn) {
  noteAddBtn.addEventListener('click', function() {
    openNoteDropdown();
  });
}
