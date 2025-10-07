---
layout: index
---

<a href="./blue%20sky" id="blueSky"></a>

<a href="./hazy%20sky" id="hazySky"></a>

<a href="./pink%20sky" id="pinkSky"></a>

<a href="./chairs" id="chairs"></a>

<a href="./stream" id="stream"></a>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const lang = localStorage.getItem("lang");
    const blueSky = document.getElementById("blueSky");
    const hazySky = document.getElementById("hazySky");
    const pinkSky = document.getElementById("pinkSky");
    const chairs = document.getElementById("chairs");
    const stream = document.getElementById("stream");
    if (lang === undefined || lang === "english") {
        blueSky.textContent = "blueSky";
        hazySky.textContent = "hazySky";
        pinkSky.textContent = "pinkSky";
        chairs.textContent = "chairs";
        stream.textContent = "stream";
        return;
    }

    blueSky.textContent = "파란 하늘";
    hazySky.textContent = "흐릿한 하늘";
    pinkSky.textContent = "분홍 하늘";
    chairs.textContent = "의자";
    stream.textContent = "개울";
}); 
</script>