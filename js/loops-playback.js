const createLogCard = (data) => {
  const card = document.createElement('div');
  card.className = 'content-card';
  card.dataset.payload = JSON.stringify(data);
  card.dataset.id = data.id;

  const startSec = parseTimeInput(data.start);
  const endSec = parseTimeInput(data.end);
  const duration = !isNaN(startSec) && !isNaN(endSec) ? Math.abs(endSec - startSec) : null;
  const loopCount = data.count || 1;
  const total = duration !== null ? duration * loopCount : null;

  let timeInfo = '';
  if (data.start !== '' && data.end !== '') {
    timeInfo = `<span class="card-meta-text">Time:${formatTimestamp(startSec)}-${formatTimestamp(endSec)}</span><span class="card-meta-sep"></span><span class="card-meta-text">Total:${formatTotalDuration(total)} min</span><span class="card-meta-sep"></span><span class="card-meta-text">Loop:${loopCount} time</span>`;
  } else {
    timeInfo = '<span class="card-meta-text">No time set</span>';
  }

  card.innerHTML = `
    <div class="card-content">
      <div class="card-icon-box"><svg class="w-6 h-6" viewBox="0 0 256 256" fill="currentColor"><path d="M128,40a96,96,0,1,0,96,96A96.11,96.11,0,0,0,128,40Zm0,176a80,80,0,1,1,80-80A80.09,80.09,0,0,1,128,216ZM173.66,90.34a8,8,0,0,1,0,11.32l-40,40a8,8,0,0,1-11.32-11.32l40-40A8,8,0,0,1,173.66,90.34ZM96,16a8,8,0,0,1,8-8h48a8,8,0,0,1,0,16H104A8,8,0,0,1,96,16Z"/></svg></div>
      <div class="min-w-0">
        <h3 class="card-title">${escapeHtml(data.name)}</h3>
        <div class="card-meta">
          ${timeInfo}
        </div>
      </div>
    </div>
  `;

  addLongPress(card, function(el) {
    el.classList.add('selected');
    updateLoopFab();
  }, function(el) {
    if (el.classList.contains('selected')) return;
    const startSec = parseTimeInput(data.start);
    const endSec = parseTimeInput(data.end);
    if (!isNaN(startSec) && !isNaN(endSec) && startSec < endSec) {
      stopAllLoopPlayback();
      setCurrentPlaybackTime(startSec);
      setPreviewEndTime(endSec);
      setPlaying(true);
      updatePlayBtnUI();
      updateProgressUI();
      switchTab('clean');
    }
  });

  return card;
};

const renderLoops = () => {
  if (!loopsList) return;
  loopsList.innerHTML = '';
  const loops = getLoops();
  loops.forEach(function(loop) {
    loopsList.appendChild(createLogCard(loop));
  });
  updateLoopView();
  clearSelection();
  updateLoopFab();
};

const openLoopDropdown = (cardToEdit = null) => {
  editingLoopCard = cardToEdit;
  if (cardToEdit) {
    loopPopupTitle.textContent = 'Edit Log Entry';
    const data = JSON.parse(cardToEdit.dataset.payload);
    loopNameInput.value = data.name;
    loopStartInput.value = data.start || '';
    loopEndInput.value = data.end || '';
    loopCountInput.value = data.count || '';
  } else {
    loopPopupTitle.textContent = 'New Log Entry';
    loopNameInput.value = '';
    loopStartInput.value = '';
    loopEndInput.value = '';
    loopCountInput.value = '';
  }
  loopPopup.classList.add('show');
  setTimeout(() => loopNameInput.focus(), 50);
};

const closeLoopDropdown = () => {
  loopPopup.classList.remove('show');
  editingLoopCard = null;
};

const openPreview = () => {
  const startVal = loopStartInput.value;
  const endVal = loopEndInput.value;

  if (startVal === '' || endVal === '') return;

  const startSec = parseTimeInput(startVal);
  const endSec = parseTimeInput(endVal);

  if (isNaN(startSec) || isNaN(endSec) || startSec >= endSec) return;

  closeLoopDropdown();
  setCurrentPlaybackTime(startSec);
  setPreviewEndTime(endSec);
  setPlaying(true);
  updatePlayBtnUI();
  updateProgressUI();
};

loopPopup.addEventListener('click', function(e) { if (e.target === this) closeLoopDropdown(); });
loopPopupCloseBtn.addEventListener('click', closeLoopDropdown);
loopSaveBtn.addEventListener('click', () => {
  const data = {
    id: editingLoopCard ? parseInt(editingLoopCard.dataset.id) : generateId(),
    name: loopNameInput.value.trim() || 'Untitled Log',
    start: loopStartInput.value,
    end: loopEndInput.value,
    count: loopCountInput.value.trim() ? parseInt(loopCountInput.value) : 1,
  };

  if (editingLoopCard) {
    const newCard = createLogCard(data);
    editingLoopCard.replaceWith(newCard);
    updateLoopById(data.id, data);
  } else {
    addLoop(data);
    loopsList.prepend(createLogCard(data));
  }
  updateLoopView();
  closeLoopDropdown();
  updateMetaText();
  updateLoopFab();
});

loopPreviewBtn.addEventListener('click', openPreview);

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape' && loopPopup.classList.contains('show')) closeLoopDropdown();
});