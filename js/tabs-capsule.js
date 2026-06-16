function initCapsuleButtons() {
  var capsuleSubtitleBtn = document.getElementById('capsuleSubtitleBtn');
  var capsuleNoteBtn = document.getElementById('capsuleNoteBtn');
  var capsuleListBtn = document.getElementById('capsuleListBtn');
  var capsuleLoopBtn = document.getElementById('capsuleLoopBtn');
  var capsuleAudioBtn = document.getElementById('capsuleAudioBtn');
  var capsuleTranslateBtn = document.getElementById('capsuleTranslateBtn');

  function closeMenu() {
    if (capsuleMenu) {
      capsuleMenu.classList.remove('show');
      if (capsuleBackdrop) capsuleBackdrop.classList.remove('show');
    }
  }

  if (capsuleSubtitleBtn) {
    capsuleSubtitleBtn.addEventListener('click', function() {
      switchTab('clean');
      updateCapsuleActiveState('clean');
      closeMenu();
    });
  }

  if (capsuleListBtn) {
    capsuleListBtn.addEventListener('click', function() {
      switchTab('save');
      updateCapsuleActiveState('save');
      closeMenu();
    });
  }

  if (capsuleLoopBtn) {
    capsuleLoopBtn.addEventListener('click', function() {
      switchTab('loop');
      updateCapsuleActiveState('loop');
      closeMenu();
    });
  }

  if (capsuleNoteBtn) {
    capsuleNoteBtn.addEventListener('click', function() {
      switchTab('notes');
      updateCapsuleActiveState('notes');
      closeMenu();
    });
  }

  if (capsuleAudioBtn) {
    capsuleAudioBtn.addEventListener('click', function() {
      toggleAudioMode();
      updateCapsuleAudioState();
      closeMenu();
    });
  }

  if (capsuleTranslateBtn) {
    capsuleTranslateBtn.addEventListener('click', function() {
      doTranslate();
      closeMenu();
    });
  }
}

function updateCapsuleActiveState(activeTab) {
  var capsuleSubtitleBtn = document.getElementById('capsuleSubtitleBtn');
  var capsuleListBtn = document.getElementById('capsuleListBtn');
  var capsuleLoopBtn = document.getElementById('capsuleLoopBtn');
  var capsuleNoteBtn = document.getElementById('capsuleNoteBtn');

  if (capsuleSubtitleBtn) capsuleSubtitleBtn.classList.toggle('active', activeTab === 'clean');
  if (capsuleListBtn) capsuleListBtn.classList.toggle('active', activeTab === 'save');
  if (capsuleLoopBtn) capsuleLoopBtn.classList.toggle('active', activeTab === 'loop');
  if (capsuleNoteBtn) capsuleNoteBtn.classList.toggle('active', activeTab === 'notes');
}

function updateCapsuleAudioState() {
  var capsuleAudioBtn = document.getElementById('capsuleAudioBtn');
  var capsuleAudioIcon = document.getElementById('capsuleAudioIcon');
  
  if (capsuleAudioBtn) {
    capsuleAudioBtn.classList.toggle('active', getAudioMode());
  }
  if (capsuleAudioIcon) {
    capsuleAudioIcon.style.color = getAudioMode() ? 'var(--dark)' : '';
  }
}

function updateSpeedTab() {
  var speedVal = getElementById('speedCurrentValue');
  var speed = speeds[currentSpeedIndex];
  if (speedVal) setTextContent(speedVal, speed + 'x');
}

function updateDropdownValues() {
}

function updateDrawerValues() {
  var speedVal = getElementById('navSpeedValue');
  var audioVal = getElementById('navAudioValue');
  var speed = speeds[currentSpeedIndex];
  if (speedVal) setTextContent(speedVal, speed + 'x');
  if (audioVal) setTextContent(audioVal, getAudioMode() ? 'On' : 'Off');
  var steps = document.querySelectorAll('.speed-step');
  steps.forEach(function(s) {
    s.classList.toggle('active', parseFloat(s.dataset.speed) === speed);
  });
}

function doSend() {
  var audioTitle = getCurrentAudioTitle() || 'Horizon Loop';
  var currentTime = formatTime(getCurrentPlaybackTime());
  var speed = speeds[currentSpeedIndex] + 'x';

  var shareData = {
    title: 'Horizon Loop',
    text: 'Listening to: ' + audioTitle + '\nProgress: ' + currentTime + '\nSpeed: ' + speed
  };

  function showPopup(message) {
    var popup = getElementById('sendPopup');
    var popupText = getElementById('sendPopupText');
    if (!popup || !popupText) return;
    popup.classList.remove('hidden');
    setTextContent(popupText, message);
    setTimeout(function() {
      popup.classList.add('hidden');
    }, 1500);
  }

  if (navigator.share) {
    navigator.share(shareData).then(function() {
      showPopup('Sent');
    }).catch(function(err) {
      showPopup('Share failed');
    });
  } else {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(shareData.text).then(function() {
        showPopup('Copied to clipboard');
      }).catch(function() {
        showPopup('Copy failed');
      });
    } else {
      showPopup('Sharing not supported');
    }
  }
}