const AppState = {
  activeTab: 'clean',
  notes: [],
  loops: [],
  isPlaying: false,
  currentPlaybackTime: 0.0,
  totalDuration: 0.0,
  activeLoopId: null,
  audioMode: false,
  previewEndTime: null,
  selectedDialogues: [],
  currentAudioTitle: ''
};

function getState() {
  return AppState;
}

function setActiveTab(tab) {
  AppState.activeTab = tab;
}

function getActiveTab() {
  return AppState.activeTab;
}

function setPlaying(playing) {
  AppState.isPlaying = playing;
}

function getPlaying() {
  return AppState.isPlaying;
}

function setCurrentPlaybackTime(time) {
  AppState.currentPlaybackTime = time;
}

function getCurrentPlaybackTime() {
  return AppState.currentPlaybackTime;
}

function getTotalDuration() {
  return AppState.totalDuration;
}

function setTotalDuration(duration) {
  AppState.totalDuration = duration;
}

function setActiveLoopId(id) {
  AppState.activeLoopId = id;
}

function getActiveLoopId() {
  return AppState.activeLoopId;
}

function getNotes() {
  return AppState.notes;
}

function addNote(note) {
  AppState.notes.push(note);
}

function updateNote(id, text) {
  AppState.notes = AppState.notes.map(n =>
    n.id === id ? { ...n, text } : n
  );
}

function deleteNoteById(id) {
  AppState.notes = AppState.notes.filter(n => n.id !== id);
}

function getLoops() {
  return AppState.loops;
}

function addLoop(loop) {
  AppState.loops.push(loop);
}

function updateLoopById(id, data) {
  AppState.loops = AppState.loops.map(l => l.id === id ? { ...l, ...data } : l);
}

function deleteLoopById(id) {
  if (AppState.activeLoopId === id) {
    AppState.activeLoopId = null;
  }
  AppState.loops = AppState.loops.filter(l => l.id !== id);
}

function findNoteById(id) {
  return AppState.notes.find(n => n.id === id);
}

function setAudioMode(mode) { AppState.audioMode = mode; }
function getAudioMode() { return AppState.audioMode; }
function setCurrentAudioTitle(title) { AppState.currentAudioTitle = title; }
function getCurrentAudioTitle() { return AppState.currentAudioTitle; }
function setPreviewEndTime(time) { AppState.previewEndTime = time; }
function getPreviewEndTime() { return AppState.previewEndTime; }
function clearPreviewEndTime() { AppState.previewEndTime = null; }
function getSelectedDialogues() { return AppState.selectedDialogues; }
function toggleDialogueSelection(id) {
  var idx = AppState.selectedDialogues.indexOf(id);
  if (idx === -1) {
    AppState.selectedDialogues.push(id);
  } else {
    AppState.selectedDialogues.splice(idx, 1);
  }
}
function isDialogueSelected(id) {
  return AppState.selectedDialogues.indexOf(id) !== -1;
}
function clearSelectedDialogues() { AppState.selectedDialogues = []; }