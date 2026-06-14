---
layout: index
idx-name: Options
idx-name-ko: 설정
---

<a id="toggle_nav" onclick="toggleNav(); return false;"></a>

<a id="toggle_lang" onclick="toggleLang(); return false;">english</a>

<a id="toggle_theme" onclick="toggleTheme(); return false;"></a>

<!-- <a id="toggle_additional_info" onclick="toggleAdditionalInfo(); return false;"></a> -->

<script>
window.addEventListener("DOMContentLoaded", function() {
    const $nav = document.getElementById("toggle_nav");
    const $lang = document.getElementById("toggle_lang");
    const $theme = document.getElementById("toggle_theme");
    //const $addtionalInfo = document.getElementById("toggle_additional_info");
    const nav = localStorage.getItem("nav");
    const lang = localStorage.getItem("lang");
    const theme = localStorage.getItem("theme");
    //const addtionalInfo = localStorage.getItem("addtional_info");
    let english = true;

    if (lang === "한글") {
        english = false;
        $lang.textContent = "English";
    } else {
        $lang.textContent = "한글";
    }

    if (nav === "false") {
        if (english) {
            $nav.textContent = "Activate Navigation";
        } else {
            $nav.textContent = "목차 활성화";
        }
        
    } else {
        if (english) {
            $nav.textContent = "Deactivate Navigation";
        } else {
            $nav.textContent = "목차 비활성화";
        }
    }

    if (theme === "dark") {
        if (english) {
            $theme.textContent = "Brightly";
        } else {
            $theme.textContent = "밝게";
        }
    } else {
        if (english) {
            $theme.textContent = "Darkly";
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

    // if (addtionalInfo == undefined || addtionalInfo === "true") {
    //     if (lang === "한글") {
    //         $addtionalInfo.textContent = "부가정보 숨기기";
    //     } else {
    //         $addtionalInfo.textContent = "hide addtional info";
    //     }
    // } else {
    //     if (lang === "한글") {
    //         $addtionalInfo.textContent = "부가정보 출력";
    //     } else {
    //         $addtionalInfo.textContent = "print addtional info";
    //     }
    // }
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
            nav.textContent = "Deactivate Navigation";
        } else {
            nav.textContent = "목차 비활성화";    
        }
        
        location.reload();
        return;
    }
    
    localStorage.setItem("nav", "false");
    if (english) {
        nav.textContent = "Activate Navigation";
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
    lang.textContent = "English";
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
            $theme.textContent = "Darkly";
        }
    } else {
        next = "dark";
        if (localStorage.getItem("lang") === "한글") {
            $theme.textContent = "밝게";
        } else {
            $theme.textContent = "Brightly";
        }
    }

    localStorage.setItem("theme", next);
    $root.setAttribute("data-theme", next);
    location.reload();
}

function toggleAdditionalInfo() {
    if (localStorage.getItem("addtional_info") === "false") {
        localStorage.setItem("addtional_info", "true");
    } else {
        localStorage.setItem("addtional_info", "false");
    }
    location.reload();
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
#toggle_additional_info {
    border-bottom: none;
}
</style>