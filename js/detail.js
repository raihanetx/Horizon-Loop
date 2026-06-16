var _prevTab = null;

function openNoteDetail(id, text) {
  _prevTab = getActiveTab();
  var detailView = document.getElementById('detailView');
  var contentArea = document.getElementById('contentArea');
  var detailContent = document.getElementById('detailContent');

  detailContent.innerHTML = '<p class="detail-text">' + escapeHtml(text) + '</p>';

  contentArea.classList.add('hidden');
  detailView.classList.remove('hidden');

  history.pushState({ view: 'detail' }, '');
}

function openLoopDetail(data) {
  _prevTab = getActiveTab();
  var detailView = document.getElementById('detailView');
  var contentArea = document.getElementById('contentArea');
  var detailContent = document.getElementById('detailContent');

  detailContent.innerHTML =
    '<div class="detail-field">' +
      '<span class="detail-label">Name</span>' +
      '<div class="detail-value">' + escapeHtml(data.name) + '</div>' +
    '</div>' +
    '<div class="detail-field">' +
      '<span class="detail-label">Start</span>' +
      '<div class="detail-value">' + escapeHtml(data.start || '—') + '</div>' +
    '</div>' +
    '<div class="detail-field">' +
      '<span class="detail-label">End</span>' +
      '<div class="detail-value">' + escapeHtml(data.end || '—') + '</div>' +
    '</div>' +
    '<div class="detail-field">' +
      '<span class="detail-label">Times</span>' +
      '<div class="detail-value">' + escapeHtml(data.count || '1') + '</div>' +
    '</div>';

  contentArea.classList.add('hidden');
  detailView.classList.remove('hidden');

  history.pushState({ view: 'detail' }, '');
}

function closeDetailView() {
  var detailView = document.getElementById('detailView');
  var contentArea = document.getElementById('contentArea');
  detailView.classList.add('hidden');
  contentArea.classList.remove('hidden');
  switchTab(_prevTab || 'clean');
}

window.addEventListener('popstate', function(e) {
  var detailView = document.getElementById('detailView');
  if (detailView && !detailView.classList.contains('hidden')) {
    closeDetailView();
  }
});
