const audioFiles = [
  { id: 1, title: 'Morning Lecture - Chapter 1', size: '24.5 MB', subtitle: true, pin: true, bitrate: '128 kbps', duration: '12:34', durationSec: 754 },
  { id: 2, title: 'Podcast Episode 42', size: '67.2 MB', subtitle: false, pin: false, bitrate: '192 kbps', duration: '45:12', durationSec: 2712 },
  { id: 3, title: 'Music - Relaxing Piano', size: '8.1 MB', subtitle: false, pin: true, bitrate: '320 kbps', duration: '5:23', durationSec: 323 },
  { id: 4, title: 'Interview - Tech Talk', size: '31.8 MB', subtitle: true, pin: false, bitrate: '128 kbps', duration: '18:07', durationSec: 1087 },
  { id: 5, title: 'Audiobook - The Great Gatsby', size: '142.3 MB', subtitle: true, pin: true, bitrate: '256 kbps', duration: '1:23:45', durationSec: 5025 },
  { id: 6, title: 'Language Lesson - Spanish', size: '15.4 MB', subtitle: true, pin: false, bitrate: '128 kbps', duration: '9:48', durationSec: 588 },
  { id: 7, title: 'Ambient Sounds - Rain', size: '52.7 MB', subtitle: false, pin: true, bitrate: '192 kbps', duration: '32:10', durationSec: 1930 },
  { id: 8, title: 'News Briefing Daily', size: '11.3 MB', subtitle: false, pin: false, bitrate: '128 kbps', duration: '7:15', durationSec: 435 }
];

let currentFilter = null;
let searchQuery = '';

function renderAudioList() {
  const list = document.getElementById('audioList');
  if (!list) return;

  let filtered = [...audioFiles];

  if (searchQuery) {
    const q = searchQuery.toLowerCase();
    filtered = filtered.filter(a => a.title.toLowerCase().includes(q));
  }

  if (currentFilter === 'size-desc') {
    filtered.sort((a, b) => parseFloat(b.size) - parseFloat(a.size));
  } else if (currentFilter === 'size-asc') {
    filtered.sort((a, b) => parseFloat(a.size) - parseFloat(b.size));
  } else if (currentFilter === 'subtitle-yes') {
    filtered = filtered.filter(a => a.subtitle);
  } else if (currentFilter === 'subtitle-no') {
    filtered = filtered.filter(a => !a.subtitle);
  } else if (currentFilter === 'pinned') {
    filtered = filtered.filter(a => a.pin);
  }

  list.innerHTML = '';

  if (filtered.length === 0) {
    list.innerHTML = '<div class="empty-list">No lessons found</div>';
    return;
  }

  filtered.forEach(audio => {
    const card = document.createElement('div');
    card.className = 'edu-card' + (audio.pin ? ' pinned' : '');
    card.setAttribute('data-id', audio.id);

    card.innerHTML =
      '<div class="card-icon">' +
        '<svg viewBox="0 0 256 256" fill="currentColor"><path d="M224,64H154.67L126.93,43.2a16.12,16.12,0,0,0-9.6-3.2H72A16,16,0,0,0,56,56V72H40A16,16,0,0,0,24,88V200a16,16,0,0,0,16,16H192.89A15.13,15.13,0,0,0,208,200.89V184h16.89A15.13,15.13,0,0,0,240,168.89V80A16,16,0,0,0,224,64ZM192,200H40V88H85.33l29.87,22.4A8,8,0,0,0,120,112h72Zm32-32H208V112a16,16,0,0,0-16-16H122.67L94.93,75.2a16.12,16.12,0,0,0-9.6-3.2H72V56h45.33L147.2,78.4A8,8,0,0,0,152,80h72Z"/></svg>' +
      '</div>' +
      '<div class="card-info">' +
        '<h3 class="card-title">' + escapeHtml(audio.title) + '</h3>' +
        '<div class="card-meta">' +
          '<span class="card-meta-text">' + escapeHtml(audio.size) + '</span>' +
          '<span class="card-meta-sep"></span>' +
          '<span class="card-meta-text">Subtitle: ' + (audio.subtitle ? 'Yes' : 'No') + '</span>' +
        '</div>' +
      '</div>' +
      '<span class="card-duration">' + escapeHtml(audio.duration) + '</span>' +
      '<button class="card-action-btn" aria-label="Play ' + escapeHtml(audio.title) + '">' +
        '<svg viewBox="0 0 256 256" fill="currentColor"><path d="M232.4,114.49,88.32,26.35a16,16,0,0,0-16.2-.3A15.86,15.86,0,0,0,64,39.87V216.13A15.94,15.94,0,0,0,80,232a16.07,16.07,0,0,0,8.36-2.35L232.4,141.51a15.81,15.81,0,0,0,0-27ZM80,215.94V40l143.83,88Z"/></svg>' +
      '</button>';

    list.appendChild(card);
  });

  initLongPress();
}

