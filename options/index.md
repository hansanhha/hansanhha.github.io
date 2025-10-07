---
layout: index
---

<a id="toggle_nav" onclick="toggleNav(); return false;"></a>

<a id="toggle_lang" onclick="toggleLang(); return false;">english</a>

<script>
window.addEventListener("load", function() {
    const $nav = document.getElementById("toggle_nav");
    const $lang = document.getElementById("toggle_lang");
    const nav = localStorage.getItem("nav");
    const lang = localStorage.getItem("lang");
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
        
        return;
    }
    
    localStorage.setItem("nav", "false");
    if (english) {
        nav.textContent = "activate navigation";
    } else {
        nav.textContent = "목차 활성화";
    }
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
</script>

<style>
#toggle_nav{
    border-bottom: none;
}
#toggle_lang{
    border-bottom: none;
}
</style>