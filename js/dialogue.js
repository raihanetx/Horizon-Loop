const dialogues = [
  { id: 1, time: '0:00', english: 'Good morning! How are you today?', bangla: '\u09b6\u09c1\u09ad \u09b8\u0995\u09be\u09b2! \u0986\u09aa\u09a8\u09bf \u0995\u09c7\u09ae\u09a8 \u0986\u099b\u09c7\u09a8?' },
  { id: 2, time: '2:45', english: 'My name is Rahim. Nice to meet you.', bangla: '\u0986\u09ae\u09be\u09b0 \u09a8\u09be\u09ae \u09b0\u09b9\u09bf\u09ae\u0964 \u0986\u09aa\u09a8\u09be\u09b0 \u09b8\u09be\u09a5\u09c7 \u09a6\u09c7\u0996\u09be \u09b9\u09b2\u09c7 \u0996\u09c1\u09b6\u09bf \u09b9\u09b2\u09be\u09ae\u0964' },
  { id: 3, time: '4:10', english: 'I would like to order a cup of coffee, please.', bangla: '\u0986\u09ae\u09bf \u098f\u0995 \u0995\u09be\u09aa \u0995\u09ab\u09bf \u0985\u09b0\u09cd\u09a1\u09be\u09b0 \u0995\u09b0\u09a4\u09c7 \u099a\u09be\u0987\u0964' },
  { id: 4, time: '6:02', english: 'Excuse me, can you tell me how to get to the station?', bangla: '\u09a6\u09cb\u09af\u09bc\u09be \u0995\u09b0\u09c7, \u09b8\u09cd\u099f\u09c7\u09b6\u09a8\u09c7 \u09af\u09be\u09ac\u09cb \u0995\u09bf\u09ad\u09be\u09ac\u09c7 \u09ac\u09b2\u09a4\u09c7 \u09aa\u09be\u09b0\u09c7\u09a8?' },
  { id: 5, time: '8:30', english: 'I wake up at 6 o\'clock every morning.', bangla: '\u0986\u09ae\u09bf \u09aa\u09cd\u09b0\u09a4\u09bf \u09b8\u0995\u09be\u09b2 \u099b\u09af\u09bc\u099f\u09be\u09af\u09bc \u0998\u09c1\u09ae \u09a5\u09c7\u0995\u09c7 \u0989\u09a0\u09bf\u0964' },
  { id: 6, time: '10:15', english: 'How much does this cost?', bangla: '\u098f\u099f\u09be\u09b0 \u09a6\u09be\u09ae \u0995\u09a4?' },
  { id: 7, time: '12:40', english: 'I have a headache. Where can I find a pharmacy?', bangla: '\u0986\u09ae\u09be\u09b0 \u09ae\u09be\u09a5\u09be \u09ac\u09cd\u09af\u09a5\u09be \u0995\u09b0\u099b\u09c7\u0964 \u0995\u09cb\u09a5\u09be\u09af\u09bc \u09ab\u09be\u09b0\u09cd\u09ae\u09c7\u09b8\u09bf \u09aa\u09be\u09ac\u09cb?' },
  { id: 8, time: '14:55', english: 'Can I please speak to Mr. Khan?', bangla: '\u09ae\u09c7\u09b9\u09c7\u09b0\u09ac\u09be\u09a8\u09bf \u0995\u09b0\u09c7 \u0995\u09b0\u09cd\u09a8\u09c7\u09b2 \u0996\u09be\u09a8\u09c7\u09b0 \u09b8\u09be\u09a5\u09c7 \u0995\u09a5\u09be \u09ac\u09b2\u09a4\u09c7 \u09aa\u09be\u09b0\u09c7\u09a8?' },
  { id: 9, time: '16:20', english: 'It is very sunny today. Don\'t forget your umbrella.', bangla: '\u0986\u099c \u0985\u09a8\u09c7\u0995 \u09b0\u09cb\u09a6 \u09b2\u09be\u0997\u099b\u09c7\u0964 \u0986\u09aa\u09a8\u09be\u09b0 \u099b\u09be\u09a4\u09be \u09ad\u09c1\u09b2\u09ac\u09c7\u09a8 \u09a8\u09be\u0964' },
  { id: 10, time: '18:05', english: 'I work at a software company as an engineer.', bangla: '\u0986\u09ae\u09bf \u098f\u0995\u099f\u09bf \u09b8\u09ab\u09cd\u099f\u0993\u09af\u09bc\u09be\u09b0 \u0995\u09ae\u09cd\u09aa\u09be\u09a8\u09bf\u09a4\u09c7 \u0987\u099e\u09cd\u099c\u09bf\u09a8\u09bf\u09af\u09bc\u09be\u09b0 \u09b9\u09bf\u09b8\u09be\u09ac\u09c7 \u0995\u09be\u099c \u0995\u09b0\u09bf\u0964' },
  { id: 11, time: '20:30', english: 'What time does the bus arrive?', bangla: '\u09ac\u09be\u09b8 \u0995\u09a4\u099f\u09be\u09af\u09bc \u0986\u09b8\u09ac\u09c7?' },
  { id: 12, time: '22:15', english: 'I really enjoyed the movie. It was fantastic!', bangla: '\u0986\u09ae\u09bf \u09b8\u09a4\u09cd\u09af\u09bf\u0987 \u099b\u09ac\u09bf\u099f\u09bf \u0989\u09aa\u09ad\u09cb\u0997 \u0995\u09b0\u09c7\u099b\u09bf\u0964 \u098f\u099f\u09bf \u099a\u09ae\u09ce\u0995\u09be\u09b0 \u099b\u09bf\u09b2!' },
  { id: 13, time: '24:40', english: 'Could you please help me carry this bag?', bangla: '\u0986\u09aa\u09a8\u09bf \u0995\u09bf \u098f\u0987 \u09ac\u09cd\u09af\u09be\u0997\u099f\u09bf \u09ac\u09cb\u099d\u09be\u09a4\u09c7 \u0986\u09ae\u09be\u0995\u09c7 \u09b8\u09be\u09b9\u09be\u09af\u09bc\u09cd\u09af \u0995\u09b0\u09a4\u09c7 \u09aa\u09be\u09b0\u09c7\u09a8?' },
  { id: 14, time: '26:50', english: 'I am learning English to improve my skills.', bangla: '\u0986\u09ae\u09bf \u0986\u09ae\u09be\u09b0 \u09a6\u0995\u09cd\u09b7\u09a4\u09be \u09ac\u09c3\u09a6\u09cd\u09a7\u09bf\u09b0 \u099c\u09a8\u09cd\u09af \u0987\u0982\u09b0\u09c7\u099c\u09bf \u09b6\u09bf\u0996\u099b\u09bf\u0964' },
  { id: 15, time: '28:35', english: 'Happy birthday! Wish you all the best.', bangla: '\u09b6\u09c1\u09ad \u099c\u09a8\u09cd\u09ae\u09a6\u09bf\u09a8! \u0986\u09aa\u09a8\u09be\u09b0 \u09b8\u09ac \u0995\u09bf\u099b\u09c1 \u09ad\u09be\u09b2\u09cb \u09b9\u09cb\u0995\u0964' },
];

