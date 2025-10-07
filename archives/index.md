---
layout: index
title: 
---

<a href="./text/" id="text"></a>
<a href="./images/" id="images"></a>
<a href="./videos/" id="videos"></a>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const lang = localStorage.getItem("lang");
    const text = document.getElementById("text");
    const images = document.getElementById("images");
    const videos = document.getElementById("videos");
    if (lang === undefined || lang === "english") {
        text.textContent = "text";
        images.textContent = "images";
        videos.textContent = "videos";
        return;
    }

    text.textContent = "글 귀";
    images.textContent = "사진";
    videos.textContent = "동영상";
}); 
</script>
