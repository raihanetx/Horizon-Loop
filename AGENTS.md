# Project Rules — Horizon Loop

## Separation of Concerns (STRICT)

These rules MUST be followed at all times. No exceptions.

### HTML (`index.html`)
- ONLY pure HTML markup
- NO inline styles (`style=""`)
- Tailwind CSS classes are allowed
- External CDN links for fonts/icons are allowed in `<head>`

### CSS (`css/` folder)
- ALL styling must be in CSS files
- Use `tokens.css` for variables and resets
- Use `utils.css` for utility classes
- Use `components.css` for component styles
- NO styling in HTML or JS files

### JavaScript (`js/` folder)
- ONLY pure JavaScript logic
- NO inline styles (no `element.style.property = value`)
- NO CSS class manipulation for styling purposes only
- Use CSS classes for show/hide toggling (`.hidden`, `.active`, `.show`)
- DOM manipulation is allowed for content, NOT for styling

## Naming Conventions
- CSS classes: kebab-case (e.g., `dialogue-card`, `audio-list`)
- JS functions: camelCase (e.g., `renderDialogues`, `switchTab`)
- CSS variables: kebab-case with `--` prefix (e.g., `--deep`, `--surface`)

## Design Spacing Rules (MUST FOLLOW)

All spacing must be consistent across the app.

### Standard Gap Values
- **Card gap (list to list):** 12px
- **Horizontal padding (left/right):** 15px
- **Vertical padding (top/bottom):** 12px
- **Section gap (search to cards):** 12px

### Rules
1. If you set a gap between cards, use the SAME gap for left/right padding
2. Top padding must equal bottom padding
3. Left padding must equal right padding
4. Gap between elements = gap between element and container edge
5. Never use random values — always use: 8px, 12px, 15px, 16px, 20px

## File Structure
```
css/
  tokens.css    → variables, resets, base styles
  utils.css     → utility classes (layout, spacing, typography)
  components.css → all component styles
js/
  utils.js      → DOM helper functions
  state.js      → app state management
  modal.js      → popup/modal logic
  tabs.js       → tab switching, speed, audio mode
  notes.js      → notes CRUD
  loops.js      → loops CRUD
  dialogue.js   → dialogue data and rendering
  player.js     → playback controls
  audiolist.js  → audio list, filters, search
  app.js        → entry point
```
