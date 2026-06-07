// Helper function to show a tab content container
  function showTabContent(el) {
    if (!el) return;
    el.style.setProperty('display', 'flex', 'important');
    el.style.setProperty('flex-direction', 'column', 'important');
    el.style.setProperty('flex', '1', 'important');
    el.style.setProperty('min-height', '0', 'important');
    el.classList.add('active');
  }

  // Helper function to hide a tab content container
  function hideTabContent(el) {
    if (!el) return;
    el.style.setProperty('display', 'none', 'important');
    el.classList.remove('active');
  }

  function switchTab(tab) {
    clearSelection();
    setActiveTab(tab);

    // Get all tab content containers
    const tabContents = document.querySelectorAll('.tab-content');
    
    // Hide all tab contents using inline style with !important to override CSS
    tabContents.forEach(function(element) {
      hideTabContent(element);
    });

    // Show only the selected tab
    if (tab === 'clean') {
      showTabContent(getElementById('cleanContainer'));
      updateCleanDialogue();
    } else if (tab === 'save') {
      showTabContent(getElementById('dialogueContainer'));
      renderDialogues();
    } else if (tab === 'speed') {
      showTabContent(getElementById('speedContainer'));
      updateSpeedTab();
    } else if (tab === 'loop') {
      showTabContent(getElementById('loopContainer'));
    } else if (tab === 'notes') {
      showTabContent(getElementById('notesContainer'));
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