function initApp() {
  initAudioList();
  initPlayer();
  initModals();
  initTabs();
  initSettings();
}

var currentSpeedIndex = 2;
var speeds = [0.5, 0.75, 1, 1.25, 1.5, 2];

function cycleSpeed() {
  currentSpeedIndex = (currentSpeedIndex + 1) % speeds.length;
  updateSpeedIndicator();
  updateMetaText();
}

function initSettings() {
  var settingsBtn = document.getElementById('settingsBtn');
  var settingsPopup = document.getElementById('settingsPopup');
  var settingsCloseBtn = document.getElementById('settingsCloseBtn');
  var settingsCancelBtn = document.getElementById('settingsCancelBtn');
  var settingsSaveBtn = document.getElementById('settingsSaveBtn');
  var apiKeyInput = document.getElementById('apiKeyInput');
  var engineSelect = document.getElementById('engineSelect');

  function openSettings() {
    if (settingsPopup) {
      var savedKey = localStorage.getItem('apiKey') || '';
      var savedEngine = localStorage.getItem('engine') || 'gpt-4o';
      if (apiKeyInput) apiKeyInput.value = savedKey;
      if (engineSelect) engineSelect.value = savedEngine;
      settingsPopup.classList.remove('hidden');
      settingsPopup.classList.add('flex');
    }
  }

  function closeSettings() {
    if (settingsPopup) {
      settingsPopup.classList.add('hidden');
      settingsPopup.classList.remove('flex');
    }
  }

  if (settingsBtn) {
    settingsBtn.addEventListener('click', openSettings);
  }
  if (settingsCloseBtn) {
    settingsCloseBtn.addEventListener('click', closeSettings);
  }
  if (settingsCancelBtn) {
    settingsCancelBtn.addEventListener('click', closeSettings);
  }
  if (settingsSaveBtn) {
    settingsSaveBtn.addEventListener('click', function() {
      if (apiKeyInput) localStorage.setItem('apiKey', apiKeyInput.value);
      if (engineSelect) localStorage.setItem('engine', engineSelect.value);
      closeSettings();
    });
  }
  if (settingsPopup) {
    settingsPopup.addEventListener('click', function(e) {
      if (e.target === settingsPopup) closeSettings();
    });
  }
}

document.addEventListener('DOMContentLoaded', initApp);