function renderDialogues() {
  var list = document.getElementById('dialoguesList');
  var emptyState = document.getElementById('dialogueEmptyState');
  if (!list) return;

  list.innerHTML = '';

  var activeDialogues = translatedDialogues.length > 0 ? translatedDialogues : dialogues;

  if (activeDialogues.length === 0) {
    if (emptyState) emptyState.classList.remove('hidden');
    return;
  }

  if (emptyState) emptyState.classList.add('hidden');

  activeDialogues.forEach(function(d) {
    var card = document.createElement('div');
    card.className = 'dialogue-card';
    card.dataset.id = d.id;

    card.innerHTML =
      '<p class="dialogue-text"><span class="dialogue-time">[' + escapeHtml(d.time) + ']</span> ' + escapeHtml(d.english) + '</p>' +
      '<p class="dialogue-bangla">' + escapeHtml(d.bangla) + '</p>';

    card.addEventListener('click', function() {
      this.classList.toggle('selected');
    });

    list.appendChild(card);
  });
}

function openDialogueSlider(dialogue) {
  var slider = document.getElementById('dialogueSlider');
  var timeEl = document.getElementById('dialogueSliderTime');
  var englishEl = document.getElementById('dialogueSliderEnglish');
  var banglaEl = document.getElementById('dialogueSliderBangla');
  
  if (timeEl) timeEl.textContent = '[' + dialogue.time + ']';
  if (englishEl) englishEl.textContent = dialogue.english;
  if (banglaEl) banglaEl.textContent = dialogue.bangla;
  
  if (slider) slider.classList.remove('hidden');
}

