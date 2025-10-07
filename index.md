---
layout: index
title: hansanhha
title-ko: 한산하
---

<p><a href="./code/" id="code">code</a></p>

<p><a href="./reflect/" id="reflect">reflect</a></p>

<p><a href="./archives/" id="archives">archives</a></p>

<p><a href="./sunrise/" id="sunrise">sunrise</a></p>

<p><a href="./options/" id="options">options</a></p>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const lang = localStorage.getItem("lang");
    if (lang === undefined || lang === "english") {
        return;
    }

    const code = document.getElementById("code");
    const reflect = document.getElementById("reflect");
    const archives = document.getElementById("archives");
    const sunrise = document.getElementById("sunrise");
    const options = document.getElementById("options");

    code.textContent = "소스코드";
    reflect.textContent = "회고";
    archives.textContent = "아카이브";
    sunrise.textContent = "해맞이";
    options.textContent = "설정";
}); 
</script>