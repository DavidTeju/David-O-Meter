const txtFile = new XMLHttpRequest();
txtFile.open(
    "GET",
    "/projects/David-O-Meter/sentimentValues.json",
    true
);

txtFile.onreadystatechange = function () {
    if (txtFile.readyState === 4)
        // Check that request is complete
        if (txtFile.status === 200) {
            // Makes sure it found the file.
            const result: { negative: number, neutral: number, positive: number, time: number } = JSON.parse(txtFile.responseText);
            const resultArray = [result.positive, result.neutral, result.negative];

            let sum = 0;
            for (let i = 0; i < resultArray.length; i++)
                sum += resultArray[i];

            const bars = [
                document.getElementById("positive")!,
                document.getElementById("neutral")!,
                document.getElementById("negative")!,
            ];

            for (let i = 0; i < bars.length; i++)
                formatBar(bars[i], resultArray[i], sum);

            populateFootnote(sum, result.time);
        }
};

function formatBar(bar: HTMLElement, barValue: number, sum: number) {
    bar.innerHTML =
        roundToTwoDecimal((barValue * 100) / sum).toString() +
        "%";

    bar.setAttribute("title", String(barValue));

    bar.style.flexGrow = roundToTwoDecimal(
        (barValue * 100) / sum
    ).toString();

    let beforeContent = window.getComputedStyle(bar, "::after").content;

    const marginLeft =
        "#" +
        bar.id +
        ":after{margin-left: -" +
        beforeContent.length / 4 +
        "em}";

    const styleTag = document.createElement("style");
    styleTag.innerHTML = marginLeft;
    document.head.insertAdjacentElement("beforeend", styleTag);
}

function populateFootnote(sum: number, epochTime: number) {
    const numTweetsEl = document.getElementById("number-of-tweets")!;
    numTweetsEl.innerHTML = numTweetsEl.innerHTML.replace(
        "$num",
        "<strong>" + sum.toLocaleString() + "</strong>"
    );

    const d = new Date(epochTime);

    const timeEl = document.getElementById("time-updated")!;
    timeEl.innerHTML =
        `${timeEl.innerHTML}<strong>${d.toLocaleTimeString("en-us", {
            hour: "2-digit",
            minute: "2-digit"
        })} ${d.toLocaleDateString("en-us")}</strong>`;
}

function hideOrShowNeutralBar() {
    let isChecked = (document.getElementById("show-neutral") as HTMLInputElement).checked;
    let neutralBarDisplay = document.getElementById("neutral")!;

    let pos = document.getElementById("positive")!;
    let posPercent = parseFloat(pos.style.flexGrow);
    let neg = document.getElementById("negative")!;
    let negPercent = parseFloat(neg.style.flexGrow);

    let sum = posPercent + negPercent;

    if (!isChecked) {
        neutralBarDisplay.style.display = "none";
        pos.innerHTML = roundToTwoDecimal((posPercent / sum) * 100) + "%";
        neg.innerHTML = roundToTwoDecimal((negPercent / sum) * 100) + "%";
    } else {
        neutralBarDisplay.style.display = "initial";
        pos.innerHTML = posPercent + "%";
        neg.innerHTML = negPercent + "%";
    }
}

function roundToTwoDecimal(toRound: number) {
    return Math.round(toRound * 100) / 100;
}

txtFile.send(null);