function closeDialogueSlider() {
  var slider = document.getElementById('dialogueSlider');
  if (slider) slider.classList.add('hidden');
}

document.addEventListener('DOMContentLoaded', function() {
  renderDialogues();
  
  var closeBtn = document.getElementById('dialogueSliderClose');
  if (closeBtn) {
    closeBtn.addEventListener('click', closeDialogueSlider);
  }
  
  var slider = document.getElementById('dialogueSlider');
  if (slider) {
    slider.addEventListener('click', function(e) {
      if (e.target === slider) closeDialogueSlider();
    });
  }
});

function parseTimeToSeconds(timeStr) {
  var parts = timeStr.split(':');
  if (parts.length === 2) {
    return parseInt(parts[0]) * 60 + parseInt(parts[1]);
  }
  return 0;
}

function getDialogueByTime(seconds) {
  var activeDialogues = translatedDialogues.length > 0 ? translatedDialogues : dialogues;
  if (activeDialogues.length === 0) return null;
  var firstSec = parseTimeToSeconds(activeDialogues[0].time);
  if (seconds < firstSec) return activeDialogues[0];
  var current = null;
  for (var i = 0; i < activeDialogues.length; i++) {
    var d = activeDialogues[i];
    var dSec = parseTimeToSeconds(d.time);
    if (dSec <= seconds) {
      current = d;
    } else {
      break;
    }
  }
  return current;
}

function updateCleanDialogue() {
  var engEl = document.getElementById('cleanDialogueEnglish');
  var banEl = document.getElementById('cleanDialogueBangla');
  var iconEl = document.getElementById('cleanIcon');
  var cleanContent = document.getElementById('cleanDialogueContent');
  if (!engEl || !banEl) return;

  function setUntranslated() {
    setTextContent(engEl, '');
    setTextContent(banEl, '');
    engEl.classList.add('muted');
  }

  function setTranslated(english, bangla) {
    setTextContent(engEl, english);
    setTextContent(banEl, bangla);
    engEl.classList.remove('muted');
  }

  var list = document.getElementById('dialoguesList');
  if (list) {
    var cards = list.querySelectorAll('.dialogue-card');
    cards.forEach(function(card) {
      card.classList.remove('playing');
    });
  }

  if (getAudioMode()) {
    if (iconEl) iconEl.classList.remove('hidden');
    if (cleanContent) cleanContent.classList.add('hidden');
    setTranslated('', '');
    return;
  }

  if (cleanContent) cleanContent.classList.remove('hidden');

  var hasTranslation = translatedDialogues.length > 0;

  if (!getPlaying() && getCurrentPlaybackTime() === 0) {
    if (iconEl) iconEl.classList.add('hidden');
    if (hasTranslation) {
      var firstDialogue = translatedDialogues.length > 0 ? translatedDialogues[0] : (dialogues.length > 0 ? dialogues[0] : null);
      if (firstDialogue) {
        setTranslated('[' + firstDialogue.time + ']' + firstDialogue.english, firstDialogue.bangla);
      } else {
        setUntranslated();
      }
    } else {
      setUntranslated();
    }
    return;
  }

  if (iconEl) iconEl.classList.add('hidden');
  var dialogue = getDialogueByTime(getCurrentPlaybackTime());
  if (dialogue) {
    setTranslated('[' + dialogue.time + ']' + dialogue.english, dialogue.bangla);

    if (list) {
      var activeCard = list.querySelector('.dialogue-card[data-id="' + dialogue.id + '"]');
      if (activeCard) {
        activeCard.classList.add('playing');
      }
    }
  } else if (hasTranslation) {
    setTranslated('Listening...', '');
  } else {
    setUntranslated();
  }
}
