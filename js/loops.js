let currentlyPlayingLoop = null;
let editingLoopCard = null;

const loopPopup = document.getElementById('loopPopup');
const loopPopupCloseBtn = document.getElementById('loopPopupCloseBtn');
const loopSaveBtn = document.getElementById('loopSaveBtn');
const loopPreviewBtn = document.getElementById('loopPreviewBtn');
const loopsList = document.getElementById('loopsList');
const loopEmptyState = document.getElementById('loopEmptyState');
const loopPopupTitle = document.getElementById('loopPopupTitle');
const loopNameInput = document.getElementById('loopNameInput');
const loopStartInput = document.getElementById('loopStartInput');
const loopEndInput = document.getElementById('loopEndInput');
const loopCountInput = document.getElementById('loopCountInput');
const loopFabEditBtn = document.getElementById('loopFabEditBtn');
const loopFabDeleteBtn = document.getElementById('loopFabDeleteBtn');
const loopAddBtn = document.getElementById('loopAddBtn');

function updateLoopFab() {
  var selected = loopsList ? loopsList.querySelectorAll('.content-card.selected') : [];
  var count = selected.length;
  if (loopAddBtn) loopAddBtn.classList.toggle('hidden', count > 0);
  if (loopFabEditBtn) loopFabEditBtn.classList.toggle('hidden', count !== 1);
  if (loopFabDeleteBtn) loopFabDeleteBtn.classList.toggle('hidden', count === 0);
}

if (loopFabEditBtn) {
  loopFabEditBtn.addEventListener('click', function() {
    var selected = loopsList ? loopsList.querySelector('.content-card.selected') : null;
    if (selected) openLoopDropdown(selected);
  });
}

if (loopFabDeleteBtn) {
  loopFabDeleteBtn.addEventListener('click', function() {
    var selected = loopsList ? loopsList.querySelectorAll('.content-card.selected') : [];
    if (selected.length === 0) return;
    selected.forEach(function(card) {
      var id = parseInt(card.dataset.id);
      var currentLoop = document.querySelector('.loop-play-btn.playing');
      if (currentLoop && currentLoop.closest('.content-card') === card) {
        stopAllLoopPlayback();
      }
      deleteLoopById(id);
      card.remove();
    });
    updateLoopView();
    updateMetaText();
    updateLoopFab();
  });
}

if (loopAddBtn) {
  loopAddBtn.addEventListener('click', function() {
    openLoopDropdown();
  });
} else {
  console.error('loopAddBtn not found');
}

function parseTimeInput(str) {
  if (str === '' || str === null || str === undefined) return NaN;
  const parts = String(str).split(':');
  if (parts.length === 2) {
    const mins = parseInt(parts[0], 10);
    const secs = parseInt(parts[1], 10);
    if (!isNaN(mins) && !isNaN(secs)) return mins * 60 + secs;
  }
  return Number(str);
}

function formatTimestamp(seconds) {
  if (seconds === '' || seconds === null || seconds === undefined) return '\u2014';
  const s = Number(seconds);
  if (isNaN(s) || s < 0) return '\u2014';
  const mins = Math.floor(s / 60);
  const secs = Math.floor(s % 60);
  return mins + ':' + (secs < 10 ? '0' : '') + secs;
}

function formatTotalDuration(seconds) {
  if (seconds === null || seconds === undefined || isNaN(seconds)) return '\u2014';
  const total = Math.floor(seconds);
  const mins = Math.floor(total / 60);
  const secs = total % 60;
  return mins + ':' + (secs < 10 ? '0' : '') + secs;
}

function calcDuration(start, end) {
  if (start === '' || end === '') return null;
  const s = parseTimeInput(start);
  const e = parseTimeInput(end);
  if (isNaN(s) || isNaN(e)) return null;
  return Math.abs(e - s);
}

const updateLoopView = () => {
  const hasLoops = loopsList && loopsList.children.length > 0;
  loopEmptyState.classList.toggle('hidden', hasLoops);
  loopsList.classList.toggle('hidden', !hasLoops);
};

const stopAllLoopPlayback = () => {
  if (currentlyPlayingLoop) {
    const btn = currentlyPlayingLoop.querySelector('.loop-play-btn');
    if (btn) {
      btn.classList.remove('playing');
      btn.innerHTML = '<svg class="w-4 h-4" viewBox="0 0 256 256" fill="currentColor"><path d="M232.4,114.49,88.32,26.35a16,16,0,0,0-16.2-.3A15.86,15.86,0,0,0,64,39.87V216.13A15.94,15.94,0,0,0,80,232a16.07,16.07,0,0,0,8.36-2.35L232.4,141.51a15.81,15.81,0,0,0,0-27ZM80,215.94V40l143.83,88Z"/></svg>';
    }
    currentlyPlayingLoop = null;
  }
  setPlaying(false);
  setActiveLoopId(null);
  updatePlayBtnUI();
};

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
