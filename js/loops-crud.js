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