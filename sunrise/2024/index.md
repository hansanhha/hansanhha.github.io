---
layout: index
idx-name: 2024
idx-name-ko: 2024
---

<a id="january" href="./january">january</a>

<a id="march" href="./march">march</a>

<a id="april" href="./april">april</a>

<a id="may" href="./may">may</a>

<a id="june" href="./june">june</a>

<a id="july" href="./july">july</a>

<a id="november" href="./november">november</a>

<a id="december" href="./december">december</a>

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