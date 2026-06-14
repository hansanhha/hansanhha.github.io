---
layout: index
idx-name: 2024
idx-name-ko: 2024
---

<a id="january" href="./January">January</a>

<a id="march" href="./March">March</a>

<a id="april" href="./April">April</a>

<a id="may" href="./May">May</a>

<a id="june" href="./June">June</a>

<a id="july" href="./July">July</a>

<a id="november" href="./November">November</a>

<a id="december" href="./December">December</a>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const lang = localStorage.getItem("lang");

    if (lang === "한글") {
        const january = document.getElementById("january");
        const march = document.getElementById("march");
        const april = document.getElementById("april");
        const may = document.getElementById("may");
        const june = document.getElementById("june");
        const july = document.getElementById("july");
        const november = document.getElementById("november");
        const december = document.getElementById("december");

        january.textContent = "1월";
        march.textContent = "3월";
        april.textContent = "4월";
        may.textContent = "5월";
        june.textContent = "6월";
        july.textContent = "7월";
        november.textContent = "11월";
        december.textContent = "12월";
    }
})
</script>