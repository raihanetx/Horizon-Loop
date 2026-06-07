function switchTab(tab) {
  clearSelection();
  setActiveTab(tab);

  // Get all tab content containers
  const tabContents = document.querySelectorAll('.tab-content');
  
  // Hide all tab contents using inline style (more reliable than CSS class)
  tabContents.forEach(function(element) {
    element.style.display = 'none';
    element.classList.remove('active');
  });

  // Show only the selected tab
  if (tab === 'clean') {
    var cleanContainer = getElementById('cleanContainer');
    if (cleanContainer) {
      cleanContainer.style.display = 'flex';
      cleanContainer.style.flexDirection = 'column';
      cleanContainer.style.flex = '1';
      cleanContainer.style.minHeight = '0';
      cleanContainer.classList.add('active');
    }
    updateCleanDialogue();
  } else if (tab === 'save') {
    var dialogueContainer = getElementById('dialogueContainer');
    if (dialogueContainer) {
      dialogueContainer.style.display = 'flex';
      dialogueContainer.style.flexDirection = 'column';
      dialogueContainer.style.flex = '1';
      dialogueContainer.style.minHeight = '0';
      dialogueContainer.classList.add('active');
    }
    renderDialogues();
  } else if (tab === 'speed') {
    var speedContainer = getElementById('speedContainer');
    if (speedContainer) {
      speedContainer.style.display = 'flex';
      speedContainer.style.flexDirection = 'column';
      speedContainer.style.flex = '1';
      speedContainer.style.minHeight = '0';
      speedContainer.classList.add('active');
    }
    updateSpeedTab();
  } else if (tab === 'loop') {
    var loopContainer = getElementById('loopContainer');
    if (loopContainer) {
      loopContainer.style.display = 'flex';
      loopContainer.style.flexDirection = 'column';
      loopContainer.style.flex = '1';
      loopContainer.style.minHeight = '0';
      loopContainer.classList.add('active');
    }
  } else if (tab === 'notes') {
    var notesContainer = getElementById('notesContainer');
    if (notesContainer) {
      notesContainer.style.display = 'flex';
      notesContainer.style.flexDirection = 'column';
      notesContainer.style.flex = '1';
      notesContainer.style.minHeight = '0';
      notesContainer.classList.add('active');
    }
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