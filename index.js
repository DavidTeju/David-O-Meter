var txtFile = new XMLHttpRequest();
txtFile.open(
	"GET",
	"https://davidteju.dev/David-O-Meter/sentimentValues.txt",
	true
);
txtFile.onreadystatechange = function () {
	let lines;
	//TODO add checkbox to allow user choose whether to include neutral or not
	//TODO modify to show time last updated
	if (txtFile.readyState === 4) {
		// Makes sure the document is ready to parse.
		if (txtFile.status === 200) {
			// Makes sure it's found the file.
			lines = txtFile.responseText.split("\n"); // Will separate each line into an array
			let sum = 0;
			for (let i = 0; i < lines.length; i++) sum += parseInt(lines[i]);

			const pos = document.getElementById("positive");
			pos.innerHTML =
				parseInt((parseInt(lines[0]) * 100) / sum).toString() + "%";
			pos.style.flexGrow = parseInt(
				(parseInt(lines[0]) * 100) / sum
			).toString();
			console.log(lines[0]);

			const neu = document.getElementById("neutral");
			neu.innerHTML =
				parseInt((parseInt(lines[1]) * 100) / sum).toString() + "%";
			neu.style.flexGrow = parseInt(
				(parseInt(lines[1]) * 100) / sum
			).toString();
			console.log(lines[1]);

			const neg = document.getElementById("negative");
			neg.innerHTML =
				parseInt((parseInt(lines[2]) * 100) / sum).toString() + "%";
			neg.style.flexGrow = parseInt(
				(parseInt(lines[2]) * 100) / sum
			).toString();

			console.log(lines[2]);
		}
	}
};

txtFile.send(null);
