var translatedDialogues = [];

function closeCapsuleMenu() {
  var capsuleMenu = document.getElementById('capsuleMenu');
  var capsuleBackdrop = document.getElementById('capsuleBackdrop');
  if (capsuleMenu) capsuleMenu.classList.remove('show');
  if (capsuleBackdrop) capsuleBackdrop.classList.remove('show');
}

function setProgress(percent) {
  var text = document.getElementById('translatePopupText');
  if (text && percent >= 0) {
    text.textContent = Math.round(percent) + '%';
  }
}

function doTranslate() {
  closeCapsuleMenu();

  var popup = document.getElementById('translatePopup');
  if (popup) popup.classList.remove('hidden');

  var progress = 0;
  var duration = 3000;
  var startTime = null;

  function animate(timestamp) {
    if (!startTime) startTime = timestamp;
    var elapsed = timestamp - startTime;
    progress = Math.min((elapsed / duration) * 100, 100);
    setProgress(progress);

    if (progress < 100) {
      requestAnimationFrame(animate);
    } else {
      setTimeout(function() {
        if (popup) popup.classList.add('hidden');

        translatedDialogues = [
        { id: 1, time: '0:00', english: 'Good morning! Welcome to the lesson.', bangla: '\u09b6\u09c1\u09ad \u09b8\u0995\u09be\u09b2! \u09aa\u09be\u09a0\u09c7 \u09b8\u09cd\u09ac\u09be\u0997\u09a4\u09ae\u0964' },
        { id: 2, time: '0:45', english: 'Today we will learn about daily conversations.', bangla: '\u0986\u099c \u0986\u09ae\u09b0\u09be \u09a6\u09c8\u09a8\u09bf\u0995 \u0995\u09a5\u09be\u09aa\u09c7\u09b6\u09a8 \u09b8\u09ae\u09cd\u09aa\u09b0\u09cd\u0995\u09c7 \u09b6\u09bf\u0996\u09ac\u09cb\u0964' },
        { id: 3, time: '1:30', english: 'First, let me introduce myself.', bangla: '\u09aa\u09cd\u09b0\u09a5\u09ae\u09c7, \u0986\u09ae\u09bf \u09a8\u09bf\u099c\u09c7\u09b0 \u09aa\u09b0\u09bf\u099a\u09af\u09bc \u09a6\u09bf\u0987\u0964' },
        { id: 4, time: '2:15', english: 'My name is John. I am your teacher.', bangla: '\u0986\u09ae\u09be\u09b0 \u09a8\u09be\u09ae \u099c\u09be\u09b9\u09a8\u0964 \u0986\u09ae\u09bf \u0986\u09aa\u09a8\u09be\u09b0 \u09b6\u09bf\u0995\u09cd\u09b7\u0995\u0964' },
        { id: 5, time: '3:00', english: 'Nice to meet you all!', bangla: '\u0986\u09aa\u09a8\u09be\u09a6\u09c7\u09b0 \u09b8\u09be\u09a5\u09c7 \u09a6\u09c7\u0996\u09be \u09b9\u09b2\u09c7 \u0996\u09c1\u09b6\u09bf \u09b9\u09b2\u09be\u09ae\u0964' },
        { id: 6, time: '3:45', english: 'Please repeat after me.', bangla: '\u0985\u09a8\u09c1\u0997\u09cd\u09b0\u09b9 \u0995\u09b0\u09c7 \u0986\u09ae\u09be\u09b0 \u09aa\u09b0\u09c7 \u09aa\u09c1\u09a8\u09b0\u09be\u09ac\u09c3\u09a4\u09cd\u09a4\u09bf \u0995\u09b0\u09c1\u09a8\u0964' },
        { id: 7, time: '4:30', english: 'How are you today?', bangla: '\u0986\u09aa\u09a8\u09bf \u0986\u099c \u0995\u09c7\u09ae\u09a8 \u0986\u099b\u09c7\u09a8?' },
        { id: 8, time: '5:15', english: 'I am fine, thank you!', bangla: '\u0986\u09ae\u09bf \u09ad\u09be\u09b2\u09cb \u0986\u099b\u09bf, \u09a7\u09a8\u09cd\u09af\u09ac\u09be\u09a6!' },
        { id: 9, time: '6:00', english: 'What is your name?', bangla: '\u0986\u09aa\u09a8\u09be\u09b0 \u09a8\u09be\u09ae \u0995\u09c0?' },
        { id: 10, time: '6:45', english: 'Where are you from?', bangla: '\u0986\u09aa\u09a8\u09bf \u0995\u09cb\u09a5\u09be\u09af\u09bc \u09a5\u09c7\u0995\u09c7 \u098f\u09b8\u09c7\u099b\u09c7\u09a8?' }
        ];
        updateDialogueData(translatedDialogues);
        renderDialogues();

        var translateBtn = document.getElementById('capsuleTranslateBtn');
        if (translateBtn) translateBtn.style.display = 'none';
      }, 500);
    }
  }

  requestAnimationFrame(animate);
}

function updateDialogueData(newDialogues) {
  window.dialogues = newDialogues;
}
