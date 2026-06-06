function switchTab(tab) {
  clearSelection();
  setActiveTab(tab);

  const tabContents = document.querySelectorAll('.tab-content');
  tabContents.forEach(element => {
    element.classList.remove('active');
  });

  if (tab === 'clean') {
    getElementById('cleanContainer').classList.add('active');
    updateCleanDialogue();
  } else if (tab === 'save') {
    getElementById('dialogueContainer').classList.add('active');
    renderDialogues();
  } else if (tab === 'speed') {
    getElementById('speedContainer').classList.add('active');
    updateSpeedTab();
  } else if (tab === 'loop') {
    getElementById('loopContainer').classList.add('active');
  } else if (tab === 'notes') {
    getElementById('notesContainer').classList.add('active');
  }

  var mainHeader = document.querySelector('#playerView > .player-header');
  if (mainHeader) {
    mainHeader.classList.remove('hidden');
  }

  updateMetaText();
  updateCapsuleActiveState(tab);
}

function updateMetaText() {
  const modeString = getActiveTab().charAt(0).toUpperCase() + getActiveTab().slice(1);
  const loopString = getActiveLoopId() ? `${getActiveLoopId()}` : 'None';
  setTextContent(getElementById('metaMode'), `Mode:${modeString}`);
  setTextContent(getElementById('metaLoop'), `Loop:${loopString}`);
  updateHeaderControls();
}

function updateHeaderControls() {
  updateDropdownValues();
}

function toggleAudioMode() {
  var newMode = !getAudioMode();
  setAudioMode(newMode);
  var playerView = document.getElementById('playerView');
  var badge = document.getElementById('audioModeBadge');
  var iconEl = document.getElementById('cleanIcon');
  var cleanContent = document.getElementById('cleanDialogueContent');

  if (newMode) {
    if (playerView) playerView.classList.add('audio-mode');
    if (badge) badge.classList.remove('hidden');
    if (iconEl) iconEl.classList.remove('hidden');
    if (cleanContent) cleanContent.classList.add('hidden');
  } else {
    if (playerView) playerView.classList.remove('audio-mode');
    if (badge) badge.classList.add('hidden');
    if (iconEl) iconEl.classList.add('hidden');
    if (cleanContent) cleanContent.classList.remove('hidden');
    updateCleanDialogue();
  }
  updateCapsuleAudioState();
  updateMetaText();
  updateDrawerValues();
}

function toggleNavDropdown() {
  var nav = document.getElementById('navPage');
  if (nav) {
    nav.classList.toggle('show');
    if (nav.classList.contains('show')) {
      updateDrawerValues();
    }
  }
}

document.addEventListener('click', function(e) {
  var nav = document.getElementById('navPage');
  
  if (!nav || !nav.classList.contains('show')) return;
  if (e.target.closest('#notesHeaderMenuBtn')) return;
  if (e.target.closest('#loopsHeaderMenuBtn')) return;
  if (e.target.closest('#navCloseBtn')) {
    nav.classList.remove('show');
    return;
  }
  var panel = nav.querySelector('.nav-page-panel');
  if (panel && !panel.contains(e.target)) {
    nav.classList.remove('show');
  }
});

function initTabs() {
  var headerBackBtn = document.getElementById('headerBackBtn');
  var headerMenuBtn = document.getElementById('headerMenuBtn');
  var capsuleMenu = document.getElementById('capsuleMenu');
  var capsuleBackdrop = document.getElementById('capsuleBackdrop');
  
  function toggleCapsuleMenu() {
    if (capsuleMenu) {
      capsuleMenu.classList.toggle('show');
      if (capsuleBackdrop) capsuleBackdrop.classList.toggle('show');
    }
  }

  function closeCapsuleMenu() {
    if (capsuleMenu) {
      capsuleMenu.classList.remove('show');
      if (capsuleBackdrop) capsuleBackdrop.classList.remove('show');
    }
  }

  if (headerBackBtn) {
    headerBackBtn.addEventListener('click', function() {
      goHome();
    });
  }

  if (headerMenuBtn) {
    headerMenuBtn.addEventListener('click', function(e) {
      e.stopPropagation();
      toggleCapsuleMenu();
    });
  }

  if (capsuleMenu) {
    capsuleMenu.addEventListener('click', function(e) {
      e.stopPropagation();
    });
  }

  document.addEventListener('click', function() {
    if (capsuleMenu && capsuleMenu.classList.contains('show')) {
      closeCapsuleMenu();
    }
  });

  var drawerItems = document.querySelectorAll('.nav-item');
  drawerItems.forEach(function(item) {
    item.addEventListener('click', function() {
      if (this.classList.contains('nav-home-btn')) {
        toggleNavDropdown();
        goHome();
        return;
      }
      var action = this.dataset.action;
      var tab = this.dataset.tab;
      if (action === 'speed') {
        var body = document.getElementById('navSpeedBody');
        if (body) body.classList.toggle('show');
        return;
      } else if (action === 'audio-mode') {
        toggleAudioMode();
        updateDrawerValues();
        toggleNavDropdown();
        return;
      }
      if (tab) {
        switchTab(tab);
        toggleNavDropdown();
      }
    });
  });

  var speedDownBtn = document.getElementById('speedDownBtn');
  var speedUpBtn = document.getElementById('speedUpBtn');

  if (speedDownBtn) {
    speedDownBtn.addEventListener('click', function() {
      currentSpeedIndex = Math.max(0, currentSpeedIndex - 1);
      updateDrawerValues();
      updateSpeedIndicator();
      updateMetaText();
      updateSpeedTab();
    });
  }

  if (speedUpBtn) {
    speedUpBtn.addEventListener('click', function() {
      currentSpeedIndex = Math.min(speeds.length - 1, currentSpeedIndex + 1);
      updateDrawerValues();
      updateSpeedIndicator();
      updateMetaText();
      updateSpeedTab();
    });
  }

  updateDrawerValues();
  initCapsuleButtons();
}

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
