---
layout: index
title:
---

<a id="ninja" href="https://github.com/hansanhha/computer-ninja">computer-ninja</a>

<a id="projects" href="./projects">projects</a>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const lang = localStorage.getItem("lang");
    if (lang === "한글") {
        const ninja = document.getElementById("ninja");
        const projects = document.getElementById("projects");
        ninja.textContent="컴퓨터 닌자";
        projects.textContent="프로젝트";
    }
});
</script>