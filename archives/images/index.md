---
layout: index
---

<a href="./moving" id="moving">moving</a>

<a href="./people" id="people">people</a>

<a href="./tools" id="tools">tools</a>

<a href="./the%20weeknd" id="theWeeknd">the weeknd</a>

<script>
document.addEventListener("DOMContentLoaded", function() {
    if (localStorage.getItem("lang") === "한글") {
        const moving = document.getElementById("moving");
        const people = document.getElementById("people");
        const tools = document.getElementById("tools");
        const theWeeknd = document.getElementById("theWeeknd");
        moving.textContent = "동작";
        people.textContent = "사람들";
        tools.textContent = "도구";
        theWeeknd.textContent = "위켄드";
    }
}); 
</script>