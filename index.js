var txtFile = new XMLHttpRequest();
txtFile.open(
	"GET",
	"https://davidteju.dev/David-O-Meter/sentimentValues.json",
	true
);
txtFile.onreadystatechange = function () {
	let lines;
	let beforeContent;
	if (txtFile.readyState === 4) {
		// Makes sure the document is ready to parse.
		if (txtFile.status === 200) {
			// Makes sure it's found the file.
			let result = JSON.parse(txtFile.responseText);
			const resultArray = [result.positive, result.neutral, result.negative];

			let sum = 0;
			for (let i = 0; i < resultArray.length; i++)
				sum += parseInt(resultArray[i]);

			const bars = [
				document.getElementById("positive"),
				document.getElementById("neutral"),
				document.getElementById("negative"),
			];

			for (let i = 0; i < bars.length; i++) {
				const bar = bars[i];
				bar.innerHTML =
					roundToTwoDecimal((parseInt(resultArray[i]) * 100) / sum).toString() +
					"%";

				bar.attributes.title = resultArray[i];

				bar.style.flexGrow = roundToTwoDecimal(
					(parseInt(resultArray[i]) * 100) / sum
				).toString();

				document.createElement("style");

				beforeContent = window.getComputedStyle(bar, "::after").content;

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
			populateFootnote(sum, result.time);
		}
	}
};

function populateFootnote(sum, epochTime) {
	document.getElementById("number-of-tweets").innerHTML = document
		.getElementById("number-of-tweets")
		.innerHTML.replace(
			"$num",
			"<strong>" + sum.toLocaleString("en-US") + "</strong>"
		);

	var d = new Date(epochTime);

	document.getElementById("time-updated").innerHTML =
		document.getElementById("time-updated").innerHTML +
		"<strong>" +
		d.toLocaleTimeString("en-us", { hour: "2-digit", minute: "2-digit" }) +
		" " +
		d.toLocaleDateString("en-us");
	("</strong>");
}

txtFile.send(null);

function hideOrShowNeutralBar() {
	let isChecked = document.getElementById("show-neutral").checked;
	let neutralBarDisplay = document.getElementById("neutral");

	let pos = document.getElementById("positive");
	let posPercent = parseFloat(pos.style.flexGrow);
	let neg = document.getElementById("negative");
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

function roundToTwoDecimal(toRound) {
	return Math.round(toRound * 100) / 100;
}
