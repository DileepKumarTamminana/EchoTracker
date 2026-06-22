// ===== Theme handling (persists in localStorage) =====
(function () {
    const KEY = 'echotracker-theme';
    const saved = localStorage.getItem(KEY);
    if (saved) {
        document.documentElement.setAttribute('data-theme', saved);
    } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        document.documentElement.setAttribute('data-theme', 'dark');
    }

    window.toggleTheme = function () {
        const current = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
        const next = current === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', next);
        localStorage.setItem(KEY, next);
        updateToggleIcon();
        document.dispatchEvent(new CustomEvent('themechange', { detail: next }));
    };

    function updateToggleIcon() {
        const btn = document.getElementById('themeToggle');
        if (!btn) return;
        const dark = document.documentElement.getAttribute('data-theme') === 'dark';
        btn.textContent = dark ? '☀️' : '🌙';
        btn.setAttribute('aria-label', dark ? 'Switch to light mode' : 'Switch to dark mode');
    }

    document.addEventListener('DOMContentLoaded', updateToggleIcon);
})();

// ===== Chart theme helpers =====
function chartTextColor() {
    return getComputedStyle(document.body).getPropertyValue('--text').trim() || '#333';
}
function chartGridColor() {
    return getComputedStyle(document.body).getPropertyValue('--border').trim() || '#ddd';
}
const ECHO_PALETTE = ['#2e7d32', '#00bfa5', '#ffb300', '#5c6bc0', '#ec407a', '#26c6da', '#8d6e63', '#7e57c2'];
