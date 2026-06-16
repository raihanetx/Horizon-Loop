function togglePlay() {
  setPlaying(!getPlaying());
  updatePlayBtnUI();
}

function updatePlayBtnUI() {
  var playStateText = getElementById('playStateText');
  var playIcon = getElementById('playIcon');
  if (getPlaying()) {
    setTextContent(playStateText, 'Pause');
    if (playIcon) playIcon.innerHTML = '<svg class="w-6 h-6" viewBox="0 0 256 256" fill="currentColor"><path d="M200,32H160a16,16,0,0,0-16,16V208a16,16,0,0,0,16,16h40a16,16,0,0,0,16-16V48A16,16,0,0,0,200,32Zm0,176H160V48h40ZM96,32H56A16,16,0,0,0,40,48V208a16,16,0,0,0,16,16H96a16,16,0,0,0,16-16V48A16,16,0,0,0,96,32Zm0,176H56V48H96Z"/></svg>';
  } else {
    setTextContent(playStateText, 'Play');
    if (playIcon) playIcon.innerHTML = '<svg class="w-6 h-6" viewBox="0 0 256 256" fill="currentColor"><path d="M232.4,114.49,88.32,26.35a16,16,0,0,0-16.2-.3A15.86,15.86,0,0,0,64,39.87V216.13A15.94,15.94,0,0,0,80,232a16.07,16.07,0,0,0,8.36-2.35L232.4,141.51a15.81,15.81,0,0,0,0-27ZM80,215.94V40l143.83,88Z"/></svg>';
  }
}

function updateProgressUI() {
  var duration = getTotalDuration();
  var percentage = duration > 0 ? (getCurrentPlaybackTime() / duration) * 100 : 0;
  var progressTrack = getElementById('progressTrack');
  var currentTimeText = getElementById('currentTime');

  if (progressTrack) progressTrack.style.width = percentage + '%';
  setTextContent(currentTimeText, formatTime(getCurrentPlaybackTime()));
}

function handleProgressClick(e) {
  var progressBarContainer = getElementById('progressBarContainer');
  if (!progressBarContainer) return;
  
  var rect = progressBarContainer.getBoundingClientRect();
  var clickX = e.clientX - rect.left;
  var percent = Math.min(Math.max(clickX / rect.width, 0), 1);
  setCurrentPlaybackTime(percent * getTotalDuration());
  updateProgressUI();
  updateCleanDialogue();
}

function rewind() {
  setCurrentPlaybackTime(Math.max(0, getCurrentPlaybackTime() - 5));
  updateProgressUI();
  updateCleanDialogue();
}

function forward() {
  setCurrentPlaybackTime(Math.min(getTotalDuration(), getCurrentPlaybackTime() + 5));
  updateProgressUI();
  updateCleanDialogue();
}

function updateSpeedIndicator() {
  var speedIndicator = getElementById('speedIndicator');
  if (speedIndicator) {
    setTextContent(speedIndicator, 'Speed: ' + speeds[currentSpeedIndex] + 'x');
  }
  updateSpeedChips();
}

function updateSpeedChips() {
  var chips = document.querySelectorAll('.speed-chip');
  chips.forEach(function(chip) {
    var s = parseFloat(chip.dataset.speed);
    chip.classList.toggle('active', s === speeds[currentSpeedIndex]);
  });
}

function setSpeed(index) {
  currentSpeedIndex = index;
  updateSpeedIndicator();
  updateMetaText();
  updateDropdownValues();
  updateDrawerValues();
  updateSpeedTab();
}

function initSpeedChips() {
  var chips = document.querySelectorAll('.speed-chip');
  chips.forEach(function(chip) {
    chip.addEventListener('click', function() {
      var s = parseFloat(this.dataset.speed);
      var idx = speeds.indexOf(s);
      if (idx !== -1) setSpeed(idx);
    });
  });
}

function showSpeedPopup(val) {
  var popup = getElementById('speedPopup');
  var text = getElementById('speedPopupText');
  if (!popup || !text) return;

  setTextContent(text, val);
  popup.classList.add('show');

  if (window._speedPopupTimeout) clearTimeout(window._speedPopupTimeout);
  window._speedPopupTimeout = setTimeout(function() {
    popup.classList.remove('show');
  }, 1000);
}

function changeSpeedByGesture(isRightSide) {
  if (isRightSide) {
    if (currentSpeedIndex < speeds.length - 1) {
      currentSpeedIndex++;
    }
  } else {
    if (currentSpeedIndex > 0) {
      currentSpeedIndex--;
    }
  }
  var speed = speeds[currentSpeedIndex];
  var speedText = speed.toFixed(2).replace(/\.00$/, '') + 'x';
  showSpeedPopup(speedText);
  updateSpeedIndicator();
  updateMetaText();
  updateDropdownValues();
  updateDrawerValues();
  updateSpeedTab();
}

function initGestureSpeedControl() {
  var cleanContainer = getElementById('cleanContainer');
  if (!cleanContainer) return;

  var pressTimer;
  var hasTriggered = false;

  function handleStart(e) {
    if (hasTriggered) return;
    var clientX = e.touches ? e.touches[0].clientX : e.clientX;
    var rect = cleanContainer.getBoundingClientRect();
    var relativeX = clientX - rect.left;
    var width = rect.width;
    var leftBoundary = width * 0.3;
    var rightBoundary = width * 0.7;

    if (relativeX < leftBoundary) {
      pressTimer = setTimeout(function() {
        hasTriggered = true;
        changeSpeedByGesture(false);
      }, 350);
    } else if (relativeX > rightBoundary) {
      pressTimer = setTimeout(function() {
        hasTriggered = true;
        changeSpeedByGesture(true);
      }, 350);
    }
  }

  function handleEnd() {
    clearTimeout(pressTimer);
    hasTriggered = false;
  }

  cleanContainer.addEventListener('mousedown', handleStart);
  cleanContainer.addEventListener('mouseup', handleEnd);
  cleanContainer.addEventListener('mouseleave', handleEnd);
  cleanContainer.addEventListener('touchstart', handleStart, { passive: true });
  cleanContainer.addEventListener('touchend', handleEnd);
  cleanContainer.addEventListener('touchcancel', handleEnd);
  cleanContainer.addEventListener('touchmove', handleEnd, { passive: true });
}

function startPlaybackInterval() {
  setInterval(function() {
    if (getPlaying()) {
      setCurrentPlaybackTime(getCurrentPlaybackTime() + 0.1);
      if (getCurrentPlaybackTime() > getTotalDuration()) {
        setCurrentPlaybackTime(0.0);
      }
      var previewEnd = getPreviewEndTime();
      if (previewEnd !== null && getCurrentPlaybackTime() >= previewEnd) {
        setPlaying(false);
        clearPreviewEndTime();
        updatePlayBtnUI();
      }
      updateProgressUI();
      updateCleanDialogue();
    }
  }, 100);
}

function initPlayer() {
  var btnPlay = getElementById('btnPlay');
  var btnRewind = getElementById('btnRewind');
  var btnForward = getElementById('btnForward');
  var progressBarContainer = getElementById('progressBarContainer');
  
  addEventListener(btnPlay, 'click', togglePlay);
  addEventListener(btnRewind, 'click', rewind);
  addEventListener(btnForward, 'click', forward);
  addEventListener(progressBarContainer, 'click', handleProgressClick);
  
  initSpeedChips();
  initGestureSpeedControl();
  startPlaybackInterval();
  updateProgressUI();
}
