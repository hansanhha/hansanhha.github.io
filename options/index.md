---
layout: index
idx-name: options
idx-name-ko: 설정
---

<a id="toggle_nav" onclick="toggleNav(); return false;"></a>

<a id="toggle_lang" onclick="toggleLang(); return false;">english</a>

<a id="toggle_theme" onclick="toggleTheme(); return false;"></a>

<script>
window.addEventListener("DOMContentLoaded", function() {
    const $nav = document.getElementById("toggle_nav");
    const $lang = document.getElementById("toggle_lang");
    const $theme = document.getElementById("toggle_theme");
    const nav = localStorage.getItem("nav");
    const lang = localStorage.getItem("lang");
    const theme = localStorage.getItem("theme");
    let english = true;

    if (lang === "한글") {
        english = false;
        $lang.textContent = "english";
    } else {
        $lang.textContent = "한글";
    }

    if (nav === "false") {
        if (english) {
            $nav.textContent = "activate navigation";
        } else {
            $nav.textContent = "목차 활성화";
        }
        
    } else {
        if (english) {
            $nav.textContent = "deactivate navigation";
        } else {
            $nav.textContent = "목차 비활성화";
        }
    }

    if (theme === "dark") {
        if (english) {
            $theme.textContent = "brightly";
        } else {
            $theme.textContent = "밝게";
        }
    } else {
        if (english) {
            $theme.textContent = "darkly";
        } else {
            $theme.textContent = "어둡게";
        }
    }

    const preferTheme = window.matchMedia("(prefers-color-scheme: dark)");
    preferTheme.addEventListener?.("change", (e) => {
        const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        const $root = document.documentElement;
        const $theme = document.getElementById("toggle_theme");

        if (prefersDark) {
            localStorage.setItem("theme", "dark");
            $root.setAttribute("data-theme", "dark");

            if (localStorage.getItem("lang") === "한글") {
                $theme.textContent = "밝게";
            } else {
                $theme.textContent = "brightly";
            }
            return;
        }

        localStorage.setItem("theme", "light");
        $root.setAttribute("data-theme", "light");
        if (localStorage.getItem("lang") === "한글") {
            $theme.textContent = "어둡게";
        } else {
            $theme.textContent = "darkly";
        }
    });

});

function toggleNav() {
    const nav = document.getElementById("toggle_nav");
    const toggle = localStorage.getItem("nav");
    const toggleLang = localStorage.getItem("lang");
    let english = true;
    
    if (toggleLang !== "english") {
        english = false;
    }

    if (toggle === undefined || toggle === "false") {
        localStorage.setItem("nav", "true");
        if (english) {
            nav.textContent = "deactivate navigation";
        } else {
            nav.textContent = "목차 비활성화";    
        }
        
        location.reload();
        return;
    }
    
    localStorage.setItem("nav", "false");
    if (english) {
        nav.textContent = "activate navigation";
    } else {
        nav.textContent = "목차 활성화";
    }

    location.reload();
}

function toggleLang() {
    const lang = document.getElementById("toggle_lang");
    const toggle = localStorage.getItem("lang");

    if (toggle === undefined || toggle === "english") {
        localStorage.setItem("lang", "한글");
        lang.textContent = "한글";
        location.reload();
        return;
    }

    localStorage.setItem("lang", "english");
    lang.textContent = "english";
    location.reload();
}

function toggleTheme() {
    const $root = document.documentElement;
    const $theme = document.getElementById("toggle_theme");
    let next;

    if (localStorage.getItem("theme") === "dark") {
        next = "light";
        if (localStorage.getItem("lang") === "한글") {
            $theme.textContent = "어둡게";
        } else {
            $theme.textContent = "darkly";
        }
    } else {
        next = "dark";
        if (localStorage.getItem("lang") === "한글") {
            $theme.textContent = "밝게";
        } else {
            $theme.textContent = "brightly";
        }
    }

    localStorage.setItem("theme", next);
    $root.setAttribute("data-theme", next);
}
</script>

<style>
#toggle_nav{
    border-bottom: none;
}
#toggle_lang{
    border-bottom: none;
}
#toggle_theme {
    border-bottom: none;
}
</style>