function initLongPress() {
  document.querySelectorAll('.edu-card').forEach(function(card) {
    let pressTimer;
    let longPressed = false;

    function handleLongPress() {
      longPressed = true;
      var id = parseInt(card.getAttribute('data-id'));
      var audio = null;
      for (var i = 0; i < audioFiles.length; i++) {
        if (audioFiles[i].id === id) {
          audio = audioFiles[i];
          break;
        }
      }
      if (audio) {
        audio.pin = !audio.pin;
        var pinStatusText = card.querySelector('.pin-status-text');
        if (pinStatusText) {
          pinStatusText.textContent = audio.pin ? 'Pinned: Yes' : 'Pinned: No';
        }
        if (audio.pin) {
          card.classList.add('pinned');
        } else {
          card.classList.remove('pinned');
        }
      }
    }

    function startPress() {
      longPressed = false;
      pressTimer = setTimeout(handleLongPress, 500);
    }

    function cancelPress() {
      clearTimeout(pressTimer);
    }

    function handleClick(e) {
      if (longPressed) {
        e.stopImmediatePropagation();
        longPressed = false;
        return;
      }
      var id = parseInt(card.getAttribute('data-id'));
      for (var i = 0; i < audioFiles.length; i++) {
        if (audioFiles[i].id === id) {
          openPlayer(audioFiles[i]);
          break;
        }
      }
    }

    card.addEventListener('mousedown', startPress);
    card.addEventListener('mouseup', cancelPress);
    card.addEventListener('mouseleave', cancelPress);
    card.addEventListener('touchstart', startPress, { passive: true });
    card.addEventListener('touchend', cancelPress);
    card.addEventListener('touchcancel', cancelPress);
    card.addEventListener('touchmove', cancelPress, { passive: true });
    card.addEventListener('click', handleClick);
  });
}

function openPlayer(audio) {
  var homeView = document.getElementById('homeView');
  var playerView = document.getElementById('playerView');
  var audioTitle = document.getElementById('audioTitle');
  var totalTime = document.getElementById('totalTime');
  var contentArea = document.getElementById('contentArea');
  var playerBottom = document.getElementById('playerBottom');
  var headerTitle = document.getElementById('headerTitle');
  var capsuleMenu = document.getElementById('capsuleMenu');

  setCurrentAudioTitle(audio.title);
  homeView.classList.add('hidden');
  playerView.classList.remove('hidden');
  contentArea.classList.remove('hidden');
  playerBottom.classList.remove('hidden');
  if (capsuleMenu) capsuleMenu.classList.remove('hidden');
  audioTitle.textContent = audio.title;
  totalTime.textContent = audio.duration;
  if (headerTitle) headerTitle.textContent = audio.title;
  setCurrentPlaybackTime(0);
  setTotalDuration(audio.durationSec);
  updateProgressUI();
  updateDropdownValues();
  updateSpeedIndicator();
  renderNotes();
  renderLoops();
  switchTab('clean');
}