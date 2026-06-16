function goHome() {
  var homeView = document.getElementById('homeView');
  var playerView = document.getElementById('playerView');
  var contentArea = document.getElementById('contentArea');
  var playerBottom = document.getElementById('playerBottom');
  var capsuleMenu = document.getElementById('capsuleMenu');

  homeView.classList.remove('hidden');
  playerView.classList.add('hidden');
  contentArea.classList.add('hidden');
  playerBottom.classList.add('hidden');
  if (capsuleMenu) capsuleMenu.classList.add('hidden');
  var nav = document.getElementById('navPage');
  if (nav) nav.classList.remove('show');
  setPlaying(false);
  clearPreviewEndTime();
  updatePlayBtnUI();

  if (getAudioMode()) {
    setAudioMode(false);
    playerView.classList.remove('audio-mode');
    var badge = document.getElementById('audioModeBadge');
    if (badge) badge.classList.add('hidden');
    updateCapsuleAudioState();
  }
}

function toggleFilterDropdown(e) {
  var dropdown = document.getElementById('filterDropdown');
  if (dropdown) {
    dropdown.classList.toggle('hidden');
  }
}

document.addEventListener('click', function(e) {
  var dropdown = document.getElementById('filterDropdown');
  var toggle = document.getElementById('filterToggleBtn');
  if (!dropdown || dropdown.classList.contains('hidden')) return;
  if (!dropdown.contains(e.target) && !toggle.contains(e.target)) {
    dropdown.classList.add('hidden');
  }
});

function applyFilter(filter) {
  if (currentFilter === filter || filter === 'all') {
    currentFilter = null;
  } else {
    currentFilter = filter;
  }

  var options = document.querySelectorAll('[data-filter]');
  options.forEach(function(opt) {
    opt.classList.remove('active');
  });

  if (currentFilter) {
    var activeBtn = document.querySelector('[data-filter="' + currentFilter + '"]');
    if (activeBtn) {
      activeBtn.classList.add('active');
    }
  } else {
    var allBtn = document.querySelector('[data-filter="all"]');
    if (allBtn) {
      allBtn.classList.add('active');
    }
  }

  renderAudioList();
}

function initAudioList() {
  var searchInput = document.getElementById('searchInput');
  var filterToggleBtn = document.getElementById('filterToggleBtn');

  if (searchInput) {
    searchInput.addEventListener('input', function(e) {
      searchQuery = e.target.value;
      renderAudioList();
    });
  }

  if (filterToggleBtn) {
    filterToggleBtn.addEventListener('click', toggleFilterDropdown);
  }

  var filterOptions = document.querySelectorAll('.filter-option[data-filter]');
  filterOptions.forEach(function(btn) {
    btn.addEventListener('click', function() {
      applyFilter(btn.getAttribute('data-filter'));
    });
  });

  renderAudioList();
}