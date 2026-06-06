function formatTime(secs) {
  const m = Math.floor(secs / 60);
  const s = Math.floor(secs % 60);
  return `${m}:${s < 10 ? '0' : ''}${s}`;
}

function generateId() {
  return Date.now();
}

function getElementById(id) {
  return document.getElementById(id);
}

function querySelector(selector) {
  return document.querySelector(selector);
}

function querySelectorAll(selector) {
  return document.querySelectorAll(selector);
}

function addEventListener(element, event, handler) {
  if (element) {
    element.addEventListener(event, handler);
  }
}

function addClass(element, className) {
  if (element) {
    element.classList.add(className);
  }
}

function removeClass(element, className) {
  if (element) {
    element.classList.remove(className);
  }
}

function hasClass(element, className) {
  return element ? element.classList.contains(className) : false;
}

function toggleClass(element, className, force) {
  if (element) {
    element.classList.toggle(className, force);
  }
}

function setTextContent(element, text) {
  if (element) {
    element.textContent = text;
  }
}

function createElement(tag, className) {
  const element = document.createElement(tag);
  if (className) {
    element.className = className;
  }
  return element;
}

function updateSelectionState() {
  if (typeof updateLoopFab === 'function') updateLoopFab();
}

function clearSelection() {
  document.querySelectorAll('.content-card.selected').forEach(function(el) {
    el.classList.remove('selected');
  });
  updateSelectionState();
}

function escapeHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function addLongPress(el, onLongPress, onClick) {
  var timer, longPressed = false;
  function handleLongPress() {
    longPressed = true;
    if (onLongPress) {
      onLongPress(el);
    } else {
      el.classList.toggle('selected');
      updateSelectionState();
    }
  }
  el.addEventListener('mousedown', function() { longPressed = false; timer = setTimeout(handleLongPress, 500); });
  el.addEventListener('mouseup', function() { clearTimeout(timer); });
  el.addEventListener('mouseleave', function() { clearTimeout(timer); });
  el.addEventListener('touchstart', function() { longPressed = false; timer = setTimeout(handleLongPress, 500); }, { passive: true });
  el.addEventListener('touchend', function() { clearTimeout(timer); });
  el.addEventListener('touchmove', function() { clearTimeout(timer); }, { passive: true });
  el.addEventListener('click', function(e) {
    if (longPressed) { e.stopImmediatePropagation(); longPressed = false; return; }
    var anySelected = document.querySelectorAll('.content-card.selected').length > 0;
    if (anySelected) {
      el.classList.toggle('selected');
      updateSelectionState();
      return;
    }
    if (onClick) onClick(el);
  });
}
