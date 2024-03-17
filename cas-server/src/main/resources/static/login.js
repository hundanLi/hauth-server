document.addEventListener("DOMContentLoaded", function() {
    const languageSelect = document.getElementById("language-select");
    const languageDataElements = document.querySelectorAll("[data-en], [data-zh]");

    function switchLanguage(language) {
        languageDataElements.forEach((element) => {
            if (element.hasAttribute("data-" + language)) {
                element.textContent = element.getAttribute("data-" + language);
            }
        });
    }

    languageSelect.addEventListener("change", function() {
        switchLanguage(this.value);
    });
});
