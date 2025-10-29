---
layout: index
idx-name: 2025
idx-name-ko: 2025
---

<a id="january" href="./january">january</a>

<a id="february" href="./february">february</a>

<a id="march" href="./march">march</a>

<a id="april" href="./april">april</a>

<a id="may" href="./may">may</a>

<a id="october" href="./october">october</a>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const lang = localStorage.getItem("lang");

    if (lang === "한글") {
        const january = document.getElementById("january");
        const february = document.getElementById("february");
        const march = document.getElementById("march");
        const april = document.getElementById("april");
        const may = document.getElementById("may");
        const october = document.getElementById("october");

        january.textContent = "1월";
        february.textContent = "2월";
        march.textContent = "3월";
        april.textContent = "4월";
        may.textContent = "5월";
        october.textContent = "10월";
    }
})
</script>